package io.github.lunasaw.zlm;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lunasaw.zlm.api.ZlmRestService;
import io.github.lunasaw.zlm.config.ZlmNode;
import io.github.lunasaw.zlm.entity.ServerResponse;
import io.github.lunasaw.zlm.entity.req.StackReq;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 批次 D/E/F/G/I 新增 Controller 路由测试
 *
 * @author luna
 * @date 2026/06/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZlmApplicationTest.class)
@AutoConfigureMockMvc
public class ZlmApiControllerStackWebrtcTest {

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

    private static <T> ServerResponse<T> ok(T data) {
        ServerResponse<T> r = new ServerResponse<>();
        r.setCode(0);
        r.setData(data);
        return r;
    }

    // ---------- 批次 D ----------

    @Test
    public void testStackStartRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.stackStart(anyString(), anyString(), any(StackReq.class)))
                .thenReturn(ok("ok"));
        StackReq req = new StackReq();
        req.setId("89");
        mockMvc.perform(post("/zlm/api/stack/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));
    }

    @Test
    public void testStackResetRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.stackReset(anyString(), anyString(), any(StackReq.class)))
                .thenReturn(ok("reset"));
        mockMvc.perform(post("/zlm/api/stack/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new StackReq())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("reset"));
    }

    @Test
    public void testStackStopRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.stackStop(anyString(), anyString(), eq("89")))
                .thenReturn(ok("stopped"));
        mockMvc.perform(delete("/zlm/api/stack/89"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("stopped"));
    }

    // ---------- 批次 E ----------

    @Test
    public void testAddWebrtcRoomKeeperRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.addWebrtcRoomKeeper(anyString(), anyString(), anyMap()))
                .thenReturn(ok("room-key-1"));
        mockMvc.perform(post("/zlm/api/webrtc/room-keeper/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"server_host\":\"127.0.0.1\",\"server_port\":\"8000\",\"room_id\":\"r1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("room-key-1"));
    }

    @Test
    public void testDelWebrtcRoomKeeperRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.delWebrtcRoomKeeper(anyString(), anyString(), eq("room-key-1")))
                .thenReturn(ok("deleted"));
        mockMvc.perform(delete("/zlm/api/webrtc/room-keeper/room-key-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("deleted"));
    }

    @Test
    public void testListWebrtcRoomKeepersRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.listWebrtcRoomKeepers(anyString(), anyString()))
                .thenReturn(ok(Arrays.asList("k1", "k2")));
        mockMvc.perform(get("/zlm/api/webrtc/room-keeper/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("k1"));
    }

    @Test
    public void testListWebrtcRoomsRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.listWebrtcRooms(anyString(), anyString()))
                .thenReturn(ok(Collections.singletonList("room1")));
        mockMvc.perform(get("/zlm/api/webrtc/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("room1"));
    }

    @Test
    public void testGetWebrtcProxyPlayerInfoRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.getWebrtcProxyPlayerInfo(anyString(), anyString(), eq("key1")))
                .thenReturn(ok("player-info"));
        mockMvc.perform(get("/zlm/api/webrtc/proxy-player/key1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("player-info"));
    }

    // ---------- 批次 F (SDP 文本) ----------

    @Test
    public void testWebrtcRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.webrtc(anyString(), anyString(), anyMap(), eq("sdp-offer")))
                .thenReturn("sdp-answer");
        mockMvc.perform(post("/zlm/api/webrtc?type=play&app=live&stream=test")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("sdp-offer"))
                .andExpect(status().isOk())
                .andExpect(content().string("sdp-answer"));
    }

    @Test
    public void testWhipRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.whip(anyString(), anyString(), anyMap(), eq("sdp-whip")))
                .thenReturn("sdp-whip-answer");
        mockMvc.perform(post("/zlm/api/webrtc/whip?app=live&stream=test")
                        .contentType(MediaType.parseMediaType("application/sdp"))
                        .content("sdp-whip"))
                .andExpect(status().isOk())
                .andExpect(content().string("sdp-whip-answer"));
    }

    @Test
    public void testWhepRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.whep(anyString(), anyString(), anyMap(), eq("sdp-whep")))
                .thenReturn("sdp-whep-answer");
        mockMvc.perform(post("/zlm/api/webrtc/whep?app=live&stream=test")
                        .contentType(MediaType.parseMediaType("application/sdp"))
                        .content("sdp-whep"))
                .andExpect(status().isOk())
                .andExpect(content().string("sdp-whep-answer"));
    }

    @Test
    public void testDeleteWebrtcRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.deleteWebrtc(anyString(), eq("id-1"), eq("token-1")))
                .thenReturn(ok("webrtc-deleted"));
        mockMvc.perform(delete("/zlm/api/webrtc/id-1?token=token-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("webrtc-deleted"));
    }

    // ---------- 批次 G ----------

    @Test
    public void testSearchOnvifDeviceRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.searchOnvifDevice(anyString(), anyString(), eq("192.168.1")))
                .thenReturn(ok(Arrays.asList("dev1", "dev2")));
        mockMvc.perform(get("/zlm/api/onvif/search?subnetPrefix=192.168.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[1]").value("dev2"));
    }

    @Test
    public void testGetOnvifStreamUrlRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.getOnvifStreamUrl(anyString(), anyString(), anyString()))
                .thenReturn(ok("rtsp://dev/stream"));
        mockMvc.perform(get("/zlm/api/onvif/stream-url?onvifUrl=http://192.168.1.10/onvif"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("rtsp://dev/stream"));
    }

    // ---------- 批次 I ----------

    @Test
    public void testLoginRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.login(anyString(), anyString(), eq("cookie123")))
                .thenReturn(ok("login-ok"));
        mockMvc.perform(get("/zlm/api/auth/login?cookie=cookie123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("login-ok"));
    }

    @Test
    public void testLogoutRoute() throws Exception {
        zlmRestServiceMock.when(() -> ZlmRestService.logout(anyString(), anyString()))
                .thenReturn(ok("logout-ok"));
        mockMvc.perform(get("/zlm/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("logout-ok"));
    }
}
