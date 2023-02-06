package com.lls.note.web;

import com.alibaba.fastjson.JSON;
import com.lls.note.po.NoteType;
import com.lls.note.po.User;
import com.lls.note.service.NoteTypeService;
import com.lls.note.util.JsonUtil;
import com.lls.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/type")
public class NoteTypeController extends HttpServlet {
          private NoteTypeService noteTypeService = new NoteTypeService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //道行高亮
        request.setAttribute("menu_page","type");
        //得到用户行为
        String actionName = request.getParameter("actionName");
        //查询类型列表
        if (actionName.equals("list")){
            typeList(request,response);
        }
        //删除note类型
        else if (actionName.equals("delete")){
            deleteType(request,response);
        }
        //类型的添加或修改
        else if (actionName.equals("addOrUpdate")){
            addOrUpdate(request,response);
        }
    }

    /**类型的添加或修改*/
    private void addOrUpdate(HttpServletRequest request, HttpServletResponse response) {
       String typeName = request.getParameter("typeName");
       String typeId = request.getParameter("typeId");
       User user = (User) request.getSession().getAttribute("user");
       ResultInfo<Integer> resultInfo = noteTypeService.addOrUpdate(typeName,typeId,user.getUserId());
       JsonUtil.toJson(response,resultInfo);

    }

    /**删除note类型(行中删除按钮)*/
    private void deleteType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String typeId = request.getParameter("typeId");
        ResultInfo<NoteType> resultInfo = noteTypeService.deleteType(typeId);
        //调用工具类，返回JSON类型数据
        JsonUtil.toJson(response,resultInfo);
    }

    /**查询类型列表*/
    private void typeList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        Integer userId = user.getUserId();
        List<NoteType> typeList = noteTypeService.findTypeList(userId);
        request.setAttribute("typeList",typeList);
        request.setAttribute("changePage","type/list.jsp");
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }
}
