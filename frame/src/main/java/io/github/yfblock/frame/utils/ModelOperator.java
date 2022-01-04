package io.github.yfblock.frame.utils;

import io.github.yfblock.frame.Core.Constant.AttributeParams;
import io.github.yfblock.frame.Core.Constant.OperatorMethodParams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModelOperator {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    public void initRequestAndResponse(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    protected void setOperator(OperatorMethodParams operatorMethodParams) {
        request.setAttribute(AttributeParams.OperatorCommand, operatorMethodParams);
    }

    public void mountModule(String moduleName, String secret) {
        this.setOperator(OperatorMethodParams.MOUNT_MODULE);
        request.setAttribute(OperatorMethodParams.MOUNT_MODULE.toString(), moduleName);
    }

    public void umountModule(String moduleName, String secret) {
        this.setOperator(OperatorMethodParams.UNMOUNT_MODULE);
        request.setAttribute(OperatorMethodParams.UNMOUNT_MODULE.toString(), moduleName);
    }

    public String getExtraPath() {
        return (String) this.request.getAttribute(AttributeParams.extraPathInfo);
    }
}
