package com.caring.sass.ai.card.service.impl;


import com.caring.sass.ai.card.dao.BusinessCardOrganMapper;
import com.caring.sass.ai.card.service.BusinessCardOrganService;
import com.caring.sass.ai.entity.card.BusinessCardOrgan;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 科普名片组织
 * </p>
 *
 * @author 杨帅
 * @date 2024-11-26
 */
@Slf4j
@Service

public class BusinessCardOrganServiceImpl extends SuperServiceImpl<BusinessCardOrganMapper, BusinessCardOrgan> implements BusinessCardOrganService {
}
