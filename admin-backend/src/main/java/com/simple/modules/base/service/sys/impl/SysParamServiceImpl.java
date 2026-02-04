package com.simple.modules.base.service.sys.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.simple.core.base.BaseServiceImpl;
import com.simple.modules.base.entity.sys.SysParamEntity;
import com.simple.modules.base.mapper.sys.SysParamMapper;
import com.simple.modules.base.service.sys.SysParamService;
import org.springframework.stereotype.Service;

/**
 * 系统参数配置 Service 实现类
 */
@Service
public class SysParamServiceImpl
        extends BaseServiceImpl<SysParamMapper, SysParamEntity>
        implements SysParamService {

    @Override
    public String dataByKey(String keyName) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SysParamEntity::getKeyName).eq(keyName);
        SysParamEntity entity = this.getOne(wrapper);
        return entity != null ? entity.getData() : null;
    }

    @Override
    public String htmlByKey(String keyName) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SysParamEntity::getKeyName).eq(keyName);
        SysParamEntity entity = this.getOne(wrapper);
        // 这里假设配置的是富文本，直接返回 data 字段
        return entity != null ? entity.getData() : "";
    }
}
