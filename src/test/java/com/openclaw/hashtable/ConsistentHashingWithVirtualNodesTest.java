package com.openclaw.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ConsistentHashingWithVirtualNodes
 */
class ConsistentHashingWithVirtualNodesTest {

    private ConsistentHashingWithVirtualNodes hash;

    @BeforeEach
    void setUp() {
        hash = new ConsistentHashingWithVirtualNodes(3);
    }

    @Test
    void testAddPhysicalNode() {
        PhysicalNode node = new PhysicalNode("node1", "192.168.1.1");
        hash.addPhysicalNode(node);
        
        assertEquals(1, hash.getAllPhysicalNodes().size());
        assertEquals(3, hash.getTotalVirtualNodeCount());
    }

    @Test
    void testRemovePhysicalNode() {
        PhysicalNode node = new PhysicalNode("node1", "192.168.1.1");
        hash.addPhysicalNode(node);
        hash.removePhysicalNode(node);
        
        assertEquals(0, hash.getAllPhysicalNodes().size());
        assertEquals(0, hash.getTotalVirtualNodeCount());
    }

    @Test
    void testGetPhysicalNode() {
        PhysicalNode node1 = new PhysicalNode("node1", "192.168.1.1");
        PhysicalNode node2 = new PhysicalNode("node2", "192.168.1.2");
        
        hash.addPhysicalNode(node1);
        hash.addPhysicalNode(node2);
        
        // Test that keys are distributed to nodes
        PhysicalNode result = hash.getPhysicalNode("testKey");
        assertNotNull(result);
        assertTrue(result.equals(node1) || result.equals(node2));
    }

    @Test
    void testGetPhysicalNodeEmptyRing() {
        PhysicalNode result = hash.getPhysicalNode("testKey");
        assertNull(result);
    }

    @Test
    void testConsistentDistribution() {
        PhysicalNode node1 = new PhysicalNode("node1", "192.168.1.1");
        PhysicalNode node2 = new PhysicalNode("node2", "192.168.1.2");
        PhysicalNode node3 = new PhysicalNode("node3", "192.168.1.3");
        
        hash.addPhysicalNode(node1);
        hash.addPhysicalNode(node2);
        hash.addPhysicalNode(node3);
        
        Set<PhysicalNode> nodesHit = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PhysicalNode node = hash.getPhysicalNode("key" + i);
            if (node != null) {
                nodesHit.add(node);
            }
        }
        
        // All nodes should be hit at least once
        assertEquals(3, nodesHit.size());
    }

    @Test
    void testAdjustByLoad() {
        PhysicalNode node = new PhysicalNode("node1", "192.168.1.1");
        hash.addPhysicalNode(node);
        
        int initialCount = hash.getTotalVirtualNodeCount();
        assertEquals(3, initialCount);
        
        // Adjust with low load (0.1) - should increase virtual nodes
        hash.adjustByLoad(0.1, 0.1);
        
        // With load 0.1, virtual nodes = (1 - 0.1) * 3 = 2.7 ≈ 2 or 3
        assertTrue(hash.getTotalVirtualNodeCount() >= 1);
    }

    @Test
    void testVirtualNodeCount() {
        PhysicalNode node = new PhysicalNode("node1", "192.168.1.1");
        hash.addPhysicalNode(node, 5);
        
        assertEquals(5, hash.getVirtualNodeCount(node));
    }

    @Test
    void testMultipleNodesWithDifferentVirtualCounts() {
        PhysicalNode node1 = new PhysicalNode("node1", "192.168.1.1");
        PhysicalNode node2 = new PhysicalNode("node2", "192.168.1.2");
        
        hash.addPhysicalNode(node1, 2);
        hash.addPhysicalNode(node2, 5);
        
        assertEquals(2, hash.getVirtualNodeCount(node1));
        assertEquals(5, hash.getVirtualNodeCount(node2));
        assertEquals(7, hash.getTotalVirtualNodeCount());
    }
}
