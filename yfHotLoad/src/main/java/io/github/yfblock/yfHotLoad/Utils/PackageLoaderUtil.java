package io.github.yfblock.yfHotLoad.Utils;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PackageLoaderUtil {
    private final ClassLoader classLoader;
    private final String packageName;
    private final Map<String, Class<?>> classes = new HashMap<>();

    private static final String CLASS_SUFFIX = ".class";
    private static final int CLASS_SUFFIX_LENGTH = 6;
    private static final String CLASS_FILE_PREFIX = File.separator + "classes"  + File.separator;
    private static final String PACKAGE_SEPARATOR = ".";

    public PackageLoaderUtil(ClassLoader classLoader, String packageName) {
        this.classLoader = classLoader;
        this.packageName = packageName;
        this.refreshClassList();
    }

    /**
     * 重新加载类列表
     */
    @SneakyThrows
    public void refreshClassList() {
        classes.clear();
        String packagePath = this.packageName.replaceAll("\\.", File.separator);
        Enumeration<URL> urls = this.classLoader.getResources(packagePath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if(url.getProtocol().equals("file"))
            {
//                this.getAllClassNameByFile(new File(url.getPath()), this.packageName);
                this.getClassesInTheDirectory(new File(url.getPath()), this.packageName);
            }
        }
    }

    private void getClassesInTheDirectory(File directory, String packageName) {
        if(!directory.exists()) return;

        File[] listFiles = directory.listFiles();
        if(listFiles == null || listFiles.length == 0) return;
        // 遍历包下的文件
        for (File file : listFiles) {
            // 如果是类文件  则加载到Class中
            if(file.isFile() && file.getPath().endsWith(CLASS_SUFFIX))
            {
                String classSimpleName = file.getName().substring(0, file.getName().length() - CLASS_SUFFIX_LENGTH);
                this.loadClass(packageName + "." + classSimpleName);
            }

            if(file.isDirectory()) {
                getClassesInTheDirectory(file, packageName + "." + file.getName());
            }
        }
    }

    /**
     * 加载class到map中
     * @param classFullName class名称和路径
     */
    private void loadClass(String classFullName) {
        try {
            if(!classFullName.contains("$")) {
                this.classes.put(classFullName, this.classLoader.loadClass(classFullName));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回classes
     * @return classes Map
     */
    public Map<String, Class<?>> getClasses() {
        return this.classes;
    }
}
