package com.yichen.basic.dto;

import java.io.Serializable;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2021/12/24 17:17
 * @describe
 */
public class ResultData implements Serializable {
    private static final long serialVersionUID = 5744366937906739106L;
    public static final String STATUS_ERROR = "0";
    public static final String STATUS_SUCC = "1";
    public static final String STATUS_NO_DATA = "1000";
    public static final String STATUS_PARAM_EMPTY = "1001";
    public static final String STATUS_PARAM_ERROR = "1002";
    public static final String STATUS_SOURCE_ERROR = "1003";
    public static final String STATUS_NO_AUTH = "1004";
    public static final String STATUS_UNRECOGNIZED = "1004";
    public static final String STATUS_SIGN = "1005";
    public static final String STATUS_REPEAT = "1006";
    public static final String STATUS_BAD_REQUEST = "1007";
    public static final String STATUS_TOKEN_INVALID = "1008";
    public static final String STATUS_TIME_INVALID = "1009";
    public static final String STATUS_STATUS_OFF = "1010";
    public static final String STATUS_NO_LOGIN = "1011";
    private String message = "成功";
    private String status = "1";
    private Object data;

    public ResultData() {
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
