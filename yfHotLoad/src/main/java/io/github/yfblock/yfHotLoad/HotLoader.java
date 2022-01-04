package io.github.yfblock.yfHotLoad;

import io.github.yfblock.yfHotLoad.Annotation.EntryApplication;
import io.github.yfblock.yfHotLoad.ClassSource.ClassSource;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HotLoader {
    private final ClassSource classSource;
    private Collection<Class<?>> classes;
    public HotLoader(ClassSource classSource) {
        this.classSource = classSource;
        this.getClassFromSource();
    }

    /**
     * get entry class which surrounded by EntryApplication
     * @return the target Class
     */
    public Class<?> getEntryClass() {
        for(Class<?> clz : this.classes) {
            if(clz.getAnnotation(EntryApplication.class) != null) {
                return clz;
            }
        }
        return null;
    }

    /**
     * get Classes form source by Annotation
     * @param annotationClass annotationClass
     * @return list of class
     */
    public List<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotationClass) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        for(Class<?> clz :this.classes) {
            if(clz.getAnnotation(annotationClass) != null) {
                classes.add(clz);
            }
        }
        return classes;
    }

    /**
     * get all classes
     * @return classes
     */
    public List<Class<?>> getClasses() {
        return new ArrayList<>(this.classes);
    }

    /**
     * get Classes from source by packageName
     * @param packageName the name of package which you want to get
     * @return the list of class
     */
    public List<Class<?>> getClassesByPackage(String packageName) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        for(Class<?> clz : this.classes) {
            if(clz.getName().indexOf(packageName) == 0) {
                classes.add(clz);
            }
        }
        return classes;
    }

    /**
     * get Classes from source by packageName
     * @param packageName the name of package which you want to get
     * @return the list of class
     */
    public List<Class<?>> getSubClassesByPackage(String packageName) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        for(Class<?> clz : this.classes) {
            if(clz.getName().indexOf(packageName) == 0 && clz.getName().lastIndexOf('.') != packageName.length()) {
                classes.add(clz);
            }
        }
        return classes;
    }

    /**
     * get classes by parent class
     * @param parentClass the class to compare
     * @return list of class
     */
    public List<Class<?>> getClassesByParentClass(Class<?> parentClass) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        for(Class<?> clz : this.classes) {
            if(this.isParentClass(clz, parentClass)) {
                classes.add(clz);
            }
        }
        return classes;
    }

    /**
     * get class from source by simple name which equals param
     * @param simpleName the name to compare
     * @return the class will be got
     */
    public Class<?> getClassBySimpleName(String simpleName) {
        for(Class<?> clz : this.classes) {
            if(clz.getSimpleName().equals(simpleName)) {
                return clz;
            }
        }
        return null;
    }

    /**
     * get class from source by simple name which equals param
     * @param name the name to compare
     * @return the class will be got
     */
    public Class<?> getClassByName(String name) {
        for(Class<?> clz : this.classes) {
            if(clz.getName().equals(name)) {
                return clz;
            }
        }
        return null;
    }

    /**
     * get all interface
     * @return interface list
     */
    public List<Class<?>> getInterfaces() {
        ArrayList<Class<?>> interfaces = new ArrayList<>();
        for(Class<?> clz : this.classes) {
            if(clz.isInterface()) {
                interfaces.add(clz);
            }
        }
        return interfaces;
    }

    /**
     * get interfaces from source by packageName
     * @param packageName the name of package which you want to get
     * @return the list of interfaces
     */
    public List<Class<?>> getInterfacesByPackage(String packageName) {
        ArrayList<Class<?>> interfaces = new ArrayList<>();
        for(Class<?> clz : this.classes) {
            if(clz.getName().indexOf(packageName) == 0) {
                interfaces.add(clz);
            }
        }
        return interfaces;
    }

    /**
     * judge the targetClass whether is the parent of sourceClass
     * @param sourceClass the class to judge
     * @param targetClass the class to compare
     * @return is or not
     */
    public boolean isParentClass(Class<?> sourceClass, Class<?> targetClass) {
        Class<?> parentClass = sourceClass.getSuperclass();
        while (parentClass != null) {
            if(parentClass.equals(targetClass)) return true;
            parentClass = parentClass.getSuperclass();
        }
        return false;
    }

    /**
     * get Classes from source, such as jar and directory
     */
    public void getClassFromSource() {
        this.classes = this.classSource.getClasses();
    }
}
