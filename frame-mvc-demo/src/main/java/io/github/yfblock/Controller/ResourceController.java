package io.github.yfblock.Controller;

import io.github.yfblock.frameMvc.Annotations.Controller;
import io.github.yfblock.frameMvc.Annotations.RequestMapping;
import io.github.yfblock.frameMvc.utils.ModelOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ResourceController {

    @RequestMapping("/static/*")
    public String get(ModelOperator modelOperator, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("Hello", request.getPathInfo());
        map.put("extraPath", modelOperator.getExtraPath());
        System.out.println(modelOperator.getExtraPath());
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("./static/" + modelOperator.getExtraPath());
        System.out.println(inputStream);
        String result = "";
//        if(inputStream != null) {
//            result = new String(inputStream.read());
////            result = Arrays.toString(inputStream.readAllBytes());
//        }
        return result;
    }
}
