package cn.poria.upms.controller.inner;

import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import cn.poria.upms.api.entity.SysRole;
import cn.poria.upms.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/role")
@Tag(name = "角色内部接口")
public class SysRoleInnerController {

    private final SysRoleService sysRoleService;

    @Inner
    @GetMapping({"/code/{code}"})
    public R getById(@PathVariable String code) {
        return R.ok(this.sysRoleService.getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, code)));
    }

    @PostMapping({"/getRoleList"})
    @Operation(summary = "通过角色ID 查询角色列表", description = "通过角色ID 查询角色列表 (sys_role_getList)")
    @Inner(value = false)
    public R getRoleList(@RequestBody List<String> roleIdList) {
        return R.ok(this.sysRoleService.listByIds(roleIdList));
    }
}
