package com.caring.sass.nursing.controller.information;

import com.caring.sass.base.R;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.nursing.dto.information.PatientInformationFieldPageDTO;
import com.caring.sass.nursing.dto.information.PatientInformationFieldSaveDTO;
import com.caring.sass.nursing.dto.information.PatientInformationFieldUpdateDTO;
import com.caring.sass.nursing.entity.information.PatientInformationField;
import com.caring.sass.nursing.service.information.PatientInformationFieldService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 患者信息完整度字段
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/patientInformationField")
@Api(value = "PatientInformationField", tags = "患者信息完整度字段")
//@PreAuth(replace = "patientInformationField:")
public class PatientInformationFieldController extends SuperController<PatientInformationFieldService, Long, PatientInformationField, PatientInformationFieldPageDTO, PatientInformationFieldSaveDTO, PatientInformationFieldUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<PatientInformationField> patientInformationFieldList = list.stream().map((map) -> {
            PatientInformationField patientInformationField = PatientInformationField.builder().build();
            //TODO 请在这里完成转换
            return patientInformationField;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(patientInformationFieldList));
    }
}
