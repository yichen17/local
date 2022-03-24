package com.yichen.basic.aop;

import com.alibaba.fastjson.JSON;
import com.yichen.basic.annotation.AndroidDecrypt;
import com.yichen.basic.annotation.H5Decrypt;
import com.yichen.basic.dto.RequestEncodeAES;
import com.yichen.basic.dto.RequestEncodeRSA;
import com.yichen.basic.utils.DataUtils;
import com.yichen.basic.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/21 16:55
 * @describe 数据解密 切面
 * @apiNote Order()   切面优先级，越小的越早执行，参考 https://blog.csdn.net/Aeve_imp/article/details/93098524
 */
@Aspect
@Component
@Slf4j
@Order(-999)
public class DecryptHandlerAspect {

//    @Pointcut("@annotation(com.yichen.basic.annotation.AndroidDecrypt) ||  @annotation(com.yichen.basic.annotation.H5Decrypt)")
    @Pointcut("execution(* com.yichen.basic.controller.*.*(..))")
    public void decryptPointCut() {
    }

    /**
     * 不可能同时触发  =>  很重要
     */
    @Before("decryptPointCut()")
    public void check(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        log.info(JSON.toJSONString(args));
        AndroidDecrypt androidDecrypt = method.getAnnotation(AndroidDecrypt.class);
        H5Decrypt h5Decrypt = method.getAnnotation(H5Decrypt.class);
        if (androidDecrypt != null && args!= null && args.length != 0){
            String value = String.valueOf(getInterfaceValue(args[0],androidDecrypt.encryptInterface()));
            if (StringUtils.isNotNull(value)){
                boolean isError = DataUtils.putDecodeDateToParam(args[0], value, "aes");
                if (isError){
                    throw new RuntimeException("json 和 form 解密出错");
                }
            }
        }
        if (h5Decrypt != null && args!= null && args.length != 0){
            String value = String.valueOf(getInterfaceValue(args[0],h5Decrypt.encryptInterface()));
            if (StringUtils.isNotNull(value)){
                boolean isError = DataUtils.putDecodeDateToParam(args[0], value, "rsa");
                if (isError){
                    throw new RuntimeException("json 和 form 解密出错");
                }
            }
        }
    }

    /**
     * 获取加密入参
     * @param param 请求参数
     * @param baseInterface  加密字段
     * @return 加密字段数据
     */
    private Object getInterfaceValue(Object param, Class<?> baseInterface){
        Method method;
        try {

            if (param instanceof RequestEncodeAES && baseInterface.isAssignableFrom(RequestEncodeAES.class)){
                method = RequestEncodeAES.class.getDeclaredMethods()[0];
                return method.invoke(param);
            }
            if (param instanceof RequestEncodeRSA && baseInterface.isAssignableFrom(RequestEncodeRSA.class)){
                method = RequestEncodeRSA.class.getDeclaredMethods()[0];
                return method.invoke(param);
            }
        }
        catch (Exception e){
            log.error("反射获取加密字段出错 {}",e.getMessage(),e);
            throw new RuntimeException("反射获取加密字段出错");
        }
        return null;
    }




}
