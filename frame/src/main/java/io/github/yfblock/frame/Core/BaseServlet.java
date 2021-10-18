package io.github.yfblock.frame.Core;

import io.github.yfblock.frame.Annotations.Controller;
import io.github.yfblock.frame.Annotations.RequestMapping;
import io.github.yfblock.frame.Core.Constant.AttributeParams;
import io.github.yfblock.frame.utils.ModulePropertiesUtil;
import io.github.yfblock.yfHotLoad.ClassSource.DirClassSource;
import io.github.yfblock.yfHotLoad.ClassSource.JarClassSource;
import io.github.yfblock.yfHotLoad.HotLoader;
import io.github.yfblock.yfHotLoad.Utils.FileUtil;
import lombok.SneakyThrows;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 控制器
 */
@WebServlet(value = "/", loadOnStartup = 1)
public class BaseServlet extends HttpServlet {
    protected HotLoader nativeHotLoader;                             // 本包加载工具
    protected Map<String, Map<String, ModelController>> containerMap;// 路径对应的控制器
    protected String url = "";                                       // url处理
    protected AnalysisJar analysisJar;                               // Jar包处理器
    protected String jarLibPath;                                     // 模块Jar包路径
    protected Map<String, String> jarMounted;                        // 已经挂载的Jar包
    protected Map<String, HotLoader> jarLoaders;                     // Jar包读取器,分包读取
    protected HttpServletRequest request;                            // 请求对象

    /**
     * 初始化信息，并创建控制器，初始化路径信息
     */
    @SneakyThrows
    @Override
    public void init() {
        // 初始化内部变量
        System.out.println("项目初始化......");
        nativeHotLoader = new HotLoader(new DirClassSource(ModelController.class.getClassLoader(),
                "io.github.yfblock.frame.Controller"));
        analysisJar  = new AnalysisJar();
        containerMap = new HashMap<>();
        jarMounted = new HashMap<>();


        // 获取路径
        String rootPath =
                Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        if (rootPath != null)
            jarLibPath = rootPath + "../../../../extra/";           // 额外的jar包的路径

        // 扫描控制器
        System.out.println("扫描控制器......");
        this.loadNativeClasses();
        this.loadExternalClasses();
        System.out.println("扫描控制器完毕......");

        // 项目初始化成功
        System.out.println("项目初始化成功!");
    }

    /**
     * 属性设置
     *
     * @param req httpServletRequest
     * @param res httpServletResponse
     */
    @SneakyThrows
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) {
        this.request = req;
        this.url = req.getServletPath().toLowerCase(Locale.ROOT);
        // 设置全局访问的对象
        req.setAttribute(AttributeParams.JAR_LIB_PATH, jarLibPath);
        req.setAttribute(AttributeParams.MODULE_MOUNTED_MAP, jarMounted);

        // 获取控制器Map 如果没有找到则返回进行404处理
        Map<String, ModelController> controllerMap = this.getControllerMap();
        if(controllerMap == null) {
            // 未找到指定的控制器
            System.out.println("未找到容器" + req.getServletPath().toLowerCase(Locale.ROOT));
            res.sendError(404, "Not found the container");
            return;
        }

        // 获取控制器class
        ModelController controller = this.getController(controllerMap);
        if (controller == null) {
            // 未找到指定的控制器
            System.out.println("未找到控制器" + req.getServletPath().toLowerCase(Locale.ROOT));
            res.sendError(404, "Not found the controller");
            return;
        }

        // 转发给控制器处理结果
        controller.setRequestAndResponse(req, res);
        controller.url = this.url.trim();
        controller.handleUrlMethod();
    }

    public void handleController(Object obj, Method method) {

    }

    public void handleControllerMethod() {

    }

    /**
     * 处理额外操作 比如卸载模块 或者加载模块
     */
    public void handleExternalAction(HttpServletRequest req, HttpServletResponse res) {
        // 加载模块
        String moduleName = (String) req.getAttribute(AttributeParams.MODULE_TO_MOUNT);
        if(moduleName != null && !moduleName.isEmpty()) {
            List<String> modules = Arrays.asList(FileUtil.getAllJarFiles(jarLibPath));
            if(!modules.contains(moduleName)) return;
            ModulePropertiesUtil propertiesUtil = new ModulePropertiesUtil(jarLibPath + moduleName);
            this.loadSingleExternalClasses(jarLibPath, moduleName, propertiesUtil.getExtraUrl());
            propertiesUtil.setDefaultLoad(true);
            propertiesUtil.store();
        }

        // 卸载模块
        moduleName = (String) req.getAttribute(AttributeParams.MODULE_TO_UNMOUNT);
        if(moduleName != null && jarMounted.containsKey(moduleName)) {
            this.unloadSingleExternalClasses(jarLibPath, moduleName);
        }
    }

    /**
     * 获取匹配的控制器
     *
     * @return 最大匹配的控制器对象
     */
    public ModelController getController(Map<String, ModelController> controllerMap) {
        // 获取控制器
        if (this.url.length() == 0 || this.url.charAt(0) != '/') this.url = "/" + this.url;
        String matchUrl = this.getMatchObject(controllerMap);
        return controllerMap.get(matchUrl);
    }

    /**
     * 获取控制器Map
     * @return 控制器Map
     */
    public Map<String, ModelController> getControllerMap() {
        // 获取匹配的
        String matchUrl = this.getMatchObject(containerMap);
        return containerMap.get(matchUrl);
    }

    public void handleHotLoader(HotLoader hotLoader, String extraUrl) {
        Map<String, ModelController> controllerMap = new HashMap<>();
        for(Class<?> cls : hotLoader.getClassesByAnnotation(Controller.class))
            this.handleControllerClass(controllerMap, extraUrl, cls);
        containerMap.put(extraUrl, controllerMap);
    }

    /**
     * 加载本地的控制器类
     */
    public void loadNativeClasses() {
        System.out.println("--------------------加载本地控制器------------------------------");
        this.handleHotLoader(this.nativeHotLoader, "/");
        System.out.println("------------------加载本地控制器加载完毕--------------------------");
    }

    /**
     * 加载单个Jar包
     * @param jarPath Jar包路劲，目前仅仅可以在特定的文件夹下 extra 文件夹下
     */
    public void loadSingleExternalClasses(String jarPath, String jarName, String extraUrl) {
        // 初始化extraUrl
        extraUrl = extraUrl.toLowerCase(Locale.ROOT);
        // 创建包读取节点
        HotLoader hotLoader = new HotLoader(new JarClassSource(jarPath + jarName));
        // Jar包分析器对Jar包进行分析
        this.handleHotLoader(hotLoader, extraUrl);

        // -----------------预留功能 需要对jar包内的css、js、图片等静态资源进行解压------------
        // 实现代码

        // 加入到已挂载模块中
        jarMounted.put(jarName, extraUrl);
    }

    public void unloadSingleExternalClasses(String jarPath, String jarName) {
        // Jar包分析器对Jar包进行分析
        String extraUrl = jarMounted.get(jarName);
        containerMap.remove(extraUrl);

        // -----------------预留功能 需要对jar包内已经解压的资源进行删除------------
        // 实现代码

        // 加入到已挂载模块中
        jarMounted.remove(jarName);
    }

    /**
     * 加载外部的控制器类
     */
    public void loadExternalClasses() {
        System.out.println("--------------------加载外部控制器------------------------------");
        // 遍历外部Jar包
        for (String jarFileName : FileUtil.getAllJarFiles(jarLibPath))
        {
            // 读取已经重载的配置
            String propertiesFilePath = jarLibPath + jarFileName;
            ModulePropertiesUtil modulePropertiesUtil = new ModulePropertiesUtil(propertiesFilePath);
            if(modulePropertiesUtil.getDefaultLoad()) {
                this.loadSingleExternalClasses(jarLibPath, jarFileName, modulePropertiesUtil.getExtraUrl());
            }
            modulePropertiesUtil.store();
        }
        System.out.println("------------------加载外部控制器加载完毕--------------------------");
    }

    /**
     * 处理控制器类，实例化并且加载进入url列表
     * @param cls           类
     */
    public void handleControllerClass(Map<String, ModelController> controllerMap, String extraUrl, Class<?> cls) {
        RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
        String url;
        if (requestMapping == null) {
            url = "/";
        } else {
            url = requestMapping.value();
        }
        ModelController controller;
        try {
            controller = (ModelController) cls.newInstance();

        } catch (InstantiationException | IllegalAccessException e) {
            return;
        }
        System.out.println("加载控制器:" + cls.getName() + ", 访问路由为:" + extraUrl + url);
        controllerMap.put(url, controller);
    }

    /**
     * 获取最匹配的对象并且根据匹配结果处理Url
     * @param maps 需要匹配的结果
     * @param <T> 匹配结果返回的类型
     * @return 匹配结果
     */
    public <T> String getMatchObject(Map<String, T> maps) {
        int maxMatch = -1;
        String matchUrl = "/";
        for (String targetUrl : maps.keySet()) {
            if ((this.url.indexOf(targetUrl) == 0) &&
                    (targetUrl.length() == this.url.length() || this.url.charAt(targetUrl.length()) == '/')) {
                if(targetUrl.length() > maxMatch){
                    maxMatch = targetUrl.length();
                    matchUrl = targetUrl;
                }
            }
        }
        this.url = this.url.substring(matchUrl.length());
//        return maps.get(matchUrl);
        return matchUrl;
    }
}