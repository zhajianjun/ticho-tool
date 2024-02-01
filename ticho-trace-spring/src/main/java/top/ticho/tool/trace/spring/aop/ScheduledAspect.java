package top.ticho.tool.trace.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled定时任务链路处理
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Aspect
@Component
@ConditionalOnClass({Aspect.class, Scheduled.class})
public class ScheduledAspect extends AbstractAspect {

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        return trace(joinPoint, "XxlJob定时任务", null);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 100;
    }

}
