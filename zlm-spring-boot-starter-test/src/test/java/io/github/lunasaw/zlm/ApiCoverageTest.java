package io.github.lunasaw.zlm;

import io.github.lunasaw.zlm.constant.ApiConstants;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertTrue;

/**
 * 接口覆盖自动校验（防回归）。
 * 反射收集 ApiConstants 的全部 API 路径常量，剥掉 API_INDEX 前缀转为短名，
 * 与「文档接口清单」比对，确保文档列出的接口 100% 有对应常量。
 *
 * @author luna
 * @date 2026/06/10
 */
public class ApiCoverageTest {

    /**
     * 文档（docs/ZLMediaKit-Api.md）列出的全部 73 个端点短名。
     * 新增文档接口时同步加入此集合，缺口将由本测试第一时间暴露。
     */
    private static final Set<String> DOCUMENTED = new TreeSet<>(Set.of(
            // 系统信息
            "getApiList", "getThreadsLoad", "getStatistic", "getWorkThreadsLoad",
            "getServerConfig", "setServerConfig", "restartServer", "version",
            // 媒体流
            "getMediaList", "getMediaInfo", "close_stream", "close_streams", "isMediaOnline",
            "getMediaPlayerList", "broadcastMessage",
            // 会话
            "getAllSession", "kick_session", "kick_sessions",
            // 代理
            "addStreamProxy", "delStreamProxy", "getProxyInfo", "addStreamPusherProxy",
            "delStreamPusherProxy", "getProxyPusherInfo",
            "listStreamProxy", "listStreamPusherProxy",
            // FFmpeg
            "addFFmpegSource", "delFFmpegSource", "listFFmpegSource",
            // 录制
            "getMp4RecordFile", "deleteRecordDirectory", "startRecord", "setRecordSpeed",
            "seekRecordStamp", "stopRecord", "isRecording", "getMp4RecordSummary", "startRecordTask",
            // 截图
            "getSnap", "deleteSnapDirectory",
            // RTP 服务
            "getRtpInfo", "openRtpServer", "openRtpServerMultiplex", "connectRtpServer",
            "closeRtpServer", "updateRtpServerSSRC", "pauseRtpCheck", "resumeRtpCheck", "listRtpServer",
            // RTP 发送
            "startSendRtp", "startSendRtpPassive", "stopSendRtp", "listRtpSender", "startSendRtpTalk",
            // MP4
            "loadMP4File", "startMultiMp4Publish", "stopMultiMp4Publish", "getStorageSpace",
            // 探针
            "addProbe",
            // 多屏拼接
            "stack/start", "stack/reset", "stack/stop",
            // WebRTC
            "addWebrtcRoomKeeper", "delWebrtcRoomKeeper", "listWebrtcRoomKeepers", "listWebrtcRooms",
            "getWebrtcProxyPlayerInfo", "webrtc", "whip", "whep", "delete_webrtc",
            // ONVIF
            "searchOnvifDevice", "getStreamUrl",
            // 文件下载
            "downloadFile", "downloadBin",
            // 鉴权
            "login", "logout"));

    @Test
    public void allDocumentedApisHaveConstant() {
        Set<String> implemented = collectConstantShortNames(ApiConstants.class);

        Set<String> missing = new TreeSet<>(DOCUMENTED);
        missing.removeAll(implemented);

        assertTrue("以下文档接口尚未在 ApiConstants 接入: " + missing, missing.isEmpty());
    }

    /**
     * 反射读取 ApiConstants 的 public static String 常量，剥掉 API_INDEX 前缀（"/index/api/"）转为短名。
     */
    private static Set<String> collectConstantShortNames(Class<?> clazz) {
        String prefix = ApiConstants.API_INDEX + "/"; // "/index/api/"
        Set<String> result = new TreeSet<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && f.getType() == String.class) {
                try {
                    String v = (String) f.get(null);
                    if (v != null && v.startsWith(prefix)) {
                        result.add(v.substring(prefix.length()));
                    }
                } catch (IllegalAccessException ignored) {
                    // skip
                }
            }
        }
        return result;
    }
}
