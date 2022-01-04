package io.github.yfblock.frameMvc.Core.Template;

import lombok.Data;

import java.util.Map;

@Data
public class Template {
    private String path;
    private Map<String, Object> data;
}
