package com.caring.sass.nursing.entity.plan;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 护理目标
 * </p>
 *
 * @author leizhi
 * @since 2020-09-16
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_aim")
@ApiModel(value = "Aim", description = "护理目标")
@AllArgsConstructor
public class Aim extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @Length(max = 20, message = "名称长度不能超过20")
    @TableField(value = "name_", condition = LIKE)
    @Excel(name = "名称")
    private String name;

    /**
     * 图片URL
     */
    @ApiModelProperty(value = "图片URL")
    @Length(max = 255, message = "图片URL长度不能超过255")
    @TableField(value = "url", condition = LIKE)
    @Excel(name = "图片URL")
    private String url;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 255, message = "描述长度不能超过255")
    @TableField(value = "describe_", condition = LIKE)
    @Excel(name = "描述")
    private String describe;

    /**
     * 是否包含（0：不包含  1：包含）
     */
    @ApiModelProperty(value = "是否包含（0：不包含  1：包含）")
    @TableField("is_contain")
    @Excel(name = "是否包含（0：不包含  1：包含）")
    private Integer isContain;

    /**
     * 项目ID
     */
    @ApiModelProperty(value = "项目ID")
    @TableField("project_id")
    @Excel(name = "项目ID")
    private Long projectId;

    /**
     * 是否是默认目标 （0：不是  1：是）
     */
    @ApiModelProperty(value = "是否是默认目标 （0：不是  1：是）")
    @TableField("is_default")
    @Excel(name = "是否是默认目标 （0：不是  1：是）")
    private Integer isDefault;


    @Builder
    public Aim(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String name, String url, String describe, Integer isContain, Long projectId, Integer isDefault) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.name = name;
        this.url = url;
        this.describe = describe;
        this.isContain = isContain;
        this.projectId = projectId;
        this.isDefault = isDefault;
    }

}
