package com.lzl.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class DataSourceFactory {
    
    /**
     * 数据库的数据源
     */
    private static volatile DruidDataSource instance;
    
    private DataSourceFactory() {
    }
    
    public static DataSource getInstance() {
        if (instance == null) {
            synchronized(DataSource.class) {
                if (instance == null) {
                    instance = new DruidDataSource();
                    //url => host,port databaseName
                    //driverClass
                    //username
                    //password
                    //这是连接MySQL的配置
//                    instance.setUrl("jdbc:mysql://127.0.0.1:3307/everything_g2");
//                    instance.setUsername("root");
//                    instance.setPassword("root");
//                    instance.setDriverClassName("com.mysql.jdbc.Driver");
                    
                    //这是连接H2数据库的配置
                    instance.setTestWhileIdle(false);
                    instance.setDriverClassName("org.h2.Driver");
                    String path = System.getProperty("user.dir") + File.separator + "everything_g2";
                    instance.setUrl("jdbc:h2:" + path);
                    
                    //数据库创建完成之后，初始化表结构
                    databaseInit(false);
                }
            }
        }
        return instance;
    }
    
    public static void databaseInit(boolean buildIndex) {
        //classpath:database.sql => String
        StringBuilder sb = new StringBuilder();
        try (InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("database.sql");
        ) {
            if (in != null) {
                try (
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(in)
                        )) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            } else {
                throw new RuntimeException("database.sql script can't load please check it.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String sql = sb.toString();
        
        try (Connection connection = getInstance().getConnection();
        ) {
            if (buildIndex) {
                try (PreparedStatement statement = connection.prepareStatement("drop table if exists thing;");) {
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                
            }
            try (PreparedStatement statement = connection.prepareStatement(sql);) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
