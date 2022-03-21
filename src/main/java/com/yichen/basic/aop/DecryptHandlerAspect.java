package com.yichen.basic.aop;

import com.alibaba.fastjson.JSON;
import com.yichen.basic.annotation.AndroidDecrypt;
import com.yichen.basic.annotation.H5Decrypt;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/21 16:55
 * @describe 数据解密 切面
 */
@Aspect
@Component
@Slf4j
public class DecryptHandlerAspect {

    @Pointcut("@annotation(com.yichen.basic.annotation.AndroidDecrypt) && @annotation(com.yichen.basic.annotation.H5Decrypt)")
    public void decryptPointCut() {
    }

    @Before("decryptPointCut()")
    public void check(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        log.info(JSON.toJSONString(args));
        AndroidDecrypt androidDecrypt = method.getAnnotation(AndroidDecrypt.class);
        H5Decrypt h5Decrypt = method.getAnnotation(H5Decrypt.class);
        if (androidDecrypt != null){

        }

        if (h5Decrypt != null){

        }
    }

}
