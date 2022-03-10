package com.yichen.basic.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.Stack;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/10 15:09
 * @describe
 */
@Data
@Builder
public class CheckDataStore {

    private Set<String> excludeFields;

    private Stack<String> diffPath;

}
