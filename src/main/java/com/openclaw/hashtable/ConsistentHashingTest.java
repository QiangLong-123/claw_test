package com.openclaw.hashtable;

/**
 * 一致性哈希算法测试
 */
public class ConsistentHashingTest {

    public static void main(String[] args) {
        // 创建一致性哈希实例，每个物理节点10000个虚拟节点
        int baseVirtualNodeCount = 10000;
        ConsistentHashingWithVirtualNodes consistentHash = 
            new ConsistentHashingWithVirtualNodes(baseVirtualNodeCount);

        // 添加物理节点
        PhysicalNode node1 = new PhysicalNode("node1", "192.168.1.1", 8080);
        PhysicalNode node2 = new PhysicalNode("node2", "192.168.1.2", 8080);
        PhysicalNode node3 = new PhysicalNode("node3", "192.168.1.3", 8080);

        consistentHash.addPhysicalNode(node1, baseVirtualNodeCount);
        consistentHash.addPhysicalNode(node2, baseVirtualNodeCount);
        consistentHash.addPhysicalNode(node3, baseVirtualNodeCount);

        System.out.println("=== Initial Status ===");
        consistentHash.printStatus();

        // 测试获取节点
        System.out.println("\n=== Get Node Tests ===");
        String[] keys = {"user:1001", "user:1002", "order:2001", "product:3001", "cache:4001"};
        for (String key : keys) {
            PhysicalNode node = consistentHash.getPhysicalNode(key);
            System.out.println("Key '" + key + "' -> " + (node != null ? node.getName() : "null"));
        }

        // 测试低负载情况 (CPU 20%, 内存 25%)
        System.out.println("\n=== Low Load Test (CPU=20%, Memory=25%) ===");
        consistentHash.adjustByLoad(0.20, 0.25);
        consistentHash.printStatus();

        // 测试中负载情况 (CPU 45%, 内存 55%)
        System.out.println("\n=== Medium Load Test (CPU=45%, Memory=55%) ===");
        consistentHash.adjustByLoad(0.45, 0.55);
        consistentHash.printStatus();

        // 测试高负载情况 (CPU 70%, 内存 80%)
        System.out.println("\n=== High Load Test (CPU=70%, Memory=80%) ===");
        consistentHash.adjustByLoad(0.70, 0.80);
        consistentHash.printStatus();

        // 测试删除节点
        System.out.println("\n=== Remove Node Test ===");
        consistentHash.removePhysicalNode(node2);
        consistentHash.printStatus();

        // 测试极高负载 (CPU 95%, 内存 92%)
        System.out.println("\n=== Critical Load Test (CPU=95%, Memory=92%) ===");
        consistentHash.adjustByLoad(0.95, 0.92);
        consistentHash.printStatus();

        // 添加回节点
        System.out.println("\n=== Re-add Node Test ===");
        consistentHash.addPhysicalNode(node2, baseVirtualNodeCount);
        consistentHash.printStatus();

        // 负载分布测试
        System.out.println("\n=== Load Distribution Test ===");
        testLoadDistribution(consistentHash, 10000);
    }

    /**
     * 测试负载分布
     */
    private static void testLoadDistribution(ConsistentHashingWithVirtualNodes hash, int testCount) {
        Map<String, Integer> distribution = new HashMap<>();
        
        for (int i = 0; i < testCount; i++) {
            String key = "key:" + i;
            PhysicalNode node = hash.getPhysicalNode(key);
            if (node != null) {
                String nodeName = node.getName();
                distribution.put(nodeName, distribution.getOrDefault(nodeName, 0) + 1);
            }
        }

        System.out.println("Distribution of " + testCount + " keys:");
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / testCount;
            System.out.printf("  %s: %d (%.2f%%)%n", 
                entry.getKey(), entry.getValue(), percentage);
        }
    }
}
