package com.simple.modules.base.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        LambdaQueryWrapper<SysParamEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysParamEntity::getKeyName, keyName);
        SysParamEntity entity = this.getOne(wrapper);
        return entity != null ? entity.getData() : null;
    }

    @Override
    public String htmlByKey(String keyName) {
        return dataByKey(keyName);
    }
}
