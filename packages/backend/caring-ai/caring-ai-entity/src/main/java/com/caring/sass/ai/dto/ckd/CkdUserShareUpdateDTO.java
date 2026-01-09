package com.caring.sass.ai.dto.ckd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CkdUserShareUpdateDTO", description = "ckd会员分享成功转换记录")
public class CkdUserShareUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 分享人ID
     */
    @ApiModelProperty(value = "分享人ID")
    private Long shareUserId;
    /**
     * 粉丝用户
     */
    @ApiModelProperty(value = "粉丝用户")
    private Long fanUsers;
}
