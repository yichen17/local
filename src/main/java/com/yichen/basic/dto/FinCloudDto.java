package com.yichen.basic.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/1/19 9:35
 * @describe 金融云相关查询 dto
 */
@Data
public class FinCloudDto {

    @ApiModelProperty(name = "url", value = "请求url", dataType = "String", required = true,example = "http://bull-prepose-test.sc.9f.cn/fincloud/common.intf")
    private String url;

    @ApiModelProperty(name = "requestHeader", value = "请求头", dataType = "Map", required = true,example ="{\"code\":30340,\"secretKey\":\"vx4nrssUVMJL2tHM\",\"intfCode\":\"100206\",\"version\":\"1.0.0\",\"time\":\"1642404189821\",\"sign\":\"b4344e4a89f8915c424215d761e81a75\",\"channelId\":\"3034000001\",\"requestNo\":\"3034010020620220117766387484373089\"}" )
    private Map<String,Object> requestHeader;

    @ApiModelProperty(name = "jsonContent", value = "数据请求体", dataType = "Map", required = true,example = "{\"certId\":\"412702199203156057\",\"appId\":\"1300003635\",\"channelId\":\"1093\"}")
    private Map<String,Object> dataBody;

}
