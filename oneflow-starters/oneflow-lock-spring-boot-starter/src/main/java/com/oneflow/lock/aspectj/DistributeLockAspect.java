package com.oneflow.lock.aspectj;

import com.alibaba.fastjson.JSON;
import com.oneflow.lock.annotation.DistributeLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解切片
 */
@Slf4j
@Order
@Aspect
@Component
public class DistributeLockAspect {

    /**
     * 最小加锁等待时间
     */
    private static final long MIN_WAIT_TIME = 1000L;

    /**
     * 异常后锁的持续时间最长半个小时
     */
    private static final long MAX_LEASE_TIME = 30 * 60 * 1000L;

    /**
     * 异常后锁的持续时间最小1s
     */
    private static final long MIN_LEASE_TIME = 1000L;

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ApplicationContext applicationContext;

    @Around("@annotation(distributedLock)")
    public Object execute(ProceedingJoinPoint joinPoint, DistributeLock distributedLock) throws Throwable {
        RLock lock = null;
        boolean isProceedException = false;
        boolean isLockFail = false;
        String lockKey = null;
        try {
            Object[] params = joinPoint.getArgs();
            lockKey = parseKey(params, distributedLock);

            lock = redissonClient.getLock(lockKey);
            long waitTime = Math.max(distributedLock.waitTime(), MIN_WAIT_TIME);
            long leaseTime = Math.min(distributedLock.leaseTime(), MAX_LEASE_TIME);
            leaseTime = Math.max(leaseTime, MIN_LEASE_TIME);
            boolean isLock = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            if (isLock) {
                //执行
                isProceedException = true;
                return joinPoint.proceed();
            } else {
                isLockFail = true;
                throw new RuntimeException("get distribute lock fail");
            }
        } catch (Throwable e) {
            if (isProceedException) {
                throw e;
            } else if (isLockFail) {
                log.error("distribute lock fail , lockKey：" + lockKey);
            } else {
                log.error("distribute lock error", e);
            }
            throw new RuntimeException("获取分布式锁失败！，请稍后重试！");
        } finally {
            if (lock != null) {
                try {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                } catch (Throwable e) {
                    log.error("distribute unlock error:" + e.getMessage());
                }
            }
        }
    }

    /**
     * 解析key
     *
     * @param params  参数
     * @param disLock 注解
     * @return key
     */
    private String parseKey(Object[] params, DistributeLock disLock) {
        String lockName = disLock.lockName();
        String expression = disLock.expression();
        String lockKeyGeneratorName = disLock.lockKeyGenerator();
        if (StringUtils.hasLength(lockName)) {
            throw new RuntimeException("lockName is empty ");
        }
        try {
            String keyValue = "";
            if (StringUtils.hasText(expression)) {
                keyValue = parseKeyByExpression(params, disLock.expression());
            } else if (StringUtils.hasText(lockKeyGeneratorName)) {
                DistributeLockKeyGenerator distributeLockKeyGenerator = (DistributeLockKeyGenerator) applicationContext.getBean(lockKeyGeneratorName);
                keyValue = distributeLockKeyGenerator.generate(params);
            }
            return "lock:" + lockName + ":" + keyValue;
        } catch (Exception e) {
            log.error("distribute lock parse key error", e);
            throw new RuntimeException("distribute lock parse key error");
        }
    }

    /**
     * 解析key表达式
     *
     * @param params     参数
     * @param expression 表达式
     * @return key
     */
    private String parseKeyByExpression(Object[] params, String expression) {
        String keyValueStr = "";
        // 表达式解析器
        ExpressionParser parser = new SpelExpressionParser();
        // 表达式参数
        EvaluationContext context = new StandardEvaluationContext();
        int i = 0;
        for (Object param : params) {
            context.setVariable("p" + i, param);
            i++;
        }
        // 设置表达式
        Expression exp = parser.parseExpression(expression);
        Object keyValue = exp.getValue(context);
        if (keyValue instanceof Character
                || keyValue instanceof Number) {
            keyValueStr = keyValue.toString();
        } else {
            keyValueStr = JSON.toJSONString(keyValueStr);
        }
        return keyValueStr.replaceAll(":", "@");
    }

}
