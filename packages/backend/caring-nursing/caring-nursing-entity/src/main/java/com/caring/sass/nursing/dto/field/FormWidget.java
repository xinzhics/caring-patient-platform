package com.caring.sass.nursing.dto.field;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @ClassName FormWidget
 * @Description
 * @Author yangShuai
 * @Date 2022/3/15 17:03
 * @Version 1.0
 */
@ApiModel("组件的类型")
public enum FormWidget {

    SINGLE_LINE_TEXT("SingleLineText", "单行文本"),
    MULTI_LINE_TEXT("MultiLineText", "多行文字"),
    RADIO("Radio", "Radio单选"),
    CHECK_BOX("CheckBox", "多选"),
    DROPDOWN_SELECT("DropdownSelect", "Select下拉"),
    MULTI_IMAGE_UPLOAD("MultiImageUpload", "图片多选"),
    NUMBER("NUMBER", "数字"),
    TIME("Time", "时间"),
    DATE("Date", "日期"),
    PAGE_SPLIT("Page", "分页"),
    DESC("Desc", "描述"),
    SplitLine("SplitLine", "分割线"),
    Address("Address", "地址"),
    FullName("FullName", "姓名"),
    Avatar("Avatar", "头像"),
    ;

    /**
     * 组件的类型(提交表单时)
     */
    @ApiModelProperty("组件的类型(提交表单时)")
    private String type;

    /**
     * 组件的类型名称
     */
    @ApiModelProperty("组件的类型名称")
    private String name;

    FormWidget(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
