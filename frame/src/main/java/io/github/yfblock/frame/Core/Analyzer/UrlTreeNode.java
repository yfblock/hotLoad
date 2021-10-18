package io.github.yfblock.frame.Core.Analyzer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UrlTreeNode {
    String url;                 // 节点路径
    UrlTreeNodeType type;       // 节点类型
    Object data;                // 节点数据
    List<UrlTreeNode> children; // 子节点
}
