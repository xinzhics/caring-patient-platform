package com.caring.sass.ai.entity.textual;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 文献解读txt
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-05
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_textual_interpretation_text_task")
@ApiModel(value = "TextualInterpretationTextTask", description = "文献解读txt")
@AllArgsConstructor
public class TextualInterpretationTextTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 创作的步骤
     */
    @ApiModelProperty(value = "创作的步骤")
    @TableField("step")
    @Excel(name = "创作的步骤")
    private Integer step;

    /**
     * 上传文件名称
     */
    @ApiModelProperty(value = "上传文件名称")
    @Length(max = 500, message = "上传文件名称长度不能超过500")
    @TableField(value = "file_name", condition = LIKE)
    @Excel(name = "上传文件名称")
    private String fileName;

    /**
     * 选择的标题
     */
    @ApiModelProperty(value = "选择的标题")
    @Length(max = 300, message = "选择的标题长度不能超过300")
    @TableField(value = "title", condition = LIKE)
    @Excel(name = "选择的标题")
    private String title;

    /**
     * 任务状态 0 创作中 1 创作完成 2 存为草稿
     */
    @ApiModelProperty(value = "任务状态 0 创作中 1 创作完成 2 保存到作品集")
    @TableField("task_status")
    private Integer taskStatus;

    /**
     * 生产的文稿
     */
    @ApiModelProperty(value = "生产的文稿")
    @TableField("content")
    @Excel(name = "生产的文稿")
    private String content;

    /**
     * 是否展示到ai医生数字分身平台
     */
    @ApiModelProperty(value = "是否展示到ai医生数字分身平台")
    @TableField("show_ai_knowledge")
    @Excel(name = "是否展示到ai医生数字分身平台")
    private Boolean showAIKnowledge;

    @ApiModelProperty(value = "coze的文件ID")
    @TableField("coze_file_id")
    @Excel(name = "coze的文件ID")
    private String cozeFileId;

    @ApiModelProperty(value = "文件路径")
    @TableField("file_url")
    private String fileUrl;





}
