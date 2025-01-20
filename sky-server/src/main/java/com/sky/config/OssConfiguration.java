package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 周超
 * @version 1.0
 * 通过配置类的方式，用于初始化AliOssUtil类的对象，把AliOssUtil对象创建好
 */
@Configuration
@Slf4j
public class OssConfiguration
{
    @Bean //作用：当项目启动的时候，就会自动调用下面的方法，把这个对象(AliOssUtil)创建出来，然后交给spring容器管理
    @ConditionalOnMissingBean //保证整个spring容器里面只有一个AliOssUtil对象，
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties)
    {
        log.info("开始创建阿里云文件上传工具类对象：{}", aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }

}


//https://web-tlias123456789123.oss-cn-hangzhou.aliyuncs.com/af5758c2-85e1-46d1-9b16-827e7817d336.png

//https://web-tlias123456789123.https://oss-cn-hangzhou.aliyuncs.com/a458133b-beed-4ae5-b1b5-14e30bb9d9c7.png