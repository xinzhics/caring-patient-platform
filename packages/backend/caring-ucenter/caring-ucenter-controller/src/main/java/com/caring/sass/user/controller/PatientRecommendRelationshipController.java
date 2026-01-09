package com.caring.sass.user.controller;


import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.user.dto.GeneratePatientInvitationQRcodeDTO;
import com.caring.sass.user.dto.PatientRecommendRelationshipPageDTO;
import com.caring.sass.user.dto.PatientRecommendRelationshipSaveDTO;
import com.caring.sass.user.dto.PatientRecommendRelationshipUpdateDTO;
import com.caring.sass.user.entity.PatientRecommendRelationship;
import com.caring.sass.user.service.PatientRecommendRelationshipService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * 运营配置-患者推荐关系
 * </p>
 *
 * @author lixiang
 * @date 2022-07-14
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patientRecommendRelationship")
@Api(value = "PatientRecommendRelationship", tags = "运营配置-患者推荐关系")
//@PreAuth(replace = "patientRecommendRelationship:")
public class PatientRecommendRelationshipController extends SuperController<PatientRecommendRelationshipService, Long, PatientRecommendRelationship, PatientRecommendRelationshipPageDTO, PatientRecommendRelationshipSaveDTO, PatientRecommendRelationshipUpdateDTO> {


    @ApiOperationSupport(author = "李祥", order = 1)
    @ApiOperation(value = "推荐人生成邀请二维码")
    @GetMapping("generatePatientInvitationQRcode")
    public R<GeneratePatientInvitationQRcodeDTO> generatePatientInvitationQRcode(@ApiParam(value = "患者ID",required = true)  @RequestParam(value = "patientId") Long patientId) {
        GeneratePatientInvitationQRcodeDTO result = baseService.generatePatientInvitationQRcode(patientId);
        return R.success(result);
    }

}
