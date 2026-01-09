package com.caring.sass.nursing.service.information;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.nursing.dto.information.CompletenessInformationPatientEditDto;
import com.caring.sass.nursing.entity.information.CompletenessInformation;
import com.caring.sass.nursing.entity.information.PatientInformationField;
import com.caring.sass.nursing.qo.CompletenessInformationQo;
import com.caring.sass.nursing.qo.IntervalQo;
import com.caring.sass.nursing.qo.ReminderQo;
import com.caring.sass.nursing.vo.NotificaVo;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 业务接口
 * 患者信息完整度概览表
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
public interface CompletenessInformationService extends SuperService<CompletenessInformation> {

    /**
     * 统计专员下 信息完整度 不到100的人数
     *
     * @param nursingId
     * @return
     */
    Integer selectCount(Long nursingId);

    /**
     * 信息完整度列表
     *
     * @return
     */
    IPage<CompletenessInformation> selectList(PageParams<CompletenessInformationQo> params);

    /**
     * 发送患者信息完整度通知
     *
     * @param reminder 一个或者多个患者
     */
    R<String> sendMessageReminder(ReminderQo reminder);

    /**
     * 单条发送
     *
     * @param patientId
     * @return
     */
    R<String> sendOne(Long patientId);

    /**
     * 通过区间Id查询出需要发送通知消息的患者
     *
     * @param Interval 区间
     * @return
     */
    R<NotificaVo> notification(IntervalQo Interval);

    /**
     * 删除患者的信息完整度
     *
     * @param patientId
     */
    void removeByPatientId(long patientId);

    /**
     * 查询患者信息不完善的字段
     *
     * @param patientId   患者ID
     * @param tenantCode 租户tenantCode
     * @return
     */
    CompletenessInformation findIncompleteInformation(Long patientId, String tenantCode);


    /**
     * 查询患者的信息完整度字段。
     * 并找到 表单中的字段。
     * @param patientId
     * @return
     */
    CompletenessInformationPatientEditDto findCompletenessFormField(Long patientId);

    /**
     * 患者编辑信息完整度 字段
     * @param editDto
     */
    void updateIncompleteInformationField(CompletenessInformationPatientEditDto editDto);


    void calculateCompletion(Set<Long> informationIds);

}
