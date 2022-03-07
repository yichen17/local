package com.yichen.basic.service;

import com.yichen.basic.dto.CheckResultDTO;
import com.yichen.basic.dto.ResultData;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/7 17:27
 * @describe 校验请求结果service
 */
@Service
public class CheckRequestResultService {

    public ResultData checkTwoResult(CheckResultDTO paramA,CheckResultDTO paramB){

        // 请求数据


        // 这里需要对结果进行比对   方法有一下几种
        // 1、重写对象的toString()方法   =>  需要自己构造逻辑
        // 2、构建工具类对类对象逐字段比对 => 返回结构通常为json格式字符串  => 比对逻辑
        // 3、调用三方工具类   =>  是否有现成的

        return null;
    }

}
