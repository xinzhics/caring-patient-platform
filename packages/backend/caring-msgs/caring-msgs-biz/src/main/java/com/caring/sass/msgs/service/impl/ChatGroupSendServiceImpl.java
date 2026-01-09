package com.caring.sass.msgs.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.MediaType;
import com.caring.sass.common.utils.ListUtils;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.dao.ChatGroupSendMapper;
import com.caring.sass.msgs.dao.ChatGroupSendObjectMapper;
import com.caring.sass.msgs.dto.DoctorGroupSendPageDTO;
import com.caring.sass.msgs.entity.ChatGroupSend;
import com.caring.sass.msgs.entity.ChatGroupSendObject;
import com.caring.sass.msgs.enumeration.ChatGroupAssociationType;
import com.caring.sass.msgs.service.ChatGroupSendService;
import com.caring.sass.nursing.api.FormApi;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.oauth.api.DoctorCustomGroupApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ChatGroupSendServiceImpl extends SuperServiceImpl<ChatGroupSendMapper, ChatGroupSend> implements ChatGroupSendService {

    @Autowired
    ChatGroupSendObjectMapper sendObjectMapper;

    @Autowired
    DoctorApi doctorApi;

    @Autowired
    FormApi formApi;

    @Autowired
    DoctorCustomGroupApi doctorCustomGroupApi;


    /**
     * 旧版的 群发。
     * @param model
     * @return
     */
    @Override
    public boolean save(ChatGroupSend model) {
        if (model.getSendTime() == null) {
            model.setSendTime(LocalDateTime.now());
        }
        setCmsInfo(model);
        return super.save(model);
    }

    /**
     * 当发送的文章是。分解一下文章的信息
     * @param model
     */
    private void setCmsInfo(ChatGroupSend model) {
        if (model.getType().equals(MediaType.cms) && StrUtil.isNotEmpty(model.getContent())) {
            String content = model.getContent();
            JSONObject object = JSONObject.parseObject(content);
            Object id = object.get("id");
            if (Objects.nonNull(id)) {
                model.setCmsId(Long.parseLong(id.toString()));
            }
            Object title = object.get("title");
            if (Objects.nonNull(title)) {
                model.setCmsTitle(title.toString());
            }
        }

    }

    @Override
    public IPage<ChatGroupSend> page(IPage<ChatGroupSend> page, LbqWrapper<ChatGroupSend> lbqWrapper) {
        return baseMapper.selectPage(page, lbqWrapper);
    }


    /**
     * 删除群发时 删除他的群发对象
     * @param id
     */
    @Override
    public void deleteGroupSend(Long id) {
        baseMapper.deleteById(id);
        sendObjectMapper.delete(Wraps.<ChatGroupSendObject>lbQ().eq(ChatGroupSendObject::getChatGroupSendId, id));
    }


    /**
     * 医生分页查询自己的群发消息记录
     * @param groupSendPageDTO
     * @return
     */
    @Override
    public List<ChatGroupSend> page(DoctorGroupSendPageDTO groupSendPageDTO) {
        String searchKeyName = groupSendPageDTO.getSearchKeyName();
        Long userId = groupSendPageDTO.getUserId();
        String userRole = groupSendPageDTO.getUserRole();
        Integer page = groupSendPageDTO.getPage();
        // 页码。页码不存在时，默认查询起始下标为 5
        if (page == null) {
            page = 0;
        } else {
            page = (page - 1) * 5;
        }
        if (StrUtil.isNotEmpty(searchKeyName)) {
            searchKeyName = "%"+ searchKeyName + "%";
        }
        List<ChatGroupSend> chatGroupSends = baseMapper.pageDoctorChatGroupSearch(userId, searchKeyName,  page, userRole);

        if (CollUtil.isNotEmpty(chatGroupSends)) {
            List<Long> collect = chatGroupSends.stream().map(SuperEntity::getId).collect(Collectors.toList());
            // 优先查询 符合条件的 群发对象
            List<ChatGroupSendObject> sendObjects = sendObjectMapper.selectList(Wraps.<ChatGroupSendObject>lbQ()
                    .in(ChatGroupSendObject::getChatGroupSendId, collect));
            if (CollUtil.isNotEmpty(sendObjects)) {
                Map<Long, List<ChatGroupSendObject>> listMap = sendObjects.stream().collect(Collectors.groupingBy(ChatGroupSendObject::getChatGroupSendId));
                for (ChatGroupSend groupSend : chatGroupSends) {
                    List<ChatGroupSendObject> objects = listMap.get(groupSend.getId());
                    groupSend.setSendObjects(objects);
                }
            }
        }
        return chatGroupSends;
    }

    /**
     * 定时发送等待 群发的消息
     */
    @Override
    public void chatGroupTiming() {

        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.now()).withSecond(0).withNano(0);
        List<ChatGroupSend> groupSends = baseMapper.queryChatGroupTiming(dateTime);
        if (CollUtil.isNotEmpty(groupSends)) {
            for (ChatGroupSend groupSend : groupSends) {
                String tenantCode = groupSend.getTenantCode();
                BaseContextHandler.setTenant(tenantCode);
                // 调用医生群发消息接口。直接发送，并返回发送人数。
                R<Long> groupMsg = doctorApi.doctorSendGroupMsg(JSON.parseObject(JSON.toJSONString(groupSend)));
                if (groupMsg.getIsSuccess()) {
                    Long data = groupMsg.getData();
                    groupSend.setPatientNumber(data);
                    baseMapper.updateById(groupSend);
                }
            }
        }

    }

    /**
     * 保存群发消息
     * @param model
     */
    @Override
    public void saveChatGroupSend(ChatGroupSend model) {

        List<ChatGroupSendObject> sendObjects = model.getSendObjects();
        if (model.getSendTime() == null) {
            model.setSendTime(LocalDateTime.now());
        }
        model.setReceiverIds(null);
        setCmsInfo(model);
        baseMapper.insert(model);
        if (CollUtil.isNotEmpty(sendObjects)) {
            for (ChatGroupSendObject object : sendObjects) {
                object.setChatGroupSendId(model.getId());
            }
            sendObjectMapper.insertBatchSomeColumn(sendObjects);
        }
        // 调用医生群发消息接口。直接发送，并返回发送人数。
        try {
            R<Long> groupMsg = doctorApi.doctorSendGroupMsg(JSON.parseObject(JSON.toJSONString(model)));
            if (groupMsg.getIsSuccess()) {
                Long data = groupMsg.getData();
                model.setPatientNumber(data);
                baseMapper.updateById(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
