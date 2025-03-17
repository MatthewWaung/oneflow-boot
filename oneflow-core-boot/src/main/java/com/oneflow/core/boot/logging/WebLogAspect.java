package com.oneflow.core.boot.logging;

import com.google.gson.Gson;
import com.oneflow.core.boot.config.WebLogAspectConfig;
import com.oneflow.core.boot.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 记录日志切面类，将请求路径、参数、操作人员打印在一行日志中，对于排查问题十分方便
 */
@Aspect
@Component
@Order(-5)
@Slf4j
public class WebLogAspect {

    // 线程变量，记录开始时间
    static ThreadLocal<Long> startTime = new ThreadLocal<>();

    // 异常发送器
//    @Autowired
//    private ISendException senderException;

    @Pointcut("execution(* com.oneflow.*.controller.*.*(..))")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 如果配置了不记录日志，则不记录日志
        if (!WebLogAspectConfig.isEnabled()) {
            return;
        }

        try {
            requestStart(joinPoint);
        } catch (Exception e) {
            log.error("请求开始异常", e);
//            senderException.send("请求开始异常" + e);
        }
    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfter(Object ret) {
        // 如果配置了不记录日志，则不记录日志
        if (!WebLogAspectConfig.isEnabled()) {
            return;
        }

        try {
            requestEnd(ret);
        } catch (Exception e) {
            log.error("请求结束异常", e);
//            senderException.send("请求结束异常" + e);
        }
    }

    @AfterThrowing(pointcut = "webLog()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        // 如果配置了不记录日志，则不记录日志
        if (!WebLogAspectConfig.isEnabled()) {
            return;
        }

        try {
            HttpServletRequest request = getHttpServletRequest();
            SysUser loginUser = getLoginUser(request);
            String userName = loginUser == null ? "" : loginUser.getUserName() + "[" + loginUser.getNickName() + "]";
            long time = new Date().getTime() - startTime.get();
            log.info("请求处理异常：接口地址={},用户={},耗时:{}ms ,接口返回异常：{}", request.getRequestURI(), userName, time, e.getMessage());
        } catch (Exception ee) {
            log.error("请求结束异常", ee);
//            senderException.send("请求结束异常" + ee);
        }
    }

    /**
     * 请求开始的处理
     * @param joinPoint
     */
    private void requestStart(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        HttpServletRequest request = getHttpServletRequest();
        SysUser loginUser = getLoginUser(request);
        String userName = loginUser == null ? "" : loginUser.getUserName() + "[" + loginUser.getNickName() + "]";
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("请求开始: 接口地址=").append(request.getRequestURI())
                .append(", 方法类型=").append(request.getMethod())
                .append(", 用户=").append(userName);
        logMessage.append(", 请求参数: ");
        Object[] args = joinPoint.getArgs();
        boolean haveParams = false;
        if (request.getMethod().equalsIgnoreCase("get")) {
            haveParams = request.getParameterMap().size() > 0;
            request.getParameterMap().forEach((key, value) -> logMessage.append(key).append(": ").append(Arrays.stream(value).collect(Collectors.joining(",", "", ""))).append(", "));
        }
        if (args.length > 0 &&!haveParams) {
            logMessage.append(Arrays.stream(args).map(this::filterMultipartFile).collect(Collectors.toList()));
        }
        log.info(logMessage.toString());
    }

    /**
     * 请求结束的处理
     * @param ret
     */
    private void requestEnd(Object ret) {
        HttpServletRequest request = getHttpServletRequest();
        SysUser loginUser = getLoginUser(request);
        String userName = loginUser == null ? "" : loginUser.getUserName() + "[" + loginUser.getNickName() + "]";
        long time = new Date().getTime() - startTime.get();
        if (!request.getRequestURI().contains("/common/")) {
            log.info("请求结束：接口地址={},用户={},耗时:{}ms, 返回结果={}", request.getRequestURI(), userName, time, ret == null ? "" : new Gson().toJson(ret));
        }
        startTime.remove();
    }

    /**
     * 获取请求HttpServletRequest
     * @return
     */
    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(attributes).getRequest();
    }

    /**
     * 获取登录用户
     * @param request
     * @return
     */
    private SysUser getLoginUser(HttpServletRequest request) {
        // 获取登录用户，根据实际获取User的方式替换这里
//        return request == null ? null : TokenUtils.getLoginUser(request);
        return new SysUser();
    }

    /**
     * 过滤 MultipartFile
     * @param obj
     * @return
     */
    private Object filterMultipartFile(Object obj) {
        if (obj instanceof MultipartFile) {
            return "[MultipartFile]";
        }
        return obj;
    }

}