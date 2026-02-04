package com.cool.modules.base.service.sys;

import com.cool.core.base.BaseService;
import com.cool.modules.base.entity.sys.SysParamEntity;

/**
 * 系统参数配置 Service
 */
public interface SysParamService extends BaseService<SysParamEntity> {

    /**
     * 根据 keyName 获取参数
     *
     * @param keyName 键名
     * @return 参数数据
     */
    String dataByKey(String keyName);

    /**
     * 根据 keyName 获取 HTML
     *
     * @param keyName 键名
     * @return HTML 内容
     */
    String htmlByKey(String keyName);
}
