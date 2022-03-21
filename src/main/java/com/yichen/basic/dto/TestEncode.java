package com.yichen.basic.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/21 17:59
 * @describe 测试加密数据
 */
@Data
public class TestEncode implements RequestEncode{

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


}
