package com.caring.sass.cms.service.impl;


import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.cms.dao.ContentCollectMapper;
import com.caring.sass.cms.entity.ContentCollect;
import com.caring.sass.cms.service.ContentCollectService;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 业务实现类
 * 点赞记录
 * </p>
 *
 */
@Slf4j
@Service
public class ContentCollectServiceImpl extends SuperServiceImpl<ContentCollectMapper, ContentCollect> implements ContentCollectService {

    @Override
    public boolean save(ContentCollect model) {

        LbqWrapper<ContentCollect> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ContentCollect::getUserId, model.getUserId());
        lbqWrapper.eq(ContentCollect::getContentId, model.getContentId());
        List<ContentCollect> contentCollects = baseMapper.selectList(lbqWrapper);
        if (CollectionUtils.isEmpty(contentCollects)) {
            model.setCollectStatus(1);
            return super.save(model);
        } else {

            ContentCollect collect = contentCollects.get(0);
            if (collect.getCollectStatus() == 0) {
                collect.setCollectStatus(1);
            } else {
                collect.setCollectStatus(0);
            }
            baseMapper.updateById(collect);
        }
        return true;
    }

    @Override
    public void cancelContent(Long contentId, Long userId) {

        LbqWrapper<ContentCollect> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ContentCollect::getUserId, userId);
        lbqWrapper.eq(ContentCollect::getContentId, contentId);

        baseMapper.delete(lbqWrapper);

    }


    @Override
    public boolean hasCollect(Long id, Long userId, String roleType) {

        if (userId == null) {
            return false;
        }
        LbqWrapper<ContentCollect> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(ContentCollect::getUserId, userId);
        lbqWrapper.eq(ContentCollect::getContentId, id);
        lbqWrapper.eq(ContentCollect::getRoleType, roleType);
        lbqWrapper.eq(ContentCollect::getCollectStatus, 1);
        Integer integer = baseMapper.selectCount(lbqWrapper);
        return integer > 0;
    }
}
