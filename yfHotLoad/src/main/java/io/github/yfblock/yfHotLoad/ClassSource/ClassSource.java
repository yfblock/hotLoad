package io.github.yfblock.yfHotLoad.ClassSource;

import java.util.Collection;
import java.util.List;

/**
 * the interface of class source, give a method which name is getClasses
 */
public interface ClassSource {
    Collection<Class<?>> getClasses();
}
