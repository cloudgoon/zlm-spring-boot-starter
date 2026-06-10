package io.github.lunasaw.zlm.entity.req;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 添加录制任务请求参数（startRecordTask，支持回溯录制）
 *
 * @author luna
 * @date 2026/06/10
 */
@Data
public class RecordTaskReq {

    /**
     * 虚拟主机，例如__defaultVhost__
     */
    private String       vhost = "__defaultVhost__";

    /**
     * 应用名，例如 live
     */
    @NotBlank
    private String       app;

    /**
     * 流id，例如 obs
     */
    @NotBlank
    private String       stream;

    /**
     * 录像文件保存相对路径，包括名称
     */
    @NotBlank
    private String       path;

    /**
     * 回溯录制时长，单位毫秒
     */
    @NotBlank
    private String       backMs;

    /**
     * 后续录制时长，单位毫秒
     */
    @NotBlank
    private String       forwardMs;

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("vhost", vhost);
        map.put("app", app);
        map.put("stream", stream);
        map.put("path", path);
        map.put("back_ms", backMs);
        map.put("forward_ms", forwardMs);
        return map;
    }
}
