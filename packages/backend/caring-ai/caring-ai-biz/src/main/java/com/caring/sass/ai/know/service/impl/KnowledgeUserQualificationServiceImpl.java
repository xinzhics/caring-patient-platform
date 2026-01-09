package com.caring.sass.ai.know.service.impl;


import com.caring.sass.ai.entity.know.KnowledgeUserQualification;
import com.caring.sass.ai.know.dao.KnowledgeUserQualificationMapper;
import com.caring.sass.ai.know.service.KnowledgeUserQualificationService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 知识库博主资质
 * </p>
 *
 * @author 杨帅
 * @date 2025-12-29
 */
@Slf4j
@Service

public class KnowledgeUserQualificationServiceImpl extends SuperServiceImpl<KnowledgeUserQualificationMapper, KnowledgeUserQualification>
        implements KnowledgeUserQualificationService {
}
