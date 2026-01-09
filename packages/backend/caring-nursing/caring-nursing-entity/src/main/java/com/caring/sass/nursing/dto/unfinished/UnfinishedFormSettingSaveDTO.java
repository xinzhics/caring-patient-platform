package com.caring.sass.nursing.dto.unfinished;

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
 * 患者管理-未完成表单跟踪设置
 * </p>
 *
 * @author 杨帅
 * @since 2024-05-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "UnfinishedFormSettingSaveDTO", description = "患者管理-未完成表单跟踪设置")
public class UnfinishedFormSettingSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "配置列表")
    private List<UnfinishedFormSettingUpdateDTO> updateDTOList;

    @ApiModelProperty(value = "租户")
    private String tenantCode;



}
