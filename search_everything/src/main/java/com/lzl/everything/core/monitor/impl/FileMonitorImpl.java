package com.lzl.everything.core.monitor.impl;

import com.lzl.everything.config.EverythingConfig;
import com.lzl.everything.config.HandlerPath;
import com.lzl.everything.core.common.FileConvertThing;
import com.lzl.everything.core.dao.FileIndexDao;
import com.lzl.everything.core.monitor.FileMonitor;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.Set;

public class FileMonitorImpl extends FileAlterationListenerAdaptor implements FileMonitor {

    private FileAlterationMonitor monitor;

    private final FileIndexDao fileIndexDao;

    public FileMonitorImpl(FileIndexDao fileIndexDao) {
        this.monitor = new FileAlterationMonitor(
                EverythingConfig.getInstance().getInterval()
        );
        this.fileIndexDao = fileIndexDao;
    }


    @Override
    public void start() {
        //启动
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void monitor(HandlerPath handlerPath) {
        //监控的目录
        Set<String> includePath = handlerPath.getIncludePath();
        for (String path : includePath) {
            FileAlterationObserver observer =
                    new FileAlterationObserver(new File(path), pathname -> {
                        for (String exclude : handlerPath.getExcludePath()) {
                            if (pathname.getAbsolutePath().startsWith(exclude)) {
                                return false;
                            }
                        }
                        return true;
                    });
            // addListener：消息通知给观察者后，观察者要做的事情（如创建、删除文件）
            observer.addListener(this);
            this.monitor.addObserver(observer);
        }
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("onDirectoryCreate :" + directory.getAbsolutePath());
        this.fileIndexDao.insert(FileConvertThing.convert(directory));
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("onDirectoryCreate :" + directory.getAbsolutePath());
        this.fileIndexDao.insert(FileConvertThing.convert(directory));
    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("onFileCreate :" + file.getAbsolutePath());
        this.fileIndexDao.insert(FileConvertThing.convert(file));
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("onFileDelete :" + file.getAbsolutePath());
        this.fileIndexDao.insert(FileConvertThing.convert(file));
    }

    @Override
    public void stop() {
        //停止
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
