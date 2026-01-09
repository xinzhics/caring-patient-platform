package com.caring.sass.wx.service.template;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.wx.dto.enums.TemplateMessageIndefiner;
import com.caring.sass.wx.dto.template.TemplateMsgDto;
import com.caring.sass.wx.dto.template.TemplateMsgSaveDTO;
import com.caring.sass.wx.dto.template.TemplateMsgUpdateDTO;
import com.caring.sass.wx.entity.template.TemplateMsg;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 模板消息
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
public interface TemplateMsgService extends SuperService<TemplateMsg> {

    String TEMPLATE_MSG_INDERFINDER_KEY = "TEMPLATE_MSG_INDERFINDER_KEY";

    /**
     * @Author yangShuai
     * @Description 查询业务的模板消息
     * @Date 2020/9/16 18:19
     *
     * @param businessId 业务Id
     * @param templateId 微信模板消息Id
     * @param indefiner  业务类型
     * @return java.util.List<com.caring.sass.wx.dto.template.TemplateMsgDto>
     */
    List<TemplateMsgDto> findBusinessTemplateMessage(String businessId, Long templateId, String indefiner);


    /**
     * @Author yangShuai
     * @Description 通过模板消息Id 设置 微信公众号模板Id
     * @Date 2020/9/17 10:45
     *
     * @return boolean
     */
    boolean updateWeiXinTemplateId(Long templateMsgId, String weiXinTemplateId);

    /**
     * @Author yangShuai
     * @Description 保存模板消息 和 属性
     * @Date 2020/9/17 11:10
     *
     * @return boolean
     */
    TemplateMsg saveTemplateMsg(TemplateMsgSaveDTO templateMsgSaveDTO);

    /**
     * @Author yangShuai
     * @Description 修改模板消息 和 属性
     * @Date 2020/9/17 11:10
     *
     * @return boolean
     */
    boolean updateTemplateMsg(TemplateMsgUpdateDTO templateMsgUpdateDTO);


    TemplateMsg updateTemplateMsgResultMsg(TemplateMsgUpdateDTO templateMsgUpdateDTO);



    /**
     * @Author yangShuai
     * @Description 获取一个模板消息的信息和 属性
     * @Date 2020/9/17 11:25
     *
     * @return com.caring.sass.wx.dto.template.TemplateMsgDto
     */
    TemplateMsgDto getOneById(Long id);



    boolean initTemplateMsg();


    Map<Long, Long> copyTemplateMsgAndFields(String fromTenantCode, String toTenantCode);

    TemplateMsgDto initDoctorCommentReminderMsg();

    /**
     * 初始化微信公众号 类目模板中的 服务工单模板
     *
     * 需要去微信公众号添加一下这个模板。并将模板保存到本地的模板库。
     * 增加线程锁。
     * 再次查询本地是否已经有了 模板库
     * 本地没有模板库， 查询微信公众号模板列表
     * 添加模板。
     * 保存模板到本地
     * @return
     */
    TemplateMsgDto initCommonCategoryServiceWorkOrderMsg();


    TemplateMsgDto getTemplateMsgDtoByIndefiner(String indefiner);

    /**
     * 一小时检测一次
     * 查询服务工单模版是否存在微信公众号
     */
    Boolean checkTemplateExist(TemplateMsgDto templateMsg);

}
