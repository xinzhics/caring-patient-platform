package com.caring.sass.ai.dto.know;

import com.caring.sass.ai.entity.know.KnowDoctorType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 知识库用户
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "KnowledgeUserExistDTO", description = "知识库用户")
public class KnowledgeUserExistDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户手机号
     */
    @NonNull
    @NotEmpty
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 20, message = "用户手机号长度不能超过20")
    private String userMobile;


    /**
     * 租户(一个主任一个租户)
     */
    @ApiModelProperty(value = "域名")
    @Length(max = 50, message = "域名")
    private String userDomain;

    @ApiModelProperty(value = "用户类型， 小程序传 CHIEF_PHYSICIAN ")
    private KnowDoctorType userType;

}
