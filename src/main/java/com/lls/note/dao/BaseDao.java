package com.lls.note.dao;

import com.lls.note.util.DBUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * 公用的增、删、改、查sql语句
 *    增、删、改：可共用一个方法，使用时改变sql语句即可
 *    查询一个字段
 *    查询一个集合（需要得到元数据，再反射为对象，将对象作为返回数据）
 *    查询一个对象（取 查询一个集合 方法中的第一个对象）
 *
 */
public class BaseDao {

    /*修改sql语句,可能有多个参数，用集合接收参数*/
    public static int executeUpdate(String sql , List<Object> params){
        int row = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        //ResultSet resultSet = null;修改没有返回数据，只有返回受影响的行数
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (params != null && params.size() > 0){
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }
            row = preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.close(null,preparedStatement,connection);
        }

        return row;
    }

    /*查询一个字段 （只会返回一条记录且只有一个字段，常用场景：如查询总数量 ）*/
    public static Object findSingleValue(String sql,List<Object> params){
        Object object = null;
        ResultSet resultSet = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        //ResultSet resultSet = null;修改没有返回数据，只有返回受影响的行数
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (params != null && params.size() > 0){
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }
            resultSet = preparedStatement.executeQuery();
            //分析结果集
            if (resultSet.next()){
                //因为不知道查询的是哪个字段，所以拿结果集的第一个就可以了
                object = resultSet.getObject(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.close(resultSet,preparedStatement,connection);
        }

        return object;
    }

    /*查询一个集合 ()JavaBean中的属性与数据库表中的字段要对应，属性可多于字段，但字段要有对应的属性*/
    public static List queryRows(String sql,List<Object> params,Class cls){
        List list = new ArrayList();
        ResultSet resultSet = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        //ResultSet resultSet = null;修改没有返回数据，只有返回受影响的行数
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (params != null && params.size() > 0){
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }
            resultSet = preparedStatement.executeQuery();
            //拿到元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            //得到列数总数量
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()){//注意这里是while循环，不是if语句了
                Object object = cls.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    //metaData.getColumnLabel获取类名或别名
                    //metaData.getColumnName获取类名
                    String columnName = metaData.getColumnLabel(i);
                    //将字段反射为数据库表对应的实体类的属性
                    Field declaredField = cls.getDeclaredField(columnName);
                    //拼接set方法
                    String setMethod = "set" + columnName.substring(0,1).toUpperCase() + columnName.substring(1);
                    // 通过反射，将set方法字符串反射成类中对应的set方法 (两个参数：方法、字段对应的实体类的属性 的类型)
                    Method method = cls.getDeclaredMethod(setMethod, declaredField.getType());
                    Object value = resultSet.getObject(columnName);
                    method.invoke(object,value);
                }
                list.add(object);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBUtil.close(resultSet,preparedStatement,connection);
        }

        return list;
    }

    /*查询一个对象*/
    public static Object queryRow(String sql,List<Object> params,Class cls){
        List list = queryRows(sql,params,cls);
        Object object = null;
        if (list != null && list.size() > 0){
            object = list.get(0);
        }
        return object;
    }

}
