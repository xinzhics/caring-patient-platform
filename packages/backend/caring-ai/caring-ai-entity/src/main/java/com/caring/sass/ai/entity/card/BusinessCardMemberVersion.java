package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 用户的会员版本
 * </p>
 *
 * @author 杨帅
 * @since 2025-01-21
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_business_card_member_version")
@ApiModel(value = "BusinessCardMemberVersion", description = "用户的会员版本")
@AllArgsConstructor
public class BusinessCardMemberVersion extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 用户版本
     */
    @ApiModelProperty(value = "用户版本")
    @TableField(value = "member_version", condition = EQUAL)
    @Excel(name = "用户版本")
    private BusinessCardMemberVersionEnum memberVersion;


    /**
     * 版本到期时间
     */
    @ApiModelProperty(value = "版本到期时间")
    @TableField("expiration_date")
    @Excel(name = "版本到期时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime expirationDate;


    @Builder
    public BusinessCardMemberVersion(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long userId, BusinessCardMemberVersionEnum memberVersion, LocalDateTime expirationDate) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userId = userId;
        this.memberVersion = memberVersion;
        this.expirationDate = expirationDate;
    }

}
