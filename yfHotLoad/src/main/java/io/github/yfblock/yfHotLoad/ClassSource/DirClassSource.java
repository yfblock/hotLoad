package io.github.yfblock.yfHotLoad.ClassSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class DirClassSource implements ClassSource{
    private final String dirPath;
    private final String packageName;
    private ClassLoader classLoader;
    private Map<String, Class<?>> classesMap = new HashMap<>();

    public DirClassSource(String dirPath) {
        this(dirPath, "");
    }

    public DirClassSource(String dirPath, String packageName) {
        this.dirPath        = dirPath;
        this.packageName    = packageName;
        try {
            URL fileURL = new File(dirPath).toURI().toURL();
            classLoader = new URLClassLoader(new URL[]{fileURL}, Thread.currentThread().getContextClassLoader());
            this.loadClasses();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public DirClassSource(ClassLoader classLoader) {
        this(classLoader, "");
    }

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

    @Override
    public Collection<Class<?>> getClasses() {
        return this.classesMap.values();
    }

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
