package com.oneflow.comm.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnowflakeIdGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SnowflakeIdGenerator.class);

    // 起始的时间戳
    private static final long START_TIMESTAMP = 1609459200000L; // 2021-01-01 00:00:00 GMT

    // 每一部分占用的位数
    private static final long SEQUENCE_BITS = 12; // 序列号占用的位数
    private static final long WORKER_ID_BITS = 5; // 工作节点ID占用的位数
    private static final long DATA_CENTER_ID_BITS = 5; // 数据中心ID占用的位数

    // 每一部分的最大值
    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
    private static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS);
    private static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BITS);

    // 每一部分向左的位移
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    private final long workerId; // 工作节点ID
    private final long dataCenterId; // 数据中心ID
    private long sequence = 0L; // 序列号
    private long lastTimestamp = -1L; // 上一次生成ID的时间戳

    /**
     * 构造函数
     *
     * @param workerId     工作节点ID (0~31)
     * @param dataCenterId 数据中心ID (0~31)
     */
    public SnowflakeIdGenerator(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 生成下一个ID
     *
     * @return 生成的ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        // 上次生成ID的时间戳
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIFT) //
                | (dataCenterId << DATA_CENTER_ID_SHIFT) //
                | (workerId << WORKER_ID_SHIFT) //
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间戳
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    // 单例模式，确保只有一个实例
    private static class SingletonHolder {
        private static final SnowflakeIdGenerator INSTANCE = new SnowflakeIdGenerator(0, 0);
    }

    public static SnowflakeIdGenerator getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static void main(String[] args) {
        SnowflakeIdGenerator idWorker = SnowflakeIdGenerator.getInstance();
        for (int i = 0; i < 30; i++) {
            long id = idWorker.nextId();
            System.out.println(id);
        }
    }
}
