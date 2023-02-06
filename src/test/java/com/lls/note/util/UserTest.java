package com.lls.note.util;

import com.lls.note.dao.BaseDao;
import com.lls.note.dao.UserDao;
import com.lls.note.po.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserTest {
    private UserDao userDao = new UserDao();
    @Test
    public void testUser(){
        User user = userDao.queryUserByName("admin");
        System.out.println(user.getUpwd());
    }

    @Test
    public void Update(){
        String sql = "insert into tb_user (uname,upwd,nick,head,mood) values(?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add("aaa");
        params.add("e10adc3949ba59abbe56e057f20f883e");
        params.add("bbb");
        params.add("404.jpg");
        params.add("hello");
        int row = BaseDao.executeUpdate(sql,params);
        System.out.println(row);
    }

}
