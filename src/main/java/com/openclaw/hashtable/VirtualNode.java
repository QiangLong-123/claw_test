package com.openclaw.hashtable;

/**
 * 虚拟节点
 */
public class VirtualNode {
    private final PhysicalNode physicalNode;
    private final int virtualIndex;
    private final long hash;

    public VirtualNode(PhysicalNode physicalNode, int virtualIndex, long hash) {
        this.physicalNode = physicalNode;
        this.virtualIndex = virtualIndex;
        this.hash = hash;
    }

    public PhysicalNode getPhysicalNode() {
        return physicalNode;
    }

    public int getVirtualIndex() {
        return virtualIndex;
    }

    public long getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "VirtualNode{" +
                "physical=" + physicalNode.getName() +
                ", index=" + virtualIndex +
                ", hash=" + hash +
                '}';
    }
}
