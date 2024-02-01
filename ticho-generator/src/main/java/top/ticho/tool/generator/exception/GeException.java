package top.ticho.tool.generator.exception;

/**
 * 自定义异常
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class GeException extends RuntimeException {

    public GeException(String errorMsg) {
        super(errorMsg);
    }

    public GeException(Throwable throwable) {
        super(throwable);
    }

    public GeException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }

}
