package com.simple.modules.base.service.sys;

import com.simple.core.base.BaseService;
import com.simple.modules.base.entity.sys.SysMenuEntity;

import java.util.List;
import java.util.Map;

/**
 * 系统菜单 Service
 */
public interface SysMenuService extends BaseService<SysMenuEntity> {

    /**
     * 创建代码
     *
     * @param params 参数
     */
    void create(Map<String, Object> params);

    /**
     * 导出菜单
     *
     * @param ids 菜单ID列表
     * @return 菜单列表
     */
    List<SysMenuEntity> export(List<Long> ids);

    /**
     * 导入菜单
     *
     * @param menus 菜单列表
     * @return 是否成功
     */
    boolean importMenu(List<SysMenuEntity> menus);
}
