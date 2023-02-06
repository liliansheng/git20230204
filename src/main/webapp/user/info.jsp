<%--
  Created by IntelliJ IDEA.
  User: 86183
  Date: 2022/12/17
  Time: 9:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>

<div class="col-md-9">

<%--  <script>
    function checkUser(){
      var nickName=$('#nick').val();
      if(nickName.length==0){
        $("#msg").html("昵称不能为空,请核对！");
        return false;
      }
      return true;
    }
  </script>--%>
  <div class="data_list">
    <div class="data_list_title"><span class="glyphicon glyphicon-edit"></span>&nbsp;个人中心 </div>
    <div class="container-fluid">
      <div class="row" style="padding-top: 20px;">
        <div class="col-md-8">
          <form class="form-horizontal" method="post" action="user" enctype="multipart/form-data" onsubmit="return checkUser();">
            <div class="form-group">
              <input type="hidden" name="actionName" value="updateUser">
              <label for="nickName" class="col-sm-2 control-label">昵称:</label>
              <div class="col-sm-3">
                <input class="form-control" name="nick" id="nickName" placeholder="昵称" value="${user.nick}">
              </div>
              <label for="img" class="col-sm-2 control-label">头像:</label>
              <div class="col-sm-5">
                <input type="file" id="img" name="img">
              </div>
            </div>
            <div class="form-group">
              <label for="mood" class="col-sm-2 control-label">心情:</label>
              <div class="col-sm-10">
                <textarea class="form-control" name="mood" id="mood" rows="3">${user.mood}</textarea>
              </div>
            </div>
            <div class="form-group">
              <div class="col-sm-offset-2 col-sm-10">
                <%--onclick="return updateUser()"  返回true时才执行--%>
                <button type="submit" id="btn" class="btn btn-success" onclick="return updateUser()">修改</button>&nbsp;&nbsp;<span style="color:red" id="msg"></span>
              </div>
            </div>
          </form>
        </div>
        <div class="col-md-4"><img style="width:260px;height:200px" src="user?actionName=userHead&imageName=${user.head}"></div>
      </div>
    </div>
  </div>
  <script type="text/javascript">
    //昵称唯一性
     $("#nickName").blur(function (){
       var neckName = $("#nickName").val();
       //用isEmpty方法需要引用一个util.js，因为本页面包含在首页里，所以可以统一在首页引用js
       if (isEmpty(neckName)){
         $("#msg").html("昵称不能为空");
         //禁用修改按钮  disabled表示禁用属性
         $("#btn").prop("disabled",true);
         return;
       }
       //如果昵称不为空，判断是否做了修改
       var nick = "${user.nick}";//要与字符串比较所以要加单引号或双引号
       if (nick == $("#nickName")){
          return;
       };
      // 如果做了修改
       $.ajax({
         type:"get",
         url:"user",
         data:{
           actionName:"checkNick",
           nick:neckName,
         },
         success:function (code){
           if (code == 1){
             $("#msg").html("");
             //禁用修改按钮
             $("#btn").prop("disabled",false);
           }else {
             $("#msg").html("该昵称已存在，请重新输入");
             //禁用修改按钮
             $("#btn").prop("disabled",true);
           }
         }
       })

     }).focus(function (){
       $("#msg").html("");
       //禁用修改按钮
       $("#btn").prop("disabled",false);
     })


   //点击修改按钮（个人中心里的数据）
    function updateUser(){
       var nick = $("#nickName").val();
       if (isEmpty(nick)){
         $("#msg").html("昵称不能为空");
         //禁用修改按钮
         $("#btn").prop("disabled",true);
         return false;
       }
       //唯一性 TODO  (标记作用，正常是要有验证唯一性的，但昵称文本框的聚焦、失焦事件)

       return true;
    }



   /* $(function(){

              var target=$("#nickName");
              //验证昵称唯一
              target.blur(
                      function(){
                        $("#btn").attr('disabled',false);
                        $("#msg").html('');
                        var value =target.val();
                        //不用ajax验证，没有填写或者与之前内容相同
                        if(value.length==0 ||value=='我思故我在'){
                          target.val('我思故我在');
                          return ;
                        }

                        //ajax验证
                        $.getJSON("user",{
                          act:'unique',
                          nick:value
                        },function(data){
                          if(data.resultCode==-1){
                            $("#msg").html(value+"此用户名已存在");
                            target.val('');
                            $("#btn").attr('disabled',true);
                          }else{
                            $("#btn").attr('disabled',false);
                          }
                        });
                      }

              );
            });*/
  </script>
</div>

