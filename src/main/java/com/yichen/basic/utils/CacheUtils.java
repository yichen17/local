package com.yichen.basic.utils;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/8/4 13:53
 * @describe 缓存工具类
 */
public class CacheUtils {

    public final static String CACHE_PREFIX_LOGIN_INFO = "loginInfo";


    public enum Type {
        USER("onecardUser","用户缓存key"),
        ORDER("onecard","账单缓存key")
        ;


        private String type;
        private String desc;

        Type(String t, String d) {
            type = t;
            desc = d;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public static Type getType(String s){
            for (Type t : Type.values()){
                if (t.getType().equals(s)){
                    return t;
                }
            }
            return null;
        }

    }

    /**
     * 构造用户缓存key
     * @param prefix 前缀
     * @param key 缓存key
     * @return 真实的缓存key
     */
    public static String getKeyUser(String prefix, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(Type.USER.getType());
        if (StringUtils.isNotNull(prefix)) {
            sb.append("_").append(prefix);
        }
        if (StringUtils.isNotNull(key)) {
            sb.append("_").append(key);
        }
        return sb.toString();
    }

    /**
     * 构造账单缓存key   工单一般都是直接用key查
     * @param prefix 前缀
     * @param key 缓存key
     * @return 最终的缓存key
     */
    public static String getKeyOrder(String prefix, String key) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(prefix) && prefix.contains(CacheUtils.CACHE_PREFIX_LOGIN_INFO)) {
            sb.append("c35d").append("::");
        }
        sb.append(Type.ORDER.getType());
        if (StringUtils.isNotNull(prefix)) {
            sb.append(":").append(prefix);
        }
        if (StringUtils.isNotNull(key)) {
            sb.append(":").append(key);
        }
        return sb.toString();
    }

}
