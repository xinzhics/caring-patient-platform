package com.caring.sass.ai.entity.know;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 知识库文档标签表
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-25
 */
@Data
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_file_personal_achievements_label")
@ApiModel(value = "KnowledgeFilePersonalAchievementsLabel", description = "个人成果资料标签")
@AllArgsConstructor
public class KnowledgeFilePersonalAchievementsLabel extends Entity<Long> {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "语言")
    @Length(max = 100, message = "语言长度不能超过100")
    @TableField("language_")
    private String language;


    @ApiModelProperty(value = "关键词")
    @Length(max = 2000, message = "关键词长度不能超过2000")
    @TableField("key_word")
    private String keyWord;

    @ApiModelProperty(value = "发布时间")
    @Length(max = 100, message = "发布时间长度不能超过100")
    @TableField("release_time")
    private String releaseTime;


    @ApiModelProperty(value = "类型")
    @Length(max = 100, message = "类型100")
    @TableField("personal_achievement_type")
    private String personalAchievementType;

    @ApiModelProperty(value = "作者")
    @Length(max = 500, message = "作者内容长度不能超过500")
    @TableField("author_")
    private String author;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @TableField("know_user_id")
    @Excel(name = "用户ID")
    private Long knowUserId;

    /**
     * 文件ID或日常收集ID
     */
    @ApiModelProperty(value = "文件ID或日常收集ID")
    @TableField("file_id")
    @Excel(name = "文件ID或日常收集ID")
    private Long fileId;


    /**
     * 业务知识库ID
     */
    @ApiModelProperty(value = "业务知识库ID")
    @TableField("know_id")
    @Excel(name = "业务知识库ID")
    private Long knowId;

    public static KnowledgeFilePersonalAchievementsLabel build(KnowledgeFile collection) {
        KnowledgeFilePersonalAchievementsLabel fileLabel = new KnowledgeFilePersonalAchievementsLabel();

        fileLabel.setFileId(collection.getId());
        fileLabel.setKnowUserId(collection.getFileUserId());
        fileLabel.setKnowId(collection.getKnowId());
        return fileLabel;
    }

}
