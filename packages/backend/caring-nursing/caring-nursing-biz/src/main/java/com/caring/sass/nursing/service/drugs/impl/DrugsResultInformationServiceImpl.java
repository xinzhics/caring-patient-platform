package com.caring.sass.nursing.service.drugs.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.utils.BigDecimalUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.nursing.dao.drugs.DrugsConditionMonitoringMapper;
import com.caring.sass.nursing.dao.drugs.DrugsResultHandleHisMapper;
import com.caring.sass.nursing.dao.drugs.DrugsResultInformationMapper;
import com.caring.sass.nursing.dao.drugs.PatientDrugsMapper;
import com.caring.sass.nursing.dto.drugs.DrugsAvailableDTO;
import com.caring.sass.nursing.entity.drugs.DrugsConditionMonitoring;
import com.caring.sass.nursing.entity.drugs.DrugsResultHandleHis;
import com.caring.sass.nursing.entity.drugs.DrugsResultInformation;
import com.caring.sass.nursing.enumeration.ClearStatusEnum;
import com.caring.sass.nursing.enumeration.PatientDrugsStatusEnum;
import com.caring.sass.nursing.enumeration.WarningTypeEnum;
import com.caring.sass.nursing.service.drugs.DrugsResultInformationService;
import com.caring.sass.nursing.vo.DrugsAvailableMessageData;
import com.caring.sass.nursing.vo.DrugsAvailableVo;
import com.caring.sass.nursing.vo.DrugsListVo;
import com.caring.sass.nursing.vo.DrugsStatisticsVo;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.utils.BizAssert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 患者管理-用药预警-预警结果表
 * </p>
 *
 * @author yangshuai
 * @date 2022-06-22
 */
@Slf4j
@Service

public class DrugsResultInformationServiceImpl extends SuperServiceImpl<DrugsResultInformationMapper, DrugsResultInformation> implements DrugsResultInformationService {
    @Autowired
    private DrugsResultHandleHisMapper drugsResultHandleHisMapper;
    @Autowired
    private DrugsConditionMonitoringMapper drugsConditionMonitoringMapper;
    @Autowired
    private PatientDrugsMapper patientDrugsMapper;


    /**
     * 用药预警-患者数
     *
     * @param
     */
    @Override
    public Integer getDrugsNumber() {
        Integer drugsDeficiency = this.baseMapper.getDrugsNumber(BaseContextHandler.getTenant(), BaseContextHandler.getUserId(), WarningTypeEnum.NOT_DRUGS.getCode(), null);
        Integer drugsBeOverdue = this.baseMapper.getDrugsNumber(BaseContextHandler.getTenant(), BaseContextHandler.getUserId(), WarningTypeEnum.BO_DRUGS.getCode(), null);
        Integer result = drugsBeOverdue + drugsDeficiency;
        return result;
    }

    @Autowired
    PatientApi patientApi;

    /**
     * 用药预警-余药不足
     *
     * @param dto
     * @return
     */
    @Override
    public IPage<DrugsAvailableVo> getDrugsDeficiency(DrugsAvailableDTO dto) {

        dto.setTenant(BaseContextHandler.getTenant());
        dto.setNursingId(BaseContextHandler.getUserId());
        Page page = new Page();
        page.setCurrent(dto.getCurrent());
        page.setSize(dto.getSize());
        IPage<DrugsAvailableVo> result = this.getBaseMapper().selectDrugsDeficiencyPageList(page, WarningTypeEnum.NOT_DRUGS.getCode(), dto);
        List<DrugsAvailableVo> records = result.getRecords();
        List<DrugsAvailableVo> vos = new ArrayList<>();
        Map<Long, String> data = new HashMap<>();
        if (CollUtil.isNotEmpty(records)) {
            List<Long> patientIds = records.stream().map(DrugsAvailableVo::getPatientId).collect(Collectors.toList());
            R<Map<Long, String>> nursingPatientRemark = patientApi.findNursingPatientRemark(patientIds);
            data = nursingPatientRemark.getData();
        }
        for (DrugsAvailableVo record : records) {
            List<DrugsAvailableMessageData> list = this.getBaseMapper().selectDrugsDate(record.getPatientId(), WarningTypeEnum.NOT_DRUGS.getCode(), dto);
            record.setDrugsDeficiencyMessageData(list);
            String s = data.get(record.getPatientId());
            if (StrUtil.isNotEmpty(s)) {
                record.setPatientName(record.getPatientName() + " (" +s+ ")");
            }
            vos.add(record);
        }
        result.setRecords(vos);
        return result;
    }

    /**
     * 用药预警-购药逾期
     *
     * @param dto
     * @return
     */
    @Override
    public IPage<DrugsAvailableVo> getDrugsBeOverdue(DrugsAvailableDTO dto) {

        dto.setTenant(BaseContextHandler.getTenant());
        dto.setNursingId(BaseContextHandler.getUserId());
        Page page = new Page();
        page.setCurrent(dto.getCurrent());
        page.setSize(dto.getSize());
        IPage<DrugsAvailableVo> result = this.getBaseMapper().selectDrugsDeficiencyPageList(page, WarningTypeEnum.BO_DRUGS.getCode(), dto);
        List<DrugsAvailableVo> records = result.getRecords();
        List<DrugsAvailableVo> vos = new ArrayList<>();
        Map<Long, String> data = new HashMap<>();
        if (CollUtil.isNotEmpty(records)) {
            List<Long> patientIds = records.stream().map(DrugsAvailableVo::getPatientId).collect(Collectors.toList());
            R<Map<Long, String>> nursingPatientRemark = patientApi.findNursingPatientRemark(patientIds);
            data = nursingPatientRemark.getData();
        }
        for (DrugsAvailableVo record : records) {
            List<DrugsAvailableMessageData> list = this.getBaseMapper().selectDrugsDate(record.getPatientId(), WarningTypeEnum.BO_DRUGS.getCode(), dto);
            record.setDrugsDeficiencyMessageData(list);
            String s = data.get(record.getPatientId());
            if (StrUtil.isNotEmpty(s)) {
                record.setPatientName(record.getPatientName() + " (" +s+ ")");
            }
            vos.add(record);
        }
        result.setRecords(vos);
        return result;
    }

    /**
     * 用药预警-余药不足-单个（全部）处理
     *
     * @param patientId
     */
    @Override
    public R getDrugsDeficiencyHandle(Long patientId) {
        LambdaQueryWrapper<DrugsResultInformation> lqw = new LambdaQueryWrapper<>();
        //患者ID不为空则处理指定患者、 若为空则处理全部计划患者
        if (patientId != null) {
            lqw.eq(DrugsResultInformation::getPatientId, patientId);
        }
        lqw.eq(DrugsResultInformation::getNursingId, BaseContextHandler.getUserId());
        lqw.eq(DrugsResultInformation::getTenantCode, BaseContextHandler.getTenant());
        lqw.eq(DrugsResultInformation::getWarningType, WarningTypeEnum.NOT_DRUGS.getCode());
        List<DrugsResultInformation> list = this.baseMapper.selectList(lqw);
        List<DrugsResultHandleHis> handleHis = new ArrayList<>();
        for (DrugsResultInformation drugsResultInformation : list) {
            DrugsResultHandleHis vo = new DrugsResultHandleHis();
            BeanUtils.copyProperties(drugsResultInformation, vo);
            vo.setHandleUser(BaseContextHandler.getUserId());
            vo.setHandleTime(LocalDateTime.now());
            vo.setClearStatus(ClearStatusEnum.UN_CLEAR.getCode());
            handleHis.add(vo);
        }
        if (handleHis == null || handleHis.size() == 0) {
            return R.success("暂无可处理数据");
        } else {
            drugsResultHandleHisMapper.insertBatchSomeColumn(handleHis);
            this.baseMapper.delete(lqw);
            return R.success();

        }

    }

    /**
     * 用药预警-用药逾期-单个（全部）处理
     *
     * @param patientId
     */
    @Override
    public R getDrugsBeOverdueHandle(Long patientId) {
        LambdaQueryWrapper<DrugsResultInformation> lqw = new LambdaQueryWrapper<>();
        //患者ID不为空则处理指定患者、 若为空则处理全部计划患者
        if (patientId != null) {
            lqw.eq(DrugsResultInformation::getPatientId, patientId);
        }
        lqw.eq(DrugsResultInformation::getNursingId, BaseContextHandler.getUserId());
        lqw.eq(DrugsResultInformation::getTenantCode, BaseContextHandler.getTenant());
        lqw.eq(DrugsResultInformation::getWarningType, WarningTypeEnum.BO_DRUGS.getCode());
        List<DrugsResultInformation> list = this.baseMapper.selectList(lqw);
        List<DrugsResultHandleHis> handleHis = new ArrayList<>();
        for (DrugsResultInformation drugsResultInformation : list) {
            DrugsResultHandleHis vo = new DrugsResultHandleHis();
            BeanUtils.copyProperties(drugsResultInformation, vo);
            vo.setHandleUser(BaseContextHandler.getUserId());
            vo.setHandleTime(LocalDateTime.now());
            vo.setClearStatus(ClearStatusEnum.UN_CLEAR.getCode());
            handleHis.add(vo);
        }
        if (handleHis == null || handleHis.size() == 0) {
            return R.success("暂无可处理数据");
        } else {
            drugsResultHandleHisMapper.insertBatchSomeColumn(handleHis);
            this.baseMapper.delete(lqw);
            return R.success();

        }
    }


    /**
     * 用药预警-统计
     *
     * @param
     * @return
     */
    @Override
    public DrugsStatisticsVo getDrugsStatistics(Long drugsId) {
        DrugsStatisticsVo result = new DrugsStatisticsVo();
        //查询药品条件表药品
        DrugsConditionMonitoring drugsResult = DrugsResult(drugsId);
        if (drugsResult == null) {
            BizAssert.notNull(drugsResult, "暂无该药品");
            return null;
        }
        //药品id
        result.setDrugsId(drugsResult.getDrugsId());
        //药品名称
        result.setDrugsName(drugsResult.getDrugsName());
        //药品规格
        result.setSpec(drugsResult.getSpec());
        //药品厂商
        result.setManufactor(drugsResult.getManufactor());
        //当前正在用药患者人数
        Integer doGetDrugsPatient = doGetDrugsPatient(drugsId);
        if (doGetDrugsPatient == null) {
            BizAssert.notNull(doGetDrugsPatient, "暂无患者添加该药品");
            return null;
        }
        result.setDrugsPatient(doGetDrugsPatient);
        //购药逾期患者数
        result.setDrugsOverduePatient(doGetDrugsOverduePatient(drugsId));
        //余药不足患者数
        result.setDrugsDeficiencyPatient(doGetDrugsDeficiencyPatient(drugsId));
        //正常患者数
        result.setNormalPatient(result.getDrugsPatient() - result.getDrugsOverduePatient() - result.getDrugsDeficiencyPatient());
        //购药逾期患者占比
        result.setDrugsOverduePatientRatio(getPercentage(result.getDrugsOverduePatient(), result.getDrugsPatient()));
        //余药不足患者占比
        result.setDrugsDeficiencyPatientRatio(getPercentage(result.getDrugsDeficiencyPatient(), result.getDrugsPatient()));
        //正常患者占比
        result.setNormalPatientRatio(getPercentage(result.getNormalPatient(), result.getDrugsPatient()));
        return result;
    }


    /**
     * 用药预警-统计-药品列表
     *
     * @param
     */
    @Override
    public List<DrugsListVo> getDrugsList() {
        LambdaQueryWrapper<DrugsConditionMonitoring> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DrugsConditionMonitoring::getTenantCode, BaseContextHandler.getTenant());
        List<DrugsConditionMonitoring> list = drugsConditionMonitoringMapper.selectList(lqw);
        List<DrugsListVo> result = new ArrayList<>();
        for (DrugsConditionMonitoring monitoring : list) {
            DrugsListVo vo = new DrugsListVo();
            vo.setDrugsId(monitoring.getDrugsId());
            vo.setDrugsName(monitoring.getDrugsName());
            vo.setSpec(monitoring.getSpec());
            vo.setManufactor(monitoring.getManufactor());
            result.add(vo);
        }
        return result;
    }

    /**
     * 获取百分比
     *
     * @param part  分子
     * @param total 总数
     * @return
     */
    private String getPercentage(int part, int total) {
        if (total == 0) {
            return "0%";
        }
        BigDecimal totalDecimal = new BigDecimal(total);
        BigDecimal chartDecimal = new BigDecimal(part);
        return String.valueOf(BigDecimalUtils.proportion(chartDecimal, totalDecimal)) + "%";
    }

    /**
     * 查询余药不足患者数
     *
     * @param drugsId
     * @return
     */
    private int doGetDrugsDeficiencyPatient(Long drugsId) {
        DrugsConditionMonitoring drugsResult = DrugsResult(drugsId);
        Integer NOT = this.baseMapper.getDrugsNumber(BaseContextHandler.getTenant(), BaseContextHandler.getUserId(), WarningTypeEnum.NOT_DRUGS.getCode(), drugsResult.getDrugsId());
        return NOT;
    }

    /**
     * 查询购药逾期患者数
     *
     * @param drugsId
     * @return
     */
    private int doGetDrugsOverduePatient(Long drugsId) {

        DrugsConditionMonitoring drugsResult = DrugsResult(drugsId);
        Integer BO = this.baseMapper.getDrugsNumber(BaseContextHandler.getTenant(), BaseContextHandler.getUserId(), WarningTypeEnum.BO_DRUGS.getCode(), drugsResult.getDrugsId());
        return BO;
    }

    /**
     * 查询当前用药人数
     *
     * @return
     */
    private Integer doGetDrugsPatient(Long drugsId) {
        DrugsConditionMonitoring drugsResult = DrugsResult(drugsId);
        Integer integer = patientDrugsMapper.doGetDrugsPatient(drugsResult.getDrugsId(), PatientDrugsStatusEnum.ON.getCode(), BaseContextHandler.getUserId());
        BizAssert.notNull(integer, "暂无患者添加该药品");
        return integer;
    }


    /**
     * 查询药品条件表药品
     *
     * @return
     */
    private DrugsConditionMonitoring DrugsResult(Long drugsId) {
        LambdaQueryWrapper<DrugsConditionMonitoring> lqw = new LambdaQueryWrapper<>();
        if (drugsId != null) {
            lqw.eq(DrugsConditionMonitoring::getDrugsId, drugsId);
        }
        lqw.eq(DrugsConditionMonitoring::getTenantCode, BaseContextHandler.getTenant());
        List<DrugsConditionMonitoring> list = drugsConditionMonitoringMapper.selectList(lqw);

        DrugsConditionMonitoring drugsConditionMonitoring = new DrugsConditionMonitoring();
        for (int i = 0; i == 0; i++) {
            if (list != null && list.size() != 0) {
                drugsConditionMonitoring = list.get(0);
            } else {
                return null;
            }

        }
        return drugsConditionMonitoring;
    }

}
