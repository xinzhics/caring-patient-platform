package com.caring.sass.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.ApplicationDomainUtil;
import com.caring.sass.nursing.constant.H5Router;
import com.caring.sass.tenant.dao.TenantMapper;
import com.caring.sass.tenant.entity.Tenant;
import com.caring.sass.user.dao.DoctorMapper;
import com.caring.sass.user.dao.PatientRecommendSettingMapper;
import com.caring.sass.user.dto.GetPatientRecommendDoctorDTO;
import com.caring.sass.user.dto.GetPatientRecommendDoctorVo;
import com.caring.sass.user.dto.RecommendUrlVo;
import com.caring.sass.user.entity.PatientRecommendSetting;
import com.caring.sass.user.service.PatientRecommendSettingService;
import com.caring.sass.utils.BizAssert;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 运营配置-患者推荐配置
 * </p>
 *
 * @author lixiang
 * @date 2022-07-14
 */
@Slf4j
@Service
@AllArgsConstructor
public class PatientRecommendSettingServiceImpl extends SuperServiceImpl<PatientRecommendSettingMapper, PatientRecommendSetting> implements PatientRecommendSettingService {
    private DoctorMapper doctorMapper;
    private TenantMapper tenantMapper;

    /**
     * 超管端-患者推荐-查询
     *
     * @param
     */
    @Override
    public PatientRecommendSetting getPatientRecommend(String tenantCode) {
        LambdaQueryWrapper<PatientRecommendSetting> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PatientRecommendSetting::getTenantCode,tenantCode);
        PatientRecommendSetting results = baseMapper.selectOne(lqw);
        return results;
    }
    /**
     * 超管端-患者推荐-查询医生
     *
     * @param
     */
    @Override
    public IPage<GetPatientRecommendDoctorVo> getPatientRecommendDoctor(GetPatientRecommendDoctorDTO dto) {
        Page page = new Page();
        page.setCurrent(dto.getCurrent());
        page.setSize(dto.getSize());
        return doctorMapper.getPatientRecommendDoctor(page,dto);
    }
    /**
     * 超管端-患者推荐-推荐页链接地址-复制
     *
     * @param
     */
    @Override
    public RecommendUrlVo recommendUrlCopy(String tenantCode) {
        LambdaQueryWrapper<Tenant> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Tenant::getCode,tenantCode);
        Tenant tenant = tenantMapper.selectOne(lqw);
        BizAssert.notNull(tenant, "暂无该项目");
        String chatUrl = ApplicationDomainUtil.wxPatientBizUrl(tenant.getDomainName(), Objects.nonNull(tenant.getWxBindTime()),H5Router.RECOMMEND_URL);
        RecommendUrlVo results = new RecommendUrlVo();
        results.setRecommendUrl(chatUrl);
        return results;
    }
}
