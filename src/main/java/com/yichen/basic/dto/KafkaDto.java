package com.yichen.basic.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/1/25 10:38
 * @describe
 */
@Data
public class KafkaDto {

    @ApiModelProperty(name = "topic",value = "kafka topic ",dataType = "String",required = true,example = "onecard.orderservice.all.loan")
    private String topic;

    @ApiModelProperty(name = "data",value = "数据体",dataType = "Map",required = true,
    example = "{\"appId\":\"11128341939781000\",\"proId\":\"a107b4d6993f7788af7ae7b94b3c6dac\",\"productId\":\"865\",\"remark\":\"local 测试\"}")
    private Map<String,Object> data;

}
