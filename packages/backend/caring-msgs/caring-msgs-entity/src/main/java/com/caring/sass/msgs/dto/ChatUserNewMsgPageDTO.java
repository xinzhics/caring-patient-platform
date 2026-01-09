package com.caring.sass.msgs.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 小组
 * </p>
 *
 * @author leizhi
 * @since 2020-09-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "医生聊天列表")
public class ChatUserNewMsgPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "患者名字", name = "patientName", dataType = "String")
    protected String patientName;

    @NotNull
    @ApiModelProperty(value = "请求方Id(医生Id/医助ID)", name = "requestId", dataType = "String")
    protected String requestId;

    @ApiModelProperty(value = "请求方角色", name = "requestRoleType", dataType = "String")
    protected String requestRoleType;

    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "小组ID")
    private Long groupId;

}
