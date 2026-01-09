package com.caring.sass.cms.config;

import com.caring.sass.boot.handler.DefaultGlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内容管理-全局异常处理
 *
 * @author leizhi
 * @date 2020-09-09
 */
@Configuration
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class CmsExceptionConfiguration extends DefaultGlobalExceptionHandler {
}
