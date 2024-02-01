package top.ticho.tool.intranet.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.intranet.constant.CommConst;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.security.KeyStore;


/**
 * ssl证书处理
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Getter
@Slf4j
public class SslHandler {

    private final SSLContext SslContext;

    public SslHandler(String jksPath, String sslPassword) {
        try {
            KeyStore ks = KeyStore.getInstance(CommConst.JKS);
            ks.load(this.loadJks(jksPath), sslPassword.toCharArray());
            log.info("初始化证书key");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, sslPassword.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            log.info("初始化证书");
            SSLContext sslCtx = SSLContext.getInstance(CommConst.TLS);
            sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            log.info("证书初始化成功");
            this.SslContext = sslCtx;
        } catch (Exception e) {
            log.error("加载证书失败;catch{}.", e.getMessage(), e);
            throw new RuntimeException("加载证书失败");
        }
    }

    private InputStream loadJks(String jksPath) throws IOException {
        ClassLoader loader = SslHandler.class.getClassLoader();
        URL ju = loader.getResource(jksPath);
        if (null != ju) {
            log.info("证书路径:{}.", jksPath);
            return loader.getResourceAsStream(jksPath);
        }
        log.warn("应用中不存在证书，从磁盘中加载");
        File jf = new File(jksPath);
        if (jf.exists()) {
            log.info("磁盘中证书已存在:{}.", jksPath);
            return Files.newInputStream(jf.toPath());
        }
        log.info("磁盘中证书不存在:{}.", jksPath);
        return null;
    }

}
