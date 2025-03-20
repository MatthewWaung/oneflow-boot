package com.ecoflow.robot.aspect;

import com.ecoflow.robot.exception.ISendException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StringUtils;

@Slf4j
@Aspect
@AllArgsConstructor
public class ExceptionAspect {

    private final ISendException sendException;

    /**
     * 切入点配置
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restControllerPointCut() {
        // 切入控制器内部方法
    }

    /**
     * 切点方法执行异常调用
     *
     * @param joinPoint {@link JoinPoint}
     * @param e         {@link Exception}
     */
    @AfterThrowing(value = "restControllerPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        if ("ServiceException".equalsIgnoreCase(e.getClass().getSimpleName())
                || "CustomException".equalsIgnoreCase(e.getClass().getSimpleName())
                || "IllegalArgumentException".equalsIgnoreCase(e.getClass().getSimpleName())) {
            log.info("ExceptionAspect doAfterThrowing skip exception {} ", e.getClass().getName());
            return;
        }
        if (!sendException.send(joinPoint, e)) {
            log.error("exception message sending failed", e);
        }
    }

}
