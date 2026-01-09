package com.caring.sass.nursing.service.form.impl;


import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.nursing.dao.form.FormScoreRuleMapper;
import com.caring.sass.nursing.entity.form.FormScoreRule;
import com.caring.sass.nursing.service.form.FormScoreRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 表单设置的成绩规则
 * </p>
 *
 * @author 杨帅
 * @date 2023-10-11
 */
@Slf4j
@Service

public class FormScoreRuleServiceImpl extends SuperServiceImpl<FormScoreRuleMapper, FormScoreRule> implements FormScoreRuleService {
}
