package com.oneflow.lock.aspectj;

import com.oneflow.lock.annotation.ControlRepeatSubmit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class RepeatSubmitAspect {

    @Resource
    private RedissonClient redissonClient;

    @Pointcut("@annotation(controlRepeatSubmit)")
    public void pointCut(ControlRepeatSubmit controlRepeatSubmit) {
        // do nothing
    }

    @Around("pointCut(controlRepeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, ControlRepeatSubmit controlRepeatSubmit)  throws Throwable {

        // 获取注解参数
        int lockTime = controlRepeatSubmit.lockTime();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        if (ObjectUtils.isEmpty(request)) {
            throw new RuntimeException("request can not null");
        }

        // 获取请求url路径
        String servletPath = request.getServletPath();

        //参数
        Object[] args = joinPoint.getArgs();
        StringBuilder paramStr = new StringBuilder();
        Arrays.asList(args).stream().forEach(p ->
                paramStr.append(p.toString()).append(",")
        );

        //唯一主键
        String key = servletPath + ": " + paramStr;

        //原来字符串太长，MD5加密
        String md5 = "repeat_submit_valid:" + MD5_SHA(key, "MD5");

        //key MD5加密后作为redis主键 原来的key作为值存起来 方便日后追踪
        boolean isSuccess = tryLock(md5, key, lockTime);
        if (isSuccess) {
            // 获取锁成功
            Object result;
            try {
                result = joinPoint.proceed();
            } finally {
                // 解锁
                releaseLock(md5);
            }

            return result;
        } else {
            // 获取锁失败，认为是重复提交的请求
            throw new RuntimeException("您的请求已经提交，请勿重复请求");
        }
    }

    /**
     * 加锁
     *
     * @param lockKey    加锁键
     * @param value      键值
     * @param expireTime 锁过期时间
     */
    public boolean tryLock(String lockKey, String value, long expireTime) {
        try {
            RBucket<Object> bucket = redissonClient.getBucket(lockKey);
            if (ObjectUtils.isEmpty(bucket.get())) {
                redissonClient.getBucket(lockKey).set(value, expireTime, TimeUnit.SECONDS);
                return true;
            }
        } catch (Exception e) {
            log.error("tryLock error", e);
        }
        return false;
    }

    //解锁
    public boolean releaseLock(String lockKey) {
        try {
            redissonClient.getBucket(lockKey).delete();
            return true;
        } catch (Exception e) {
            log.error("releaseLock error", e);
        }
        return false;
    }

    /**
     * md5加密
     * method:如果输入“SHA”，就是实现SHA加密。
     **/
    public static String MD5_SHA(String s, String method) {
        try {
            byte[] strTemp = s.getBytes();
            char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            MessageDigest mdTemp = MessageDigest.getInstance(method);
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

}
