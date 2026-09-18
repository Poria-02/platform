package cn.poria.upms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.constant.enums.LoginTypeEnum;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.IPUtils;
import cn.poria.common.core.util.R;
import cn.poria.common.data.datascope.DataScope;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.upms.api.dto.AdminSendCodeDTO;
import cn.poria.upms.api.dto.UserDTO;
import cn.poria.upms.api.dto.UserInfo;
import cn.poria.upms.api.dto.UserPwdUpdateDTO;
import cn.poria.upms.api.entity.SysDept;
import cn.poria.upms.api.entity.SysDeptRelation;
import cn.poria.upms.api.entity.SysRole;
import cn.poria.upms.api.entity.SysUser;
import cn.poria.upms.api.entity.SysUserRole;
import cn.poria.upms.api.util.ParamResolver;
import cn.poria.upms.api.vo.MenuVO;
import cn.poria.upms.api.vo.UserVO;
import cn.poria.upms.config.AuthConfigProperties;
import cn.poria.upms.mapper.SysUserMapper;
import cn.poria.upms.service.SysDeptRelationService;
import cn.poria.upms.service.SysDeptService;
import cn.poria.upms.service.SysMenuService;
import cn.poria.upms.service.SysRoleService;
import cn.poria.upms.service.SysUserRoleService;
import cn.poria.upms.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.invoke.SerializedLambda;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
   private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);
   private AuthConfigProperties authConfigProperties;
   private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();
   private final SysMenuService sysMenuService;
   private final SysRoleService sysRoleService;
   private final SysDeptService sysDeptService;
   private final SysUserRoleService sysUserRoleService;
   private final SysDeptRelationService sysDeptRelationService;
   private StringRedisTemplate redisTemplate;
   @Value("#{'${write.mobiles}'.split(',')}")
   private List<String> writeMobiles;

   @Transactional(rollbackFor = {Exception.class})
   public Boolean saveUser(UserDTO userDto) {
      if (StrUtil.isNotBlank(userDto.getPassword())) {
         AES aes = new AES(Mode.CFB, Padding.NoPadding, new SecretKeySpec(this.authConfigProperties.getEncodeKey().getBytes(), "AES"), new IvParameterSpec(this.authConfigProperties.getEncodeKey().getBytes()));
         userDto.setPassword(aes.decryptStr(userDto.getPassword()));
      }

      SysUser one = (SysUser)this.getOne((Wrapper)Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPhone, userDto.getPhone()));
      if (null != one) {
         throw new ServiceException("手机号已注册");
      } else {
         SysUser sysUser = new SysUser();
         BeanUtils.copyProperties(userDto, sysUser);
         sysUser.setDelFlag("0");
         sysUser.setPassword(ENCODER.encode(userDto.getPassword()));
         sysUser.setLastPwdUpdateTime(new Date());
         ((SysUserMapper)this.baseMapper).insert(sysUser);
         List<SysUserRole> userRoleList = (List)userDto.getRole().stream().map((roleId) -> {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(sysUser.getUserId());
            userRole.setRoleId(roleId);
            return userRole;
         }).collect(Collectors.toList());
         return this.sysUserRoleService.saveBatch(userRoleList);
      }
   }

   public Boolean bindYXToken(Integer userId, String yxToken) {
      SysUser sysUser = (SysUser)this.getById(userId);
      if (sysUser == null) {
         return false;
      } else {
         sysUser.setYxToken(yxToken);
         return this.updateById(sysUser);
      }
   }

   public UserInfo findUserInfo(SysUser sysUser, String userType) {
      UserInfo userInfo = new UserInfo();
      userInfo.setSysUser(sysUser);
      List<Long> roleIds = (List)this.sysRoleService.findRolesByUserId(sysUser.getUserId()).stream().map(SysRole::getRoleId).collect(Collectors.toList());
      userInfo.setRoles((Long[])ArrayUtil.toArray(roleIds, Long.class));
      Set<String> permissions = new HashSet();
      roleIds.forEach((roleId) -> {
         List<String> permissionList = (List)this.sysMenuService.findMenuByRoleId(roleId, userType).stream().filter((menuVo) -> StrUtil.isNotEmpty(menuVo.getPermission())).map(MenuVO::getPermission).collect(Collectors.toList());
         permissions.addAll(permissionList);
      });
      userInfo.setPermissions((String[])ArrayUtil.toArray(permissions, String.class));
      return userInfo;
   }

   public IPage<UserVO> getUsersWithRolePage(Page page, UserDTO userDTO) {
      return ((SysUserMapper)this.baseMapper).getUserVosPage(page, userDTO, new DataScope());
   }

   public UserVO selectUserVoById(Integer id) {
      return ((SysUserMapper)this.baseMapper).getUserVoById(id);
   }

   @CacheEvict(value = {"user_details"}, key = "#sysUser.username")
   public Boolean deleteUserById(SysUser sysUser) {
      this.sysUserRoleService.deleteByUserId(sysUser.getUserId());
      this.removeById(sysUser.getUserId());
      return Boolean.TRUE;
   }

   @CacheEvict(value = {"user_details"}, key = "#userDto.username")
   public R<Boolean> updateUserInfo(UserDTO userDto) {
      AES aes = new AES(Mode.CFB, Padding.NoPadding, new SecretKeySpec(this.authConfigProperties.getEncodeKey().getBytes(), "AES"), new IvParameterSpec(this.authConfigProperties.getEncodeKey().getBytes()));
      if (StrUtil.isNotBlank(userDto.getPassword())) {
         userDto.setPassword(aes.decryptStr(userDto.getPassword()));
      }

      if (StrUtil.isNotBlank(userDto.getNewpassword1())) {
         userDto.setNewpassword1(aes.decryptStr(userDto.getNewpassword1()));
      }

      if (StrUtil.isNotEmpty(userDto.getNewpassword1()) && StrUtil.equals(userDto.getPassword(), userDto.getNewpassword1())) {
         return R.failed("新密码与原密码相同，修改失败");
      } else if (StrUtil.isNotBlank(userDto.getNewpassword1()) && !Pattern.matches("^(?![A-Za-z0-9]+$)(?![a-z0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$)[a-zA-Z0-9\\W]{8,20}$", userDto.getNewpassword1())) {
         throw new ServiceException("密码必须包含大小写字母、数字、特殊符号，且至少8位");
      } else {
         UserVO userVO = ((SysUserMapper)this.baseMapper).getUserVoByUsername(userDto.getUsername());
         boolean phoneBoolean = null != userDto.getPhone() && !userDto.getPhone().isEmpty();
         if (phoneBoolean) {
            this.verifyPhone(userVO.getPhone(), userDto.getPhone());
         }

         SysUser sysUser = new SysUser();
         if (StrUtil.isNotBlank(userDto.getPassword()) && StrUtil.isNotBlank(userDto.getNewpassword1())) {
            if (!ENCODER.matches(userDto.getPassword(), userVO.getPassword())) {
               log.info("原密码错误，修改密码失败:{}", userDto.getUsername());
               return R.failed("原密码错误，修改失败");
            }

            sysUser.setPassword(ENCODER.encode(userDto.getNewpassword1()));
            sysUser.setLastPwdUpdateTime(new Date());
            this.weakPwdCheck(userDto.getNewpassword1());
         }

         if (phoneBoolean) {
            sysUser.setPhone(userDto.getPhone());
         }

         sysUser.setUserId(userVO.getUserId());
         sysUser.setAvatar(userDto.getAvatar());
         return R.ok(this.updateById(sysUser));
      }
   }

   private void verifyPhone(String oldPhone, String phone) {
      if (!oldPhone.equals(phone)) {
         SysUser phoneUser = (SysUser)this.getOne((Wrapper)Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPhone, phone));
         if (null != phoneUser) {
            throw new ServiceException("手机号已注册");
         }
      }

   }

   @CacheEvict(value = {"user_details"}, key = "#userDto.username")
   public R<Boolean> updateUserPwd(UserPwdUpdateDTO userDto) {
      AES aes = new AES(Mode.CFB, Padding.NoPadding, new SecretKeySpec(this.authConfigProperties.getEncodeKey().getBytes(), "AES"), new IvParameterSpec(this.authConfigProperties.getEncodeKey().getBytes()));
      userDto.setUsername(aes.decryptStr(userDto.getUsername()));
      userDto.setPassword(aes.decryptStr(userDto.getPassword()));
      userDto.setNewpassword1(aes.decryptStr(userDto.getNewpassword1()));
      UserVO existUser = ((SysUserMapper)this.baseMapper).getUserVoByUsername(userDto.getUsername());
      if (existUser == null) {
         return R.failed("未查询到用户信息");
      } else if (!StrUtil.isBlank(userDto.getPassword()) && !StrUtil.isBlank(userDto.getNewpassword1())) {
         if (!ENCODER.matches(userDto.getPassword(), existUser.getPassword())) {
            return R.failed("原密码错误，修改失败");
         } else if (ENCODER.matches(userDto.getNewpassword1(), existUser.getPassword())) {
            return R.failed("新密码不能与原密码相同");
         } else {
            this.weakPwdCheck(userDto.getNewpassword1());
            return R.ok(this.update((Wrapper)(((new LambdaUpdateWrapper<SysUser>()).set(SysUser::getPassword, ENCODER.encode(userDto.getNewpassword1()))).set(SysUser::getLastPwdUpdateTime, new Date())).eq(SysUser::getUserId, existUser.getUserId())));
         }
      } else {
         return R.failed("参数错误");
      }
   }

   @CacheEvict(value = {"user_details"}, key = "#userDto.username")
   public Boolean updateUser(UserDTO userDto) {
      SysUser one = (SysUser)this.getById(userDto.getUserId());
      if (null != userDto.getPhone() && !userDto.getPhone().isEmpty()) {
         this.verifyPhone(one.getPhone(), userDto.getPhone());
      }

      SysUser sysUser = new SysUser();
      BeanUtils.copyProperties(userDto, sysUser);
      sysUser.setUpdateTime(LocalDateTime.now());
      if (StrUtil.isNotBlank(userDto.getPassword())) {
         AES aes = new AES(Mode.CFB, Padding.NoPadding, new SecretKeySpec(this.authConfigProperties.getEncodeKey().getBytes(), "AES"), new IvParameterSpec(this.authConfigProperties.getEncodeKey().getBytes()));
         userDto.setPassword(aes.decryptStr(userDto.getPassword()));
         if (!Pattern.matches("^(?![A-Za-z0-9]+$)(?![a-z0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$)[a-zA-Z0-9\\W]{8,20}$", userDto.getPassword())) {
            throw new ServiceException("密码必须包含大小写字母、数字、特殊符号，且至少8位");
         }

         this.weakPwdCheck(userDto.getPassword());
         sysUser.setPassword(ENCODER.encode(userDto.getPassword()));
         sysUser.setLastPwdUpdateTime(new Date());
         this.redisTemplate.delete("ADMIN_PWD_LIMIT_KEY:" + sysUser.getUserId());
      }

      this.updateById(sysUser);
      this.sysUserRoleService.remove((Wrapper)Wrappers.<SysUserRole>lambdaUpdate().eq(SysUserRole::getUserId, userDto.getUserId()));
      userDto.getRole().forEach((roleId) -> {
         SysUserRole userRole = new SysUserRole();
         userRole.setUserId(sysUser.getUserId());
         userRole.setRoleId(roleId);
         userRole.insert();
      });
      return Boolean.TRUE;
   }

   public List<SysUser> listAncestorUsers(String username) {
      SysUser sysUser = (SysUser)this.getOne((Wrapper)Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
      SysDept sysDept = (SysDept)this.sysDeptService.getById(sysUser.getDeptId());
      if (sysDept == null) {
         return null;
      } else {
         Long parentId = sysDept.getParentId();
         return this.list((Wrapper)Wrappers.<SysUser>lambdaQuery().eq(SysUser::getDeptId, parentId));
      }
   }

   private List<Long> getChildDepts() {
      Long deptId = SecurityUtils.getUser().getOrgId();
      return this.sysDeptRelationService.list(Wrappers.<SysDeptRelation>lambdaQuery().eq(SysDeptRelation::getAncestor, deptId)).stream().map(SysDeptRelation::getDescendant).collect(Collectors.toList());
   }

   private void weakPwdCheck(String pwd) {
      String weakCipher = ParamResolver.getStr("WEAK_CIPHER", new String[0]);
      if (StrUtil.isNotBlank(weakCipher)) {
         List weakCiphers = CollectionUtil.toList(weakCipher.split(","));
         if (weakCiphers.contains(pwd)) {
            throw new ServiceException("系统验证为弱密码,请重新修改");
         }
      }

   }

   public R sendSmsCode(AdminSendCodeDTO model, HttpServletRequest request) {
      if (this.checkCode(model)) {
         String ip = IPUtils.getIpAddr(request);
         String userAgent = request.getHeader("user-agent");
         log.info("ip:{},agent:{}", ip, userAgent);
         String key = MD5.create().digestHex16(ip + userAgent);
         if (!this.writeMobiles.contains(model.getMobile())) {
            Object blackLimit = this.redisTemplate.opsForValue().get("black_limit:" + key);
            if (blackLimit != null) {
               log.info("同ip发送短信访问限制");
               return R.ok(Boolean.TRUE);
            }
         }

         SysUser user = (SysUser)this.getOne((Wrapper)Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPhone, model.getMobile()));
         if (null == user) {
            log.info("手机号未注册:{}", model.getMobile());
            return R.failed(Boolean.FALSE, "请联系管理员");
         } else {
            String var10000 = LoginTypeEnum.SMS.getType();
            String redisKey = "DEFAULT_CODE_KEY:" + var10000 + "@" + model.getType() + "|" + model.getMobile();
            Object codeObj = this.redisTemplate.opsForValue().get(redisKey);
            if (codeObj != null) {
               log.info("手机号验证码未过期:{}，{}", model.getMobile(), codeObj);
               return R.failed(Boolean.FALSE, "验证码发送过频繁");
            } else {
               String code = RandomUtil.randomNumbers(Integer.parseInt("4"));
               log.debug("手机号生成验证码成功:{},{}, {}", new Object[]{model.getMobile(), code, model.getType()});
               String body = ((HttpRequest)HttpUtil.createPost("http://poria-message:8007/inner/sms/send").body(JSONUtil.createObj().set("key", "VERIFY_CODE").set("phones", model.getMobile()).set("param", MapUtil.builder("code", code).build()).toString()).header("from", "Y")).execute().body();
               log.info("发送短信，手机号： {},验证码： {}, 响应结果：{}", new Object[]{model.getMobile(), code, body});
               JSONObject bodyJson = JSONUtil.parseObj(body);
               if (bodyJson.getInt("code") != 0) {
                  return R.failed("验证码发送失败！");
               } else {
                  this.redisTemplate.opsForValue().set(redisKey, code, 300L, TimeUnit.SECONDS);
                  this.redisTemplate.opsForValue().set("black_limit:" + key, code, 300L, TimeUnit.SECONDS);
                  return R.ok(Boolean.TRUE);
               }
            }
         }
      } else {
         return R.failed("验证码发送失败！");
      }
   }

   private Boolean checkCode(AdminSendCodeDTO model) {
      String key = "DEFAULT_CODE_KEY:" + model.getRandomStr();
      log.info("验证码key:{}", key);
      Object codeObj = this.redisTemplate.opsForValue().get(key);
      if (ObjectUtil.isEmpty(codeObj)) {
         throw new ServiceException("验证码已发送，五分钟之内不能重复发送！");
      } else if (!model.getVerifyCode().equals(codeObj)) {
         throw new ServiceException("验证码不合法");
      } else {
         this.redisTemplate.delete(key);
         return true;
      }
   }

   public SysUserServiceImpl(final AuthConfigProperties authConfigProperties, final SysMenuService sysMenuService, final SysRoleService sysRoleService, final SysDeptService sysDeptService, final SysUserRoleService sysUserRoleService, final SysDeptRelationService sysDeptRelationService, final StringRedisTemplate redisTemplate, final List<String> writeMobiles) {
      this.authConfigProperties = authConfigProperties;
      this.sysMenuService = sysMenuService;
      this.sysRoleService = sysRoleService;
      this.sysDeptService = sysDeptService;
      this.sysUserRoleService = sysUserRoleService;
      this.sysDeptRelationService = sysDeptRelationService;
      this.redisTemplate = redisTemplate;
      this.writeMobiles = writeMobiles;
   }

}
