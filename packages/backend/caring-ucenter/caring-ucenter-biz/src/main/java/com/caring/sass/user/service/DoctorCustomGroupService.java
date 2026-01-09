package com.caring.sass.user.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.dto.DoctorCustomGroupPageDTO;
import com.caring.sass.user.dto.DoctorCustomGroupPatientPageDTO;
import com.caring.sass.user.dto.DoctorCustomGroupSaveDTO;
import com.caring.sass.user.dto.DoctorCustomGroupUpdateDTO;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.DoctorCustomGroup;
import com.caring.sass.user.entity.Patient;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 医生的自定义小组
 * </p>
 *
 * @author yangshuai
 * @date 2022-08-11
 */
public interface DoctorCustomGroupService extends SuperService<DoctorCustomGroup> {

    /**
     * 新增小组信息
     * @param saveDTO
     * @return
     */
    DoctorCustomGroup saveDoctorGroupAndPatient(DoctorCustomGroupSaveDTO saveDTO);

    /**
     * 修改小组信息和小组内的患者
     * @param groupUpdateDTO
     * @return
     */
    DoctorCustomGroup updateDoctorGroupPatient(DoctorCustomGroupUpdateDTO groupUpdateDTO);

    /**
     * 移除小组内的某一个患者
     * @param groupId
     * @param patientId
     * @return
     */
    boolean deleteDoctorGroupPatient(Long groupId, Long patientId);

    /**
     * 分页查询小组内的患者
     * @param iPage
     * @param model
     */
    void pageGroupPatient(IPage<Patient> iPage, DoctorCustomGroupPatientPageDTO model);


    IPage<JSONObject> pageDoctorPatient(IPage<Patient>  iPage, DoctorCustomGroupPatientPageDTO model);

    /**
     * 医助下的医生小组列表
     */
    List<Doctor> queryGroupDoctor(DoctorCustomGroupPageDTO pageParams);

    /**
     * @title 给医生列表设置医生的独立医生状态和 所在小组名称
     * @author 杨帅
     * @updateTime 2023/4/3 15:46
     * @throws
     */
    void setDoctorGroupInfo(List<Doctor> doctors);

    /**
     * 移除医生的小组中 患者
     * @param patientId
     * @param patientDoctorId
     */
    void removePatient(Long patientId, Long patientDoctorId);
}
