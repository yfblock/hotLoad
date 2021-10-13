package io.github.yfblock.yfHotLoad.ClassSource;


import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClassSource implements ClassSource{

    private final String jarPath;
    private final String packageName;
    private ClassLoader classLoader;
    private Map<String, Class<?>> classesMap = new HashMap<>();

    public JarClassSource(String jarPath) {
        this(jarPath, "");
    }

    public JarClassSource(String jarPath, String packageName) {
        this.jarPath        = jarPath;
        this.packageName    = packageName;
        try {
            URL fileURL = new File(jarPath).toURI().toURL();
            classLoader = new URLClassLoader(new URL[]{fileURL}, Thread.currentThread().getContextClassLoader());
            this.loadClasses();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<Class<?>> getClasses() {
        return this.classesMap.values();
    }

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
//                System.out.println(className);
                Class<?> cls = this.classLoader.loadClass(className);
                classesMap.put(className, cls);
            }
        }
    }
}
