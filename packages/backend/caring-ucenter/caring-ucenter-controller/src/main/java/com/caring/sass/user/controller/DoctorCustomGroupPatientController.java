package com.caring.sass.user.controller;

import com.caring.sass.user.entity.DoctorCustomGroupPatient;
import com.caring.sass.user.dto.DoctorCustomGroupPatientSaveDTO;
import com.caring.sass.user.dto.DoctorCustomGroupPatientUpdateDTO;
import com.caring.sass.user.dto.DoctorCustomGroupPatientPageDTO;
import com.caring.sass.user.service.DoctorCustomGroupPatientService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caring.sass.security.annotation.PreAuth;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 医生的自定义小组患者
 * </p>
 *
 * @author yangshuai
 * @date 2022-08-11
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/doctorCustomGroupPatient")
@Api(value = "DoctorCustomGroupPatient", tags = "医生的自定义小组患者")
@PreAuth(replace = "doctorCustomGroupPatient:")
public class DoctorCustomGroupPatientController extends SuperController<DoctorCustomGroupPatientService, Long, DoctorCustomGroupPatient, DoctorCustomGroupPatientPageDTO, DoctorCustomGroupPatientSaveDTO, DoctorCustomGroupPatientUpdateDTO> {

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list){
        List<DoctorCustomGroupPatient> doctorCustomGroupPatientList = list.stream().map((map) -> {
            DoctorCustomGroupPatient doctorCustomGroupPatient = DoctorCustomGroupPatient.builder().build();
            //TODO 请在这里完成转换
            return doctorCustomGroupPatient;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(doctorCustomGroupPatientList));
    }
}
