package com.yichen.basic.dto;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/22 9:00
 * @describe 请求编码 rsa + aes 加密
 */
public interface RequestEncodeRSA {
    /**
     * 获取加密字段
     * @return 加密字段
     */
    String getEncryptedInfoForH5();
}
