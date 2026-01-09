package com.caring.sass.ai.docuSearch.service.impl;



import com.caring.sass.ai.docuSearch.dao.KnowledgeJcrCasMapper;
import com.caring.sass.ai.docuSearch.service.KnowledgeJcrCasService;
import com.caring.sass.ai.entity.docuSearch.KnowledgeJcrCas;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * jcr和cas分区信息表
 * </p>
 *
 * @author 杨帅
 * @date 2024-10-18
 */
@Slf4j
@Service

public class KnowledgeJcrCasServiceImpl extends SuperServiceImpl<KnowledgeJcrCasMapper, KnowledgeJcrCas> implements KnowledgeJcrCasService {
}
