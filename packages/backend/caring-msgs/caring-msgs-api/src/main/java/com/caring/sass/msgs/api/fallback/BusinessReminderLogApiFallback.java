package com.caring.sass.msgs.api.fallback;

import com.caring.sass.base.R;
import com.caring.sass.msgs.api.BusinessReminderLogControllerApi;
import com.caring.sass.sms.dto.BusinessReminderLogSaveDTO;
import org.springframework.stereotype.Component;

/**
 * 熔断
 *
 * @author caring
 * @date 2019/07/25
 */
@Component
public class BusinessReminderLogApiFallback implements BusinessReminderLogControllerApi {


    @Override
    public R<Boolean> sendNoticeSms(BusinessReminderLogSaveDTO businessReminderLogSaveDTO) {
        return R.timeout();
    }
}
