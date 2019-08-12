package com.lzl.everything.core.search;

import com.lzl.everything.core.model.Condition;
import com.lzl.everything.core.model.Thing;
import java.util.List;

/**
 * 文件检索业务
 */
public interface ThingSearch {
    
    /**
     * 根据condition条件检索数据
     */
    List<Thing> search(Condition condition);
    
}
