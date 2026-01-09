package com.caring.sass.nursing.dto.form;

/**
 * 表单小部件类型
 *
 * @author xinzh
 */
public interface FormWidgetType {
    /**  单行文字*/
    String SINGLE_LINE_TEXT= "SingleLineText";

    /**  多行文字*/
    String MULTI_LINE_TEXT= "MultiLineText";

    /**  Radio单选*/
    String RADIO= "Radio";

    /**  Check多选*/
    String CHECK_BOX= "CheckBox";

    /**  Select下拉*/
    String DROPDOWN_SELECT= "DropdownSelect";

    /**  图片多选*/
    @Deprecated
    String MULTI_IMAGE_UPLOAD= "MultiImageUpload";

    /**  数字*/
    String NUMBER= "Number";

    /**  时间*/
    String TIME= "Time";

    /**  日期*/
    String DATE= "Date";

    /**  姓名*/
    String FULL_NAME= "FullName";

    /**  头像*/
    String AVATAR = "Avatar";

    /**  地址*/
    String ADDRESS= "Address";

    /**  描述*/
    String DESC= "Desc";

    /**  分页*/
    String PAGE= "Page";

    /**  分割线*/
    String SPLIT_LINE= "SplitLine";
}
