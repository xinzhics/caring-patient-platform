package com.caring.sass.nursing.dto.plan;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName PlanCmsReminderLog
 * @Description
 * @Author yangShuai
 * @Date 2021/12/20 11:17
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "PlanCmsReminderLogPageDTO", description = "推送的图文记录")
public class PlanCmsReminderLogPageDTO implements Serializable {

    @ApiModelProperty(value = "用户ID")
    @TableField(value = "user_id", condition = EQUAL)
    private Long userId;


}
