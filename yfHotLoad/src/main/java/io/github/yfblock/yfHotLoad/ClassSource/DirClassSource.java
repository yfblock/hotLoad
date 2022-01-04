package io.github.yfblock.yfHotLoad.ClassSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件夹源 动态加载文件夹中的数据
 */
public class DirClassSource extends BaseClassSource{
    private final String dirPath;               // 文件夹路径
    private final String packageName;           // 包名称

    /**
     * 构造函数
     * @param dirPath 文件夹路径
     */
    public DirClassSource(String dirPath) {
        this(dirPath, "");
    }

    /**
     * 构造函数
     * @param dirPath 文件夹路径
     * @param packageName 包名称
     */
    public DirClassSource(String dirPath, String packageName) {
        this.dirPath        = dirPath;
        this.packageName    = packageName;
        try {
            URL fileURL = new File(dirPath).toURI().toURL();
            this.classLoader = new URLClassLoader(new URL[]{fileURL}, Thread.currentThread().getContextClassLoader());
            this.loadClasses();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造函数
     * @param classLoader 类加载器
     */
    public DirClassSource(ClassLoader classLoader) {
        this(classLoader, "");
    }

    /**
     * 构造函数
     * @param classLoader 类加载器
     * @param packageName 包名称
     */
    public DirClassSource(ClassLoader classLoader, String packageName) {
        this.dirPath = "";
        this.packageName = packageName;
        try {
            this.classLoader = classLoader;
            this.loadClasses();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取类
     * @return 获取类的数据集
     */
    @Override
    public Collection<Class<?>> getClasses() {
        return this.classesMap.values();
    }

    /**
     * 加载类
     * @throws IOException 数据读写异常
     * @throws ClassNotFoundException 未找到类异常
     */
    public void loadClasses() throws IOException, ClassNotFoundException {
        String packagePath = this.packageName.replaceAll("\\.", File.separator);
        Enumeration<URL> urls = this.classLoader.getResources(packagePath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if(url.getProtocol().equals("file") && url.getPath().indexOf(this.dirPath) == 0)
            {
                this.loadClassFromDir(new File(url.getPath()), this.packageName);
            }
        }
    }

    /**
     * 从文件夹里读取类到
     * @param directory 文件夹路径
     * @param packageName 要读取的包名称
     * @throws ClassNotFoundException 未找到类
     */
    public void loadClassFromDir(File directory, String packageName) throws ClassNotFoundException {
        if (!directory.exists()) return;

        File[] listFiles = directory.listFiles();
        if (listFiles == null || listFiles.length == 0) return;
        // 遍历包下的文件
        for (File file : listFiles) {
            // 如果是类文件  则加载到Class中
            if (file.isFile() && file.getPath().endsWith(".class")) {
                String classSimpleName = file.getName().substring(0, file.getName().length() - 6);
                String className = (packageName.equals("")?"":packageName + ".") + classSimpleName;
                this.classesMap.put(className, this.classLoader.loadClass(className));
//                this.loadClass(packageName + "." + classSimpleName);
            }

            if (file.isDirectory()) {
                loadClassFromDir(file, packageName.equals("")? file.getName():packageName + "." + file.getName());
            }
        }
    }
}
