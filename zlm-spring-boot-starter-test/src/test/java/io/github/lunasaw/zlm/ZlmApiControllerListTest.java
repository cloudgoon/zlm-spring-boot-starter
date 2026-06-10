package io.github.lunasaw.zlm;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lunasaw.zlm.api.ZlmRestService;
import io.github.lunasaw.zlm.config.ZlmNode;
import io.github.lunasaw.zlm.config.ZlmProperties;
import io.github.lunasaw.zlm.entity.ServerResponse;
import io.github.lunasaw.zlm.entity.StreamFfmpegItem;
import io.github.lunasaw.zlm.entity.StreamProxyItem;
import io.github.lunasaw.zlm.entity.StreamPusherItem;
import io.github.lunasaw.zlm.entity.req.MediaReq;
import io.github.lunasaw.zlm.entity.req.RecordTaskReq;
import io.github.lunasaw.zlm.node.LoadBalancer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 批次 A/B/J 新增 Controller 路由测试（验证路由绑定 + 委托 ZlmRestService）
 *
 * @author luna
 * @date 2026/06/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZlmApplicationTest.class)
@AutoConfigureMockMvc
public class ZlmApiControllerListTest {

    @Autowired
    private MockMvc                    mockMvc;

    @Autowired
    private ObjectMapper               objectMapper;

    @MockBean
    private LoadBalancer               loadBalancer;

    private MockedStatic<ZlmRestService> zlmRestServiceMock;
    private ZlmNode                      testNode;

    @Before
    public void setUp() {
        testNode = new ZlmNode();
        testNode.setServerId("test-node-1");
        testNode.setHost("http://127.0.0.1:9092");
        testNode.setSecret("zlm");
        testNode.setWeight(1);
        when(loadBalancer.selectNode(anyString())).thenReturn(testNode);
        zlmRestServiceMock = Mockito.mockStatic(ZlmRestService.class);
    }

    @After
    public void tearDown() {
        if (zlmRestServiceMock != null) {
            zlmRestServiceMock.close();
        }
    }

    private static <T> ServerResponse<T> ok(T data) {
        ServerResponse<T> r = new ServerResponse<>();
        r.setCode(0);
        r.setData(data);
        return r;
    }

    @Test
    public void testListStreamProxyRoute() throws Exception {
        StreamProxyItem item = new StreamProxyItem();
        item.setKey("k1");
        zlmRestServiceMock.when(() -> ZlmRestService.listStreamProxy(anyString(), anyString()))
                .thenReturn(ok(Collections.singletonList(item)));

        mockMvc.perform(get("/zlm/api/proxy/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].key").value("k1"));
    }

    @Test
    public void testListStreamPusherProxyRoute() throws Exception {
        StreamPusherItem item = new StreamPusherItem("__defaultVhost__", "rtsp", "live", "test", "rtmp://a");
        item.setKey("p1");
        zlmRestServiceMock.when(() -> ZlmRestService.listStreamPusherProxy(anyString(), anyString()))
                .thenReturn(ok(Collections.singletonList(item)));

        mockMvc.perform(get("/zlm/api/pusher/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].key").value("p1"));
    }

    @Test
    public void testListFFmpegSourceRoute() throws Exception {
        StreamFfmpegItem item = new StreamFfmpegItem("rtsp://a", "rtmp://b", 10000, false, false);
        item.setKey("f1");
        zlmRestServiceMock.when(() -> ZlmRestService.listFFmpegSource(anyString(), anyString()))
                .thenReturn(ok(Collections.singletonList(item)));

        mockMvc.perform(get("/zlm/api/ffmpeg/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].key").value("f1"));
    }

    @Test
    public void testListRtpSenderRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.listRtpSender(anyString(), anyString(), any(MediaReq.class)))
                .thenReturn(ok(Arrays.asList("0x1234", "0x5678")));

        MediaReq req = new MediaReq();
        req.setApp("live");
        req.setStream("test");
        mockMvc.perform(post("/zlm/api/rtp/sender/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("0x1234"));
    }

    @Test
    public void testStartRecordTaskRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.startRecordTask(anyString(), anyString(), any(RecordTaskReq.class)))
                .thenReturn(ok("ok"));

        RecordTaskReq req = new RecordTaskReq();
        req.setApp("live");
        req.setStream("test");
        req.setPath("p.mp4");
        req.setBackMs("5000");
        req.setForwardMs("10000");
        mockMvc.perform(post("/zlm/api/record/task/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));
    }

    @Test
    public void testDeleteSnapDirectoryRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.deleteSnapDirectory(anyString(), anyString(), any(MediaReq.class)))
                .thenReturn(ok("deleted"));

        MediaReq req = new MediaReq();
        req.setApp("live");
        req.setStream("test");
        mockMvc.perform(post("/zlm/api/snapshot/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("deleted"));
    }

    @Test
    public void testAddProbeRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.addProbe(anyString(), anyString(), any(MediaReq.class), anyInt()))
                .thenReturn(ok("probe-added"));

        MediaReq req = new MediaReq();
        req.setApp("live");
        req.setStream("test");
        mockMvc.perform(post("/zlm/api/probe/add?probeMs=3000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("probe-added"));
    }
}
