package com.caring.sass.tenant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.tenant.enumeration.SequenceEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

@Data
@Builder
@Accessors(chain = true)
@TableName("t_sys_sequence")
public class Sequence {

    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    @ApiModelProperty(value = "自增类型")
    @Length(max = 255, message = "自增类型")
    @TableField(value = "seq_type", condition = EQUAL)
    private SequenceEnum seqType;

    @ApiModelProperty(value = "当前值")
    @Length(max = 255, message = "当前值")
    @TableField(value = "current_val", condition = EQUAL)
    private Long currentVal;
}
