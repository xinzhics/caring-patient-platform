package com.caring.sass.user.service.impl;


import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.user.dao.CommonMsgTemplateContentMapper;
import com.caring.sass.user.dao.CommonMsgTemplateTypeMapper;
import com.caring.sass.user.entity.CommonMsgTemplateContent;
import com.caring.sass.user.entity.CommonMsgTemplateType;
import com.caring.sass.user.service.CommonMsgTemplateTypeService;
import com.caring.sass.base.service.SuperServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * 业务实现类
 * 常用语模板分类
 * </p>
 *
 * @author 杨帅
 * @date 2023-05-08
 */
@Slf4j
@Service

public class CommonMsgTemplateTypeServiceImpl extends SuperServiceImpl<CommonMsgTemplateTypeMapper, CommonMsgTemplateType> implements CommonMsgTemplateTypeService {

    @Autowired
    CommonMsgTemplateContentMapper commonMsgTemplateContentMapper;

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            removeById(id);
        }
        return true;
    }


    /**
     * 删除常用模板的分类。 删除分类下的常用语模板
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {

        commonMsgTemplateContentMapper.delete(Wraps.<CommonMsgTemplateContent>lbQ()
                .eq(CommonMsgTemplateContent::getTemplateTypeId, id));
        baseMapper.deleteById(id);
        return true;
    }
}
