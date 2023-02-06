package com.lls.note.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class JsonUtil {

    public static void toJson(HttpServletResponse response,Object result){
        try {
            response.setContentType("application/json;charset=UTF-8");
            String json = JSON.toJSONString(result);
            PrintWriter out = response.getWriter();
            out.write(json);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
