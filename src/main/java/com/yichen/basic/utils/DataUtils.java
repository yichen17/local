package com.yichen.basic.utils;

import com.alibaba.fastjson.JSON;
import com.yichen.basic.dto.RequestEncode;
import com.yichen.basic.dto.TestEncode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
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

    private static final Character COMMA = ',';
    private static final String EQUAL = "=";
    private static final Character LEFT_BRACKET = '{';
    private static final Character RIGHT_BRACKET = '}';

    /**
     * 将加密后的数据解密，同时填充或替换原有请求入参
     * @param data 请求入参
     * @return 解密成功或者不需要解密-true    其他-false
     */
    public static boolean putDecodeDateToParam(RequestEncode data){
        if (StringUtils.isNotNull(data.getEncryptedInfo())){
            if (log.isInfoEnabled()) {
                log.info("满足解密复制要求,入参 {}",JSON.toJSONString(data));
            }
            Map<String,String> decodeData = null;
            // 先测试 json 格式数据解密
            try{
                decodeData = getDesData(data.getEncryptedInfo());
                // 将 map 中的数据再次置入实体
                return fillMapToRequest(decodeData, data);
            }
            catch (Exception e){
                log.info("json 进行接口解密异常 {}", JSON.toJSONString(data),e);
            }
            // form 表单格式数据解密
            try {
                decodeData = convertFormDataToMap(data.getEncryptedInfo());
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
    public static boolean fillMapToRequest(Map<String,String> decodeData, RequestEncode request ){
        // 获取定义的所有字段  private protected  public
        Set<String> names = Arrays.stream(request.getClass().getDeclaredFields()).flatMap(p -> Stream.of(p.getName())).collect(Collectors.toSet());
        for (Map.Entry<String,String> entry : decodeData.entrySet()){
            // 前置校验 如果是多余字段则跳出   => 不然后面会报错 NoSuchFieldException
            if (!names.contains(entry.getKey())){
                continue;
            }
            try {
                Field declaredField = request.getClass().getDeclaredField(entry.getKey());
                declaredField.setAccessible(true);
                declaredField.set(request,entry.getValue());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("小鱼 解密赋值出错 {}",e.getMessage(),e);
                return true;
            }
        }
        return false;
    }



    /**
     * 四要素解密数据
     * @param encryptedInfo 加密数据
     * @return 解密后的 Map 数据
     */
    public static Map<String, String> getDesData(String encryptedInfo) throws Exception {
        String decryptData = decryptData(encryptedInfo);
        if (log.isInfoEnabled()) {
            log.info("【huxiao】解密数据：{}", decryptData);
        }
        return JSON.parseObject(decryptData, Map.class);
    }

    /**
     * 将加密数据解密
     * @param encryptedInfo 原加密数据
     * @return 解密后的数据
     */
    public static String decryptData(String encryptedInfo)  throws Exception{
        return new String(Method.decryptecb_aes(Common.cryptoCipher(encryptedInfo),
                Common.hex2byte("9b6f011102e72b8a420b9246a6a96bee")));
    }

    /**
     * TODO  数组形式待验证
     * 将表单格式数据转为 map形式，单层
     * @param formData 原表单数据
     * @return map转义结果
     */
    public static  Map<String,String> convertFormDataToMap(String formData) throws Exception{
        String decryptData = decryptData(formData);
        Map<String,String> result = new HashMap<>(16);
        List<String> items = commaSplit(decryptData);

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
            result.add(formData.substring(start,formData.length()-1));
        }
        else {
            result.add(formData.substring(start));
        }
        return result;
    }

	public static void main(String[] args) throws Exception {
		DataUtils baseController = new DataUtils();
		TestEncode entity = new TestEncode();
		entity.setEncryptedInfo("AAQ6dZFdYfGczz1W/EQd+jXjqpN4T3Qz35g8dtngeiYwepmpy80O/q0l8xaQQ52y/0jpBtSrtjTGrCwcmfAXwQvbzu2gzVr+Kcvyax5bj7QQOtA7JmMiDf9w3G5AxCiyApSCBKFcsUELowGxxupL6cFkGezYm8BH1LrKGDlZsLKU/5PJccIo+aii3BPmEyLA4w9JjJkiCbm8rfj+lJby/VR0bw7/xK5cnU24BkQYJBV64laQxuXDj22v2I8KbwMyywJSDRJvntaK/HobxrP0qONZy1kO0z6MoHP5gOsEflh0n5zvnQvmGTGwoSQ/Pu4JTgfLCQdLjBdEUKPtrgbDsadoM1NoupiopfyPexAihUCHFvx5l+Hdx80Th776PmN4");

		baseController.putDecodeDateToParam(entity);
		System.out.println("111");
		Map<String, String> desData = baseController.getDesData("AAQ6dZFdYfGczz1W/EQd+jXjqpN4T3Qz35g8dtngeiYwepmpy80O/q0l8xaQQ52y/0jpBtSrtjTGrCwcmfAXwQvbzu2gzVr+Kcvyax5bj7QQOtA7JmMiDf9w3G5AxCiyApSCBKFcsUELowGxxupL6cFkGezYm8BH1LrKGDlZsLKU/5PJccIo+aii3BPmEyLA4w9JjJkiCbm8rfj+lJby/VR0bw7/xK5cnU24BkQYJBV64laQxuXDj22v2I8KbwMyywJSDRJvntaK/HobxrP0qONZy1kO0z6MoHP5gOsEflh0n5zvnQvmGTGwoSQ/Pu4JTgfLCQdLjBdEUKPtrgbDsadoM1NoupiopfyPexAihUCHFvx5l+Hdx80Th776PmN4");
		Map<String, String> desData1 = baseController.getDesData("hyHy/P8pabsyUHt2yX4V9Td1VDC5PNd9MradMEFDQyLOC8f08z6dw9MKV7S97qQllKJPE9qSUuJb3CvkCo5rHCnGjLdXS9oSh6gSTx6L6To2hhShDUxqRVEDzoR6ZRJP6KH2NSq8k/RoMSAvectobwxDyqzdw942OHC2ygVOFKzEAZuC+35mtV/1IQ7bbcMoMOggR0VLEZdtF3X6VbLDvv6ry4zzFp+KuaoQgKNA16o8WrctjImCJwXKIVO29F5Ozy36s39a4Q61S06uD/tuaTxhNsNQpK3kYo8YVE2uTyZ9fJYSmbJfkZgzdMnwfxKbQ+Amf6w5ew7xh0sY6zYRnL1psAxsX6BQFDe/tfFrmMxMvkBjiCU8i3N1neUrcyRvj55u6TzFdxkhk3B1HJGZSA==");
		String encryptedInfo = "AAQ6dZFdYfGczz1W/EQd+jXjqpN4T3Qz35g8dtngeiYwepmpy80O/q0l8xaQQ52y/0jpBtSrtjTGrCwcmfAXwQvbzu2gzVr+Kcvyax5bj7QQOtA7JmMiDf9w3G5AxCiyApSCBKFcsUELowGxxupL6cFkGezYm8BH1LrKGDlZsLKU/5PJccIo+aii3BPmEyLA4w9JjJkiCbm8rfj+lJby/VR0bw7/xK5cnU24BkQYJBV64laQxuXDj22v2I8KbwMyywJSDRJvntaK/HobxrP0qONZy1kO0z6MoHP5gOsEflh0n5zvnQvmGTGwoSQ/Pu4JTgfLCQdLjBdEUKPtrgbDsadoM1NoupiopfyPexAihUCHFvx5l+Hdx80Th776PmN4";
		encryptedInfo = new String(Method.decryptecb_aes(Common.cryptoCipher(encryptedInfo),
				Common.hex2byte("9b6f011102e72b8a420b9246a6a96bee")));
		TestEncode TestEncode = JSON.parseObject(encryptedInfo, TestEncode.class);
		System.out.println(TestEncode);
	}

}
