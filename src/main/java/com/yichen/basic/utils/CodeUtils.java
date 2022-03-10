package com.yichen.basic.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/2/25 15:48
 * @describe 编码工具类  中文、unicode编码
 */
public class CodeUtils {

    /**
     * 中文转unicode编码
     * @param chinese 中文
     * @return unicode 码值
     */
    public static String gbEncoding(final String chinese) {
        char[] utfBytes = chinese.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    /**
     * unicode 转中文
     * @param unicode unicode
     * @return 中文
     */
    public static String decodeUnicode(final String unicode) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = unicode.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = unicode.substring(start + 2, unicode.length());
            } else {
                charStr = unicode.substring(start + 2, end);
            }
            // 16进制parse整形字符串。
            char letter = (char) Integer.parseInt(charStr, 16);
            buffer.append(new Character(letter));
            start = end;
        }
        return buffer.toString();
    }

    public static void main(String[] args)throws Exception {
//        System.out.println(gbEncoding("鄢群芳"));
        String s = "%E6%9C%8B%E5%8F%8B";
        System.out.println(URLDecoder.decode(s,"UTF-8"));

        System.out.println(URLEncoder.encode("朋友","UTF-8"));

    }

}
