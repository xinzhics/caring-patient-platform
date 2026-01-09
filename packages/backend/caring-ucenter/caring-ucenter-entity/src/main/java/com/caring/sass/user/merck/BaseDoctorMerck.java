package com.caring.sass.user.merck;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseDoctorMerck  {

    private String department;  //科室

    private String title;   //职称

    private String identityCard;  //身份证

    private String doctorType;

    @ApiModelProperty("是否认证 1 已认证 0 未认证")
    private Integer certification;

    @ApiModelProperty("环信Im开关 1 关闭 其他值为开启")
    private Integer hxImClose;

    @ApiModelProperty("Im开关 1 关闭 其他值为开启")
    private Integer imClose;

    // 联系方式类型 (1: 手机， 0： 座机)
    private Integer linkType;

    // 区号
    private String areaCode;

    // 座机
    private String landLine;

    // 分机号
    private String ext;


}
