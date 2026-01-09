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
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 实体类
 * 医生的自定义小组
 * </p>
 *
 * @author yangshuai
 * @since 2022-08-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "DoctorCustomGroupUpdateDTO", description = "医生的自定义小组")
public class DoctorCustomGroupUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 小组名称
     */
    @ApiModelProperty(value = "小组名称")
    @Length(max = 300, message = "小组名称长度不能超过300")
    private String groupName;

    @ApiModelProperty(value = "要加入的患者id集合")
    private Set<Long> joinPatientIds;


    @ApiModelProperty(value = "移出的患者id集合")
    private Set<Long> removePatientIds;


}
