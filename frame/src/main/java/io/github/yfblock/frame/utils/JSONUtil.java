package io.github.yfblock.frame.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

public class JSONUtil {
    public static String stringify(Class<?> classType, Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        StringBuilder targetString;
        if (classType.equals(void.class)) {
            targetString = new StringBuilder();
        } else if (classType.equals(String.class)) {
            targetString = new StringBuilder("\"").append((String) obj).append('"');
        } else if (classType.equals(int.class) || classType.equals(double.class) || classType.equals(float.class) ||
                classType.equals(Integer.class) || classType.equals(Double.class) || classType.equals(Float.class) ) {
            targetString = new StringBuilder(String.valueOf(obj));
        } else if (classType.equals(Date.class) || classType.equals(LocalDateTime.class)) {
            targetString = new StringBuilder("\"").append((obj).toString()).append('"');
        } else if (classType.equals(Boolean.class) || classType.equals(boolean.class)){
            targetString = new StringBuilder(obj.toString());
        } else if (classType.equals(HashMap.class) || classType.equals(Map.class)){
            targetString = new StringBuilder("{");
            for(Object item : ((Map<?, ?>)obj).keySet()) {
                Object target = ((Map<?, ?>) obj).get(item);
                String targetValue;
                if(target != null)
                    targetValue = JSONUtil.stringify(target.getClass() , target);
                else
                    targetValue = "";
                targetString.append('"')
                        .append(item.toString())
                        .append('"')
                        .append(':')
                        .append(targetValue)
                        .append(",");
            }
            if(targetString.toString().equals("{"))
                targetString.append('}');
            else
                targetString.setCharAt(targetString.length() - 1, '}');
        } else if (classType.equals(ArrayList.class) || classType.equals(List.class)) {
            targetString = new StringBuilder("[");
            for(Object item : (List<Object>)obj) {
                String targetValue = stringify(item.getClass(), item);
                targetString.append(targetValue).append(',');
            }
            if(targetString.toString().equals("["))
                targetString.append(']');
            else
                targetString.setCharAt(targetString.length() - 1, ']');
        } else {
            targetString = new StringBuilder("{");
            for (Field field : classType.getDeclaredFields()) {
                String first = field.getName().substring(0, 1).toUpperCase();
                String other = field.getName().substring(1);
                Method getter = classType.getDeclaredMethod("get" + first + other);
                Object filedObject = getter.invoke(obj);
                String filedResult;
                if(filedObject == null) filedResult = "null";
                else filedResult = JSONUtil.stringify(filedObject.getClass(), filedObject);
                targetString
                        .append('"')
                        .append(field.getName())
                        .append('"')
                        .append(':')
                        .append(filedResult)
                        .append(',');
            }
            if(targetString.toString().equals("{"))
                targetString.append('}');
            else
                targetString.setCharAt(targetString.length() - 1, '}');
        }
        return targetString.toString();
    }
}
