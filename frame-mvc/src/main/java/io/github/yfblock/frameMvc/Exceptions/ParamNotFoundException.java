package io.github.yfblock.frameMvc.Exceptions;

public class ParamNotFoundException extends RuntimeException{
    public ParamNotFoundException() {
        super("未找到指定类");
    }

    public ParamNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
