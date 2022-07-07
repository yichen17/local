package com.yichen.basic.interceptor;

import com.yichen.basic.servlet.DecryptServletRequestWrapper;
import com.yichen.basic.utils.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/30 9:59
 * @describe 数据解密拦截器  => 处理逻辑，在这里进行 数据解密，这里处理操作理论上是先于 @Valid 执行的
 *   =>  http://stackoverflow.com/questions/28975025/advise-controller-method-before-valid-annotation-is-handled
 *   =>  https://stackoverflow.com/questions/39271035/how-do-i-get-my-spring-aspects-to-execute-before-valid-validated-annotation-on
 *   获取方法或类上的注解  =>  https://blog.csdn.net/yuru974882032/article/details/119577999
 */
@Slf4j
public class DecryptInterceptor implements HandlerInterceptor {

    private static final String DECRYPT_FIELD = "encryptedInfo";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception  {
        if (request instanceof DecryptServletRequestWrapper){
            DecryptServletRequestWrapper requestWrapper = (DecryptServletRequestWrapper) request;
            Map<String, Object> requestBody = requestWrapper.getRequestBody();
            // 判断是否有加密字段，有则解密
            if (requestBody.containsKey(DECRYPT_FIELD)){
                String decryptDataAes = DataUtils.decryptDataAes(String.valueOf(requestBody.get(DECRYPT_FIELD)));
                Map<String, Object> data = DataUtils.convertFormDataToMap(decryptDataAes);
                requestBody.putAll(data);
            }
            DecryptServletRequestWrapper.printMap(requestBody);
        }
        return true;
    }



}
