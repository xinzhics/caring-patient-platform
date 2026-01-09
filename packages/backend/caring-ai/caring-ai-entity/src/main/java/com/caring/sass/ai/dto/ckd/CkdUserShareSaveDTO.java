package com.caring.sass.ai.dto.ckd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
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
@ApiModel(value = "CkdUserShareSaveDTO", description = "ckd会员分享成功转换记录")
public class CkdUserShareSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分享人ID
     */
    @NotNull
    @ApiModelProperty(value = "分享人ID")
    private Long shareUserId;
    /**
     * 粉丝用户
     */
    @ApiModelProperty(value = "粉丝用户")
    @NotNull
    private Long fanUsers;

}
