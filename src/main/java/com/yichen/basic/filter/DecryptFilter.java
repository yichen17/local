package com.yichen.basic.filter;

import com.yichen.basic.servlet.DecryptServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/3/31 22:28
 * @describe
 */
@Slf4j
public class DecryptFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        DecryptServletRequestWrapper requestWrapper = null;
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            requestWrapper = new DecryptServletRequestWrapper(httpServletRequest);
        }
        catch (Exception e){
            log.error("DecryptServletRequestWrapper error {}",e.getMessage(),e);
        }
        chain.doFilter((Objects.isNull(requestWrapper) ? request : requestWrapper),response);
    }
}
