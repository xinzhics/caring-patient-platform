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
@ApiModel(value = "医生共享聊天的")
public class ChatDoctorSharedMsgDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "患者名字", name = "patientName", dataType = "String")
    protected String patientName;

    @NotNull
    @ApiModelProperty(value = "请求方Id(医生Id)", name = "requestId", dataType = "Long")
    protected Long requestId;


}
