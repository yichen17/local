package com.yichen.basic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2021/12/24 15:32
 * @describe bus 工单查询 vo 包含 年华利率、签约时间等
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BusOrderInfoVo {

    private String code;
    private String message;
    private String result;

}
