package io.github.yfblock.yfHotLoad.ClassSource;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Jar包形式的类来源
 */
public class JarClassSource extends BaseClassSource{

    private final String jarPath;       // jar包路径
    private final String packageName;   // 要获取的包名称

    public JarClassSource(String jarPath) {
        this(jarPath, "");
    }

    /**
     * 构造函数
     * @param jarPath jar包路径
     * @param packageName 要加载的包名称
     */
    public JarClassSource(String jarPath, String packageName) {
        this.jarPath        = jarPath;
        this.packageName    = packageName;
        try {
            URL fileURL = new File(jarPath).toURI().toURL();
            this.classLoader = new URLClassLoader(new URL[]{fileURL}, Thread.currentThread().getContextClassLoader());
            this.loadClasses();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取类
     * @throws IOException  读写异常
     * @throws ClassNotFoundException 未找到类异常
     */
    public void loadClasses() throws IOException, ClassNotFoundException {
        String packagePath = packageName.replaceAll("\\.", "/");
        JarFile jarFile = new JarFile(this.jarPath);
        Enumeration<JarEntry> entrys = jarFile.entries();
        while (entrys.hasMoreElements()) {
            JarEntry jarEntry = entrys.nextElement();
            if(jarEntry.getName().indexOf(packagePath) == 0 && jarEntry.getName().endsWith(".class"))
            {
                String className = jarEntry.getName().substring(0, jarEntry.getName().lastIndexOf(".class"))
                        .replaceAll("/", "\\.");
                Class<?> cls = this.classLoader.loadClass(className);
                classesMap.put(className, cls);
            }
        }
    }
}
