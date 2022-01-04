package io.github.yfblock.frame.Core;

import io.github.yfblock.frame.Controller.SuperAdminController;
import io.github.yfblock.frame.Core.Analyzer.UrlTreeNode;
import io.github.yfblock.frame.Core.Constant.AttributeParams;
import io.github.yfblock.frame.Core.Constant.OperatorMethodParams;
import io.github.yfblock.frame.Core.Servlet.MultiModuleServlet;
import io.github.yfblock.frame.utils.ModelOperator;
import io.github.yfblock.frame.utils.ModulePropertiesUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet(value = "/", loadOnStartup = 1)
public class DispatcherServlet extends MultiModuleServlet {
    @Override
    public void init() throws ServletException {
        super.init();
        this.initBeans();
    }

    /**
     * 初始化Ioc容器和Beans
     */
    public void initBeans() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ModelOperator.class);
        this.context.registerBeanDefinition(ModelOperator.class.getName(), builder.getBeanDefinition());
    }

    @SneakyThrows
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        super.service(req, res);

        UrlTreeNode methodNode = this.getMatchMethod();
        // 如果找到了方法节点 则执行方法并且输出结果
        if(methodNode != null) this.handleMethod(this.context.getBean((String) methodNode.getExtra()), (Method) methodNode.getData());
        else res.sendError(404, "Not found the match method");

        this.handleOperation();
    }

    /**
     * 在执行完dispatcherServlet后,判断是否存在其他操作
     */
    public void handleOperation() {
        // 如果有特殊节点
        OperatorMethodParams operatorMethod = (OperatorMethodParams) request.getAttribute(AttributeParams.OperatorCommand);

        // 判断是否有特殊操作
        if(operatorMethod != null) {
            switch (operatorMethod) {
                // 模块挂载
                case MOUNT_MODULE:
                    String moduleName = (String) request.getAttribute(OperatorMethodParams.MOUNT_MODULE.toString());
                    if(moduleName != null) {
                        String propertiesFilePath = jarLibPath + moduleName;
                        ModulePropertiesUtil modulePropertiesUtil = new ModulePropertiesUtil(propertiesFilePath);
                        this.loadSingleExternalClasses(jarLibPath, moduleName, modulePropertiesUtil.getExtraUrl());
                        modulePropertiesUtil.setDefaultLoad(true);
                        modulePropertiesUtil.store();
                    }
                    break;
                // 模块卸载
                case UNMOUNT_MODULE:
                    moduleName = (String) request.getAttribute(OperatorMethodParams.UNMOUNT_MODULE.toString());
                    if(moduleName != null) {
                        String propertiesFilePath = jarLibPath + moduleName;
                        ModulePropertiesUtil modulePropertiesUtil = new ModulePropertiesUtil(propertiesFilePath);
//                    this.loadSingleExternalClasses(jarLibPath, moduleName, modulePropertiesUtil.getExtraUrl());
                        this.unloadSingleExternClasses(moduleName, modulePropertiesUtil.getExtraUrl());
                        modulePropertiesUtil.setDefaultLoad(false);
                        modulePropertiesUtil.store();
                    }
                    break;
            }
        }
    }
}
