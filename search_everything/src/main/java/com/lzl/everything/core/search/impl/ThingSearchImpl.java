package com.lzl.everything.core.search.impl;

import com.lzl.everything.core.dao.FileIndexDao;
import com.lzl.everything.core.interceptor.impl.ThingClearInterceptor;
import com.lzl.everything.core.model.Condition;
import com.lzl.everything.core.model.Thing;
import com.lzl.everything.core.search.ThingSearch;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThingSearchImpl implements ThingSearch {
    
    private final FileIndexDao fileIndexDao;
    private final ThingClearInterceptor interceptor;
    private final Queue<Thing> thingQueue = new ArrayBlockingQueue<>(1024);
    public ThingSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
        this.interceptor = new ThingClearInterceptor(this.fileIndexDao, thingQueue);
        this.backgroundClearThread();
    }
    
    @Override
    public List<Thing> search(Condition condition) {
        //BUG:
        //如果本地文件系统将文件删除，数据库中仍然存储到索引信息
        //此时如果查询结果存在已经在文件系统中删除的文件，那么需要在数据库中清除掉该文件的索引信息
        // 后面引入文件监控系统

        System.out.println(Test1.printTime());

        List<Thing> things = this.fileIndexDao.query(condition);
        Iterator<Thing> iterator = things.iterator();
        while (iterator.hasNext()) {
            Thing thing = iterator.next();
            File file = new File(thing.getPath());
            if (!file.exists()) {
                //删除
                iterator.remove();
                this.thingQueue.add(thing);
            }
        }

        System.out.println(Test1.printTime());
        return things;
    }
    
    private void backgroundClearThread() {
        //进行后台清理工作
        Thread thread = new Thread(this.interceptor);
        thread.setName("Thread-Clear");
        thread.setDaemon(true);
        thread.start();
    }
}

class Test1{
    public static String printTime(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //规定日期格式
        String str = dateFormat.format(date); // 把当前时间变为字符串格式
        return str;
    }
}