package com.lls.note.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 返回结果封装类
 */
@Setter
@Getter
public class ResultInfo<T> {
    private Integer code;
    private String msg;
    private T result;
}
