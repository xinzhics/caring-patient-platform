package com.caring.sass.msgs.api.fallback;

import com.caring.sass.base.R;
import com.caring.sass.msgs.api.GroupImApi;
import com.caring.sass.msgs.dto.ImGroupDto;
import com.caring.sass.msgs.entity.ConsultationChat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 熔断
 *
 * @author caring
 * @date 2019/07/25
 */
@Component
public class GroupImFallback implements GroupImApi {


    @Override
    public R<String> createImGroup(ImGroupDto imGroupDto) {
        return R.timeout();
    }

    @Override
    public R<Boolean> deleteImGroup(String groupId) {
        return R.timeout();
    }

    @Override
    public R<Boolean> deleteImGroupMessage(Long groupId) {
        return R.timeout();
    }

    @Override
    public R<Boolean> addUserToGroup(String groupId, String userImAccount, Boolean newOwner) {
        return R.timeout();
    }

    @Override
    public R<Boolean> removeUserFromGroup(String groupId, String userImAccount) {
        return R.timeout();
    }

    @Override
    public R<ConsultationChat> sendConsultationChat(ConsultationChat consultationChat) {
        return R.timeout();
    }

    @Override
    public R<Map<Long, Integer>> countNoReadMessage(Long receiverId, String userRole, List<Long> groupIds) {
        return R.timeout();
    }

}
