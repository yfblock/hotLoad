package io.github.yfblock.yfHotLoad.Utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderUtil {
    /**
     * 从文件夹中获取classLoader
     * @param dirPath
     * @return
     * @throws MalformedURLException
     */
    public static ClassLoader getClassLoaderFromDirectory(String dirPath) throws MalformedURLException {
        File file = new File(dirPath);
        URL url = file.toURI().toURL();
        return new URLClassLoader(new URL[] {url});
    }

    public static ClassLoader getClassLoaderFromJar(String jar_path) throws MalformedURLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
//        File file = new File(jar_path);
//        ClassLoader classLoader = ClassLoaderUtil.class.getClassLoader();
//        return new URLClassLoader(new URL[]{file.toURI().toURL()});
        return addClassPath(jar_path);
    }

    public static ClassLoader addClassPath(String url) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        File file = new File(url);
//        URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        URLClassLoader classloader = (URLClassLoader) Thread.currentThread().getContextClassLoader();   // 使用上下文加载器
        Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        add.setAccessible(true);
        add.invoke(classloader, file.toURI().toURL());
        return classloader;
    }
}
