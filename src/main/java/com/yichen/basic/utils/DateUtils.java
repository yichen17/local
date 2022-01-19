package com.yichen.basic.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/1/19 14:02
 * @describe
 */
@Slf4j
public class DateUtils {

    private static final String BASIC_TIME_PATTERN="yyyy-MM-dd hh:mm:ss";

    /**
     * 日期转时间戳
     * @param time  yyyy-MM-dd hh:mm:ss 格式日期
     * @return 时间戳
     */
    public static String timeToTimestamp(String time){
        try{
            SimpleDateFormat sdf=new SimpleDateFormat(BASIC_TIME_PATTERN);
            Date parse = sdf.parse(time);
            return String.valueOf(parse.getTime());
        }
        catch (ParseException e){
            log.error("转换日期出错，日期数据 {}，转换格式 {}",time,BASIC_TIME_PATTERN);
            return null;
        }
    }

    /**
     * 时间戳转日期
     * @param timestamp 时间戳
     * @return yyyy-MM-dd hh:mm:ss 格式日期
     */
    public static String timestampToTime(String timestamp){
        SimpleDateFormat sdf=new SimpleDateFormat(BASIC_TIME_PATTERN);
        return sdf.format(new Date(Long.parseLong(timestamp)));
    }


}
