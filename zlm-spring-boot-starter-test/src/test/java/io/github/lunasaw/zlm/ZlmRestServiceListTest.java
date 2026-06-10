package io.github.lunasaw.zlm;

import com.luna.common.net.HttpUtils;
import io.github.lunasaw.zlm.api.ZlmRestService;
import io.github.lunasaw.zlm.constant.ApiConstants;
import io.github.lunasaw.zlm.entity.ServerResponse;
import io.github.lunasaw.zlm.entity.StreamFfmpegItem;
import io.github.lunasaw.zlm.entity.StreamProxyItem;
import io.github.lunasaw.zlm.entity.StreamPusherItem;
import io.github.lunasaw.zlm.entity.req.MediaReq;
import io.github.lunasaw.zlm.entity.req.RecordTaskReq;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;

/**
 * 批次 A/B/J 接口单元测试（mock HttpUtils 静态出口）
 * 仅验证「参数被正确组装 + 响应被正确反序列化」，不依赖真实 ZLM。
 *
 * @author luna
 * @date 2026/06/10
 */
public class ZlmRestServiceListTest {

    private static final String HOST   = "http://127.0.0.1:9092";
    private static final String SECRET = "zlm";

    // ==================== 批次 A：列表查询补全 ====================

    @Test
    public void testListStreamProxy() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.LIST_STREAM_PROXY), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":[{\"key\":\"k1\",\"app\":\"live\",\"stream\":\"test\"}]}");

            ServerResponse<List<StreamProxyItem>> resp = ZlmRestService.listStreamProxy(HOST, SECRET);

            assertNotNull(resp);
            assertEquals(Integer.valueOf(0), resp.getCode());
            assertEquals(1, resp.getData().size());
            assertEquals("k1", resp.getData().get(0).getKey());
            assertEquals("live", resp.getData().get(0).getApp());
        }
    }

    @Test
    public void testListStreamPusherProxy() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.LIST_STREAM_PUSHER_PROXY), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":[{\"key\":\"p1\",\"app\":\"live\"}]}");

            ServerResponse<List<StreamPusherItem>> resp = ZlmRestService.listStreamPusherProxy(HOST, SECRET);

            assertEquals(Integer.valueOf(0), resp.getCode());
            assertEquals("p1", resp.getData().get(0).getKey());
        }
    }

    @Test
    public void testListFFmpegSource() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.LIST_FFMPEG_SOURCE), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":[{\"key\":\"f1\",\"src_url\":\"rtsp://a\"}]}");

            ServerResponse<List<StreamFfmpegItem>> resp = ZlmRestService.listFFmpegSource(HOST, SECRET);

            assertEquals(Integer.valueOf(0), resp.getCode());
            assertEquals("f1", resp.getData().get(0).getKey());
            assertEquals("rtsp://a", resp.getData().get(0).getSrcUrl());
        }
    }

    @Test
    public void testListRtpSender() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.LIST_RTP_SENDER), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":[\"0x1234\",\"0x5678\"]}");

            MediaReq req = new MediaReq();
            req.setApp("live");
            req.setStream("test");
            ServerResponse<List<String>> resp = ZlmRestService.listRtpSender(HOST, SECRET, req);

            assertEquals(Integer.valueOf(0), resp.getCode());
            assertEquals(2, resp.getData().size());
            assertEquals("0x1234", resp.getData().get(0));
        }
    }

    // ==================== 批次 B：录制 / 截图补全 ====================

    @Test
    public void testStartRecordTask() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.START_RECORD_TASK), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"ok\"}");

            RecordTaskReq req = new RecordTaskReq();
            req.setApp("live");
            req.setStream("test");
            req.setPath("record/live/test.mp4");
            req.setBackMs("5000");
            req.setForwardMs("10000");
            ServerResponse<String> resp = ZlmRestService.startRecordTask(HOST, SECRET, req);

            assertEquals(Integer.valueOf(0), resp.getCode());
            assertEquals("ok", resp.getData());
        }
    }

    @Test
    public void testStartRecordTaskParamAssembly() {
        // 验证 RecordTaskReq.toMap 组装了全部必填字段
        RecordTaskReq req = new RecordTaskReq();
        req.setVhost("__defaultVhost__");
        req.setApp("live");
        req.setStream("test");
        req.setPath("p.mp4");
        req.setBackMs("5000");
        req.setForwardMs("10000");
        Map<String, String> map = req.toMap();
        assertEquals("__defaultVhost__", map.get("vhost"));
        assertEquals("live", map.get("app"));
        assertEquals("test", map.get("stream"));
        assertEquals("p.mp4", map.get("path"));
        assertEquals("5000", map.get("back_ms"));
        assertEquals("10000", map.get("forward_ms"));
    }

    @Test
    public void testDeleteSnapDirectory() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.DELETE_SNAP_DIRECTORY), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"deleted\"}");

            MediaReq req = new MediaReq();
            req.setApp("live");
            req.setStream("test");
            ServerResponse<String> resp = ZlmRestService.deleteSnapDirectory(HOST, SECRET, req);

            assertEquals(Integer.valueOf(0), resp.getCode());
            assertEquals("deleted", resp.getData());
        }
    }

    // ==================== 批次 J：探针 ====================

    @Test
    public void testAddProbe() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.ADD_PROBE), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"probe-added\"}");

            MediaReq req = new MediaReq();
            req.setApp("live");
            req.setStream("test");
            ServerResponse<String> resp = ZlmRestService.addProbe(HOST, SECRET, req, 3000);

            assertEquals(Integer.valueOf(0), resp.getCode());
            assertEquals("probe-added", resp.getData());
        }
    }
}
