package com.yichen.basic.controller;

import com.yichen.basic.dto.ResultData;
import com.yichen.basic.dto.ResultDataUtil;
import com.yichen.basic.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


}
