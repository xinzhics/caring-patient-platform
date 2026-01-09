package com.caring.sass.msgs.entity;

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
 * 
 * </p>
 *
 * @author 杨帅
 * @since 2024-04-22
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_msg_chat_automatic_reply")
@ApiModel(value = "ChatAutomaticReply", description = "")
@AllArgsConstructor
public class ChatAutomaticReply extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 回复的内容
     */
    @ApiModelProperty(value = "回复的内容")
    @Length(max = 200, message = "回复的内容长度不能超过200")
    @TableField(value = "content", condition = LIKE)
    @Excel(name = "回复的内容")
    private String content;

    /**
     * 1 开启， 0关闭
     */
    @ApiModelProperty(value = "1 开启， 0关闭")
    @TableField("status")
    @Excel(name = "1 开启， 0关闭", replace = {"是_true", "否_false", "_null"})
    private Boolean status;

    /**
     * 消息超出多少时间未回复
     */
    @ApiModelProperty(value = "消息超出多少时间未回复")
    @TableField("time_out")
    @Excel(name = "消息超出多少时间未回复")
    private Integer timeOut;

    @ApiModelProperty(value = "租户")
    @TableField("tenant_code")
    @Excel(name = "租户")
    private String tenantCode;




}
