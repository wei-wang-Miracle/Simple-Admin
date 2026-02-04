package com.simple.modules.base.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simple.core.base.BaseServiceImpl;
import com.simple.modules.base.entity.sys.SysDepartmentEntity;
import com.simple.modules.base.mapper.sys.SysDepartmentMapper;
import com.simple.modules.base.service.sys.SysDepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统部门 Service 实现类
 */
@Service
public class SysDepartmentServiceImpl
        extends BaseServiceImpl<SysDepartmentMapper, SysDepartmentEntity>
        implements SysDepartmentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void order(List<SysDepartmentEntity> list) {
        for (int i = 0; i < list.size(); i++) {
            SysDepartmentEntity entity = list.get(i);
            entity.setOrderNum(i);
            this.updateById(entity);
        }
    }

    /**
     * 获取所有下级部门ID
     */
    public List<Long> getChildrenIds(Long parentId) {
        LambdaQueryWrapper<SysDepartmentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDepartmentEntity::getParentId, parentId);
        return this.listObjs(wrapper, obj -> ((SysDepartmentEntity) obj).getId());
    }
}
