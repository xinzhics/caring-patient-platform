package com.caring.sass.user.dao;

import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.user.entity.BulkNotificationScanUser;
import org.springframework.stereotype.Repository;

/**
 * 扫了群发通知二维码的微信用户
 */
@Repository
public interface BulkNotificationScanUserMapper extends SuperMapper<BulkNotificationScanUser> {

}
