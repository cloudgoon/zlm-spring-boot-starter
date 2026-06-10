package io.github.lunasaw.zlm.entity.req;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 多屏拼接请求参数（stack/start、stack/reset，POST + JSON body）。
 *
 * @author luna
 * @date 2026/06/10
 */
@Data
public class StackReq {

    /**
     * 垂直间隙比例
     */
    @JSONField(name = "gapv")
    private Double             gapv;

    /**
     * 水平间隙比例
     */
    @JSONField(name = "gaph")
    private Double             gaph;

    /**
     * 拼接输出宽度
     */
    @JSONField(name = "width")
    private Integer            width;

    /**
     * 拼接输出高度
     */
    @JSONField(name = "height")
    private Integer            height;

    /**
     * 拼接 id
     */
    @JSONField(name = "id")
    private String             id;

    /**
     * 行数
     */
    @JSONField(name = "row")
    private Integer            row;

    /**
     * 列数
     */
    @JSONField(name = "col")
    private Integer            col;

    /**
     * 每个格子的流地址，二维数组 [row][col]
     */
    @JSONField(name = "url")
    private List<List<String>> url;

    /**
     * 跨格配置，三维数组 [[ [r,c],[r,c] ], ...]
     */
    @JSONField(name = "span")
    private List<List<List<Integer>>> span;
}
