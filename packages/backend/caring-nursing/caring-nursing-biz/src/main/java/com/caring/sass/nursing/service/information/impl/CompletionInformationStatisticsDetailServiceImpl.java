package com.caring.sass.nursing.service.information.impl;


import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.nursing.dao.information.CompletionInformationStatisticsDetailMapper;
import com.caring.sass.nursing.entity.information.CompletionInformationStatisticsDetail;
import com.caring.sass.nursing.service.information.CompletionInformationStatisticsDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 信息完整度区间统计详细表
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Service

public class CompletionInformationStatisticsDetailServiceImpl extends SuperServiceImpl<CompletionInformationStatisticsDetailMapper, CompletionInformationStatisticsDetail> implements CompletionInformationStatisticsDetailService {
}
