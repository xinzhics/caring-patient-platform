package com.caring.sass.wx.entity.template;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 模板消息
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_template_msg")
@ApiModel(value = "TemplateMsg", description = "模板消息")
@AllArgsConstructor
public class TemplateMsg extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 对应微信公众平台中的模板Id
     */
    @ApiModelProperty(value = "对应微信公众平台中的模板Id")
    @Length(max = 200, message = "对应微信公众平台中的模板Id长度不能超过200")
    @TableField(value = "template_id", condition = EQUAL)
    @Excel(name = "对应微信公众平台中的模板Id")
    private String templateId;

    /**
     * 业务Id
     */
    @ApiModelProperty(value = "业务Id")
    @Length(max = 32, message = "业务Id长度不能超过32")
    @TableField(value = "business_id", condition = LIKE)
    @Excel(name = "业务Id")
    private String businessId;

    /**
     * 模板json
     */
    @ApiModelProperty(value = "模板json")
    @Length(max = 65535, message = "模板json长度不能超过65535")
    @TableField("extra_json")
    @Excel(name = "模板json")
    private String extraJson;

    /**
     * 标识
     */
    @ApiModelProperty(value = "标识")
    @Length(max = 50, message = "标识长度不能超过50")
    @TableField(value = "indefiner", condition = LIKE)
    @Excel(name = "标识")
    private String indefiner;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    @Length(max = 255, message = "模板名称长度不能超过255")
    @TableField(value = "title", condition = LIKE)
    @Excel(name = "模板名称")
    private String title;

    /**
     * 原模板消息Id
     */
    @ApiModelProperty(value = "原模板消息Id")
    @Length(max = 32, message = "原模板消息Id长度不能超过32")
    @TableField(value = "source_id", condition = LIKE)
    @Excel(name = "原模板消息Id")
    private String sourceId;


    @Builder
    public TemplateMsg(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime,
                       String templateId, String businessId, String extraJson, String indefiner,
                       String title, String sourceId) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.templateId = templateId;
        this.businessId = businessId;
        this.extraJson = extraJson;
        this.indefiner = indefiner;
        this.title = title;
        this.sourceId = sourceId;
    }

}
