package com.caring.sass.user.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.common.utils.StringUtils;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.user.dao.CommonMsgMapper;
import com.caring.sass.user.dao.CommonMsgTemplateContentMapper;
import com.caring.sass.user.dao.CommonMsgTemplateTypeMapper;
import com.caring.sass.user.dao.CommonMsgTypeMapper;
import com.caring.sass.user.entity.CommonMsg;
import com.caring.sass.user.entity.CommonMsgTemplateContent;
import com.caring.sass.user.entity.CommonMsgTemplateType;
import com.caring.sass.user.entity.CommonMsgType;
import com.caring.sass.user.service.CommonMsgService;
import com.caring.sass.user.service.CommonMsgTemplateTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.sql.Wrapper;
import java.util.*;
import java.util.stream.Collectors;


/**
     * @Author yangShuai
 * @Description 常用语
 * @Date 2020/10/13 10:52
 *
 * @return
 */
@Slf4j
@Service
public class CommonMsgServiceImpl extends SuperServiceImpl<CommonMsgMapper, CommonMsg> implements CommonMsgService {

    @Autowired
    CommonMsgTemplateContentMapper commonMsgTemplateContentMapper;

    @Autowired
    CommonMsgTemplateTypeMapper commonMsgTemplateTypeMapper;

    @Autowired
    CommonMsgTypeMapper commonMsgTypeMapper;

    /**
     * @Author yangShuai
     * @Description 清空常用语
     * @Date 2020/10/16 17:20
     *
     * @return boolean
     */
    @Override
    public boolean cleanAll(Long accountId, String userType) {
        LbqWrapper<CommonMsg> lbqWrapper = new LbqWrapper<>();
        lbqWrapper.eq(CommonMsg::getAccountId, accountId);
        if (StringUtils.isEmpty(userType)) {
            lbqWrapper.eq(CommonMsg::getUserType, UserType.UCENTER_NURSING_STAFF);
        } else {
            lbqWrapper.eq(CommonMsg::getUserType, userType);
        }
        baseMapper.delete(lbqWrapper);
        return true;
    }


    @Override
    public boolean save(CommonMsg model) {
        if (StringUtils.isEmpty(model.getUserType())) {
            model.setUserType(UserType.UCENTER_NURSING_STAFF);
        }
        return super.save(model);
    }

    @Override
    public boolean updateById(CommonMsg model) {
        if (StringUtils.isEmpty(model.getUserType())) {
            model.setUserType(UserType.UCENTER_NURSING_STAFF);
        }
        return super.updateById(model);
    }


    /**
     * 查询我的分类和 系统后台的分类
     * @param userId
     * @param userType
     * @return
     */
    @Override
    public List<CommonMsgType> queryAllType(Long userId, String userType) {

        List<CommonMsgType> msgTypeList = queryMyType(userId, userType);
        if (CollUtil.isEmpty(msgTypeList)) {
            msgTypeList = new ArrayList<>();
        }
        List<CommonMsgTemplateType> templateTypes = commonMsgTemplateTypeMapper.selectList(Wraps.<CommonMsgTemplateType>lbQ()
                .eq(CommonMsgTemplateType::getUserType, userType)
                .orderByDesc(CommonMsgTemplateType::getTypeSort)
                .orderByAsc(SuperEntity::getCreateTime));
        if (CollUtil.isNotEmpty(templateTypes)) {
            for (CommonMsgTemplateType type : templateTypes) {
                CommonMsgType build = CommonMsgType.builder().title(type.getTypeName()).formTemplate(1).build();
                build.setId(type.getId());
                msgTypeList.add(build);
            }
        }
        return msgTypeList;
    }

    /**
     * 查询用户自己的常用语分类
     * @param userId
     * @param userType
     * @return
     */
    @Override
    public List<CommonMsgType> queryMyType(Long userId, String userType) {

        LbqWrapper<CommonMsgType> wrapper = Wraps.<CommonMsgType>lbQ();
        wrapper.eq(CommonMsgType::getUserId, userId);
        wrapper.eq(CommonMsgType::getUserType, userType);
        wrapper.orderByDesc(SuperEntity::getCreateTime);
        return commonMsgTypeMapper.selectList(wrapper);
    }

    /**
     * 导入模版到自己的常用语
     * @param userId
     * @param userType
     * @param templateContentIds
     */
    @Override
    public void importCommonMsgTemplate(Long userId, String userType, List<Long> templateContentIds) {

        List<CommonMsgTemplateContent> commonMsgTemplateContentList = commonMsgTemplateContentMapper.selectBatchIds(templateContentIds);
        Set<Long> templateIds = commonMsgTemplateContentList.stream().map(CommonMsgTemplateContent::getTemplateTypeId).collect(Collectors.toSet());
        Map<Long, String> templateIdNameMap = new HashMap<>();
        if (CollUtil.isNotEmpty(templateIds)) {
            List<CommonMsgTemplateType> templateTypes = commonMsgTemplateTypeMapper.selectList(Wraps.<CommonMsgTemplateType>lbQ()
                    .in(SuperEntity::getId, templateIds));
            if (CollUtil.isNotEmpty(templateTypes)) {
                templateIdNameMap = templateTypes.stream().collect(Collectors.toMap(SuperEntity::getId, CommonMsgTemplateType::getTypeName));
            }
        }
        List<CommonMsg> commonMsgs = new ArrayList<>();
        CommonMsg commonMsg;
        Map<String, Long> commonMsgType = new HashMap<>();
        for (CommonMsgTemplateContent templateContent : commonMsgTemplateContentList) {
            commonMsg = new CommonMsg();
            commonMsg.setAccountId(userId);
            commonMsg.setUserType(userType);

            commonMsg.setTitle(templateContent.getTemplateTitle());
            commonMsg.setContent(templateContent.getTemplateContent());
            commonMsg.setSourceTemplateId(templateContent.getId());

            Long templateTypeId = templateContent.getTemplateTypeId();
            if (Objects.nonNull(templateTypeId)) {
                String name = templateIdNameMap.get(templateTypeId);
                if (StrUtil.isNotEmpty(name)) {
                    Long typeId = commonMsgType.get(name);
                    if (Objects.isNull(typeId)) {
                        CommonMsgType type = commonMsgTypeMapper.selectOne(Wraps.<CommonMsgType>lbQ()
                                .eq(CommonMsgType::getTitle, name)
                                .eq(CommonMsgType::getUserId, userId)
                                .eq(CommonMsgType::getUserType, userType)
                                .last(" limit 0,1 ")
                        );
                        if (Objects.isNull(type)) {
                            type = new CommonMsgType();
                            type.setTitle(name);
                            type.setUserType(userType);
                            type.setUserId(userId);
                            commonMsgTypeMapper.insert(type);
                        }
                        typeId = type.getId();
                        commonMsgType.put(name, typeId);
                    }
                    if (Objects.nonNull(typeId)) {
                        commonMsg.setTypeId(typeId);
                    }
                }
            }
            commonMsgs.add(commonMsg);
        }
        if (CollUtil.isNotEmpty(commonMsgs)) {
            baseMapper.insertBatchSomeColumn(commonMsgs);
        }
    }

    /**
     * 使用分类名称查询用户的分类
     * @param typeName
     * @param accountId
     * @param userType
     * @return
     */
    public CommonMsgType getType(String typeName, Long accountId, String userType) {
        CommonMsgType type = commonMsgTypeMapper.selectOne(Wraps.<CommonMsgType>lbQ()
                .eq(CommonMsgType::getTitle, typeName)
                .eq(CommonMsgType::getUserId, accountId)
                .eq(CommonMsgType::getUserType, userType)
                .last(" limit 0,1 "));
        if (Objects.isNull(type)) {
            type = new CommonMsgType();
            type.setUserId(accountId);
            type.setUserType(userType);
            type.setTitle(typeName);
            commonMsgTypeMapper.insert(type);
        }
        return type;
    }

    /**
     * 新增或者更新常用语和分类
     * @param commonMsg
     */
    @Override
    public void saveOrUpdateCommonMsgAndType(CommonMsg commonMsg) {

        Integer typeForm = commonMsg.getTypeForm();
        Long typeId = commonMsg.getTypeId();
        String typeName = commonMsg.getTypeName();
        Long id = commonMsg.getId();
        if (Objects.nonNull(typeForm)) {
            if (typeForm.equals(0)) {
                commonMsg.setTypeId(typeId);
            } else if (typeForm.equals(1)) {
                CommonMsgTemplateType templateType = commonMsgTemplateTypeMapper.selectById(typeId);
                if (Objects.nonNull(templateType) && StrUtil.isEmpty(typeName)) {
                    typeName = templateType.getTypeName();
                }
                CommonMsgType type = getType(typeName, commonMsg.getAccountId(), commonMsg.getUserType());
                typeId = type.getId();
                commonMsg.setTypeId(typeId);
            } else if (typeForm.equals(2)) {
                CommonMsgType type = getType(typeName, commonMsg.getAccountId(), commonMsg.getUserType());
                typeId = type.getId();
                commonMsg.setTypeId(typeId);
            }
        }
        if (Objects.isNull(id)) {
            baseMapper.insert(commonMsg);
        } else {
            baseMapper.updateById(commonMsg);
        }

    }
}
