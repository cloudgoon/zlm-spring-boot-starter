package io.github.lunasaw.zlm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * ZLM Web配置
 * 用于配置静态资源映射，支持截图文件访问
 *
 * @author luna
 * @version 1.0
 * @date 2023/12/2
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "zlm.enable", havingValue = "true")
public class ZlmWebConfig implements WebMvcConfigurer {

    private static volatile String tempSnapshotPath = null;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 检查是否存在 classpath:static 目录
        boolean hasStaticDir = false;
        try {
            File file = ResourceUtils.getFile("classpath:static");
            if (file.exists() && file.isDirectory()) {
                hasStaticDir = true;
                log.info("检测到类路径静态资源目录，无需额外配置临时目录映射");
            }
        } catch (Exception e) {
            log.debug("无法获取类路径静态资源目录: {}", e.getMessage());
        }

        // 如果没有静态目录，则配置临时目录映射
        if (!hasStaticDir) {
            String tempDir = System.getProperty("java.io.tmpdir");
            File tempSnapshotDir = new File(tempDir, "zlm-snapshots");

            // 确保临时目录存在
            if (!tempSnapshotDir.exists()) {
                boolean created = tempSnapshotDir.mkdirs();
                if (created) {
                    log.info("创建临时截图目录: {}", tempSnapshotDir.getAbsolutePath());
                } else {
                    log.warn("无法创建临时截图目录: {}", tempSnapshotDir.getAbsolutePath());
                    return;
                }
            }

            // 配置静态资源映射：/snapshots/** -> file:/temp/zlm-snapshots/snapshots/
            String resourceLocation = "file:" + tempSnapshotDir.getAbsolutePath() + "/";
            registry.addResourceHandler("/snapshots/**")
                    .addResourceLocations(resourceLocation)
                    .setCachePeriod(3600); // 缓存1小时

            tempSnapshotPath = tempSnapshotDir.getAbsolutePath();
            log.info("配置临时截图资源映射: /snapshots/** -> {}", resourceLocation);
        }
    }

    /**
     * 获取临时截图目录路径
     *
     * @return 临时截图目录路径，如果未配置则返回null
     */
    public static String getTempSnapshotPath() {
        return tempSnapshotPath;
    }
}