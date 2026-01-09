package com.caring.sass.user.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.entity.CommonMsgTemplateContent;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * <p>
 * 业务接口
 * 常用语模板分类
 * </p>
 *
 * @author 杨帅
 * @date 2023-05-08
 */
public interface CommonMsgTemplateContentService extends SuperService<CommonMsgTemplateContent> {


    void importMsgTemplate(InputStream inputStream,  String userType);



}
