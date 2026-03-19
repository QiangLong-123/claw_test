# 一致性哈希算法实现大纲

## 项目概述

本项目实现了一个带虚拟节点和动态负载因子的一致性哈希算法，主要用于分布式系统中的负载均衡。

## 核心特性

- **虚拟节点**: 每个物理节点配置10000个虚拟节点，减少数据倾斜
- **动态负载因子**: 根据CPU和内存使用率动态调整虚拟节点数量
- **加权负载计算**: CPU权重40%，内存权重60%
- **负载档位**: 30%（低）、60%（中）、90%（高）

## 类设计

### 1. PhysicalNode (物理节点)

表示真实的服务器节点。

```
属性:
- name: 节点名称
- host: 主机地址
- port: 端口号

方法:
- getName(): 获取节点名称
- getHost(): 获取主机地址
- getPort(): 获取端口号
```

### 2. LoadInfo (负载信息)

存储和计算节点负载信息。

```
属性:
- cpuUsage: CPU使用率 (0.0-1.0)
- memoryUsage: 内存使用率 (0.0-1.0)

方法:
- getWeightedLoad(): 获取加权负载因子 (CPU*0.4 + Memory*0.6)
- getLoadLevel(): 获取负载档次 (1-低/2-中/3-高/4-极高)
```

### 3. VirtualNode (虚拟节点)

虚拟节点，映射到物理节点。

```
属性:
- physicalNode: 关联的物理节点
- virtualIndex: 虚拟节点索引
- hash: 哈希值

方法:
- getPhysicalNode(): 获取物理节点
- getVirtualIndex(): 获取虚拟节点索引
- getHash(): 获取哈希值
```

### 4. ConsistentHashingWithVirtualNodes (主算法类)

一致性哈希算法实现。

```
属性:
- hashRing: 哈希环 (TreeMap<Long, VirtualNode>)
- physicalToVirtual: 物理节点到虚拟节点映射
- baseVirtualNodeCount: 基准虚拟节点数量

方法:
- addPhysicalNode(node, virtualCount): 添加物理节点
- addPhysicalNode(node): 添加物理节点（默认虚拟节点数）
- removePhysicalNode(node): 删除物理节点
- getPhysicalNode(key): 根据key获取物理节点
- adjustVirtualNodesByLoad(loadInfo): 根据负载调整虚拟节点
- adjustByLoad(cpuUsage, memoryUsage): 根据负载调整（便捷方法）
- getAllPhysicalNodes(): 获取所有物理节点
- getTotalVirtualNodeCount(): 获取虚拟节点总数
- getVirtualNodeCount(node): 获取指定物理节点的虚拟节点数
- printStatus(): 打印状态信息
```

## 算法原理

### 1. 哈希算法

使用 FNV1a 算法计算哈希值，具有良好的分布性。

### 2. 虚拟节点动态调整

```
新虚拟节点数量 = (1 - 负载因子) * 基准数量

示例（基准=10000）:
- 低负载(20%): 8000 个虚拟节点
- 中负载(50%): 5000 个虚拟节点  
- 高负载(80%): 2000 个虚拟节点
- 极高负载(95%): 500 个虚拟节点
```

### 3. 负载因子计算

```
加权负载 = CPU使用率 × 0.4 + 内存使用率 × 0.6
```

负载档次：
- 1: 低负载 (< 30%)
- 2: 中负载 (< 60%)
- 3: 高负载 (< 90%)
- 4: 极高负载 (>= 90%)

## 使用示例

```java
// 1. 创建一致性哈希实例（每个物理节点10000个虚拟节点）
ConsistentHashingWithVirtualNodes hash = 
    new ConsistentHashingWithVirtualNodes(10000);

// 2. 添加物理节点
hash.addPhysicalNode(new PhysicalNode("node1", "192.168.1.1", 8080));
hash.addPhysicalNode(new PhysicalNode("node2", "192.168.1.2", 8080));

// 3. 根据key获取物理节点
PhysicalNode node = hash.getPhysicalNode("user:123");

// 4. 根据负载调整虚拟节点
hash.adjustByLoad(0.45, 0.55); // CPU 45%, 内存 55%

// 5. 删除物理节点
hash.removePhysicalNode(node1);

// 6. 查看状态
hash.printStatus();
```

## 运行测试

```bash
mvn compile exec:java
```

## 文件结构

```
claw_test/
├── pom.xml
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── openclaw/
│                   └── hashtable/
│                       ├── ConsistentHashingTest.java
│                       ├── ConsistentHashingWithVirtualNodes.java
│                       ├── LoadInfo.java
│                       ├── PhysicalNode.java
│                       └── VirtualNode.java
└── implementation.md
```

## 扩展建议

1. **心跳检测**: 添加节点健康检查
2. **数据迁移**: 支持节点变更时的数据迁移
3. **权重配置**: 支持为不同节点设置不同权重
4. **统计功能**: 添加请求统计和负载报告
