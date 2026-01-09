package com.caring.sass.cms.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.cms.dao.SiteColorHistoryMapper;
import com.caring.sass.cms.entity.SiteColorHistory;
import com.caring.sass.cms.service.SiteColorHistoryService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.utils.SaasGlobalThreadPool;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 建站最近使用颜色
 * </p>
 *
 * @author 杨帅
 * @date 2023-11-27
 */
@Slf4j
@Service

public class SiteColorHistoryServiceImpl extends SuperServiceImpl<SiteColorHistoryMapper, SiteColorHistory> implements SiteColorHistoryService {


    /**
     * 查询是否已经有这个颜色
     * 如果有这个颜色，则修改颜色的创建时间为此时。
     * 如果没有这个颜色，则新增一条最近使用记录
     * 检查是否最近使用超过20个。如果超过则进行清除。
     * @param model
     * @return
     */
    @Override
    public boolean save(SiteColorHistory model) {
        Long userId = BaseContextHandler.getUserId();
        SiteColorHistory selectOne = baseMapper.selectOne(Wraps.<SiteColorHistory>lbQ()
                .eq(SuperEntity::getCreateUser, userId)
                .eq(SiteColorHistory::getColor, model.getColor())
                .last(" limit 0,1 "));
        if (Objects.isNull(selectOne)) {
            String tenant = BaseContextHandler.getTenant();
            super.save(model);
            SaasGlobalThreadPool.execute(() -> syncDelete(userId, tenant));
            return true;
        } else {
            selectOne.setCreateTime(LocalDateTime.now());
            super.updateById(selectOne);
        }
        return true;
    }

    /**
     * 删除多余的过去的记录
     * @param userId
     * @param tenantCode
     */
    public void syncDelete(Long userId, String tenantCode) {
        BaseContextHandler.setTenant(tenantCode);
        Integer count = baseMapper.selectCount(Wraps.<SiteColorHistory>lbQ().eq(SuperEntity::getCreateUser, userId));
        if (count != null && count > 20) {
            int i = count - 20;
            List<SiteColorHistory> selectList = baseMapper.selectList(Wraps.<SiteColorHistory>lbQ()
                    .select(SuperEntity::getId, SiteColorHistory::getColor)
                    .eq(SuperEntity::getCreateUser, userId)
                    .orderByAsc(SuperEntity::getCreateTime)
                    .last(" limit 0, " + i));
            if (CollUtil.isNotEmpty(selectList)) {
                List<Long> ids = selectList.stream().map(SuperEntity::getId).collect(Collectors.toList());
                baseMapper.deleteBatchIds(ids);
            }

        }


    }
}
