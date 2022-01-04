package io.github.yfblock.yfHotLoad.ClassSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the interface of class source, give a method which name is getClasses
 */
public interface ClassSource {
    Collection<Class<?>> getClasses();
}
