package com.lls.note.dao;

import com.lls.note.po.User;
import com.lls.note.util.DBUtil;
import com.mysql.cj.jdbc.PreparedStatementWrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    //用BaseDao中的方法查询User，登录操作
    public User queryUserByName(String userName){
        String sql = "select * from tb_user where uname = ?";
        List<Object> params = new ArrayList<>();
        params.add(userName);
        User user = (User) BaseDao.queryRow(sql, params,User.class);
        return user;
    }
    //与上相同，没用BaseDao方法
    public User queryUserByName02(String userName){
         User user = new User();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "select * from tb_user where uname = ?";
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            preparedStatement.setString(1,userName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                user.setUserId(resultSet.getInt("userId"));
                user.setUname(userName);
                user.setUpwd(resultSet.getString("uPwd"));
                user.setNick(resultSet.getString("nick"));
                user.setHead(resultSet.getString("head"));
                user.setMood(resultSet.getString("mood"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.close(resultSet,preparedStatement,connection);
        }

        return user;
    }

    /**
     * 验证昵称唯一性
     * @param nick
     * @param userId
     * @return
     */
    public User queryUserByNickAndUserId(String nick, Integer userId) {
        String sql = "select * from tb_user where nick = ? and userId != ?";
        List<Object> params = new ArrayList<>();
        params.add(nick);
        params.add(userId);
        User user = (User) BaseDao.queryRow(sql, params,User.class);
        return user;
    }

    /** 更改用户信息 （个人中心数据）*/
    public int updateUser(User user) {
        String sql = "update tb_user set nick = ? ,mood = ? ,head = ? where userId = ?";
        List<Object> params = new ArrayList<>();
        params.add(user.getNick());
        params.add(user.getMood());
        params.add(user.getHead());
        params.add(user.getUserId());
        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }
}
