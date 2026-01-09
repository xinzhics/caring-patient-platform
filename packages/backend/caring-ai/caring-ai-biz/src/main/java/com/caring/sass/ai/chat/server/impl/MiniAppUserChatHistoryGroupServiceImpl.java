package com.caring.sass.ai.chat.server.impl;

import cn.hutool.core.collection.CollUtil;
import com.caring.sass.ai.chat.dao.MiniAppUserChatHistoryGroupMapper;
import com.caring.sass.ai.chat.dao.MiniAppUserChatMapper;
import com.caring.sass.ai.chat.server.MiniAppUserChatHistoryGroupService;
import com.caring.sass.ai.entity.chat.MiniAppUserChat;
import com.caring.sass.ai.entity.chat.MiniAppUserChatHistoryGroup;
import com.caring.sass.ai.entity.dto.DateGroupEnum;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;

/**
 * <p>
 * 业务实现类
 * 敏智用户历史对话
 * </p>
 *
 * @author 杨帅
 * @date 2024-03-28
 */
@Slf4j
@Service

public class MiniAppUserChatHistoryGroupServiceImpl extends SuperServiceImpl<MiniAppUserChatHistoryGroupMapper, MiniAppUserChatHistoryGroup> implements MiniAppUserChatHistoryGroupService {

    @Autowired
    MiniAppUserChatMapper miniAppUserChatMapper;

    @Override
    public boolean removeById(Serializable id) {

        miniAppUserChatMapper.delete(Wraps.<MiniAppUserChat>lbQ().eq(MiniAppUserChat::getHistoryId, id));
        baseMapper.deleteById(id);
        return true;
    }


    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {

        for (Serializable serializable : idList) {
            removeById(serializable);
        }
        return true;
    }

    @Override
    public Map<String, List<MiniAppUserChatHistoryGroup>> customPage(List<MiniAppUserChatHistoryGroup> records) {

        if (CollUtil.isEmpty(records)) {
            return null;
        }
        Map<String, List<MiniAppUserChatHistoryGroup>> map = new HashMap<>();

        for (MiniAppUserChatHistoryGroup record : records) {
            String ascription = DateGroupEnum.calculateDateAscription(record.getCreateTime());
            List<MiniAppUserChatHistoryGroup> groups = map.get(ascription);
            if (CollUtil.isEmpty(groups)) {
                groups = new ArrayList<>();
                groups.add(record);
                map.put(ascription, groups);
            } else {
                groups.add(record);
            }
        }
        return map;
    }
}
