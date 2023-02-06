package com.lls.note.po;

import lombok.Getter;
import lombok.Setter;

/**
 * 使用lombok插件，引入相关依赖 （下载插件）
 */
@Getter
@Setter
public class User {
    private Integer userId;
    private String uname;
    private String upwd;
    private String nick;
    private String head;
    private String mood;

}
