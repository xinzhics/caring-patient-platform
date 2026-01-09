package com.caring.sass.cms.service.impl;


import com.caring.sass.cms.dao.DoctorLastPullMapper;
import com.caring.sass.cms.entity.DoctorLastPull;
import com.caring.sass.cms.service.DoctorLastPullService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 医生订阅最新文章推送表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-14
 */
@Slf4j
@Service

public class DoctorLastPullServiceImpl extends SuperServiceImpl<DoctorLastPullMapper, DoctorLastPull> implements DoctorLastPullService {
}
