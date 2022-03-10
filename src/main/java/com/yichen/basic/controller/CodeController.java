package com.yichen.basic.controller;

import com.yichen.basic.utils.CodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/2/25 15:55
 * @describe 编码controller
 */
@Api(tags = "编码工具类")
@RestController
@RequestMapping("/code")
public class CodeController extends BaseController{

    @PostMapping(value = "/gbToUnicode")
    @ApiOperation(value = "中文转unicode")
    public String gbEncoding(@RequestParam(required=true,name = "chinese")
                                 @ApiParam(name = "chinese",value = "中文字符",example = "奕晨" ) String chinese){
        logger.info("中文转unicode 入参 {}",chinese);
        return CodeUtils.gbEncoding(chinese);
    }

    @PostMapping(value = "/unicodeToGb")
    @ApiOperation(value = "unicode转中文")
    public String decodeUnicode(@RequestParam(required=true,name = "unicode")
                                    @ApiParam(name = "unicode",value = "unicode码",example = "\\u5955\\u6668" ) String unicode){
        logger.info("unicode转中文 入参 {}",unicode);
//        unicode=unicode.replaceAll("#","\\\\");
        return CodeUtils.decodeUnicode(unicode);
    }


    @PostMapping("/urlEncode")
    @ApiOperation(value = "urlEncode 加密")
    public String urlEncode(@RequestParam(name = "s")
                            @ApiParam(name = "s", value = "加密字符串", example = "朋友")String s,
                            @RequestParam(name = "codeType")
                            @ApiParam(name = "codeType", value = "编码类型", example = "UTF-8")String codeType){
        logger.info("url encoder 入参 {} {}",s,codeType);
        try{
            return URLEncoder.encode(s,codeType);
        }
        catch (Exception e){
            logger.error("url encoder 出错 {}",e.getMessage(),e);
        }
        return null;
    }


    @PostMapping("/urlDecode")
    @ApiOperation(value = "urlDecode 解密")
    public String urlDecode(@RequestParam(name = "s")
                            @ApiParam(name = "s", value = "解密字符串", example = "%E6%9C%8B%E5%8F%8B")String s,
                            @RequestParam(name = "codeType")
                            @ApiParam(name = "codeType", value = "编码类型", example = "UTF-8")String codeType){
        logger.info("url decode 入参 {} {}",s,codeType);
        try{
            return URLEncoder.encode(s,codeType);
        }
        catch (Exception e){
            logger.error("url decode 出错 {}",e.getMessage(),e);
        }
        return null;
    }


}
