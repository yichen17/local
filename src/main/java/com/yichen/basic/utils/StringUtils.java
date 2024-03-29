package com.yichen.basic.utils;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.stream.Stream;

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

    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
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

    public static boolean containsEmpty(String ...param){
       return Arrays.stream(param).anyMatch(StringUtils::isEmpty);
    }

    /**
     * 标准化json字符串  有时候存在内部是个json的String，前后多了双引号，实例：
     *      {"address":"{\"city\":\"上海市\",\"name\":\"上海\",\"province\":\"浙江省\"}","age":18}
     * @param str
     * @return
     */
    public static String normalizeJsonStr(String str){
        str = str.replace("\"{", "{")
                .replace("}\"", "}")
                .replace("\"[", "[")
                .replace("]\"", "]")
                .replace("\\\"", "\"");
        return str;
    }

}
