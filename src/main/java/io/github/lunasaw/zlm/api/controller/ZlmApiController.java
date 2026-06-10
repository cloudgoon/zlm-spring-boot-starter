package io.github.lunasaw.zlm.api.controller;

import com.luna.common.check.Assert;
import io.github.lunasaw.zlm.api.ZlmRestService;
import io.github.lunasaw.zlm.config.ZlmNode;
import io.github.lunasaw.zlm.entity.*;
import io.github.lunasaw.zlm.entity.req.CloseStreamsReq;
import io.github.lunasaw.zlm.entity.req.MediaReq;
import io.github.lunasaw.zlm.entity.req.RecordReq;
import io.github.lunasaw.zlm.entity.req.RecordTaskReq;
import io.github.lunasaw.zlm.entity.req.SnapshotReq;
import io.github.lunasaw.zlm.entity.req.StackReq;
import io.github.lunasaw.zlm.entity.rtp.*;
import io.github.lunasaw.zlm.hook.service.ZlmHookService;
import io.github.lunasaw.zlm.node.NodeSupplier;
import io.github.lunasaw.zlm.node.service.NodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/2
 * @description: ZLM REST API 控制器
 */
@Slf4j
@RestController
@ConditionalOnProperty(value = "zlm.enable", havingValue = "true")
@RequestMapping("/zlm/api")
@Tag(name = "ZLM媒体服务器管理", description = "ZLMediaKit流媒体服务器管理相关接口")
public class ZlmApiController {

    @Autowired
    private NodeSupplier nodeSupplier;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ZlmHookService zlmHookService;
    /**
     * 获取可用的 ZLM 节点
     * 支持通过请求头 X-Node-Key 指定节点
     */
    private ZlmNode getAvailableNode() {
        String nodeKey = request.getHeader("X-Node-Key");
        return getAvailableNode(nodeKey);
    }

    /**
     * 获取可用的 ZLM 节点
     *
     * @param nodeKey 节点key，如果为空则使用负载均衡策略选择节点
     */
    private ZlmNode getAvailableNode(String nodeKey) {
        if (nodeKey != null && !nodeKey.trim().isEmpty()) {
            // 使用指定的节点key获取节点
            return nodeService.getAvailableNode(nodeKey);
        } else {
            // 使用负载均衡策略选择节点
            return nodeService.selectNode();
        }
    }

    // ==================== 系统信息接口 ====================

    /**
     * 获取版本信息
     */
    @GetMapping("/version")
    @Operation(summary = "获取版本信息", description = "获取ZLMediaKit服务器的版本信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<Version> getVersion() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getVersion(node.getHost(), node.getSecret());
    }

    /**
     * 获取API列表
     */
    @GetMapping("/api/list")
    @Operation(summary = "获取API列表", description = "获取ZLMediaKit服务器支持的所有API接口列表")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<String>> getApiList(
            @Parameter(description = "查询参数") @RequestParam(required = false) Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getApiList(node.getHost(), node.getSecret(), params);
    }

    /**
     * 获取网络线程负载
     */
    @GetMapping("/threads/load")
    @Operation(summary = "获取网络线程负载", description = "获取ZLMediaKit服务器网络线程的负载情况")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<ThreadLoad>> getThreadsLoad() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getThreadsLoad(node.getHost(), node.getSecret());
    }

    /**
     * 获取主要对象个数
     */
    @GetMapping("/statistic")
    @Operation(summary = "获取统计信息", description = "获取ZLMediaKit服务器主要对象的统计数量")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<ImportantObjectNum> getStatistic() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getStatistic(node.getHost(), node.getSecret());
    }

    /**
     * 获取后台线程负载
     */
    @GetMapping("/work-threads/load")
    @Operation(summary = "获取后台线程负载", description = "获取ZLMediaKit服务器后台工作线程的负载情况")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<ThreadLoad>> getWorkThreadsLoad() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getWorkThreadsLoad(node.getHost(), node.getSecret());
    }

    /**
     * 获取服务器配置
     */
    @GetMapping("/server/config")
    @Operation(summary = "获取服务器配置", description = "获取ZLMediaKit服务器的配置信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<ServerNodeConfig>> getServerConfig() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getServerConfig(node.getHost(), node.getSecret());
    }

    /**
     * 设置服务器配置
     */
    @PostMapping("/server/config")
    @Operation(summary = "设置服务器配置", description = "修改ZLMediaKit服务器的配置参数")
    @ApiResponse(responseCode = "200", description = "设置成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> setServerConfig(
            @Parameter(description = "配置参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.setServerConfig(node.getHost(), node.getSecret(), params);
    }

    /**
     * 重启服务器
     */
    @PostMapping("/server/restart")
    @Operation(summary = "重启服务器", description = "重启ZLMediaKit服务器")
    @ApiResponse(responseCode = "200", description = "重启成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<Object> restartServer(
            @Parameter(description = "重启参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.restartServer(node.getHost(), node.getSecret(), params);
    }

    // ==================== 媒体流管理接口 ====================

    /**
     * 获取流列表
     */
    @PostMapping("/media/list")
    @Operation(summary = "获取流列表", description = "获取ZLMediaKit服务器中的媒体流列表")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<MediaData>> getMediaList(
            @Parameter(description = "媒体查询条件") @RequestBody MediaReq mediaReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getMediaList(node.getHost(), node.getSecret(), mediaReq);
    }

    /**
     * 关断单个流
     */
    @PostMapping("/media/close")
    @Operation(summary = "关断单个流", description = "关闭指定的媒体流")
    @ApiResponse(responseCode = "200", description = "关闭成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> closeStream(
            @Parameter(description = "媒体流信息") @RequestBody MediaReq mediaReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.closeStream(node.getHost(), node.getSecret(), mediaReq);
    }

    /**
     * 批量关断流
     */
    @PostMapping("/media/close-batch")
    @Operation(summary = "批量关断流", description = "批量关闭多个媒体流")
    @ApiResponse(responseCode = "200", description = "关闭成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse closeStreams(
            @Parameter(description = "批量关流请求") @RequestBody CloseStreamsReq closeStreamsReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.closeStreams(node.getHost(), node.getSecret(), closeStreamsReq.toMap());
    }

    /**
     * 流是否在线
     */
    @PostMapping("/media/online")
    @Operation(summary = "检查流是否在线", description = "检查指定媒体流是否在线")
    @ApiResponse(responseCode = "200", description = "检查成功",
            content = @Content(schema = @Schema(implementation = MediaOnlineStatus.class)))
    public MediaOnlineStatus isMediaOnline(
            @Parameter(description = "媒体流信息") @RequestBody MediaReq mediaReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.isMediaOnline(node.getHost(), node.getSecret(), mediaReq);
    }

    /**
     * 获取媒体流播放器列表
     */
    @PostMapping("/media/player/list")
    @Operation(summary = "获取媒体流播放器列表", description = "获取指定媒体流的播放器列表")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<MediaPlayer> getMediaPlayerList(
            @Parameter(description = "媒体流信息") @RequestBody MediaReq mediaReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getMediaPlayerList(node.getHost(), node.getSecret(), mediaReq);
    }

    /**
     * 获取流信息
     */
    @PostMapping("/media/info")
    @Operation(summary = "获取流信息", description = "获取指定媒体流的详细信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<MediaInfo> getMediaInfo(
            @Parameter(description = "媒体流信息") @RequestBody MediaReq mediaReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getMediaInfo(node.getHost(), node.getSecret(), mediaReq);
    }

    /**
     * 获取播放地址
     */
    @PostMapping("/media/play-urls")
    @Operation(summary = "获取播放地址", description = "获取指定媒体流的多协议播放地址")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<PlayUrl> getPlaybackUrls(
            @Parameter(description = "媒体流信息") @RequestBody MediaReq mediaReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getPlaybackUrls(node.getHost(), node.getSecret(), mediaReq);
    }

    /**
     * 广播webrtc datachannel消息
     */
    @PostMapping("/broadcast/message")
    @Operation(summary = "广播WebRTC消息", description = "广播WebRTC datachannel消息")
    @ApiResponse(responseCode = "200", description = "广播成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse broadcastMessage(
            @Parameter(description = "消息参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.broadcastMessage(node.getHost(), node.getSecret(), params);
    }

    // ==================== TCP会话管理接口 ====================

    /**
     * 获取所有TcpSession列表
     */
    @GetMapping("/session/list")
    @Operation(summary = "获取TCP会话列表", description = "获取ZLMediaKit服务器中所有TCP连接会话的列表")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<TcpLink>> getAllSession(
            @Parameter(description = "本地端口") @RequestParam(required = false) String localPort,
            @Parameter(description = "对端IP") @RequestParam(required = false) String peerIp) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getAllSession(node.getHost(), node.getSecret(), localPort, peerIp);
    }

    /**
     * 断开tcp连接
     */
    @DeleteMapping("/session/{sessionId}")
    @Operation(summary = "断开TCP连接", description = "根据会话ID断开指定的TCP连接")
    @ApiResponse(responseCode = "200", description = "断开成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> kickSession(
            @Parameter(description = "会话ID") @PathVariable("sessionId") String sessionId) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.kickSession(node.getHost(), node.getSecret(), sessionId);
    }

    /**
     * 批量断开tcp连接
     */
    @PostMapping("/session/kick-batch")
    @Operation(summary = "批量断开TCP连接", description = "根据条件批量断开TCP连接")
    @ApiResponse(responseCode = "200", description = "断开成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> kickSessions(
            @Parameter(description = "查询条件") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.kickSessions(node.getHost(), node.getSecret(), params);
    }

    // ==================== 代理流管理接口 ====================

    /**
     * 添加代理拉流
     */
    @PostMapping("/proxy/add")
    @Operation(summary = "添加代理拉流", description = "添加一个拉流代理，用于从外部拉取媒体流")
    @ApiResponse(responseCode = "200", description = "添加成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<StreamKey> addStreamProxy(
            @Parameter(description = "拉流代理配置") @RequestBody StreamProxyItem streamProxyItem) {
        ZlmNode node = getAvailableNode();
        ServerResponse<StreamKey> serverResponse = ZlmRestService.addStreamProxy(node.getHost(), node.getSecret(), streamProxyItem);
        Assert.notNull(serverResponse.getData(), "拉流代理添加失败");
        zlmHookService.onProxyAdded(streamProxyItem, serverResponse.getData(), request);
        return serverResponse;
    }

    /**
     * 关闭拉流代理
     */
    @DeleteMapping("/proxy/{key}")
    @Operation(summary = "关闭拉流代理", description = "根据代理key关闭指定的拉流代理")
    @ApiResponse(responseCode = "200", description = "关闭成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<StreamKey.StringDelFlag> delStreamProxy(
            @Parameter(description = "代理key") @PathVariable("key") String key) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.delStreamProxy(node.getHost(), node.getSecret(), key);
    }

    /**
     * 获取拉流代理信息
     */
    @PostMapping("/proxy/info")
    @Operation(summary = "获取拉流代理信息", description = "获取拉流代理的详细信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse getProxyInfo(
            @Parameter(description = "查询参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getProxyInfo(node.getHost(), node.getSecret(), params);
    }

    /**
     * 添加推流代理
     */
    @PostMapping("/pusher/add")
    @Operation(summary = "添加推流代理", description = "添加一个推流代理，用于向外部推送媒体流")
    @ApiResponse(responseCode = "200", description = "添加成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<StreamKey> addStreamPusherProxy(
            @Parameter(description = "推流代理配置") @RequestBody StreamPusherItem streamPusherItem) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.addStreamPusherProxy(node.getHost(), node.getSecret(), streamPusherItem);
    }

    /**
     * 关闭推流代理
     */
    @DeleteMapping("/pusher/{key}")
    @Operation(summary = "关闭推流代理", description = "根据代理key关闭指定的推流代理")
    @ApiResponse(responseCode = "200", description = "关闭成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<StreamKey.StringDelFlag> delStreamPusherProxy(
            @Parameter(description = "代理key") @PathVariable("key") String key) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.delStreamPusherProxy(node.getHost(), node.getSecret(), key);
    }

    /**
     * 获取推流代理信息
     */
    @PostMapping("/pusher/info")
    @Operation(summary = "获取推流代理信息", description = "获取推流代理的详细信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse getProxyPusherInfo(
            @Parameter(description = "查询参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getProxyPusherInfo(node.getHost(), node.getSecret(), params);
    }

    // ==================== FFmpeg管理接口 ====================

    /**
     * 添加FFmpeg拉流代理
     */
    @PostMapping("/ffmpeg/add")
    @Operation(summary = "添加FFmpeg拉流代理", description = "添加一个FFmpeg拉流代理，用于从外部拉取媒体流")
    @ApiResponse(responseCode = "200", description = "添加成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<StreamKey> addFFmpegSource(
            @Parameter(description = "FFmpeg配置") @RequestBody StreamFfmpegItem streamFfmpegItem) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.addFFmpegSource(node.getHost(), node.getSecret(), streamFfmpegItem);
    }

    /**
     * 关闭FFmpeg拉流代理
     */
    @DeleteMapping("/ffmpeg/{key}")
    @Operation(summary = "关闭FFmpeg拉流代理", description = "根据代理key关闭指定的FFmpeg拉流代理")
    @ApiResponse(responseCode = "200", description = "关闭成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<StreamKey.StringDelFlag> delFFmpegSource(
            @Parameter(description = "代理key") @PathVariable("key") String key) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.delFFmpegSource(node.getHost(), node.getSecret(), key);
    }

    /**
     * 获取FFmpeg拉流代理列表
     */
    @GetMapping("/ffmpeg/list")
    @Operation(summary = "获取FFmpeg拉流代理列表", description = "获取当前所有FFmpeg拉流代理")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<StreamFfmpegItem>> listFFmpegSource() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.listFFmpegSource(node.getHost(), node.getSecret());
    }

    // ==================== 代理流列表接口 ====================

    /**
     * 获取拉流代理列表
     */
    @GetMapping("/proxy/list")
    @Operation(summary = "获取拉流代理列表", description = "获取当前所有拉流代理")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<StreamProxyItem>> listStreamProxy() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.listStreamProxy(node.getHost(), node.getSecret());
    }

    /**
     * 获取推流代理列表
     */
    @GetMapping("/pusher/list")
    @Operation(summary = "获取推流代理列表", description = "获取当前所有推流代理")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<StreamPusherItem>> listStreamPusherProxy() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.listStreamPusherProxy(node.getHost(), node.getSecret());
    }

    // ==================== 录制管理接口 ====================

    /**
     * 获取录制文件列表
     */
    @PostMapping("/record/files")
    @Operation(summary = "获取录制文件列表", description = "获取指定媒体流的录制文件列表")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<Mp4RecordFile> getMp4RecordFile(
            @Parameter(description = "录制查询条件") @RequestBody RecordReq recordReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getMp4RecordFile(node.getHost(), node.getSecret(), recordReq);
    }

    /**
     * 删除录像文件夹
     */
    @PostMapping("/record/delete-directory")
    @Operation(summary = "删除录像文件夹", description = "删除指定的录像文件夹")
    @ApiResponse(responseCode = "200", description = "删除成功",
            content = @Content(schema = @Schema(implementation = DeleteRecordDirectory.class)))
    public DeleteRecordDirectory deleteRecordDirectory(
            @Parameter(description = "删除参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.deleteRecordDirectory(node.getHost(), node.getSecret(), params);
    }

    /**
     * 开始录制
     */
    @PostMapping("/record/start")
    @Operation(summary = "开始录制", description = "开始录制指定的媒体流")
    @ApiResponse(responseCode = "200", description = "录制开始成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> startRecord(
            @Parameter(description = "录制配置") @RequestBody RecordReq recordReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.startRecord(node.getHost(), node.getSecret(), recordReq);
    }

    /**
     * 设置录像速度
     */
    @PostMapping("/record/speed")
    @Operation(summary = "设置录像速度", description = "设置录像文件的播放速度")
    @ApiResponse(responseCode = "200", description = "设置成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> setRecordSpeed(
            @Parameter(description = "录制配置") @RequestBody RecordReq recordReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.setRecordSpeed(node.getHost(), node.getSecret(), recordReq);
    }

    /**
     * 设置录像流播放位置
     */
    @PostMapping("/record/seek")
    @Operation(summary = "设置录像播放位置", description = "设置录像流的播放位置")
    @ApiResponse(responseCode = "200", description = "设置成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> seekRecordStamp(
            @Parameter(description = "录制配置") @RequestBody RecordReq recordReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.seekRecordStamp(node.getHost(), node.getSecret(), recordReq);
    }

    /**
     * 停止录制
     */
    @PostMapping("/record/stop")
    @Operation(summary = "停止录制", description = "停止录制指定的媒体流")
    @ApiResponse(responseCode = "200", description = "录制停止成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> stopRecord(
            @Parameter(description = "录制配置") @RequestBody RecordReq recordReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.stopRecord(node.getHost(), node.getSecret(), recordReq);
    }

    /**
     * 是否正在录制
     */
    @PostMapping("/record/status")
    @Operation(summary = "检查录制状态", description = "检查指定媒体流是否正在录制")
    @ApiResponse(responseCode = "200", description = "检查成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> isRecording(
            @Parameter(description = "录制配置") @RequestBody RecordReq recordReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.isRecording(node.getHost(), node.getSecret(), recordReq);
    }

    /**
     * 查询文件概览
     */
    @PostMapping("/record/summary")
    @Operation(summary = "查询文件概览", description = "查询录制文件的概览信息")
    @ApiResponse(responseCode = "200", description = "查询成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> getMp4RecordSummary(
            @Parameter(description = "查询参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getMp4RecordSummary(node.getHost(), node.getSecret(), params);
    }

    /**
     * 添加录制任务（支持回溯录制）
     */
    @PostMapping("/record/task/start")
    @Operation(summary = "添加录制任务", description = "添加录制任务，支持回溯录制（back_ms）与后续录制（forward_ms）")
    @ApiResponse(responseCode = "200", description = "添加成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> startRecordTask(
            @Parameter(description = "录制任务配置") @RequestBody RecordTaskReq recordTaskReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.startRecordTask(node.getHost(), node.getSecret(), recordTaskReq);
    }

    // ==================== 截图接口 ====================

    /**
     * 获取截图
     */
    @PostMapping("/snapshot")
    @Operation(summary = "获取截图", description = "获取指定媒体流的截图")
    @ApiResponse(responseCode = "200", description = "截图获取成功",
            content = @Content(schema = @Schema(implementation = String.class)))
    public String getSnap(
            @Parameter(description = "截图配置") @RequestBody SnapshotReq snapshotReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getSnap(node.getHost(), node.getSecret(), snapshotReq);
    }

    /**
     * 获取截图URL - 返回可访问的URL路径
     */
    @PostMapping("/snapshot-url")
    @Operation(summary = "获取截图URL", description = "获取指定媒体流的截图并返回可访问的URL")
    @ApiResponse(responseCode = "200", description = "截图URL获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> getSnapUrl(
            @Parameter(description = "截图配置") @RequestBody SnapshotReq snapshotReq) {

        try {
            ZlmNode node = getAvailableNode();

            // 生成唯一的文件名
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileName = "snapshot_" + timestamp + ".jpg";

            // 使用 Spring Boot 的类路径处理静态资源
            String snapshotDir = "snapshots";
            File staticResourceDir = getStaticResourceDirectory();
            File snapshotFolder = new File(staticResourceDir, snapshotDir);

            // 确保静态资源目录存在
            if (!snapshotFolder.exists()) {
                boolean created = snapshotFolder.mkdirs();
                if (!created) {
                    throw new RuntimeException("无法创建截图目录: " + snapshotFolder.getAbsolutePath());
                }
            }

            // 设置完整的文件保存路径
            String fullSavePath = new File(snapshotFolder, fileName).getAbsolutePath();
            snapshotReq.setSavePath(fullSavePath);

            // 调用ZLM API获取截图
            String filePath = ZlmRestService.getSnap(node.getHost(), node.getSecret(), snapshotReq);

            if (filePath != null && !filePath.trim().isEmpty()) {
                // 验证文件是否实际创建
                File savedFile = new File(filePath);
                if (savedFile.exists() && savedFile.length() > 0) {
                    // 构建HTTP访问URL
                    String baseUrl = getBaseUrl();
                    String accessUrl = baseUrl + "/" + snapshotDir + "/" + fileName;

                    ServerResponse<String> response = new ServerResponse<>();
                    response.setCode(0);
                    response.setMsg("截图获取成功");
                    response.setData(accessUrl);
                    return response;
                } else {
                    ServerResponse<String> response = new ServerResponse<>();
                    response.setCode(-1);
                    response.setMsg("截图文件生成失败");
                    return response;
                }
            } else {
                ServerResponse<String> response = new ServerResponse<>();
                response.setCode(-1);
                response.setMsg("ZLM API调用失败");
                return response;
            }

        } catch (Exception e) {
            log.error("获取截图URL失败: {}", e.getMessage(), e);
            ServerResponse<String> response = new ServerResponse<>();
            response.setCode(-1);
            response.setMsg("截图获取失败: " + e.getMessage());
            return response;
        }
    }

    /**
     * 获取静态资源目录
     * 支持开发环境和生产环境（JAR包）
     */
    private File getStaticResourceDirectory() throws Exception {
        // 首先尝试获取 Spring Boot 应用的静态资源目录
        try {
            File file = ResourceUtils.getFile("classpath:static");
            if (file.exists() && file.isDirectory()) {
                log.info("使用类路径静态资源目录: {}", file.getAbsolutePath());
                return file;
            }
        } catch (Exception e) {
            log.debug("无法获取类路径静态资源目录: {}", e.getMessage());
        }

        // 使用临时目录（配合 ZlmWebConfig 的资源映射）
        String tempDir = System.getProperty("java.io.tmpdir");
        File appTempDir = new File(tempDir, "zlm-snapshots");
        if (!appTempDir.exists()) {
            boolean created = appTempDir.mkdirs();
            if (!created) {
                throw new RuntimeException("无法创建临时目录: " + appTempDir.getAbsolutePath());
            }
        }
        log.info("使用临时目录存储截图: {}", appTempDir.getAbsolutePath());
        return appTempDir;
    }

    /**
     * 获取基础URL
     */
    private String getBaseUrl() {
        // 从请求中获取基础URL
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(scheme).append("://").append(serverName);

        // 只有在非标准端口时才添加端口号
        if ((scheme.equals("http") && serverPort != 80) ||
                (scheme.equals("https") && serverPort != 443)) {
            baseUrl.append(":").append(serverPort);
        }

        return baseUrl.toString();
    }

    /**
     * 删除截图文件夹
     */
    @PostMapping("/snapshot/delete")
    @Operation(summary = "删除截图文件夹", description = "删除指定媒体流的截图文件夹")
    @ApiResponse(responseCode = "200", description = "删除成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> deleteSnapDirectory(
            @Parameter(description = "媒体流参数") @RequestBody MediaReq mediaReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.deleteSnapDirectory(node.getHost(), node.getSecret(), mediaReq);
    }

    // ==================== 探针接口 ====================

    /**
     * 添加探针
     */
    @PostMapping("/probe/add")
    @Operation(summary = "添加探针", description = "探测指定流的编码信息，probeMs 为探针时长（毫秒）")
    @ApiResponse(responseCode = "200", description = "添加成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> addProbe(
            @Parameter(description = "媒体流参数") @RequestBody MediaReq mediaReq,
            @Parameter(description = "探针时长(毫秒)") @RequestParam("probeMs") int probeMs) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.addProbe(node.getHost(), node.getSecret(), mediaReq, probeMs);
    }

    // ==================== RTP服务器管理接口 ====================

    /**
     * 获取rtp推流信息
     */
    @GetMapping("/rtp/info/{streamId}")
    @Operation(summary = "获取RTP推流信息", description = "根据流ID获取RTP推流的详细信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = RtpInfoResult.class)))
    public RtpInfoResult getRtpInfo(
            @Parameter(description = "流ID") @PathVariable("streamId") String streamId) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getRtpInfo(node.getHost(), node.getSecret(), streamId);
    }

    /**
     * 创建RTP服务器
     */
    @PostMapping("/rtp/server/open")
    @Operation(summary = "创建RTP服务器", description = "创建一个RTP服务器用于接收RTP推流")
    @ApiResponse(responseCode = "200", description = "创建成功",
            content = @Content(schema = @Schema(implementation = OpenRtpServerResult.class)))
    public OpenRtpServerResult openRtpServer(
            @Parameter(description = "RTP服务器配置") @RequestBody OpenRtpServerReq req) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.openRtpServer(node.getHost(), node.getSecret(), req);
    }

    /**
     * 创建多路复用RTP服务器
     */
    @PostMapping("/rtp/server/open-multiplex")
    @Operation(summary = "创建多路复用RTP服务器", description = "创建一个多路复用RTP服务器")
    @ApiResponse(responseCode = "200", description = "创建成功",
            content = @Content(schema = @Schema(implementation = OpenRtpServerResult.class)))
    public OpenRtpServerResult openRtpServerMultiplex(
            @Parameter(description = "RTP服务器配置") @RequestBody OpenRtpServerReq req) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.openRtpServerMultiplex(node.getHost(), node.getSecret(), req);
    }

    /**
     * 连接RTP服务器
     */
    @PostMapping("/rtp/server/connect")
    @Operation(summary = "连接RTP服务器", description = "连接到指定的RTP服务器")
    @ApiResponse(responseCode = "200", description = "连接成功",
            content = @Content(schema = @Schema(implementation = OpenRtpServerResult.class)))
    public OpenRtpServerResult connectRtpServer(
            @Parameter(description = "RTP连接配置") @RequestBody ConnectRtpServerReq req) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.connectRtpServer(node.getHost(), node.getSecret(), req);
    }

    /**
     * 关闭RTP服务器
     */
    @DeleteMapping("/rtp/server/{streamId}")
    @Operation(summary = "关闭RTP服务器", description = "根据流ID关闭指定的RTP服务器")
    @ApiResponse(responseCode = "200", description = "关闭成功",
            content = @Content(schema = @Schema(implementation = CloseRtpServerResult.class)))
    public CloseRtpServerResult closeRtpServer(
            @Parameter(description = "流ID") @PathVariable("streamId") String streamId) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.closeRtpServer(node.getHost(), node.getSecret(), streamId);
    }

    /**
     * 更新RTP服务器过滤SSRC
     */
    @PutMapping("/rtp/server/{streamId}/ssrc/{ssrc}")
    @Operation(summary = "更新RTP服务器SSRC", description = "更新RTP服务器的过滤SSRC")
    @ApiResponse(responseCode = "200", description = "更新成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> updateRtpServerSSRC(
            @Parameter(description = "流ID") @PathVariable("streamId") String streamId,
            @Parameter(description = "SSRC值") @PathVariable("ssrc") String ssrc) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.updateRtpServerSSRC(node.getHost(), node.getSecret(), streamId, ssrc);
    }

    /**
     * 暂停RTP超时检查
     */
    @PostMapping("/rtp/server/{streamId}/pause-check")
    @Operation(summary = "暂停RTP超时检查", description = "暂停指定RTP服务器的超时检查")
    @ApiResponse(responseCode = "200", description = "暂停成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> pauseRtpCheck(
            @Parameter(description = "流ID") @PathVariable("streamId") String streamId) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.pauseRtpCheck(node.getHost(), node.getSecret(), streamId);
    }

    /**
     * 恢复RTP超时检查
     */
    @PostMapping("/rtp/server/{streamId}/resume-check")
    @Operation(summary = "恢复RTP超时检查", description = "恢复指定RTP服务器的超时检查")
    @ApiResponse(responseCode = "200", description = "恢复成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> resumeRtpCheck(
            @Parameter(description = "流ID") @PathVariable("streamId") String streamId) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.resumeRtpCheck(node.getHost(), node.getSecret(), streamId);
    }

    /**
     * 获取RTP服务器列表
     */
    @GetMapping("/rtp/server/list")
    @Operation(summary = "获取RTP服务器列表", description = "获取所有正在运行的RTP服务器列表")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<RtpServer>> listRtpServer() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.listRtpServer(node.getHost(), node.getSecret());
    }

    // ==================== RTP发送管理接口 ====================

    /**
     * 开始发送rtp
     */
    @PostMapping("/rtp/send/start")
    @Operation(summary = "开始发送RTP", description = "开始向指定地址发送RTP流")
    @ApiResponse(responseCode = "200", description = "开始成功",
            content = @Content(schema = @Schema(implementation = StartSendRtpResult.class)))
    public StartSendRtpResult startSendRtp(
            @Parameter(description = "RTP发送配置") @RequestBody StartSendRtpReq req) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.startSendRtp(node.getHost(), node.getSecret(), req);
    }

    /**
     * 开始tcp passive被动发送rtp
     */
    @PostMapping("/rtp/send/start-passive")
    @Operation(summary = "开始被动发送RTP", description = "开始TCP passive模式被动发送RTP流")
    @ApiResponse(responseCode = "200", description = "开始成功",
            content = @Content(schema = @Schema(implementation = StartSendRtpResult.class)))
    public StartSendRtpResult startSendRtpPassive(
            @Parameter(description = "RTP发送配置") @RequestBody StartSendRtpReq req) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.startSendRtpPassive(node.getHost(), node.getSecret(), req);
    }

    /**
     * 停止发送rtp
     */
    @PostMapping("/rtp/send/stop")
    @Operation(summary = "停止发送RTP", description = "停止向指定地址发送RTP流")
    @ApiResponse(responseCode = "200", description = "停止成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> stopSendRtp(
            @Parameter(description = "RTP停止配置") @RequestBody CloseSendRtpReq req) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.stopSendRtp(node.getHost(), node.getSecret(), req);
    }

    /**
     * 获取rtp发送列表
     */
    @PostMapping("/rtp/sender/list")
    @Operation(summary = "获取RTP发送列表", description = "获取指定流的所有RTP发送ssrc列表")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<String>> listRtpSender(
            @Parameter(description = "媒体流参数") @RequestBody MediaReq mediaReq) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.listRtpSender(node.getHost(), node.getSecret(), mediaReq);
    }

    /**
     * RTP 双向对讲推流
     */
    @PostMapping("/rtp/send/start-talk")
    @Operation(summary = "RTP双向对讲推流", description = "启动RTP双向对讲推流（startSendRtpTalk）")
    @ApiResponse(responseCode = "200", description = "启动成功",
            content = @Content(schema = @Schema(implementation = StartSendRtpResult.class)))
    public StartSendRtpResult startSendRtpTalk(
            @Parameter(description = "RTP对讲配置") @RequestBody StartSendRtpTalkReq req) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.startSendRtpTalk(node.getHost(), node.getSecret(), req);
    }

    // ==================== MP4文件管理接口 ====================

    /**
     * 多文件推流
     */
    @PostMapping("/mp4/publish/start")
    @Operation(summary = "开始多文件推流", description = "开始推流多个MP4文件")
    @ApiResponse(responseCode = "200", description = "开始成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse startMultiMp4Publish(
            @Parameter(description = "推流参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.startMultiMp4Publish(node.getHost(), node.getSecret(), params);
    }

    /**
     * 关闭多文件推流
     */
    @PostMapping("/mp4/publish/stop")
    @Operation(summary = "停止多文件推流", description = "停止推流多个MP4文件")
    @ApiResponse(responseCode = "200", description = "停止成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse stopMultiMp4Publish(
            @Parameter(description = "停止参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.stopMultiMp4Publish(node.getHost(), node.getSecret(), params);
    }

    /**
     * 点播mp4文件
     */
    @PostMapping("/mp4/load")
    @Operation(summary = "点播MP4文件", description = "加载并点播指定MP4文件")
    @ApiResponse(responseCode = "200", description = "加载成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse loadMP4File(
            @Parameter(description = "文件参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.loadMP4File(node.getHost(), node.getSecret(), params);
    }

    // ==================== 存储管理接口 ====================

    /**
     * 获取存储信息
     */
    @PostMapping("/storage/space")
    @Operation(summary = "获取存储空间信息", description = "获取服务器的存储空间信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> getStorageSpace(
            @Parameter(description = "查询参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getStorageSpace(node.getHost(), node.getSecret(), params);
    }

    // ==================== 文件下载接口 ====================

    /**
     * 下载文件（下载到服务器临时目录，返回落盘绝对路径）
     */
    @GetMapping("/download/file")
    @Operation(summary = "下载文件", description = "按文件绝对路径从ZLM下载文件到本地临时目录，返回落盘路径")
    @ApiResponse(responseCode = "200", description = "下载成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> downloadFile(
            @Parameter(description = "文件绝对路径") @RequestParam("filePath") String filePath,
            @Parameter(description = "保存文件名(可选)") @RequestParam(value = "saveName", required = false) String saveName) {
        ZlmNode node = getAvailableNode();
        String name = (saveName != null && !saveName.isEmpty()) ? saveName : new File(filePath).getName();
        File target = new File(System.getProperty("java.io.tmpdir"), "zlm-download-" + System.currentTimeMillis() + "-" + name);
        String path = ZlmRestService.downloadFile(node.getHost(), node.getSecret(), filePath, saveName, target);
        return ServerResponse.success(path);
    }

    /**
     * 下载二进制（如配置文件 downloadBin，下载到服务器临时目录）
     */
    @GetMapping("/download/bin")
    @Operation(summary = "下载二进制", description = "从ZLM下载二进制(如配置文件)到本地临时目录，返回落盘路径")
    @ApiResponse(responseCode = "200", description = "下载成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> downloadBin() {
        ZlmNode node = getAvailableNode();
        File target = new File(System.getProperty("java.io.tmpdir"), "zlm-download-bin-" + System.currentTimeMillis());
        String path = ZlmRestService.downloadBin(node.getHost(), node.getSecret(), target);
        return ServerResponse.success(path);
    }

    // ==================== 多屏拼接接口 ====================

    /**
     * 多屏拼接 - 开始
     */
    @PostMapping("/stack/start")
    @Operation(summary = "多屏拼接-开始", description = "启动多屏拼接（POST JSON body）")
    @ApiResponse(responseCode = "200", description = "启动成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> stackStart(
            @Parameter(description = "拼接配置") @RequestBody StackReq req) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.stackStart(node.getHost(), node.getSecret(), req);
    }

    /**
     * 多屏拼接 - 重置
     */
    @PostMapping("/stack/reset")
    @Operation(summary = "多屏拼接-重置", description = "重置多屏拼接（POST JSON body）")
    @ApiResponse(responseCode = "200", description = "重置成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> stackReset(
            @Parameter(description = "拼接配置") @RequestBody StackReq req) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.stackReset(node.getHost(), node.getSecret(), req);
    }

    /**
     * 多屏拼接 - 停止
     */
    @DeleteMapping("/stack/{id}")
    @Operation(summary = "多屏拼接-停止", description = "根据拼接id停止多屏拼接")
    @ApiResponse(responseCode = "200", description = "停止成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> stackStop(
            @Parameter(description = "拼接id") @PathVariable("id") String id) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.stackStop(node.getHost(), node.getSecret(), id);
    }

    // ==================== WebRTC 房间管理接口 ====================

    /**
     * WebRTC 注册到信令服务器
     */
    @PostMapping("/webrtc/room-keeper/add")
    @Operation(summary = "WebRTC注册到信令服务器", description = "注册WebRTC房间守护者(server_host/server_port/room_id)")
    @ApiResponse(responseCode = "200", description = "注册成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> addWebrtcRoomKeeper(
            @Parameter(description = "注册参数") @RequestBody Map<String, String> params) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.addWebrtcRoomKeeper(node.getHost(), node.getSecret(), params);
    }

    /**
     * WebRTC 从信令服务器注销
     */
    @DeleteMapping("/webrtc/room-keeper/{roomKey}")
    @Operation(summary = "WebRTC从信令服务器注销", description = "根据room_key注销WebRTC房间守护者")
    @ApiResponse(responseCode = "200", description = "注销成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> delWebrtcRoomKeeper(
            @Parameter(description = "room_key") @PathVariable("roomKey") String roomKey) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.delWebrtcRoomKeeper(node.getHost(), node.getSecret(), roomKey);
    }

    /**
     * WebRTC 房间守护者列表
     */
    @GetMapping("/webrtc/room-keeper/list")
    @Operation(summary = "WebRTC房间守护者列表", description = "获取所有WebRTC房间守护者")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<String>> listWebrtcRoomKeepers() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.listWebrtcRoomKeepers(node.getHost(), node.getSecret());
    }

    /**
     * WebRTC 房间列表
     */
    @GetMapping("/webrtc/rooms")
    @Operation(summary = "WebRTC房间列表", description = "获取所有WebRTC房间")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<String>> listWebrtcRooms() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.listWebrtcRooms(node.getHost(), node.getSecret());
    }

    /**
     * WebRTC 代理播放器信息
     */
    @GetMapping("/webrtc/proxy-player/{key}")
    @Operation(summary = "WebRTC代理播放器信息", description = "根据key获取WebRTC代理播放器信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> getWebrtcProxyPlayerInfo(
            @Parameter(description = "key") @PathVariable("key") String key) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getWebrtcProxyPlayerInfo(node.getHost(), node.getSecret(), key);
    }

    // ==================== WebRTC 交互接口（SDP 文本）====================

    /**
     * WebRTC 交互（body 为 SDP offer，返回 SDP answer）
     */
    @PostMapping(value = "/webrtc", consumes = "text/plain", produces = "text/plain")
    @Operation(summary = "WebRTC交互", description = "WebRTC交互，body为SDP offer，返回SDP answer")
    public String webrtc(
            @Parameter(description = "type:play/push/echo") @RequestParam("type") String type,
            @Parameter(description = "应用名") @RequestParam("app") String app,
            @Parameter(description = "流id") @RequestParam("stream") String stream,
            @RequestBody String sdpOffer) {
        ZlmNode node = getAvailableNode();
        Map<String, String> query = new java.util.HashMap<>();
        query.put("type", type);
        query.put("app", app);
        query.put("stream", stream);
        return ZlmRestService.webrtc(node.getHost(), node.getSecret(), query, sdpOffer);
    }

    /**
     * WebRTC WHIP 推流（body 为 SDP offer）
     */
    @PostMapping(value = "/webrtc/whip", consumes = "application/sdp", produces = "application/sdp")
    @Operation(summary = "WebRTC WHIP推流", description = "WHIP标准推流，body为SDP offer")
    public String whip(
            @Parameter(description = "应用名") @RequestParam("app") String app,
            @Parameter(description = "流id") @RequestParam("stream") String stream,
            @RequestBody String sdpOffer) {
        ZlmNode node = getAvailableNode();
        Map<String, String> query = new java.util.HashMap<>();
        query.put("app", app);
        query.put("stream", stream);
        return ZlmRestService.whip(node.getHost(), node.getSecret(), query, sdpOffer);
    }

    /**
     * WebRTC WHEP 播放（body 为 SDP offer）
     */
    @PostMapping(value = "/webrtc/whep", consumes = "application/sdp", produces = "application/sdp")
    @Operation(summary = "WebRTC WHEP播放", description = "WHEP标准播放，body为SDP offer")
    public String whep(
            @Parameter(description = "应用名") @RequestParam("app") String app,
            @Parameter(description = "流id") @RequestParam("stream") String stream,
            @RequestBody String sdpOffer) {
        ZlmNode node = getAvailableNode();
        Map<String, String> query = new java.util.HashMap<>();
        query.put("app", app);
        query.put("stream", stream);
        return ZlmRestService.whep(node.getHost(), node.getSecret(), query, sdpOffer);
    }

    /**
     * 删除 WebRTC 连接
     */
    @DeleteMapping("/webrtc/{id}")
    @Operation(summary = "删除WebRTC连接", description = "根据id和token删除WebRTC连接")
    @ApiResponse(responseCode = "200", description = "删除成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> deleteWebrtc(
            @Parameter(description = "WebRTC连接id") @PathVariable("id") String id,
            @Parameter(description = "删除token") @RequestParam("token") String token) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.deleteWebrtc(node.getHost(), id, token);
    }

    // ==================== ONVIF 接口 ====================

    /**
     * 搜索 ONVIF 设备
     */
    @GetMapping("/onvif/search")
    @Operation(summary = "搜索ONVIF设备", description = "在指定子网前缀下搜索ONVIF设备")
    @ApiResponse(responseCode = "200", description = "搜索成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<String>> searchOnvifDevice(
            @Parameter(description = "子网前缀，例如192.168.1") @RequestParam("subnetPrefix") String subnetPrefix) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.searchOnvifDevice(node.getHost(), node.getSecret(), subnetPrefix);
    }

    /**
     * 获取 ONVIF 设备流地址
     */
    @GetMapping("/onvif/stream-url")
    @Operation(summary = "获取ONVIF设备流地址", description = "根据ONVIF设备地址获取流地址")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> getOnvifStreamUrl(
            @Parameter(description = "ONVIF设备地址") @RequestParam("onvifUrl") String onvifUrl) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.getOnvifStreamUrl(node.getHost(), node.getSecret(), onvifUrl);
    }

    // ==================== 鉴权接口 ====================

    /**
     * 登录鉴权
     */
    @GetMapping("/auth/login")
    @Operation(summary = "登录鉴权", description = "通过cookie计算digest登录")
    @ApiResponse(responseCode = "200", description = "登录成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> login(
            @Parameter(description = "cookie") @RequestParam("cookie") String cookie) {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.login(node.getHost(), node.getSecret(), cookie);
    }

    /**
     * 注销鉴权
     */
    @GetMapping("/auth/logout")
    @Operation(summary = "注销鉴权", description = "注销当前登录")
    @ApiResponse(responseCode = "200", description = "注销成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<String> logout() {
        ZlmNode node = getAvailableNode();
        return ZlmRestService.logout(node.getHost(), node.getSecret());
    }

    // ==================== 指定节点操作接口 ====================

    /**
     * 指定节点获取版本信息
     */
    @GetMapping("/node/{nodeId}/version")
    @Operation(summary = "指定节点获取版本信息", description = "获取指定ZLM节点的版本信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<Version> getVersionByNode(
            @Parameter(description = "节点ID") @PathVariable("nodeId") String nodeId) {
        ZlmNode node = nodeService.getAvailableNode(nodeId);
        return ZlmRestService.getVersion(node.getHost(), node.getSecret());
    }

    /**
     * 指定节点获取流列表
     */
    @PostMapping("/node/{nodeId}/media/list")
    @Operation(summary = "指定节点获取流列表", description = "获取指定ZLM节点中的媒体流列表")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = ServerResponse.class)))
    public ServerResponse<List<MediaData>> getMediaListByNode(
            @Parameter(description = "节点ID") @PathVariable(value = "nodeId") String nodeId,
            @Parameter(description = "媒体查询条件") @RequestBody MediaReq mediaReq) {
        ZlmNode node = nodeService.getAvailableNode(nodeId);
        return ZlmRestService.getMediaList(node.getHost(), node.getSecret(), mediaReq);
    }

    /**
     * 获取所有节点列表
     */
    @GetMapping("/nodes")
    @Operation(summary = "获取所有节点列表", description = "获取当前配置的所有ZLM节点信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = List.class)))
    public List<ZlmNode> getAllNodes() {
        return nodeSupplier.getNodes();
    }

    // ==================== 异常处理 ====================

    /**
     * 处理节点不存在异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ServerResponse<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数错误: {}", e.getMessage());
        ServerResponse<String> response = new ServerResponse<>();
        response.setCode(0);
        response.setMsg(e.getMessage());
        return response;
    }
}