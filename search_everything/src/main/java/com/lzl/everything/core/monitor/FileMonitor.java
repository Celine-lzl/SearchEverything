package com.lzl.everything.core.monitor;

import com.lzl.everything.config.HandlerPath;

/**
 * 文件监控接口
 */
public interface FileMonitor {

    void start();

    /**
     * 监控
     */
    void monitor(HandlerPath handlerPath);


    void stop();

}
