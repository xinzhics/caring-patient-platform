package com.caring.sass.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.SensitiveInfoUtils;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.user.dto.DoctorAuditPageDTO;
import com.caring.sass.user.dto.DoctorAuditSaveDTO;
import com.caring.sass.user.dto.DoctorAuditUpdateDTO;
import com.caring.sass.user.entity.DoctorAudit;
import com.caring.sass.user.entity.Patient;
import com.caring.sass.user.service.DoctorAuditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName DoctorAuditContorller
 * @Description
 * @Author yangShuai
 * @Date 2022/2/22 14:11
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/doctorAudit")
@Api(value = "DoctorAudit", tags = "医生审核业务")
public class DoctorAuditController extends SuperController<DoctorAuditService, Long, DoctorAudit, DoctorAuditPageDTO,
        DoctorAuditSaveDTO, DoctorAuditUpdateDTO> {


    @ApiOperation("提交医生审核申请")
    @PostMapping("/anno/submitDoctorAudit")
    public R<Boolean> submitDoctorAudit(@RequestBody @Validated DoctorAuditSaveDTO auditSaveDTO) {

        DoctorAudit audit = new DoctorAudit();
        BeanUtils.copyProperties(auditSaveDTO, audit);
        audit.setAuditSort(0);
        audit.setAuditStatus("apply");
        String passwordMd5 = SecureUtil.md5(auditSaveDTO.getPassword());
        if (Objects.nonNull(auditSaveDTO.getId())) {
            audit.setId(auditSaveDTO.getId());
            audit.setPassword(passwordMd5);
            baseService.updateById(audit);
        } else {
            audit.setPassword(passwordMd5);
            baseService.save(audit);
        }
        return R.success(true);
    }


    @ApiOperation("手机号是否已经在审核")
    @GetMapping("/anno/phoneAuditExist")
    public R<Boolean> phoneAuditExist(@RequestParam String phone) {

        DoctorAudit doctorAudit = baseService.getOne(Wraps.<DoctorAudit>lbQ()
                .eq(DoctorAudit::getMobile, phone)
                .eq(DoctorAudit::getAuditStatus, "apply"));
        if (Objects.nonNull(doctorAudit)) {
            return R.success(true);
        }
        return R.success(false);

    }



    @ApiOperation("审核医生申请")
    @PostMapping("/auditApply")
    public R<Boolean> auditApply(@RequestBody @Validated DoctorAuditUpdateDTO auditUpdateDTO) {

        DoctorAudit audit = new DoctorAudit();
        BeanUtils.copyProperties(auditUpdateDTO, audit);
        if (Objects.nonNull(auditUpdateDTO.getId())) {
            audit.setId(auditUpdateDTO.getId());
            audit.setAuditSort(1);
            baseService.updateById(audit);
        }
        return R.success(true);
    }

    @Override
    public R<IPage<DoctorAudit>> page(PageParams<DoctorAuditPageDTO> params) {

        DoctorAuditPageDTO model = params.getModel();

        DoctorAudit queryParam = BeanUtil.toBean(model, DoctorAudit.class);
        QueryWrap<DoctorAudit> wrap = handlerWrapper(queryParam, params);
        IPage<DoctorAudit> buildPage = params.buildPage();
        LbqWrapper<DoctorAudit> wrapper = wrap.lambda();
        if (StringUtils.isNotEmptyString(model.getName())) {
            wrapper.like(DoctorAudit::getName, model.getName());
        }
        if (StringUtils.isNotEmptyString(model.getMobile())) {
            wrapper.like(DoctorAudit::getMobile, model.getMobile());
        }
        if (StringUtils.isNotEmptyString(model.getHospitalName())) {
            wrapper.like(DoctorAudit::getHospitalName, model.getHospitalName());
        }
        if (StringUtils.isNotEmptyString(model.getAuditStatus())) {
            wrapper.eq(DoctorAudit::getAuditStatus, model.getAuditStatus());
        }
        wrapper.orderByAsc(DoctorAudit::getAuditSort);
        wrapper.orderByDesc(DoctorAudit::getCreateTime);
        baseService.page(buildPage, wrapper);
        baseService.desensitization(buildPage.getRecords());
        return R.success(buildPage);
    }


}
