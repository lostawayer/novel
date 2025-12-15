package com;

import com.persistence.mybatis.mapper.MybatisConfig;
import com.service.IUserManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Configurable//告诉Spring容器这个类是一个配置类，并且应当被加载和处理。

@ComponentScan({
        "com.rest",
        "com.service",
        "com.listeners",
        "com.runner"
})
@Import({
        MybatisConfig.class,
})
public class SystemConfig {
}
