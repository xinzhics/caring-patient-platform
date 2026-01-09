package com.caring.sass.cms.controller;

import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.cms.service.ChannelContentService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName CmsShowController
 * @Description
 * @Author yangShuai
 * @Date 2021/9/1 9:50
 * @Version 1.0
 */
@Api(value = "CmsShowController", tags = "文章内容预览接口")
@RestController
@RequestMapping("content/anno")
public class CmsShowController {

    @Autowired
    ChannelContentService channelContentService;

    @GetMapping("/show")
    public ChannelContent getCmsDetailHtml(@RequestParam("id") Long id, HttpServletRequest request) {

        ChannelContent channelContent = channelContentService.getByIdWithoutTenant(id);
        return channelContent;
    }



}
