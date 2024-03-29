package com.yichen.basic.controller;

import com.yichen.basic.utils.CodeUtils;
import com.yichen.basic.utils.EncryptUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

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
            return URLDecoder.decode(s,codeType);
        }
        catch (Exception e){
            logger.error("url decode 出错 {}",e.getMessage(),e);
        }
        return null;
    }

    @PostMapping("base64Encode")
    @ApiOperation(value = "base64 加密数据")
    public String base64Encode(@RequestParam("data") @ApiParam(name = "data",value = "加密数据",example = "奕晨")String data){
        logger.info("base 64 加密入参 {}",data);
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("base64Decode")
    @ApiOperation(value = "base64 解密数据")
    public String base64Decode(@RequestParam("data") @ApiParam(name = "data",value = "解密数据",example = "奕晨")String data){
        logger.info("base 64 解密入参 {}",data);
        return new String(Base64.getDecoder().decode(data));
    }

    @GetMapping("/charToInt")
    @ApiOperation(value = "char 转为 int")
    public int charToInt(@RequestParam("c") @ApiParam(name = "c",value = "待转换成int值的char",example = "=") char c){
        logger.info("char to int 入参 {}",c);
        return (int)c;
    }

    @GetMapping("/intToChar")
    @ApiOperation(value = "char 转为 int")
    public char intToChar(@RequestParam("i") @ApiParam(name = "i",value = "待转换成char的int值",example = "32") int i){
        logger.info("int to char 入参 {}",i);
        return (char)i;
    }

    @GetMapping("/signDecode")
    @ApiOperation(value = "签名解密")
    public String signDecode(@RequestParam @ApiParam(name = "str", value = "待解密字符串", example = "4DA6DB24A0F5912B60495B71D64E17F4")String str,
                             @RequestParam @ApiParam(name = "salt", value = "盐值 账单密钥: pVrG+nGlxU4amKi39E6bTA== , api验签:M3A2OrXBF3ZcOFx0oCnZQ",
                                     example = "pVrG+nGlxU4amKi39E6bTA==")String salt){
        logger.info("签名解密入参 str {} salt {}", str, salt);
        return EncryptUtil.getInstance().aesDecode(str, salt);
    }

    @GetMapping("signEncode")
    @ApiOperation(value = "签名加密")
    public String signEncode(@RequestParam @ApiParam(name = "str", value = "加密字符串", example = "3681765")String str,
                             @RequestParam @ApiParam(name = "salt", value = "盐值 账单密钥: pVrG+nGlxU4amKi39E6bTA== , api验签:M3A2OrXBF3ZcOFx0oCnZQ",
                                     example = "pVrG+nGlxU4amKi39E6bTA==")String salt){
        logger.info("签名加密入参 str {} salt {}", str, salt);
        return EncryptUtil.getInstance().aesEncode(str, salt);
    }

    @GetMapping("escape")
    @ApiOperation(value = "去除所有转义(包含多层嵌套)")
    public String signEncode(@RequestParam @ApiParam(name = "escapeStr", value = "转义字符串",
            example = "\"{\\\\\\\"name\\\\\\\":\\\\\\\"xiaoxu\\\\\\\",\\\\\\\"age\\\\\\\":\\\\\\\"27\\\\\\\"}\"\n")String escapeStr){
        logger.info("去除所有转义(包含多层嵌套) escapeStr {}", escapeStr);
        String newStr = null;
        do {
            escapeStr = Objects.isNull(newStr) ? escapeStr : newStr;
            newStr = StringEscapeUtils.unescapeJava(escapeStr);
        }
        while (!newStr.equals(escapeStr));
        return StringEscapeUtils.unescapeJava(newStr);
    }


}
