package com.yichen.basic.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/12 13:56
 * @describe 接口切换dto
 *   用途：  新老接口替换比对数据的反参是否同原来的一样
 *   =>  入参、请求方式一致，仅调用地址不同
 *   =>    比对返回结果数据，可以多，但原来的数据必须存在且一致
 */
@Data
@Builder
public class InterfaceSwitchDTO {

    @ApiModelProperty(name = "oldUrl",value = "旧的请求地址 ",dataType = "String",required = true
            ,example = "http://onecard-user-api-test.sc.9f.cn/user/select/selectByFiled")
    private String oldUrl;

    @ApiModelProperty(name = "newUrl",value = "新的请求地址 ",dataType = "String",required = true
            ,example = "http://user-adapter-test.sc.9f.cn/userapi/userinfo/getAccountOrCustomerByType")
    private String newUrl;


    @ApiModelProperty(name = "header", value = "请求头", dataType = "Map", required = false,
            example = "{\"token\":\"e115030051b10100752d89df47274ad091fee0d1a9e8b0e0\"}")
    private Map<String,String> header;

    @ApiModelProperty(name = "body", value = "请求体", dataType = "Map", required = false,
            example = "{\"mobile\":\"18600296208\",\"selectType\":\"120ac\",\"certId\":\"340827199309120020\",\"customerId\":\"1170259320\",\"md5Mobile\":\"05c24b607ffaaa2bfb71cf205504f4a1\",\"accountId\":\"22041166666925628\"}")
    private Map<String,Object> body;

    @ApiModelProperty(name = "type", value = "传输方式", dataType = "String", required = true, example = "POST_FORM")
    private String type;

    @ApiModelProperty(name = "checkFields", value = "校验字段,多字段以分号分隔", dataType = "String", required = true, example = "data.account;data.customer")
    private String  checkFields;

    @ApiModelProperty(name = "excludeFields", value = "不需要匹配的字段，以分号分隔", dataType = "String", required = true, example = "label;isLableMember;isPzscLable")
    private String excludeFields;

}
