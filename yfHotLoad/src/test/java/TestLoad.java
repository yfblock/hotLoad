import io.github.yfblock.yfHotLoad.ClassSource.DirClassSource;
import io.github.yfblock.yfHotLoad.ClassSource.JarClassSource;
import io.github.yfblock.yfHotLoad.HotLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestLoad {

    @Test
    public void testJarLoad() {
        String jar_path = "/home/yufeng/Code/java/yfHotLoad/Test1/build/libs/Test1-1.0-SNAPSHOT.jar";
        HotLoader hotLoader = new HotLoader(new JarClassSource(jar_path));

        List<Class<?>> classList = hotLoader.getClassesByParentClass(Object.class);
        for(Class<?> clz : classList) {
            System.out.println(clz.getName());
        }
    }

    @Test
    public void testDirLoad() {
        String jar_path = "/home/yufeng/Code/java/yfHotLoad/Test1/build/classes/java/main";
        HotLoader hotLoader = new HotLoader(new DirClassSource(jar_path));

        List<Class<?>> classList = hotLoader.getClassesByParentClass(Object.class);
        for(Class<?> clz : classList) {
            System.out.println(clz.getName());
        }
    }
}
