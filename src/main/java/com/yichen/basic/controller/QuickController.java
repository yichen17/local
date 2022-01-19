package com.yichen.basic.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yichen.basic.dto.FinCloudDto;
import com.yichen.basic.dto.ResultData;
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
@Api(tags ="快捷调用服务")
public class QuickController extends BaseController{


    @ApiOperation(value = "请求金融云数据")
    @PostMapping(value = "/fincloud",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultData getDataFromFincloud(@RequestBody @Validated FinCloudDto dto){
        logger.info("查询金融云 请求参数 {} ", JSON.toJSONString(dto));
        // 默认提供 sign 标识
        return FincloudService.queryByParams(dto);
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
