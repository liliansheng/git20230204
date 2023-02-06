package com.lls.note.service;

import cn.hutool.core.util.StrUtil;
import com.lls.note.dao.NoteTypeDao;
import com.lls.note.po.NoteType;
import com.lls.note.vo.ResultInfo;

import java.util.ArrayList;
import java.util.List;

public class NoteTypeService {
    private NoteTypeDao noteTypeDao = new NoteTypeDao();

    /**根据用户id查询日记类型列表*/
    public List<NoteType> findTypeList(Integer userId){
        List<NoteType> typeList = new ArrayList<>();
        typeList = noteTypeDao.findTypeListByUserId(userId);
        return typeList;
    }


    /**删除类型*/
    public ResultInfo<NoteType> deleteType(String typeId) {
        ResultInfo<NoteType> resultInfo = new ResultInfo<>();
        if (StrUtil.isBlank(typeId)){
            resultInfo.setCode(0);
            resultInfo.setMsg("未选中要删除的数据");
            return resultInfo;
        }
        //查询要删除的类型是否有子记录
        long count = noteTypeDao.findNoteCountByTypeId(typeId);
        if (count > 0){
            resultInfo.setCode(0);
            resultInfo.setMsg("该类型有子记录，不可删除");
            return resultInfo;
        }
        //执行删除操作
        int row = noteTypeDao.deleteTypeByTypeId(typeId);
        if (row > 0){
            resultInfo.setCode(1);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("删除失败！");
        }
        return resultInfo;
    }

    /**类型的添加或修改*/
    public ResultInfo<Integer> addOrUpdate(String typeName, String typeId, Integer userId) {
        ResultInfo<Integer> resultInfo = new ResultInfo<>();
        if (StrUtil.isBlank(typeName)){
            resultInfo.setCode(0);
            resultInfo.setMsg("类型名称不能为空");
            return resultInfo;
        }
        Integer key = 0;
        //查询当前用户下有无前台会传过来的类型名称
        Integer code = noteTypeDao.checkTypeName(typeName,typeId,userId);
        if (code == 0){//当前用户已有此类型名称，不可用
            resultInfo.setCode(0);
            resultInfo.setMsg("类型名称已存在，请重新输入");
            return resultInfo;
        }//如果没有查到要更改或要添加的名称，再根据前台传的参数typeId是否为空来判断是执行添加操作不是修改操作

        if (StrUtil.isBlank(typeId)){//添加操作(typeId是主键自增)执行添加操作要返回主键给前台
            key = noteTypeDao.addType(typeName,userId);//key代表主键
        }else {//修改操作
            key = noteTypeDao.updateType(typeId,typeName);//key代表受影响的行数
        }

        if (key > 0){//执行添加或修改成功
            resultInfo.setCode(1);
            resultInfo.setResult(key);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("修改失败");
        }
        return resultInfo;
    }
}
