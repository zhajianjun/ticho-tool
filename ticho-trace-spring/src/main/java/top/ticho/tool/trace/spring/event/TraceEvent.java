package top.ticho.tool.trace.spring.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import top.ticho.tool.trace.common.bean.TraceInfo;

/**
 * 链路追踪事件
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
@Getter
public class TraceEvent extends ApplicationContextEvent {

    private final TraceInfo traceInfo;

    public TraceEvent(ApplicationContext source, TraceInfo traceInfo) {
        super(source);
        this.traceInfo = traceInfo;
    }

}
