package com.yichen.basic.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/21 17:59
 * @describe 测试加密数据
 */
@Data
@Builder
public class TestEncode implements RequestEncodeAES,RequestEncodeRSA {

    @ApiModelProperty(value = "手机号", required = false)
    private String mobile;
    @ApiModelProperty(value = "登录类型", required = false)
    private String loginType;
    @ApiModelProperty(value = "设备指纹信息", required = false)
    private String userData;
    @ApiModelProperty(value = "微信openid", required = false)
    private String openId;
    @ApiModelProperty(value = "加密盐值", required = false)
    private String encryptedInfo;
    @ApiModelProperty(value = "h5加密数据", required = false)
    private String encryptedInfoForH5;

    //  以下为 h5 测试字段
    private String channel;


}
