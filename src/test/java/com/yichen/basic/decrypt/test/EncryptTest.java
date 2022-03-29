package com.yichen.basic.decrypt.test;

import com.alibaba.fastjson.JSON;
import com.yichen.basic.decrypt.model.Person;
import com.yichen.basic.decrypt.model.Student;
import com.yichen.basic.utils.Base64;
import com.yichen.basic.utils.Common;
import com.yichen.basic.utils.DataUtils;
import com.yichen.basic.utils.Method;
import io.swagger.models.auth.In;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/28 21:25
 * @describe 加解密数据测试
 */
@SpringBootTest
public class EncryptTest {


    /**
     * 包含int属性数据加密
     *   =>   hRKIJqNlE+BavU7sYXqGGop136SBLIj4CQG8DMYsSHooxeMFpM0Rpu+g8p1bzFuoDX7aD2nTtZ/D4FMiuJ/vfA==
     * @throws Exception
     */
    @Test
    public void encryptIntData() throws Exception{
        Person person = new Person();
        person.setAddress("浙江温州");
        person.setAge(18);
        person.setName("奕晨");
        byte[] bytes = Method.encryptecb_aes(JSON.toJSONString(person).getBytes(StandardCharsets.UTF_8), Common.hex2byte("9b6f011102e72b8a420b9246a6a96bee"));
        String encodeString = new String(Base64.encode(bytes));
        System.out.println(encodeString);
    }

    /**
     * String 数据加密
     *  =>  hRKIJqNlE+BavU7sYXqGGop136SBLIj4CQG8DMYsSHoJGGnbymYl/SSW2M2qJOn2dmtcMUXln5GyAG/kpFHL2g==
     * @throws Exception
     */
    @Test
    public void encryptStringData() throws Exception{
        Student student = new Student();
        student.setAddress("浙江温州");
        student.setAge("18");
//        student.setName("奕晨");
        byte[] bytes = Method.encryptecb_aes(JSON.toJSONString(student).getBytes(StandardCharsets.UTF_8), Common.hex2byte("9b6f011102e72b8a420b9246a6a96bee"));
        String encodeString = new String(Base64.encode(bytes));
        System.out.println(encodeString);
    }

    /**
     * 解密数据  数据类型问题
     * @throws Exception
     */
    @Test
    public void decryptData() throws Exception{
//        String encodeString = "hRKIJqNlE+BavU7sYXqGGop136SBLIj4CQG8DMYsSHooxeMFpM0Rpu+g8p1bzFuoDX7aD2nTtZ/D4FMiuJ/vfA==";
        String encodeString = "hRKIJqNlE+BavU7sYXqGGop136SBLIj4CQG8DMYsSHoJGGnbymYl/SSW2M2qJOn2dmtcMUXln5GyAG/kpFHL2g==";
        String s = DataUtils.decryptDataAes(encodeString);
        System.out.println("解密数据内容" + s);
        Map<String, Object> mapData = DataUtils.getDesData(s);
        for (Map.Entry<String,Object> entry : mapData.entrySet()){
            System.out.println("key => " + entry.getKey() + " value => " + entry.getValue());
        }
        // 反转成对象
        Person person = new Person();
        DataUtils.fillMapToRequest(mapData, person);
        System.out.println(person);
    }

    @Test
    public void typeConvert() throws Exception{
        Double d = 11.0;
        String s = "11";
//        Long.parseLong();
//        Short.parseShort();
//        Integer.parseInt();
//        Byte.parseByte();
//        Float.parseFloat();
//        Double.parseDouble();
        java.lang.reflect.Method declaredMethod = d.getClass().getDeclaredMethod("parse" + d.getClass().getSimpleName(), String.class);
        System.out.println("反射赋值 " + declaredMethod.invoke(null,s));
    }

    /**
     * 基础测试
     */
    @Test
    public void basicTest(){
        Double d = 1.0;
        System.out.println(d.getClass().getSimpleName());
        System.out.println(d.getClass().getName());
        System.out.println(d.getClass().getCanonicalName());
        System.out.println(d.getClass().getTypeName());
    }

    /**
     * 测试 String 转基本数据类型
     * @throws Exception
     */
    @Test
    public void basicConvert() throws Exception{
        String s = "1111";
        System.out.println("Byte => " + Byte.parseByte(s));
        System.out.println("Long => " + Long.parseLong(s));
        System.out.println("Short => " + Short.parseShort(s));
        System.out.println("Double => " + Double.parseDouble(s));
        System.out.println("Float => " + Float.parseFloat(s));
        System.out.println("Integer => " + Integer.parseInt(s));
    }

    /**
     * 测试 rsa 解密
     * @throws Exception
     */
    @Test
    public void RsaDecrypt() throws Exception{
//        String s = "KY6QQe/PXkX6CjFeJ1UDUM6/YeyD4Y1FB9VXG1LqOnoiOSuqrCOyQWcW7m77zuh7ra5d2c8TcFG27vmD74t/wfHG/WJxzC/2LA0abrKRFiD+bpOmp89mw7lWSK9zlp7a9fh7zovq/ZN+cRE0aUy1PTP3M/eWTqoTUmJkUEEr4m4l1ad5LSjCGC1siks2yEAiikxuVnAF4JSXK5UuWGOHnBb9WRa5vq5W+vn8PfJxgZ3pv8TWoDjwvXUlmvembhiyreBgMAfrZqOR03o4unXFA5qxnVJUqe9SIe1lVuhqLd0mZRRBrkztGjv3oFlwLF/owYsCNkuV9H74m+Bv8IyjuQ==";
        String s = "BYNBDd9cKaGNddD6k3/z3tkc22CpEQpJ+19BCAWm5oAtsITYCKOyon87bbCFeh5B3cT1mF375IOLPjmjLx2jL/pOE5ajw+QPuGfRE4Hl2rXnF9VagvC4qCiAZc2B6vAxizsr3qfR0LGds+deVfqj6pfUt7s5yljVXWoPNk1Q2XqMhCm/umrKaeiFdita9wK1FaGQQW6FwW3+ksyyGYnwAdaT9zTKzfGuR5gg+mXl24XyW9s0WAr882aHfmBn3zpZuKts/EXphnQTTDXbfK5OzDSD/gO+Xh6k2q4kIkbjMHBtX4xfxJUEepJBZVWMbiv5tiyF14VCdS83oroOyGpGIw==";
        System.out.println(DataUtils.decryptDataRas(s));
    }

    /**
     * 测试 aes 解密
     * @throws Exception
     */
    @Test
    public void AesDecrypt() throws Exception{
        String s = "AAQ6dZFdYfGczz1W/EQd+jXjqpN4T3Qz35g8dtngeiYwepmpy80O/q0l8xaQQ52y/0jpBtSrtjTGrCwcmfAXwQvbzu2gzVr+Kcvyax5bj7QQOtA7JmMiDf9w3G5AxCiyApSCBKFcsUELowGxxupL6cFkGezYm8BH1LrKGDlZsLKU/5PJccIo+aii3BPmEyLA4w9JjJkiCbm8rfj+lJby/VR0bw7/xK5cnU24BkQYJBV64laQxuXDj22v2I8KbwMyywJSDRJvntaK/HobxrP0qONZy1kO0z6MoHP5gOsEflh0n5zvnQvmGTGwoSQ/Pu4JTgfLCQdLjBdEUKPtrgbDsadoM1NoupiopfyPexAihUCHFvx5l+Hdx80Th776PmN4";
        String decryptDataRas = DataUtils.decryptDataAes(s);
        System.out.println(decryptDataRas);
        Map<String, Object> stringObjectMap = DataUtils.convertFormDataToMap(decryptDataRas);
        System.out.println(JSON.toJSONString(stringObjectMap));
    }



}
