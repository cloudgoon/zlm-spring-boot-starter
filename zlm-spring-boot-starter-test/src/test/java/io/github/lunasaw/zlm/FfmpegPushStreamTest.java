package io.github.lunasaw.zlm;

import io.github.lunasaw.zlm.api.controller.ZlmApiController;
import io.github.lunasaw.zlm.entity.MediaOnlineStatus;
import io.github.lunasaw.zlm.entity.req.MediaReq;
import io.github.lunasaw.zlm.support.FfmpegTestHelper;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.junit.Assert.*;

/**
 * 推流 E2E 测试：本地 ffmpeg 推流 → ZLM → 断言流在线/截图。
 * <p>
 * 守卫（任一不满足即 Assume 跳过，不失败 CI）：
 * 1. zlm.test.e2e-enabled=true
 * 2. ffmpeg 可执行
 * 3. ZLM 服务器可达（getVersion 成功）
 *
 * @author luna
 * @date 2026/06/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZlmApplicationTest.class)
@ActiveProfiles("dev")
public class FfmpegPushStreamTest {

    @Autowired
    private ZlmApiController        zlmApiController;

    @Value("${zlm.test.e2e-enabled:false}")
    private boolean                e2eEnabled;

    @Value("${zlm.test.ffmpeg-path:/usr/local/bin/ffmpeg}")
    private String                 ffmpegPath;

    @Value("${zlm.test.files-dir:../src/main/resources/files}")
    private String                 filesDir;

    @Value("${zlm.test.rtsp-push-url:rtsp://127.0.0.1:554}")
    private String                 rtspBase;

    private static final String    APP    = "live";
    private static final String    STREAM = "tdd_test";

    private Process                ffmpeg;

    @Before
    public void setUp() {
        // 守卫 1：E2E 开关
        Assume.assumeTrue("zlm.test.e2e-enabled=false，跳过 E2E 推流测试", e2eEnabled);
        // 守卫 2：ffmpeg 可用
        Assume.assumeTrue("ffmpeg 不可用，跳过 E2E 推流测试", FfmpegTestHelper.isFfmpegAvailable(ffmpegPath));
        // 守卫 3：ZLM 可达
        boolean zlmReachable;
        try {
            zlmReachable = zlmApiController.getVersion() != null
                    && zlmApiController.getVersion().getCode() != null;
        } catch (Exception e) {
            zlmReachable = false;
        }
        Assume.assumeTrue("ZLM 服务器不可达，跳过 E2E 推流测试", zlmReachable);
    }

    @After
    public void tearDown() {
        FfmpegTestHelper.stop(ffmpeg);
        // 收尾：关闭测试流
        try {
            MediaReq req = mediaReq();
            zlmApiController.closeStream(req);
        } catch (Exception ignored) {
            // 收尾失败不影响测试结论
        }
    }

    @Test
    public void testRtspPushStreamOnline() {
        File source = FfmpegTestHelper.resolveFile(filesDir, "record.mp4");
        assertTrue("测试素材不存在: " + source.getAbsolutePath(), source.exists());

        String url = rtspBase + "/" + APP + "/" + STREAM;
        ffmpeg = FfmpegTestHelper.pushRtsp(ffmpegPath, source.getAbsolutePath(), url);

        // 轮询等待流上线（最多 15s）
        boolean online = FfmpegTestHelper.awaitTrue(() -> {
            MediaOnlineStatus status = zlmApiController.isMediaOnline(mediaReq());
            return status != null && Boolean.TRUE.equals(status.getOnline());
        }, 15, 1000);

        assertTrue("推流 15s 内未上线", online);
    }

    private MediaReq mediaReq() {
        MediaReq req = new MediaReq();
        req.setApp(APP);
        req.setStream(STREAM);
        return req;
    }
}
