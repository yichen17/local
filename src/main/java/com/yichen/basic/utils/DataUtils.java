package com.yichen.basic.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
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
            String[] keyValue = item.split(EQUAL);
            // 表示没有value
            if (keyValue.length == 1){
                result.put(keyValue[0].trim(),"");
            }
            else {
                result.put(keyValue[0].trim(),keyValue[1].trim());
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

	public static void main(String[] args) throws Exception {
//		DataUtils baseController = new DataUtils();
//		TestEncode entity = new TestEncode();
//		entity.setEncryptedInfo("AAQ6dZFdYfGczz1W/EQd+jXjqpN4T3Qz35g8dtngeiYwepmpy80O/q0l8xaQQ52y/0jpBtSrtjTGrCwcmfAXwQvbzu2gzVr+Kcvyax5bj7QQOtA7JmMiDf9w3G5AxCiyApSCBKFcsUELowGxxupL6cFkGezYm8BH1LrKGDlZsLKU/5PJccIo+aii3BPmEyLA4w9JjJkiCbm8rfj+lJby/VR0bw7/xK5cnU24BkQYJBV64laQxuXDj22v2I8KbwMyywJSDRJvntaK/HobxrP0qONZy1kO0z6MoHP5gOsEflh0n5zvnQvmGTGwoSQ/Pu4JTgfLCQdLjBdEUKPtrgbDsadoM1NoupiopfyPexAihUCHFvx5l+Hdx80Th776PmN4");
//
//		baseController.putDecodeDateToParam(entity);
//		System.out.println("111");
//		Map<String, String> desData = baseController.getDesData("AAQ6dZFdYfGczz1W/EQd+jXjqpN4T3Qz35g8dtngeiYwepmpy80O/q0l8xaQQ52y/0jpBtSrtjTGrCwcmfAXwQvbzu2gzVr+Kcvyax5bj7QQOtA7JmMiDf9w3G5AxCiyApSCBKFcsUELowGxxupL6cFkGezYm8BH1LrKGDlZsLKU/5PJccIo+aii3BPmEyLA4w9JjJkiCbm8rfj+lJby/VR0bw7/xK5cnU24BkQYJBV64laQxuXDj22v2I8KbwMyywJSDRJvntaK/HobxrP0qONZy1kO0z6MoHP5gOsEflh0n5zvnQvmGTGwoSQ/Pu4JTgfLCQdLjBdEUKPtrgbDsadoM1NoupiopfyPexAihUCHFvx5l+Hdx80Th776PmN4");
//		Map<String, String> desData1 = baseController.getDesData("hyHy/P8pabsyUHt2yX4V9Td1VDC5PNd9MradMEFDQyLOC8f08z6dw9MKV7S97qQllKJPE9qSUuJb3CvkCo5rHCnGjLdXS9oSh6gSTx6L6To2hhShDUxqRVEDzoR6ZRJP6KH2NSq8k/RoMSAvectobwxDyqzdw942OHC2ygVOFKzEAZuC+35mtV/1IQ7bbcMoMOggR0VLEZdtF3X6VbLDvv6ry4zzFp+KuaoQgKNA16o8WrctjImCJwXKIVO29F5Ozy36s39a4Q61S06uD/tuaTxhNsNQpK3kYo8YVE2uTyZ9fJYSmbJfkZgzdMnwfxKbQ+Amf6w5ew7xh0sY6zYRnL1psAxsX6BQFDe/tfFrmMxMvkBjiCU8i3N1neUrcyRvj55u6TzFdxkhk3B1HJGZSA==");
//		String encryptedInfo = "AAQ6dZFdYfGczz1W/EQd+jXjqpN4T3Qz35g8dtngeiYwepmpy80O/q0l8xaQQ52y/0jpBtSrtjTGrCwcmfAXwQvbzu2gzVr+Kcvyax5bj7QQOtA7JmMiDf9w3G5AxCiyApSCBKFcsUELowGxxupL6cFkGezYm8BH1LrKGDlZsLKU/5PJccIo+aii3BPmEyLA4w9JjJkiCbm8rfj+lJby/VR0bw7/xK5cnU24BkQYJBV64laQxuXDj22v2I8KbwMyywJSDRJvntaK/HobxrP0qONZy1kO0z6MoHP5gOsEflh0n5zvnQvmGTGwoSQ/Pu4JTgfLCQdLjBdEUKPtrgbDsadoM1NoupiopfyPexAihUCHFvx5l+Hdx80Th776PmN4";
//		encryptedInfo = new String(Method.decryptecb_aes(Common.cryptoCipher(encryptedInfo),
//				Common.hex2byte("9b6f011102e72b8a420b9246a6a96bee")));
//		TestEncode TestEncode = JSON.parseObject(encryptedInfo, TestEncode.class);
//		System.out.println(TestEncode);
	}

}
