package com.caring.sass.user.dto;

import com.caring.sass.user.entity.ConsultationGroupMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ConsultationGroupPage
 * @Description
 * @Author yangShuai
 * @Date 2022/5/11 14:07
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "ConsultationGroupPage", description = "病历讨论列表")
public class ConsultationGroupPage {

    @ApiModelProperty(value = "成员状态(-2 邀请，待加入  ，1 进行中， 2 已结束)")
    @NotNull
    private Integer memberState;

    @ApiModelProperty(value = "用户ID(医助或者医生)")
    private Long userId;

    @NotEmpty
    @ApiModelProperty(value = "用户的IM账号")
    private String imAccount;

    @ApiModelProperty(value = "医生端使用医生的openID来查询")
    private String doctorOpenId;

    @ApiModelProperty(value = "医助传  NursingStaff 医生传 roleDoctor")
    @NotEmpty
    private String userRole;

    public List<Integer> memberState() {
        if (memberState.equals(ConsultationGroupMember.INVITE)) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            arrayList.add(ConsultationGroupMember.REFUSE);
            arrayList.add(ConsultationGroupMember.INVITE);
            return arrayList;
        } else if (memberState.equals(ConsultationGroupMember.JOINED)) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            arrayList.add(memberState);
            arrayList.add(ConsultationGroupMember.CANNOT_REMOVED);
            return arrayList;
        } else {
            ArrayList<Integer> arrayList = new ArrayList<>();
            arrayList.add(memberState);
            return arrayList;
        }
    }

}
