package io.github.lunasaw.zlm;

import io.github.lunasaw.zlm.config.ZlmNode;
import io.github.lunasaw.zlm.config.ZlmProperties;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * ZlmProperties 并发安全测试（方案 §9.1 #2）。
 *
 * @author luna
 * @date 2026/06/10
 */
public class ZlmPropertiesConcurrencyTest {

    private static ZlmNode node(String serverId, boolean enabled) {
        ZlmNode n = new ZlmNode();
        n.setServerId(serverId);
        n.setEnabled(enabled);
        n.setWeight(1);
        return n;
    }

    /**
     * 重复 serverId 不应导致 afterPropertiesSet 抛 IllegalStateException（取后者）。
     */
    @Test
    public void testDuplicateServerIdDoesNotThrow() {
        ZlmProperties props = new ZlmProperties();
        List<ZlmNode> nodes = new ArrayList<>();
        nodes.add(node("dup", true));
        ZlmNode second = node("dup", true);
        second.setHost("http://second");
        nodes.add(second);
        props.setNodes(nodes);

        // 不应抛异常
        props.afterPropertiesSet();

        Map<String, ZlmNode> map = props.getNodeMap();
        assertEquals(1, map.size());
        assertEquals("http://second", map.get("dup").getHost());
    }

    /**
     * 生成的 nodeMap 应为并发安全的 ConcurrentHashMap。
     */
    @Test
    public void testNodeMapIsConcurrent() {
        ZlmProperties props = new ZlmProperties();
        List<ZlmNode> nodes = new ArrayList<>();
        nodes.add(node("n1", true));
        nodes.add(node("n2", true));
        props.setNodes(nodes);

        props.afterPropertiesSet();

        assertTrue("nodeMap 应为 ConcurrentHashMap 以保证线程安全",
                props.getNodeMap() instanceof ConcurrentHashMap);
        assertEquals(2, props.getNodeMap().size());
    }

    /**
     * 仅启用的节点进入 nodeMap。
     */
    @Test
    public void testOnlyEnabledNodesInMap() {
        ZlmProperties props = new ZlmProperties();
        List<ZlmNode> nodes = new ArrayList<>();
        nodes.add(node("on", true));
        nodes.add(node("off", false));
        props.setNodes(nodes);

        props.afterPropertiesSet();

        assertTrue(props.getNodeMap().containsKey("on"));
        assertFalse(props.getNodeMap().containsKey("off"));
    }
}
