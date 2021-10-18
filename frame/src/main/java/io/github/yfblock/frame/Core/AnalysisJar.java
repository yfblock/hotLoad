package io.github.yfblock.frame.Core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 分析Jar包，获得类和类内的所有url
 */
public class AnalysisJar {
    // jar文件对象
    private JarFile jarFile;
    // 配置文件
    private Properties properties;
    // url对应的类
    private Map<String, Class<?>> classes = new HashMap<>();
    // 配置文件名称
    private final String propertiesFileName = "application.properties";
    // 类加载器
    private ClassLoader classLoader;

    /**
     * 设置jar包文件路径，分析jar包文件的所有类
     * @param jarPath jar包路径
     * @throws IOException 读写异常
     */
    public Map<String, Class<?>> analyze(String jarPath) throws
            IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException,
            ClassNotFoundException {
        this.jarFile = new JarFile(jarPath);                    // 设置jarFile
        this.getProperties();                                   // 获取配置文件
        String packageName = this.properties.getProperty("yfblock.controller.package");          // 获取url对应的包名称
        String packagePath = packageName.replaceAll("\\.", "/");                // 将包名称转化为路径

        this.getClassLoader(jarPath);                           // 获取类加载器

        Enumeration<JarEntry> entrys = jarFile.entries();
        while (entrys.hasMoreElements()) {
            JarEntry jarEntry = entrys.nextElement();
            // 判断类是否为类文件或者是否为需要的类
            if(jarEntry.getName().indexOf(packagePath) == 0 && jarEntry.getName().endsWith(".class"))
            {
                String className = jarEntry.getName().substring(0, jarEntry.getName().lastIndexOf(".class"))
                        .replaceAll("/", "\\.");
                Class<?> cls = this.classLoader.loadClass(className);       // 加载类
                this.classes.put(className, cls);                           // 将类放入所有类中
            }
        }
        return this.classes;
    }

    /**
     * 获取配置文件
     * @throws IOException 读写异常
     */
    public void getProperties() throws IOException {
        // 获取文件输入流
        InputStream inputStream = this.jarFile.getInputStream(this.jarFile.getEntry(propertiesFileName));
        this.properties = new Properties(); // 重载配置类
        if(inputStream == null) return;     // 如果输入流为空返回
        this.properties.load(inputStream);  // 不为空则加载文件
    }

    /**
     * 获取类加载器
     * @param jarPath jar文件路径
     */
    public void getClassLoader(String jarPath) throws
            NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {

        // 加载jar文件
        File file = new File(jarPath);
        // 获取上下文类加载器
        URLClassLoader classloader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
        // 将jar包加入类加载器的环境中
        Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        add.setAccessible(true);
        add.invoke(classloader, file.toURI().toURL());
        // 设置类加载器
        this.classLoader = classloader;
    }

    /**
     * 获取额外的访问路径
     * @return 访问路径
     */
    public String getExtraUrl() {
        return this.properties.getProperty("yfblock.extra.url");
    }
}
