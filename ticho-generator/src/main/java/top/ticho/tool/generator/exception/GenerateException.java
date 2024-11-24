package top.ticho.tool.generator.exception;

/**
 * 自定义异常
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class GenerateException extends RuntimeException {

    public GenerateException(String errorMsg) {
        super(errorMsg);
    }

    public GenerateException(Throwable throwable) {
        super(throwable);
    }

    public GenerateException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }

}
