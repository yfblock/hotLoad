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
    Object extra;               // 附加数据
    List<UrlTreeNode> children; // 子节点

    public UrlTreeNode(String url, UrlTreeNodeType type, Object data, List<UrlTreeNode> children) {
        this.url = url;
        this.type = type;
        this.data = data;
        this.children = children;
    }
}
