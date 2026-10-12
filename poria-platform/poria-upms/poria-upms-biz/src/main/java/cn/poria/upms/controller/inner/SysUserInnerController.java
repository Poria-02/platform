package cn.poria.upms.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.dto.AdminSendCodeDTO;
import cn.poria.upms.api.dto.UserPwdUpdateDTO;
import cn.poria.upms.api.entity.SysUser;
import cn.poria.upms.service.SysUserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "用户内部接口")
public class SysUserInnerController {

    private final SysUserService userService;

    @Inner
    @GetMapping({"/info/{username}", "/info/{username}/{userType}"})
    public R info(@PathVariable String username, @PathVariable(required = false) String userType) {
        SysUser user = this.userService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username));
        return user == null ? R.failed( null, String.format("用户信息为空 %s", username)) : R.ok(this.userService.findUserInfo(user, userType));
    }

    @Inner
    @GetMapping({"/mobile/{mobile}"})
    public R mobile(@PathVariable String mobile) {
        SysUser user = this.userService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPhone, mobile));
        return user == null ? R.failed(null, String.format("用户信息为空 %s", mobile)) : R.ok(user);
    }

    @Inner(false)
    @SysLog("修改个人信息")
    @PutMapping({"/pwd/update"})
    public R updateUserPwd(@RequestBody @Valid UserPwdUpdateDTO userDto) {
        return this.userService.updateUserPwd(userDto);
    }

    @Inner
    @GetMapping({"/yxtoken/{userId}/{token}"})
    public R bindYXToken(@PathVariable Integer userId, @PathVariable String token) {
        return R.ok(this.userService.bindYXToken(userId, token));
    }

    @Inner
    @GetMapping({"/info/mobile/{mobile}/{userType}"})
    public R infoByMobile(@PathVariable String mobile, @PathVariable String userType) {
        SysUser user = this.userService.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getPhone, mobile));
        return user == null ? R.failed(null, String.format("用户信息为空 %s", mobile)) : R.ok(this.userService.findUserInfo(user, userType));
    }

    @Inner(false)
    @Operation(summary = "管理端图片验证码校验", description = "管理端图片验证码校验")
    @PostMapping({"/mobile"})
    public R sendSmsCode(HttpServletRequest request, @RequestBody @Validated AdminSendCodeDTO adminSendCodeDTO) {
        return this.userService.sendSmsCode(adminSendCodeDTO, request);
    }
}
