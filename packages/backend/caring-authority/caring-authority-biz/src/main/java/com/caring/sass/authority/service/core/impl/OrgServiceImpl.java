package com.caring.sass.authority.service.core.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.caring.sass.authority.dao.core.OrgMapper;
import com.caring.sass.authority.entity.auth.RoleOrg;
import com.caring.sass.authority.entity.core.Org;
import com.caring.sass.authority.service.auth.RoleOrgService;
import com.caring.sass.authority.service.auth.UserService;
import com.caring.sass.authority.service.core.OrgService;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperCacheServiceImpl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.oauth.api.NursingStaffApi;
import com.caring.sass.utils.MapHelper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.caring.sass.common.constant.CacheKey.ORG;

/**
 * <p>
 * 业务实现类
 * 组织
 * </p>
 *
 * @author caring
 * @date 2019-07-22
 */
@Slf4j
@Service

public class OrgServiceImpl extends SuperCacheServiceImpl<OrgMapper, Org> implements OrgService {
    @Autowired
    private RoleOrgService roleOrgService;

    @Autowired
    private UserService userService;
    
    @Autowired
    NursingStaffApi nursingStaffApi;

    @Override
    protected String getRegion() {
        return ORG;
    }

    @Override
    public List<Org> findChildren(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        // MySQL 全文索引
        String applySql = String.format(" MATCH(tree_path) AGAINST('%s' IN BOOLEAN MODE) ", StringUtils.join(ids, " "));

        return super.list(Wraps.<Org>lbQ().in(Org::getId, ids).or(query -> query.apply(applySql)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(List<Long> ids) {
        List<Org> list = this.findChildren(ids);
        List<Long> idList = list.stream().mapToLong(Org::getId).boxed().collect(Collectors.toList());

        boolean bool = !idList.isEmpty() ? super.removeByIds(idList) : true;

        // 删除自定义类型的数据权限范围
        roleOrgService.remove(Wraps.<RoleOrg>lbQ().in(RoleOrg::getOrgId, idList));
        return bool;
    }

    @Override
    public Map<Serializable, Object> findOrgByIds(Set<Serializable> ids) {
        List<Org> list = getOrgs(ids);

        //key 是 组织id， value 是org 对象
        ImmutableMap<Serializable, Object> typeMap = MapHelper.uniqueIndex(list, Org::getId, (org) -> org);
        return typeMap;
    }

    private List<Org> getOrgs(Set<Serializable> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> idList = ids.stream().mapToLong(Convert::toLong).boxed().collect(Collectors.toList());

        List<Org> list = null;
        if (idList.size() <= 1000) {
            list = idList.stream().map(this::getByIdCache).filter(Objects::nonNull).collect(Collectors.toList());
        } else {
            LbqWrapper<Org> query = Wraps.<Org>lbQ()
                    .in(Org::getId, idList)
                    .eq(Org::getStatus, true);
            list = super.list(query);

            if (!list.isEmpty()) {
                list.forEach(item -> {
                    String itemKey = key(item.getId());
                    cacheChannel.set(getRegion(), itemKey, item);
                });
            }
        }
        return list;
    }

    @Override
    public Map<Serializable, Object> findOrgNameByIds(Set<Serializable> ids) {
        List<Org> list = getOrgs(ids);

        //key 是 组织id， value 是org 对象
        ImmutableMap<Serializable, Object> typeMap = MapHelper.uniqueIndex(list, Org::getId, Org::getLabel);
        return typeMap;
    }

    @Override
    protected R<Boolean> handlerSave(Org model) {
        // 设置
        model.setCode(generateCode(10000L));
        baseMapper.insert(model);
        return R.success();
    }

    /**
     * 获取不重复的随机6位数字的字符串
     */
    private String generateCode(Long times) {
        // 超过10w次推出循环？
        if (times > 100000L) {
            throw new RuntimeException("生成机构码异常");
        }

        int[] ints = RandomUtil.randomInts(6);
        StringBuilder builder = new StringBuilder();
        for (int anInt : ints) {
            builder.append(anInt);
        }
        String code = builder.toString();
        boolean exist = baseMapper.selectCount(Wraps.<Org>lbQ().eq(Org::getCode, code)) > 0;
        if (!exist) {
            return code;
        }
        return generateCode(++times);
    }


    @Override
    public List<Org> listWithScope(LbqWrapper<Org> wrapper) {
        Long userId = BaseContextHandler.getUserId();
        Map<String, Object> u = userService.getDataScopeById(userId);
        List<Long> orgIds = (List<Long>) u.get("orgIds");
        wrapper.in(Org::getId,orgIds);
        return baseMapper.selectList(wrapper);
    }


    @Override
    public boolean updateAllById(Org model) {
        Org org = baseMapper.selectById(model.getId());
        if (StringUtils.isNotEmpty(model.getLabel()) && !StringUtils.equals(org.getLabel(), model.getLabel())) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orgId", model.getId());
            jsonObject.put("orgName", model.getLabel());
            nursingStaffApi.updateUserOrgName(jsonObject);
        }

        return super.updateAllById(model);
    }

    @Override
    public boolean updateById(Org model) {

        Org org = baseMapper.selectById(model.getId());
        if (StringUtils.isNotEmpty(model.getLabel()) && !StringUtils.equals(org.getLabel(), model.getLabel())) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orgId", model.getId());
            jsonObject.put("orgName", model.getLabel());
            nursingStaffApi.updateUserOrgName(jsonObject);
        }
        return super.updateById(model);
    }
}
