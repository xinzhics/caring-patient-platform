package com.caring.sass.ai.entity.docuSearch;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 实体类
 * 知识库证据浏览记录
 * </p>
 *
 * @author 杨帅
 * @since 2024-10-17
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_user_browsing_history")
@ApiModel(value = "KnowledgeUserBrowsingHistory", description = "知识库证据浏览记录")
@AllArgsConstructor
public class KnowledgeUserBrowsingHistory extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 问题证据id
     */
    @ApiModelProperty(value = "问题证据id")
    @NotNull(message = "问题证据id不能为空")
    @TableField("problem_evidence_id")
    @Excel(name = "问题证据id")
    private Long problemEvidenceId;

    /**
     * 浏览时间
     */
    @ApiModelProperty(value = "浏览时间")
    @NotNull(message = "浏览时间不能为空")
    @TableField("view_time")
    @Excel(name = "浏览时间")
    private LocalDateTime viewTime;


    @TableField(exist = false)
    private KnowledgeQuestionEvidence questionEvidence;


    @Builder
    public KnowledgeUserBrowsingHistory(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long userId, Long problemEvidenceId, LocalDateTime viewTime) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userId = userId;
        this.problemEvidenceId = problemEvidenceId;
        this.viewTime = viewTime;
    }

}
