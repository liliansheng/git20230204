package com.lls.note.service;

import cn.hutool.core.util.StrUtil;
import com.lls.note.dao.NoteDao;
import com.lls.note.po.Note;
import com.lls.note.util.Page;
import com.lls.note.vo.NoteVo;
import com.lls.note.vo.ResultInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteService {
    NoteDao noteDao = new NoteDao();

    //添加或者修改云记
    public ResultInfo<Note> addUpdate(String typeId, String title, String content,String noteId) {
        ResultInfo<Note> resultInfo = new ResultInfo<>();

        //参数非空判断，参数可能为空，所以回显数据要放在后面写
        if (StrUtil.isBlank(typeId)){
            resultInfo.setCode(0);
            resultInfo.setMsg("类型不能为空");
        }
        if (StrUtil.isBlank(title)){
            resultInfo.setCode(0);
            resultInfo.setMsg("标题不能为空");
        }
        if (StrUtil.isBlank(content)){
            resultInfo.setCode(0);
            resultInfo.setMsg("内容不能为空");
        }
        //设置回显数据
        Note note = new Note();
        note.setContent(content);
        note.setTypeId(Integer.parseInt(typeId));
        note.setTitle(title);

        //判断云记Id是否为空
        if (!StrUtil.isBlank(noteId)){
            note.setNoteId(Integer.parseInt(noteId));
        }
        resultInfo.setResult(note);

        int row = noteDao.addUpdate(note);
        if (row > 0){
            resultInfo.setCode(1);
            resultInfo.setResult(note);
        }else {
            resultInfo.setCode(0);
            resultInfo.setResult(note);
        }
        return resultInfo;
    }

    //分页面查询云记列表(参数加Str用于区分)
    public Page<Note> findNoteListByPage(String pageNumStr, String pageSizeStr, Integer userId,String title,String date,String typeId) {
        //如果参数为空，则设置起始页和每页数量默认值
         Integer pageNum = 1;
         Integer pageSize = 10;
         if (!(StrUtil.isBlank(pageNumStr))){
             pageNum = Integer.parseInt(pageNumStr);
         }
        if (!StrUtil.isBlank(pageSizeStr)){
            pageSize = Integer.parseInt(pageSizeStr);
        }
        //查询当前登录用户云记总数量
         long count = noteDao.findNoteCount(userId,title,date,typeId);

         if (count < 1){
             return null;
         }

         //page构造得到其他属性值
         Page<Note> page = new Page<>(pageNum,pageSize,count);

         /**分页查询*/
        //得到数据库中开始分页查询的下标
        Integer index = (pageNum-1)*pageSize;
        List<Note> noteList = noteDao.findNoteListByPage(userId,index,pageSize,title,date,typeId);
        page.setDataList(noteList);

         return page;
    }

    //查询首页左侧日期分组列表
    public List<NoteVo> findNoteCountByDate(Integer userId) {
       return noteDao.findNoteCountByDate(userId);

    }
    //查询首页左侧类型分组列表
    public List<NoteVo> findNoteCountByType(Integer userId) {

        return noteDao.findNoteCountByType(userId);
    }

    //查询云记详情
    public Note findNoteDetailByNoteId(String noteId) {
        if (StrUtil.isBlank(noteId)){
            return null;
        }
        Note note = noteDao.findNoteDetailByNoteId(noteId);
        return note;
    }

    //删除云记
    public Integer deleteNote(String noteId) {
        if (StrUtil.isBlank(noteId)){
            return 0;
        }
        Integer row = noteDao.deleteNote(noteId);
        if (row > 0){
            return 1;
        }else {
            return 0;
        }
    }

    //通过月份分组查询月份及对应的数量
    public ResultInfo<Map<String, Object>> queryNoteCountByMonth(Integer userId) {
        ResultInfo<Map<String,Object>> resultInfo = new ResultInfo<>();

        List<NoteVo> noteVos = noteDao.findNoteCountByDate(userId);
        if (noteVos != null && noteVos.size() > 0){
            List<String> monthList = new ArrayList<>();
            List<Integer> noteCountList = new ArrayList<>();
            for (NoteVo noteVo : noteVos) {
                monthList.add(noteVo.getGroupName());
                noteCountList.add((int) noteVo.getNoteCount());

            }
            Map<String,Object> map = new HashMap<>();
            map.put("monthArray",monthList);
            map.put("dataArray",noteCountList);

            resultInfo.setCode(1);
            resultInfo.setResult(map);
        }
        return resultInfo;
    }
}
