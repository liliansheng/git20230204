package com.lls.note.web;

import com.lls.note.po.Note;
import com.lls.note.po.User;
import com.lls.note.service.NoteService;
import com.lls.note.util.Page;
import com.lls.note.vo.NoteVo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/index")
public class IndexController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置首页导航高亮
        request.setAttribute("menu_page","index");

        //搜索框的条件查询
        //得到用户行为(判断是什么条件查询：标题、日期、类型)
        String actionName = request.getParameter("actionName");
//        String actionName = "aa";
        /**
         (页面中点击其他页数(页码)，查询条件还是查询所有的数据，没有起到条件查询的作用，
         所以将用户行为设置到作用域中,前端获取后作为页码的超链接条件查询参数)*/
        request.setAttribute("action",actionName);
//        if (actionName.equals("searchTitle")){
        if ("searchTitle".equals(actionName)){
            String title = request.getParameter("title");
            //设置回显数据
            request.setAttribute("title",title);
            //如果搜索框中有传值，则按模糊查询,
            noteList(request,response,title,null,null);
//        }else if(actionName.equals("searchDate")) {
        }else if("searchDate".equals(actionName)) {
            String date = request.getParameter("date");
            //设置回显数据
            request.setAttribute("date", date);
            //如果搜索框中有传值，则按模糊查询,
            noteList(request, response, null, date,null);
        }
//        else if(actionName.equals("searchTypeId")) {
        else if("searchTypeId".equals(actionName)) {
            String typeId = request.getParameter("typeId");
            //设置回显数据
            request.setAttribute("typeId", typeId);
            //如果搜索框中有传值，则按模糊查询,
            noteList(request, response, null, null,typeId);
        }
        else {//查询当前登录用户所有数据
            /**分页查询云记列表*/
            noteList(request,response,null,null,null);
        }



        //设置首页动态包含的页面
        request.setAttribute("changePage","note/list.jsp");
        request.getRequestDispatcher("index.jsp").forward(request,response);

    }

    /**分页查询云记列表*/
    private void noteList(HttpServletRequest request, HttpServletResponse response,String title,String date,String typeId){
        String pageNum = request.getParameter("pageNum");
        String pageSize = request.getParameter("pageSize");
        User user = (User) request.getSession().getAttribute("user");
        Page<Note> page = new NoteService().findNoteListByPage(pageNum,pageSize,user.getUserId(),title,date,typeId);
        request.setAttribute("page",page);
        //设置请求转发到首页，及首页包含的页面值，进入首页时已设置，不用再设置了

        //首页左侧日期分组查询
        List<NoteVo> dateInfo = new NoteService().findNoteCountByDate(user.getUserId());
        request.getSession().setAttribute("dateInfo",dateInfo);

        //首页左侧类型分组查询
        List<NoteVo> typeInfo = new NoteService().findNoteCountByType(user.getUserId());
        request.getSession().setAttribute("typeInfo",typeInfo);

    }
}
