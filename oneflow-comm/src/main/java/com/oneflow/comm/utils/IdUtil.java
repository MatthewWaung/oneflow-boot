package com.oneflow.comm.utils;

import java.util.UUID;

public class IdUtil {

    private static final SnowflakeIdGenerator snowflakeIdGenerator = SnowflakeIdGenerator.getInstance();

    /**
     * 生成 UUID
     *
     * @return UUID 字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成 UUID，去掉了横线
     *
     * @return UUID 字符串，去掉了横线
     */
    public static String generateUUIDWithoutDash() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成 Snowflake ID
     *
     * @return Snowflake ID 数字字符串
     */
    public static String generateSnowflakeId() {
        return String.valueOf(snowflakeIdGenerator.nextId());
    }

}
