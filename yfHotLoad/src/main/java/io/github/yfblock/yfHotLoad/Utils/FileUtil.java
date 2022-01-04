package io.github.yfblock.yfHotLoad.Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * 文件工具
 */
public class FileUtil {

    /**
     * 获取指定路径下所有文件 (仅仅获取一级文件夹)
     * @param path 路径
     * @return 文件名数组
     */
    public static String[] getAllFiles(String path) {
        File rootFile = new File(path);
        ArrayList<String> files = new ArrayList<>();
        if(rootFile.isFile()) {
            files.add(rootFile.getName());
        }else {
            File[] targetFiles = rootFile.listFiles();
            if(!(targetFiles == null || targetFiles.length == 0))
                for(File file : targetFiles) {
                    if(!file.isFile()) continue;
                    files.add(file.getName());
                }
        }
        return files.toArray(new String[0]);
    }

    /**
     * 获取指定路径下所有jar包 (仅仅获取一级文件夹)
     * @param path 路径
     * @return 文件名数组
     */
    public static String[] getAllJarFiles(String path) {
        String[] files = getAllFiles(path);
        ArrayList<String> jarFiles = new ArrayList<>();
        for(String filename:files) {
            if(filename.lastIndexOf(".jar") == filename.length() - 4) {
                jarFiles.add(filename);
            }
        }
        return jarFiles.toArray(new String[0]);
    }

    /**
     * 获取类
     * @param loader classLoad
     * @param className 类名称
     * @return class对象
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public static Class<?> loadClass(ClassLoader loader, String className) throws ClassNotFoundException {
        return loader.loadClass(className);
    }
}
