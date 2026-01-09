package com.caring.sass.ai.ckd.server.impl;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.ai.ckd.dao.CkdUserGfrMapper;
import com.caring.sass.ai.ckd.server.CkdUserGfrService;
import com.caring.sass.ai.dto.ckd.CkdGfrTrend;
import com.caring.sass.ai.entity.ckd.CkdUserGfr;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.taskdefs.War;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 用户GFR
 * </p>
 *
 * @author 杨帅
 * @date 2025-02-08
 */
@Slf4j
@Service

public class CkdUserGfrServiceImpl extends SuperServiceImpl<CkdUserGfrMapper, CkdUserGfr> implements CkdUserGfrService {
    @Override
    public CkdGfrTrend getGfrTrend(String openId) {

        List<CkdUserGfr> ckdUserGfrList = baseMapper.selectList(Wraps.<CkdUserGfr>lbQ().eq(CkdUserGfr::getOpenId, openId)
                .orderByDesc(SuperEntity::getCreateTime)
                .last(" limit 10 "));
        CkdGfrTrend gfrTrend = new CkdGfrTrend();
        if (CollUtil.isEmpty(ckdUserGfrList)) {
            return gfrTrend;
        }
        List<LocalDateTime> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();
        for (int i = ckdUserGfrList.size() - 1; i >= 0 ; i--) {
            CkdUserGfr userGfr = ckdUserGfrList.get(i);
            xData.add(userGfr.getCreateTime());
            yData.add(userGfr.getGfr());
        }
        gfrTrend.setXData(xData);
        gfrTrend.setYData(yData);
        return gfrTrend;
    }
}
