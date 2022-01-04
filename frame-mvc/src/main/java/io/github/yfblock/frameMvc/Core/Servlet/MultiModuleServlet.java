package io.github.yfblock.frameMvc.Core.Servlet;

import io.github.yfblock.frameMvc.Annotations.Controller;
import io.github.yfblock.frameMvc.Annotations.RequestMapping;
import io.github.yfblock.frameMvc.Core.Analyzer.UrlTreeNode;
import io.github.yfblock.frameMvc.Core.Analyzer.UrlTreeNodeType;
import io.github.yfblock.frameMvc.Core.Constant.AttributeParams;
import io.github.yfblock.frameMvc.Core.ModelController;
import io.github.yfblock.frameMvc.utils.ModelOperator;
import io.github.yfblock.frameMvc.utils.ModulePropertiesUtil;
import io.github.yfblock.yfHotLoad.ClassSource.DirClassSource;
import io.github.yfblock.yfHotLoad.ClassSource.JarClassSource;
import io.github.yfblock.yfHotLoad.HotLoader;
import io.github.yfblock.yfHotLoad.Utils.FileUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class MultiModuleServlet extends AbstractServlet {
    protected UrlTreeNode rootUrlTreeNode;                           // 请求路由根节点
    protected String jarLibPath;                                     // 模块Jar包路径
    protected Map<String, String> jarMounted;                        // 已经挂载的Jar包
    protected HotLoader nativeHotLoader;                             // 本包加载工具
    protected AnnotationConfigApplicationContext context;

    @Override
    public void init() throws ServletException {
        super.init();
        rootUrlTreeNode = new UrlTreeNode("/", UrlTreeNodeType.ROOT, null, new ArrayList<>());
        nativeHotLoader = new HotLoader(new DirClassSource(ModelController.class.getClassLoader(),
                "io.github.yfblock.frame"));
        context = new AnnotationConfigApplicationContext();

        // 已经挂载的模块
        jarMounted = new HashMap<>();
        // 获取路径
        String rootPath =
                Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        if (rootPath != null)
            jarLibPath = rootPath + "../extra/";           // 额外的jar包的路径
        this.loadNativeClasses();
        this.loadExternalModules();
        this.context.refresh();
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        super.service(req, res);
        req.setAttribute(AttributeParams.JAR_LIB_PATH, jarLibPath);
        req.setAttribute(AttributeParams.MODULE_MOUNTED_MAP, jarMounted);
    }

    /**
     * 加载本地的控制器类
     */
    public void loadNativeClasses() {
        System.out.println("--------------------加载本地控制器------------------------------");
        this.rootUrlTreeNode.getChildren().add(this.handleHotLoader(this.nativeHotLoader, "/"));
        System.out.println("------------------加载本地控制器加载完毕--------------------------");
    }

    /**
     * 加载单个Jar包
     *
     * @param jarPath Jar包路劲，目前仅仅可以在特定的文件夹下 extra 文件夹下
     */
    public void loadSingleExternalClasses(String jarPath, String jarName, String extraUrl) {
        // 初始化extraUrl
        extraUrl = extraUrl.toLowerCase(Locale.ROOT);
        // 创建包读取节点
        HotLoader hotLoader = new HotLoader(new JarClassSource(jarPath + jarName));
        // Jar包分析器对Jar包进行分析
        this.rootUrlTreeNode.getChildren().add(this.handleHotLoader(hotLoader, extraUrl));

        // -----------预留功能 需要对jar包内的css、js、图片等静态资源进行解压或者代理----------
        // 实现代码


        // 加入到已挂载模块中
        jarMounted.put(jarName, extraUrl);
    }

    /**
     * 加载单个文件夹
     */
    public void loadSingleDirectoryClasses(String path, String name, String extraUrl) {
        // 初始化extraUrl
        extraUrl = extraUrl.toLowerCase(Locale.ROOT);
        // 创建包读取节点
        HotLoader hotLoader = new HotLoader(new DirClassSource(path));
        // Jar包分析器对Jar包进行分析
        this.rootUrlTreeNode.getChildren().add(this.handleHotLoader(hotLoader, extraUrl));

        // -----------预留功能 需要对jar包内的css、js、图片等静态资源进行解压或者代理----------
        // 实现代码


        // 加入到已挂载模块中
        jarMounted.put(name, extraUrl);
    }

    /**
     * 单个模块卸载
     * @param jarName 模块名称
     * @param extraUrl 模块url
     */
    public void unloadSingleExternClasses(String jarName, String extraUrl) {
        // 初始化extraUrl
        extraUrl = extraUrl.toLowerCase(Locale.ROOT);
        // 包卸载 遍历方式找到要卸载的模块 然后进行卸载
        for(UrlTreeNode moduleNode : this.rootUrlTreeNode.getChildren()) {
            if(extraUrl.equals(moduleNode.getUrl())) {
                this.rootUrlTreeNode.getChildren().remove(moduleNode);
                break;
            }
        }

        // -----------------预留功能 需要对jar包内的css、js、图片等静态资源进行删除------------
        // 实现代码

        // 加入到已挂载模块中
        jarMounted.remove(jarName);
    }

    /**
     * 加载外部的控制器类
     */
    public void loadExternalModules() {
        System.out.println("--------------------加载外部控制器------------------------------");
        // 遍历外部Jar包
        for (String jarFileName : FileUtil.getAllJarFiles(jarLibPath)) {
            // 读取已经重载的配置
            String propertiesFilePath = jarLibPath + jarFileName;
            ModulePropertiesUtil modulePropertiesUtil = new ModulePropertiesUtil(propertiesFilePath);
            if (modulePropertiesUtil.getDefaultLoad()) {
                this.loadSingleExternalClasses(jarLibPath, jarFileName, modulePropertiesUtil.getExtraUrl());
            }
            modulePropertiesUtil.store();
        }
        System.out.println("------------------加载外部控制器加载完毕--------------------------");
    }


    @Override
    protected Object buildParameter(Parameter parameter) {
        try{
            Object obj =  this.context.getBean(parameter.getType());
            if(obj.getClass().equals(ModelOperator.class))
                ((ModelOperator)obj).initRequestAndResponse(request, response);
            return obj;
        } catch (BeansException ignored) {

        }

        return super.buildParameter(parameter);
    }

    protected UrlTreeNode handleHotLoader(HotLoader hotLoader, String extraUrl) {
        // 加载Beans
        this.loadBeans(hotLoader);

        // 加载Controller
        UrlTreeNode urlTreeNode = new UrlTreeNode(extraUrl, UrlTreeNodeType.MODULE, null, new ArrayList<>());
        for (Class<?> cls : hotLoader.getClassesByAnnotation(Controller.class))
        {
            this.handleControllerClass(urlTreeNode, cls);
        }
        return urlTreeNode;
    }

    public void loadBeans(HotLoader hotLoader) {
        for(Class<?> cls : hotLoader.getClassesByAnnotation(Configuration.class)) {
            System.out.println("加载Bean:" + cls);
            this.context.register(cls);
        }
    }

    /**
     * 加载类中的方法列表
     *
     * @param controllerUrlTreeNode 控制器节点
     * @param cls                   控制器类
     */
    public void handleMethods(UrlTreeNode controllerUrlTreeNode, Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (requestMapping == null) continue;
            // 如果RequestMapping不为空 则加载方法入树
//            UrlTreeNode urlTreeNode = new UrlTreeNode(requestMapping.value(), UrlTreeNodeType.METHOD, method, obj, null);
            UrlTreeNode urlTreeNode = new UrlTreeNode(requestMapping.value(), UrlTreeNodeType.METHOD, method, cls.getName(), null);

            controllerUrlTreeNode.getChildren().add(urlTreeNode);
        }
    }

    /**
     * 处理控制器类，实例化并且加载进入url列表
     *
     * @param cls 类
     */
    public void handleControllerClass(UrlTreeNode moduleUrlTreeNode, Class<?> cls) {
        RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
        String url = requestMapping == null ? "/" : requestMapping.value();
        // 实例化 并且加入Bean
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
        this.context.registerBeanDefinition(cls.getName(), builder.getBeanDefinition());
//            this.context.getBeanFactory().registerSingleton(cls.getName(), controller);
//            UrlTreeNode treeNode = new UrlTreeNode(url, UrlTreeNodeType.CONTROLLER, controller, new ArrayList<>());
        UrlTreeNode treeNode = new UrlTreeNode(url, UrlTreeNodeType.CONTROLLER, cls.getName(), new ArrayList<>());
        this.handleMethods(treeNode, cls);
        moduleUrlTreeNode.getChildren().add(treeNode);
        System.out.println("加载控制器:" + cls.getName());
    }

    public UrlTreeNode getMatchMethod() {
        String url = this.requestUrl;
        if (url.equals("/")) url = "";
        // 遍历模块url
        for (UrlTreeNode urlTreeNode : this.rootUrlTreeNode.getChildren()) {
            String currUrl = urlTreeNode.getUrl().equals("/") ? "" : urlTreeNode.getUrl();
            if (url.indexOf(currUrl) != 0) continue;
            // 如果模块url匹配， 遍历控制器url
            for (UrlTreeNode controllerTreeNode : urlTreeNode.getChildren()) {
                String connUrl = controllerTreeNode.getUrl().equals("/") ? currUrl : currUrl + controllerTreeNode.getUrl();
                if (url.indexOf(connUrl) != 0) continue;
                // 如果控制器url匹配，遍历方法url
                for (UrlTreeNode methodNode : controllerTreeNode.getChildren()) {
                    String methodUrl = methodNode.getUrl().equals("/") ? connUrl : connUrl + methodNode.getUrl();
                    if (methodUrl.equals(url))
                        return methodNode;
                    if (methodUrl.endsWith("*")) {
                        methodUrl = methodUrl.substring(0, methodUrl.lastIndexOf("*"));
                        if(url.indexOf(methodUrl) == 0){
                            this.request.setAttribute(AttributeParams.extraPathInfo,
                                    request.getServletPath().substring(methodUrl.length()));
                            return methodNode;
                        }
                    }
                }
            }
        }
        return null;
    }
}
