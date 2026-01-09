package com.caring.sass.authority.service.common.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.caring.sass.authority.dao.common.DictionaryItemMapper;
import com.caring.sass.authority.entity.common.DictionaryItem;
import com.caring.sass.authority.service.common.DictionaryItemService;
import com.caring.sass.base.service.SuperCacheServiceImpl;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.injection.properties.InjectionProperties;
import com.caring.sass.tenant.api.TenantApi;
import com.caring.sass.utils.MapHelper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.caring.sass.common.constant.CacheKey.DICTIONARY_ITEM;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 业务实现类
 * 字典项
 * </p>
 *
 * @author caring
 * @date 2019-07-02
 */
@Slf4j
@Service

public class DictionaryItemServiceImpl extends SuperCacheServiceImpl<DictionaryItemMapper, DictionaryItem> implements DictionaryItemService {

    @Autowired
    private InjectionProperties ips;

    @Autowired
    TenantApi tenantApi;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    protected String getRegion() {
        return DICTIONARY_ITEM;
    }




    @Override
    public Map<String, Map<String, String>> map(String[] types) {
        if (ArrayUtil.isEmpty(types)) {
            return Collections.emptyMap();
        }
        LbqWrapper<DictionaryItem> query = Wraps.<DictionaryItem>lbQ()
                .in(DictionaryItem::getDictionaryType, types)
                .eq(DictionaryItem::getStatus, true)
                .orderByAsc(DictionaryItem::getSortValue);
        List<DictionaryItem> list = super.list(query);

        //key 是类型
        Map<String, List<DictionaryItem>> typeMap = list.stream().collect(groupingBy(DictionaryItem::getDictionaryType, LinkedHashMap::new, toList()));

        //需要返回的map
        Map<String, Map<String, String>> typeCodeNameMap = new LinkedHashMap<>(typeMap.size());

        typeMap.forEach((key, items) -> {
            ImmutableMap<String, String> itemCodeMap = MapHelper.uniqueIndex(items, DictionaryItem::getCode, DictionaryItem::getName);
            typeCodeNameMap.put(key, itemCodeMap);
        });
        return typeCodeNameMap;
    }

    @Override
    public Map<Serializable, Object> findDictionaryItem(Set<Serializable> codes) {
        if (codes.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<String> types = codes.stream().filter(Objects::nonNull)
                .map((item) -> StrUtil.split(String.valueOf(item), ips.getDictSeparator())[0]).collect(Collectors.toSet());
        Set<String> newCodes = codes.stream().filter(Objects::nonNull)
                .map((item) -> StrUtil.split(String.valueOf(item), ips.getDictSeparator())[1]).collect(Collectors.toSet());

        // 1. 根据 字典编码查询可用的字典列表
        LbqWrapper<DictionaryItem> query = Wraps.<DictionaryItem>lbQ()
                .in(DictionaryItem::getDictionaryType, types)
                .in(DictionaryItem::getCode, newCodes)
                .eq(DictionaryItem::getStatus, true)
                .orderByAsc(DictionaryItem::getSortValue);
        List<DictionaryItem> list = super.list(query);

        // 2. 将 list 转换成 Map，Map的key是字典编码，value是字典名称
        ImmutableMap<String, String> typeMap = MapHelper.uniqueIndex(list,
                (item) -> StrUtil.join(ips.getDictSeparator(), item.getDictionaryType(), item.getCode())
                , DictionaryItem::getName);

        // 3. 将 Map<String, String> 转换成 Map<Serializable, Object>
        Map<Serializable, Object> typeCodeNameMap = new HashMap<>(typeMap.size());
        typeMap.forEach((key, value) -> typeCodeNameMap.put(key, value));
        return typeCodeNameMap;
    }

    @Override
    public boolean updateById(DictionaryItem model) {
        String tenant = BaseContextHandler.getTenant();
        redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_DICTIONARY + tenant)
                .put(model.getId().toString(), JSON.toJSONString(model));
        super.updateById(model);
        if (model.getCode().equals(UserType.UCENTER_DOCTOR)) {
            // 要求 项目更新医生的激活码，
            tenantApi.updateDoctorQrCode(tenant, model.getName());
        }
        return true;
    }

    @Override
    public String findDictionaryItemName(String code) {
        List<DictionaryItem> dictionaryItems = list(Wraps.<DictionaryItem>lbQ());
        for (DictionaryItem item : dictionaryItems) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return null;
    }

    @Override
    public List<DictionaryItem> list(Wrapper<DictionaryItem> queryWrapper) {
        String tenant = BaseContextHandler.getTenant();
        List<Object> objectList = redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_DICTIONARY + tenant).values();
        if (Objects.nonNull(objectList) && CollUtil.isNotEmpty(objectList)) {
            List<DictionaryItem> dictionaryItems = new ArrayList<>();
            for (Object object : objectList) {
                DictionaryItem dictionaryItem = JSON.parseObject(object.toString(), DictionaryItem.class);
                dictionaryItems.add(dictionaryItem);
            }
            dictionaryItems.sort((o1, o2) -> {
                if (o1.getSortValue() == null || o2.getSortValue() == null) {
                    return -1;
                }
                if (o1.getSortValue() < o2.getSortValue()) {
                    return -1;
                } else {
                    return 1;
                }
            });
            return dictionaryItems;
        }
        List<DictionaryItem> dictionaryItems = baseMapper.selectList(Wraps.<DictionaryItem>lbQ().orderByAsc(DictionaryItem::getSortValue));
        for (DictionaryItem dictionaryItem : dictionaryItems) {
            redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_DICTIONARY + tenant)
                    .put(dictionaryItem.getId().toString(), JSON.toJSONString(dictionaryItem));
        }
        return dictionaryItems;
    }
}
