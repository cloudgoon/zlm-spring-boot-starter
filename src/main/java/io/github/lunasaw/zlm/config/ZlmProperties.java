package io.github.lunasaw.zlm.config;

import io.github.lunasaw.zlm.enums.LoadBalancerEnums;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/2
 * @description:
 */
@ConfigurationProperties(prefix = "zlm")
@Data
@Slf4j
public class ZlmProperties implements InitializingBean {
    /**
     * 对外NodeMap
     */
    public static Map<String, ZlmNode> nodeMap = new ConcurrentHashMap<>();

    private boolean enable = true;
    private boolean hookEnable = true;
    private LoadBalancerEnums balance = LoadBalancerEnums.ROUND_ROBIN;
    private List<ZlmNode> nodes = new CopyOnWriteArrayList<>();


    public Map<String, ZlmNode> getNodeMap() {
        return nodeMap;
    }

    public List<ZlmNode> getNodes() {
        return this.nodes;
    }

    @Override
    public void afterPropertiesSet() {
        // 初始化节点映射，只包含启用的节点。
        // 显式 merge 函数避免重复 serverId 抛 IllegalStateException（取后者）；
        // ConcurrentHashMap::new 保证替换后的 nodeMap 仍是线程安全的并发容器。
        if (this.nodes != null && !this.nodes.isEmpty()) {
            nodeMap = this.nodes.stream()
                    .filter(ZlmNode::isEnabled)
                    .collect(Collectors.toMap(
                            ZlmNode::getServerId,
                            node -> node,
                            (existing, replacement) -> {
                                log.warn("发现重复的 serverId [{}]，使用后出现的节点配置覆盖前者", replacement.getServerId());
                                return replacement;
                            },
                            ConcurrentHashMap::new));
        }
    }
}
