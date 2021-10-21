package io.github.yfblock.frame.Core.Servlet;

import freemarker.template.TemplateException;
import io.github.yfblock.frame.Annotations.RequestParam;
import io.github.yfblock.frame.Core.Template.Template;
import io.github.yfblock.frame.Core.Template.TemplateHandler;
import io.github.yfblock.frame.Exceptions.ParamNotFoundException;
import io.github.yfblock.frame.utils.JSONUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Locale;

public class AbstractServlet extends HttpServlet {
    protected String requestUrl;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    public TemplateHandler templateHandler;

    @Override
    public void init() throws ServletException {
        templateHandler = new TemplateHandler(this.getClass());
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        this.requestUrl = req.getServletPath().toLowerCase(Locale.ROOT);
        this.request = req;
        this.response = res;
    }

    /**
     * 构建参数列表
     *
     * @param method 需要构建参数列表的方法
     * @return 参数列表
     */
    protected Object[] buildParameters(Method method) {
        ArrayList<Object> arrayList = new ArrayList<>();
        for (Parameter parameter : method.getParameters())
            arrayList.add(this.buildParameter(parameter));
        return arrayList.toArray();
    }

    /**
     * 构建单个参数
     *
     * @param parameter 参数名称
     * @return 构建对象
     */
    protected Object buildParameter(Parameter parameter) {
        // 判断请求类型
        if(parameter.getType().equals(HttpServletRequest.class) || parameter.getType().equals(ServletRequest.class))
            return this.request;
        // 获取请求参数
        String resultString;
        RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        if (requestParam == null) {
            String parameterName = parameter.getName();
            resultString = this.getParameter(parameterName);
            if (resultString == null) return null;
        } else {
            String parameterName = requestParam.value();
            resultString = this.getParameter(parameterName);
            if (resultString == null) {
                if (requestParam.required()) throw new ParamNotFoundException("未找到指定参数");
                resultString = requestParam.defaultValue();
            }
        }
        // 对结果进行转换
        Class<?> parameterType = parameter.getType();
        try {
            if (parameterType.equals(int.class) || parameterType.equals(Integer.class)) {
                return Integer.parseInt(resultString);
            }
        } catch (Exception e) {
            // 如果匹配异常则返回Null
            e.printStackTrace();
            return null;
        }
        return resultString;
    }

    /**
     * 获取请求参数
     *
     * @param key 请求参数键值
     * @return 请求参数结果
     */
    protected String getParameter(String key) {
        return request.getParameter(key);
    }

    /**
     * 判断是否为基本类型
     *
     * @param targetClass 将要被判断的类型
     * @return 是/否
     */
    protected boolean isBasicType(Class<?> targetClass) {
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
     * 执行方法
     *
     * @param object 方法所在的对象
     * @param method 方法
     */
    protected void handleMethod(Object object, Method method) throws IOException, InvocationTargetException,
            NoSuchMethodException, IllegalAccessException, TemplateException {
        Object obj = method.invoke(object, this.buildParameters(method));

        Class<?> resultType = method.getReturnType();
        if (this.isBasicType(resultType)) {
            // 普通类型 直接输出
            this.response.setContentType("text/html;charset=utf-8");
            // 如果结果是可输出对象则输出结果
            if (resultType != void.class && resultType != Void.class)
                response.getWriter().write(obj.toString());
        } else if (Template.class.equals(resultType)) {
            // 返回模板对象
            this.response.setContentType("text/html;charset=utf-8");
            // 输出模板对象
            Template template = (Template) obj;
            templateHandler.process(template.getPath(), template.getData(), response.getWriter());
        } else {
            // 复杂类型和自定义类型json格式输出
            this.response.setContentType("application/json;charset=utf-8");
            // 输出JSON数据
            response.getWriter().write(JSONUtil.stringify(resultType, obj));
        }
        response.getWriter().flush();
    }
}
