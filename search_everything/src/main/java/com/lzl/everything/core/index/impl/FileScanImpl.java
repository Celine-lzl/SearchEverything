package com.lzl.everything.core.index.impl;

import com.lzl.everything.config.EverythingConfig;
import com.lzl.everything.core.index.FileScan;
import com.lzl.everything.core.interceptor.FileInterceptor;

import java.io.File;
import java.util.LinkedList;
import java.util.Set;

public class FileScanImpl implements FileScan {
//    public int count = 0;
    
    private final LinkedList<FileInterceptor> interceptors = new LinkedList<>();
    
    private EverythingConfig config = EverythingConfig.getInstance();
    
    @Override
    public void index(String path) {
        Set<String> excludePaths = config.getHandlerPath().getExcludePath();
        // C:\Windows
        // C:\Windows  C:\Windows\system32
        // 判断 A path 是否在 B path中
        for (String excludePath : excludePaths) {
            if (path.startsWith(excludePath)) {
                return;
            }
        }
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
//                    count++;
                    index(f.getAbsolutePath());
                }
            }
        }
        for (FileInterceptor interceptor : this.interceptors) {
            interceptor.apply(file);
        }
//        System.out.println("文件总数"+count);
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }
}

