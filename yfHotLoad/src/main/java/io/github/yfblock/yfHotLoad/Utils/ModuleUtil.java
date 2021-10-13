package io.github.yfblock.yfHotLoad.Utils;

import io.github.yfblock.yfHotLoad.Config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 模块工具包
 */
public class ModuleUtil {
    /**
     * 获取模块列表
     * @return 模块列表名称
     */
    public static String[] getModules() {
        return FileUtil.getAllJarFiles(Config.Path);
    }

    /**
     * 从模块中获取所有类的名称
     * @return 返回类数组
     */
    public String[] getClassesFromModule(String modulePath) throws MalformedURLException {
        File file = new File(modulePath);
        if(file.isDirectory()) return null;
        URL url = file.toURI().toURL();
        ClassLoader loader = new URLClassLoader(new URL[]{url});
        return null;
    }

    /**
     * 加载类
     * @param jarPath jar包的路径
     * @param className 类的名称
     * @return Class类文件
     * @throws MalformedURLException URL拼装失败
     * @throws ClassNotFoundException 未找到类
     */
    public static Class<?> loadClass(String jarPath, String className) throws MalformedURLException, ClassNotFoundException {
        File file = new File(jarPath);
        URL url = file.toURI().toURL();
        ClassLoader loader = new URLClassLoader(new URL[] {url});
        return loader.loadClass(className);
    }

    /**
     * 从模块中获取模块的配置
     * @return 模块配置文件的对象
     */
    public Object getConfigFromModule() {
        return null;
    }
}
