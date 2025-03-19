package com.oneflow.lock.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DistributeLock {

    /**
     * 加锁key前缀
     *
     * @return key前缀
     */
    String lockName();

    /**
     * 加锁key表达式，可参考{@link org.springframework.cache.annotation.Cacheable key}
     * p0代表第一个参数 p1代表第二个参数 以此类推
     * expression="p0" 那么加锁key = lockName-p0参数值  如果没有则为null
     * p0.id 代表第一个参数得id
     * 例：
     * @DistributeLock(expression="p0.id") void needLock1(User user);
     * @DistributeLock(expression="p0") void needLock2(User user);
     *
     * @return Spring Expression Language (SpEL) expression for computing the key dynamically.
     */
    String expression() default "";

    /**
     * 锁最大超时时间，单位ms，默认1s
     *
     * @return 超时时间
     */
    long waitTime() default 1000L;

    /**
     * Redis key过期时间，超2/3时间会续期，单位ms，默认30s
     *
     * @return 过期时间
     */
    long leaseTime() default 30 * 1000L;

    /**
     * 自定义分布式锁key解析器
     * 需要继承{@link DistributeLockKeyGenerator}
     * 并且需要注入到Spring中
     *
     * @return 返回bean名
     */
    String lockKeyGenerator() default "";

}
