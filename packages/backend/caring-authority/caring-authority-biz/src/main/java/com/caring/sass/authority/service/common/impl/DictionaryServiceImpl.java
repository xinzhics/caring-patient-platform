package com.caring.sass.authority.service.common.impl;


import com.caring.sass.authority.dao.common.DictionaryMapper;
import com.caring.sass.authority.entity.common.Dictionary;
import com.caring.sass.authority.service.common.DictionaryService;
import com.caring.sass.base.service.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 字典类型
 * </p>
 *
 * @author caring
 * @date 2019-07-02
 */
@Slf4j
@Service

public class DictionaryServiceImpl extends SuperServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

}
