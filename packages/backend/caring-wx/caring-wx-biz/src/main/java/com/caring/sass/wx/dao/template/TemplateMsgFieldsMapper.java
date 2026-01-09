package com.caring.sass.wx.dao.template;

import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.wx.entity.template.TemplateMsgFields;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 模板消息 属性表通过 templateId 和 TemplateMessage 表关联。
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Repository
public interface TemplateMsgFieldsMapper extends SuperMapper<TemplateMsgFields> {

}
