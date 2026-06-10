package io.github.lunasaw.zlm.entity.rtp;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.lunasaw.zlm.entity.req.MediaReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * RTP 双向对讲推流请求参数（startSendRtpTalk）。
 * 在 startSendRtp 基础上增加 recv_stream_id：对方推流上来的流 id，
 * 我们将通过该链接回复 rtp 流；两个流的 app/vhost 需一致。
 *
 * @author luna
 * @date 2026/06/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StartSendRtpTalkReq extends MediaReq {

    /**
     * rtp 推流出去的 ssrc
     */
    @JSONField(name = "ssrc")
    private String  ssrc;

    /**
     * 对方 rtp 推流上来的流 id，用于回复 rtp 流
     */
    @JSONField(name = "recv_stream_id")
    private String  recvStreamId;

    /**
     * 是否推送本地 MP4 录像（可选）
     */
    @JSONField(name = "from_mp4")
    private String  fromMp4;

    /**
     * 0(ES流)、1(PS流)、2(TS流)，默认1(PS流)（可选）
     */
    @JSONField(name = "type")
    private String  type;

    /**
     * rtp payload type，默认96（可选）
     */
    @JSONField(name = "pt")
    private String  pt;

    /**
     * rtp es 方式打包时，是否只打包音频（可选）
     */
    @JSONField(name = "only_audio")
    private String  onlyAudio;

    /**
     * 转发 rtp(tcp模式)时，发送不出去是否限制源端收流速度（可选）
     */
    @JSONField(name = "enable_origin_recv_limit")
    private String  enableOriginRecvLimit;

    /**
     * 组装对讲参数：vhost/app/stream（来自父类）+ ssrc/recv_stream_id 及可选项。
     */
    public Map<String, String> getTalkMap() {
        Map<String, String> map = toMap();
        putIfNotNull(map, "ssrc", ssrc);
        putIfNotNull(map, "recv_stream_id", recvStreamId);
        putIfNotNull(map, "from_mp4", fromMp4);
        putIfNotNull(map, "type", type);
        putIfNotNull(map, "pt", pt);
        putIfNotNull(map, "only_audio", onlyAudio);
        putIfNotNull(map, "enable_origin_recv_limit", enableOriginRecvLimit);
        return map;
    }

    private static void putIfNotNull(Map<String, String> map, String key, String value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
