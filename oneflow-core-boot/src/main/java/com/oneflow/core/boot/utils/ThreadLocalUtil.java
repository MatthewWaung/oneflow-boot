package com.oneflow.core.boot.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程缓存工具类，如果需要和子线程关联，参考ContextHolder
 */
public class ThreadLocalUtil {
    private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    /**
     * 获取或初始化 ThreadLocal 中的 Map
     * @return Map<String, Object>
     */
    private static Map<String, Object> getOrInitMap() {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }
        return map;
    }

    /**
     * 向 ThreadLocal 缓存值
     *
     * @param key   要缓存的 KEY
     * @param value 要缓存的 VALUE
     */
    public static void set(String key, Object value) {
        getOrInitMap().put(key, value);
    }

    /**
     * 从 ThreadLocal 里获取缓存的值
     *
     * @param key 要获取的数据的 KEY
     * @param <T> 返回值的类型
     * @return 要获取的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(String key) {
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            return null;
        }
        return (T) map.get(key);
    }

    /**
     * 根据 KEY 移除缓存里的数据
     *
     * @param key
     */
    public static void removeByKey(String key) {
        Map<String, Object> map = threadLocal.get();
        if (map != null) {
            map.remove(key);
        }
    }

    /**
     * 移除当前线程缓存
     * 用于释放当前线程 threadLocal 资源
     */
    public static void remove() {
        threadLocal.remove();
    }
}