package com.sky.aspect;

/**
 * @author 周超
 * @version 1.0
 */

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充的处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect
{
    /**
     * 切入点，就是对哪些类的哪些方法进行拦截
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    //在通知当中写入公共字段自动填充的处理逻辑
    //应该使用前置通知，在insert和update方法执行之前，要为公共的字段赋值
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint)
    {
        log.info("开始进行公共字段自动填充");

        //先获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature)joinPoint.getSignature(); //被拦截的方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); //获取被拦截的方法上的注解对象
        OperationType operationType = autoFill.value(); //获得被拦截的方法上的数据库操作类型

        //获取到当前被拦截的方法的参数--->实体对象
        Object[] args = joinPoint.getArgs();
        //如果方法的参数为null的情况，当然这种情况不会发生，但是以防万一
        if (args == null || args.length == 0)
        {
            return;
        }
        //不为null的情况
        Object entity = args[0];
        /*
        插一嘴，这里的实体对象有可能是Employee,Category,Dish或者是Setmeal，所以不能以单纯的某个
        对象来接收args[0],只能用Object，然后运用反射来为公共字段赋值，如果知道是哪一个对象的话，就可以
        用set方法赋值，但是会造成代码冗余度高
         */
        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if (operationType == OperationType.INSERT)
        {
            //是插入操作，为4个公共字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为方法对象属性赋值
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (operationType == OperationType.UPDATE)
        {
            //是更新操作，为2个公共字段赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}

















