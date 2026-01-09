package com.caring.sass.user.dto;

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
import com.caring.sass.base.entity.SuperEntity;
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
 * 常用语模板分类
 * </p>
 *
 * @author 杨帅
 * @since 2023-05-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CommonMsgTemplateTypeUpdateDTO", description = "常用语模板分类")
public class CommonMsgTemplateTypeUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    @ApiModelProperty(value = "名称")
    @Length(max = 200, message = "长度不能超过200")
    private String typeName;
    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private Integer typeSort;
    /**
     * 用户类型 NursingStaff, doctor
     */
    @ApiModelProperty(value = "用户类型 NursingStaff, doctor")
    @Length(max = 255, message = "用户类型 NursingStaff, doctor长度不能超过255")
    private String userType;
}
