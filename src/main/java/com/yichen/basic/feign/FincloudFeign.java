package com.yichen.basic.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/1/25 14:08
 * @describe
 */
@FeignClient(name = "fincloud",url = "${url.fincloud}")
public interface FincloudFeign {

    @PostMapping(value = "/fincloud/common.intf",consumes = MediaType.APPLICATION_JSON_VALUE)
    String request(@RequestHeader Map<String,String> header, @RequestBody Map<String,Object> body);

}
