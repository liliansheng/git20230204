package com.lls.note.dao;

import cn.hutool.core.util.StrUtil;
import com.lls.note.po.Note;
import com.lls.note.vo.NoteVo;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {

    //添加云记
    public int addUpdate(Note note) {
        String sql = "";
        List<Object> params = new ArrayList<>();
        params.add(note.getTypeId());
        params.add(note.getTitle());
        params.add(note.getContent());

        if (note.getNoteId() == null){
            sql = "insert into tb_note (typeId,title,content,pubTime) values(?,?,?,now())";
        }else {
            sql = "update tb_note set typeId = ? , title = ? , content = ? where noteId = ?";
            params.add(note.getNoteId());
        }

        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }

    /**查询云记总数量，用于分页*/
    public long findNoteCount(Integer userId,String title,String date,String typeId) {
        //因为云记表中没有userId,所以要连表查询
        String sql = "select count(1) from tb_note t inner join tb_note_type b on t.typeId = b.typeId where userId = ?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        //搜索框标题模糊查询 （数量也要根据条件查询，而不是查询所有的，要不然总页数不变，会有空白页）
        if (!StrUtil.isBlank(title)){
            sql += "and title like concat('%',?,'%')";
            params.add(title);
        }else if (!StrUtil.isBlank(date)){//按日期查询，因为传过来的是年月类型的日期，所以要将数据库中的字段格式化一下
            sql += "and date_format(pubTime,'%Y年%m月') = ?";
            params.add(date);
        }else if (!StrUtil.isBlank(typeId)){
            sql += "and t.typeId = ?";
            params.add(typeId);
        }
        long count = (long) BaseDao.findSingleValue(sql,params);
        return count;
    }

    //分页查询
    public List<Note> findNoteListByPage(Integer userId, Integer index, Integer pageSize,String title,String date,String typeId) {
        String sql = "select noteId,title,pubTime from tb_note t inner join tb_note_type b " +
                "on t.typeId = b.typeId where userId = ?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        //搜索框标题模糊查询
        if (!StrUtil.isBlank(title)){
            sql += "and title like concat('%',?,'%')";
            params.add(title);
        }else if (!StrUtil.isBlank(date)){//按日期查询，因为传过来的是年月类型的日期，所以要将数据库中的字段格式化一下
            sql += "and date_format(pubTime,'%Y年%m月')=?";
            params.add(date);
        }else if (!StrUtil.isBlank(typeId)){
            sql += "and t.typeId = ?";
            params.add(typeId);
        }

        //limit语句要放在最后面(做个倒序，这样添加或修改时的数据会在前面显示出来)
        sql += "order by pubTime desc limit ?,?";
        params.add(index);
        params.add(pageSize);



        List<Note> noteList = BaseDao.queryRows(sql,params,Note.class);
        return noteList;
    }

    //查询首页左侧日期分组列表
    public List<NoteVo> findNoteCountByDate(Integer userId) {
        String sql = "SELECT count(1) noteCount,DATE_FORMAT(pubTime,'%Y年%m月') groupName FROM tb_note n " +
                " INNER JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ? " +
                " GROUP BY DATE_FORMAT(pubTime,'%Y年%m月')" +
                " ORDER BY DATE_FORMAT(pubTime,'%Y年%m月') DESC ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<NoteVo> list = BaseDao.queryRows(sql,params,NoteVo.class);
        return list;
    }
    //查询首页左侧 类型分组 列表
    public List<NoteVo> findNoteCountByType(Integer userId) {
        String sql = "SELECT count(noteId) noteCount, t.typeId, typeName groupName FROM tb_note n " +
                " RIGHT JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ? " +
                " GROUP BY t.typeId ORDER BY COUNT(noteId) DESC ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<NoteVo> list = BaseDao.queryRows(sql,params,NoteVo.class);
        return list;
    }

    //查询云记详情
    public Note findNoteDetailByNoteId(String noteId) {
        String sql = "select noteId,title,content,pubTime,typeName,n.typeId from tb_note n " +
                "inner join tb_note_type t on n.typeId = t.typeId where noteId = ?";
        List<Object> params = new ArrayList<>();
        params.add(noteId);
        Note note = (Note) BaseDao.queryRow(sql,params,Note.class);
        return note;
    }

    //删除云记
    public Integer deleteNote(String noteId) {
        String sql = "delete from tb_note where noteId = ?";
        List<Object> params = new ArrayList<>();
        params.add(noteId);
        return BaseDao.executeUpdate(sql,params);
    }
}
