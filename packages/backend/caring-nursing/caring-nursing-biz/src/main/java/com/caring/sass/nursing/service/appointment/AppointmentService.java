package com.caring.sass.nursing.service.appointment;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.appointment.AppointDayCountVo;
import com.caring.sass.nursing.dto.appointment.AppointModel;
import com.caring.sass.nursing.dto.appointment.AppointWeekTotalVo;
import com.caring.sass.nursing.dto.appointment.AppointmentAuditDTO;
import com.caring.sass.nursing.entity.appointment.AppointConfig;
import com.caring.sass.nursing.entity.appointment.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 患者预约表
 * </p>
 *
 * @author leizhi
 * @date 2021-01-27
 */
public interface AppointmentService extends SuperService<Appointment> {

    @Deprecated
    AppointWeekTotalVo statisticsWeek(Integer week, Long doctorId, Long organId, Long groupId );

    /**
     * @Author yangShuai
     * @Description 统计上下午的预约情况
     * @Date 2021/1/28 14:18
     *
     * @param day
     * @param doctorId
     * @return com.caring.sass.nursing.dto.appointment.AppointDayCountVo
     */
    @Deprecated
    AppointDayCountVo statisticsDay(String day, Long doctorId,  Long organId, Long groupId);

    List<AppointModel> noAppointConfig(Integer week);

    /**
     * 统计日期内 医生的 使用号源情况
     * @param doctorId
     * @param week
     * @return
     */
    List<AppointModel> statistics7Day(Long doctorId, List<Date> week);

    /**
     * 【精准预约】 患者查看7天医生号源
     * @param serviceOne
     * @param appointModels
     * @param dateList
     * @param userId
     * @param doctorId
     * @return
     */
    List<AppointModel> getSurplusAppoint(AppointConfig serviceOne, List<AppointModel> appointModels, List<Date> dateList, Long userId, Long doctorId);

    /**
     * @title 【精准预约】 医助或医生 查看日统计数据
     * @author 杨帅
     * @updateTime 2023/3/29 9:24
     * @throws
     */
    AppointDayCountVo statisticsDay(String day, List<Long> doctorIds);

    /**
     * 【精准预约】 医助或医生 查看周统计数据
     * @param week
     * @param doctorIds
     * @return
     */
    AppointWeekTotalVo statisticsWeek(Integer week, List<Long> doctorIds);

    /**
     * @title 患者删除预约
     * @author 杨帅 
     * @updateTime 2023/3/29 13:12 
     * @throws 
     */
    void patientDeleteAppoint(Long appointmentId);

    /**
     * @title 患者取消预约
     * @author 杨帅 
     * @updateTime 2023/3/29 13:13 
     * @throws 
     */
    void patientCancelAppoint(Long appointmentId);

    /**
     * @title 清除医生的 已过期预约
     * @author 杨帅 
     * @updateTime 2023/3/29 13:15 
     * @throws 
     */
    void clearAppoint(List<Long> doctorIds);

    /**
     * @Author yangShuai
     * @Description 获取总号源 多少
     * @Date 2021/1/28 14:44
     *
     * @param week  从 1 开始。 周日为7
     * @param config
     * @return java.lang.Integer
     */
    Integer getTotalNum(Integer week, Integer time, AppointConfig config);

    /**
     * @title 医生审核预约 通过或者 拒绝
     * @author 杨帅 
     * @updateTime 2023/3/29 14:47
     * @throws 
     */
    void doctorApprove(AppointmentAuditDTO appointmentAuditDTO);

    /**
     * @title  医助审核预约 通过或者 拒绝
     * @author 杨帅
     * @updateTime 2023/3/29 14:47
     * @throws
     */
    void nursingApprove(AppointmentAuditDTO appointmentAuditDTO);

    /**
     * @title 医助或者医生直接通过预约审批。
     * @author 杨帅
     * @updateTime 2023/3/29 16:31
     * @throws
     */
    void directApproval(Long appointmentId, Boolean nursing);

    /**
     * @title 3.11 定时任务 【预约审核过期】
     * 每分钟执行一次。
     * @author 杨帅
     * @updateTime 2023/3/30 17:40
     * @throws
     * @param localDateTime
     */
    void appointmentReviewExpired(LocalDateTime localDateTime);


    /**
     * @title 3.12 定时任务【就诊过期】
     * 每晚8点执行
     * 查询当天 未就诊的 待就诊记录
     * 更新 已过期的就诊记录 预约状态为 4就诊过期，排序状态为： 已过期 5
     * @author 杨帅 
     * @updateTime 2023/3/30 17:41 
     * @throws
     * @param localDate
     */
    void appointmentVisitExpired(LocalDate localDate);


    /**
     * 查询项目中还没有审核的预约。
     * 给相关的医生发送通知
     */
    void doctorAppointmentReviewReminder();


}
