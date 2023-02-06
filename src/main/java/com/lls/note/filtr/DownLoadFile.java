package com.lls.note.filtr;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
   文件下载后端实现方式
      前端表单设置一个text类型的输入框，后台可得到要下载的文件名
       1、设置请求编码格式
       2、获取从前台页面请求要下载的文件名
       3、文件名非空判断，trim()去除输入内容的前后空字符串，判断去除空字符串后是否库空
       4、得到要下载资源的路径
       5、根据路径和文件名得到要下载的文件
       6、文件非空判断，并且是标准的文件（不是文件夹等）
       7、设置响应类型(设置为浏览器无法识别的类型)
       8、设置头信息（弹出框和文件名，页面会弹出下载状态）
       9、获取文件的输入、输出流
       10、设置一个数组（byte类型的）定义一个常量
       11、将数组输出(循环输出，流里面没有内容时会返回-1)
       12、关流（先打开的后关闭）
 */
@WebServlet("/downLoadFile")
public class DownLoadFile extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        /*如果有给浏览器返回中文信息，会有乱码，要处理乱码(响应类型和编码格式)*/
        response.setContentType("text/html;charset=UTF-8");
        String fileName = request.getParameter("fileName");
        if (fileName == null || "".equals(fileName.trim())){
            response.getWriter().write("请输入要下载的文件名！");
            response.getWriter().close();
            return;
        }
        String realPath = request.getServletContext().getRealPath("/download/");/*download:资源存放的路径*/
        File file = new File(realPath + fileName);/*如果上面的download后面没加/，则realPath + fileName中间要加上/  */
        if (file.exists() && file.isFile()){
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition","attachment;fileName" + fileName);
            InputStream in = new FileInputStream(file);
            ServletOutputStream os = response.getOutputStream();
            byte[] car = new byte[1024];
            int length = 0;
            while ((length = in.read(car))!= -1){
                os.write(car,0,length);
            }
            os.close();
            in.close();
        }else {
            System.out.println("文件不存在");
            response.getWriter().write("文件不存在,请重试");
            response.getWriter().close();
        }

    }
}
