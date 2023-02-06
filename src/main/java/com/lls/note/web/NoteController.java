package com.lls.note.web;

import cn.hutool.core.util.StrUtil;
import com.lls.note.dao.NoteTypeDao;
import com.lls.note.po.Note;
import com.lls.note.po.NoteType;
import com.lls.note.po.User;
import com.lls.note.service.NoteService;
import com.lls.note.service.NoteTypeService;
import com.lls.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/note")
public class NoteController extends HttpServlet {
    private NoteService noteService = new NoteService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("menu_page","note");
        String actionName = request.getParameter("actionName");
        if (actionName.equals("view")){
            noteView(request,response);
        }
        //发布云记
        else if (actionName.equals("addUpdate")){
            addUpdate(request,response);
        }
        //云记详情
        else if (actionName.equals("detail")){
            noteDetail(request,response);
        }
        //删除云记
        else if (actionName.equals("delete")){
            deleteNote(request,response);
        }

    }

    //删除云记
    private void deleteNote(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String noteId = request.getParameter("noteId");
        Integer code = noteService.deleteNote(noteId);
        //响应ajax请求，以字符串形式返回
        response.getWriter().write(code + "");
        response.getWriter().close();
    }

    //查询云记详情
    private void noteDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String noteId = request.getParameter("noteId");
        Note note = noteService.findNoteDetailByNoteId(noteId);
        request.setAttribute("note",note);
        request.setAttribute("changePage","note/detail.jsp");
        request.getRequestDispatcher("index.jsp").forward(request,response);

    }

    /**发布或修改云记*/
    private void addUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String typeId = request.getParameter("typeId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        //修改云记时要通过noteId
        String noteId = request.getParameter("noteId");

        ResultInfo<Note> resultInfo = noteService.addUpdate(typeId,title,content,noteId);
        if (resultInfo.getCode() == 1){
            response.sendRedirect("index");
        }else {
            request.setAttribute("resultInfo",resultInfo);
            String url = "note?actionName=view";
            if (!StrUtil.isBlank(noteId)){
                url += "&noteId=" + noteId;
            }
            request.getRequestDispatcher(url).forward(request,response);
        }

    }

    /**
     * 进入发布云记页面
     *    用户有云记类型才可发布云记，如果没有，要先创建云记类型再发布
     * @param request
     * @param response
     */
    private void noteView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       //修改云记操作(页面回显数据)
        String noteId = request.getParameter("noteId");
        Note note = noteService.findNoteDetailByNoteId(noteId);
        request.setAttribute("noteInfo",note);


        User user = (User) request.getSession().getAttribute("user");
        List<NoteType> typeList = new NoteTypeService().findTypeList(user.getUserId());
        request.setAttribute("typeList",typeList);

        request.setAttribute("changePage","note/view.jsp");
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }
}
