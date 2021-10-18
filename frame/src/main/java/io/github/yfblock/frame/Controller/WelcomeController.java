package io.github.yfblock.frame.Controller;

import io.github.yfblock.frame.Annotations.Controller;
import io.github.yfblock.frame.Annotations.RequestMapping;
import io.github.yfblock.frame.Annotations.RequestParam;
import io.github.yfblock.frame.Core.ModelController;
import io.github.yfblock.frame.Core.Template.Template;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/home")
@Controller
public class WelcomeController extends ModelController {

    @RequestMapping("/index")
    public Template login(@RequestParam("custom") String name,
                          @RequestParam(value = "id", defaultValue = "0") Integer id){
        System.out.println(name);
        Map<String, Object> data = new HashMap<>();
        data.put("title", id);
        data.put("name", name);
        Template template = new Template();
        template.setPath("test.ftl");
        template.setData(data);
        return template;
    }
}
