import io.github.yfblock.yfHotLoad.ClassSource.BaseClassSource;
import io.github.yfblock.yfHotLoad.ClassSource.ClassSource;
import io.github.yfblock.yfHotLoad.ClassSource.DirClassSource;
import io.github.yfblock.yfHotLoad.HotLoader;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

public class TestLoader {
    @Test
    public void testLoadClass() {
        String dirUrl = "/home/yufeng/Code/java/yfHotLoad/yfHotLoad/build/classes/java/main";
        DirClassSource dirClassSource = new DirClassSource(dirUrl);
        HotLoader hotLoader = new HotLoader(dirClassSource);
//        List<Class<?>> classes = hotLoader.getClassesByParentClass();
//        for(Class<?> clz : classes) {
//            System.out.println(clz);
//        }
        Class<?> clz = hotLoader.getClassByName("io.github.yfblock.yfHotLoad.ClassSource.DirClassSource");
        System.out.println(clz);
    }
}
