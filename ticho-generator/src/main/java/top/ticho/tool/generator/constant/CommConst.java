package top.ticho.tool.generator.constant;

import ch.qos.logback.classic.util.ContextInitializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @author zhajianjun
 * @date 2024-11-16 20:21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommConst {

    public static final String EMPTY = "";
    public static final String DOT = ".";
    public static final String COMMA = ",";


    public static final String CONFIG_FILE_PROPERTY = ContextInitializer.CONFIG_FILE_PROPERTY;
    public static final String CONFIG_LOGBACK_XML = "config/logback.xml";
    public static final String CONFIG_YML = "config.yml";
    public static final String CONFIG_ENV_YML = "config-%s.yml";
    public static final String PROJECT_PATH = System.getProperty("user.dir");
    public static final String DATA_PATH = "data";
    public static final String JSON_FILE_NAME = "param.json";
    public static final String TRACE_ID_KEY = "traceId";
    public static final String TRACE_KEY = "trace";
    public static final String TEMPLATE_PATH = PROJECT_PATH + File.separator + "templates";
    public static final String TEMPLATE_FILE_EXT_NAME = "btl";
    public static final String JAVA_FILE_EXT_NAME = "java";

    public static final String TABLE = "table";
    public static final String PACKAGE = "package";
    public static final String CLASS_NAME = "className";
    public static final String DATE = "date";
    public static final String DEFAULT_KEY_NAME = "id";
    public static final String KEY_NAME = "keyName";
    public static final String KEY_NAME_UF = "keyNameUF";
    public static final String KEY_NAME_LF = "keyNameLF";

    public static final String WINDOWS = "Windows";
    public static final String MAC = "Mac";

}
