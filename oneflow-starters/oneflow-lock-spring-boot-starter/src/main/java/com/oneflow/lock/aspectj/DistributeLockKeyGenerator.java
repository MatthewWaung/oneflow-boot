package com.oneflow.lock.aspectj;

/**
 * 自定义分布式锁key生成器
 */
public interface DistributeLockKeyGenerator {

    /**
     * 根据参数生成分布式锁的key
     * @param params 参数
     * @return
     */
    String generate(Object[] params);

}
