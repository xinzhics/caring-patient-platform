package com.caring.sass.cms.dto;

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
@ApiModel(value = "BannerSaveDTO", description = "banner")
public class BannerSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;



    @ApiModelProperty(value = "用户角色")
    @Length(max = 50, message = "用户角色长度不能超过50")
    private String userRole;

    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    private String fileUrl;
    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级")
    private Integer order;
    /**
     * 跳转类型
     */
    @ApiModelProperty(value = "跳转类型")
    @Length(max = 50, message = "跳转类型长度不能超过50")
    private String jumpType;
    /**
     * 网址
     */
    @ApiModelProperty(value = "网址")
    @Length(max = 500, message = "网址长度不能超过500")
    private String jumpWebsite;
    /**
     * 系统功能
     */
    @ApiModelProperty(value = "系统功能")
    @Length(max = 200, message = "系统功能长度不能超过100")
    private String systemFunction;

    @ApiModelProperty(value = "租户")
    @NotNull
    private String tenantCode;

}
