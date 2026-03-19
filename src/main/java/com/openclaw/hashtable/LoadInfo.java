package com.openclaw.hashtable;

/**
 * 负载信息
 */
public class LoadInfo {
    private final double cpuUsage;      // CPU使用率 0.0-1.0
    private final double memoryUsage;  // 内存使用率 0.0-1.0

    public LoadInfo(double cpuUsage, double memoryUsage) {
        this.cpuUsage = Math.max(0, Math.min(1, cpuUsage));
        this.memoryUsage = Math.max(0, Math.min(1, memoryUsage));
    }

    /**
     * 获取加权负载因子
     * CPU权重40%，内存权重60%
     */
    public double getWeightedLoad() {
        return cpuUsage * 0.4 + memoryUsage * 0.6;
    }

    /**
     * 获取负载档次
     * @return 1: 低负载(<30%), 2: 中负载(<60%), 3: 高负载(<90%), 4: 极高负载(>=90%)
     */
    public int getLoadLevel() {
        double load = getWeightedLoad();
        if (load < 0.3) return 1;
        if (load < 0.6) return 2;
        if (load < 0.9) return 3;
        return 4;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    @Override
    public String toString() {
        return String.format("LoadInfo{cpu=%.2f, memory=%.2f, weighted=%.2f, level=%d}",
                cpuUsage, memoryUsage, getWeightedLoad(), getLoadLevel());
    }
}
