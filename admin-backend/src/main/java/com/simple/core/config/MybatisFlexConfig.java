package com.simple.core.config;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * MyBatis-Flex 配置
 */
@Configuration
public class MybatisFlexConfig {

    public MybatisFlexConfig() {
        // 开启审计功能，能在控制台打印 SQL
        AuditManager.setAuditEnable(true);
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);
    }

    @PostConstruct
    public void init() {
        FlexGlobalConfig config = FlexGlobalConfig.getDefaultConfig();
        
        // 注册全局插入监听器，用于自动填充 createTime 和 updateTime
        config.registerInsertListener(new InsertListener() {
            @Override
            public void onInsert(Object entity) {
                // 这里通常建议通过反射设置时间，或者在 BaseEntity 中处理
            }
        }, Object.class);
    }
}
