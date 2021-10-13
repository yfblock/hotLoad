package io.github.yfblock.yfHotLoad.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Jar包操作工具包
 */
public class JarLoaderUtil {
    private final ClassLoader classLoader;      // ClassLoader
    private final String jar_path;              // 需要读取的jar包的路径
    private String packageName;                 // 包名称
    private JarFile jarFile;                    // jar文件
    private final Map<String, Class<?>> classes = new HashMap<>();  // 所有Classes

    /**
     * 构造函数
     * @param jar_path jar文件路径
     * @param packageName 包名称
     * @throws IOException 读写错误
     */
    public JarLoaderUtil(String jar_path, String packageName) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.classLoader = ClassLoaderUtil.getClassLoaderFromJar(jar_path);
        this.jar_path = jar_path;
        this.packageName = packageName;
        this.refresh();
    }

    /**
     * 重新读取jar包内的类数据
     * @throws IOException 读写错误
     */
    public void refresh() throws IOException, ClassNotFoundException {

    }

    /**
     * 获取配置文件
     * @param name 配置文件名称
     * @return Properties对象
     * @throws IOException 读写错误
     */
    public Properties getPropertiesFile(String name) throws IOException {
        InputStream inputStream = this.getInputStream(name);
        if(inputStream == null) return null;
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    /**
     * 获取文件数据
     * @param name 文件名称
     * @return InputStream
     * @throws IOException 读写错误
     */
    public InputStream getInputStream(String name) throws IOException {
        return this.jarFile.getInputStream(this.jarFile.getEntry(name));
    }

    /**
     * 获取所有匹配类的Map
     * @return Map<String, Class<?>> 匹配类的Map
     */
    public Map<String, Class<?>> getClasses() {
        return this.classes;
    }
}
