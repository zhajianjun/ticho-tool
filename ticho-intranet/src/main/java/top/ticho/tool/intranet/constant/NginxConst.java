package top.ticho.tool.intranet.constant;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class NginxConst {

    public static final String NGINX_CONF_FILE_NAME = "nginx.conf";

    /** 默认nginx配置文件名称 */
    public static final String DEFAULT_NGINX_CONFIG_FILE_NAME = "default.conf";

    public static final String CONF = "conf";

    public static final String NGINX_CONF_D_FILE_NAME = "conf.d";

    public static final String HTTPS = "https";

    public static final String KEY = "key";

    public static final String PEM = "pem";

    public static final String NGINX_RELOAD = " -p %s -s reload";

    public static final String DEFAULT_NGINX_CONFIG =
        "\n" + "#user  nobody;\n" + "worker_processes  1;\n" + "\n" + "#error_log  logs/error.log;\n" + "#error_log  logs/error.log  notice;\n" + "#error_log  logs/error.log  info;\n" + "\n"
            + "#pid        logs/nginx.pid;\n" + "\n" + "\n" + "events {\n" + "    worker_connections  1024;\n" + "}\n" + "\n" + "\n" + "http {\n" + "    include       mime.types;\n"
            + "    default_type  application/octet-stream;\n" + "\n" + "\n" + "    #access_log  logs/access.log  main;\n" + "\n" + "    sendfile        on;\n" + "    #tcp_nopush     on;\n"
            + "\n" + "    keepalive_timeout  65;\n" + "\n" + "    include conf.d/*.conf;\n" + "    server_names_hash_bucket_size 256;\n" + "}\n";

    /** 主站nginx https模板编码 */
    public static final String MAIN_NGINX_HTTPS_CONFIG_TEMPLATE = "MAIN_HTTPS";

    /** 主站nginx http模板编码 */
    public static final String MAIN_NGINX_HTTP_CONFIG_TEMPLATE = "MAIN_HTTP";

}
