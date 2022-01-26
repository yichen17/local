package com.yichen.basic.feign.service;

import com.alibaba.fastjson.JSONObject;
import com.yichen.basic.feign.FincloudFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/1/25 16:10
 * @describe
 */
@Service
@Slf4j
public class FincloudFeignService {



    @Resource
    private FincloudFeign feign;

    public String  getFincloudData(Map<String,String> header, Map<String,Object> body){
        String result;
        try{
            log.info("查询金融云入参 请求头 {} 请求体 {}", JSONObject.toJSONString(header),JSONObject.toJSONString(body));
            result = feign.request(header, body);
            log.info("请求结果 {}",result);
        }
        catch (Exception e){
            log.error("请求金融云出错 {}",e);
            result=null;
        }
        return result;
    }


}
