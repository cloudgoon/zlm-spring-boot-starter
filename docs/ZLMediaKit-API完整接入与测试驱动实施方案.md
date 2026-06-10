# ZLMediaKit API 完整接入与测试驱动实施方案

> 目标：以测试驱动（TDD）方式补齐 `ZLMediaKit-Api.md` 中尚未接入的全部 API，并基于本地 `ffmpeg` + `src/main/resources/files` 测试视频文件，构建可重复运行的推流 / 拉流端到端验证。
>
> 适用模块：`zlm-spring-boot-starter`（主库） + `zlm-spring-boot-starter-test`（测试模块）
>
> 编写日期：2026-06-10

---

## 1. 现状盘点（API 覆盖率分析）

对照 `docs/ZLMediaKit-Api.md` 共 **70+** 个接口，逐一与 `ApiConstants` / `ZlmRestService` / `ZlmApiController` 比对，结论如下。

### 1.1 已接入（约 47 个，无需新增）

| 分类 | 已覆盖接口 |
|------|-----------|
| 系统信息 | `version`、`getApiList`、`getThreadsLoad`、`getStatistic`、`getWorkThreadsLoad`、`getServerConfig`、`setServerConfig`、`restartServer` |
| 媒体流 | `getMediaList`、`getMediaInfo`、`close_stream`、`close_streams`、`isMediaOnline`、`getMediaPlayerList`、`broadcastMessage` |
| 会话 | `getAllSession`、`kick_session`、`kick_sessions` |
| 代理 | `addStreamProxy`、`delStreamProxy`、`getProxyInfo`、`addStreamPusherProxy`、`delStreamPusherProxy`、`getProxyPusherInfo` |
| FFmpeg | `addFFmpegSource`、`delFFmpegSource` |
| 录制 | `getMp4RecordFile`、`deleteRecordDirectory`、`startRecord`、`setRecordSpeed`、`seekRecordStamp`、`stopRecord`、`isRecording`、`getMp4RecordSummary` |
| 截图 | `getSnap` |
| RTP 服务 | `getRtpInfo`、`openRtpServer`、`openRtpServerMultiplex`、`connectRtpServer`、`closeRtpServer`、`updateRtpServerSSRC`、`pauseRtpCheck`、`resumeRtpCheck`、`listRtpServer` |
| RTP 发送 | `startSendRtp`、`startSendRtpPassive`、`stopSendRtp` |
| MP4 | `loadMP4File`、`startMultiMp4Publish`、`stopMultiMp4Publish`、`getStorageSpace` |

### 1.2 待接入（约 27 个，本方案核心工作量）

按业务域分组（这是后续编码与测试的批次划分依据）：

| 批次 | 分组 | 缺失接口 | HTTP 方法 | 优先级 |
|------|------|----------|-----------|--------|
| **A** | 列表查询补全 | `listStreamProxy`、`listStreamPusherProxy`、`listFFmpegSource`、`listRtpSender` | GET | P0 |
| **B** | 录制 / 截图补全 | `startRecordTask`、`deleteSnapDirectory` | GET | P0 |
| **C** | RTP 对讲 | `startSendRtpTalk` | GET | P1 |
| **D** | 多屏拼接 | `stack/start`、`stack/reset`、`stack/stop` | POST/GET | P2 |
| **E** | WebRTC 信令 | `addWebrtcRoomKeeper`、`delWebrtcRoomKeeper`、`listWebrtcRoomKeepers`、`listWebrtcRooms`、`getWebrtcProxyPlayerInfo` | GET | P2 |
| **F** | WebRTC 交互 | `webrtc`、`whip`、`whep`、`delete_webrtc` | POST/DELETE | P2 |
| **G** | ONVIF | `searchOnvifDevice`、`getStreamUrl` | GET | P2 |
| **H** | 文件下载 | `downloadFile`、`downloadBin` | GET | P1 |
| **I** | 鉴权 | `login`、`logout` | GET | P2 |
| **J** | 探针 | `addProbe` | GET | P1 |

> 说明：`getMp4RecordSummary`、`startMultiMp4Publish`、`stopMultiMp4Publish`、`getStorageSpace` 在文档中未单列，但代码已实现，保留并补测试即可。

---

## 2. 测试驱动（TDD）总策略

遵循项目 `CLAUDE.md` 既有约定：**Java 17 + jakarta + FastJSON2 + JUnit4 + SpringRunner + Mockito**，测试统一放在 `zlm-spring-boot-starter-test` 模块（避免循环依赖）。

### 2.1 测试金字塔分层

```
        ┌─────────────────────────┐
        │  E2E 推拉流测试 (少量)    │  ← ffmpeg + 真实 ZLM，验证完整链路
        ├─────────────────────────┤
        │  集成测试 (中量)          │  ← @SpringBootTest，真实 ZLM 节点
        ├───────────────────��─────┤
        │  单元测试 (大量)          │  ← Mockito mock HttpUtils，无需 ZLM
        └─────────────────────────┘
```

### 2.2 红 → 绿 → 重构 循环（每个接口）

1. **红**：先在 `ZlmRestServiceXxxTest` 写单元测试，断言「参数被正确组装 / 响应被正确反序列化」。此时方法不存在，编译失败（红）。
2. **绿**：在 `ApiConstants` 加常量 → `ZlmRestService` 加静态方法 → 必要时加 `entity` / `req` 实体 → `ZlmApiController` 暴露 REST 端点。测试通过（绿）。
3. **重构**：复用 `executeApiCall` / `executeApiCallWithSingleParam` 模板，消除重复；补 Swagger 注解。
4. **集成验证**：在 `dev` profile 下对真实 ZLM 跑一次集成测试，连接失败时按现有 `ZlmApiIntegrationTest` 的容错模式给出友好提示（不硬失败 CI）。

### 2.3 单元测试 mock 方案

`ZlmRestService` 的所有 HTTP 出口集中在 `doApi` / `doApiImg`，二者底层调用 `com.luna.common.net.HttpUtils`。单测用 `mockito-inline` 的 `mockStatic(HttpUtils.class)` 拦截：

```java
try (MockedStatic<HttpUtils> mocked = mockStatic(HttpUtils.class)) {
    mocked.when(() -> HttpUtils.doPostHander(anyString(), eq(ApiConstants.LIST_STREAM_PROXY),
            anyMap(), anyMap(), anyString()))
          .thenReturn("{\"code\":0,\"data\":[{\"key\":\"k1\"}]}");

    ServerResponse<List<StreamProxyItem>> resp =
            ZlmRestService.listStreamProxy("http://127.0.0.1:9092", "zlm");

    assertEquals(Integer.valueOf(0), resp.getCode());
    assertEquals("k1", resp.getData().get(0).getKey());
}
```

> 需在 `zlm-spring-boot-starter-test/pom.xml` 确认存在 `mockito-inline`（静态 mock 必需）。若缺失则补：
> ```xml
> <dependency>
>     <groupId>org.mockito</groupId>
>     <artifactId>mockito-inline</artifactId>
>     <scope>test</scope>
> </dependency>
> ```

---

## 3. 待接入接口的实现设计

下表给出每个接口的「常量 + Service 签名 + 返回实体 + Controller 路由」。实体若已存在则复用，否则新增到对应包。

### 批次 A：列表查询补全（P0）

| 接口 | 常量 | Service 方法签名 | 返回实体 | Controller 路由 |
|------|------|------------------|----------|----------------|
| listStreamProxy | `LIST_STREAM_PROXY` | `ServerResponse<List<StreamProxyItem>> listStreamProxy(host, secret)` | 复用/扩展 `StreamProxyItem`（补 `key`/`status` 字段） | `GET /zlm/api/proxy/list` |
| listStreamPusherProxy | `LIST_STREAM_PUSHER_PROXY` | `ServerResponse<List<StreamPusherItem>> listStreamPusherProxy(host, secret)` | 复用 `StreamPusherItem` | `GET /zlm/api/pusher/list` |
| listFFmpegSource | `LIST_FFMPEG_SOURCE` | `ServerResponse<List<StreamFfmpegItem>> listFFmpegSource(host, secret)` | 复用 `StreamFfmpegItem` | `GET /zlm/api/ffmpeg/list` |
| listRtpSender | `LIST_RTP_SENDER` | `ServerResponse<List<String>> listRtpSender(host, secret, MediaReq)` | 复用 `MediaReq`（vhost/app/stream） | `POST /zlm/api/rtp/sender/list` |

### 批次 B：录制 / 截图补全（P0）

| 接口 | 常量 | Service 方法签名 | 入参实体 | Controller 路由 |
|------|------|------------------|----------|----------------|
| startRecordTask | `START_RECORD_TASK` | `ServerResponse<String> startRecordTask(host, secret, RecordTaskReq)` | 新增 `RecordTaskReq`（vhost/app/stream/path/back_ms/forward_ms） | `POST /zlm/api/record/task/start` |
| deleteSnapDirectory | `DELETE_SNAP_DIRECTORY` | `ServerResponse<String> deleteSnapDirectory(host, secret, Map)` | 复用 Map | `POST /zlm/api/snapshot/delete` |

### 批次 C：RTP 双向对讲（P1）

| 接口 | 常量 | Service 方法签名 | 入参实体 | Controller 路由 |
|------|------|------------------|----------|----------------|
| startSendRtpTalk | `START_SEND_RTP_TALK` | `StartSendRtpResult startSendRtpTalk(host, secret, StartSendRtpReq)` | 扩展 `StartSendRtpReq`（补 `recv_stream_id`，已有则复用 `getTalkMap()`） | `POST /zlm/api/rtp/send/start-talk` |

### 批次 D：多屏拼接（P2）

| 接口 | 常量 | Service 方法签名 | 入参实体 | Controller 路由 |
|------|------|------------------|----------|----------------|
| stack/start | `STACK_START` | `ServerResponse<String> stackStart(host, secret, StackReq)` | 新增 `StackReq`（gapv/gaph/width/height/url[][]/id/row/col/span[][][]，POST body） | `POST /zlm/api/stack/start` |
| stack/reset | `STACK_RESET` | `ServerResponse<String> stackReset(host, secret, StackReq)` | 复用 `StackReq` | `POST /zlm/api/stack/reset` |
| stack/stop | `STACK_STOP` | `ServerResponse<String> stackStop(host, secret, String id)` | 单参 `id` | `DELETE /zlm/api/stack/{id}` |

> 注意：`stack/start`、`stack/reset` 为 **POST + JSON body**，需新增以 body 形式提交的辅助方法（现有 `doApi` 是 query 提交）。新增 `doApiJson(host, path, secret, jsonBody)` 包装 `HttpUtils` 的 POST-body 调用。

### 批次 E / F：WebRTC（P2）

| 接口 | 常量 | Service 方法签名 | 返回/入参 | Controller 路由 |
|------|------|------------------|-----------|----------------|
| addWebrtcRoomKeeper | `ADD_WEBRTC_ROOM_KEEPER` | `ServerResponse<String> addWebrtcRoomKeeper(host, secret, Map)` | server_host/server_port/room_id | `POST /zlm/api/webrtc/room-keeper/add` |
| delWebrtcRoomKeeper | `DEL_WEBRTC_ROOM_KEEPER` | `ServerResponse<String> delWebrtcRoomKeeper(host, secret, String roomKey)` | room_key | `DELETE /zlm/api/webrtc/room-keeper/{roomKey}` |
| listWebrtcRoomKeepers | `LIST_WEBRTC_ROOM_KEEPERS` | `ServerResponse<List<String>> listWebrtcRoomKeepers(host, secret)` | - | `GET /zlm/api/webrtc/room-keeper/list` |
| listWebrtcRooms | `LIST_WEBRTC_ROOMS` | `ServerResponse<List<String>> listWebrtcRooms(host, secret)` | - | `GET /zlm/api/webrtc/rooms` |
| getWebrtcProxyPlayerInfo | `GET_WEBRTC_PROXY_PLAYER_INFO` | `ServerResponse<String> getWebrtcProxyPlayerInfo(host, secret, String key)` | key | `GET /zlm/api/webrtc/proxy-player/{key}` |
| webrtc | `WEBRTC` | `String webrtc(host, secret, Map query, String sdpOffer)` | body=SDP, query=type/app/stream | `POST /zlm/api/webrtc` |
| whip | `WHIP` | `String whip(host, secret, Map query, String sdpOffer)` | body=SDP | `POST /zlm/api/webrtc/whip` |
| whep | `WHEP` | `String whep(host, secret, Map query, String sdpOffer)` | body=SDP | `POST /zlm/api/webrtc/whep` |
| delete_webrtc | `DELETE_WEBRTC` | `ServerResponse<String> deleteWebrtc(host, id, token)` | id/token | `DELETE /zlm/api/webrtc/{id}` |

> `webrtc`/`whip`/`whep` 为 SDP 文本 body，返回也是 SDP 文本，使用 `String` 出入参，复用 `doApiJson` 的文本提交变体（`Content-Type` 透传）。

### 批次 G：ONVIF（P2）

| 接口 | 常量 | Service 方法签名 | Controller 路由 |
|------|------|------------------|----------------|
| searchOnvifDevice | `SEARCH_ONVIF_DEVICE` | `ServerResponse<List<String>> searchOnvifDevice(host, secret, String subnetPrefix)` | `GET /zlm/api/onvif/search` |
| getStreamUrl | `GET_ONVIF_STREAM_URL` | `ServerResponse<String> getOnvifStreamUrl(host, secret, String onvifUrl)` | `GET /zlm/api/onvif/stream-url` |

### 批次 H：文件下载（P1）

| 接口 | 常量 | Service 方法签名 | 说明 | Controller 路由 |
|------|------|------------------|------|----------------|
| downloadFile | `DOWNLOAD_FILE` | `String downloadFile(host, secret, String filePath, String saveName, File target)` | 复用 `doApiImg` 的字节流落盘逻辑 | `GET /zlm/api/download/file` |
| downloadBin | `DOWNLOAD_BIN` | `String downloadBin(host, secret, File target)` | 同上 | `GET /zlm/api/download/bin` |

### 批次 I：鉴权（P2）

| 接口 | 常量 | Service 方法签名 | 说明 |
|------|------|------------------|------|
| login | `LOGIN` | `ServerResponse<String> login(host, secret, String cookie)` | digest = `MD5("zlmediakit:"+secret+":"+cookie)`，需 `DigestUtils` 计算 |
| logout | `LOGOUT` | `ServerResponse<String> logout(host, secret)` | - |

### 批次 J：探针（P1）

| 接口 | 常量 | Service 方法签名 | 入参 | Controller 路由 |
|------|------|------------------|------|----------------|
| addProbe | `ADD_PROBE` | `ServerResponse<String> addProbe(host, secret, MediaReq, int probeMs)` | vhost/app/stream/probe_ms | `POST /zlm/api/probe/add` |

---

## 4. 推流 / 拉流 端到端测试设计（核心验证）

利用本地 `ffmpeg 7.1.1`（`/usr/local/bin/ffmpeg`）与测试素材：

| 文件 | 用途 |
|------|------|
| `src/main/resources/files/record.mp4` (7.6MB) | 推流源（含音视频） |
| `src/main/resources/files/invite.mp4` (7.6MB) | 备用推流源 / 拉流代理目标 |
| `src/main/resources/files/videofile.h264` (4.2MB) | 裸 H264 推流源 |

### 4.1 测试前置开关（避免无 ZLM 环境时 CI 失败）

新增配置项控制 E2E 测试启停：

```yaml
# application-dev.yml
zlm:
  test:
    ffmpeg-path: /usr/local/bin/ffmpeg
    files-dir: src/main/resources/files
    e2e-enabled: true        # CI 默认 false，本地联调置 true
    rtsp-push-url: rtsp://127.0.0.1:554/live
    rtmp-push-url: rtmp://127.0.0.1:1935/live
```

测试类用 `@EnabledIfSystemProperty` / `Assume.assumeTrue(e2eEnabled)` 守卫。

### 4.2 推流测试链路（FfmpegPushStreamTest）

```
[本地ffmpeg] --RTSP/RTMP推流--> [ZLM:554/1935]
                                     │
       ┌─────────────────────────────┤
   getMediaList                  isMediaOnline
   断言流出现                     断言 online=true
                                     │
                                close_stream 收尾 + 销毁 ffmpeg 进程
```

测试步骤（每个用例独立 setUp/tearDown）：

1. **推流**：用 `ProcessBuilder` 启动 ffmpeg，循环推本地文件：
   ```bash
   ffmpeg -re -stream_loop -1 -i record.mp4 -c copy -f rtsp -rtsp_transport tcp rtsp://127.0.0.1:554/live/tdd_test
   # RTMP 变体：
   ffmpeg -re -stream_loop -1 -i record.mp4 -c copy -f flv rtmp://127.0.0.1:1935/live/tdd_test
   # 裸H264变体（需指定帧率）：
   ffmpeg -re -stream_loop -1 -r 25 -i videofile.h264 -c copy -f rtsp rtsp://127.0.0.1:554/live/tdd_h264
   ```
2. **轮询等待**：调用 `isMediaOnline` 最多重试 N 次（间隔 1s，超时 15s），直到 `online=true`。
3. **断言**：
   - `getMediaList` 返回列表包含 `app=live, stream=tdd_test`；
   - `getMediaInfo` 返回 `tracks` 非空，含 video track；
   - `getMediaPlayerList` 可正常返回（0 播放器也算成功）。
4. **截图验证**：流稳定后调用 `getSnap`，断言落盘文件 `length > 0`。
5. **录制验证**：`startRecord(type=1 mp4)` → 等待 5s → `isRecording` 断言 true → `stopRecord` → `getMp4RecordFile` 断言生成文件。
6. **tearDown**：`close_stream(force=1)`；`process.destroyForcibly()`；清理临时截图 / 录制文件。

### 4.3 拉流代理测试链路（StreamProxyE2ETest）

ZLM 自身去拉一路流（两种方式）：

- **方式一 addStreamProxy**：先用 ffmpeg 推一路 `live/source`，再 `addStreamProxy(url=rtsp://127.0.0.1:554/live/source, app=proxy, stream=p1)`，断言 `listStreamProxy` 含新代理、`isMediaOnline(proxy/p1)=true`，最后 `delStreamProxy(key)`。
- **方式二 addFFmpegSource**：`addFFmpegSource(src_url=本地文件或rtsp, dst_url=rtmp://127.0.0.1/live/ff1)`，断言 `listFFmpegSource` 含该源，`delFFmpegSource(key)` 收尾。

> ZLM 默认 `ffmpeg.bin` 指向其容器内 ffmpeg；`addFFmpegSource` 是 ZLM 侧拉流，本地 ffmpeg 仅用于「方式一」造源。

### 4.4 MP4 点播测试（LoadMp4FileE2ETest）

将 `record.mp4` 绝对路径传给 `loadMP4File(file_path=..., app=vod, stream=m1)`，断言 `isMediaOnline(vod/m1)=true` 后 `close_stream` 收尾。验证 `loadMP4File` 真实可用。

### 4.5 ffmpeg 进程工具类（测试基础设施）

新增 `zlm-spring-boot-starter-test/src/test/java/.../support/FfmpegTestHelper.java`：

```java
public class FfmpegTestHelper {
    public static Process pushRtsp(String ffmpeg, String file, String url) { /* ProcessBuilder + inheritIO */ }
    public static Process pushRtmp(String ffmpeg, String file, String url) { /* ... */ }
    public static void stop(Process p) { if (p != null && p.isAlive()) p.destroyForcibly(); }
    public static boolean isFfmpegAvailable(String ffmpeg) { /* exec -version */ }
}
```

并提供轮询断言工具 `awaitOnline(controller, mediaReq, timeoutSec)`。

---

## 5. 分阶段实施计划（Stage 划分）

> 按 `CLAUDE.md` 的「3-5 个阶段 + 每阶段编译通过 + 测试通过」推进。每个 Stage 末尾执行 `(cd zlm-spring-boot-starter && mvn clean install -DskipTests)` 保证 `-SNAPSHOT` 可被 voglander 消费。

### Stage 1：测试基础设施 + 列表/录制/探针补全（批次 A、B、J）
- **目标**：搭好 mock 单测脚手架 + ffmpeg 工具类；补齐 P0 列表与录制接口。
- **产物**：`FfmpegTestHelper`、`ZlmRestServiceListTest`、`RecordTaskReq`、批次 A/B/J 全部接口 + 单测。
- **成功标准**：`mvn test -Dtest=ZlmRestServiceListTest` 全绿；新增接口单测覆盖参数组装与反序列化。
- **状态**：未开始

### Stage 2：推拉流 E2E 测试落地（核心验证）
- **目标**：基于 ffmpeg + 本地文件跑通推流、拉流代理、MP4 点播、截图、录制全链路。
- **产物**：`FfmpegPushStreamTest`、`StreamProxyE2ETest`、`LoadMp4FileE2ETest`，`application-dev.yml` 增加 `zlm.test.*` 配置。
- **成功标准**：本地起 ZLM 后 `mvn test -Dtest=FfmpegPushStreamTest -Dspring.profiles.active=dev` 全绿；无 ZLM 时用例被 `assumeTrue` 跳过而非失败。
- **状态**：未开始

### Stage 3：RTP 对讲 + 文件下载（批次 C、H）
- **目标**：补 `startSendRtpTalk`、`downloadFile`、`downloadBin`。
- **产物**：对应 Service/Controller + 单测（下载类用 mock 字节流断言落盘）。
- **成功标准**：单测全绿；`downloadFile` 集成测试可下载已录制 mp4。
- **状态**：未开始

### Stage 4：多屏拼接 + WebRTC + ONVIF + 鉴权（批次 D、E、F、G、I）
- **目标**：补齐剩余 P2 接口，新增 `doApiJson` body 提交能力与 `StackReq` 实体，`login` 的 MD5 digest 计算。
- **产物**：批次 D/E/F/G/I 全部接口 + 单测（WebRTC 用 mock SDP 文本，stack 用 mock body 提交）。
- **成功标准**：单测全绿；`getApiList` 实际返回的接口名与本库已接入常量做交叉核对（新增一个 `ApiCoverageTest` 自动比对）。
- **状态**：未开始

### Stage 5：覆盖率校验 + 文档收尾
- **目标**：跑 JaCoCo 覆盖率；新增 `ApiCoverageTest` 断言「文档列出的接口 100% 有对应常量」；更新 README/CLAUDE.md 接口清单。
- **产物**：`mvn test jacoco:report`；覆盖率报告；本方案勾选完成。
- **成功标准**：`ZlmRestService` 行覆盖率 ≥ 80%；`ApiCoverageTest` 绿。
- **状态**：未开始

---

## 6. 接口覆盖自动校验（防回归）

新增 `ApiCoverageTest`，从 `ApiConstants` 反射收集所有 `API_INDEX + "/xxx"` 常量，与一份「文档接口清单」常量数组比对，确保新增文档接口时不会漏接：

```java
@Test
public void allDocumentedApisHaveConstant() {
    Set<String> implemented = collectConstantPaths(ApiConstants.class); // 反射
    Set<String> documented = Set.of(
        "getApiList","stack/start","stack/reset","stack/stop","listStreamProxy",
        "listStreamPusherProxy","listFFmpegSource","listRtpSender","startRecordTask",
        "deleteSnapDirectory","startSendRtpTalk","addWebrtcRoomKeeper","delWebrtcRoomKeeper",
        "listWebrtcRoomKeepers","listWebrtcRooms","getWebrtcProxyPlayerInfo","webrtc",
        "whip","whep","delete_webrtc","searchOnvifDevice","getStreamUrl","downloadFile",
        "downloadBin","login","logout","addProbe" /* ...全量 */);
    Set<String> missing = new TreeSet<>(documented);
    missing.removeAll(implemented);
    assertTrue("以下文档接口尚未接入: " + missing, missing.isEmpty());
}
```

> 该测试在 Stage 4/5 完成后转为「全绿守门」，后续 ZLM 升级新增接口时第一时间暴露缺口。

---

## 7. 验收清单（Definition of Done）

- [ ] 文档列出的全部接口在 `ApiConstants` 有常量、`ZlmRestService` 有方法、关键接口在 `ZlmApiController` 有路由
- [ ] 每个新增接口至少 1 个 mock 单元测试（参数组装 + 反序列化）
- [ ] 推流（RTSP/RTMP/H264）、拉流代理、MP4 点播、截图、录制 5 条 E2E 链路在本地真实 ZLM 跑通
- [ ] 无 ZLM 环境时 E2E 用例自动跳过，不阻塞 CI
- [ ] `ApiCoverageTest` 绿（接口零缺口）
- [ ] `ZlmRestService` JaCoCo 行覆盖率 ≥ 80%
- [ ] `mvn clean install` 通过，`-SNAPSHOT` 已安装到本地 `~/.m2`
- [ ] 代码遵循 jakarta / FastJSON2 / @Slf4j / TAB-XML 规范，已用 `ali-code-style.xml` 格式化

---

## 8. 风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| 本地无 ZLM 服务器 | E2E 测试无法运行 | `assumeTrue` 守卫 + 单测用 mock 全覆盖逻辑 |
| ffmpeg 推流端口/协议与 ZLM 配置不符 | 流推不上去 | E2E 先调 `getServerConfig` 读取真实 rtsp/rtmp 端口再拼 URL |
| `stack`/`webrtc` 为 POST body，与现有 query 提交模型不同 | 需改造 HTTP 出口 | 新增 `doApiJson` 不影响现有 `doApi` |
| `login` 需 MD5 digest | 易算错 | 用 `commons-codec DigestUtils`，单测对拍已知 digest |
| ffmpeg 子进程未清理 | 端口占用 / 资源泄漏 | `@After` 强制 `destroyForcibly()` + try-finally |
| WebRTC 需真实 SDP 协商 | 难做真链路 E2E | 仅做接口连通性（mock SDP）+ 真实环境手测，不纳入自动化断言 |

---

## 附录：关键代码落点速查

| 类型 | 路径 |
|------|------|
| API 常量 | `src/main/java/io/github/lunasaw/zlm/constant/ApiConstants.java` |
| REST 调用 | `src/main/java/io/github/lunasaw/zlm/api/ZlmRestService.java` |
| Controller | `src/main/java/io/github/lunasaw/zlm/api/controller/ZlmApiController.java` |
| 请求实体 | `src/main/java/io/github/lunasaw/zlm/entity/req/`（新增 `RecordTaskReq`、`StackReq`） |
| 响应实体 | `src/main/java/io/github/lunasaw/zlm/entity/` |
| 测试（单元） | `zlm-spring-boot-starter-test/src/test/java/io/github/lunasaw/zlm/` |
| 测试（基础设施） | 新增 `.../support/FfmpegTestHelper.java` |
| 测试素材 | `src/main/resources/files/{record,invite}.mp4`、`videofile.h264` |
| 测试配置 | `zlm-spring-boot-starter-test/src/main/resources/application-dev.yml` |
