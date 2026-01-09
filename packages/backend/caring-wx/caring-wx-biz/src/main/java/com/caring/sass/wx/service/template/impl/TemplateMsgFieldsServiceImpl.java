package com.caring.sass.wx.service.template.impl;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.wx.dao.template.TemplateMsgFieldsMapper;
import com.caring.sass.wx.dto.template.TemplateMsgFieldsSaveDTO;
import com.caring.sass.wx.dto.template.TemplateMsgFieldsUpdateDTO;
import com.caring.sass.wx.entity.template.TemplateMsgFields;
import com.caring.sass.wx.service.template.TemplateMsgFieldsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 业务实现类
 * 模板消息 属性表通过 templateId 和 TemplateMessage 表关联。
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class TemplateMsgFieldsServiceImpl extends SuperServiceImpl<TemplateMsgFieldsMapper, TemplateMsgFields> implements TemplateMsgFieldsService {



    /**
     * @Author yangShuai
     * @Description 通过  TemplateMessage Id 查询 TemplateMessage 的属性设置
     * @Date 2020/9/17 10:32
     *
     * @return java.util.List<com.caring.sass.wx.entity.template.TemplateMsgFields>
     */
    @Override
    public List<TemplateMsgFields> findByMsgTemplateId(Long templateId) {

        LbqWrapper<TemplateMsgFields> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(TemplateMsgFields::getTemplateId, templateId);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * @Author yangShuai
     * @Description 通过  TemplateMessage Id 删除 TemplateMessage 的属性设置
     * @Date 2020/9/17 10:32
     *
     * @return java.util.List<com.caring.sass.wx.entity.template.TemplateMsgFields>
     */
    @Override
    public void deleteByTemplateId(Long templateId) {

        LbqWrapper<TemplateMsgFields> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(TemplateMsgFields::getTemplateId, templateId);
        int delete = baseMapper.delete(queryWrapper);
    }


    /**
     * @Author yangShuai
     * @Description 保存模板消息的属性
     * @Date 2020/9/17 11:19
     *
     * @return int
     */
    @Override
    public int save(List<TemplateMsgFieldsSaveDTO> saveDTOFields, Long templateId) {
        if (CollectionUtils.isEmpty(saveDTOFields)) {
            return 0;
        }
        List<TemplateMsgFields> templateMsgFields = new ArrayList<>();
        TemplateMsgFields msgFields;
        for (TemplateMsgFieldsSaveDTO saveDTOField : saveDTOFields) {
            msgFields = new TemplateMsgFields();
            BeanUtils.copyProperties(saveDTOField, msgFields);
            msgFields.setTemplateId(templateId);
            templateMsgFields.add(msgFields);
        }
        if (CollUtil.isEmpty(templateMsgFields)) {
            return 0;
        }
        return baseMapper.insertBatchSomeColumn(templateMsgFields);
    }

    /**
     * @Author yangShuai
     * @Description 更新模板消息的属性
     * @Date 2020/9/17 11:19
     *
     * @return int
     */
    @Override
    public int update(List<TemplateMsgFieldsUpdateDTO> updateDTOFields) {

        TemplateMsgFields msgFields;
        int update = 0;
        for (TemplateMsgFieldsUpdateDTO updateDTO : updateDTOFields) {
            msgFields = new TemplateMsgFields();
            BeanUtils.copyProperties(updateDTO, msgFields);
            if (updateDTO.getId() != null) {
                msgFields.setId(updateDTO.getId());
                baseMapper.updateById(msgFields);
            } else {
                baseMapper.insert(msgFields);
            }
            update++;
        }
        return update;
    }
}
