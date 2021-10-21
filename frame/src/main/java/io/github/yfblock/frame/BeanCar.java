package io.github.yfblock.frame;

import io.github.yfblock.frame.Core.Analyzer.UrlTreeNode;
import io.github.yfblock.frame.Core.Analyzer.UrlTreeNodeType;
import io.github.yfblock.frame.utils.ModulePropertiesUtil;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanCar {
    @Bean(name = "urlTreeNode")
    public UrlTreeNode getUrlTreeNode() {
        return new UrlTreeNode("/", UrlTreeNodeType.BLANK, null, null, null);
    }
}
