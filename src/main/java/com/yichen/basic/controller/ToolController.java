package com.yichen.basic.controller;

import com.yichen.basic.dto.RequestDTO;
import com.yichen.basic.dto.ResultData;
import com.yichen.basic.dto.ResultDataUtil;
import com.yichen.basic.service.CheckRequestResultService;
import com.yichen.basic.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/1/19 13:58
 * @describe 工具类 controller
 */
@Api(tags = "提供常用的工具方法")
@RequestMapping("/tool")
@RestController
public class ToolController extends BaseController{

    @Autowired
    private CheckRequestResultService checkRequestResultService;


    @PostMapping(value = "/timeToTimestamp",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "yyyy-MM-dd hh:mm:ss 时间转为时间戳")
    public ResultData timeToTimestamp(
            @RequestParam(required=true,name = "time") @ApiParam(name = "time",value = "yyyy-MM-dd hh:mm:ss格式时间",example = "2022-01-19 00:00:00" ) String time
    ){
        logger.info("时间转为时间戳，入参 {}",time);
        String s = DateUtils.timeToTimestamp(time);
        if(s==null){
            return ResultDataUtil.errorResult("执行出错，请确认日期格式为 yyyy-MM-dd hh:mm:ss");
        }
        return ResultDataUtil.successResult(s);
    }

    @PostMapping(value = "/timestampToTime",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "时间戳转为 yyyy-MM-dd hh:mm:ss 格式 时间")
    public ResultData timestampToTime(
            @RequestParam(required=true,name = "timestamp") @ApiParam(name = "timestamp",value = "时间戳，精确到毫秒",example = "1642404189821" ) String timestamp
    ){
        logger.info("时间戳转为日期，入参 {}",timestamp);
        String s = DateUtils.timestampToTime(timestamp);
        return ResultDataUtil.successResult(s);
    }

    @PostMapping(value = "/checkTwoResult",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "两接口数据返回结果比对 => 可用于数据重构  =>  b中必须包含a中所有字段")
    public ResultData checkTwoResult(@RequestBody RequestDTO dto){
        // 这里需要对结果进行比对   方法有一下几种
        // 1、重写对象的toString()方法   =>  需要自己构造逻辑
        // 2、构建工具类对类对象逐字段比对 => 返回结构通常为json格式字符串  => 比对逻辑
        // 3、调用三方工具类   =>  是否有现成的
        return checkRequestResultService.checkTwoResult(dto.getDtoA(),dto.getDtoB(),dto.getCheckFields());
    }
}
