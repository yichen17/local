package com.yichen.basic.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/8/4 13:54
 * @describe 缓存key查询 dto
 */
@Data
public class CacheKeyDTO {
    @ApiModelProperty(name = "type", value = "key来源类型， 用户-onecardUser 账单-onecard", dataType = "String", required = true, example = "onecardUser")
    private String type;

    @ApiModelProperty(name = "module", value = "模块 系统配置-sysConfig 用户token-loginInfo", dataType = "String", required = true, example = "sysConfig")
    private String module;

    @ApiModelProperty(name = "key", value = "缓存key", dataType = "String", required = true, example = "")
    private String key;

}
