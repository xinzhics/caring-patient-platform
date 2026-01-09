package com.caring.sass.nursing.entity.follow;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 随访任务
 * </p>
 *
 * @author 杨帅
 * @since 2023-01-04
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_nursing_follow_task")
@ApiModel(value = "FollowTask", description = "随访任务")
@AllArgsConstructor
public class FollowTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "名称")
    @Length(max = 20, message = "长度不能超过20")
    @TableField(value = "name_", condition = LIKE)
    private String name;

    @ApiModelProperty(value = "背景图url")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "url", condition = LIKE)
    private String url;

    @ApiModelProperty(value = "背景图文件名称")
    @TableField(value = "bg_file_name", condition = LIKE)
    private String bgFileName;

    @ApiModelProperty(value = "使用默认背景图 (1: 使用默认，0 使用上传的)")
    @TableField(value = "use_default", condition = EQUAL)
    private Integer useDefault;

    /**
     * 是否显示概要（0：不显示  1：显示）
     */
    @ApiModelProperty(value = "是否显示概要（0：不显示  1：显示）")
    @TableField("show_outline")
    @Excel(name = "是否显示概要（0：不显示  1：显示）")
    private Integer showOutline;


    @Builder
    public FollowTask(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String name, String url, Integer showOutline) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.name = name;
        this.url = url;
        this.showOutline = showOutline;
    }

}
