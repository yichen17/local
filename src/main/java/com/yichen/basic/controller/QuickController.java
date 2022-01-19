package com.yichen.basic.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yichen.basic.dto.FinCloudDto;
import com.yichen.basic.dto.ResultData;
import com.yichen.basic.dto.ResultDataUtil;
import com.yichen.basic.service.FincloudService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2021/12/24 17:14
 * @describe
 */
@RequestMapping("/quick")
@RestController
@Api(tags ="金融云相关-快捷调用服务")
public class QuickController extends BaseController{


    @ApiOperation(value = "请求金融云数据")
    @PostMapping(value = "/fincloud",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultData getDataFromFincloud(@RequestBody @Validated FinCloudDto dto){
        logger.info("查询金融云 请求参数 {} ", JSON.toJSONString(dto));
        // 默认提供 sign 标识
        return FincloudService.queryByParams(dto);
    }

    @ApiOperation(value = "查询工单信息-年利率")
    @PostMapping(value = "/orderInfo/rate",consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultData getRateFromFincloud(
            @RequestParam(required=true,name = "productId") @ApiParam(name = "productId",value = "产品号",example = "867" ) String productId,
            @RequestParam(required=true,name = "busCode") @ApiParam(name = "busCode",value = "金融云工单号",example = "1300002047" ) String busCode,
            @RequestParam(required=true,name = "saleChannel") @ApiParam(name = "saleChannel",value = "渠道号，需要匹配 万卡的为1056，湖消的为1093",example = "1056" ) String saleChannel){
        logger.info("查询工单信息-年利率 请求入参 产品号{} 金融云工单号{} 渠道号{}",productId,busCode,saleChannel);
        return FincloudService.getYearRate(productId,busCode,saleChannel);
    }

    @ApiOperation(value = "查询工单信息")
    @PostMapping(value = "/orderInfo",consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultData getOrderInfoFromFincloud(
            @RequestParam(required=true,name = "productId") @ApiParam(name = "productId",value = "产品号",example = "867" ) String productId,
            @RequestParam(required=true,name = "busCode") @ApiParam(name = "busCode",value = "金融云工单号",example = "1300002047" ) String busCode,
            @RequestParam(required=true,name = "saleChannel") @ApiParam(name = "saleChannel",value = "渠道号，需要匹配 万卡的为1056，湖消的为1093",example = "1056" ) String saleChannel){
        logger.info("查询工单信息-年利率 请求入参 产品号{} 金融云工单号{} 渠道号{}",productId,busCode,saleChannel);
        return FincloudService.getBusOrderInfo(productId,busCode,saleChannel);
    }


    @ApiOperation(value = "获取默认年利率信息")
    @PostMapping(value = "/defaultRateInfo",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultData getDefaultRateInfo(@RequestParam(required=true,name = "len") @ApiParam(name = "len",value = "还款期数",example = "12" ) int len){
        logger.info("查询默认年利率入参 还款期数 {}",len);
        return ResultDataUtil.successResult(FincloudService.getDefaultRateInfo(len));
    }


    public static JSONObject getJSONParam(HttpServletRequest request) {
        JSONObject jsonParam = null;
        try {
            // 获取输入流
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

            // 写入数据到Stringbuilder
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = streamReader.readLine()) != null) {
                sb.append(line);
            }
            jsonParam = JSONObject.parseObject(sb.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("getJsonParam => {}", JSON.toJSONString(jsonParam));
        return jsonParam;
    }


}
