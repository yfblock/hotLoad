package Test;

import io.github.yfblock.frame.Annotations.Controller;
import io.github.yfblock.frame.Annotations.RequestMapping;
import io.github.yfblock.frame.Core.ModelController;

@Controller
@RequestMapping("/test")
public class ControllerTest extends ModelController {

    @RequestMapping("/index")
    public String index() {
        return "Hello";
    }

}
