package com.lls.note.util;

import com.lls.note.dao.NoteDao;
import com.lls.note.po.Note;
import com.lls.note.vo.NoteVo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class TestDB {
    private Logger logger = LoggerFactory.getLogger(TestDB.class);
    @Test
    public void test(){
        System.out.println(DBUtil.getConnection());
        logger.info("获取数据库连接:" + DBUtil.getConnection());
        //logger.info("获取数据库连接,{}括号表示占位符，括号在哪里，后面的参数就显示在占位符里" , DBUtil.getConnection());

    }

    @Test
    public void test1(){
        NoteDao noteDao = new NoteDao();
        //List<Note> noteListByPage = noteDao.findNoteListByPage(1, 1, 3,null);
       // noteListByPage.forEach(System.out::println);
    }

    @Test
    public void test2(){
        NoteDao noteDao = new NoteDao();
        List<NoteVo> noteCountByDate = noteDao.findNoteCountByDate(1);
        System.out.println("======");
        noteCountByDate.forEach(System.out::println);
    }

}
