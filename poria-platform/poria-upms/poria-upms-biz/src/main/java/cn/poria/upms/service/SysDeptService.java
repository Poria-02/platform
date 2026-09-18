package cn.poria.upms.service;

import cn.poria.upms.api.dto.DeptTree;
import cn.poria.upms.api.entity.SysDept;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface SysDeptService extends IService<SysDept> {
   List<DeptTree> selectTree();

   List<DeptTree> selectTree(Long departId);

   SysDept saveDept(SysDept sysDept);

   Boolean removeDeptById(Long id);

   Boolean updateDeptById(SysDept sysDept);

   SysDept getOrgByDeptId(Long deptId);
}
