package com.caring.sass.user.dao;

import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.user.entity.BulkNotificationSendRecord;
import org.springframework.stereotype.Repository;

/**
 * 群发通知的 接收人 记录
 */
@Repository
public interface BulkNotificationSendRecordMapper extends SuperMapper<BulkNotificationSendRecord> {

}
