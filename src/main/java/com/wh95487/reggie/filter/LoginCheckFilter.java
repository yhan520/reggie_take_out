package com.wh95487.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.wh95487.reggie.common.BaseContext;
import com.wh95487.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String requestURI = httpServletRequest.getRequestURI();

        log.info("拦截到请求：{}",requestURI);

        // 定义不需要处理的请求路径
        String[] urls = {"/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };
        //判断此次请求是否需要处理
        boolean check = check(requestURI, urls);

        log.info("拦截到请求：{}",check);

        //判断本次请求需不需要处理，如果不需要处理，就直接放行
        //别忘了return
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(httpServletRequest, httpServletResponse);

            return;
        }
        //请求需要处理，再判断员工是否登录，如果已经登录，将当前员工ID存入ThreadLocal，然后放行
        if(httpServletRequest.getSession().getAttribute("employee") != null){
            Long employee = (Long) httpServletRequest.getSession().getAttribute("employee");

            log.info("用户已登录，用户id为：{}", employee);
            BaseContext.setCurrentId(employee);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        //判断用户是否登录，如果已经登录，将当前员工ID存入ThreadLocal，然后放行
        if(httpServletRequest.getSession().getAttribute("user") != null){
            Long user = (Long) httpServletRequest.getSession().getAttribute("user");

            log.info("用户已登录，用户id为：{}", user);
            BaseContext.setCurrentId(user);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        log.info("用户未登录");
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }
    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String requestURI, String[] urls){
        for (String url : urls){
            if(PATH_MATCHER.match(url, requestURI)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
