package com.simple.modules.base.service.sys.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.simple.core.base.BaseServiceImpl;
import com.simple.core.util.SecurityUtil;
import com.simple.modules.base.entity.sys.SysMenuEntity;
import com.simple.modules.base.mapper.sys.SysMenuMapper;
import com.simple.modules.base.service.sys.SysMenuService;
import com.simple.modules.base.service.sys.SysPermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统菜单 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl
        extends BaseServiceImpl<SysMenuMapper, SysMenuEntity>
        implements SysMenuService {

    private final SysPermsService sysPermsService;

    @Override
    public Object list(JSONObject requestParams, QueryWrapper queryWrapper) {
        List<SysMenuEntity> list = sysPermsService.getMenus(SecurityUtil.getCurrentUsername());
        list.forEach(e -> {
            List<SysMenuEntity> parent = list.stream()
                    .filter(sysMenuEntity -> e.getParentId() != null && e.getParentId()
                            .equals(sysMenuEntity.getId())).collect(Collectors.toList());
            if (!parent.isEmpty()) {
                e.setParentName(parent.get(0).getName());
            }
        });
        return list;
    }


    @Override
    public boolean delete(Long... ids) {
        super.delete(ids);
        for (Long id : ids) {
            this.delChildMenu(id);
        }
        return true;
    }

    /**
     * 删除子菜单
     *
     * @param id 删除的菜单ID
     */
    private void delChildMenu(Long id) {
        List<SysMenuEntity> delMenu = list(
                QueryWrapper.create().eq(SysMenuEntity::getParentId, id));
        if (CollectionUtil.isEmpty(delMenu)) {
            return;
        }
        Long[] ids = delMenu.stream().map(SysMenuEntity::getId).toArray(Long[]::new);
        if (ArrayUtil.isNotEmpty(ids)) {
            delete(ids);
            for (Long delId : ids) {
                this.delChildMenu(delId);
            }
        }
    }
}
