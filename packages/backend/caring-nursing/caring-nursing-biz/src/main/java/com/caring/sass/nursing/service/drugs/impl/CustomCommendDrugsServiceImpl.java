package com.caring.sass.nursing.service.drugs.impl;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.dao.drugs.CustomCommendDrugsMapper;
import com.caring.sass.nursing.entity.drugs.CustomCommendDrugs;
import com.caring.sass.nursing.entity.drugs.SysDrugs;
import com.caring.sass.nursing.service.drugs.CustomCommendDrugsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 自定义推荐药品
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class CustomCommendDrugsServiceImpl extends SuperServiceImpl<CustomCommendDrugsMapper, CustomCommendDrugs> implements CustomCommendDrugsService {

    @Override
    public Boolean copyRecommendDrugs(String fromTenantCode, String toTenantCode) {
        String currentTenant = BaseContextHandler.getTenant();

        BaseContextHandler.setTenant(fromTenantCode);
        List<CustomCommendDrugs> customCommendDrugs = baseMapper.selectList(Wrappers.emptyWrapper());

        // 修改数据
        List<CustomCommendDrugs> toSaveCommendDrugs = customCommendDrugs.stream().peek(f -> f.setId(null)).collect(Collectors.toList());
        BaseContextHandler.setTenant(toTenantCode);
        saveBatchSomeColumn(toSaveCommendDrugs);
        BaseContextHandler.setTenant(currentTenant);
        return true;
    }

    public static void main(String[] args) {
        StringBuilder buf = new StringBuilder(256);

        List<Long> longs = new ArrayList<>();
        longs.add(1l);

        Iterator<Long> iterator = longs.iterator();
        while(iterator.hasNext()) {
            if (buf.length() > 0) {
                buf.append(",");
            }
            buf.append("'");
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj).append("'");
            }
        }
        System.out.println(buf.toString());

    }

    @Override
    public void deleteByDrugsIds(List<Long> longs) {

        if (CollectionUtils.isNotEmpty(longs)) {
            StringBuilder buf = new StringBuilder(256);
            Iterator<Long> iterator = longs.iterator();
            while(iterator.hasNext()) {
                if (buf.length() > 0) {
                    buf.append(",");
                }
                buf.append("'");
                Object obj = iterator.next();
                if (obj != null) {
                    buf.append(obj).append("'");
                }
            }

            baseMapper.deleteByDrugsLd(buf.toString());
        }
    }
}
