package io.github.lunasaw.zlm;

import com.luna.common.net.HttpUtils;
import io.github.lunasaw.zlm.api.ZlmRestService;
import io.github.lunasaw.zlm.constant.ApiConstants;
import io.github.lunasaw.zlm.entity.ServerResponse;
import io.github.lunasaw.zlm.entity.req.StackReq;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;

/**
 * 批次 D(多屏拼接) / E+F(WebRTC) / G(ONVIF) / I(鉴权) 接口单元测试
 *
 * @author luna
 * @date 2026/06/10
 */
public class ZlmRestServiceStackWebrtcTest {

    private static final String HOST   = "http://127.0.0.1:9092";
    private static final String SECRET = "zlm";

    // ==================== 批次 D：多屏拼接（POST + JSON body）====================

    @Test
    public void testStackStart() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            // stack/start 走 JSON body 提交：第 5 个参数为 body，断言 body 非空
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.STACK_START), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"ok\"}");

            StackReq req = new StackReq();
            req.setId("89");
            req.setRow(4);
            req.setCol(4);
            req.setWidth(1920);
            req.setHeight(1080);
            req.setUrl(Arrays.asList(Arrays.asList("rtsp://a/1", "rtsp://a/2")));

            ServerResponse<String> resp = ZlmRestService.stackStart(HOST, SECRET, req);
            assertEquals(Integer.valueOf(0), resp.getCode());
            assertEquals("ok", resp.getData());
        }
    }

    @Test
    public void testStackReset() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.STACK_RESET), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"reset\"}");

            StackReq req = new StackReq();
            req.setId("89");
            ServerResponse<String> resp = ZlmRestService.stackReset(HOST, SECRET, req);
            assertEquals("reset", resp.getData());
        }
    }

    @Test
    public void testStackStop() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.STACK_STOP), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"stopped\"}");

            ServerResponse<String> resp = ZlmRestService.stackStop(HOST, SECRET, "89");
            assertEquals("stopped", resp.getData());
        }
    }

    // ==================== 批次 E：WebRTC 房间管理 ====================

    @Test
    public void testAddWebrtcRoomKeeper() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.ADD_WEBRTC_ROOM_KEEPER), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"room-key-1\"}");

            Map<String, String> params = new HashMap<>();
            params.put("server_host", "127.0.0.1");
            params.put("server_port", "8000");
            params.put("room_id", "r1");
            ServerResponse<String> resp = ZlmRestService.addWebrtcRoomKeeper(HOST, SECRET, params);
            assertEquals("room-key-1", resp.getData());
        }
    }

    @Test
    public void testDelWebrtcRoomKeeper() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.DEL_WEBRTC_ROOM_KEEPER), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"deleted\"}");

            ServerResponse<String> resp = ZlmRestService.delWebrtcRoomKeeper(HOST, SECRET, "room-key-1");
            assertEquals("deleted", resp.getData());
        }
    }

    @Test
    public void testListWebrtcRoomKeepers() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.LIST_WEBRTC_ROOM_KEEPERS), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":[\"k1\",\"k2\"]}");

            ServerResponse<List<String>> resp = ZlmRestService.listWebrtcRoomKeepers(HOST, SECRET);
            assertEquals(2, resp.getData().size());
        }
    }

    @Test
    public void testListWebrtcRooms() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.LIST_WEBRTC_ROOMS), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":[\"room1\"]}");

            ServerResponse<List<String>> resp = ZlmRestService.listWebrtcRooms(HOST, SECRET);
            assertEquals("room1", resp.getData().get(0));
        }
    }

    @Test
    public void testGetWebrtcProxyPlayerInfo() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.GET_WEBRTC_PROXY_PLAYER_INFO), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"player-info\"}");

            ServerResponse<String> resp = ZlmRestService.getWebrtcProxyPlayerInfo(HOST, SECRET, "key1");
            assertEquals("player-info", resp.getData());
        }
    }

    // ==================== 批次 F：WebRTC 交互（SDP 文本 body）====================

    @Test
    public void testWebrtc() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.WEBRTC), anyMap(), anyMap(), eq("v=0\r\no=sdp-offer")))
                    .thenReturn("v=0\r\no=sdp-answer");

            Map<String, String> query = new HashMap<>();
            query.put("type", "play");
            query.put("app", "live");
            query.put("stream", "test");
            String answer = ZlmRestService.webrtc(HOST, SECRET, query, "v=0\r\no=sdp-offer");
            assertEquals("v=0\r\no=sdp-answer", answer);
        }
    }

    @Test
    public void testWhip() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.WHIP), anyMap(), anyMap(), eq("sdp-whip")))
                    .thenReturn("sdp-whip-answer");

            Map<String, String> query = new HashMap<>();
            query.put("app", "live");
            query.put("stream", "test");
            String answer = ZlmRestService.whip(HOST, SECRET, query, "sdp-whip");
            assertEquals("sdp-whip-answer", answer);
        }
    }

    @Test
    public void testWhep() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.WHEP), anyMap(), anyMap(), eq("sdp-whep")))
                    .thenReturn("sdp-whep-answer");

            Map<String, String> query = new HashMap<>();
            query.put("app", "live");
            query.put("stream", "test");
            String answer = ZlmRestService.whep(HOST, SECRET, query, "sdp-whep");
            assertEquals("sdp-whep-answer", answer);
        }
    }

    @Test
    public void testDeleteWebrtc() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doDeleteHandler(eq(HOST), eq(ApiConstants.DELETE_WEBRTC), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"webrtc-deleted\"}");

            ServerResponse<String> resp = ZlmRestService.deleteWebrtc(HOST, "id-1", "token-1");
            assertEquals("webrtc-deleted", resp.getData());
        }
    }

    // ==================== 批次 G：ONVIF ====================

    @Test
    public void testSearchOnvifDevice() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.SEARCH_ONVIF_DEVICE), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":[\"dev1\",\"dev2\"]}");

            ServerResponse<List<String>> resp = ZlmRestService.searchOnvifDevice(HOST, SECRET, "192.168.1");
            assertEquals(2, resp.getData().size());
        }
    }

    @Test
    public void testGetOnvifStreamUrl() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.GET_ONVIF_STREAM_URL), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"rtsp://dev/stream\"}");

            ServerResponse<String> resp = ZlmRestService.getOnvifStreamUrl(HOST, SECRET, "http://192.168.1.10/onvif");
            assertEquals("rtsp://dev/stream", resp.getData());
        }
    }

    // ==================== 批次 I：鉴权 ====================

    @Test
    public void testLogin() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            String cookie = "abc123";
            String expectedDigest = DigestUtils.md5DigestAsHex(("zlmediakit:" + SECRET + ":" + cookie).getBytes());
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.LOGIN),
                    anyMap(), argThat(m -> m != null && expectedDigest.equals(((Map<?, ?>) m).get("digest"))), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"login-ok\"}");

            ServerResponse<String> resp = ZlmRestService.login(HOST, SECRET, cookie);
            assertEquals("login-ok", resp.getData());
        }
    }

    @Test
    public void testLogout() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            mocked.when(() -> HttpUtils.doPostHander(eq(HOST), eq(ApiConstants.LOGOUT), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0,\"data\":\"logout-ok\"}");

            ServerResponse<String> resp = ZlmRestService.logout(HOST, SECRET);
            assertEquals("logout-ok", resp.getData());
        }
    }
}
