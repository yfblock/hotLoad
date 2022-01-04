package io.github.yfblock.yfHotLoad.ClassSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 基本类源
 */
public class BaseClassSource implements ClassSource {
    protected ClassLoader classLoader;            // 类加载器
    protected final Map<String, Class<?>> classesMap = new HashMap<>();   // 类表

    /**
     * 获取所有类
     * @return 类列表
     */
    @Override
    public Collection<Class<?>> getClasses() {
        return this.classesMap.values();
    }
}
