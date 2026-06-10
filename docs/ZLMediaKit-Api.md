---
title: ZLMediaKit
language_tabs:
  - shell: Shell
  - http: HTTP
  - javascript: JavaScript
  - ruby: Ruby
  - python: Python
  - php: PHP
  - java: Java
  - go: Go
toc_footers: []
includes: []
search: true
code_clipboard: true
highlight_theme: darkula
headingLevel: 2
generator: "@tarslib/widdershins v4.0.30"

---

# ZLMediaKit

媒体服务器

Base URLs:

# Authentication

# Default

## GET 获取服务器api列表(getApiList)

GET /index/api/getApiList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 关闭多屏拼接(stack/stop)

GET /index/api/stack/stop

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|id|query|string| 是 |多屏拼接id|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## POST 重置多屏拼接(stack/reset)

POST /index/api/stack/reset

> Body 请求参数

```json
{
    "gapv": 0.002,
    "gaph": 0.001,
    "width": 1920,
    "url": [
        [
            "rtsp://kkem.me/live/test3",
            "rtsp://kkem.me/live/cy1",
            "rtsp://kkem.me/live/cy1",
            "rtsp://kkem.me/live/cy2"
        ],
        [
            "rtsp://kkem.me/live/cy1",
            "rtsp://kkem.me/live/cy5",
            "rtsp://kkem.me/live/cy3",
            "rtsp://kkem.me/live/cy4"
        ],
        [
            "rtsp://kkem.me/live/cy5",
            "rtsp://kkem.me/live/cy6",
            "rtsp://kkem.me/live/cy7",
            "rtsp://kkem.me/live/cy8"
        ],
        [
            "rtsp://kkem.me/live/cy9",
            "rtsp://kkem.me/live/cy10",
            "rtsp://kkem.me/live/cy11",
            "rtsp://kkem.me/live/cy12"
        ]
    ],
    "id": "89",
    "row": 4,
    "col": 4,
    "height": 1080,
    "span": [
        [
            [
                0,
                0
            ],
            [
                1,
                1
            ]
        ],
        [
            [
                3,
                0
            ],
            [
                3,
                1
            ]
        ],
        [
            [
                2,
                3
            ],
            [
                3,
                3
            ]
        ]
    ]
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|body|body|object| 否 |none|
|» gapv|body|number| 是 |none|
|» gaph|body|number| 是 |none|
|» width|body|integer| 是 |none|
|» url|body|[array]| 是 |none|
|» id|body|string| 是 |none|
|» row|body|integer| 是 |none|
|» col|body|integer| 是 |none|
|» height|body|integer| 是 |none|
|» span|body|[array]| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## POST 添加多屏拼接(stack/start)

POST /index/api/stack/start

> Body 请求参数

```json
{
    "gapv": 0.002,
    "gaph": 0.001,
    "width": 1920,
    "url": [
        [
            "rtsp://kkem.me/live/test3",
            "rtsp://kkem.me/live/cy1",
            "rtsp://kkem.me/live/cy1",
            "rtsp://kkem.me/live/cy2"
        ],
        [
            "rtsp://kkem.me/live/cy1",
            "rtsp://kkem.me/live/cy5",
            "rtsp://kkem.me/live/cy3",
            "rtsp://kkem.me/live/cy4"
        ],
        [
            "rtsp://kkem.me/live/cy5",
            "rtsp://kkem.me/live/cy6",
            "rtsp://kkem.me/live/cy7",
            "rtsp://kkem.me/live/cy8"
        ],
        [
            "rtsp://kkem.me/live/cy9",
            "rtsp://kkem.me/live/cy10",
            "rtsp://kkem.me/live/cy11",
            "rtsp://kkem.me/live/cy12"
        ]
    ],
    "id": "89",
    "row": 4,
    "col": 4,
    "height": 1080,
    "span": [
        [
            [
                0,
                0
            ],
            [
                1,
                1
            ]
        ],
        [
            [
                3,
                0
            ],
            [
                3,
                1
            ]
        ],
        [
            [
                2,
                3
            ],
            [
                3,
                3
            ]
        ]
    ]
}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|body|body|object| 否 |none|
|» gapv|body|number| 是 |none|
|» gaph|body|number| 是 |none|
|» width|body|integer| 是 |none|
|» url|body|[array]| 是 |none|
|» id|body|string| 是 |none|
|» row|body|integer| 是 |none|
|» col|body|integer| 是 |none|
|» height|body|integer| 是 |none|
|» span|body|[array]| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取网络线程负载(getThreadsLoad)

GET /index/api/getThreadsLoad

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取主要对象个数(getStatistic)

GET /index/api/getStatistic

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取后台线程负载(getWorkThreadsLoad)

GET /index/api/getWorkThreadsLoad

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取服务器配置(getServerConfig)

GET /index/api/getServerConfig

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 设置服务器配置(setServerConfig)

GET /index/api/setServerConfig

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|api.apiDebug|query|string| 是 |配置键与配置项值|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 重启服务器(restartServer)

GET /index/api/restartServer

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取流列表(getMediaList)

GET /index/api/getMediaList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|schema|query|string| 否 |筛选协议，例如 rtsp或rtmp|
|vhost|query|string| 否 |筛选虚拟主机，例如__defaultVhost__|
|app|query|string| 否 |筛选应用名，例如 live|
|stream|query|string| 否 |筛选流id，例如 test|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 删除截图(deleteSnapDirectory)

GET /index/api/deleteSnapDirectory

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |筛选虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |筛选应用名，例如 live|
|stream|query|string| 是 |筛选流id，例如 test|
|file|query|string| 否 |文件名，非必选|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 关断单个流(close_stream)

GET /index/api/close_stream

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|schema|query|string| 是 |协议，例如 rtsp或rtmp|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 test|
|force|query|string| 否 |是否强制关闭(有人在观看是否还关闭)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 批量关断流(close_streams)

GET /index/api/close_streams

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|schema|query|string| 是 |协议，例如 rtsp或rtmp|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 test|
|force|query|string| 否 |是否强制关闭(有人在观看是否还关闭)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取Session列表(getAllSession)

GET /index/api/getAllSession

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|local_port|query|string| 否 |筛选本机端口，例如筛选rtsp链接：554|
|peer_ip|query|string| 否 |筛选客户端ip|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 断开tcp连接(kick_session)

GET /index/api/kick_session

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|id|query|string| 是 |客户端唯一id，可以通过getAllSession接口获取|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 批量断开tcp连接(kick_sessions)

GET /index/api/kick_sessions

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|local_port|query|string| 否 |筛选本机端口，例如筛选rtsp链接：554|
|peer_ip|query|string| 否 |筛选客户端ip|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 添加拉流代理(addStreamProxy)

GET /index/api/addStreamProxy

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |添加的流的虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |添加的流的应用名，例如live|
|stream|query|string| 是 |添加的流的id名，例如test|
|url|query|string| 是 |拉流地址，支持rtsp/rtmp/hls/srt/http-flv/http-ts协议|
|rtp_type|query|string| 否 |rtsp拉流时，拉流方式，0：tcp，1：udp，2：组播|
|timeout_sec|query|string| 否 |拉流超时时间，单位秒，float类型|
|retry_count|query|string| 否 |拉流重试次数,不传此参数或传值<=0时，则无限重试|
|enable_hls|query|string| 否 |是否转hls-ts|
|enable_hls_fmp4|query|string| 否 |是否转hls-fmp4|
|enable_mp4|query|string| 否 |是否mp4录制|
|enable_rtsp|query|string| 否 |是否转协议为rtsp/webrtc|
|enable_rtmp|query|string| 否 |是否转协议为rtmp/flv|
|enable_ts|query|string| 否 |是否转协议为http-ts/ws-ts|
|enable_fmp4|query|string| 否 |是否转协议为http-fmp4/ws-fmp4|
|enable_audio|query|string| 否 |转协议是否开启音频|
|add_mute_audio|query|string| 否 |转协议无音频时，是否添加静音aac音频|
|mp4_save_path|query|string| 否 |mp4录制保存根目录，置空使用默认目录|
|mp4_max_second|query|string| 否 |mp4录制切片大小，单位秒|
|hls_save_path|query|string| 否 |hls保存根目录，置空使用默认目录|
|modify_stamp|query|string| 否 |是否修改原始时间戳，默认值2；取值范围：0.采用源视频流绝对时间戳，不做任何改变;1.采用zlmediakit接收数据时的系统时间戳(有平滑处理);2.采用源视频流时间戳相对时间戳(增长量)，有做时间戳跳跃和回退矫正|
|auto_close|query|string| 否 |无人观看时，是否直接关闭(而不是通过on_none_reader hook返回close)|
|latency|query|string| 否 |srt延时, 单位毫秒|
|passphrase|query|string| 否 |srt拉流的密码|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 关闭拉流代理(delStreamProxy)

GET /index/api/delStreamProxy

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|key|query|string| 是 |addStreamProxy接口返回的key|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取拉流代理列表(listStreamProxy)

GET /index/api/listStreamProxy

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取推流代理列表(listStreamPusherProxy)

GET /index/api/listStreamPusherProxy

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 添加rtsp/rtmp/srt推流(addStreamPusherProxy)

GET /index/api/addStreamPusherProxy

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|schema|query|string| 是 |推流协议，支持rtsp、rtmp，大小写敏感|
|vhost|query|string| 是 |已注册流的虚拟主机，一般为__defaultVhost__|
|app|query|string| 是 |已注册流的应用名，例如live|
|stream|query|string| 是 |已注册流的id名，例如test|
|dst_url|query|string| 是 |推流地址，需要与schema字段协议一致|
|rtp_type|query|string| 否 |rtsp推流时，推流方式，0：tcp，1：udp|
|timeout_sec|query|string| 否 |推流超时时间，单位秒，float类型|
|retry_count|query|string| 否 |推流重试次数,不传此参数或传值<=0时，则无限重试|
|force|query|string| 否 |是否强制添加代理，默认0，设置为1时如果拉流失败也会不断重试|
|latency|query|string| 否 |srt延时, 单位毫秒|
|passphrase|query|string| 否 |srt推流的密码|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 关闭推流(delStreamPusherProxy)

GET /index/api/delStreamPusherProxy

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|key|query|string| 是 |addStreamPusherProxy接口返回的key|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取FFmpeg拉流代理列表(listFFmpegSource)

GET /index/api/listFFmpegSource

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 添加FFmpeg拉流代理(addFFmpegSource)

GET /index/api/addFFmpegSource

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|src_url|query|string| 是 |FFmpeg拉流地址,支持任意协议或格式(只要FFmpeg支持即可)|
|dst_url|query|string| 是 |FFmpeg rtmp推流地址，一般都是推给自己，例如rtmp://127.0.0.1/live/stream_form_ffmpeg|
|timeout_ms|query|string| 是 |FFmpeg推流成功超时时间,单位毫秒|
|enable_hls|query|string| 是 |是否开启hls录制|
|enable_mp4|query|string| 是 |是否开启mp4录制|
|ffmpeg_cmd_key|query|string| 否 |FFmpeg命名参数模板，置空则采用配置项:ffmpeg.cmd|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 关闭FFmpeg拉流代理(delFFmpegSource)

GET /index/api/delFFmpegSource

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|key|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 流是否在线(isMediaOnline)

GET /index/api/isMediaOnline

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|schema|query|string| 是 |协议，例如 rtsp或rtmp|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 test|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取媒体流播放器列表(getMediaPlayerList)

GET /index/api/getMediaPlayerList

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|schema|query|string| 是 |协议，例如 rtsp或rtmp|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 test|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 广播webrtc datachannel消息(broadcastMessage)

GET /index/api/broadcastMessage

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|schema|query|string| 是 |协议，例如 rtsp或rtmp，目前仅支持rtsp协议|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 test|
|msg|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取流信息(getMediaInfo)

GET /index/api/getMediaInfo

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|schema|query|string| 是 |协议，例如 rtsp或rtmp|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 test|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取录像文件列表(getMP4RecordFile)

GET /index/api/getMP4RecordFile

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 test|
|customized_path|query|string| 是 |录像文件保存自定义根目录，为空则采用配置文件设置|
|period|query|string| 是 |流的录像日期，格式为2020-02-01,如果不是完整的日期，那么是搜索录像文件夹列表，否则搜索对应日期下的mp4文件列表|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 删除录像文件夹(deleteRecordDirectory)

GET /index/api/deleteRecordDirectory

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 test|
|period|query|string| 是 |流的录像日期，格式为2020-01-01,如果不是完整的日期，那么会删除失败|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 开始录制(startRecord)

GET /index/api/startRecord

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|type|query|string| 是 |0为hls，1为mp4|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 obs|
|customized_path|query|string| 否 |录像文件保存自定义根目录，为空则采用配置文件设置|
|max_second|query|string| 否 |MP4录制的切片时间大小，单位秒|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 开始事件视频录制(startRecordTask)

GET /index/api/startRecordTask

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 obs|
|path|query|string| 是 |录像文件保存相对路径，包括名称|
|back_ms|query|string| 是 |回溯录制时长|
|forward_ms|query|string| 是 |后续录制时长|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 设置录像速度(setRecordSpeed)

GET /index/api/setRecordSpeed

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 obs|
|speed|query|string| 是 |要设置的录像倍速|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 设置录像流播放位置(seekRecordStamp)

GET /index/api/seekRecordStamp

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 obs|
|stamp|query|string| 是 |要设置的录像播放位置|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 停止录制(stopRecord)

GET /index/api/stopRecord

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|type|query|string| 是 |0为hls，1为mp4|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 obs|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 是否正在录制(isRecording)

GET /index/api/isRecording

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|type|query|string| 是 |0为hls，1为mp4|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 obs|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取截图(getSnap)

GET /index/api/getSnap

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|url|query|string| 是 |需要截图的url，可以是本机的，也可以是远程主机的|
|timeout_sec|query|string| 是 |截图失败超时时间，防止FFmpeg一直等待截图|
|expire_sec|query|string| 是 |截图的过期时间，该时间内产生的截图都会作为缓存返回|
|async|query|string| 否 |是否采用zlm内置播放器、解码器api异步截图，开启后截图速度提升但兼容性降低|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取rtp推流信息(getRtpInfo)

GET /index/api/getRtpInfo

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 rtp|
|stream_id|query|string| 是 |流id|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 创建RTP服务器(openRtpServer)

GET /index/api/openRtpServer

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|port|query|string| 是 |绑定的端口，0时为随机端口|
|tcp_mode|query|string| 是 |tcp模式，0时为不启用tcp监听，1时为启用tcp监听，2时为tcp主动连接模式|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 rtp|
|stream_id|query|string| 是 |该端口绑定的流id|
|re_use_port|query|string| 否 |是否重用端口，默认为0，非必选参数|
|ssrc|query|string| 否 |是否指定收流的rtp ssrc, 十进制数字，不指定或指定0时则不过滤rtp，非必选参数|
|only_track|query|string| 否 |是否为单音频/单视频track，0：不设置，1：单音频，2：单视频|
|local_ip|query|string| 否 |指定创建RTP的本地ip，ipv4可填”0.0.0.0“，ipv6可填”::“，一般保持默认|

#### 详细说明

**stream_id**: 该端口绑定的流id

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 创建多路复用RTP服务器(openRtpServerMultiplex)

GET /index/api/openRtpServerMultiplex

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|port|query|string| 是 |绑定的端口，0时为随机端口|
|tcp_mode|query|string| 是 |tcp模式，0时为不启用tcp监听，1时为启用tcp监听|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 rtp|
|stream_id|query|string| 是 |该端口绑定的流id|
|only_track|query|string| 否 |是否为单音频/单视频track，0：不设置，1：单音频，2：单视频|
|local_ip|query|string| 否 |指定创建RTP的本地ip，ipv4可填”0.0.0.0“，ipv6可填”::“，一般保持默认|

#### 详细说明

**stream_id**: 该端口绑定的流id

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 连接RTP服务器(connectRtpServer)

GET /index/api/connectRtpServer

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|dst_url|query|string| 是 |tcp主动模式时服务端地址|
|dst_port|query|string| 是 |tcp主动模式时服务端端口|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 rtp|
|stream_id|query|string| 是 |OpenRtpServer时绑定的流id|

#### 详细说明

**stream_id**: OpenRtpServer时绑定的流id

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 关闭RTP服务器(closeRtpServer)

GET /index/api/closeRtpServer

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 rtp|
|stream_id|query|string| 是 |该端口绑定的流id|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 更新RTP服务器过滤SSRC(updateRtpServerSSRC)

GET /index/api/updateRtpServerSSRC

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 rtp|
|stream_id|query|string| 是 |该端口绑定的流id|
|ssrc|query|string| 是 |十进制ssrc|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 暂停RTP超时检查(pauseRtpCheck)

GET /index/api/pauseRtpCheck

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 rtp|
|stream_id|query|string| 是 |该端口绑定的流id|
|pause_seconds|query|string| 否 |暂停超时监测后，将在pause_seconds时间后恢复|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 恢复RTP超时检查(resumeRtpCheck)

GET /index/api/resumeRtpCheck

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 rtp|
|stream_id|query|string| 是 |该端口绑定的流id|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取RTP服务器列表(listRtpServer)

GET /index/api/listRtpServer

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 开始active模式发送rtp(startSendRtp)

GET /index/api/startSendRtp

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 obs|
|ssrc_multi_send|query|string| 否 |是否支持同ssrc推流到多个上级服务器,该参数非必选参数 默认false|
|ssrc|query|string| 是 |rtp推流的ssrc|
|dst_url|query|string| 是 |目标ip或域名|
|dst_port|query|string| 是 |目标端口|
|is_udp|query|string| 是 |1:udp active模式, 0:tcp active模式|
|src_port|query|string| 否 |指定tcp/udp客户端使用的本地端口，0时为随机端口，该参数非必选参数，不传时为随机端口。|
|from_mp4|query|string| 否 |是否推送本地MP4录像，该参数非必选参数|
|type|query|string| 否 |rtp打包模式，0:es, 1: ps, 2: ts|
|pt|query|string| 否 |rtp payload type，默认96，该参数非必选参数|
|only_audio|query|string| 否 |rtp es方式打包时，是否只打包音频，该参数非必选参数|
|udp_rtcp_timeout|query|string| 否 |udp方式推流时，是否开启rtcp发送和rtcp接收超时判断，开启后(默认关闭)，如果接收rr rtcp超时，将导致主动停止rtp发送|
|recv_stream_id|query|string| 否 |发送rtp同时接收，一般用于双向语言对讲, 如果不为空，说明开启接收，值为接收流的id|
|enable_origin_recv_limit|query|string| 否 |转发rtp(tcp模式)时，如果发送不出去，是否限制源端收流速度，此参数在多倍速rtp转发时作用较大|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 开始passive模式发送rtp(startSendRtpPassive)

GET /index/api/startSendRtpPassive

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 obs|
|ssrc|query|string| 是 |rtp推流的ssrc，ssrc不同时，可以推流到多个上级服务器|
|is_udp|query|string| 否 |1:udp passive模式, 0:tcp passive模式|
|src_port|query|string| 否 |指定tcp/udp客户端使用的本地端口，0时为随机端口，该参数非必选参数，不传时为随机端口。|
|from_mp4|query|string| 否 |是否推送本地MP4录像，该参数非必选参数|
|type|query|string| 否 |rtp打包模式，0:es, 1: ps, 2: ts|
|pt|query|string| 否 |rtp payload type，默认96，该参数非必选参数|
|only_audio|query|string| 否 |rtp es方式打包时，是否只打包音频，该参数非必选参数|
|recv_stream_id|query|string| 否 |发送rtp同时接收，一般用于双向语言对讲, 如果不为空，说明开启接收，值为接收流的id|
|close_delay_ms|query|string| 否 |等待tcp连接超时时间，单位毫秒，默认5000毫秒|
|enable_origin_recv_limit|query|string| 否 |转发rtp(tcp模式)时，如果发送不出去，是否限制源端收流速度，此参数在多倍速rtp转发时作用较大|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 开始双向对讲(startSendRtpTalk)

GET /index/api/startSendRtpTalk

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 rtp|
|stream|query|string| 是 |流id，例如webrtc推流上来的流id|
|ssrc|query|string| 是 |rtp推流出去的ssrc|
|recv_stream_id|query|string| 是 |对方rtp推流上来的流id，我们将通过这个链接回复他rtp流；请注意两个流的app和vhost需一致|
|from_mp4|query|string| 否 |是否推送本地MP4录像，该参数非必选参数|
|type|query|string| 否 |0(ES流)、1(PS流)、2(TS流)，默认1(PS流)；该参数非必选参数|
|pt|query|string| 否 |rtp payload type，默认96；该参数非必选参数|
|only_audio|query|string| 否 |rtp es方式打包时，是否只打包音频；该参数非必选参数|
|enable_origin_recv_limit|query|string| 否 |转发rtp(tcp模式)时，如果发送不出去，是否限制源端收流速度，此参数在多倍速rtp转发时作用较大|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 停止 发送rtp(stopSendRtp)

GET /index/api/stopSendRtp

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 obs|
|ssrc|query|string| 否 |根据ssrc关停某路rtp推流，不传时关闭所有推流|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取rtp发送列表(listRtpSender)

GET /index/api/listRtpSender

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |应用名，例如 live|
|stream|query|string| 是 |流id，例如 obs|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取版本信息(version)

GET /index/api/version

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取拉流代理信息(getProxyInfo)

GET /index/api/getProxyInfo

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |none|
|key|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取推流代理信息(getProxyPusherInfo)

GET /index/api/getProxyPusherInfo

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |none|
|key|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 点播mp4文件(loadMP4File)

GET /index/api/loadMP4File

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |添加的流的虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |添加的流的应用名，例如live|
|stream|query|string| 是 |添加的流的id名，例如test|
|file_path|query|string| 是 |mp4文件绝对路径|
|file_repeat|query|string| 否 |是否循环点播mp4文件，如果配置文件已经开启循环点播，此参数无效|
|seek_ms|query|string| 否 |点播seek到特定位置，单位毫秒|
|speed|query|string| 否 |播放速度, float类型|
|enable_hls|query|string| 否 |是否转hls-ts|
|enable_hls_fmp4|query|string| 否 |是否转hls-fmp4|
|enable_mp4|query|string| 否 |是否mp4录制，默认不开启(覆盖配置文件)|
|enable_rtsp|query|string| 否 |是否转协议为rtsp/webrtc|
|enable_rtmp|query|string| 否 |是否转协议为rtmp/flv|
|enable_ts|query|string| 否 |是否转协议为http-ts/ws-ts|
|enable_fmp4|query|string| 否 |是否转协议为http-fmp4/ws-fmp4|
|enable_audio|query|string| 否 |转协议是否开启音频|
|add_mute_audio|query|string| 否 |转协议无音频时，是否添加静音aac音频|
|mp4_save_path|query|string| 否 |mp4录制保存根目录，置空使用默认目录|
|mp4_max_second|query|string| 否 |mp4录制切片大小，单位秒|
|hls_save_path|query|string| 否 |hls保存根目录，置空使用默认目录|
|modify_stamp|query|string| 否 |是否修改原始时间戳，默认值2；取值范围：0.采用源视频流绝对时间戳，不做任何改变;1.采用zlmediakit接收数据时的系统时间戳(有平滑处理);2.采用源视频流时间戳相对时间戳(增长量)，有做时间戳跳跃和回退矫正|
|auto_close|query|string| 否 |无人观看时，是否直接关闭(而不是通过on_none_reader hook返回close)；强制开启，此参数不生效|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 下载文件(downloadFile)

GET /index/api/downloadFile

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|file_path|query|string| 是 |文件绝对路径，根据文件名生成Content-Type；该接口将触发on_http_access hook|
|save_name|query|string| 否 |浏览器下载文件后保存文件名；可选参数|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET WebRTC-注册到信令服务器(addWebrtcRoomKeeper)

GET /index/api/addWebrtcRoomKeeper

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |none|
|server_host|query|string| 是 |要注册到的信令服务器地址|
|server_port|query|string| 是 |要注册到的信令服务器端口|
|room_id|query|string| 是 |要注册到的roomid|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET WebRTC-从信令服务器注销(delWebrtcRoomKeeper)

GET /index/api/delWebrtcRoomKeeper

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |none|
|room_key|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET WebRTC-Peer查看注册信息(listWebrtcRoomKeepers)

GET /index/api/listWebrtcRoomKeepers

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET WebRTC-信令服务器查看注册信息(listWebrtcRooms)

GET /index/api/listWebrtcRooms

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET WebRTC-查看WebRTCProxyPlayer连接信息(getWebrtcProxyPlayerInfo)

GET /index/api/getWebrtcProxyPlayerInfo

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |none|
|key|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET onvif 搜索

GET /index/api/searchOnvifDevice

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |none|
|subnet_prefix|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 获取 onvif 设备url

GET /index/api/getStreamUrl

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |none|
|onvif_url|query|string| 是 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 下载程序二进制文件(downloadBin)

GET /index/api/downloadBin

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## POST WebRTC交互(webrtc)

POST /index/api/webrtc

WebRTC交互接口，body为SDP offer

> Body 请求参数

```json
{}
```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|type|query|string| 是 |webrtc类型，play为播放，push为推流，echo为回显测试|
|app|query|string| 是 |应用名|
|stream|query|string| 是 |流id|
|preferred_tcp|query|string| 否 |是否webrtc over tcp优先模式|
|cand_udp|query|string| 否 |指定zlm服务器udp candidate|
|cand_tcp|query|string| 否 |指定zlm服务器tcp candidate|
|Content-Type|header|string| 是 |none|
|body|body|object| 否 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## POST WebRTC-WHIP推流(whip)

POST /index/api/whip

WebRTC WHIP标准推流接口，body为SDP offer

> Body 请求参数

```
string

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|app|query|string| 是 |应用名|
|stream|query|string| 是 |流id|
|preferred_tcp|query|string| 否 |是否webrtc over tcp优先模式|
|cand_udp|query|string| 否 |指定zlm服务器udp candidate|
|cand_tcp|query|string| 否 |指定zlm服务器tcp candidate|
|Content-Type|header|string| 是 |none|
|body|body|string| 否 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## POST WebRTC-WHEP播放(whep)

POST /index/api/whep

WebRTC WHEP标准播放接口，body为SDP offer

> Body 请求参数

```
string

```

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|app|query|string| 是 |应用名|
|stream|query|string| 是 |流id|
|preferred_tcp|query|string| 否 |是否webrtc over tcp优先模式|
|cand_udp|query|string| 否 |指定zlm服务器udp candidate|
|cand_tcp|query|string| 否 |指定zlm服务器tcp candidate|
|Content-Type|header|string| 是 |none|
|body|body|string| 否 |none|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## DELETE WebRTC-删除连接(delete_webrtc)

DELETE /index/api/delete_webrtc

删除WebRTC连接，需要使用DELETE方法。id和token由whip/whep接口返回的Location头中获取。

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|id|query|string| 是 |WebRTC连接的唯一标识|
|token|query|string| 是 |删除操作的验证token|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 登录(login)

GET /index/api/login

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|digest|query|string| 是 |MD5("zlmediakit:"+${secret}+":" +${cookie})|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 登出(logout)

GET /index/api/logout

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

## GET 添加探针(addProbe)

GET /index/api/addProbe

### 请求参数

|名称|位置|类型|必选|说明|
|---|---|---|---|---|
|secret|query|string| 是 |api操作密钥(配置文件配置)|
|vhost|query|string| 是 |流的虚拟主机，例如__defaultVhost__|
|app|query|string| 是 |流的应用名，例如live|
|stream|query|string| 是 |流的id名，例如test|
|probe_ms|query|string| 是 |探针时长，单位毫秒|

> 返回示例

> 200 Response

```json
{}
```

### 返回结果

|状态码|状态码含义|说明|数据模型|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|none|Inline|

### 返回数据结构

# 数据模型

