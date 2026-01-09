package com.caring.sass.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.result.view.Rendering;


/**
 * 常用Controller
 *
 * @author caring
 * @date 2019-06-21 18:22
 */
@Controller
public class GeneratorController {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * 兼容zuul
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/gate/doc.html")
    public Rendering doc() throws Exception {
        String uri = String.format("%s/doc.html", contextPath);
        return Rendering.redirectTo(uri).build();
    }

    /**
     * 微信txt文件校验
     */
    @ResponseBody
    @GetMapping("/{fileName}.txt")
    public String getWxContent(@PathVariable("fileName") String fileName) {
        if ("4721197183".equals(fileName)) {
            return "facb3686e1e17cb8136312835a92ddfb";
        } else if ("bHnOy0e7gF".equals(fileName)) {
            return "bd09575511b0baf690ec288f1be10524";
        } else if ("74UvLPk54n".equals(fileName)) {
            return "4579fd1c169b4491a3665f7a443b938d";
        }
        return "htxGlsNmVlUf5Wei";
    }


}
