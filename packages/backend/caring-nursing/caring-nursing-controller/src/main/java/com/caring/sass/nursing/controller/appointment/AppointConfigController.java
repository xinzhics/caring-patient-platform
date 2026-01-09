package com.caring.sass.nursing.controller.appointment;

import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.nursing.entity.appointment.AppointConfig;
import com.caring.sass.nursing.dto.appointment.AppointConfigSaveDTO;
import com.caring.sass.nursing.dto.appointment.AppointConfigUpdateDTO;
import com.caring.sass.nursing.dto.appointment.AppointConfigPageDTO;
import com.caring.sass.nursing.service.appointment.AppointConfigService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.caring.sass.base.controller.SuperController;
import com.caring.sass.base.R;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.user.entity.Doctor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 记录医生的周预约配置
 * </p>
 *
 * @author leizhi
 * @date 2021-01-27
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/appointConfig")
@Api(value = "AppointConfig", tags = "记录医生的周预约配置")
//@PreAuth(replace = "appointConfig:")
public class AppointConfigController extends SuperController<AppointConfigService, Long, AppointConfig, AppointConfigPageDTO, AppointConfigSaveDTO, AppointConfigUpdateDTO> {


    @Autowired
    DoctorApi doctorApi;

    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<AppointConfig> appointConfigList = list.stream().map((map) -> {
            AppointConfig appointConfig = AppointConfig.builder().build();
            //TODO 请在这里完成转换
            return appointConfig;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(appointConfigList));
    }

    @ApiOperation("查询医生周预约配置")
    @GetMapping("doctorId/{doctorId}")
    public R<AppointConfig> getByDoctorId(@PathVariable Long doctorId) {
        LbqWrapper<AppointConfig> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(AppointConfig::getDoctorId, doctorId);
        AppointConfig config = baseService.getOne(lbqWrapper);
        if (Objects.isNull(config)) {
            Long orgId = null;
            R<Doctor> doctorApiOne = doctorApi.get(doctorId);
            if (doctorApiOne.getIsSuccess()) {
                Doctor doctor = doctorApiOne.getData();
                orgId = doctor.getOrganId();
            }
            config = AppointConfig.builder()
                    .doctorId(doctorId)
                    .numOfMondayMorning(0)
                    .numOfMondayAfternoon(0)
                    .numOfTuesdayMorning(0)
                    .numOfTuesdayAfternoon(0)
                    .numOfWednesdayMorning(0)
                    .numOfWednesdayAfternoon(0)
                    .numOfThursdayMorning(0)
                    .numOfThursdayAfternoon(0)
                    .numOfFridayMorning(0)
                    .numOfFridayAfternoon(0)
                    .numOfSaturdayMorning(0)
                    .numOfSaturdayAfternoon(0)
                    .numOfSundayMorning(0)
                    .numOfSundayAfternoon(0)
                    .organizationId(orgId)
                    .build();
            baseService.save(config);
        }
        return R.success(config);
    }


}
