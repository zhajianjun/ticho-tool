package top.ticho.tool.generator.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import top.ticho.tool.generator.constant.CommConst;

/**
 * @author zhajianjun
 * @date 2024-11-23 23:29
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TraceUtil {

    public static void traceEnv(String env) {
        trace();
        if (StrUtil.isBlank(env)) {
            return;
        }
        String traceId = MDC.get(CommConst.TRACE_ID_KEY);
        MDC.put(CommConst.TRACE_KEY, String.format("%s[%s]", traceId, env));
    }

    public static void trace() {
        String traceId = MDC.get(CommConst.TRACE_ID_KEY);
        if (StrUtil.isBlank(traceId)) {
            traceId = StrUtil.generateId();
            MDC.put(CommConst.TRACE_ID_KEY, traceId);
            MDC.put(CommConst.TRACE_KEY, traceId);
        }
    }

}
