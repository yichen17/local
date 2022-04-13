package com.yichen.basic.controller;

import com.alibaba.fastjson.JSON;
import com.yichen.basic.dto.*;
import com.yichen.basic.service.CheckRequestResultService;
import com.yichen.basic.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
            @RequestParam(required=true,name = "time") @ApiParam(name = "time",value = "yyyy-MM-dd HH:mm:ss格式时间",example = "2022-01-19 00:00:00" ) String time
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

    @GetMapping(value = "/getCurrentTimestamp")
    @ApiOperation(value = "获取当前时间的时间戳")
    public ResultData getCurrentTimestamp(){
        logger.info("获取当前时间的时间戳");
        return ResultDataUtil.successResult("当前时间的时间戳",System.currentTimeMillis());
    }



    @PostMapping(value = "/checkTwoRequestResult",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "两接口数据返回结果比对 => 可用于数据重构  =>  b中必须包含a中所有字段")
    public ResultData checkTwoRequestResult(@RequestBody RequestDTO dto){
        Set<String> excludeFields ;
        if (Objects.isNull(dto.getExcludeFields())){
            excludeFields = new HashSet<>();
        }
        else {
            excludeFields = new HashSet<String>(Arrays.asList(dto.getExcludeFields().split(";")));
        }
        CheckDataStore dataStore = CheckDataStore.builder().excludeFields(excludeFields).diffPath(new Stack<>()).build();
        CheckRequestResultService.DATA_STORE.set(dataStore);
        ResultData result = checkRequestResultService.checkTwoResult(dto.getDtoA(), dto.getDtoB(), dto.getCheckFields());
        CheckRequestResultService.DATA_STORE.remove();
        return result;
    }

    @PostMapping(value = "/checkTwoStringResult",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "比对两个字符串格式的json数据是否一样，b包含a中所有")
    public ResultData checkTwoStringResult(
            @RequestParam("jsonA")@ApiParam(name = "jsonA", value = "字符串形式a", example = "{\"name\":\"shanliang\"}")String jsonA,
            @RequestParam("jsonB")@ApiParam(name = "jsonB", value = "字符串形式b", example = "{\"name\":\"yichen\"}")String jsonB){
        boolean isSame = checkRequestResultService.compareJsonData(JSON.parseObject(jsonA), JSON.parseObject(jsonB));
        if (isSame){
            return ResultDataUtil.successResult("比对一致");
        }
        return ResultDataUtil.successResult("比对不一致");
    }

    @PostMapping(value = "/interfaceSwitch",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "新老接口切换结果比对 => 请求入参、请求方式一致，仅请求地址即反参可能变化  =>  新接口必须包含旧接口返回的所有内容，可额外增加")
    public ResultData interfaceSwitch(@RequestBody InterfaceSwitchDTO dto){
        Set<String> excludeFields ;
        if (Objects.isNull(dto.getExcludeFields())){
            excludeFields = new HashSet<>();
        }
        else {
            excludeFields = new HashSet<String>(Arrays.asList(dto.getExcludeFields().split(";")));
        }
        CheckDataStore dataStore = CheckDataStore.builder().excludeFields(excludeFields).diffPath(new Stack<>()).build();
        CheckRequestResultService.DATA_STORE.set(dataStore);
        RequestDTO.CheckResultDTO oldInterface = RequestDTO.CheckResultDTO.builder().url(dto.getOldUrl()).body(dto.getBody()).header(dto.getHeader()).type(dto.getType()).build();
        RequestDTO.CheckResultDTO newInterface = RequestDTO.CheckResultDTO.builder().url(dto.getNewUrl()).body(dto.getBody()).header(dto.getHeader()).type(dto.getType()).build();
        ResultData result = checkRequestResultService.checkTwoResult(oldInterface, newInterface, dto.getCheckFields());
        CheckRequestResultService.DATA_STORE.remove();
        return result;
    }


}
