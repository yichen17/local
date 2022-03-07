package com.yichen.basic.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/7 16:57
 * @describe 校验两个方法入参结果 dto
 */
@Data
public class CheckResultDTO {

    @ApiModelProperty(name = "url",value = "请求地址 ",dataType = "String",required = true
            ,example = "http://user-adapter-test.sc.9f.cn/userapi/userinfo/getAccountOrCustomerByType")
    private String url;

    @ApiModelProperty(name = "header", value = "请求头", dataType = "Map", required = false,
            example = "{\"token\":\"e115030051b10100752d89df47274ad091fee0d1a9e8b0e0\"}")
    private Map<String,Object> header;

    @ApiModelProperty(name = "body", value = "请求体", dataType = "Map", required = false,
        example = "{\"mobile\":\"18600296208\"}")
    private Map<String,Object> body;

    @ApiModelProperty(name = "type", value = "传输方式", dataType = "String", required = true, example = "GET")
    private String type;

    @ApiModelProperty(name = "checkFields", value = "校验字段,多字段以分号分隔", dataType = "String", required = true, example = "data.account;data.customer")
    private String checkFields;

}