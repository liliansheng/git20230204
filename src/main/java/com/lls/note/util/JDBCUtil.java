package com.lls.note.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 自己写的DBUtil类
 */
public class JDBCUtil {

    private final static Properties properties = new Properties();

    static {
        try {
            InputStream in = JDBCUtil.class.getClassLoader().getResourceAsStream("db.properties");
            properties.load(in);
            Class.forName(properties.getProperty("jdbcName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        Connection connection = null;
        String dbUrl = properties.getProperty("dbUrl");
        String dbName = properties.getProperty("dbName");
        String dbPwd = properties.getProperty("dbPwd");
        try {
             connection = DriverManager.getConnection(dbUrl, dbName, dbPwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
       return connection;
    }


    public static void close(ResultSet resultSet, PreparedStatement preparedStatement,Connection connection) {

        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
                }
            if (connection != null) {
                 connection.close();
                }
            } catch(SQLException e){
                e.printStackTrace();
            }

        }

}
