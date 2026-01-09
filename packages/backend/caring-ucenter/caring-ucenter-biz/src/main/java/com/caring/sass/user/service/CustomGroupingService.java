package com.caring.sass.user.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.user.dto.NursingCustomGroupPatientPageDTO;
import com.caring.sass.user.dto.NursingCustomGroupUpdateDTO;
import com.caring.sass.user.entity.CustomGrouping;
import com.caring.sass.user.entity.Patient;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName CustomGroupingService
 * @Description
 * @Author yangShuai
 * @Date 2021/8/27 11:55
 * @Version 1.0
 */
public interface CustomGroupingService extends SuperService<CustomGrouping> {

    List<CustomGrouping> findPatientInGroupList(Long patientId, Long userId);

    /**
     * 删除患者时，将患者从小组 关系中删除
     * @param patientId
     */
    void removeByPatientId(Long patientId);

    /**
     * 查询医助所有的小组。并统计小组内未取关的患者的数量
     * @param nursingId
     * @param patientStatus
     * @return
     */
    List<CustomGrouping> queryAllAndCountPatientNumber(Long nursingId, List<Integer> patientStatus);

    /**
     * 新医助端 新增自定义小组的方法
     *
     * @param groupUpdateDTO
     * @return
     */
    CustomGrouping createGroupPatient(NursingCustomGroupUpdateDTO groupUpdateDTO);

    /**
     * 新医助端 修改自定义小组的方法
     *
     * @param customGroupId
     * @param groupUpdateDTO
     * @return
     */
    CustomGrouping updateGroupPatient(Long customGroupId, NursingCustomGroupUpdateDTO groupUpdateDTO);

    List<Patient> findAllPatientBaseInfo(Long customGroupingId);


    IPage<Patient> findPatientBaseInfo(IPage buildPage, Long customGroupingId);


    void deletePatient(Long customGroupingId, Long patientId);


    /**
     * 查询医助患者的列表并按首字母返回
     * @param iPage
     * @param model
     * @return
     */
    IPage<JSONObject> pageNursingPatient(IPage<Patient> iPage, NursingCustomGroupPatientPageDTO model);
}
