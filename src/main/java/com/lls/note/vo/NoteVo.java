package com.lls.note.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用于首页左侧列表日期分组,类型分组
 */
@Setter
@Getter
public class NoteVo{
    private String groupName;
    private long noteCount;

    //用于做条件查询
    private Integer typeId;

}
