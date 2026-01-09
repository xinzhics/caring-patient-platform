package com.caring.sass.cms.controller;

import com.caring.sass.base.controller.SuperController;
import com.caring.sass.cms.dto.ArticleOtherPageDto;
import com.caring.sass.cms.dto.ArticleOtherSaveDto;
import com.caring.sass.cms.dto.ArticleOtherUpdateDto;
import com.caring.sass.cms.entity.ArticleOther;
import com.caring.sass.cms.service.ArticleOtherService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@Validated
@RestController
@RequestMapping("/articleOther")
@Api(value = "ArticleOtherController", tags = "其他素材上传")
public class ArticleOtherController extends SuperController<ArticleOtherService, Long, ArticleOther, ArticleOtherPageDto, ArticleOtherSaveDto, ArticleOtherUpdateDto> {

}
