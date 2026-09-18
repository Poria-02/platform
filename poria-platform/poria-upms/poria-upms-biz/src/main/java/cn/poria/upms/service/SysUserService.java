package cn.poria.upms.service;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.dto.AdminSendCodeDTO;
import cn.poria.upms.api.dto.UserDTO;
import cn.poria.upms.api.dto.UserInfo;
import cn.poria.upms.api.dto.UserPwdUpdateDTO;
import cn.poria.upms.api.entity.SysUser;
import cn.poria.upms.api.vo.UserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface SysUserService extends IService<SysUser> {
   UserInfo findUserInfo(SysUser sysUser, String userType);

   IPage getUsersWithRolePage(Page page, UserDTO userDTO);

   Boolean deleteUserById(SysUser sysUser);

   R<Boolean> updateUserInfo(UserDTO userDto);

   R<Boolean> updateUserPwd(UserPwdUpdateDTO userDto);

   Boolean updateUser(UserDTO userDto);

   UserVO selectUserVoById(Integer id);

   List<SysUser> listAncestorUsers(String username);

   Boolean saveUser(UserDTO userDto);

   Boolean bindYXToken(Integer userId, String yxToken);

   R sendSmsCode(AdminSendCodeDTO adminSendCodeDTO, HttpServletRequest request);
}
