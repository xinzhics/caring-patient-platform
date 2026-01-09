package com.caring.sass.ai.entity.call;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
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
 * 分身通话内容
 * </p>
 *
 * @author 杨帅
 * @since 2025-12-02
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_call_content")
@ApiModel(value = "CallContent", description = "分身通话内容")
@AllArgsConstructor
public class CallContent extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 通话记录id
     */
    @ApiModelProperty(value = "通话记录id")
    @TableField("call_record_id")
    @Excel(name = "通话记录id")
    private Long callRecordId;

    /**
     * 用户 user，智能体：agent
     */
    @ApiModelProperty(value = "用户 user，智能体：agent")
    @Length(max = 100, message = "用户 user，智能体：agent长度不能超过100")
    @TableField(value = "user_type", condition = LIKE)
    @Excel(name = "用户 user，智能体：agent")
    private String userType;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    @Length(max = 2000, message = "消息内容长度不能超过2000")
    @TableField(value = "call_content", condition = LIKE)
    @Excel(name = "消息内容")
    private String callContent;


    @Builder
    public CallContent(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long callRecordId, String userType, String callContent) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.callRecordId = callRecordId;
        this.userType = userType;
        this.callContent = callContent;
    }

}
