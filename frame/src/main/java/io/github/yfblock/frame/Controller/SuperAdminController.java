package io.github.yfblock.frame.Controller;

import io.github.yfblock.frame.Annotations.Controller;
import io.github.yfblock.frame.Annotations.RequestMapping;
import io.github.yfblock.frame.Annotations.RequestParam;
import io.github.yfblock.frame.Core.Constant.AttributeParams;
import io.github.yfblock.frame.Core.ModelController;
import io.github.yfblock.frame.Core.Template.Template;
import io.github.yfblock.frame.utils.ModelOperator;
import io.github.yfblock.frame.utils.TemplateUtil;
import io.github.yfblock.yfHotLoad.Utils.FileUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

@RequestMapping("/super-admin")
@Controller
public class SuperAdminController{
    @RequestMapping("/login")
    public Template login() {
        return TemplateUtil.build("templates/login.ftl");
    }

    @RequestMapping("/index")
    public Template index() {
        return TemplateUtil.build("templates/index.ftl");
    }

    @RequestMapping("/modules")
    public Template modules() {
        return TemplateUtil.build("templates/modules.ftl");
    }

    @RequestMapping("/upload")
    public Map<String, String> upload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 上传文件存储目录
        final String UPLOAD_DIRECTORY = "WEB-INF/extra";

        // 上传配置
        final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
        final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
        final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB

        // 检测是否为多媒体上传
        Map<String, String> result = new HashMap<>();
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果不是则停止
            result.put("code", "500");
            return result;
        }

        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);

        // 设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);

        // 设置最大请求值 (包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);

        // 中文处理
        upload.setHeaderEncoding("UTF-8");

        // 构造临时路径来存储上传的文件
        // 这个路径相对当前应用的目录
        String uploadPath = request.getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;


        // 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try {
            // 解析请求的内容提取文件数据
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);

            if (formItems != null && formItems.size() > 0) {
                // 迭代表单数据
                for (FileItem item : formItems) {
                    // 处理不在表单中的字段
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
                        // 在控制台输出文件的上传路径
                        System.out.println(filePath);
                        // 保存文件到硬盘
                        item.write(storeFile);
                        request.setAttribute("message",
                                "文件上传成功!");
                    }
                }
            }
        } catch (Exception ex) {
            result.put("code", "500");
            result.put("msg", ex.getMessage());
            return result;
        }

        result.put("code", "200");
        return result;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/module_list")
    public Map<String, Object> getModules(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String[] jarFiles = FileUtil.getAllJarFiles((String) request.getAttribute(AttributeParams.JAR_LIB_PATH));
        Map<String, String> jarMounted = (Map<String, String>) request.getAttribute(AttributeParams.MODULE_MOUNTED_MAP);
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
    public Map<String, Object> mountModule(ModelOperator modelOperator, HttpServletRequest request, @RequestParam(value = "module_name", required = true) String moduleName) {
//        request.setAttribute("OperatorMethod", "mountModule");
//        request.setAttribute("mountModule", moduleName);
        System.out.println(modelOperator);
        modelOperator.mountModule(moduleName, "");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "挂载成功");
        return result;
    }

    @RequestMapping("/unmount_module")
    public Map<String, Object> unmountModule(ModelOperator modelOperator,HttpServletRequest request, @RequestParam(value = "module_name", required = true) String moduleName) {
        modelOperator.umountModule(moduleName, "");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "卸载成功");
        return result;
    }

    @RequestMapping("/init")
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
