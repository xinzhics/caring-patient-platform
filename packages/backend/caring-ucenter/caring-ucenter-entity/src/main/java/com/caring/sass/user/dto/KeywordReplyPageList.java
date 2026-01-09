package com.caring.sass.user.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.user.entity.KeywordSetting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.List;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KeywordReplyPageList", description = "关键字回复内容列表")
public class KeywordReplyPageList {

    @ApiModelProperty(value = "规则ID")
    private Long id;

    /**
     * 规则名
     */
    @ApiModelProperty(value = "规则名")
    private String ruleName;

    /**
     * 回复类型 (text, cms)
     */
    @ApiModelProperty(value = "回复类型 (text, cms)")
    private String replyType;

    /**
     * 回复内容
     */
    @ApiModelProperty(value = "回复内容")
    private String replyContent;

    @ApiModelProperty(value = "启用或关闭（open, close）")
    private String status;

    @ApiModelProperty(value = "昨天触发次数")
    private Integer yesterdayTrigger;

    @ApiModelProperty(value = "今天触发次数")
    private Integer todayTrigger;

    @ApiModelProperty(value = "关键字列表")
    private List<KeywordSetting> keywordSettingList;


}
