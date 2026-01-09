package com.caring.sass.msgs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.service.SuperService;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.dto.PatientSystemMessageRemarkSaveDTO;
import com.caring.sass.msgs.entity.MsgPatientSystemMessage;
import com.caring.sass.msgs.entity.PatientSystemMessageRemark;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 患者系统消息
 * </p>
 *
 * @author 杨帅
 * @date 2023-08-21
 */
public interface MsgPatientSystemMessageService extends SuperService<MsgPatientSystemMessage> {

    String getFunctionName(String functionType, Integer caseDiscussionStatus);


    String i18nPushContent(MsgPatientSystemMessage message);

    void doctorQueryMessage(IPage<MsgPatientSystemMessage> builtPage, LbqWrapper<MsgPatientSystemMessage> lbqWrapper);


    void doctorSeeMessage(Long messageId, String doctorName);


    void deleteDoctorCommentMessage(Long commentId, Long messageId);

    PatientSystemMessageRemark doctorCommentMessage(PatientSystemMessageRemarkSaveDTO saveDTO);

    void doctorMessage(List<Long> messageIds);
}
