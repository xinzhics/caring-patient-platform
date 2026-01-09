package com.caring.sass.nursing.service.tag.impl;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.constant.AttrBindEvent;
import com.caring.sass.nursing.constant.TagBindRedisKey;
import com.caring.sass.nursing.dao.plan.PlanTagMapper;
import com.caring.sass.nursing.dao.tag.AssociationMapper;
import com.caring.sass.nursing.dao.tag.TagMapper;
import com.caring.sass.nursing.dto.tag.AttrBindChangeDto;
import com.caring.sass.nursing.dto.tag.TagPatientCountResult;
import com.caring.sass.nursing.entity.plan.PlanTag;
import com.caring.sass.nursing.entity.tag.Association;
import com.caring.sass.nursing.entity.tag.Attr;
import com.caring.sass.nursing.entity.tag.Tag;
import com.caring.sass.nursing.service.tag.AttrService;
import com.caring.sass.nursing.service.tag.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 标签管理
 * </p>
 *
 * @author leizhi
 * @date 2020-09-16
 */
@Slf4j
@Service

public class TagServiceImpl extends SuperServiceImpl<TagMapper, Tag> implements TagService {

    private final AttrService attrService;
    private final DatabaseProperties databaseProperties;

    private final AssociationMapper associationMapper;

    private final PlanTagMapper planTagMapper;

    private final RedisTemplate<String, String> redisTemplate;

    public TagServiceImpl(AttrService attrService,
                          DatabaseProperties databaseProperties,
                          AssociationMapper associationMapper,
                          RedisTemplate<String, String> redisTemplate,
                          PlanTagMapper planTagMapper) {

        this.attrService = attrService;
        this.databaseProperties = databaseProperties;
        this.associationMapper = associationMapper;
        this.planTagMapper = planTagMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean removeById(Serializable id) {
        baseMapper.deleteById(id);
        LbqWrapper<Attr> wrapper = Wraps.<Attr>lbQ().eq(Attr::getTagId, id);
        attrService.remove(wrapper);
        LbqWrapper<Association> associationLbqWrapper =
                Wraps.<Association>lbQ().eq(Association::getTagId, id);
        associationMapper.delete(associationLbqWrapper);
        LbqWrapper<PlanTag> tagLbqWrapper = Wraps.<PlanTag>lbQ().eq(PlanTag::getTagId, id);
        planTagMapper.delete(tagLbqWrapper);
        return true;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        baseMapper.deleteBatchIds(idList);
        LbqWrapper<Attr> wrapper = Wraps.<Attr>lbQ().in(Attr::getTagId, idList);
        attrService.remove(wrapper);
        LbqWrapper<Association> associationLbqWrapper =
                Wraps.<Association>lbQ().in(Association::getTagId, idList);
        associationMapper.delete(associationLbqWrapper);
        LbqWrapper<PlanTag> tagLbqWrapper = Wraps.<PlanTag>lbQ().in(PlanTag::getTagId, idList);
        planTagMapper.delete(tagLbqWrapper);
        return true;
    }

    @Override
    public boolean save(Tag model) {
        model.setHandleAttrBind(0);
        return super.save(model);
    }

    @Override
    public boolean updateById(Tag model) {
        model.setHandleAttrBind(0);
        return super.updateById(model);
    }

    @Transactional
    @Override
    public void openTagBindPatientTask(Long tagId) {

        Tag tag = getById(tagId);
        if (tag.getHandleAttrBind() != null && tag.getHandleAttrBind().equals(2)) {
            throw new BizException("标签同步任务正在执行中，请稍等");
        }
        tag.setHandleAttrBind(2);
        baseMapper.updateById(tag);

        // 发布任务之前 检查一个标签是否有属性设置
        int count = attrService.count(Wraps.<Attr>lbQ().eq(Attr::getTagId, tagId));
        if (count == 0) {
            tag.setHandleAttrBind(1);
            baseMapper.updateById(tag);
            return;
        }
        String tenant = BaseContextHandler.getTenant();
        AttrBindChangeDto changeDto = new AttrBindChangeDto();
        changeDto.setEvent(AttrBindEvent.ATTR_CHANGE);
        changeDto.setTenantCode(tenant);
        changeDto.setTagId(tagId);
        String string = JSON.toJSONString(changeDto);
        redisTemplate.opsForList().leftPush(TagBindRedisKey.TENANT_ATTR_BIND, string);

    }

    @Override
    public void judgeUpdate(Long tagId) {
        // 判断是否可以更新标签
        if (tagId == null) {
            return;
        }
        Long size = redisTemplate.opsForSet().size(TagBindRedisKey.getTagBindKey(BaseContextHandler.getTenant(), tagId));
        if (size != null && size > 0) {
            throw new BizException("标签同步任务正在执行中，暂时不可修改");
        }
        Tag tag = baseMapper.selectById(tagId);
        if (tag.getHandleAttrBind() != null && tag.getHandleAttrBind().equals(2)) {
            throw new BizException("标签同步任务正在执行中，暂时不可修改");
        }

    }

    @Override
    public List<TagPatientCountResult> selectTagCountPatientNumber(Long serviceAdvisorId, Long doctorId, List<Long> doctorIds) {

        List<TagPatientCountResult> countResults = associationMapper.selectTagCountPatientNumber(serviceAdvisorId, doctorId, doctorIds);
        return countResults;
    }

    @Override
    public Boolean copyTag(String fromTenantCode, String toTenantCode) {
        String currentTenant = BaseContextHandler.getTenant();

        // 查找出需要复制的项目数据
        BaseContextHandler.setTenant(fromTenantCode);
        List<Tag> tags = baseMapper.selectList(Wrappers.emptyWrapper());
        List<Attr> tagAttrs = attrService.list();
        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());

        // 修改栏目数据
        List<Tag> toSaveTags = tags.stream().peek(c -> {
            c.setId(snowflake.nextId());
            c.setHandleAttrBind(0);
            }
        ).collect(Collectors.toList());

        // 新旧id匹配
        Map<Long, Long> tagIdMap = new HashMap<>();
        for (Tag fromTag : tags) {
            for (Tag toSaveTag : toSaveTags) {
                boolean isSame = Objects.equals(fromTag.getName(), toSaveTag.getName());
                if (!isSame) {
                    continue;
                }
                tagIdMap.put(fromTag.getId(), toSaveTag.getId());
            }
        }
        // 替换标签属性关联的标签id
        for (Attr attr : tagAttrs) {
            attr.setTagId(tagIdMap.get(attr.getTagId()));
            attr.setId(null);
        }

        // 保存修改后的数据
        BaseContextHandler.setTenant(toTenantCode);
        saveBatchSomeColumn(toSaveTags);
        attrService.saveBatch(tagAttrs);
        BaseContextHandler.setTenant(currentTenant);
        return true;
    }
}
