package com.caring.sass.nursing.entity.tag;

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
 * 标签属性
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_tag_attr")
@ApiModel(value = "Attr", description = "标签属性")
@AllArgsConstructor
public class Attr extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static final String BASE_INFO = "BASE_INFO";
    public static final String HEALTH_RECORD = "HEALTH_RECORD";
    public static final String MONITORING_PLAN = "monitoringPlan";
    public static final String DRUG = "drug";

    @ApiModelProperty(value = "标签属性的来源(BASE_INFO 基本信息， HEALTH_RECORD 疾病信息，monitoringPlan 监测计划，drug 药品)")
    @TableField(value = "attr_source", condition = EQUAL)
    @Excel(name = "标签属性的来源")
    private String attrSource;

    @ApiModelProperty(value = "监测计划ID")
    @TableField(value = "plan_id", condition = EQUAL)
    @Excel(name = "监测计划ID")
    private Long planId;

    /**
     * 属性ID
     */
    @ApiModelProperty(value = "表单属性ID或者药品的id")
    @Length(max = 32, message = "表单属性ID长度不能超过32")
    @TableField(value = "attr_id", condition = EQUAL)
    @Excel(name = "表单属性ID")
    private String attrId;

    /**
     * 属性名称
     */
    @ApiModelProperty(value = "表单属性名称, 药品名称")
    @Length(max = 255, message = "表单属性名称长度不能超过255")
    @TableField(value = "attr_name", condition = LIKE)
    @Excel(name = "表单属性名称 , 药品名称")
    private String attrName;

    /**
     * 属性值
     */
    @ApiModelProperty(value = "目标属性值（医院ID、选项ID、 用药状态）")
    @Length(max = 255, message = "目标属性值长度不能超过255")
    @TableField(value = "attr_value", condition = EQUAL)
    @Excel(name = "目标属性值")
    private String attrValue;

    /**
     * 属性最小值
     */
    @ApiModelProperty(value = "目标属性最小值")
    @Length(max = 255, message = "目标属性最小值长度不能超过255")
    @TableField(value = "attr_value_min", condition = EQUAL)
    @Excel(name = "目标属性最小值")
    private String attrValueMin;

    /**
     * 属性最大值
     */
    @ApiModelProperty(value = "目标属性最大值")
    @Length(max = 255, message = "目标属性最大值长度不能超过255")
    @TableField(value = "attr_value_max", condition = EQUAL)
    @Excel(name = "目标属性最大值")
    private String attrValueMax;

    /**
     * 标签Id
     */
    @ApiModelProperty(value = "标签Id")
    @TableField("tag_id")
    @Excel(name = "标签Id")
    private Long tagId;

    @ApiModelProperty(value = "表单属性类型")
    @TableField(value = "widget_type", condition = EQUAL)
    @Excel(name = "表单属性类型")
    private String widgetType;


    @Builder
    public Attr(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String attrId, String attrName, String attrValue, String attrValueMin, String attrValueMax, 
                    Long tagId, String widgetType) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.attrId = attrId;
        this.attrName = attrName;
        this.attrValue = attrValue;
        this.attrValueMin = attrValueMin;
        this.attrValueMax = attrValueMax;
        this.tagId = tagId;
        this.widgetType = widgetType;
    }

}
