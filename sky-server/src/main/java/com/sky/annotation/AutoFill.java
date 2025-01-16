package com.sky.annotation;

/**
 * @author 周超
 * @version 1.0
 */

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行公共字段的自动填充处理
 */
@Target(ElementType.METHOD)//当前这个注解只会加到方法的位置上
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill
{
    //指定一个属性，就是指定我们当前数据库的操作类型，通过枚举的方式指定
    //数据库操作类型:UPDATE, INSERT
    OperationType value();

}
