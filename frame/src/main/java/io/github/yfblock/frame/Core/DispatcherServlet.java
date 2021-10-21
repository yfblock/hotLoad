package io.github.yfblock.frame.Core;

import io.github.yfblock.frame.Controller.SuperAdminController;
import io.github.yfblock.frame.Core.Analyzer.UrlTreeNode;
import io.github.yfblock.frame.Core.Servlet.MultiModuleServlet;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet(value = "/", loadOnStartup = 1)
public class DispatcherServlet extends MultiModuleServlet {
    protected SuperAdminController superAdminController;
    @Override
    public void init() throws ServletException {
        super.init();
        superAdminController = new SuperAdminController();
    }

    @SneakyThrows
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        super.service(req, res);
//        this.handleMethod(superAdminController, SuperAdminController.class.getDeclaredMethod("init"));
//        for(UrlTreeNode urlTreeNode : rootUrlTreeNode.getChildren()) {
//            for (UrlTreeNode conTreeNode : urlTreeNode.getChildren()) {
//                for(UrlTreeNode methodTreeNode : conTreeNode.getChildren()) {
//                    System.out.println("Method" + methodTreeNode.getData() + " Store");
//                }
//            }
//        }
        UrlTreeNode methodNode = this.getMatchMethod();
        // 如果找到了方法节点 则执行方法并且输出结果
        if(methodNode != null) this.handleMethod(this.context.getBean((String) methodNode.getExtra()), (Method) methodNode.getData());
        else res.sendError(404, "Not found the match method");
    }
}
