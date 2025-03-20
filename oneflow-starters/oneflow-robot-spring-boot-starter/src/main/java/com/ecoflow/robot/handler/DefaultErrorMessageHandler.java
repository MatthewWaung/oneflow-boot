package com.ecoflow.robot.handler;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/**
 * 默认异常消息处理器
 */
public class DefaultErrorMessageHandler implements IErrorMessageHandler {

    /**
     * 换行
     */
    private final String LINE_BREAK = "\n";


    @Override
    public String message(JoinPoint joinPoint, Exception e) {
        StringBuffer error = new StringBuffer();
        error.append("Time: ").append(LocalDateTime.now());
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (requestAttributes == null) ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
//        error.append(LINE_BREAK).append("IP: ").append(WebUtil.getIP());
        Signature signature = joinPoint.getSignature();

        // traceId
        String traceId = MDC.get("traceId");
        if (null != traceId) {
            error.append(LINE_BREAK).append("traceId: ").append(traceId);
        }
        error.append(LINE_BREAK).append("Method: ").append(request.getMethod() + " ").append(signature.getDeclaringTypeName()).append(".").append(signature.getName());
        error.append(LINE_BREAK).append("Args: ").append(JSONObject.toJSONString(joinPoint.getArgs()));
        error.append(LINE_BREAK).append("Exception: ").append(getStackTrace(e));
        return error.toString();
    }

    @Override
    public String message(Exception e) {
        StringBuffer error = new StringBuffer();
        error.append("Time: ").append(LocalDateTime.now());
        error.append(LINE_BREAK).append("Exception: ").append(getStackTrace(e));
        return error.toString();
    }

    /**
     * 获取异常堆栈跟踪详情（可放在工具类中）
     *
     * @param throwable 异常对象
     * @return
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

}
