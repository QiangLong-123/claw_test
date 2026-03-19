package com.openclaw.hashtable;

/**
 * 物理节点
 */
public class PhysicalNode {
    private final String name;
    private final String host;
    private final int port;

    public PhysicalNode(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "PhysicalNode{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
