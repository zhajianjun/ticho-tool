package top.ticho.tool.generator.exception;

import lombok.extern.slf4j.Slf4j;
import org.beetl.core.ErrorHandler;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.exception.ErrorInfo;

import java.io.IOException;
import java.io.Writer;
import java.util.StringJoiner;

/**
 * 模版引擎错误处理
 * <p>
 * 模版引擎的错误默认是输出异常信息，并不会抛异常中断过程，
 * 所以默认实现ErrorHandler接口实现抛出异常。此类是要注入GroupTemplate中去。
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 * @see GroupTemplate#setErrorHandler(org.beetl.core.ErrorHandler)
 * </p>
 */
@Slf4j
public class BeetlErrorHandler implements ErrorHandler {

    @Override
    public void processException(BeetlException ex, GroupTemplate gt, Writer writer) {
        ErrorInfo error = new ErrorInfo(ex);
        StringJoiner errMsgJoiner = new StringJoiner("");
        if (error.getErrorCode().equals(BeetlException.CLIENT_IO_ERROR_ERROR)) {
            // 不输出详细提示信息
            if (!gt.getConf().isIgnoreClientIOError()) {
                errMsgJoiner.add("客户端IO异常:" + getResourceName(ex.resource.getId()) + ":" + error.getMsg());
                throw new GenerateException(errMsgJoiner.toString());
            }

        }

        int line = error.getErrorTokenLine();

        errMsgJoiner.add(">>");
        errMsgJoiner.add(":");
        errMsgJoiner.add(error.getType());
        errMsgJoiner.add(":");
        errMsgJoiner.add(error.getErrorTokenText() + "位于:" + (line != 0 ? line + "行" : "") + "\n");
        // errMsgJoiner.add(String.valueOf(getResourceName(ex.resource.getId()))).add("\n");
        if (error.getErrorCode().equals(BeetlException.TEMPLATE_LOAD_ERROR)) {
            if (error.getMsg() != null) {
                errMsgJoiner.add(error.getMsg()).add("\n");
            }
            errMsgJoiner.add(gt.getResourceLoader().getInfo());
            throw new GenerateException(errMsgJoiner.toString());
        }
        if (ex.getMessage() != null) {
            errMsgJoiner.add(ex.getMessage()).add("\n");
        }
        String content;
        try {
            Resource<?> res = ex.resource;
            // 显示前后三行的内容
            int[] range = this.getRange(line);
            content = res.getContent(range[0], range[1]);
            if (content != null) {
                String[] strs = content.split(ex.cr);
                int lineNumber = range[0];
                for (String str : strs) {
                    errMsgJoiner.add("" + lineNumber);
                    errMsgJoiner.add("|");
                    errMsgJoiner.add(str).add("\n");
                    lineNumber++;
                }

            }
        } catch (IOException e) {

            // ingore

        }

        if (error.hasCallStack()) {
            errMsgJoiner.add("  ========================").add("\n");
            errMsgJoiner.add("  调用栈:").add("\n");
            for (int i = 0; i < error.getResourceCallStack().size(); i++) {
                errMsgJoiner
                    .add("  " + error.getResourceCallStack().get(i) + " 行：" + error.getTokenCallStack().get(i).line)
                    .add("\n");
            }
        }
        throw new GenerateException(errMsgJoiner.toString());
    }

    protected Object getResourceName(Object resourceId) {
        return resourceId;
    }

    protected int[] getRange(int line) {
        int startLine;
        int endLine;
        if (line > 3) {
            startLine = line - 3;
        } else {
            startLine = 1;
        }

        endLine = startLine + 6;
        return new int[]{startLine, endLine};
    }
}
