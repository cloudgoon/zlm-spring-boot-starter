package io.github.lunasaw.zlm;

import com.luna.common.net.HttpUtils;
import io.github.lunasaw.zlm.api.ZlmRestService;
import io.github.lunasaw.zlm.entity.*;
import io.github.lunasaw.zlm.entity.req.MediaReq;
import io.github.lunasaw.zlm.entity.req.RecordReq;
import io.github.lunasaw.zlm.entity.rtp.*;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;

/**
 * ZlmRestService 全量方法覆盖测试：对每个 public static 方法走一次 mock 调用，
 * 验证参数组装链路不抛异常并提升行覆盖率（目标 ≥ 80%）。
 * 仅做「能跑通 + 不抛异常」级别断言，精确语义断言见各专项测试类。
 *
 * @author luna
 * @date 2026/06/10
 */
public class ZlmRestServiceCoverageTest {

    private static final String HOST   = "http://127.0.0.1:9092";
    private static final String SECRET = "zlm";

    private static MediaReq media() {
        MediaReq r = new MediaReq();
        r.setApp("live");
        r.setStream("test");
        return r;
    }

    private static RecordReq record() {
        RecordReq r = new RecordReq();
        r.setApp("live");
        r.setStream("test");
        r.setType(1);
        return r;
    }

    private static Map<String, String> params() {
        Map<String, String> m = new HashMap<>();
        m.put("app", "live");
        m.put("stream", "test");
        return m;
    }

    /**
     * 覆盖所有走 doPostHander（JSON 返回）的方法。
     */
    @Test
    public void coverJsonApis() {
        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            // 返回不带 data 字段的通用 JSON：data 为 null，可被任意返回类型反序列化
            mocked.when(() -> HttpUtils.doPostHander(anyString(), anyString(), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0}");
            mocked.when(() -> HttpUtils.doDeleteHandler(anyString(), anyString(), anyMap(), anyMap(), anyString()))
                    .thenReturn("{\"code\":0}");

            // 系统信息
            ZlmRestService.getVersion(HOST, SECRET);
            ZlmRestService.getApiList(HOST, SECRET);
            ZlmRestService.getApiList(HOST, SECRET, params());
            ZlmRestService.getThreadsLoad(HOST, SECRET);
            ZlmRestService.getStatistic(HOST, SECRET);
            ZlmRestService.getWorkThreadsLoad(HOST, SECRET);
            ZlmRestService.getServerConfig(HOST, SECRET);
            ZlmRestService.setServerConfig(HOST, SECRET, params());
            ZlmRestService.restartServer(HOST, SECRET, params());

            // 媒体流
            ZlmRestService.getMediaList(HOST, SECRET, media());
            ZlmRestService.getMediaList(HOST, SECRET, params());
            ZlmRestService.closeStream(HOST, SECRET, media());
            ZlmRestService.closeStream(HOST, SECRET, params());
            ZlmRestService.closeStreams(HOST, SECRET, params());
            ZlmRestService.isMediaOnline(HOST, SECRET, media());
            ZlmRestService.isMediaOnline(HOST, SECRET, params());
            ZlmRestService.getMediaPlayerList(HOST, SECRET, media());
            ZlmRestService.getMediaPlayerList(HOST, SECRET, params());
            ZlmRestService.broadcastMessage(HOST, SECRET, params());
            ZlmRestService.getMediaInfo(HOST, SECRET, media());
            ZlmRestService.getPlaybackUrls(HOST, SECRET, media());

            // 会话
            ZlmRestService.getAllSession(HOST, SECRET, "554", "127.0.0.1");
            ZlmRestService.getAllSession(HOST, SECRET, params());
            ZlmRestService.kickSession(HOST, SECRET, "sid");
            ZlmRestService.kickSessions(HOST, SECRET, params());

            // 代理
            ZlmRestService.addStreamProxy(HOST, SECRET, new StreamProxyItem("__defaultVhost__", "live", "test", "rtmp://a"));
            ZlmRestService.addStreamProxy(HOST, SECRET, params());
            ZlmRestService.delStreamProxy(HOST, SECRET, "key");
            ZlmRestService.addStreamPusherProxy(HOST, SECRET, new StreamPusherItem("__defaultVhost__", "rtsp", "live", "test", "rtmp://a"));
            ZlmRestService.addStreamPusherProxy(HOST, SECRET, params());
            ZlmRestService.delStreamPusherProxy(HOST, SECRET, "key");
            ZlmRestService.getProxyInfo(HOST, SECRET, params());
            ZlmRestService.getProxyPusherInfo(HOST, SECRET, params());
            ZlmRestService.listStreamProxy(HOST, SECRET);
            ZlmRestService.listStreamPusherProxy(HOST, SECRET);

            // FFmpeg
            ZlmRestService.addFFmpegSource(HOST, SECRET, new StreamFfmpegItem("rtsp://a", "rtmp://b", 1000, false, false));
            ZlmRestService.addFFmpegSource(HOST, SECRET, params());
            ZlmRestService.delFFmpegSource(HOST, SECRET, "key");
            ZlmRestService.listFFmpegSource(HOST, SECRET);

            // 录制
            ZlmRestService.getMp4RecordFile(HOST, SECRET, record());
            ZlmRestService.getMp4RecordFile(HOST, SECRET, params());
            ZlmRestService.deleteRecordDirectory(HOST, SECRET, params());
            ZlmRestService.startRecord(HOST, SECRET, record());
            ZlmRestService.startRecord(HOST, SECRET, params());
            ZlmRestService.setRecordSpeed(HOST, SECRET, record());
            ZlmRestService.setRecordSpeed(HOST, SECRET, params());
            ZlmRestService.seekRecordStamp(HOST, SECRET, record());
            ZlmRestService.seekRecordStamp(HOST, SECRET, params());
            ZlmRestService.stopRecord(HOST, SECRET, record());
            ZlmRestService.stopRecord(HOST, SECRET, params());
            ZlmRestService.isRecording(HOST, SECRET, record());
            ZlmRestService.isRecording(HOST, SECRET, params());
            ZlmRestService.getMp4RecordSummary(HOST, SECRET, params());

            // RTP 服务
            ZlmRestService.getRtpInfo(HOST, SECRET, "ssrc");
            OpenRtpServerReq openReq = new OpenRtpServerReq();
            openReq.setPort(0);
            openReq.setStreamId("s1");
            ZlmRestService.openRtpServer(HOST, SECRET, openReq);
            ZlmRestService.openRtpServer(HOST, SECRET, params());
            ZlmRestService.openRtpServerMultiplex(HOST, SECRET, openReq);
            ZlmRestService.openRtpServerMultiplex(HOST, SECRET, params());
            ConnectRtpServerReq connReq = new ConnectRtpServerReq();
            connReq.setStreamId("s1");
            ZlmRestService.connectRtpServer(HOST, SECRET, connReq);
            ZlmRestService.connectRtpServer(HOST, SECRET, params());
            ZlmRestService.closeRtpServer(HOST, SECRET, "s1");
            ZlmRestService.updateRtpServerSSRC(HOST, SECRET, "s1", "ssrc");
            ZlmRestService.pauseRtpCheck(HOST, SECRET, "s1");
            ZlmRestService.resumeRtpCheck(HOST, SECRET, "s1");
            ZlmRestService.listRtpServer(HOST, SECRET);

            // RTP 发送
            StartSendRtpReq sendReq = new StartSendRtpReq();
            sendReq.setApp("live");
            sendReq.setStream("test");
            sendReq.setSsrc(1);
            sendReq.setSrcPort(0);
            sendReq.setPt(96);
            sendReq.setUsePs(1);
            sendReq.setOnlyAudio(false);
            ZlmRestService.startSendRtp(HOST, SECRET, sendReq);
            ZlmRestService.startSendRtp(HOST, SECRET, params());
            ZlmRestService.startSendRtpPassive(HOST, SECRET, sendReq);
            ZlmRestService.startSendRtpPassive(HOST, SECRET, params());
            CloseSendRtpReq closeSend = new CloseSendRtpReq();
            closeSend.setApp("live");
            closeSend.setStream("test");
            ZlmRestService.stopSendRtp(HOST, SECRET, closeSend);
            ZlmRestService.stopSendRtp(HOST, SECRET, params());

            // MP4 / 存储
            ZlmRestService.startMultiMp4Publish(HOST, SECRET, params());
            ZlmRestService.stopMultiMp4Publish(HOST, SECRET, params());
            ZlmRestService.getStorageSpace(HOST, SECRET, params());
            ZlmRestService.loadMP4File(HOST, SECRET, params());

            // WebRTC / 对讲 / DELETE
            ZlmRestService.deleteWebrtc(HOST, "id", "token");
        }
    }

    /**
     * 覆盖 doApiImg 字节流方法（getSnap / downloadFile / downloadBin）。
     */
    @Test
    public void coverImgAndDownloadApis() throws Exception {
        File snap = File.createTempFile("zlm-cov-snap-", ".jpg");
        snap.deleteOnExit();
        File dl = File.createTempFile("zlm-cov-dl-", ".bin");
        dl.deleteOnExit();

        try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
            ClassicHttpResponse resp = Mockito.mock(ClassicHttpResponse.class);
            mocked.when(() -> HttpUtils.doPost(anyString(), anyString(), anyMap(), anyMap(), anyString()))
                    .thenReturn(resp);
            mocked.when(() -> HttpUtils.checkResponseStreamAndGetResult(resp)).thenReturn("bytes".getBytes());

            Map<String, String> snapParams = params();
            ZlmRestService.getSnap(HOST, SECRET, snapParams, snap.getAbsolutePath());
            ZlmRestService.downloadFile(HOST, SECRET, "/data/a.mp4", "a.mp4", dl);
            ZlmRestService.downloadBin(HOST, SECRET, dl);
        }
    }
}
