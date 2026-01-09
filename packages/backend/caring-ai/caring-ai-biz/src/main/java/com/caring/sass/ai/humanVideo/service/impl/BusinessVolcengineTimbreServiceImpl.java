package com.caring.sass.ai.humanVideo.service.impl;

import com.caring.sass.ai.entity.humanVideo.BusinessVolcengineTimbre;
import com.caring.sass.ai.entity.humanVideo.VolcengineTimbreStatus;
import com.caring.sass.ai.humanVideo.dao.BusinessVolcengineTimbreMapper;
import com.caring.sass.ai.humanVideo.service.BusinessVolcengineTimbreService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 火山音色管理
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-14
 */
@Slf4j
@Service

public class BusinessVolcengineTimbreServiceImpl extends SuperServiceImpl<BusinessVolcengineTimbreMapper, BusinessVolcengineTimbre> implements BusinessVolcengineTimbreService {


    @Override
    public boolean save(BusinessVolcengineTimbre model) {


        model.setTimbreStatus(VolcengineTimbreStatus.FREE);

        return super.save(model);
    }
}
