package com.caring.sass.nursing.service.form.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.redis.SaasRedisBusinessKey;
import com.caring.sass.common.utils.BigDecimalUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.auth.DataScope;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.exception.BizException;
import com.caring.sass.nursing.constant.FormResultExportConstant;
import com.caring.sass.nursing.dao.form.FormMapper;
import com.caring.sass.nursing.dao.form.FormResultExportMapper;
import com.caring.sass.nursing.dao.plan.PlanMapper;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.entity.form.FormResultExport;
import com.caring.sass.nursing.entity.plan.Plan;
import com.caring.sass.nursing.enumeration.FormEnum;
import com.caring.sass.nursing.service.exoprt.FormResultExportTask;
import com.caring.sass.nursing.service.form.FormResultExportService;
import com.caring.sass.nursing.service.task.DateUtils;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Patient;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 业务实现类
 * 表单结果导出记录表
 * </p>
 *
 * @author yangshuai
 * @date 2022-07-13
 */
@Slf4j
@Service

public class FormResultExportServiceImpl extends SuperServiceImpl<FormResultExportMapper, FormResultExport> implements FormResultExportService {

    // 生成一个导出的序号
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    PlanMapper planMapper;

    @Autowired
    FormMapper formMapper;

    @Autowired
    FormResultExportTask formResultExportTask;

    @Autowired
    PatientApi patientApi;

    @Override
    public IPage<FormResultExport> findPage(IPage<FormResultExport>  page, Wrapper<FormResultExport> queryWrapper) {
        return  baseMapper.findPage(page, queryWrapper, new DataScope());
    }

    /**
     * 更新导出的进度
     * @param id
     * @param total
     * @param queryTotal
     */
    @Override
    public void updateExportProgress(Long id, long total, long queryTotal) {

        BigDecimal totalDecimal = new BigDecimal(total);
        BigDecimal chartDecimal = new BigDecimal(queryTotal);
        int proportion = BigDecimalUtils.proportion(chartDecimal, totalDecimal);

        if (proportion == 100) {
            FormResultExport export = FormResultExport.builder()
                    .exportTotal(Integer.parseInt(total + ""))
                    .exportProgress(99).build();
            export.setId(id);
            baseMapper.updateById(export);

        } else {
            FormResultExport export = FormResultExport.builder()
                    .exportTotal(Integer.parseInt(total + ""))
                    .exportProgress(proportion).build();
            export.setId(id);
            baseMapper.updateById(export);
        }
    }

    @Override
    public void updateExportMessage(Long id, String message) {
        FormResultExport export = FormResultExport.builder()
                .message(message).build();
        export.setId(id);
        baseMapper.updateById(export);
    }

    @Override
    public void updateExportResult(Long id, String uploadDataUrl) {
        FormResultExport export = FormResultExport.builder()
                .exportProgress(100)
                .exportFileUrl(uploadDataUrl).build();
        export.setId(id);
        baseMapper.updateById(export);
        String tenant = BaseContextHandler.getTenant();
        redisTemplate.delete(SaasRedisBusinessKey.getTenantExportTaskId(tenant, id));
    }

    @Override
    public boolean save(FormResultExport model) {

        String exportName = getExportName(model.getExportType(), model);
        model.setExportFileName(exportName);
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(SaasRedisBusinessKey.TENANT_EXPORT_COUNT);
        String tenant = BaseContextHandler.getTenant();
        Boolean hasKey = boundHashOps.hasKey(tenant);
        if (hasKey != null && hasKey) {
            Long increment = boundHashOps.increment(tenant, 1);
            model.setExportId(increment);
        } else {
            boundHashOps.increment(tenant, 10000);
            model.setExportId(10000L);
        }
        // 把任务添加的redis中。每导出一个患者的数据，校验任务是否还有效，当redis中任务被移出之后，导出任务终结，退出。
        boolean save = super.save(model);
        if (save) {
            BoundValueOperations<String, String> valueOps = redisTemplate.boundValueOps(SaasRedisBusinessKey.getTenantExportTaskId(tenant, model.getId()));
            valueOps.set(model.getId().toString());
        } else {
            boundHashOps.increment(tenant, -1);
        }
        formResultExportTask.execute(model, tenant);

        return save;
    }

    /**
     * 导出的表格的名字
     * @param exportType
     * @param model
     * @return
     */
    public String getExportName(String exportType, FormResultExport model) {
        StringBuilder stringBuilder = new StringBuilder();
        if (FormResultExportConstant.baseInfo.toString().equals(exportType)) {
            // 导出的基本信息
            stringBuilder.append("基础信息_");
            String baseInfoScopeArrayJson = model.getBaseInfoScopeArrayJson();
            JSONArray jsonArray = JSON.parseArray(baseInfoScopeArrayJson);
            if (Objects.isNull(jsonArray)) {
                throw new BizException("基础信息不能为空");
            }
        } else if (FormResultExportConstant.follow_up.toString().equals(exportType)) {
            // 导出随访计划
            stringBuilder.append("随访记录_");
            String planIdArrayJson = model.getPlanIdArrayJson();
            List<Long> planIds = JSON.parseArray(planIdArrayJson, Long.class);
            if (Objects.isNull(planIds)) {
                throw new BizException("随访计划不能为空");
            }
        } else if (FormResultExportConstant.doctor.toString().equals(exportType)) {
            // 医生信息
            stringBuilder.append("医生信息_");
        } else if (FormResultExportConstant.nursing.toString().equals(exportType)) {
            // 医助信息
            stringBuilder.append("医助信息_");
        } else if (FormResultExportConstant.imMsg.toString().equals(exportType)) {
            // 聊天记录
            String patientImAccount = model.getPatientImAccount();
            R<Patient> patientName = patientApi.findPatientName(patientImAccount);
            if (patientName.getIsSuccess()) {
                Patient data = patientName.getData();
                if (Objects.nonNull(data)) {
                    model.setPatientName(data.getName());
                    stringBuilder.append(data.getName());
                }
                stringBuilder.append("聊天记录_");
            }
            // 敏宁项目定制导出代码
        } else if (FormResultExportConstant.minNing.toString().equals(exportType)){
            String idArrayJson = model.getPlanIdArrayJson();
            stringBuilder.append(idArrayJson);
            stringBuilder.append(DateUtils.date2Str(new Date(), DateUtils.YMD_)).append(".csv");
            return stringBuilder.toString();
        } else if (FormResultExportConstant.MENTAL_PATIENT_TRACKING.toString().equals(exportType)){
            stringBuilder.append("患者跟踪_");
            stringBuilder.append(DateUtils.date2Str(new Date(), DateUtils.YMD_)).append(".xlsx");
            return stringBuilder.toString();
        }
        stringBuilder.append(DateUtils.date2Str(new Date(), DateUtils.YMD_)).append(".xls");
        return stringBuilder.toString();
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return true;
    }

    @Override
    public boolean removeById(Serializable id) {
        String tenant = BaseContextHandler.getTenant();
        redisTemplate.delete(SaasRedisBusinessKey.getTenantExportTaskId(tenant, Long.parseLong(id.toString())));
        baseMapper.deleteById(id);
        return true;
    }
}


