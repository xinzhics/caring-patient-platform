package com.caring.sass.ai.dto.ckd;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * CKD用户信息
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CkdUserInfoUpdateDTO", description = "CKD用户信息")
public class CkdUserInfoUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 100, message = "openId长度不能超过100")
    private String openId;
    @ApiModelProperty(value = "")
    @Length(max = 200, message = "长度不能超过200")
    private String nickname;
    @ApiModelProperty(value = "")
    @Length(max = 400, message = "长度不能超过400")
    private String headImgUrl;
    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    private String userRole;
    /**
     * 体重
     */
    @ApiModelProperty(value = "体重")
    private Integer userWeight;
    /**
     * 身高
     */
    @ApiModelProperty(value = "身高")
    private Integer userHeight;
    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    private LocalDateTime dateOfBirth;
    /**
     * 血肌酐
     */
    @ApiModelProperty(value = "血肌酐")
    private Double serumCreatinine;
    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    @Length(max = 255, message = "性别长度不能超过255")
    private String gender;
    /**
     * 会员等级
     */
    @ApiModelProperty(value = "会员等级")
    @Length(max = 255, message = "会员等级长度不能超过255")
    private String membershipLevel;
    /**
     * 到期时间
     */
    @ApiModelProperty(value = "到期时间")
    private LocalDateTime expirationDate;
}
