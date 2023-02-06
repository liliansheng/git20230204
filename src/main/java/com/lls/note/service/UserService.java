package com.lls.note.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.lls.note.dao.BaseDao;
import com.lls.note.dao.UserDao;
import com.lls.note.po.User;
import com.lls.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;

public class UserService {
    private UserDao userDao = new UserDao();


    /**
     * 用户登录
     * @param userName
     * @param userPwd
     * @return
     */
    public ResultInfo<User> userLogin(String userName,String userPwd){
        ResultInfo<User> resultInfo = new ResultInfo<>();
        User u = new User();
        u.setUpwd(userPwd);
        u.setUname(userName);
        resultInfo.setResult(u);

        if (StrUtil.isBlank(userName) || StrUtil.isBlank(userPwd)){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户名或密码不能为空");
            return resultInfo;
        }
         User user = userDao.queryUserByName(userName);
        if (user == null){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户对象不存在，请重新输入");
            return resultInfo;
        }
        userPwd = DigestUtil.md5Hex(userPwd);
        if (!userPwd.equals(user.getUpwd())){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户密码不不正确");
            return resultInfo;
        }
        resultInfo.setCode(1);
        resultInfo.setMsg("登录成功");
        resultInfo.setResult(user);
        return resultInfo;
    }


    /**
     * 验证昵称唯一性
     * @param nick
     * @param userId
     * @return
     */
    public Integer checkNick(String nick, Integer userId) {
        if (StrUtil.isBlank(nick)){
            return 0;
        }
        User user = userDao.queryUserByNickAndUserId(nick,userId);
        if (user != null){
            return 0;
        }else {
            return 1;
        }

    }

    /**
     * 更改用户信息
     * @param request
     * @param response
     * @return
     */
    public ResultInfo<User> updateUser(HttpServletRequest request, HttpServletResponse response) {
        ResultInfo<User> resultInfo = new ResultInfo<>();
        String nick = request.getParameter("nick");
        String mood = request.getParameter("mood");

        //如果上传的为空，则将原来的数据设为默认值
        if (StrUtil.isBlank(nick)){
            resultInfo.setCode(0);
            resultInfo.setMsg("昵称不能为空");
            return resultInfo;
        }
        //从session中拿到原来的值
        User user = (User) request.getSession().getAttribute("user");
        user.setNick(nick);
        user.setMood(mood);

        //实现文件上传(从请求头中拿到上传的文件名,请求头是键值对形式，也就是从请求头的value中获取上传的文件名)
        try {
            Part part = request.getPart("img");
            String header = part.getHeader("Content-Disposition");
            String str = header.substring(header.lastIndexOf("=") + 2);
            String fileName = str.substring(0, str.length() - 1);
            if (!fileName.isEmpty()){
                //更新头像
                user.setHead(fileName);
                //获取路径（文件（头像）上传后，存放的路径）
                String filePath = request.getServletContext().getRealPath("/WEB-INF/upload/");
                //将上传的文件存入到指定路径下的文件中
                part.write(filePath + "/" + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        int row = userDao.updateUser(user);
        if (row > 0){
            resultInfo.setCode(1);
            //更新后也要把session中的数据更改,
            request.getSession().setAttribute("user",user);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("更新失败");
        }

        return resultInfo;
    }
}
