package com.caring.sass.ai.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * ai用户关联表
 * </p>
 *
 * @author 杨帅
 * @since 2025-07-29
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_user_join")
@ApiModel(value = "UserJoin", description = "ai用户关联表")
@AllArgsConstructor
public class UserJoin extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 科普名片id
     */
    @ApiModelProperty(value = "科普名片id")
    @TableField("business_card_id")
    @Excel(name = "科普名片id")
    private Long businessCardId;

    /**
     * AI分身平台用户ID
     */
    @ApiModelProperty(value = "AI分身平台用户ID")
    @TableField("ai_know_user_id")
    @Excel(name = "AI分身平台用户ID")
    private Long aiKnowUserId;

    /**
     * ai工作室医生租户
     */
    @ApiModelProperty(value = "ai工作室医生租户")
    @Length(max = 50, message = "ai工作室医生租户长度不能超过50")
    @TableField(value = "ai_studio_tenant_id", condition = LIKE)
    @Excel(name = "ai工作室医生租户")
    private String aiStudioTenantId;

    /**
     * ai工作室医生ID
     */
    @ApiModelProperty(value = "ai工作室医生ID")
    @TableField("ai_studio_doctor_id")
    @Excel(name = "ai工作室医生ID")
    private Long aiStudioDoctorId;


    @Builder
    public UserJoin(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long businessCardId, Long aiKnowUserId, String aiStudioTenantId, Long aiStudioDoctorId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.businessCardId = businessCardId;
        this.aiKnowUserId = aiKnowUserId;
        this.aiStudioTenantId = aiStudioTenantId;
        this.aiStudioDoctorId = aiStudioDoctorId;
    }

}
