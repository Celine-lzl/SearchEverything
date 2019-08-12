package com.lzl.everything.core.interceptor;

import com.lzl.everything.core.model.Thing;

/**
 * 检索结果thing的拦截器
 */
public interface ThingInterceptor {
    void apply(Thing thing);
}
