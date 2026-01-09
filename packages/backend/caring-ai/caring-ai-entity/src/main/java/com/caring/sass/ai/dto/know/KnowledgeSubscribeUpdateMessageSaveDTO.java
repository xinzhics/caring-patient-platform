package com.caring.sass.ai.dto.know;

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
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 博主订阅设置修改记录
 * </p>
 *
 * @author 杨帅
 * @since 2025-08-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KnowledgeSubscribeUpdateMessageSaveDTO", description = "博主订阅设置修改记录")
public class KnowledgeSubscribeUpdateMessageSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "知识库博主ID")
    private Long knowledgeUserId;


    @ApiModelProperty(value = "订阅开关")
    private Boolean subscribeSwitch;

    @ApiModelProperty(value = "订阅最后更新时间")
    private LocalDateTime subscribeLastUpdateTime;

    @ApiModelProperty(value = "订阅会员名称")
    private String subscribeUserName;

    @ApiModelProperty(value = "开启月费")
    private Boolean openMonthlyPayment;

    @ApiModelProperty(value = "开启年费")
    private Boolean openAnnualPayment;

    @ApiModelProperty(value = "开放科普患教数据")
    private Boolean openArticleData;

    @ApiModelProperty(value = "开放文献解读")
    private Boolean openTextual;

    @ApiModelProperty(value = "开放学术材料")
    private Boolean openAcademicMaterials;

    @ApiModelProperty(value = "开放个人成果")
    private Boolean openPersonalAchievements;

    @ApiModelProperty(value = "开放病例数据库")
    private Boolean openCaseDatabase;

    @ApiModelProperty(value = "开放日常收藏")
    private Boolean openDailyCollection;

    @ApiModelProperty(value = "月度会员价格，单位是分")
    private Integer basicMembershipPrice;

    @ApiModelProperty(value = "年度会员价格，单位是分")
    private Integer professionalVersionPrice;

}
