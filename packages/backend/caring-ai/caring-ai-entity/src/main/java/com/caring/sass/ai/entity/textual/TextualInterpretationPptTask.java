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

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 文献解读PPT
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
@TableName("m_ai_textual_interpretation_ppt_task")
@ApiModel(value = "TextualInterpretationPptTask", description = "文献解读PPT")
@AllArgsConstructor
public class TextualInterpretationPptTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @ApiModelProperty(value = "会话ID")
    @TableField("uid")
    @Excel(name = "会话ID")
    private String uid;

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
    @ApiModelProperty(value = "任务状态 0 创作中 1 创作完成 2 存为草稿")
    @TableField("task_status")
    @Excel(name = "任务状态 0 创作中 1 创作完成 2 存为草稿")
    private Integer taskStatus;

    /**
     * 生产的ppt大纲
     */
    @ApiModelProperty(value = "生产的ppt大纲")

    @TableField("content")
    @Excel(name = "生产的ppt大纲")
    private String content;

    /**
     * 是否展示到ai医生数字分身平台
     */
    @ApiModelProperty(value = "是否展示到ai医生数字分身平台")
    @TableField("show_ai_knowledge")
    @Excel(name = "是否展示到ai医生数字分身平台")
    private Boolean showAIKnowledge;

    /**
     * 上传的文献地址
     */
    @ApiModelProperty(value = "上传的文献地址")
    @Length(max = 255, message = "上传的文献地址长度不能超过255")
    @TableField(value = "file_url", condition = LIKE)
    @Excel(name = "上传的文献地址")
    private String fileUrl;

    /**
     * 做好的ppt地址
     */
    @ApiModelProperty(value = "做好的ppt地址")
    @Length(max = 255, message = "做好的ppt地址长度不能超过255")
    @TableField(value = "ppt_url", condition = LIKE)
    @Excel(name = "做好的ppt地址")
    private String pptUrl;

    @ApiModelProperty(value = "coze的文件ID")
    @TableField(value = "coze_file_id")
    private String cozeFileId;

    @ApiModelProperty(value = "ppt任务ID")
    @TableField(value = "ppt_task_id")
    private String pptTaskId;

    @ApiModelProperty(value = "ppt任务状态1 进行中2 已完成 3 失败")
    @TableField(value = "ppt_task_status")
    private String pptTaskStatus;

    @ApiModelProperty(value = "ppt数据结果")
    @TableField(value = "ppt_data_result")
    private String pptDataResult;

    @ApiModelProperty(value = "上次查询状态时间")
    @TableField(value = "last_query_status_time")
    private LocalDateTime lastQueryStatusTime;




}
