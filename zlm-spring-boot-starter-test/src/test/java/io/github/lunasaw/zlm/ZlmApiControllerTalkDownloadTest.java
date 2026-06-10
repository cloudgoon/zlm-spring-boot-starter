package io.github.lunasaw.zlm;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lunasaw.zlm.api.ZlmRestService;
import io.github.lunasaw.zlm.config.ZlmNode;
import io.github.lunasaw.zlm.entity.rtp.StartSendRtpResult;
import io.github.lunasaw.zlm.entity.rtp.StartSendRtpTalkReq;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 批次 C（RTP 对讲）/ H（文件下载）Controller 路由测试
 *
 * @author luna
 * @date 2026/06/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZlmApplicationTest.class)
@AutoConfigureMockMvc
public class ZlmApiControllerTalkDownloadTest {

    @Autowired
    private MockMvc                      mockMvc;

    @Autowired
    private ObjectMapper                 objectMapper;

    @MockBean
    private LoadBalancer                 loadBalancer;

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

    @Test
    public void testStartSendRtpTalkRoute() throws Exception {
        StartSendRtpResult result = new StartSendRtpResult();
        result.setCode("0");
        zlmRestServiceMock.when(() -> ZlmRestService.startSendRtpTalk(anyString(), anyString(), any(StartSendRtpTalkReq.class)))
                .thenReturn(result);

        StartSendRtpTalkReq req = new StartSendRtpTalkReq();
        req.setApp("rtp");
        req.setStream("test");
        req.setSsrc("1234");
        req.setRecvStreamId("recv-001");
        mockMvc.perform(post("/zlm/api/rtp/send/start-talk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }

    @Test
    public void testDownloadFileRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.downloadFile(anyString(), anyString(), anyString(), nullable(String.class), any()))
                .thenReturn("/tmp/zlm-download-x-a.mp4");

        mockMvc.perform(get("/zlm/api/download/file?filePath=/data/record/a.mp4&saveName=a.mp4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("/tmp/zlm-download-x-a.mp4"));
    }

    @Test
    public void testDownloadBinRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.downloadBin(anyString(), anyString(), any()))
                .thenReturn("/tmp/zlm-download-bin-x");

        mockMvc.perform(get("/zlm/api/download/bin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("/tmp/zlm-download-bin-x"));
    }
}
