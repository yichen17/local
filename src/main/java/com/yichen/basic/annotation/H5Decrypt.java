package com.yichen.basic.annotation;

import com.yichen.basic.dto.RequestEncodeAES;
import com.yichen.basic.dto.RequestEncodeRSA;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/21 16:54
 * @describe h5 数据解密
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface H5Decrypt {

    /**
     * 加密接口， 从中获取加密数据
     * @return 加密接口类
     */
    Class<?> encryptInterface() default RequestEncodeRSA.class;

}
