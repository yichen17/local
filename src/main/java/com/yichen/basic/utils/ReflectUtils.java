package com.yichen.basic.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/5/11 17:20
 * @describe 反射工具类
 */
@Slf4j
public class ReflectUtils {

    /**
     * 获取某个对象的某个属性，如果该属性的类型不是反参类型，则返回 null
     * @param object 对象
     * @param fieldName 某一个属性
     * @param clazz 该属性对应的类型，如果类型不一致，则返回 null
     * @param <T> 属性定义的类型
     * @return 该对象中入参属性名称的属性值
     */
    public static <T> T getFieldFromObject(Object object, String fieldName, Class<?> clazz){
        try {
            Map<String,Field> fields = new HashMap<>(16);
            getAllDeclareField(object.getClass(), fields);
            if (fields.get(fieldName) == null){
                log.error("类 {} 中无属性名 {} 的属性",object.getClass().getName(), fieldName);
                return null;
            }
            Field field = fields.get(fieldName);
            field.setAccessible(true);
            Object fieldValue = field.get(object);
            if (clazz.isAssignableFrom(fieldValue.getClass())){
                return (T)fieldValue;
            }
            else {
                log.warn("类 {} 中 字段 {} 定义类型 {} 转换类型 {}", object.getClass().getName(), fieldName, field.getType().getName(), clazz.getName());
                return null;
            }
        } catch (IllegalAccessException e) {
            log.error("获取类 {} 中的字段 {} 出错，错误信息 {}", object.getClass().getName(), fieldName, e.getMessage(), e);
        }
        return null;
    }

    /**
     * 通过反射给某个对象的某个属性赋值，如果类型不一致，则不进行赋值操作
     * @param object 对象
     * @param fieldName 对象的某个属性
     * @param value 待赋值的属性的值
     */
    public static void setFieldValue(Object object, String fieldName, Object value){
        try {
            Map<String,Field> fields = new HashMap<>(16);
            getAllDeclareField(object.getClass(), fields);
            if (fields.get(fieldName) == null){
                log.error("类 {} 中无属性名 {} 的属性",object.getClass().getName(), fieldName);
                return ;
            }
            Field field = fields.get(fieldName);
            field.setAccessible(true);
            if (value.getClass().isAssignableFrom(field.getType())){
                field.set(object, value);
            }
            else {
                log.warn("类 {} 中 字段 {} 定义类型 {} 赋值类型 {}", object.getClass().getName(), fieldName, field.getType().getName(), value.getClass().getName());
            }
        } catch (IllegalAccessException e) {
            log.error("获取类 {} 中的属性 {} 出错，错误信息 {}", object.getClass().getName(), fieldName, e.getMessage(), e);
        }
    }

    /**
     * 获取定义的所有属性名以及 属性名-属性 关系map (包括父类的，如果子类和父类有同一个属性，则子类优先)
     * @param object 查询对象
     * @param fields 属性名-属性 关系 map
     */
    public static void getAllDeclareField(Class<?> object, Map<String,Field> fields){
        // 获取定义的所有字段  private protected  public  => 无法获取父类中定义的属性
        Field[] declaredFields = object.getDeclaredFields();
        for (Field field : declaredFields){
            fields.put(field.getName(),field);
        }
        //  获取父类属性和名称
        Class<?> superclass = object.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class) ){
            getAllDeclareField(superclass, fields);
        }
    }




}
