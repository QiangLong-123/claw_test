package com.openclaw.hashtable;

import java.util.*;

/**
 * 带虚拟节点和动态负载因子的一致性哈希算法
 */
public class ConsistentHashingWithVirtualNodes {

    /**
     * 哈希环，key为哈希值，value为虚拟节点信息
     */
    private final TreeMap<Long, VirtualNode> hashRing;

    /**
     * 物理节点到其虚拟节点列表的映射
     */
    private final Map<PhysicalNode, List<VirtualNode>> physicalToVirtual;

    /**
     * 基准虚拟节点数量（每个物理节点）
     */
    private final int baseVirtualNodeCount;

    /**
     * 负载因子阈值
     */
    private static final double LOW_LOAD_THRESHOLD = 0.3;
    private static final double MEDIUM_LOAD_THRESHOLD = 0.6;
    private static final double HIGH_LOAD_THRESHOLD = 0.9;

    public ConsistentHashingWithVirtualNodes(int baseVirtualNodeCount) {
        this.hashRing = new TreeMap<>();
        this.physicalToVirtual = new HashMap<>();
        this.baseVirtualNodeCount = baseVirtualNodeCount;
    }

    /**
     * 计算哈希值（使用FNV1a算法）
     */
    private long hash(String key) {
        final long FNV_64_INIT = 0xcbf29ce484222325L;
        final long FNV_64_PRIME = 0x100000001b3L;
        long hash = FNV_64_INIT;
        for (int i = 0; i < key.length(); i++) {
            hash ^= key.charAt(i);
            hash *= FNV_64_PRIME;
        }
        return Math.abs(hash);
    }

    /**
     * 添加物理节点
     * @param node 物理节点
     * @param virtualCount 虚拟节点数量（默认使用baseVirtualNodeCount）
     */
    public void addPhysicalNode(PhysicalNode node, int virtualCount) {
        if (physicalToVirtual.containsKey(node)) {
            return;
        }

        List<VirtualNode> virtualNodes = new ArrayList<>();
        for (int i = 0; i < virtualCount; i++) {
            String vNodeName = node.getName() + "#VN" + i;
            long hashValue = hash(vNodeName);
            VirtualNode vNode = new VirtualNode(node, i, hashValue);
            hashRing.put(hashValue, vNode);
            virtualNodes.add(vNode);
        }
        physicalToVirtual.put(node, virtualNodes);
    }

    /**
     * 添加物理节点（使用默认虚拟节点数量）
     */
    public void addPhysicalNode(PhysicalNode node) {
        addPhysicalNode(node, baseVirtualNodeCount);
    }

    /**
     * 删除物理节点
     * @param node 物理节点
     */
    public void removePhysicalNode(PhysicalNode node) {
        List<VirtualNode> virtualNodes = physicalToVirtual.remove(node);
        if (virtualNodes != null) {
            for (VirtualNode vNode : virtualNodes) {
                hashRing.remove(vNode.getHash());
            }
        }
    }

    /**
     * 根据key获取物理节点
     * @param key 键
     * @return 物理节点，不存在返回null
     */
    public PhysicalNode getPhysicalNode(String key) {
        if (hashRing.isEmpty()) {
            return null;
        }

        long hashValue = hash(key);
        Map.Entry<Long, VirtualNode> entry = hashRing.ceilingEntry(hashValue);

        if (entry == null) {
            entry = hashRing.firstEntry();
        }

        return entry != null ? entry.getValue().getPhysicalNode() : null;
    }

    /**
     * 根据负载因子调整虚拟节点数量
     * 虚拟节点数量 = (1 - load) * baseVirtualNodeCount
     * @param loadInfo 负载信息
     */
    public void adjustVirtualNodesByLoad(LoadInfo loadInfo) {
        double load = loadInfo.getWeightedLoad();
        
        // 计算新的虚拟节点数量
        int newVirtualCount = (int) ((1 - load) * baseVirtualNodeCount);
        newVirtualCount = Math.max(1, newVirtualCount); // 至少保留1个虚拟节点

        // 重新配置每个物理节点的虚拟节点
        for (Map.Entry<PhysicalNode, List<VirtualNode>> entry : physicalToVirtual.entrySet()) {
            PhysicalNode node = entry.getKey();
            List<VirtualNode> oldVirtualNodes = entry.getValue();

            // 移除旧的虚拟节点
            for (VirtualNode vNode : oldVirtualNodes) {
                hashRing.remove(vNode.getHash());
            }

            // 添加新的虚拟节点
            List<VirtualNode> newVirtualNodes = new ArrayList<>();
            for (int i = 0; i < newVirtualCount; i++) {
                String vNodeName = node.getName() + "#VN" + i;
                long hashValue = hash(vNodeName);
                VirtualNode vNode = new VirtualNode(node, i, hashValue);
                hashRing.put(hashValue, vNode);
                newVirtualNodes.add(vNode);
            }

            entry.setValue(newVirtualNodes);
        }
    }

    /**
     * 根据负载信息自动调整（便捷方法）
     */
    public void adjustByLoad(double cpuUsage, double memoryUsage) {
        adjustVirtualNodesByLoad(new LoadInfo(cpuUsage, memoryUsage));
    }

    /**
     * 获取所有物理节点
     */
    public Set<PhysicalNode> getAllPhysicalNodes() {
        return physicalToVirtual.keySet();
    }

    /**
     * 获取虚拟节点总数
     */
    public int getTotalVirtualNodeCount() {
        return hashRing.size();
    }

    /**
     * 获取指定物理节点的虚拟节点数量
     */
    public int getVirtualNodeCount(PhysicalNode node) {
        List<VirtualNode> nodes = physicalToVirtual.get(node);
        return nodes != null ? nodes.size() : 0;
    }

    /**
     * 打印状态信息
     */
    public void printStatus() {
        System.out.println("=== Consistent Hash Status ===");
        System.out.println("Physical nodes: " + physicalToVirtual.size());
        System.out.println("Total virtual nodes: " + hashRing.size());
        System.out.println("Base virtual nodes per node: " + baseVirtualNodeCount);
        
        for (Map.Entry<PhysicalNode, List<VirtualNode>> entry : physicalToVirtual.entrySet()) {
            System.out.println("  " + entry.getKey().getName() + ": " + entry.getValue().size() + " virtual nodes");
        }
        System.out.println("=============================");
    }
}
