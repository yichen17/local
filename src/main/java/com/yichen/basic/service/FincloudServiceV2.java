package com.yichen.basic.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yichen.basic.dto.BusOrderInfoVo;
import com.yichen.basic.dto.ResultData;
import com.yichen.basic.dto.ResultDataUtil;
import com.yichen.basic.feign.service.FincloudFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/1/25 13:49
 * @describe 金融云 service 第二版
 */
@Service
public class FincloudServiceV2 {

    @Autowired
    private FincloudFeignService feignService;

    /**
     * 构造请求头
     * @param intfCode 接口编号
     * @param code 商户编号
     * @param secretKey 密钥
     * @return 请求头map
     */
    public static Map<String,String> constructRequestHeader(String intfCode,String code,String secretKey){
        Map<String, String> headerMap = new HashMap<>(16);
        headerMap.put("intfCode", intfCode );
        headerMap.put("code", code);
        headerMap.put("channelId", "3034000001");

        String timestamp = String.valueOf(System.currentTimeMillis());
        headerMap.put("time", timestamp);

        String sign = FincloudService.stringInMd5(code + timestamp + secretKey);
        headerMap.put("sign", sign);

        String dateStr = FincloudService.date2Str(new Date(), "yyyyMMdd");
        headerMap.put("requestNo", code + intfCode + dateStr + FincloudService.getSerialNo());
        headerMap.put("version", "1.0.0");
        return headerMap;
    }

    /**
     * 用于请求 工单中心
     * 构造数据体头部，即 请求体 body中分为两部分 一部分 jsonHead   jsonContent
     * @return 构造的结果集
     */
    public static Map<String,Object> constructDataHeader(){
        Map<String,Object> jsonHead=new HashMap<>(4);
        jsonHead.put("secretKey","");
        jsonHead.put("isSecret",2);
        return jsonHead;
    }

    /**
     * 用于请求 工单中心
     * 构造数据体 body 部分
     * @param busCode 金融云 东单号
     * @param saleChannel 渠道号 可能区分 => 万卡 1056 小鱼 1093
     * @param productId 产品 ID
     * @return 构造的结果集
     */
    public static Map<String,String> constructDataBody(String busCode,String saleChannel,String productId){
        Map<String,String> jsonContent=new HashMap<>(8);
        //  1300002047
        jsonContent.put("appId",busCode);
        //  1056 => 万卡    1093 => 小鱼
        jsonContent.put("saleChannel",saleChannel);
//        867
        jsonContent.put("productId",productId);
        jsonContent.put("saleChannelKey","null");
        jsonContent.put("productIdKey","null");
        return jsonContent;
    }

    /**
     * 构造数据体，一个map   => 用于获取还款计划
     * @param certId 身份证号
     * @param appId 金融云工单号
     * @param channelId 渠道编号
     * @return
     */
    public static Map<String,Object> constructBody(String certId,String appId,String channelId){
        Map<String,Object> body=new HashMap<>(4);
        body.put("certId",certId);
        body.put("appId",appId);
        body.put("channelId",channelId);
        return body;
    }

    /**
     * 获取 工单中心
     * @param intfCode 接口编号
     * @param code 商户标号
     * @param secretKey 密钥
     * @param busCode 金融云 工单中心
     * @param saleChannel 渠道号
     * @param productId 产品号
     * @return 请求结果
     */
    public ResultData getOrderInfo(String intfCode,String code,String secretKey,String busCode,String saleChannel,String productId){
        Map<String, String> header = constructRequestHeader(intfCode, code, secretKey);
        Map<String, Object> jsonHeader = constructDataHeader();
        Map<String, String> jsonContent = constructDataBody(busCode, saleChannel, productId);
        Map<String, Object> requestParam = new HashMap<String, Object>(4);
        requestParam.put("jsonHead", jsonHeader);
        requestParam.put("jsonContent", jsonContent);
        String result = feignService.getFincloudData(header, requestParam);
        if(StringUtils.isEmpty(result)){
            return ResultDataUtil.errorResult("获取数据失败");
        }
        // 这里的结果集 resp_result 字段为字符串，需要处理一下
        Map<String,Object> res=new LinkedHashMap<>(4);
        BusOrderInfoVo busOrderInfoVo = JSON.parseObject(result, BusOrderInfoVo.class);
        res.put("code",busOrderInfoVo.getCode());
        res.put("message",busOrderInfoVo.getMessage());
        String result1 = busOrderInfoVo.getResult();
        if(!StringUtils.isEmpty(result1)){
            JSONObject jsonObject = JSON.parseObject(result1);
            Map<String,Object> data=new LinkedHashMap<>(8);
            data.put("resp_msg",jsonObject.get("resp_msg"));
            data.put("resp_aesKey",jsonObject.get("resp_aesKey"));
            data.put("resp_code",jsonObject.get("resp_code"));
            data.put("resp_result",JSON.parseObject(String.valueOf(jsonObject.get("resp_result"))));
            res.put("result",data);
        }
        return ResultDataUtil.successResult("获取数据成功",res);
    }

    /**
     * 获取 金融云 还款计划
     * @param certId 身份证
     * @param appId 金融云 工单号
     * @param channelId 渠道号
     * @param intfCode 接口编号
     * @param code 商户编号
     * @param secretKey 密钥
     * @return 请求结果
     */
    public ResultData getReplayPlan(String certId,String appId,String channelId,String intfCode,String code,String secretKey){
        Map<String, String> header = constructRequestHeader(intfCode, code, secretKey);
        Map<String, Object> body = constructBody(certId, appId, channelId);
        String result = feignService.getFincloudData(header, body);
        if(StringUtils.isEmpty(result)){
            return ResultDataUtil.errorResult("获取数据失败");
        }
        return ResultDataUtil.successResult("获取数据成功",JSON.parseObject(result));
    }






}
