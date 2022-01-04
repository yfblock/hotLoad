package io.github.yfblock.frameMvc.Core.Template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 模板处理器
 */
public class TemplateHandler {
    Configuration conf;         // FreeMarker配置类
    Class<?> caller;            // 调用者类的class, 为了实现模板调用
    Map<String, Object> globalData = new HashMap<>();   // 公共数据
    public TemplateHandler(Class<?> caller) {
        this.caller = caller;
        init();

        try {
            Properties properties = new Properties();
            InputStream inputStream = this.caller.getResourceAsStream("/template.properties");
            if (inputStream == null) return;
            properties.load(inputStream);
            for(String key:properties.stringPropertyNames()) {
                globalData.put(key, properties.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化FreeMarker
     */
    void init() {
        // 创建配置对象
        conf = new Configuration();
        // 加载模板文件(类加载模式)
        conf.setClassForTemplateLoading(this.caller, "/");

        // 加载模板文件(文件夹加载方式——暂时弃用)
        // String templateDir= Objects.requireNonNull(this.getClass().getResource("/")).getPath();
        // conf.setDirectoryForTemplateLoading(new File(templateDir));
    }

    /**
     * 调用模板引擎显示页面
     * @param templateSrc   模板路径
     * @param data          渲染数据
     * @param out           输出Writer
     * @throws IOException  IO异常
     * @throws TemplateException 模板异常
     */
    public void process(String templateSrc, Map<String, Object> data, Writer out) throws IOException, TemplateException {
        Template template = conf.getTemplate(templateSrc);
        data.putAll(globalData);
        template.process(data, out);
    }
}
