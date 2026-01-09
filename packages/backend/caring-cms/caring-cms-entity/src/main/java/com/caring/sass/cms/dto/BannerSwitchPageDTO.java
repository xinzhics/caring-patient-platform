package com.caring.sass.cms.dto;

import java.time.LocalDateTime;
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

/**
 * <p>
 * 实体类
 * banner
 * </p>
 *
 * @author 杨帅
 * @since 2023-12-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "BannerSwitchPageDTO", description = "banner")
public class BannerSwitchPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户角色
     */
    @ApiModelProperty(value = "用户角色")
    @Length(max = 50, message = "用户角色长度不能超过50")
    private String userRole;
    /**
     * 开关状态
     */
    @ApiModelProperty(value = "开关状态")
    private Integer switchStatus;


    @ApiModelProperty(value = "租户")
    @NotNull
    private String tenantCode;

}
