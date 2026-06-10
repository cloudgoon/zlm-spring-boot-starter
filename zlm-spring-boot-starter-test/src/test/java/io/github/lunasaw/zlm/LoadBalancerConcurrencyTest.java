package io.github.lunasaw.zlm;

import io.github.lunasaw.zlm.config.ZlmNode;
import io.github.lunasaw.zlm.node.NodeSupplier;
import io.github.lunasaw.zlm.node.impl.ConsistentHashingLoadBalancer;
import io.github.lunasaw.zlm.node.impl.RoundRobinLoadBalancer;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * 负载均衡器并发安全 / 实时性增强测试（方案 §9.1 / §9.2）。
 *
 * @author luna
 * @date 2026/06/10
 */
public class LoadBalancerConcurrencyTest {

    private static NodeSupplier supplierOf(List<ZlmNode> nodes) {
        return new NodeSupplier() {
            @Override
            public String getName() {
                return "TestSupplier";
            }

            @Override
            public List<ZlmNode> getNodes() {
                return nodes;
            }

            @Override
            public ZlmNode getNode(String serverId) {
                return nodes.stream().filter(n -> n.getServerId().equals(serverId)).findFirst().orElse(null);
            }
        };
    }

    private static List<ZlmNode> threeNodes() {
        List<ZlmNode> nodes = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ZlmNode n = new ZlmNode();
            n.setServerId("node-" + i);
            n.setWeight(1);
            n.setEnabled(true);
            nodes.add(n);
        }
        return nodes;
    }

    /**
     * §9.1 #1：AtomicInteger 接近 Integer.MAX_VALUE 翻负时，RoundRobin 不得抛 IndexOutOfBoundsException。
     */
    @Test
    public void testRoundRobinNoOverflowIndexException() throws Exception {
        RoundRobinLoadBalancer lb = new RoundRobinLoadBalancer();
        lb.setNodeSupplier(supplierOf(threeNodes()));

        // 反射把内部 sequence 预置到接近 Integer.MAX_VALUE
        Field seqField = RoundRobinLoadBalancer.class.getDeclaredField("sequence");
        seqField.setAccessible(true);
        AtomicInteger seq = (AtomicInteger) seqField.get(lb);
        seq.set(Integer.MAX_VALUE - 2);

        // 跨越翻转点多次选择，任何一次都不能抛异常且必须返回节点
        for (int i = 0; i < 10; i++) {
            ZlmNode node = lb.selectNode("k");
            assertNotNull("翻转点附近 selectNode 不应返回 null", node);
        }
    }

    /**
     * §9.1：RoundRobin 多线程并发下不抛异常、不越界。
     */
    @Test
    public void testRoundRobinConcurrentSafe() throws Exception {
        RoundRobinLoadBalancer lb = new RoundRobinLoadBalancer();
        lb.setNodeSupplier(supplierOf(threeNodes()));

        int threads = 16;
        int perThread = 5000;
        List<Thread> ts = new ArrayList<>();
        AtomicInteger errors = new AtomicInteger(0);
        for (int t = 0; t < threads; t++) {
            Thread th = new Thread(() -> {
                for (int i = 0; i < perThread; i++) {
                    try {
                        assertNotNull(lb.selectNode("k"));
                    } catch (Throwable e) {
                        errors.incrementAndGet();
                    }
                }
            });
            ts.add(th);
            th.start();
        }
        for (Thread th : ts) {
            th.join();
        }
        assertEquals("并发选点不应产生任何异常", 0, errors.get());
    }

    /**
     * §9.2 #4：一致性哈希同 key 落点稳定（缓存哈希环后不再每次重建导致漂移）。
     */
    @Test
    public void testConsistentHashingStableForSameKey() {
        ConsistentHashingLoadBalancer lb = new ConsistentHashingLoadBalancer();
        lb.setNodeSupplier(supplierOf(threeNodes()));

        ZlmNode first = lb.selectNode("business-key-1");
        assertNotNull(first);
        for (int i = 0; i < 100; i++) {
            ZlmNode n = lb.selectNode("business-key-1");
            assertEquals("同 key 应稳定落到同一节点", first.getServerId(), n.getServerId());
        }
    }
}
