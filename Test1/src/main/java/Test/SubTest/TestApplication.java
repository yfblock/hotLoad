package Test.SubTest;


import io.github.yfblock.yfHotLoad.Annotation.EntryApplication;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;

@EntryApplication
public class TestApplication {
    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, InstantiationException {
//        ClassLoader classLoader = ClassLoaderUtil.getClassLoaderFromDirectory(TestApplication.class.getResource(".").getPath());
//        PackageLoaderUtil packageLoaderUtil = new PackageLoaderUtil(classLoader, "Test.SubTest");
//        File file = new File("/home/yufeng/Code/java/yfHotLoad/TestLoader/build/libs/TestLoader-1.0-SNAPSHOT.jar");
//        JarLoaderUtil jarLoaderUtil = new JarLoaderUtil(file.getPath(), "Custom");
//        Map<String, Class<?>> classes = jarLoaderUtil.getClasses();
//        for(String className : classes.keySet()) {
//            if(className.equals("Custom.CustomApplication")) {
//                Class<?> clz = classes.get(className);
//                Object obj = clz.newInstance();
//                Method m = clz.getMethod("run");
//                m.invoke(obj);
//            }
//        }
    }

    public void test() {
        System.out.println("Test in Test1");
    }
}
