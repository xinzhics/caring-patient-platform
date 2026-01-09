package com.caring.sass.tenant.dto;

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
import java.util.List;

/**
 * <p>
 * 实体类
 * 用户项目管理表
 * </p>
 *
 * @author 杨帅
 * @since 2023-04-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "GlobalUserTenantSaveDTO", description = "用户项目管理表")
public class GlobalUserTenantSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号ID
     */
    @ApiModelProperty(value = "账号ID")
    @NotNull(message = "账号ID不能为空")
    private Long accountId;
    /**
     * 项目租户ID
     */
    @ApiModelProperty(value = "项目租户ID")
    @NotNull(message = "项目租户ID不能为空")
    private List<Long> tenantIds;


}
