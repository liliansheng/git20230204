package com.lls.note.web;

import cn.hutool.core.util.StrUtil;
import com.lls.note.po.Note;
import com.lls.note.po.User;
import com.lls.note.service.NoteService;
import com.lls.note.util.JsonUtil;
import com.lls.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**数据报表模块*/
@WebServlet("/report")
public class ReportController extends HttpServlet {
    NoteService noteService = new NoteService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //导航高亮
        request.setAttribute("menu_page","report");

        //得到用户行为
        String actionName = request.getParameter("actionName");
        //进入数据报表页面
        if (actionName.equals("info")){
           reportInfo(request,response);
        }
        //查询报表数据
        if (actionName.equals("month")){
            queryNoteCountByMonth(request,response);
        }
    }

    /**查询报表数据，（按月份进行分组及每组对应的数量）*/
    private void queryNoteCountByMonth(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        ResultInfo<Map<String,Object>> resultInfo = noteService.queryNoteCountByMonth(user.getUserId());
        //将对象转成JSON类型返回，响应ajax请求
        JsonUtil.toJson(response,resultInfo);
    }


    //进入数据报表页
    private void reportInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("changePage","report/info.jsp");
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }



}
