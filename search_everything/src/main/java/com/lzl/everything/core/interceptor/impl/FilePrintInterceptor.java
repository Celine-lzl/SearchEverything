package com.lzl.everything.core.interceptor.impl;

import com.lzl.everything.core.interceptor.FileInterceptor;

import java.io.File;
// 打印拦截到的地址
public class FilePrintInterceptor implements FileInterceptor {
    @Override
    public void apply(File file) {
        System.out.println(file.getAbsolutePath());
    }
}
