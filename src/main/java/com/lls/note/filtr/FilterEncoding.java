package com.lls.note.filtr;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 过滤器实例：处理请求乱码
 * 字符乱码处理
 *   乱码情况：
 *             Tomcat8及以上版本                         Tomcat7及以下版本
 *
     POST请求
               乱码，需要处理                             乱码，需要处理
               request.setCharacterEncoding("UTF-8");
 *   GET请求
 *             不会乱码，不需要处理                        乱码，需要处理
 *                                                       new String(request.getParameter("参数名").getBytes("ISO-8859-1"),"UTF-8");
 *
 *  如何处理：
 *      1、处理POST请求
 *          request.setCharacterEncoding("UTF-8");
*       2、处理GET请求且服务器版本在Tomcat8以下的
*           1> 得到请求类型 （GET请求）
*           2> 得到服务器的版本的信息
*           3> 判断是GET请求且Tomcat版本小于8
*           4> 处理乱码
*                newString(request.getParameter("参数名").getBytes("ISO-8859-1"),"UTF-8");
 *
 *
 *
 */

@WebFilter("/*")
public class FilterEncoding implements Filter {
    public FilterEncoding() {}

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 基于HTTP
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 处理请求乱码乱码 （处理POST请求）
        request.setCharacterEncoding("UTF-8");

        // 处理GET请求且服务器版本在Tomcat8以下的
        String method = request.getMethod();
        // 如果是GET请求(忽略大小写进行比较)
        if ("GET".equalsIgnoreCase(method)) {
            // 服务器版本在Tomcat8以下的   ApacheTomcat/8.0.45
            String serverInfo = request.getServletContext().getServerInfo();
            // 得到具体的版本号
            String versionStr = serverInfo.substring(serverInfo.indexOf("/")+1, serverInfo.indexOf("."));
            // 判断服务器版本是否小于8
            if (Integer.parseInt(versionStr) < 8) {
                // 得到自定义内部类 （MyWapper继承了HttpServletRequestWapper对象，
                // 而HttpServletRequestWapper对象实现了HttpServletRequest接口，所以MyWapper的本质也是 request对象）
                HttpServletRequest myRequest = new MyWrapper(request);
                // 放行资源
                filterChain.doFilter(myRequest, response);
                return;
            }
        }
        // 放行资源
        filterChain.doFilter(request, response);

    }
   //ServletRequest   HttpServletRequestWrapper
    /**
     * 定义内部类，继承HttpServletRequestWrapper包装类对象，重写getParameter()方法
     */
    class MyWrapper extends HttpServletRequestWrapper {

        // 定义成员变量，提升构造器 中的request对象的范围
        private HttpServletRequest request;
        public MyWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }
        /**
         * 重写getParameter()方法
         */
        @Override
        public String getParameter(String name) {
            String value = request.getParameter(name);
            if (value != null && !"".equals(value.trim())) {
                try {
                    // 将默认ISO-8859-1编码的字符转换成UTF-8
                    value = new String(value.getBytes("ISO-8859-1"),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return value;
        }
   }



}
