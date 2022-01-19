package com.yichen.basic.dto;

import java.io.Serializable;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2021/12/24 17:18
 * @describe
 */
public class ResultDataUtil implements Serializable {
    private static final long serialVersionUID = 1L;

    public ResultDataUtil() {
    }


    public static ResultData result(String status, String msg, Object data) {
        ResultData resultData = new ResultData();
        resultData.setStatus(status);
        resultData.setMessage(msg);
        resultData.setData(data);
        return resultData;
    }

    public static ResultData result(String status, String msg) {
        return result(status, msg, (Object)null);
    }

    public static ResultData errorResult(String msg, Object data) {
        return result("0", msg, data);
    }

    public static ResultData errorResult(String msg) {
        return result("0", msg, (Object)null);
    }

    public static ResultData errorResult() {
        return result("0", "系统异常，请稍后再试！", (Object)null);
    }

    public static ResultData successResult(String msg, Object data) {
        return result("1", msg, data);
    }

    public static ResultData successResult(String msg) {
        return result("1", msg, (Object)null);
    }

    public static ResultData successResult() {
        return result("1", "操作成功！", (Object)null);
    }

    public static ResultData noData(String msg) {
        return result("1000", msg);
    }

    public static ResultData paramEmpty(String msg) {
        return result("1001", msg);
    }

    public static ResultData paramError(String msg) {
        return result("1002", msg);
    }

    public static ResultData sourceError(String msg) {
        return result("1003", msg);
    }

    public static ResultData signFail(String msg) {
        return result("1005", msg);
    }

    public static ResultData repeatSubmit(String msg) {
        return result("1006", msg);
    }

    public static ResultData badRequest(String msg) {
        return result("1007", msg);
    }

    public static ResultData tokenInvalid(String msg) {
        return result("1008", msg);
    }

    public static ResultData timeInvalid(String msg) {
        return result("1009", msg);
    }

    public static Boolean isSuccess(ResultData data) {
        if (data == null) {
            return Boolean.FALSE;
        } else {
            return "1".equals(data.getStatus()) ? Boolean.TRUE : Boolean.FALSE;
        }
    }
}

