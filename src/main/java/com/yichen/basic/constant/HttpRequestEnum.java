package com.yichen.basic.constant;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/7 17:06
 * @describe  http 传输枚举
 */
public enum HttpRequestEnum {
    /**
     * 请求方式
     */
    POST_FORM("POST_FORM"),
    POST_JSON("POST_JSON"),
    GET("GET"),
    OPTIONS("OPTIONS"),
    PUT("PUT");

    /**
     * 请求方式
     */
    private String type;

    public String getType() {
        return type;
    }

    HttpRequestEnum(String type) {
        this.type = type;
    }


}
