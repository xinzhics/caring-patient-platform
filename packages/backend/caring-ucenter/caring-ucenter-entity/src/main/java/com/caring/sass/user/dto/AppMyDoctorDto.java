package com.caring.sass.user.dto;

import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.Group;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName AppMyDoctorDto
 * @Description
 * @Author yangShuai
 * @Date 2021/9/15 14:25
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "AppMyDoctorDto", description = "app我的医生页面使用")
public class AppMyDoctorDto {

    private List<Group> groupList;

    private List<Doctor> doctorList;

}
