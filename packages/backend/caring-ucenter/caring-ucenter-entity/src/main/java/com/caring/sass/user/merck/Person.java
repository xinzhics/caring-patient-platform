package com.caring.sass.user.merck;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Person extends AppPerson {

    private Long id;

    private String strid;

    private String name;

    @ApiModelProperty("关键词： 姓名， 档案号， 手机号")
    private String keyNode;

    private String sex;

    private String birth;

    private String address;

    private Image headImage;

    private String status;

    private String remark;

    private String imAccount;

    @ApiModelProperty(value = "是否展开聊天小组, 0 关闭，1展示")
    private Integer imGroupStatus;

    @ApiModelProperty(value = "专员是否展开聊天小组, 0 关闭，1展示")
    private Integer nursingStaffImGroupStatus;

    @ApiModelProperty(value = "医生是否展开聊天小组, 0 关闭，1展示")
    private Integer doctorImGroupStatus;


}
