package com.caring.sass.nursing.service.drugs.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.dao.drugs.DrugsResultHandleHisMapper;
import com.caring.sass.nursing.dto.drugs.DrugsAvailableDTO;
import com.caring.sass.nursing.entity.drugs.DrugsResultHandleHis;
import com.caring.sass.nursing.enumeration.ClearStatusEnum;
import com.caring.sass.nursing.service.drugs.DrugsResultHandleHisService;
import com.caring.sass.nursing.vo.DrugsAvailableVo;
import com.caring.sass.nursing.vo.DrugsHandledVo;
import com.caring.sass.oauth.api.PatientApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者管理-用药预警-预警处理历史表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-22
 */
@Slf4j
@Service

public class DrugsResultHandleHisServiceImpl extends SuperServiceImpl<DrugsResultHandleHisMapper, DrugsResultHandleHis> implements DrugsResultHandleHisService {


    @Autowired
    PatientApi patientApi;

    /**
     * 用药预警-余药不足（逾期）已处理列表
     *
     *
     */
    @Override
    public IPage<DrugsHandledVo> getDrugsHandled(DrugsAvailableDTO dto) {
        dto.setTenant(BaseContextHandler.getTenant());
        dto.setNursingId(BaseContextHandler.getUserId());
        Page page = new Page();
        page.setCurrent(dto.getCurrent());
        page.setSize(dto.getSize());
        IPage<DrugsHandledVo> result = this.getBaseMapper().selectDrugsHandled(page, ClearStatusEnum.UN_CLEAR.getCode(), dto);
        List<DrugsHandledVo> records = result.getRecords();
        Map<Long, String> data ;
        if (CollUtil.isNotEmpty(records)) {
            List<Long> patientIds = records.stream().map(DrugsHandledVo::getPatientId).collect(Collectors.toList());
            R<Map<Long, String>> nursingPatientRemark = patientApi.findNursingPatientRemark(patientIds);
            data = nursingPatientRemark.getData();
            for (DrugsHandledVo record : records) {
                String s = data.get(record.getPatientId());
                if (StrUtil.isNotEmpty(s)) {
                    record.setPatientName(record.getPatientName() + " (" +s+ ")");
                }
            }
        }

        return result;
    }

    /**
     * 用药预警-已处理-清除标记
     *
     * @param
     */
    @Override
    public R getDrugsEliminate() {
        LambdaUpdateWrapper<DrugsResultHandleHis> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(DrugsResultHandleHis::getTenantCode,BaseContextHandler.getTenant());
        lqw.eq(DrugsResultHandleHis::getNursingId,BaseContextHandler.getUserId());
        lqw.eq(DrugsResultHandleHis::getClearStatus,ClearStatusEnum.UN_CLEAR.getCode());
        lqw.set(DrugsResultHandleHis::getClearStatus,ClearStatusEnum.AL_CLEAR.getCode());

        return R.success(this.update(lqw));
    }
}
