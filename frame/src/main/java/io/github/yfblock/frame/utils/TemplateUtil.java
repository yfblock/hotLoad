package io.github.yfblock.frame.utils;

import io.github.yfblock.frame.Core.Template.Template;

import java.util.HashMap;
import java.util.Map;

/**
 * 快速构建模板的工具类
 */
public class TemplateUtil {
    /**
     * 构建模板
     * @param path 模板路径
     * @param data 模板渲染数据
     * @return 模板对象
     */
    public static Template build(String path, Map<String, Object> data) {
        Template template = new Template();
        template.setPath(path);
        template.setData(data);
        return template;
    }

    /**
     * 构建模板
     * @param path 模板路径
     * @return 模板对象
     */
    public static Template build(String path) {
        return TemplateUtil.build(path, new HashMap<>());
    }
}
