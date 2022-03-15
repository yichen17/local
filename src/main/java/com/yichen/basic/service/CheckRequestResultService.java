package com.yichen.basic.service;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yichen.basic.constant.HttpRequestEnum;
import com.yichen.basic.dto.CheckDataStore;
import com.yichen.basic.dto.RequestDTO.CheckResultDTO;
import com.yichen.basic.dto.ResultData;
import com.yichen.basic.dto.ResultDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/7 17:27
 * @describe 校验请求结果service
 */
@Service
@Slf4j
public class CheckRequestResultService {


    /**
     * 保存跳过字段，根据线程赋值
     */
    public static ThreadLocal<CheckDataStore> DATA_STORE = new ThreadLocal<>();


    public ResultData checkTwoResult(CheckResultDTO paramA, CheckResultDTO paramB, String checkFields){

        // 前置请求字段校验
        if (checkFieldIsEmpty(paramA) || checkFieldIsEmpty(paramB)){
            return ResultDataUtil.paramError("基本请求入参为空");
        }
        // 请求数据
        String resultA = getQueryData(paramA);
        String resultB = getQueryData(paramB);
        log.info("A 请求结果 {}",JSON.toJSONString(resultA));
        log.info("B 请求结果 {}",JSON.toJSONString(resultB));
        // 排序结果校验字段，先根据分号切分在根据字段长度排序
        String[] sortField = getSortField(checkFields);
        if (Objects.isNull(sortField) || sortField.length == 0){
            return ResultDataUtil.paramEmpty("结果比对字段为空");
        }
        // 找到目标数据段进行匹配  => 没有该字段或者有该字段值为空 可以看为一样
        JSONObject jsonA;
        JSONObject jsonB;
        try {
            jsonA = JSON.parseObject(resultA);
            jsonB = JSON.parseObject(resultB);
        }
        catch (Exception e){
            log.error("将结果转换成 JSONObject 出错 {}",e.getMessage(),e);
            return ResultDataUtil.successResult("将结果转换成 JSONObject 出错");
        }

        for(String field : sortField){
            // 清除错误路径
            clearDiffPath();
            if (!compareData(jsonA,jsonB,field)){
//                return ResultDataUtil.successResult("数据不一致,字段 " + getDiffFieldPath(DATA_STORE.get().getDiffPath()));
                Map<String,Object> res = new HashMap<>(4);
                String checkResult = "数据不一致,字段 " + getDiffFieldPath(DATA_STORE.get().getDiffPath());
                res.put("checkResult",checkResult);
                res.put("a result",jsonA);
                res.put("b result",jsonB);
                return ResultDataUtil.successResult("数据不一致",res);
            }
        }
        // 这里需要对结果进行比对   方法有一下几种
        // 1、重写对象的toString()方法   =>  需要自己构造逻辑
        // 2、构建工具类对类对象逐字段比对 => 返回结构通常为json格式字符串  => 比对逻辑
        // 3、调用三方工具类   =>  是否有现成的
        return ResultDataUtil.successResult("数据一致，b中有a");
    }

    public boolean compareData(JSONObject jsonA,JSONObject jsonB,String field){
        String[] fields = field.split("\\.");
        JSONObject valueA = jsonA,valueB = jsonB;
        int i;
        Object a,b;
        try{
            // 排除最后那一个，可能是值也可能是对象
            for (i = 0; i < fields.length - 1; i++){
                valueA = valueA.getJSONObject(fields[i]);
                valueB = valueB.getJSONObject(fields[i]);
                // 置入请求路径
                pushDiffPath(fields[i]);
            }
            a = valueA.get(fields[i]);
            b = valueB.get(fields[i]);
            pushDiffPath(fields[i]);
        }
        catch (Exception e){
            log.error("获取数据出错，异常信息 {}",e.getMessage(),e);
            pushDiffPath("checkFields => " + field + "  字段错误，请确认路径");
            return false;
        }
        return compareObject(a,b,fields[i]);
    }

    /**
     * 比较两个对象的字段 有以下几种情况
     * 1、都为 jsonArray  2、都为基本类型 8+1  3、都为jsonObject  4、
     * @param a
     * @param b
     * @param field
     * @return
     */
    public boolean compareObject(Object a, Object b,String field){

        if (a instanceof JSONArray){
            if (b instanceof JSONArray){
                JSONArray arrayA = (JSONArray) a;
                JSONArray arrayB = (JSONArray) b;
                if (arrayA.size() > arrayB.size()){
                    log.info("a长度小于b，长度不一致数据字段 {}",field);
                    return false;
                }
                for (int i=0;i<arrayA.size();i++){
                    if (!compareObject(arrayA.get(i),arrayB.get(i),"位置"+i)){
                        return false;
                    }
                }
                return true;
            }
            log.info("类型不一致数据字段 {}",field);
            return false;
        }

        boolean isJsonA = a instanceof JSONObject;
        boolean isJsonB = b instanceof JSONObject;
        // 都为值
        if (!isJsonA && !isJsonB){
            if (a == null && b == null){
                return true;
            }
            if (a != null){
                return a.equals(b);
            }
            log.info("不一致数据字段 {}",field);
            return false;
        }
        // 都为 json 对象
        if (isJsonA && isJsonB){
            return compareJsonData((JSONObject) a,(JSONObject) b);
        }
        // 一个值 一个json
        return false;
    }

    public boolean compareJsonData(JSONObject jsonA, JSONObject jsonB){
        for(Map.Entry<String,Object> entry: jsonA.entrySet()){
            Set<String> excludeFields = DATA_STORE.get().getExcludeFields();
            if (!Objects.isNull(excludeFields) && excludeFields.contains(entry.getKey())){
                log.info("当前字段 {} 在排除字段中",entry.getKey());
                continue;
            }
            pushDiffPath(entry.getKey());
            if (!compareObject(jsonA.get(entry.getKey()), jsonB.get(entry.getKey()), entry.getKey())){
                log.info("不一致数据字段 {}",entry.getKey());
                return false;
            }
            popDiffPath();
        }
        return true;
    }

    public boolean checkFieldIsEmpty(CheckResultDTO param){
        return Objects.isNull(param) || Objects.isNull(param.getUrl()) || Objects.isNull(param.getType()) ;
    }


    public static void clearDiffPath(){
        if (Objects.isNull(DATA_STORE.get().getDiffPath())){
            DATA_STORE.get().setDiffPath(new Stack<>());
        }
        else {
            DATA_STORE.get().getDiffPath().clear();
        }
    }

    public static void pushDiffPath(String path){
        if (Objects.isNull(DATA_STORE.get().getDiffPath())){
            DATA_STORE.get().setDiffPath(new Stack<>());
        }
        DATA_STORE.get().getDiffPath().push(path);
    }

    public static String popDiffPath(){
        if (Objects.isNull(DATA_STORE.get().getDiffPath()) || DATA_STORE.get().getDiffPath().size() <= 0){
            log.error("逻辑错误，栈为空或长度小于1");
            return null;
        }
        return DATA_STORE.get().getDiffPath().pop();
    }





    public static String getQueryData(CheckResultDTO param){

        log.info("请求入参 {}",JSON.toJSONString(param));

        try {
            if (HttpRequestEnum.POST_FORM.getType().equals(param.getType().toUpperCase())){
                return HttpRequest.post(param.getUrl()).addHeaders(param.getHeader())
                        .form(param.getBody()).timeout(10000).execute().body();
            }
            if (HttpRequestEnum.POST_JSON.getType().equals(param.getType().toUpperCase())){
                return HttpRequest.post(param.getUrl()).addHeaders(param.getHeader())
                        .body(JSON.toJSONString(param.getBody())).timeout(10000).execute().body();
            }
            if (HttpRequestEnum.GET.getType().equals(param.getType().toUpperCase())){
                return HttpRequest.get(param.getUrl()).addHeaders(param.getHeader())
                        .body(JSON.toJSONString(param.getBody())).timeout(10000).execute().body();
            }
        }
        catch (Exception e){
            log.error("出现错误 {}",e.getMessage(),e);
        }

        return null;
    }

    public static String[] getSortField(String checkFields){
        String[] fields = checkFields.split(";");
        if (fields.length == 0){
            return null;
        }
        Arrays.sort(fields,(a,b)->{
            return a.split("\\.").length - b.split("\\.").length;
        });
        return fields;
    }

    public static String getDiffFieldPath(Stack<String> stack){
        StringBuilder builder = new StringBuilder();
        while (stack.size() > 0){
            builder.insert(0,stack.pop());
            builder.insert(0,"=>");
        }
        return builder.toString();
    }



    public static void main(String[] args) {
        Map<String,String> header = new HashMap<>(4);
        header.put("token","e115030051b10100752d89df47274ad091fee0d1a9e8b0e0");
        header.put("loginInfo","{\"@class\":\"com.onecard.user.vo.LoginToken\",\"mobile\":\"18600296208\",\"customerId\":1170259320,\"accountId\":22041166666925628,\"realName\":\"\\u9122\\u7fa4\\u82b3\",\"certId\":\"340827199309120020\",\"sex\":\"0\",\"birthday\":\"19930912\",\"loginType\":null,\"uuid\":\"73fdca82-b775-4067-b912-eeb025c9c191\",\"loginTime\":null,\"source\":\"A8893951A9FFB399E44E678FA4391848\",\"decodeSource\":15,\"custNo\":\"201702090600297823279131962\",\"isBack\":null,\"imei\":null,\"lable\":null,\"isRoute\":1,\"loginProId\":null,\"createTime\":\"2019-11-12 16:58:28\",\"exprieTime\":null,\"isOld\":null,\"pzCustomerId\":1170259320,\"tenantId\":1001}");
        header.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,Object> body = new HashMap<>(16);
        body.put("certId","340827199309120020");
        body.put("selectType","120ac");
        body.put("mobile","18600296208");
        body.put("customerId","1170259320");
        body.put("md5Mobile","05c24b607ffaaa2bfb71cf205504f4a1");
        body.put("accountId","22041166666925628");
        //  直接请求  http://user-adapter-test.sc.9f.cn/userapi/userinfo/getAccountOrCustomerByType
        // 间接请求  网关    http://spring-webflux-api-gateway-test.sc.9f.cn/ddd/user/select/selectbyfiled
        CheckResultDTO dto = CheckResultDTO.builder().url("http://spring-webflux-api-gateway-test.sc.9f.cn/ddd/user/select/selectbyfiled")
                .type("POST_FORM").header(header).body(body).build();
        String result = getQueryData(dto);
        log.info(" ==> 请求数据 {}",result);
        JSONObject jsonObject = JSON.parseObject(result);
        System.out.println(jsonObject.get("message") instanceof JSONObject);
        System.out.println(jsonObject.get("data") instanceof JSONObject);
        JSONObject message = jsonObject.getJSONObject("message");

//        String[] sortField = getSortField("a.b.c;d.e;a.c.b.d");
//        Map<Integer, List<String>> collectField = Arrays.stream(sortField).collect(Collectors.groupingBy(p->p.split("\\.").length));
//        for(Map.Entry item: collectField.entrySet()){
//            System.out.println(item.getKey()+"---"+item.getValue());
//        }
    }




}
