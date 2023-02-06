package com.lls.note.web;

import com.lls.note.po.User;
import com.lls.note.service.UserService;
import com.lls.note.vo.ResultInfo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;

@WebServlet("/user")
@MultipartConfig//接收文件上传
public class UserController extends HttpServlet {
    private UserService userService = new UserService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //设置首页导航高亮
        request.setAttribute("menu_page","user");


        //判断用户行为
        String actionName = request.getParameter("actionName");
        //登录操作
        if (actionName.equals("login")){
            userLogin(request,response);
        }
        //退出登录
        else if (actionName.equals("logout")){
            userLogout(request,response);
        }
        //进入个人中心
        else if (actionName.equals("userCenter")){
            userCenter(request,response);
        }
        //加载头像
        else if (actionName.equals("userHead")){
            userHead(request,response);
        }
        //验证昵称唯一性
        else if (actionName.equals("checkNick")){
            checkNick(request,response);
        }
        //修改用户信息
        else if (actionName.equals("updateUser")){
            updateUser(request,response);
        }


    }

    /**修改用户信息（与其他不同，参数还在controller层接收,而在service层接收，
       参数多 或对request、或response做处理 可选择在service中接收参数）
    */
    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultInfo<User> resultInfo = userService.updateUser(request,response);
        request.setAttribute("resultInfo",resultInfo);
        //修改后请求转发到个人中心，自动会把返回的数据加载出来
        request.getRequestDispatcher("user?actionName=userCenter").forward(request,response);

    }

    //验证昵称唯一性
    private void checkNick(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nick = request.getParameter("nick");
        User user = (User) request.getSession().getAttribute("user");
        Integer code = userService.checkNick(nick,user.getUserId());
        response.getWriter().write(code + "");
        response.getWriter().close();

    }

    /**
     * 加载头像
     * @param request
     * @param response
     */
    private void userHead(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String head = request.getParameter("imageName");
      String path = request.getServletContext().getRealPath("/WEB-INF/upload/");
        File file = new File(path + "/" + head);
        String pic = head.substring(head.lastIndexOf(".")+1);
        //如果不设置响应类型，则为二进制输出（乱码）
        if ("PNG".equalsIgnoreCase(pic)){
           response.setContentType("image/png");
        }if ("JPG".equalsIgnoreCase(pic) || "JPEG".equalsIgnoreCase(pic)){
            response.setContentType("image/jpg");
        }if ("GIF".equalsIgnoreCase(pic)) {
            response.setContentType("image/gif");
        }
        FileUtils.copyFile(file,response.getOutputStream());

    }

    /**
     * 进入个人中心
     * @param request
     * @param response
     */
    private void userCenter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("changePage","user/info.jsp");
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }

    /**
     * 用户退出
     *    清除cookie
     *    清除session
     *    重定向到登录页面
     * @param request
     * @param response
     */
    private void userLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
       request.getSession().invalidate();
       Cookie cookie = new Cookie("user",null);
       cookie.setMaxAge(0);
       response.addCookie(cookie);
       response.sendRedirect("login.jsp");
    }

    /**
     * 用户登录
     * @param request
     * @param response
     */
    private void userLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("userName");
        String userPwd = request.getParameter("userPwd");

        //session和cookie域名相同不冲突
        ResultInfo<User> resultInfo = userService.userLogin(userName, userPwd);
        if (resultInfo.getCode() == 1) {//登录成功
            request.getSession().setAttribute("user", resultInfo.getResult());
            //判断用户是否记住密码
            String rem = request.getParameter("rem");
            if ("1".equals(rem)) {
                Cookie cookie = new Cookie("user", userName + "-" + userPwd);
                cookie.setMaxAge(7 * 24 * 60 * 60);
                response.addCookie(cookie);
            } else {
                Cookie cookie = new Cookie("user", null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }

            response.sendRedirect("index");

        } else {//登录失败
            request.setAttribute("resultInfo", resultInfo);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

}
