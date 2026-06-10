package io.github.lunasaw.zlm.node.impl;

import io.github.lunasaw.zlm.config.ZlmNode;
import io.github.lunasaw.zlm.enums.LoadBalancerEnums;
import io.github.lunasaw.zlm.node.LoadBalancer;
import io.github.lunasaw.zlm.node.NodeSupplier;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 一致性哈希负载均衡器
 * 每次选择节点时重新构建哈希环，确保使用最新节点列表
 * @author luna
 * @date 2024/1/5
 */
@Slf4j
public class ConsistentHashingLoadBalancer implements LoadBalancer {

    private static final int VIRTUAL_NODE_COUNT = 10;
    private volatile NodeSupplier nodeSupplier;

    /** 缓存的哈希环，仅在节点集合「指纹」变化时重建，避免每次 selectNode 全量重建 */
    private volatile TreeMap<Integer, ZlmNode> cachedRing;
    /** 当前缓存环对应的节点指纹（排序后的 serverId+weight 拼接） */
    private volatile String                    cachedFingerprint;

    @Override
    public void setNodeSupplier(NodeSupplier nodeSupplier) {
        this.nodeSupplier = nodeSupplier;
        // 节点提供器变更，缓存失效
        this.cachedRing = null;
        this.cachedFingerprint = null;
        log.info("设置节点提供器: {}", nodeSupplier != null ? nodeSupplier.getName() : "null");
    }

    @Override
    public ZlmNode selectNode(String key) {
        if (key == null) {
            return null;
        }

        List<ZlmNode> nodes = getCurrentNodes();
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        // 仅在节点集合变化时重建哈希环
        TreeMap<Integer, ZlmNode> hashRing = getOrBuildHashRing(nodes);
        if (hashRing == null || hashRing.isEmpty()) {
            return null;
        }

        // 选择节点
        int hash = getHash(key);
        Map.Entry<Integer, ZlmNode> entry = hashRing.ceilingEntry(hash);
        if (entry == null) {
            entry = hashRing.firstEntry();
        }

        return entry.getValue();
    }

    @Override
    public String getType() {
        return LoadBalancerEnums.CONSISTENT_HASHING.getType();
    }

    /**
     * 获取缓存的哈希环；节点指纹变化时重建并更新缓存。
     */
    private TreeMap<Integer, ZlmNode> getOrBuildHashRing(List<ZlmNode> nodes) {
        String fingerprint = fingerprint(nodes);
        TreeMap<Integer, ZlmNode> ring = cachedRing;
        if (ring != null && fingerprint.equals(cachedFingerprint)) {
            return ring;
        }
        // 双重检查，避免并发重复构建（构建结果幂等，无需加锁即可保证最终一致）
        TreeMap<Integer, ZlmNode> built = buildHashRing(nodes);
        cachedRing = built;
        cachedFingerprint = fingerprint;
        return built;
    }

    /**
     * 计算节点集合指纹：排序后的 serverId#weight 拼接，节点增删/权重变化都会改变指纹。
     */
    private String fingerprint(List<ZlmNode> nodes) {
        return nodes.stream()
                .map(n -> n.getServerId() + "#" + n.getWeight())
                .sorted()
                .collect(java.util.stream.Collectors.joining(","));
    }

    /**
     * 构建哈希环
     *
     * @param nodes 节点列表
     * @return 哈希环
     */
    private TreeMap<Integer, ZlmNode> buildHashRing(List<ZlmNode> nodes) {
        TreeMap<Integer, ZlmNode> hashRing = new TreeMap<>();

        for (ZlmNode node : nodes) {
            int weight = node.getWeight();
            String serverId = node.getServerId();

            // 根据权重创建虚拟节点
            for (int i = 0; i < weight * VIRTUAL_NODE_COUNT; i++) {
                String virtualNodeName = serverId + "#" + i;
                int hash = getHash(virtualNodeName);
                hashRing.put(hash, node);
            }
        }

        return hashRing;
    }

    /**
     * 计算哈希值
     *
     * @param str 字符串
     * @return 哈希值
     */
    private int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return hash;
    }

    /**
     * 获取当前可用节点列表
     *
     * @return 节点列表
     */
    private List<ZlmNode> getCurrentNodes() {
        if (nodeSupplier == null) {
            log.warn("NodeSupplier未设置，无法获取节点列表");
            return null;
        }

        try {
            return nodeSupplier.getNodes();
        } catch (Exception e) {
            log.error("从NodeSupplier获取节点列表失败", e);
            return null;
        }
    }
}
