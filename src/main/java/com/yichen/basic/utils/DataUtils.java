package com.yichen.basic.utils;

import com.alibaba.fastjson.JSON;
import com.yichen.basic.dto.TestEncode;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/21 11:28
 * @describe 数据格式转换工具类
 */
@Slf4j
public class DataUtils {

    /**
     * form 数据拆分 逗号
     */
    private static final Character COMMA = ',';
    /**
     * form 数据拆分 等号
     */
    private static final String EQUAL = "=";
    /**
     * form 数据拆分 左大括号
     */
    private static final Character LEFT_BRACKET = '{';
    /**
     * form 数据拆分  右大括号
     */
    private static final Character RIGHT_BRACKET = '}';
    /**
     * 加解密方式 aes
     */
    public static final String AES = "aes";
    /**
     * 加解密方式 rsa
     */
    public static final String RSA = "rsa";
    /**
     * 基础类型转换   将 String 转为基本类型
     * key = 基本类    value = 转换方法 => parse
     */
    public static final Map<Class<?>, String> BASIC_CONVERT  = new HashMap<>(8);
    /**
     * android 版本加解密key
     */
    public static final String ANDROID_KEY = "9b6f011102e72b8a420b9246a6a96bee";

    static {
        BASIC_CONVERT.put(Double.class,"parseDouble");
        BASIC_CONVERT.put(Short.class,"parseShort");
        BASIC_CONVERT.put(Long.class,"parseLong");
        BASIC_CONVERT.put(Integer.class,"parseInt");
        BASIC_CONVERT.put(Byte.class,"parseByte");
        BASIC_CONVERT.put(Float.class,"parseFloat");
    }


    /**
     * TODO 最好区分开  JSON格式解密 或者 FORM格式解密
     * 将加密后的数据解密，同时填充或替换原有请求入参
     * @param data 请求入参
     * @return 解密成功或者不需要解密-true    其他-false
     */
    public static boolean putDecodeDateToParam(Object data, String encryptData, String type){


        if (StringUtils.isNotNull(encryptData)){
            if (log.isInfoEnabled()) {
                log.info("满足解密复制要求,入参 {}",JSON.toJSONString(data));
            }

            try {
                if (AES.equals(type)){
                    encryptData = decryptDataAes(encryptData);
                }
                else if (RSA.equals(type)){
                    encryptData = decryptDataRas(encryptData);
                }
            }
            catch (Exception e){
                log.error("数据解密出错 type {} error {}",type,e.getMessage(),e);
                return true;
            }

            log.info("解密后的数据为 {}",encryptData);

            Map<String,Object> decodeData = null;
            // 先测试 json 格式数据解密
            try{
                decodeData = getDesData(encryptData);
                // 将 map 中的数据再次置入实体
                return fillMapToRequest(decodeData, data);
            }
            catch (Exception e){
                log.info("json 进行接口解密异常 {}", JSON.toJSONString(data),e);
            }
            // form 表单格式数据解密
            try {
                decodeData = convertFormDataToMap(encryptData);
                // 将 map 中的数据再次置入实体
                return fillMapToRequest(decodeData, data);
            }
            catch (Exception e){
                log.info("form 进行接口解密异常 {}", JSON.toJSONString(data),e);
            }
            log.error("接口解密异常，json和form均失败");
            return true;
        }
        return false;
    }

    /**
     * 将 解密后的数据集替换原有入参(如果有)
     * @param decodeData 解密后的数据集
     * @param request 原有请求入参
     * @return 填充数据成功-true  其他-false
     */
    public static boolean fillMapToRequest(Map<String,Object> decodeData, Object request ){
        Map<String,Field> fields = new HashMap<>();
        Set<String> names = new HashSet<>();
        // 获取定义的所有字段  private protected  public
        getAllDeclareFieldAndNames(request,fields,names);
        for (Map.Entry<String,Object> entry : decodeData.entrySet()){
            // 前置校验 如果是多余字段则跳出   => 不然后面会报错 NoSuchFieldException
            if (!names.contains(entry.getKey())){
                continue;
            }
            // 如果填充字段失败，则直接跳出
            if (fillDataToFieldError(fields.get(entry.getKey()),request,entry.getValue())){
                return true;
            }
        }
        return false;
    }

    /**
     * 将数据填充到指定字段
     * @param field 字段
     * @param target 对象
     * @param value 值
     * @return 填充成功 false，填充失败true
     */
    public static boolean fillDataToFieldError(Field field, Object target, Object value){
        try {
            // 字段类型校验
            if (field.getType().isAssignableFrom(value.getClass())){
                field.setAccessible(true);
                field.set(target,value);
                return false;
            }
            // 如果类型不一致，进行基础转换
            if (StringUtils.isNotNull(BASIC_CONVERT.get(field.getType())) && value.getClass().isAssignableFrom(String.class)){
                log.info("开始尝试基本类型转换");
                java.lang.reflect.Method method = field.getType().getDeclaredMethod(BASIC_CONVERT.get(field.getType()), String.class);
                field.setAccessible(true);
                field.set(target,method.invoke(null, (String)value));
                return false;
            }
            return true;
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException exception){
            log.error("对象对应属性赋值出错，字段名 {}, 字段类型 {}, 入参类型 {}, 错误信息 {}",
                    field.getName(),field.getType().getName(),value.getClass().getName(), exception.getMessage(),exception);
        }
        return true;
    }


    /**
     * 获取定义的所有属性名以及 属性名-属性 关系map (包括父类的，如果子类和父类有同一个属性，则子类优先)
     * @param object 查询对象
     * @param fields 属性名-属性 关系 map
     * @param names 属性名
     */
    public static void getAllDeclareFieldAndNames(Object object, Map<String,Field> fields, Set<String> names ){
        // 获取定义的所有字段  private protected  public  => 无法获取父类中定义的属性
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field field : declaredFields){
            names.add(field.getName());
            fields.put(field.getName(),field);
        }
        //  获取父类属性和名称
        Class<?> superclass = object.getClass().getSuperclass();
        if (superclass != null && !superclass.equals(Object.class) ){
            getAllDeclareFieldAndNames(superclass,fields,names);
        }
    }

    /**
     * 获取定义的所有属性名以及 属性名-属性 关系map (包括父类的，如果子类和父类有同一个属性，则子类优先)
     * @param object 查询对象
     * @param fields 属性名-属性 关系 map
     * @param names 属性名
     */
    public static void getAllDeclareFieldAndNames(Class<?> object, Map<String,Field> fields, Set<String> names ){
        // 获取定义的所有字段  private protected  public  => 无法获取父类中定义的属性
        Field[] declaredFields = object.getDeclaredFields();
        for (Field field : declaredFields){
            // 同名的优先父类中定义的
            if (!names.contains(field.getName())){
                names.add(field.getName());
                fields.put(field.getName(),field);
            }
        }
        //  获取父类属性和名称
        Class<?> superclass = object.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class) ){
            getAllDeclareFieldAndNames(superclass, fields, names);
        }
    }


    /**
     * 获取该类型对象的所有属性(包括父类的)
     * @param object 对象
     * @return 改对象的所有属性
     */
    public static Set<String> getAllDeclareFieldName(Object object){
        // 获取定义的所有字段  private protected  public  => 无法获取父类中定义的属性
        Set<String> names = Arrays.stream(object.getClass().getDeclaredFields()).flatMap(p -> Stream.of(p.getName())).collect(Collectors.toSet());
        //  获取父类属性
        Class<?> superclass = object.getClass().getSuperclass();
        if (superclass != null && !superclass.equals(Object.class) ){
            names.addAll(getAllDeclareFieldName(superclass));
        }
        return names;
    }

    /**
     * 获取该类型对象的所有属性(包括父类的)
     * @param object 对象
     * @return 改对象的所有属性
     */
    public static Set<String> getAllDeclareFieldName(Class<?> object){
        // 获取定义的所有字段  private protected  public  => 无法获取父类中定义的属性
        Set<String> names = Arrays.stream(object.getDeclaredFields()).flatMap(p -> Stream.of(p.getName())).collect(Collectors.toSet());
        //  获取父类属性
        Class<?> superclass = object.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class) ){
            names.addAll(getAllDeclareFieldName(superclass));
        }
        return names;
    }



    /**
     * 四要素解密数据
     * @param encryptedInfo 加密数据
     * @return 解密后的 Map 数据
     */
    public static Map<String,Object> getDesData(String encryptedInfo) throws Exception {
        return JSON.parseObject(encryptedInfo, Map.class);
    }

    /**
     * 将加密数据解密
     * @param encryptedInfo 原加密数据
     * @return 解密后的数据
     */
    public static String decryptDataAes(String encryptedInfo)  throws Exception{
        return new String(Method.decryptecb_aes(Common.cryptoCipher(encryptedInfo),
                Common.hex2byte("9b6f011102e72b8a420b9246a6a96bee")));
    }

    /**
     * rsa 解密数据
     * @param encryptInfo 解密数据
     * @return 解密后的字符串
     */
    public static String decryptDataRas(String encryptInfo) throws Exception{
        byte[] plaintext = RsaUtilsForH5.decryptByPrivateKey(Base64.decodeBase64(encryptInfo), RsaUtilsForH5.privateKey);
        return URLDecoder.decode(new String(plaintext),"utf8");
    }

    /**
     * TODO  数组形式待验证
     * 将表单格式数据转为 map形式，单层
     * @param formData 原表单数据
     * @return map转义结果
     */
    public static  Map<String,Object> convertFormDataToMap(String formData) throws Exception{
        Map<String,Object> result = new HashMap<>(16);
        List<String> items = commaSplit(formData);

        for(String item : items){
            // 前置校验  => 数据为空   => 理论上不存在
            if (StringUtils.isEmpty(item)){
                continue;
            }
            // 不能根据 = 解密   =>  原因：数据中可能就有等号 => 示例：数据通过 Base64加密
            int position = item.indexOf(EQUAL);
            // 表示没有value
            if ((position == -1) || (position == item.length() -1)){
                result.put(item.substring(0, item.length() - 1).trim(),"");
            }
            else {
                // 正常格式  A=b
                result.put(item.substring(0,position).trim(),item.substring(position+1));
            }
        }
        return result;
    }

    /**
     * 表单数据按项切分，逗号
     * @param formData 表单数据
     * @return 节分后的map数据
     */
    public static  List<String> commaSplit(String formData){
        List<String> result = new ArrayList<>(16);
        Stack<Integer> stack = new Stack<>();
        int start=0,i;
        // 栈相对偏移
        int deviation = 0;
        for(i=0; i<formData.length(); i++){
            if (i == 0 && formData.charAt(i) == LEFT_BRACKET){
                deviation++;
            }
            if (formData.charAt(i) == LEFT_BRACKET ){
                stack.push(i);
                continue;
            }
            if (formData.charAt(i) == RIGHT_BRACKET){
                stack.pop();
            }
            if (formData.charAt(i) == COMMA && stack.size() == deviation){
                if (start == 0 && deviation == 1){
                    result.add(formData.substring(start+1,i));
                }
                else {
                    result.add(formData.substring(start,i));
                }
                start = i+1;
            }
        }
        if (deviation == 1){
            // 只有一个字段的场景
            if (start == 0){
                result.add(formData.substring(start+1, formData.length()-1));
            }
            else {
                result.add(formData.substring(start,formData.length()-1));
            }
        }
        else {
            result.add(formData.substring(start));
        }
        return result;
    }

    /**
     * 将数据进行安卓方式的加密
     * @param data 待加密数据
     * @return 加密后的字符串
     */
    public static String androidEncrypt(String data){
        try {
            String result = new String(com.yichen.basic.utils.Base64.encode(Method.encryptecb_aes(data.getBytes(StandardCharsets.UTF_8),
                    Common.hex2byte(DataUtils.ANDROID_KEY))), StandardCharsets.UTF_8);
            log.info("android 加密后数据 {}",result);
            return result;
        }
        catch (Exception e){
            log.error("android 加密数据出错 {}",e.getMessage(),e);
        }
        return null;
    }

    /**
     * 将数据进行h5方式的加密
     * @param data 待加密数据
     * @return 加密后的字符串
     */
    public static String h5Encrypt(String data){
        try {
            byte[] bytes = RsaUtilsForH5.encryptByPublicKey(JSON.toJSONBytes(data), RsaUtilsForH5.publicKey);
            return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
        }
        catch (Exception e){
            log.error("h5 加密数据出错 {}",e.getMessage(),e);
        }
        return null;
    }



	public static void main(String[] args) throws Exception {

        // android数据加密
        // 现成数据
//        String encodeData = "{\"mobile\":\"15034542237\",\"registrationId\":\"13065ffa4ef84de99a5\",\"userData\":\"{\\\"androidId\\\":\\\"51887e004735a068\\\",\\\"appTime\\\":\\\"1650338254405\\\",\\\"appVersion\\\":\\\"4.0.8\\\",\\\"deviceId\\\":\\\"989547d5-9ef1-32c2-a5cf-2a357ab0dda4\\\",\\\"deviceSource\\\":\\\"bangcle\\\",\\\"deviceType\\\":\\\"android\\\",\\\"iccid\\\":\\\"获取失败,获取失败;46002,获取失败\\\",\\\"idfa\\\":\\\"5f99c9106254380\\\",\\\"imei\\\":\\\"N\\/A\\\",\\\"imsi\\\":\\\"N\\/A\\\",\\\"latitude\\\":\\\"35.403373\\\",\\\"longitude\\\":\\\"110.689077\\\",\\\"mac\\\":\\\"A4:50:46:D8:14:83\\\",\\\"mobile\\\":\\\"15034542237\\\",\\\"netType\\\":\\\"wifi\\\",\\\"networkIp\\\":\\\"192.168.0.102\\\",\\\"networkType\\\":\\\"wifi\\\",\\\"oaid\\\":\\\"5f99c9106254380\\\",\\\"routeMac\\\":\\\"A4:50:46:D8:14:83\\\",\\\"startId\\\":\\\"1650338229758\\\",\\\"systemCode\\\":\\\"WK\\\",\\\"systemName\\\":\\\"android\\\",\\\"systemVersion\\\":\\\"10\\\",\\\"udid\\\":\\\"989547d5-9ef1-32c2-a5cf-2a357ab0dda4\\\",\\\"userId\\\":\\\"\\\",\\\"wifiMac\\\":\\\"f8:8c:21:ce:8d:af\\\"}\",\"loginType\":\"2\",\"system_environment\":\"1\",\"dataMap\":{\"isContract\":\"0\"},\"checkPrivacyPolicy\":\"1\",\"hasReadPrivacyPolicy\":\"1\",\"privacyPolicyName\":\"\",\"verifyCode\":\"226730\",\"channel\":\"WEB\",\"inviteCode\":\"\",\"openId\":\"\",\"udid\":\"989547d5-9ef1-32c2-a5cf-2a357ab0dda4\"}";
//        String result = new String(com.yichen.basic.utils.Base64.encode(Method.encryptecb_aes(encodeData.getBytes(StandardCharsets.UTF_8), Common.hex2byte(DataUtils.ANDROID_KEY))), StandardCharsets.UTF_8);
        // 手动构造
        TestEncode testEncode = TestEncode.builder().mobile("13733621659").build();
        String result = new String(com.yichen.basic.utils.Base64.encode(Method.encryptecb_aes(JSON.toJSONBytes(testEncode), Common.hex2byte(DataUtils.ANDROID_KEY))), StandardCharsets.UTF_8);
        System.out.println(result);

        // android数据解密
//        String s = "2814owLq9Cf6yS275qvZjlhwqOtLzg3ssDb+ImPeUhcbhPj28W6pOGZDK5MM4/6e6vI1sE9lNPWM10XIJd36dFNh7Bp3U3+r01zYLsb/wFdsixuEJr0G850pWYqID+5qrA1THdAYTAncCN6b87QS4KI5SR+qwx6JWYRxi+7WBUeTn1X8UkmpH1/hhqoX08VtVVgKMKTVXj4a42t4Ri/l5abs20xwnMFtlARLem/Fu8RjpZQ+3p2oxqK0e3MCnO7UyBUkWsB2XmaVc8jG4aJk7+8mTM95Fl3HpHW4+CMe02BnEzePyxZ9PXDy7qnk6yXEbw8JQgkhESloqcBdpvHhkVphtRWUwGy4ti8Id5SdDr/HG7a/YeeHZchG1I8SAZahHIsZFP+n25/0WhBHR1PN8BCuaxuMjwkQA/5ketOQoSLFAPrB3oieVjygpr7vL4UpzEIwchslix+Rz3QaQhXXsSwuZ24op1vEHRHgFCfABzCDyYxcTcaiGgQFuoOiBONkoM8fflkZetBqtMysL8vb+E3k8kLU6/kTm6Ou2WUUyojjVU+/QIrZobOjeLFY8iBhsEn8fmzpXw02avWDa0/fcNAkwh//RbBGqP+baJN/qn4/oMuarypymP+BIIYtNU84M4Ckmiwzl5yyPkcL/IVGVBR478/+3xpRQ23vqe1usff4yHV0SU4hQI4JPozmK7CTvjI2I/rkR+08M4KS7p7iUOG2uzp143GLIfqMWElmQTN1Zm5nh2u8dyOlt4BKvIfNU7vgT2eDCuMCS3EeWCVaRYaP+EuCg5oooLIbovwURgYiHbUGE2sr2A6c88bA8FAgvCZNxOCmqjEy4nddTeZ+20ZBlqLQQXeSubGIYLKPs+N39yfqK0f0A2h1p9FKBNn94spyE7U7pn9ix6nMFag/kBUcpP4+CCOKVdFb6+TLE4I2Mi7O9NzH5xJE//PaVF3tDvw+bNdq0vS2jKz0gsMTOXiIbf4e8gcqpiIuBFWG2db9ZcF0xc4NBCZ4F1Pr6LpveZ3c3YZQ6M8YHCBUyC9EkhAcIH2xk/fpis8iRaNhU58RLt3ogYaX/1PrDlKe5aJqem2QnGOkWjYr1fAOok9yPWWkamc7HxsuXVGID4NNoJ6Q/GbtLnNcbhZ4nLC2lxSS3dh98nS6InRMUftvaeU1GuoUWHDtr+93Ce1VGMUWcEHxUHKKEvjyozr3CUj4K+55nnAyctL5XGEZFDPGuFRQvGnSw1Wazpf0pc394Eypozhi1+0Dm89qmJeuUezHFOZBkQapbr84KzDChB1kcco1Wb1DmaJXuKURypMlm6IrorRzj2Oy1hslZuOYJrUdAaD8Acbhl94B/din58PWs3PbbeG9zbe5fm0FQGYlWXKc3+dGUVM9duMn12rYYLwtM1eRE8vPGWLatR9VbviHLosGTPp63JD07ZOhlELEwqwBsP7JGQMUAjWVYoGmdC8NHmGGaq9CRx7kJ6rKyHR8JiXJpw==";
//        String encodeData = new String(Common.cryptoCipher(s), StandardCharsets.UTF_8);
//        System.out.println(encodeData);
//        s = new String(Method.decryptecb_aes(Common.cryptoCipher(s), Common.hex2byte(DataUtils.ANDROID_KEY)));
//        System.out.println(s);


        // h5 数据加密
//        TestEncode testEncode = TestEncode.builder().mobile("13733621659").build();
//        byte[] bytes = RsaUtilsForH5.encryptByPublicKey(JSON.toJSONBytes(testEncode), RsaUtilsForH5.publicKey);
//        System.out.println(org.apache.commons.codec.binary.Base64.encodeBase64String(bytes));


	}

}
