package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_article_task")
@ApiModel(value = "ArticleTask", description = "Ai创作任务")
@AllArgsConstructor
public class ArticleTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 创作的步骤
     */
    @ApiModelProperty(value = "创作的步骤")
    @TableField(value = "step", condition = EQUAL)
    @Excel(name = "创作的步骤")
    private Integer step;

    /**
     * 关键词
     */
    @ApiModelProperty(value = "关键词")
    @Length(max = 500, message = "关键词长度不能超过500")
    @TableField(value = "key_words", condition = LIKE)
    @Excel(name = "关键词")
    private String keyWords;

    /**
     * 受众
     */
    @ApiModelProperty(value = "受众")
    @Length(max = 500, message = "受众长度不能超过500")
    @TableField(value = "audience", condition = LIKE)
    @Excel(name = "受众")
    private String audience;

    /**
     * 写作风格
     */
    @ApiModelProperty(value = "写作风格")
    @Length(max = 100, message = "写作风格长度不能超过100")
    @TableField(value = "writing_style", condition = LIKE)
    @Excel(name = "写作风格")
    private String writingStyle;

    /**
     * 文章字数
     */
    @ApiModelProperty(value = "文章字数")
    @TableField("article_word_count")
    @Excel(name = "文章字数")
    private Integer articleWordCount;

    /**
     * 自动配图 0 关闭 1 开启
     */
    @ApiModelProperty(value = "自动配图 0 关闭 1 开启")
    @TableField("automatic_picture_matching")
    @Excel(name = "自动配图 0 关闭 1 开启")
    private Integer automaticPictureMatching;

    /**
     * 选择的标题
     */
    @ApiModelProperty(value = "选择的标题")
    @TableField("title")
    @Excel(name = "选择的标题")
    private String title;

    /**
     * 任务状态 0 创作中 1 创作完成 2 存为草稿
     */
    @ApiModelProperty(value = "任务状态 0 创作中 1 创作完成 2 存为草稿")
    @TableField("task_status")
    @Excel(name = "任务状态 0 创作中 1 创作完成 2 存为草稿")
    private Integer taskStatus;


    @NotEmpty
    @ApiModelProperty(value = "会话标识")
    @TableField(exist = false)
    private String uid;


    @ApiModelProperty(value = "版本")
    @TableField("version")
    private String version;


    @ApiModelProperty(value = "任务创作类型 1: 图文创作， 2 小红书文案， 3 视频口播")
    @TableField("task_type")
    private Integer taskType;

    @ApiModelProperty(value = "创作方式 1 原创， 2 仿写")
    @TableField("creative_approach")
    private Integer creativeApproach;

    @ApiModelProperty(value = "仿写方式: 1 文本， 2 链接")
    @TableField("imitative_writing_type")
    private Integer imitativeWritingType;

    @ApiModelProperty(value = "原创仿写素材： 文本内容 或 链接地址")
    @TableField("imitative_writing_material")
    private String imitativeWritingMaterial;

    @ApiModelProperty(value = "任务最终结果")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "展示在ai医生数字分身平台")
    @TableField("show_ai_knowledge")
    private Boolean showAIKnowledge;




}
