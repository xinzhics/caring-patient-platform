package com.caring.sass.wx.entity.template;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 模板消息 属性表通过 templateId 和 TemplateMessage 表关联。
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
@TableName("t_wx_template_msg_fields")
@ApiModel(value = "TemplateMsgFields", description = "模板消息 属性表通过 templateId 和 TemplateMessage 表关联。")
@AllArgsConstructor
public class TemplateMsgFields extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 对应微信公众平台中的模板Id
     */
    @ApiModelProperty(value = "TemplateMsgId")
    @TableField(value = "template_id", condition = LIKE)
    @Excel(name = "TemplateMsgId")
    private Long templateId;

    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    @Length(max = 100, message = "标签长度不能超过100")
    @TableField(value = "label", condition = LIKE)
    @Excel(name = "标签")
    private String label;

    /**
     * 属性
     */
    @ApiModelProperty(value = "属性")
    @Length(max = 200, message = "属性长度不能超过200")
    @TableField(value = "attr", condition = LIKE)
    @Excel(name = "属性")
    private String attr;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    @TableField("type")
    @Excel(name = "类型")
    private Integer type;

    /**
     * 属性值
     */
    @ApiModelProperty(value = "属性值")
    @Length(max = 65535, message = "属性值长度不能超过65535")
    @TableField("value")
    @Excel(name = "属性值")
    private String value;

    /**
     * 颜色值
     */
    @ApiModelProperty(value = "颜色值")
    @Length(max = 20, message = "颜色值长度不能超过20")
    @TableField(value = "color", condition = LIKE)
    @Excel(name = "颜色值")
    private String color;

    /**
     * 业务Id
     */
    @ApiModelProperty(value = "业务Id")
    @Length(max = 32, message = "业务Id长度不能超过32")
    @TableField(value = "business_id", condition = LIKE)
    @Excel(name = "业务Id")
    private String businessId;


    @Builder
    public TemplateMsgFields(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    Long templateId, String label, String attr, Integer type, String value,
                    String color, String businessId) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.templateId = templateId;
        this.label = label;
        this.attr = attr;
        this.type = type;
        this.value = value;
        this.color = color;
        this.businessId = businessId;
    }

}
