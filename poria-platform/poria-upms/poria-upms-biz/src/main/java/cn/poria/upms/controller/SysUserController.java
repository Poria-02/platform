package cn.poria.upms.controller;

import cn.poria.common.core.util.R;
import cn.poria.common.data.conver.annotation.ConverData;
import cn.poria.common.data.conver.annotation.ConverDatas;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.dto.AdminSendCodeDTO;
import cn.poria.upms.api.dto.UserDTO;
import cn.poria.upms.api.dto.UserPwdUpdateDTO;
import cn.poria.upms.api.entity.SysUser;
import cn.poria.upms.api.vo.UserVO;
import cn.poria.upms.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.lang.invoke.SerializedLambda;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/user"})
@Tag(name = "用户管理模块")
public class SysUserController {
   private final SysUserService userService;

   @Inner
   @GetMapping({"/info/{username}", "/info/{username}/{userType}"})
   public R info(@PathVariable String username, @PathVariable(required = false) String userType) {
      SysUser user = (SysUser)this.userService.getOne((Wrapper)Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
      return user == null ? R.failed((Object)null, String.format("用户信息为空 %s", username)) : R.ok(this.userService.findUserInfo(user, userType));
   }

   @Inner
   @GetMapping({"/mobile/{mobile}"})
   public R mobile(@PathVariable String mobile) {
      SysUser user = (SysUser)this.userService.getOne((Wrapper)Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPhone, mobile));
      return user == null ? R.failed((Object)null, String.format("用户信息为空 %s", mobile)) : R.ok(user);
   }

   @GetMapping({"/{id}"})
   @Operation(summary = "通过ID查询用户信息", description = "通过ID查询用户信息(sys_user_get)")
   // @PreAuthorize("@pms.hasPermission('sys_user_get')")
   public R user(@PathVariable Integer id) {
      return R.ok(this.userService.selectUserVoById(id));
   }

   @GetMapping({"/details/{username}"})
   @Operation(summary = "根据用户名查询用户信息", description = "根据用户名查询用户信息(sys_user_query)")
   // @PreAuthorize("@pms.hasPermission('sys_user_query')")
   public R user(@PathVariable String username) {
      SysUser condition = new SysUser();
      condition.setUsername(username);
      return R.ok((SysUser)this.userService.getOne(new QueryWrapper(condition)));
   }

   @SysLog("删除用户信息")
   @DeleteMapping({"/{id}"})
   // @PreAuthorize("@pms.hasPermission('sys_user_del')")
   @Operation(summary = "删除用户", description = "根据ID删除用户（sys_user_del）")
   public R userDel(@PathVariable Integer id) {
      SysUser sysUser = (SysUser)this.userService.getById(id);
      return R.ok(this.userService.deleteUserById(sysUser));
   }

   @SysLog("添加用户")
   @PostMapping
   // @PreAuthorize("@pms.hasPermission('sys_user_add')")
   @Operation(summary = "添加用户", description = "添加用户（sys_user_add）")
   public R user(@RequestBody @Validated UserDTO userDto) {
      return R.ok(this.userService.saveUser(userDto));
   }

   @SysLog("更新用户信息")
   @PutMapping
   // @PreAuthorize("@pms.hasPermission('sys_user_edit')")
   @Operation(summary = "更新用户信息", description = "更新用户信息（sys_user_edit）")
   public R updateUser(@RequestBody @Valid UserDTO userDto) {
      return R.ok(this.userService.updateUser(userDto));
   }

   @GetMapping({"/page"})
   @Operation(summary = "分页查询用户", description = "分页查询用户(sys_user_page)")
   // @PreAuthorize("@pms.hasPermission('sys_user_page')")
//   @ConverDatas({@ConverData(table = "user_login_info", serviceId = "${PORIA_LOG:http://poria-log:8046}", key = "userId", value = "lastLoginTimeManager", dbKey = "username", dbValue = "max(login_time)", condition = "client='shanxincd' group by username"),
//           @ConverData(table = "user_login_info", serviceId = "${PORIA_LOG:http://poria-log:8046}", key = "userId", value = "lastLoginTimeOperate", dbKey = "username", dbValue = "max(login_time)", condition = "client='operate' group by username")})
   public R<IPage<UserVO>> getUserPage(Page page, UserDTO userDTO) {
      return R.ok(this.userService.getUsersWithRolePage(page, userDTO));
   }

   @SysLog("修改个人信息")
   @PutMapping({"/edit"})
   @Operation(summary = "修改个人信息", description = "修改个人信息(sys_user_edit)")
   // @PreAuthorize("@pms.hasPermission('sys_user_edit')")
   public R updateUserInfo(@RequestBody @Valid UserDTO userDto) {
      return this.userService.updateUserInfo(userDto);
   }

   @Inner(false)
   @SysLog("修改个人信息")
   @PutMapping({"/pwd/update"})
   public R updateUserPwd(@RequestBody @Valid UserPwdUpdateDTO userDto) {
      return this.userService.updateUserPwd(userDto);
   }

   @GetMapping({"/ancestor/{username}"})
   @Operation(summary = "上级部门用户列表", description = "上级部门用户列表(sys_user_parent)")
   // @PreAuthorize("@pms.hasPermission('sys_user_parent')")
   public R listAncestorUsers(@PathVariable String username) {
      return R.ok(this.userService.listAncestorUsers(username));
   }

   @Inner
   @GetMapping({"/yxtoken/{userId}/{token}"})
   public R bindYXToken(@PathVariable Integer userId, @PathVariable String token) {
      return R.ok(this.userService.bindYXToken(userId, token));
   }

   @Inner
   @GetMapping({"/info/mobile/{mobile}/{userType}"})
   public R infoByMobile(@PathVariable String mobile, @PathVariable String userType) {
      SysUser user = (SysUser)this.userService.getOne((Wrapper)Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPhone, mobile));
      return user == null ? R.failed((Object)null, String.format("用户信息为空 %s", mobile)) : R.ok(this.userService.findUserInfo(user, userType));
   }

   @Inner(false)
   @Operation(summary = "管理端图片验证码校验", description = "管理端图片验证码校验")
   @PostMapping({"/mobile"})
   public R sendSmsCode(HttpServletRequest request, @RequestBody @Validated AdminSendCodeDTO adminSendCodeDTO) {
      return this.userService.sendSmsCode(adminSendCodeDTO, request);
   }

   public SysUserController(final SysUserService userService) {
      this.userService = userService;
   }

}
