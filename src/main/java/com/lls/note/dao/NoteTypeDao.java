package com.lls.note.dao;

import com.lls.note.po.NoteType;
import com.lls.note.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NoteTypeDao {


    //根据用户id查询日记类型列表
    public List<NoteType> findTypeListByUserId(Integer userId){
        String sql = "select typeId,typeName,userId from tb_note_type where userId = ?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<NoteType> list = BaseDao.queryRows(sql,params,NoteType.class);
        return list;
    }

    /**查询云记录（根据typeId查询该类型下的云日记）*/
    public long findNoteCountByTypeId(String typeId) {

        String sql = "select count(1) from tb_note where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        long count = (long) BaseDao.findSingleValue(sql,params);
        return count;
    }

    /**删除操作*/
    public int deleteTypeByTypeId(String typeId) {
        String sql = "delete from tb_note_type where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }

    /**
     * 查询当前用户下类型名称是否唯一（）
     * @param typeName
     * @param typeId
     * @param userId
     * @return
     */
    public Integer checkTypeName(String typeName, String typeId, Integer userId) {
        String sql = "select * from tb_note_type where typeName = ? and userId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeName);
        params.add(userId);
        NoteType noteType = (NoteType) BaseDao.queryRow(sql,params,NoteType.class);
        if (noteType == null){
            return 1;
        }else {//如果是修改操作，还需要判断是不是当前记录本身
            if (typeId.equals(noteType.getTypeId())){
                return 1;
            }
        }
        return 0;
    }

    /**执行添加类型操作 （返回主键）*/
    public Integer addType(String typeName, Integer userId) {
        Integer key = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();
            String sql = "insert into tb_note_type (typeName,userId) values(?,?)";
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,typeName);
            preparedStatement.setObject(2,userId);
            //返回受影响的行数
            int row = preparedStatement.executeUpdate();
            if (row > 0){
                //获取结果集
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()){
                    //获取主键
                    key = resultSet.getInt(1);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.close(resultSet,preparedStatement,connection);
        }
        return key;
    }

    /**执行修改类型操作 */
    public Integer updateType(String typeId, String typeName) {
        String sql = "update tb_note_type set typeName = ? where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeName);
        params.add(typeId);
        Integer key = BaseDao.executeUpdate(sql,params);
        return key;
    }
}
