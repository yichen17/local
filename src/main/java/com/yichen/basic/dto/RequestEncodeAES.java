package com.yichen.basic.dto;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/21 17:38
 * @describe 请求编码 aes加密
 */
public interface RequestEncodeAES {

    /**
     * 获取加密字段
     * @return 加密字段
     */
    String getEncryptedInfo();

}
