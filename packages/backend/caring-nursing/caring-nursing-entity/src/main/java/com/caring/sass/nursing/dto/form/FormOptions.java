package com.caring.sass.nursing.dto.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 表单选项字段
 *
 * @author xinzh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "FormOptionsField", description = "表单选项字段")
public class FormOptions {

    @ApiModelProperty("选项的ID")
    private String id;

    @ApiModelProperty("选项的分数")
    private Float score;

    @ApiModelProperty("隐藏分数")
    private Boolean scoreHide;

    @ApiModelProperty("不计分")
    private Boolean notScored;

    private String codeTypeId;

    private Integer code;

    private String name;

    private String order;

    @ApiModelProperty("默认值")
    private Boolean defaultValue;

    @ApiModelProperty("选项的文本描述")
    String attrValue;

    @ApiModelProperty("多选时，选中为true。")
    private Boolean select;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty("子选项")
    private List<FormOptions> children;

    @ApiModelProperty("只支持 多选和单选组件， 选项下面的问题， 问题里面的属性与同类型父题相同")
    private List<FormField> questions;

    public Boolean getScoreHide() {
        if (scoreHide == null) {
            return false;
        }
        return scoreHide;
    }

    public Boolean getNotScored() {
        if (notScored == null) {
            return false;
        }
        return notScored;
    }
}
