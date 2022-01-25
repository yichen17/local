package com.yichen.basic.controller;

import com.alibaba.fastjson.JSONObject;
import com.yichen.basic.dto.KafkaDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/1/25 10:00
 * @describe kafka controller
 */
@RestController
@RequestMapping("/kafka")
@Api(tags ="发送kafka消息")
public class KafkaController extends BaseController {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    /**
     * 发送kafka消息
     * @param dto kafka => topic 和 data
     * @return
     */
    @PostMapping("/send")
    @ApiOperation(value = "/send",
            consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public String sendKafkaMsg(@RequestBody @Validated KafkaDto dto){
        logger.info("kafka发送消息 接收入参 {}",JSONObject.toJSONString(dto));
        kafkaTemplate.send(dto.getTopic(), JSONObject.toJSONString(dto.getData()));
        return "成功";
    }
}
