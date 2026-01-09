package com.caring.sass.tenant.service.sys.impl;


import com.caring.sass.tenant.dao.sys.DictItemMapper;
import com.caring.sass.tenant.entity.sys.DictItem;
import com.caring.sass.tenant.service.sys.DictItemService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 字典项
 * </p>
 *
 * @author leizhi
 * @date 2021-03-25
 */
@Slf4j
@Service

public class DictItemServiceImpl extends SuperServiceImpl<DictItemMapper, DictItem> implements DictItemService {
}
