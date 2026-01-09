package com.caring.sass.ai.controller.knows;


import com.caring.sass.ai.dto.know.KnowledgeUserDomainPageDTO;
import com.caring.sass.ai.dto.know.KnowledgeUserDomainSaveDTO;
import com.caring.sass.ai.dto.know.KnowledgeUserDomainUpdateDTO;
import com.caring.sass.ai.entity.know.KnowledgeUserDomain;
import com.caring.sass.ai.know.service.KnowledgeUserDomainService;
import com.caring.sass.base.controller.SuperController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 知识库博主子平台分类
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-26
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/knowledgeUserDomain")
@Api(value = "KnowledgeUserDomain", tags = "知识库博主子平台分类")
public class KnowledgeUserDomainController extends SuperController<KnowledgeUserDomainService, Long, KnowledgeUserDomain, KnowledgeUserDomainPageDTO, KnowledgeUserDomainSaveDTO, KnowledgeUserDomainUpdateDTO> {


}
