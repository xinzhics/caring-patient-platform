package com.caring.sass.user.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.dto.DoctorNoReadGroupDoctorMsgDto;
import com.caring.sass.user.entity.Doctor;
import com.caring.sass.user.entity.DoctorGroup;
import com.caring.sass.user.entity.DoctorNoReadGroupDoctorMsg;

import java.util.List;

/**
 * @ClassName DoctorGroupService
 * @Description
 * @Author yangShuai
 * @Date 2021/9/14 16:50
 * @Version 1.0
 */
public interface DoctorGroupService extends SuperService<DoctorGroup> {
    /**
     * 获取医生所在小组内的 所有的医生
     * @param doctorId
     * @return
     */
    List<Doctor> getGroupDoctorListReadMyMessage(Long doctorId);

    /**
     * 查询 哪些医生愿意接收看 我和患者的消息
     * 不包括 doctorId
     * @param doctorId
     * @return
     */
    List<Doctor> getReadMyMsgDoctor(Long doctorId);

    /**
     * 设置医生查看某医生患者和不查看某医生患者的消息状态
     * @param doctorList
     * @param doctorId
     * @return
     */
    List<DoctorNoReadGroupDoctorMsgDto> setReadStauts(List<Doctor> doctorList, Long doctorId);


    Boolean saveDoctorNoReadGroupDoctor(DoctorNoReadGroupDoctorMsg groupDoctorMsg);


    List<DoctorGroup> getDoctorGroupOtherDoctor(Long doctorId);


    void removeByDoctorId(Long doctorId);

    void removeNotReadMessage(Long doctorId);


    /**
     * 查询医生所在的小组内 所有医生的ID
     * @param doctorId
     * @return
     */
    List<Long> findGroupDoctorIdByDoctorId(Long doctorId);

}
