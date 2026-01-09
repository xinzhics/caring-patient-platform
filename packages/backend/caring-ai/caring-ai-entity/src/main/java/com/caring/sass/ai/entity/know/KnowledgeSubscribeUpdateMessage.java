package com.caring.sass.ai.entity.know;

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
 * 博主订阅设置修改记录
 * </p>
 *
 * @author 杨帅
 * @since 2025-08-05
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_subscribe_update_message")
@ApiModel(value = "KnowledgeSubscribeUpdateMessage", description = "博主订阅设置修改记录")
@AllArgsConstructor
public class KnowledgeSubscribeUpdateMessage extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 知识库博主ID
     */
    @ApiModelProperty(value = "知识库博主ID")
    @TableField("knowledge_user_id")
    @Excel(name = "知识库博主ID")
    private Long knowledgeUserId;

    /**
     * 修改记录说明
     */
    @ApiModelProperty(value = "修改记录说明")
    @Length(max = 65535, message = "修改记录说明长度不能超过65535")
    @TableField("update_message")
    @Excel(name = "修改记录说明")
    private String updateMessage;


    @Builder
    public KnowledgeSubscribeUpdateMessage(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long knowledgeUserId, String updateMessage) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.knowledgeUserId = knowledgeUserId;
        this.updateMessage = updateMessage;
    }

}
