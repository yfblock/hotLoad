package io.github.yfblock.frameMvc.Core;

import freemarker.template.TemplateException;
import io.github.yfblock.frameMvc.Annotations.RequestMapping;
import io.github.yfblock.frameMvc.Annotations.RequestParam;
import io.github.yfblock.frameMvc.Core.Template.Template;
import io.github.yfblock.frameMvc.Core.Template.TemplateHandler;
import io.github.yfblock.frameMvc.Exceptions.ParamNotFoundException;
import io.github.yfblock.frameMvc.utils.JSONUtil;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ModelController {
    public HttpServletRequest request;
    public HttpServletResponse response;
    public Map<String, Method> methodMap = new HashMap<>();
    public String url = "";
    public TemplateHandler templateHandler;

    /**
     * 初始化项目并且检查控制器
     */
    @SneakyThrows
    public ModelController() {
        templateHandler = new TemplateHandler(this.getClass());
        for(Method method : this.getClass().getDeclaredMethods()) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            String url;
            if(requestMapping == null) {
                url = "/" + method.getName().toLowerCase(Locale.ROOT);
            } else {
                url = requestMapping.value();
            }
            this.methodMap.put(url, method);
        }
    }

    /**
     * 重定向到一个新的路径
     * @param newPath the path to redirect
     */
    public void redirect(String newPath) throws IOException {
            this.response.sendRedirect(newPath);
    }

    /**
     * 处理Url获得method
     */
    public void handleUrlMethod()
            throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, TemplateException {
        if(this.url == null || this.url.length() == 0 || this.url.equals("/")) this.url = "/index";
        Method method = this.getMethod();
        if(method == null) {
            this.response.sendError(404, "Not found the method");
            return;
        }
        Object obj = method.invoke(this, this.buildParameters(method));

        Class<?> resultType = method.getReturnType();
        if(this.isBasicType(resultType)) {
            // 普通类型 直接输出
            this.response.setContentType("text/html;charset=utf-8");
            if(resultType!=void.class && resultType!=Void.class) {
                response.getWriter().write(obj.toString());
            }
        }else if(Template.class.equals(resultType)) {
            // 返回模板对象
            this.response.setContentType("text/html;charset=utf-8");
            Template template = (Template) obj;
            templateHandler.process(template.getPath(), template.getData(), response.getWriter());
        }else{
            // 复杂类型和自定义类型json格式输出
            this.response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSONUtil.stringify(resultType, obj));
        }
        response.getWriter().flush();
    }

    /**
     * 设置请求对象和相应对象
     * @param request 请求对象
     * @param response 相应对象
     */
    public void setRequestAndResponse(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * 获取匹配的所有方法
     * @return 匹配到的方法
     */
    public Map<Integer, Method> getMethods() {
        Map<Integer, Method> methods = new HashMap<>();
        for(String controllerUrl : methodMap.keySet()) {
            if(this.url.indexOf(controllerUrl) == 0 &&
                    (controllerUrl.length() == this.url.length() || this.url.charAt(controllerUrl.length()) =='/'))
                methods.put(controllerUrl.length(), methodMap.get(controllerUrl));
        }
        return methods;
    }

    /**
     * 选择匹配程度最高的方法
     * @return 匹配到的方法
     */
    public Method getMethod() {
        Map<Integer, Method> methods = this.getMethods();
        if(methods.size() == 0) return null;
        int maxMatch = -1;
        for(int matchNumber : methods.keySet()) {
            if(matchNumber > maxMatch) maxMatch = matchNumber;
        }
        Method method = methods.getOrDefault(maxMatch, null);
        if(method == null) return null;
        this.url = url.substring(maxMatch).toLowerCase(Locale.ROOT);
        if(this.url.length() > 0 && this.url.charAt(0)=='/') this.url = this.url.substring(1);
        return method;
    }

    /**
     * 判断是否为基本类型
     * @param targetClass 将要被判断的类型
     * @return 是/否
     */
    public boolean isBasicType(Class<?> targetClass) {
        return targetClass.isPrimitive() ||
                targetClass.equals(Integer.class) ||
                targetClass.equals(Byte.class) ||
                targetClass.equals(Long.class) ||
                targetClass.equals(Double.class) ||
                targetClass.equals(Float.class) ||
                targetClass.equals(Character.class) ||
                targetClass.equals(Short.class) ||
                targetClass.equals(Boolean.class) ||
                targetClass.equals(String.class);
    }

    /**
     * 构建参数列表
     * @param method 需要构建参数列表的方法
     * @return 参数列表
     */
    public Object[] buildParameters(Method method) {
        ArrayList<Object> arrayList = new ArrayList<>();
        for(Parameter parameter : method.getParameters())
            arrayList.add(this.buildParameter(parameter));
        return arrayList.toArray();
    }

    public Object buildParameter(Parameter parameter) {
        // 获取请求参数
        String resultString;
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        if(requestParam == null) {
            String parameterName = parameter.getName();
            resultString = this.getParameter(parameterName);
            if(resultString == null) resultString = "";
        } else {
            String parameterName = requestParam.value();
            resultString = this.getParameter(parameterName);
            if(resultString == null) {
                if(requestParam.required()) throw new ParamNotFoundException("未找到指定参数");
                resultString = requestParam.defaultValue();
            }
        }
        // 对结果进行转换
        Class<?> parameterType = parameter.getType();
        try {
            if(parameterType.equals(int.class) || parameterType.equals(Integer.class)) {
                return Integer.parseInt(resultString);
            }
        }catch (Exception e) {
            // 如果匹配异常则返回Null
            e.printStackTrace();
            return null;
        }
        return resultString;
    }

    /**
     * 获取请求参数
     * @param key 请求参数键值
     * @return 请求参数结果
     */
    public String getParameter(String key){
        return request.getParameter(key);
    }
}
