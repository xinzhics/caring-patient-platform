package com.caring.sass.ai.entity.ckd;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 实体类
 * ckd会员分享成功转换记录
 * </p>
 *
 * @author 杨帅
 * @since 2025-03-14
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_ckd_user_share")
@ApiModel(value = "CkdUserShare", description = "ckd会员分享成功转换记录")
@AllArgsConstructor
public class CkdUserShare extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 分享人ID
     */
    @ApiModelProperty(value = "分享人ID")
    @TableField("share_user_id")
    @Excel(name = "分享人ID")
    private Long shareUserId;

    /**
     * 粉丝用户
     */
    @ApiModelProperty(value = "粉丝用户")
    @TableField("fan_users")
    @Excel(name = "粉丝用户")
    private Long fanUsers;


    @Builder
    public CkdUserShare(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long shareUserId, Long fanUsers) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.shareUserId = shareUserId;
        this.fanUsers = fanUsers;
    }

}
