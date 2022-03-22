package com.yichen.basic.utils;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/21 17:47
 * @describe
 */
public class StringUtils {

    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    public static boolean isNull(String str) {
        if (StringUtils.isBlank(str) || str.equals("null")) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(String str){
        if (str == null || str.length() == 0){
            return true;
        }
        return false;
    }


    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
