package io.github.yfblock.frame.Controller;

import io.github.yfblock.frame.Annotations.Controller;
import io.github.yfblock.frame.Annotations.RequestMapping;
import io.github.yfblock.frame.Annotations.RequestParam;
import io.github.yfblock.frame.Core.ModelController;
import io.github.yfblock.frame.Core.Template.Template;
import io.github.yfblock.frame.utils.TemplateUtil;
import io.github.yfblock.yfHotLoad.Utils.FileUtil;

import java.util.*;

@RequestMapping("/super-admin")
@Controller
public class SuperAdminController extends ModelController {
    public Template login() {
        return TemplateUtil.build("templates/login.ftl");
    }

    public Template index() {
        return TemplateUtil.build("templates/index.ftl");
    }

    public Template modules() {
        return TemplateUtil.build("templates/modules.ftl");
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/module_list")
    public Map<String, Object> getModules() {
        Map<String, Object> result = new HashMap<>();
        String[] jarFiles = FileUtil.getAllJarFiles((String) request.getAttribute("jarLibPath"));
        Map<String, String> jarMounted = (Map<String, String>) request.getAttribute("jarMounted");
        Set<String> jarMountedNames = jarMounted.keySet();;
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        for(String jarFile : jarFiles) {
            Map<String, Object> module = new HashMap<>();
            module.put("moduleName", jarFile);
            module.put("status", jarMountedNames.contains(jarFile));
            data.add(module);
        }
        result.put("msg", "");
        result.put("code", 0);
        result.put("count", jarFiles.length);
        result.put("data", data);
        return result;
    }

    @RequestMapping("/mount_module")
    public Map<String, Object> mountModule(@RequestParam(value = "module_name", required = true) String moduleName) {
        request.setAttribute("mountModule", moduleName);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "挂载成功");
        return result;
    }

    @RequestMapping("/unmount_module")
    public Map<String, Object> unmountModule(@RequestParam(value = "module_name", required = true) String moduleName) {
        request.setAttribute("unmountModule", moduleName);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "卸载成功");
        return result;
    }

    public Map<String, Object> init() {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> homeInfo = new HashMap<>();
        homeInfo.put("title", "首页");
        homeInfo.put("href", "page/welcome-1.html?t=1");
        result.put("homeInfo", homeInfo);
        Map<String, String> logoInfo = new HashMap<>();
        logoInfo.put("title", "御风阁");
        logoInfo.put("image", "/static/images/logo.png");
        logoInfo.put("href", "");
        result.put("logoInfo", logoInfo);
        ArrayList<Map<String, Object>> menuInfo = new ArrayList<>();
        Map<String, Object> menuItem = new HashMap<>();
        menuItem.put("title", "框架管理");
        menuItem.put("icon", "fa fa-home");
        menuItem.put("href", "/super-admin/modules");
        menuItem.put("target", "_self");
        menuInfo.add(menuItem);
        result.put("menuInfo", menuInfo);
        return result;
    }


}
