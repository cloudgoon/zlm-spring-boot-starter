package io.github.lunasaw.zlm;

import com.luna.common.net.HttpUtils;
import io.github.lunasaw.zlm.api.ZlmRestService;
import io.github.lunasaw.zlm.constant.ApiConstants;
import io.github.lunasaw.zlm.entity.rtp.StartSendRtpResult;
import io.github.lunasaw.zlm.entity.rtp.StartSendRtpTalkReq;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;

/**
 * 批次 C（RTP 对讲）/ H（文件下载）接口单元测试
 *
 * @author luna
 * @date 2026/06/10
 */
public class ZlmRestServiceTalkDownloadTest {

    private static final String HOST   = "http://127.0.0.1:9092";
    private static final String SECRET = "zlm";

    // ==================== 批次 C：RTP 双向对讲 ====================

    @Test
    public void testStartSendRtpTalk() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.START_SEND_RTP_TALK), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"local_port\":12345}");

            StartSendRtpTalkReq req = new StartSendRtpTalkReq();
            req.setApp("rtp");
            req.setStream("test");
            req.setSsrc("1234");
            req.setRecvStreamId("recv-001");

            StartSendRtpResult result = ZlmRestService.startSendRtpTalk(HOST, SECRET, req);

            assertNotNull(result);
            assertEquals("0", result.getCode());
        }
    }

    @Test
    public void testStartSendRtpTalkParamAssembly() {
        StartSendRtpTalkReq req = new StartSendRtpTalkReq();
        req.setVhost("__defaultVhost__");
        req.setApp("rtp");
        req.setStream("test");
        req.setSsrc("1234");
        req.setRecvStreamId("recv-001");
        Map<String, String> map = req.getTalkMap();
        assertEquals("__defaultVhost__", map.get("vhost"));
        assertEquals("rtp", map.get("app"));
        assertEquals("test", map.get("stream"));
        assertEquals("1234", map.get("ssrc"));
        assertEquals("recv-001", map.get("recv_stream_id"));
    }

    // ==================== 批次 H：文件下载 ====================

    @Test
    public void testDownloadFile() throws Exception {
        File target = File.createTempFile("zlm-dl-", ".bin");
        target.deleteOnExit();
        byte[] payload = "hello-zlm".getBytes();

        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            ClassicHttpResponse resp = org.mockito.Mockito.mock(ClassicHttpResponse.class);
            mocked.when(() -> HttpUtils.doPost(eq(HOST), eq(ApiConstants.DOWNLOAD_FILE), anyMap(), anyMap(), anyString()))
                    .thenReturn(resp);
            mocked.when(() -> HttpUtils.checkResponseStreamAndGetResult(resp)).thenReturn(payload);

            String path = ZlmRestService.downloadFile(HOST, SECRET, "/data/record/a.mp4", "a.mp4", target);

            assertNotNull(path);
            assertEquals(target.getAbsolutePath(), path);
            assertArrayEquals(payload, Files.readAllBytes(target.toPath()));
        }
    }

    @Test
    public void testDownloadBin() throws Exception {
        File target = File.createTempFile("zlm-bin-", ".bin");
        target.deleteOnExit();
        byte[] payload = new byte[]{1, 2, 3, 4};

        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            ClassicHttpResponse resp = org.mockito.Mockito.mock(ClassicHttpResponse.class);
            mocked.when(() -> HttpUtils.doPost(eq(HOST), eq(ApiConstants.DOWNLOAD_BIN), anyMap(), anyMap(), anyString()))
                    .thenReturn(resp);
            mocked.when(() -> HttpUtils.checkResponseStreamAndGetResult(resp)).thenReturn(payload);

            String path = ZlmRestService.downloadBin(HOST, SECRET, target);

            assertNotNull(path);
            assertArrayEquals(payload, Files.readAllBytes(target.toPath()));
        }
    }
}
