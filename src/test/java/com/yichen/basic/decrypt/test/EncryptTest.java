package com.yichen.basic.decrypt.test;

import com.alibaba.fastjson.JSON;
import com.yichen.basic.decrypt.model.Person;
import com.yichen.basic.decrypt.model.Student;
import com.yichen.basic.utils.Base64;
import com.yichen.basic.utils.Common;
import com.yichen.basic.utils.DataUtils;
import com.yichen.basic.utils.Method;
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
        student.setName("奕晨");
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



}
