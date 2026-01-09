package com.caring.sass.ai.dto.know;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
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
@ApiModel(value = "KnowledgeUserSaveDTO", description = "知识库用户")
public class KnowledgeUserSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户手机号
     */
    @NonNull
    @NotEmpty
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 20, message = "用户手机号长度不能超过20")
    private String userMobile;

    @ApiModelProperty(value = "手机号验证码")
    @Length(max = 20, message = "手机号验证码")
    private String smsCode;

    @ApiModelProperty(value = "用户密码")
    @Length(max = 40, message = "用户密码")
    private String password;

    /**
     * 租户(一个主任一个租户)
     */
    @Deprecated
    @ApiModelProperty(value = "域名")
    @Length(max = 50, message = "域名")
    private String userDomain;


    @ApiModelProperty(value = "openId")
    @Length(max = 100, message = "openId")
    private String openId;

}
