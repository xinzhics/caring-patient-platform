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
 * 病例库资料标签
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
@TableName("m_ai_knowledge_file_case_database_label")
@ApiModel(value = "KnowledgeFileCaseDatabaseLabel", description = "病例库资料标签")
@AllArgsConstructor
public class KnowledgeFileCaseDatabaseLabel extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "语言")
    @Length(max = 100, message = "语言长度不能超过100")
    @TableField("language_")
    private String language;

    @ApiModelProperty(value = "诊断结果")
    @Length(max = 2000, message = "诊断结果长度不能超过2000")
    @TableField("diagnostic_results")
    private String diagnosticResults;

    @ApiModelProperty(value = "记录时间")
    @Length(max = 300, message = "记录时间长度不能超过300")
    @TableField("ji_lu_time")
    private String jiLuTime;


    @ApiModelProperty(value = "治疗方案")
    @Length(max = 2000, message = "治疗方案2000")
    @TableField("treatment_plan")
    private String treatmentPlan;

    @ApiModelProperty(value = "关键症状")
    @Length(max = 2000, message = "关键症状2000")
    @TableField("key_symptoms")
    private String keySymptoms;

    @ApiModelProperty(value = "病例类型")
    @Length(max = 2000, message = "病例类型2000")
    @TableField("case_type")
    private String caseType;

    @ApiModelProperty(value = "治疗结果")
    @Length(max = 2000, message = "治疗结果2000")
    @TableField("treatment_outcome")
    private String treatmentOutcome;

    @ApiModelProperty(value = "病例来源")
    @Length(max = 1000, message = "病例来源1000")
    @TableField("case_source")
    private String caseSource;

    @ApiModelProperty(value = "发表状态")
    @Length(max = 1000, message = "发表状态1000")
    @TableField("post_status")
    private String postStatus;

    @ApiModelProperty(value = "随访结果")
    @Length(max = 1000, message = "随访结果1000")
    @TableField("follow_up_results")
    private String followUpResults;

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

    public static KnowledgeFileCaseDatabaseLabel build(KnowledgeFile collection) {
        KnowledgeFileCaseDatabaseLabel fileLabel = new KnowledgeFileCaseDatabaseLabel();

        fileLabel.setFileId(collection.getId());
        fileLabel.setKnowUserId(collection.getFileUserId());
        fileLabel.setKnowId(collection.getKnowId());
        return fileLabel;
    }
}
