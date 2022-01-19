package com.yichen.basic.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yichen.basic.dto.FinCloudDto;
import com.yichen.basic.dto.ResultData;
import com.yichen.basic.dto.ResultDataUtil;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2021/12/24 15:26
 * @describe 金融云相关查询
 */
@Service
public class FincloudService {

    private static Logger logger= LoggerFactory.getLogger(FincloudService.class);


    /**
     * 根据入参构造自定义请求 金融云入参
     * @param dto 请求金融云 入参
     * @return 请求结果
     */
    public static ResultData queryByParams(FinCloudDto dto){
        String url="";
        // 获取请求地址
        if(Objects.isNull(dto.getUrl())){
            return ResultDataUtil.errorResult("请求地址为空，必须指定请求地址 url");
        }
        url=String.valueOf(dto.getUrl());
        Map<String,Object> requestHeader;
        Map<String,Object> dataBody;
        // 开始构造请求头
        if(Objects.isNull(dto.getRequestHeader())){
            logger.info("请求头为空，不符合");
        }
        requestHeader=dto.getRequestHeader();
        if(Objects.isNull(dto.getDataBody())){
            logger.info("请求体 为空");
        }
        dataBody=dto.getDataBody();
        StringBuilder requestNoBuilder=new StringBuilder();
        StringBuilder signBuilder=new StringBuilder();
        String timestamp=String.valueOf(System.currentTimeMillis());
        String dateStr=date2Str(new Date(),"yyyyMMdd");
        for(String key: requestHeader.keySet()){
            if("code".equals(key)){
                requestNoBuilder.append(requestHeader.get("code"));
                signBuilder.append(requestHeader.get("code")).append(timestamp);
            }
            if("secretKey".equals(key)){
                if(StringUtils.isEmpty(signBuilder.toString())){
                    return ResultDataUtil.errorResult("请求头 requestHeader中没有 code 字段");
                }
                else{
                    signBuilder.append(requestHeader.get("key"));
                }
            }
            if("intfCode".equals(key)){
                if(StringUtils.isEmpty(requestNoBuilder.toString())){
                    return ResultDataUtil.errorResult("请求头 requestHeader中没有 code 字段");
                }
                else{
                    requestNoBuilder.append(requestHeader.get("key")).append(dateStr).append(getSerialNo());
                }
            }
        }
        requestHeader.put("time",timestamp);
        if(StringUtils.isEmpty(signBuilder.toString())){
            logger.info("请求头 sign字段构造为空，不设置签名");
        }
        else{
            requestHeader.put("sign",stringInMd5(signBuilder.toString()));
        }
        if(StringUtils.isEmpty(requestNoBuilder.toString())){
            logger.info("请求头 requestNo字段构造为空，不设置请求流水");
        }
        else{
            requestHeader.put("requestNo",requestNoBuilder.toString());
        }

        if(Objects.isNull(requestHeader.get("version"))){
            logger.info("请求头中没有版本号，设置默认版本号 1.0.0");
            requestHeader.put("version","1.0.0");
        }


        // 执行请求
        String orderInfo = httpPostWithHeaderAndBody(url,requestHeader,JSON.toJSONString(dataBody),"application/json; charset=UTF-8");
        return ResultDataUtil.successResult(orderInfo);
    }

    /**
     * 查询 年化利率
     * @param length  还款计划长度
     * @param productId 产品号
     * @param busCode 金融云 工单号
     * @return 年化利率信息  没有查询到返回默认值
     */
    public static ResultData getBusOrderInfo(int length, String productId, String busCode){

        Map<String, Object> headerMap = new HashMap<>(2);
        //  30060000003
        String intfCode="100191";
        String code="30340";
        String secretKey="vx4nrssUVMJL2tHM";
        headerMap.put("intfCode", intfCode );
        headerMap.put("code", code);
        headerMap.put("channelId", "3034000001");

        String timestamp = String.valueOf(System.currentTimeMillis());
        headerMap.put("time", timestamp);

        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(code).append(timestamp).append(secretKey);

        String sign = stringInMd5(signBuilder.toString());
        headerMap.put("sign", sign);

        String dateStr = date2Str(new Date(), "yyyyMMdd");
        StringBuilder requestNoBuilder=new StringBuilder();
        requestNoBuilder.append(code).append(intfCode).append(dateStr).append(getSerialNo());
        headerMap.put("requestNo", requestNoBuilder.toString());
        headerMap.put("version", "1.0.0");

        Map<String,Object> jsonHead=new HashMap<>(2);
        jsonHead.put("secretKey","");
        jsonHead.put("isSecret",2);
        Map<String,String> jsonContent=new HashMap<>(8);
//        jsonContent.put("appId",busCode);
        jsonContent.put("appId","1300002047");
        //  1056  865
        jsonContent.put("saleChannel","1056");
        jsonContent.put("productId","867");
        jsonContent.put("saleChannelKey","null");
        jsonContent.put("productIdKey","null");
        Map<String, Object> requestParam = new HashMap<String, Object>(2);
        requestParam.put("jsonHead", jsonHead);
        requestParam.put("jsonContent", jsonContent);
        try{
            logger.info("getBusOrderInfo => 请求头 {}，请求体 {}",JSON.toJSONString(headerMap),JSON.toJSONString(requestParam));
            String orderInfo = httpPostWithHeaderAndBody("http://bull-prepose-test.sc.9f.cn/fincloud/common.intf",headerMap,JSON.toJSONString(requestParam),"application/json; charset=UTF-8");
            logger.info("getBusOrderInfo => 请求结果 {}",orderInfo);
            if(orderInfo!=null&&!"".equals(orderInfo)){
                JSONObject results = JSON.parseObject(orderInfo);
                if(Objects.nonNull(results)&&"000000".equals(results.get("code"))&&"0000".equals(results.get("code"))){
                    JSONObject respResult = JSON.parseObject(String.valueOf(results.get("result")));
                    if(Objects.nonNull(respResult)&&"000000".equals(respResult.get("resp_code"))&&"0000".equals(respResult.get("resp_code"))){
                        JSONObject busOrderInfoVo = JSON.parseObject(String.valueOf(respResult.get("resp_result")));
                        return ResultDataUtil.successResult("从金融云工单中心获取数据成功",busOrderInfoVo.get("yearRate"));
                    }
                    else{
                        return ResultDataUtil.successResult("获取工单数据不正确，返回默认值，根据借款期数判断",getDefaultRateInfo(length));
                    }
                }
                else{
                    return ResultDataUtil.successResult("请求数据出错，返回默认值，根据借款期数判断",getDefaultRateInfo(length));
                }
            }
            else{
                return ResultDataUtil.successResult("请求数据为空，返回默认值，根据借款期数判断",getDefaultRateInfo(length));
            }
        }
        catch (Exception e){
            logger.warn("getBusOrderInfo =》fincloud feign 请求出错");
            return ResultDataUtil.successResult("执行异常，返回默认值，根据借款期数判断",getDefaultRateInfo(length));
        }
    }

    public static String httpPostWithHeaderAndBody(String url,Map<String,Object> header,String body,String contentType){
        logger.info("Callback url {},header {},body {},contentType {}",url,JSON.toJSONString(header),body,contentType);
        String resultStr="";
        PostMethod postMethod = new PostMethod(url);
        org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
        // 设置超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(30000);
        // 执行json 传输
        postMethod.setRequestHeader("content-type", contentType);
        // header 填写数据
        for(String key:header.keySet()){
            postMethod.setRequestHeader(key,String.valueOf(header.get(key)));
        }
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        try {
            // 设置  body
            RequestEntity entity = new StringRequestEntity(body);
            postMethod.setRequestEntity(entity);
            httpClient.executeMethod(postMethod);
            resultStr = postMethod.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("Callback 执行结果{}",resultStr);
        return resultStr;
    }

    public static String getDefaultRateInfo(int length){
        if(length==6){
            return "0.36";
        }
        else{
            return "0.24";
        }
    }


    public static  String getSerialNo() {
        Random rm = new Random();
        double num = 1 + rm.nextDouble();
        DecimalFormat df = new DecimalFormat("0.000000000000000");
        String formatNumber = df.format(num);
        String subStr = formatNumber.substring(2, 17);
        return subStr;
    }

    public static String date2Str(Date date, String format) {
        if (null != date && !"".equals(date)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        }
        return null;
    }

    public static String stringInMd5(String str) {
        // 消息签名（摘要）
        MessageDigest md5 = null;
        try {
            // 参数代表的是算法名称
            md5 = MessageDigest.getInstance("md5");
            byte[] result = md5.digest(str.getBytes());
            String hexStr = bytesToHex(result);
            return hexStr;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(getBusOrderInfo(6,"666","1300002655")));
    }

}
