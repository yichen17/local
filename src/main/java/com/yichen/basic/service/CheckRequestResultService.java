package com.yichen.basic.service;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yichen.basic.constant.HttpRequestEnum;
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

    public ResultData checkTwoResult(CheckResultDTO paramA, CheckResultDTO paramB, String checkFields){

        // 前置请求字段校验
        if (checkFieldIsEmpty(paramA) || checkFieldIsEmpty(paramB)){
            return ResultDataUtil.paramError("基本请求入参为空");
        }
        // 请求数据
        String resultA = getQueryData(paramA);
        String resultB = getQueryData(paramB);
        log.info("A 请求结果 {}\n B请求结果 {}",JSON.toJSONString(resultA),JSON.toJSONString(resultB));
        // 排序结果校验字段，先根据分号切分在根据字段长度排序
        String[] fields = checkFields.split(";");
        if (fields.length == 0){
            return ResultDataUtil.paramEmpty("结果校验字段不能为空");
        }
        Arrays.sort(fields,(a,b)->{
            return a.split("\\.").length - b.split("\\.").length;
        });
        // 将数据转换成 JSONObject
        String[] sortField = getSortField(checkFields);
        if (Objects.isNull(sortField) || sortField.length == 0){
            return ResultDataUtil.paramEmpty("结果比对字段为空");
        }
        // 找到目标数据段进行匹配  => 没有该字段或者有该字段值为空 可以看为一样
        JSONObject jsonA = JSON.parseObject(resultA);
        JSONObject jsonB = JSON.parseObject(resultB);
        for(String field : sortField){
            if (!compareData(jsonA,jsonB,field)){
                return ResultDataUtil.successResult("数据不一致,字段 "+field);
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
            }
            a = valueA.get(fields[i]);
            b = valueB.get(fields[i]);
        }
        catch (Exception e){
            log.error("获取数据出错，异常信息 {}",e.getMessage(),e);
            return false;
        }
        return compareObject(a,b);
    }

    public boolean compareObject(Object a, Object b){
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
            if (!compareObject(jsonA.get(entry.getKey()),jsonB.get(entry.getKey()))){
                return false;
            }
        }
        return true;
    }

    public boolean checkFieldIsEmpty(CheckResultDTO param){
        return Objects.isNull(param) || Objects.isNull(param.getUrl()) || Objects.isNull(param.getType()) ;
    }


    public static String getQueryData(CheckResultDTO param){

        log.info("请求入参 {}",JSON.toJSONString(param));

        if (HttpRequestEnum.POST_FORM.getType().equals(param.getType().toUpperCase())){
            return HttpRequest.post(param.getUrl()).addHeaders(param.getHeader())
                    .form(param.getBody()).timeout(5000).execute().body();
        }
        if (HttpRequestEnum.POST_JSON.getType().equals(param.getType().toUpperCase())){
            return HttpRequest.post(param.getUrl()).addHeaders(param.getHeader())
                    .body(JSON.toJSONString(param.getBody())).timeout(5000).execute().body();
        }
        if (HttpRequestEnum.GET.getType().equals(param.getType().toUpperCase())){
            return HttpRequest.get(param.getUrl()).addHeaders(param.getHeader())
                    .body(JSON.toJSONString(param.getBody())).timeout(5000).execute().body();
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
