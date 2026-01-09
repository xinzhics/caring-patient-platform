package com.caring.sass.cms.service.impl;


import com.caring.sass.cms.dao.BannerMapper;
import com.caring.sass.cms.entity.Banner;
import com.caring.sass.cms.service.BannerService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * banner
 * </p>
 *
 * @author 杨帅
 * @date 2023-12-08
 */
@Slf4j
@Service

public class BannerServiceImpl extends SuperServiceImpl<BannerMapper, Banner> implements BannerService {
}
