package com.caring.sass.ai.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * Ai创作任务
 * </p>
 *
 * @author 杨帅
 * @since 2024-08-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ArticleTaskSaveBo", description = "Ai创作")
public class ArticleTaskSaveBo implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    @ApiModelProperty(value = "选择的标题")
    @Excel(name = "选择的标题")
    private String title;

    @ApiModelProperty(value = "任务创作类型 1: 图文创作， 2 小红书文案， 3 视频口播")
    @TableField("task_type")
    private Integer taskType = 1;

    @ApiModelProperty(value = "创作方式 1 原创， 2 仿写")
    @TableField("creative_approach")
    private Integer creativeApproach = 1;

    @ApiModelProperty(value = "仿写方式: 1 文本， 2 链接")
    @TableField("imitative_writing_type")
    private Integer imitativeWritingType;

    @ApiModelProperty(value = "原创仿写素材： 文本内容 或 链接地址")
    @TableField("imitative_writing_material")
    private String imitativeWritingMaterial;

    @ApiModelProperty(value = "任务最终结果")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "任务状态")
    @TableField("task_status")
    private Integer taskStatus;



}
