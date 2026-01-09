package com.caring.sass.ai.entity.know;

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
 * 记录普通用户订阅博主的信息，包括了普通用户在博主这里的会员时长，会员等级，订阅状态
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_knowledge_user_subscribe", autoResultMap = true)
@ApiModel(value = "KnowledgeUserSubscribe", description = "知识库用户订阅会员表")
@AllArgsConstructor
public class KnowledgeUserSubscribe extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "知识库用户ID")
    @TableField(value = "knowledge_user_id", condition = EQUAL)
    private Long knowledgeUserId;

    @ApiModelProperty(value = "会员等级")
    @TableField(value = "membership_level", condition = EQUAL)
    private MembershipLevel membershipLevel;


    @ApiModelProperty(value = "会员到期时间")
    @TableField(value = "membership_expiration")
    private LocalDateTime membershipExpiration;


    @ApiModelProperty(value = "体验AI的截止时间")
    @Deprecated
    @TableField(value = "experience_ai_time")
    private LocalDateTime experienceAiTime;


    @ApiModelProperty(value = "域名(一个主任一个域名)")
    @Length(max = 50, message = "域名(一个主任一个域名)长度不能超过50")
    @TableField(value = "user_domain", condition = LIKE)
    @Excel(name = "租户(一个主任一个租户)")
    private String userDomain;


    @ApiModelProperty(value = "订阅状态")
    @TableField(value = "subscribe_status", condition = EQUAL)
    private Boolean subscribeStatus;

    @ApiModelProperty(value = "订阅时间")
    @TableField(value = "subscribe_time")
    private LocalDateTime subscribeTime;

    @ApiModelProperty(value = "查询的等级要求")
    @Deprecated
    @TableField(exist = false)
    private Integer viewPermissions;

    @ApiModelProperty(value = "下载的等级要求")
    @Deprecated
    @TableField(exist = false)
    private Integer downloadPermission;

    public Integer getViewPermissions() {
        return MembershipLevel.getGrade(membershipLevel);
    }

    public Integer getDownloadPermission() {
        return MembershipLevel.getGrade(membershipLevel);
    }


}
