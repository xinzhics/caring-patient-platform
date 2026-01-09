package com.caring.sass.wx.service.template;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.dto.template.TemplateMsgFieldsSaveDTO;
import com.caring.sass.wx.dto.template.TemplateMsgFieldsUpdateDTO;
import com.caring.sass.wx.entity.template.TemplateMsgFields;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 模板消息 属性表通过 templateId 和 TemplateMessage 表关联。
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface TemplateMsgFieldsService extends SuperService<TemplateMsgFields> {


    /**
     * @Author yangShuai
     * @Description 使用 TemplateId 获取模板属性
     * @Date 2020/9/17 9:59
     *
     * @return java.util.List<com.caring.sass.wx.entity.template.TemplateMsgFields>
     */
    List<TemplateMsgFields> findByMsgTemplateId(Long templateId);

    /**
     * @Author yangShuai
     * @Description 删除模板消息 属性
     * @Date 2020/9/17 11:15
     *
     * @return void
     */
    void deleteByTemplateId(Long id);

    /**
     * @Author yangShuai
     * @Description 保存模板消息的属性
     * @Date 2020/9/17 11:14
     *
     * @return int
     */
    int save(List<TemplateMsgFieldsSaveDTO> saveDTOFields, Long templateId);

    /**
     * @Author yangShuai
     * @Description 更新模板消息的属性
     * @Date 2020/9/17 11:15
     *
     * @return int
     */
    int update(List<TemplateMsgFieldsUpdateDTO> updateDTOFields);
}
