package top.ticho.tool.trace.common.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 链路初始化信息
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TraceInitInfo {

    /** 链路id */
    private String traceId;
    /** 跨度id */
    private String spanId;
    /** 当前应用名称 */
    private String appName;
    /** 当前ip */
    private String ip;
    /** 上个链路的应用名称 */
    private String preAppName;
    /** 上个链路的Ip */
    private String preIp;
    /** 链路 */
    private String trace;

}
