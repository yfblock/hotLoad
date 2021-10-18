package io.github.yfblock.frame.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ModulePropertiesUtil {
    Properties props = new Properties();
    protected final String filePath;
    protected final String modulePath;
    public ModulePropertiesUtil(String modulePath) {
        this.modulePath = modulePath;
        this.filePath = modulePath + ".properties";
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(this.filePath));
            props.load(in);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        // 如果配置为空 则初始化配置
        if(this.props.isEmpty()) this.initProperties();
    }

    public String getExtraUrl() {
        return this.props.getProperty("extraUrl");
    }

    /**
     * 初始化配置文件
     */
    public void initProperties() {
        this.props.setProperty("defaultLoad", "false");
        String moduleName = this.modulePath.substring(this.modulePath.lastIndexOf(File.separatorChar) + 1);
        moduleName = '/' + moduleName.substring(0, moduleName.indexOf('.'));
        this.props.setProperty("extraUrl", moduleName);
    }

    public void store() {
        try {
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
            FileOutputStream fos = new FileOutputStream(this.filePath, false);
            // 以适合使用 load 方法加载到 Properties 表中的格式，
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流
            props.store(fos, "");
        } catch (IOException e) {
            System.err.println("属性文件更新错误");
        }
    }

    public boolean getDefaultLoad() {
        return this.props.getProperty("defaultLoad", "false").equals("true");
    }

    public void setDefaultLoad(boolean defaultLoad) {
        this.props.setProperty("defaultLoad", Boolean.toString(defaultLoad));
    }
}
