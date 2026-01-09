package com.caring.sass.nursing.service.tag.impl;


import com.caring.sass.nursing.dao.tag.AttrMapper;
import com.caring.sass.nursing.entity.tag.Attr;
import com.caring.sass.nursing.service.tag.AttrService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 标签属性
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Service

public class AttrServiceImpl extends SuperServiceImpl<AttrMapper, Attr> implements AttrService {
}
