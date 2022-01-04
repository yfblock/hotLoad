package io.github.yfblock.frame.Controller;

import io.github.yfblock.frame.Annotations.Controller;
import io.github.yfblock.frame.Annotations.RequestMapping;
import io.github.yfblock.frame.Annotations.RequestParam;
import io.github.yfblock.frame.Core.Analyzer.UrlTreeNode;
import io.github.yfblock.frame.Core.ModelController;
import io.github.yfblock.frame.Core.Template.Template;
import io.github.yfblock.frame.utils.ModelOperator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/home")
public class WelcomeController {

    @Autowired
    public UrlTreeNode urlTreeNode;

    @RequestMapping("/")
    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/index").forward(request, response);
    }

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

    @RequestMapping("/test")
    public UrlTreeNode test(ModelOperator modelOperator) {
        System.out.println(modelOperator);
        return this.urlTreeNode;
    }
}
