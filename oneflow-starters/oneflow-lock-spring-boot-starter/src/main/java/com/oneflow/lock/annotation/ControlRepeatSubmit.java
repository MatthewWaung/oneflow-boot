package com.oneflow.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防止重复提交注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ControlRepeatSubmit {

    /**
     * 设置重复请求提交锁定时间，单位秒
     * @return
     */
    int lockTime() default 3;
}
