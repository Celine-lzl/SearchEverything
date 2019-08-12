package com.lzl.everything.core.dao;

import com.lzl.everything.core.model.Condition;
import com.lzl.everything.core.model.Thing;

import java.util.List;

/**
 * 数据库访问的对象
 */
public interface FileIndexDao {
    
    //File -> Thing -> Database Table Record
    //CRUD -> C / R U  D
    
    /**
     * 插入
     */
    void insert(Thing thing);
    
    /**
     * 删除
     */
    void delete(Thing thing);
    
    /**
     * 查询
     */
    List<Thing> query(Condition condition);
    
    
}
