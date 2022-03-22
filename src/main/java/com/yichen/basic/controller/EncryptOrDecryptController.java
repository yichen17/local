package com.yichen.basic.controller;

import com.alibaba.fastjson.JSON;
import com.yichen.basic.annotation.AndroidDecrypt;
import com.yichen.basic.annotation.H5Decrypt;
import com.yichen.basic.dto.TestEncode;
import com.yichen.basic.utils.DataUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/21 17:58
 * @describe 加解密 controller
 */
@RestController
@RequestMapping("/crypt")
public class EncryptOrDecryptController {

    @RequestMapping("/testAES")
    @AndroidDecrypt
    public String testAES(@RequestBody TestEncode encode){
//        DataUtils.putDecodeDateToParam(encode);
        return JSON.toJSONString(encode);
    }

    @RequestMapping("/testRSA")
    @H5Decrypt
    public String testRSA(@RequestBody TestEncode encode){
//        DataUtils.putDecodeDateToParam(encode);
        return JSON.toJSONString(encode);
    }

    @RequestMapping("/test")
    @H5Decrypt
    @AndroidDecrypt
    public String test(@RequestBody TestEncode encode){
        return JSON.toJSONString(encode);
    }





}
