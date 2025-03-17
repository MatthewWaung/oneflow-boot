package com.oneflow.core.boot.utils;

import com.oneflow.comm.utils.SpringUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * ThreadUtils 是一个线程管理工具类，提供了便捷的线程池创建、任务提交和线程中断等功能。
 */
public class ThreadUtils {

    // 默认核心线程数和最大线程数
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;

    // 线程池用于执行异步任务
    private static final ThreadPoolTaskExecutor executor;

    static {
        executor = SpringUtil.getBean("oneflowTaskExecutor", ThreadPoolTaskExecutor.class);
    }

    /**
     * 获取默认的线程池实例
     *
     * @return 线程池实例
     */
    public static ThreadPoolTaskExecutor instance() {
        return executor;
    }

    /**
     * 获取另一个自定义的线程池
     *
     * @return 线程池实例
     */
    public static ThreadPoolTaskExecutor taskExecutor() {
        return SpringUtil.getBean("oneflowThreadPoolTaskExecutor", ThreadPoolTaskExecutor.class);
    }
    
    /**
     * 提交任务到默认的线程池执行
     *
     * @param task 需要执行的任务
     */
    public static void execute(Runnable task) {
        executor.execute(task);
    }

    /**
     * 提交有返回值的任务到默认的线程池执行
     *
     * @param task Callable 类型的任务
     * @param <T>  返回值类型
     * @return Future对象，表示异步任务的结果
     */
    public static <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    /**
     * 安全中断线程
     *
     * @param thread 要中断的线程
     */
    public static void interrupt(Thread thread) {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
    }

    /**
     * 检查线程是否已经中断
     *
     * @param thread 要检查的线程
     * @return 如果线程已中断则返回 true，否则返回 false
     */
    public static boolean isInterrupted(Thread thread) {
        return thread != null && thread.isInterrupted();
    }

    /**
     * 获取当前线程名称
     *
     * @return 当前线程的名称
     */
    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    /**
     * 暂停当前线程
     *
     * @param millis 暂停时间，单位为毫秒
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取活动线程数
     *
     * @return 活动线程数
     */
    public static int getActiveThreadCount() {
        return Thread.activeCount();
    }

    /**
     * 打印当前线程堆栈信息
     */
    public static void printStackTrace() {
        Thread.currentThread().dumpStack();
    }

    /**
     * 等待所有线程完成
     *
     * @param threads 线程数组
     */
    public static void waitForCompletion(Thread[] threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
