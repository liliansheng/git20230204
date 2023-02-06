package com.lls.note.filtr;

import com.lls.note.po.User;

import javax.servlet.*;
import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录过滤器，拦截所有资源
 *     放行资源：
 *        1、指定页面（如：登录页面、注册页面）
 *        2、静态资源 (如：js、css、image)
 *        3、指定操作（如：登录操作、注册操作（点击登录按钮事件，放行点击后请求的资源路径））
 *        4、登录状态（如果通过验证、登录成功，则放行（所有）资源，）登录成功后会设置一个session域对象，
 *           对过判断session域对象是否存在，来确定登录状态是成功还是失败
 */
@WebFilter("/*")
public class ALoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
       //基于Http
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //得到资源路径
        String path = request.getRequestURI();
        System.out.println(path);
        /*1、指定页面*/
        if (path.contains("/login.jsp")) {
            filterChain.doFilter(request,response);
            return;
        }
        /*2、静态资源*/
        if (path.contains("/statics")){
            filterChain.doFilter(request,response);
            return;
        }
        /*3、指定操作*/
        if (path.contains("/user")){
            String actionName = request.getParameter("actionName");
            if ("login".equals(actionName)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        /*4、登录状态*/
        User user = (User) request.getSession().getAttribute("user");
        if (user != null){
            filterChain.doFilter(request,response);
            return;
        }

        /**
         * 免登录
         */
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user")){
                    String value = cookie.getValue();
                    String[] val = value.split("-");
                    String userName = val[0];
                    String userPwd = val[1];
                    String url = "user?actionName=login&rem=1&userName="+userName+"&userPwd="+userPwd;
                    request.getRequestDispatcher(url).forward(request,response);
                    return;
                }
            }
        }



        //当用户未登录时重定向到登录页面
        response.sendRedirect("login.jsp");

    }

    @Override
    public void destroy() {

    }
}
