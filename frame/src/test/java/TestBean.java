import io.github.yfblock.frame.BeanCar;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class TestBean {
    @Test
    public void testBean() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanCar.class);
        applicationContext.refresh();
            System.out.println(Arrays.toString(applicationContext.getBeanDefinitionNames()));
    }
}
