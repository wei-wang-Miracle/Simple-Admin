package com.simple.modules.base.entity.sys;

import com.mybatisflex.annotation.Table;
import com.simple.core.annotation.ColumnDefine;
import com.simple.core.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统参数配置实体类
 */
@Getter
@Setter
@Table(value = "base_sys_param", comment = "系统参数配置")
public class SysParamEntity extends BaseEntity<SysParamEntity> {
    @ColumnDefine(comment = "键", notNull = true)
    private String keyName;

    @ColumnDefine(comment = "名称")
    private String name;

    @ColumnDefine(comment = "数据", type = "text")
    private String data;

    @ColumnDefine(comment = "数据类型 0:字符串 1:数组 2:键值对", defaultValue = "0")
    private Integer dataType;

    @ColumnDefine(comment = "备注")
    private String remark;
}
