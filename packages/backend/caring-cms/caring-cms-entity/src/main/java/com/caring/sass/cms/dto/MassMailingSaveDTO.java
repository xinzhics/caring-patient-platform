package com.caring.sass.cms.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.cms.constant.SendTarget;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * @ClassName MassMailingSaveDTO
 * @Description
 * @Author yangShuai
 * @Date 2021/11/19 14:02
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "MassMailingSaveDTO", description = "群发消息")
public class MassMailingSaveDTO extends MassMailingBaseDto {

    /**
     * 定时发送： timing
     * 手动发送： manual
     */
    @ApiModelProperty(value = "发送类型 定时发送 timing 手动发送： manual")
    private String sendType;

    /**
     * 定时发送的时间
     */
    @ApiModelProperty(value = "定时发送的时间")
    private LocalDateTime timingSendTime;


    /**
     * 发送的目标
     */
    @ApiModelProperty(value = "发送的目标")
    @TableField(value = "send_target", condition = EQUAL)
    private SendTarget sendTarget;


    /**
     * 选择的标签 的id
     */
    @ApiModelProperty(value = "选择的标签")
    @TableField(value = "tag_ids", condition = EQUAL)
    private List<Long> tagIds;

    /**
     * openIds List的 toJSONString
     */
    @ApiModelProperty(value = "发送的目标的openIds")
    @TableField(value = "openids")
    private List<Long> openIds;


}
