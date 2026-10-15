package cn.poria.upms.controller.api;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.service.PlatUser;
import cn.poria.common.security.util.SecurityUtils;
import cn.poria.upms.api.dto.UserDTO;
import cn.poria.upms.api.entity.SysUser;
import cn.poria.upms.api.vo.response.user.UserVO;
import cn.poria.upms.api.vo.response.user.UserProfileVO;
import cn.poria.upms.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/user"})
@Tag(name = "用户管理模块")
public class SysUserController {

    @Resource
    private final SysUserService userService;

    @GetMapping("/me")
    @Operation(summary = "查看个人中心", description = "仅返回当前登录用户的资料（sys_user_profile）")
    @PreAuthorize("@pms.hasPermission('sys_user_profile')")
    public R<UserProfileVO> currentProfile() {
        PlatUser user = SecurityUtils.getUser();
        if (user == null) {
            throw new AccessDeniedException("请先使用平台账号登录");
        }
        return R.ok(this.userService.getUserProfile(user.getId()));
    }

    @GetMapping({"/{id}"})
    @Operation(summary = "通过ID查询用户信息", description = "通过ID查询用户信息(sys_user_get)")
    @PreAuthorize("@pms.hasPermission('sys_user_get')")
    public R user(@PathVariable Integer id) {
        return R.ok(this.userService.selectUserVoById(id));
    }

    @GetMapping({"/details/{username}"})
    @Operation(summary = "根据用户名查询用户信息", description = "根据用户名查询用户信息(sys_user_query)")
    @PreAuthorize("@pms.hasPermission('sys_user_query')")
    public R user(@PathVariable String username) {
        SysUser condition = new SysUser();
        condition.setUsername(username);
        return R.ok(this.userService.getOne(new QueryWrapper<>(condition)));
    }

    @SysLog("删除用户信息")
    @DeleteMapping({"/{id}"})
    @PreAuthorize("@pms.hasPermission('sys_user_del')")
    @Operation(summary = "删除用户", description = "根据ID删除用户（sys_user_del）")
    public R userDel(@PathVariable Integer id) {
        SysUser sysUser = this.userService.getById(id);
        return R.ok(this.userService.deleteUserById(sysUser));
    }

    @SysLog("添加用户")
    @PostMapping
    @PreAuthorize("@pms.hasPermission('sys_user_add')")
    @Operation(summary = "添加用户", description = "添加用户（sys_user_add）")
    public R user(@RequestBody @Validated UserDTO userDto) {
        return R.ok(this.userService.saveUser(userDto));
    }

    @SysLog("更新用户信息")
    @PutMapping
    @PreAuthorize("@pms.hasPermission('sys_user_edit')")
    @Operation(summary = "更新用户信息", description = "更新用户信息（sys_user_edit）")
    public R updateUser(@RequestBody @Valid UserDTO userDto) {
        return R.ok(this.userService.updateUser(userDto));
    }

    @GetMapping({"/page"})
    @Operation(summary = "分页查询用户", description = "分页查询用户(sys_user_page)")
    @PreAuthorize("@pms.hasPermission('sys_user_page')")
//   @ConverDatas({@ConverData(table = "user_login_info", serviceId = "${PORIA_LOG:http://poria-log:8046}", key = "userId", value = "lastLoginTimeManager", dbKey = "username", dbValue = "max(login_time)", condition = "client='' group by username"),
//           @ConverData(table = "user_login_info", serviceId = "${PORIA_LOG:http://poria-log:8046}", key = "userId", value = "lastLoginTimeOperate", dbKey = "username", dbValue = "max(login_time)", condition = "client='operate' group by username")})
    public R<IPage<UserVO>> getUserPage(Page page, UserDTO userDTO) {
        return R.ok(this.userService.getUsersWithRolePage(page, userDTO));
    }

    @SysLog("修改个人信息")
    @PutMapping({"/edit"})
    @Operation(summary = "修改个人信息", description = "修改个人信息(sys_user_edit)")
    @PreAuthorize("@pms.hasPermission('sys_user_edit')")
    public R updateUserInfo(@RequestBody @Valid UserDTO userDto) {
        return this.userService.updateUserInfo(userDto);
    }

    @GetMapping({"/ancestor/{username}"})
    @Operation(summary = "上级部门用户列表", description = "上级部门用户列表(sys_user_parent)")
    @PreAuthorize("@pms.hasPermission('sys_user_parent')")
    public R listAncestorUsers(@PathVariable String username) {
        return R.ok(this.userService.listAncestorUsers(username));
    }
}
