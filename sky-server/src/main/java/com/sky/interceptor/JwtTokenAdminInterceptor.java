package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        //判断当前拦截到的是Controller方法还是其他资源（比如可能是请求的静态图片、样式文件等资源对应的处理器）
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法（Spring 中对应着控制器类里具体的方法（也就是处理业务逻辑的那些方法）），直接放行
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            log.info("当前员工id：", empId);
            /*
            在执行到拦截器校验令牌的地方，把当前用户的id存储到线程为我们提供的单独的存储空间里，
            然后当我们的程序执行到Service层里的save()方法时，在单独的存储空间里把当前用户的id
            取出来，因为是同一个线程，所以同一个存储空间
             */
            BaseContext.setCurrentId(empId);

            //3、通过，放行
            return true;
        } catch (Exception ex) //验证token未通过，返回的错误信息——>401状态码，含义为未授权，把状态码返回给前端
        {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;//不放行
        }
    }
}



