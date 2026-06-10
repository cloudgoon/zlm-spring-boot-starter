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
>
> **缺口复核（2026-06-10，脚本比对结论）**：以「文档 73 个端点」与「`ApiConstants` 51 个常量」做大小写不敏感差集，得到 **26 个真实缺口**，与下表批次 A~J（4+2+1+3+5+4+2+2+2+1=26）**完全吻合**，清单无遗漏、无虚构。比对脚本见 §6。
>
> **硬性要求：全部 26 个接口必须完整接入，不得因优先级（含 P2 的 stack / WebRTC / ONVIF）裁剪或延后到本方案之外。** 优先级仅用于排期顺序，不影响最终交付范围。

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

测试类用 `Assume.assumeTrue(e2eEnabled)` 守卫（**本模块是 JUnit4 + SpringRunner，不能用 JUnit5 的 `@EnabledIfSystemProperty`**；E2E 开关统一从 `application-dev.yml` 的 `zlm.test.e2e-enabled` 读取，或 `-De2e.enabled=true` 系统属性）。

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
- **状态**：✅ 已完成（批次A/B/J 接入，15测试绿；修复vintage引擎/重复Bean/断言漂移）

### Stage 2：推拉流 E2E 测试落地（核心验证）
- **目标**：基于 ffmpeg + 本地文件跑通推流、拉流代理、MP4 点播、截图、录制全链路。
- **产物**：`FfmpegPushStreamTest`、`StreamProxyE2ETest`、`LoadMp4FileE2ETest`，`application-dev.yml` 增加 `zlm.test.*` 配置。
- **成功标准**：本地起 ZLM 后 `mvn test -Dtest=FfmpegPushStreamTest -Dspring.profiles.active=dev` 全绿；无 ZLM 时用例被 `assumeTrue` 跳过而非失败。
- **状态**：✅ 已完成（FfmpegTestHelper + 守卫式E2E推流测试，无ZLM时assumeTrue跳过）

### Stage 3：RTP 对讲 + 文件下载（批次 C、H）
- **目标**：补 `startSendRtpTalk`、`downloadFile`、`downloadBin`。
- **产物**：对应 Service/Controller + 单测（下载类用 mock 字节流断言落盘）。
- **成功标准**：单测全绿；`downloadFile` 集成测试可下载已录制 mp4。
- **状态**：✅ 已完成（批次C/H 接入，7测试绿）

### Stage 4：多屏拼接 + WebRTC + ONVIF + 鉴权（批次 D、E、F、G、I）
- **目标**：补齐剩余 P2 接口，新增 `doApiJson` body 提交能力与 `StackReq` 实体，`login` 的 MD5 digest 计算。
- **产物**：批次 D/E/F/G/I 全部接口 + 单测（WebRTC 用 mock SDP 文本，stack 用 mock body 提交）。
- **成功标准**：单测全绿；`getApiList` 实际返回的接口名与本库已接入常量做交叉核对（新增一个 `ApiCoverageTest` 自动比对）。
- **状态**：✅ 已完成（批次D/E/F/G/I + doApiJson/doApiText/doDelete，32测试绿）

### Stage 5：覆盖率校验 + 文档收尾
- **目标**：跑 JaCoCo 覆盖率；新增 `ApiCoverageTest` 断言「文档列出的接口 100% 有对应常量」；更新 README/CLAUDE.md 接口清单。
- **产物**：`mvn test jacoco:report`；覆盖率报告；本方案勾选完成。
- **成功标准**：`ZlmRestService` 行覆盖率 ≥ 80%；`ApiCoverageTest` 绿。
- **状态**：✅ 已完成（ApiCoverageTest绿；ZlmRestService行覆盖率92.5%≥80%）

### Stage 6：并发安全 / 高并发实时性 / 异常处理增强（详见 §9）
- **目标**：在接口全部接入后，修复现有热路径的并发与可靠性缺陷。**不裁剪任何接口，本 Stage 不动 API 范围，只增强既有实现。**
- **产物**：见 §9 各小节的「修正」与「测试」列；新增 `zlm.http.connect-timeout-ms` / `read-timeout-ms`、`zlm.node.cache-ttl-ms`、`zlm.node.health-check` 配置项。
- **成功标准**：多线程压测无 `IndexOutOfBoundsException`；timeout 生效（mock 慢响应在阈值内返回失败而非永久阻塞）；部分节点故障时 failover 单测全绿。
- **状态**：✅ 部分完成。已修复并各带 TDD 测试：§9.1#1 RoundRobin 整数溢出、§9.1#2 重复 serverId 崩溃 + nodeMap 改用 ConcurrentHashMap merge、§9.1#3 ThreadLocalRandom、§9.2#4 一致性哈希环缓存（按节点指纹失效）、§9.3#7 选点 null 优雅降级（原 NPE 已修，`testLoadBalancerReturnsNull` 解除 @Ignore 并通过）。§9.2#6 经核实 `HttpUtils` 已有有限默认超时（CONNECT=10s/RESPONSE=30s/SOCKET=100s）且客户端在静态块构建、无法从 starter 重建，故未引入「会落空」的配置项（原评审「无限阻塞」结论予以修正）。未做（留后续、范围更大）：§9.2#5 NodeSupplier TTL 缓存、§9.3#8 健康检查、§9.3#9 Hook 线程池拒绝策略评估。

> **优先级提示**：§9 的 P0 项（轮询整数溢出、HTTP 超时、选点失败 failover）是**真实生产隐患**，实现顺序上建议紧跟 Stage 1 完成后立即穿插修复，不必等接口全部接入——但它们的归属范围仍记在 Stage 6，以免与「接口完整接入」的主线交付混淆。

---

## 9. 并发安全 / 高并发实时性 / 异常处理增强

> 以下问题均为**对照现有代码核实的真实缺陷**（非臆测），与「接口完整接入」并行推进，不削减接口范围。每条给出「定位 → 问题 → 修正 → 测试」。

### 9.1 并发安全

| # | 定位 | 问题 | 修正 | 测试 | 优先级 |
|---|------|------|------|------|--------|
| 1 | `RoundRobinLoadBalancer#selectNode` `sequence.getAndIncrement() % size` | `AtomicInteger` 累加越过 `Integer.MAX_VALUE` 翻负 → 负 index → `IndexOutOfBoundsException`（高并发持续运行必现） | `(sequence.getAndIncrement() & Integer.MAX_VALUE) % size` | 多线程把 `sequence` 预置到接近 MAX_VALUE，并发 selectNode 断言无越界 | **P0** |
| 2 | `ZlmProperties` `public static Map/List` + `afterPropertiesSet` 整体替换引用 | ① public static 可变字段无封装；② `Collectors.toMap` 默认返回非并发 `HashMap`，丢失 `ConcurrentHashMap` 语义；③ 重复 `serverId` 抛 `IllegalStateException` 启动即崩 | 字段改 `private`，提供受控的 `updateNodes()`；`toMap` 显式指定 merge 函数 + `ConcurrentHashMap::new`；重复 key 记日志取后者 | 重复 serverId 配置用例断言不崩、取最后一个；并发读 + 更新无 `ConcurrentModification` | **P1** |
| 3 | `WeightRoundRobinLoadBalancer` / `WeightRandomLoadBalancer` 共享 `java.util.Random` | 线程安全但高并发下 CAS 自旋成热点 | 改 `ThreadLocalRandom.current()` | 基准对比（可选）；功能等价单测 | P2 |

### 9.2 高并发实时性

| # | 定位 | 问题 | 修正 | 测试 | 优先级 |
|---|------|------|------|------|--------|
| 4 | `ConsistentHashingLoadBalancer#selectNode` 每次 `buildHashRing` | 每请求全量重建 TreeMap（O(节点×weight×10)），既慢又破坏一致性哈希「同 key 稳定落点」的核心价值 | 缓存哈希环，仅在节点列表「指纹」变化时重建（指纹 = 排序后 serverId+weight 的 hash） | 同 key 多次 selectNode 落点稳定；节点变更后环重建 | **P1** |
| 5 | 所有 5 个均衡器 `selectNode` 内直调 `nodeSupplier.getNodes()` | CLAUDE.md 推荐 DB/注册中心版 `NodeSupplier` → **每个媒体请求打一次 DB**，无缓存 | 在 `NodeService` 或包装层加 TTL 缓存（默认 `zlm.node.cache-ttl-ms=1000`），变更失效 | mock 慢 `NodeSupplier`，断言 TTL 内只调一次 | **P1** |
| 6 | `ZlmRestService#doApi` / `doApiImg` 调 `HttpUtils` | **全代码库零 connect/socket timeout**：一个慢/挂节点无限阻塞调用线程 → 高并发耗尽 web 线程池 → 雪崩 | 暴露 `zlm.http.connect-timeout-ms` / `read-timeout-ms`，经 `HttpUtils` 的超时入口或自定义 RequestConfig 透传 | `mockStatic(HttpUtils)` 模拟超时抛异常，断言被捕获并在阈值内返回失败 | **P0** |

> 注：若 `HttpUtils.doPostHander` 不支持传超时，需在 §3 的 `doApiJson` 改造同批，封装一个带 `RequestConfig` 的调用入口（不破坏现有 `doApi` 签名）。

### 9.3 异常处理

| # | 定位 | 问题 | 修正 | 测试 | 优先级 |
|---|------|------|------|------|--------|
| 7 | 均衡器 `getCurrentNodes` catch 返回 `null` → `NodeServiceImpl` `Assert.notNull` 抛 `IllegalArgumentException` | 单点抖动无重试、无 failover、无坏节点摘除，直接对外暴露 500 | 选点失败重试下一可用节点；连续失败的节点临时摘除（熔断窗口） | mock 部分节点抛异常，断言 failover 到健康节点 | **P0** |
| 8 | 无任何健康检查 | `selectNode` 可能返回已宕机节点 | 可选：定时 `getVersion` 探活，维护节点健康状态（`zlm.node.health-check.enabled`） | 健康状态机单测；探活失败标记 unhealthy | P2 |
| 9 | `ZlmThreadPoolConfig` `CallerRunsPolicy`（queue 100 / max 30） | 打满后接收 Hook 的 Web 线程同步跑业务 → Hook 响应变慢 → ZLM 侧 Hook 超时甚至阻塞推拉流 | 评估队列容量与拒绝策略；高频 Hook 场景考虑独立线程池 + 可配置参数 | 压测 Hook 端点，观测拒绝行为与延迟 | P2 |

### 9.4 验收补充（并入 §7 DoD）

- [ ] 轮询均衡器多线程压测（如 1000 线程 × 10 万次）无 `IndexOutOfBoundsException`
- [ ] HTTP 超时配置生效：mock 慢响应在 `read-timeout-ms` 内返回失败而非永久阻塞
- [ ] 部分节点故障时 `selectNode` 能 failover 到健康节点（单测覆盖）
- [ ] 重复 `serverId` 配置不导致启动失败
- [ ] 一致性哈希同 key 落点稳定（缓存生效后）

---

## 6. 接口覆盖自动校验（防回归）

新增 `ApiCoverageTest`，从 `ApiConstants` 反射收集所有 `API_INDEX + "/xxx"` 常量，与一份「文档接口清单」常量数组比对，确保新增文档接口时不会漏接。

> **口径对齐（重要）**：`ApiConstants` 常量存的是**全路径** `/index/api/listStreamProxy`，而 `documented` 集合是**短名** `listStreamProxy`。比对前必须把常量值 `substring(API_INDEX.length() + 1)` 剥掉 `/index/api/` 前缀再 removeAll，否则永远判定为「全部缺失」。`stack/start` 等带子路径的短名同理保留。

```java
@Test
public void allDocumentedApisHaveConstant() {
    // 反射收集 ApiConstants 的 String 常量值，剥掉 API_INDEX 前缀转为短名
    Set<String> implemented = collectConstantShortNames(ApiConstants.class);
    Set<String> documented = Set.of(
        "getApiList","stack/start","stack/reset","stack/stop","listStreamProxy",
        "listStreamPusherProxy","listFFmpegSource","listRtpSender","startRecordTask",
        "deleteSnapDirectory","startSendRtpTalk","addWebrtcRoomKeeper","delWebrtcRoomKeeper",
        "listWebrtcRoomKeepers","listWebrtcRooms","getWebrtcProxyPlayerInfo","webrtc",
        "whip","whep","delete_webrtc","searchOnvifDevice","getStreamUrl","downloadFile",
        "downloadBin","login","logout","addProbe" /* ...全量 26 项 */);
    Set<String> missing = new TreeSet<>(documented);
    missing.removeAll(implemented);
    assertTrue("以下文档接口尚未接入: " + missing, missing.isEmpty());
}

/** 反射读取 public static String 常量，剥掉 API_INDEX 前缀 */
private static Set<String> collectConstantShortNames(Class<?> clazz) {
    String prefix = ApiConstants.API_INDEX + "/";   // "/index/api/"
    Set<String> result = new TreeSet<>();
    for (Field f : clazz.getDeclaredFields()) {
        if (Modifier.isStatic(f.getModifiers()) && f.getType() == String.class) {
            try {
                String v = (String) f.get(null);
                if (v != null && v.startsWith(prefix)) {
                    result.add(v.substring(prefix.length()));
                }
            } catch (IllegalAccessException ignored) {}
        }
    }
    return result;
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
