package com.yichen.basic.controller;

import com.alibaba.fastjson.JSON;
import com.yichen.basic.annotation.AndroidDecrypt;
import com.yichen.basic.annotation.H5Decrypt;
import com.yichen.basic.dto.TestEncode;
import com.yichen.basic.utils.DataUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/21 17:58
 * @describe 加解密 controller
 */
@Api(tags = "数据加解密")
@RestController
@RequestMapping("/crypt")
@Slf4j
public class EncryptOrDecryptController {

    @PostMapping("/testAES")
    @AndroidDecrypt
    public String testAES(@RequestBody TestEncode encode){
//        DataUtils.putDecodeDateToParam(encode);
        return JSON.toJSONString(encode);
    }

    @PostMapping("/testRSA")
    @H5Decrypt
    public String testRSA(@RequestBody TestEncode encode){
//        DataUtils.putDecodeDateToParam(encode);
        return JSON.toJSONString(encode);
    }

    @PostMapping("/test")
    @H5Decrypt
    @AndroidDecrypt
    public String test(@RequestBody TestEncode encode){
        return JSON.toJSONString(encode);
    }

    @GetMapping(value = "/androidDecrypt")
    @ApiOperation(value = "android 版本解密")
    public String androidDecrypt(@RequestParam("encrypt")
                                     @ApiParam(name = "encrypt", value = "解密数据", example = "hyHy/P8pabsyUHt2yX4V9Td1VDC5PNd9MradMEFDQyJ6ilZOdFB6/erP/E9KmjaRuMpg5I0SvRSwLmUKLlrUNWLhAIoU8lVP7NTOtdgcpxdIjrUUpUWEzeaZta4Z89qUEJ8bcbzYg10cGy9ZjqQjEZBSRB9emyWHEaQq4hGoOl3O9SYX90OpB7G7ducrmtHcGGGvLeet6uam7Luv9Gp6Mqm7RDWY/zkb2mc9XRLJzUMhpb24p4ad2lQ7o8XslfE4gDr5dMch9STJaHiqrtBQd1z6B8WO96ZMbba8czD4NDy0U4OVHSeaY+KUERQwXZPnNJEuxA96w+8FWVSnVBoT6cFdFOzAp7bwwyRpMI0GCYEocfP2kxaAb2o9WjV7eJzgFnf4FThF2lYLXdl6dAw/XQ==")String encrypt)throws Exception{
        return DataUtils.decryptDataAes(encrypt);
    }

    @GetMapping(value = "/H5Decrypt")
    @ApiOperation(value = "H5 版本解密")
    public String h5Decrypt(@RequestParam("encrypt")
                                @ApiParam(name = "encrypt", value = "解密数据", example = "BYNBDd9cKaGNddD6k3/z3tkc22CpEQpJ+19BCAWm5oAtsITYCKOyon87bbCFeh5B3cT1mF375IOLPjmjLx2jL/pOE5ajw+QPuGfRE4Hl2rXnF9VagvC4qCiAZc2B6vAxizsr3qfR0LGds+deVfqj6pfUt7s5yljVXWoPNk1Q2XqMhCm/umrKaeiFdita9wK1FaGQQW6FwW3+ksyyGYnwAdaT9zTKzfGuR5gg+mXl24XyW9s0WAr882aHfmBn3zpZuKts/EXphnQTTDXbfK5OzDSD/gO+Xh6k2q4kIkbjMHBtX4xfxJUEepJBZVWMbiv5tiyF14VCdS83oroOyGpGIw==")String encrypt)throws Exception{
        return DataUtils.decryptDataRas(encrypt);
    }

    @GetMapping(value = "/android/encrypt")
    @ApiOperation(value = "android 数据加密")
    public String androidEncrypt(@RequestParam @ApiParam(name = "data",value = "android 方式加密数据",
            example = "{\"mobile\":\"13733621659\"}") String data){
        log.info("android 数据加密 {}",data);
        return DataUtils.androidEncrypt(data);
    }


    @GetMapping(value = "/h5/encrypt")
    @ApiOperation(value = "h5 数据加密")
    public String h5Encrypt(@RequestParam @ApiParam(name = "data",value = "h5 方式加密数据",
            example = "{\"mobile\":\"13733621659\"}") String data){
        log.info("h5 数据加密 {}",data);
        return DataUtils.h5Encrypt(data);
    }










}
