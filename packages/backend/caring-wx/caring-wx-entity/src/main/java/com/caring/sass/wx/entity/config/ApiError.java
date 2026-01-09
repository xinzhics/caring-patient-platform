package com.caring.sass.wx.entity.config;

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
 * 微信接口异常
 * </p>
 *
 * @author 杨帅
 * @since 2024-12-26
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_api_error")
@ApiModel(value = "ApiError", description = "微信接口异常")
@AllArgsConstructor
public class ApiError extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "微信api异常")
    @Length(max = 65535, message = "长度不能超过65535")
    @TableField("wx_error_message")
    private String wxErrorMessage;

    @Length(max = 50, message = "长度不能超过50")
    @TableField(value = "wx_app_id", condition = LIKE)
    private String wxAppId;


    @Builder
    public ApiError(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    String wxErrorMessage, String wxAppId) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.wxErrorMessage = wxErrorMessage;
        this.wxAppId = wxAppId;
    }

}
