package com.caring.sass.ai.dto.ckd;

import com.caring.sass.ai.entity.ckd.CkdUserOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@Data
public class CkdUserSharePageModel {


    @ApiModelProperty("粉丝ID")
    private Long fanUserId;


    @ApiModelProperty("粉丝姓名")
    private String userName;

    @ApiModelProperty("粉丝注册时间")
    private LocalDateTime createTime;


    @ApiModelProperty("粉丝会员等级")
    private CkdMembershipLevel membershipLevel;


    @ApiModelProperty("订单")
    private List<CkdUserOrder> orderList;

    @ApiModelProperty("会员到期时间")
    private LocalDateTime expirationDate;



}
