package io.github.yfblock.frame.Exceptions;

public class ParamNotFoundException extends RuntimeException{
    public ParamNotFoundException() {
        super("未找到指定类");
    }

    public ParamNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
