package com.caring.sass.ai.dto.ckd;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDate;
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
@ApiModel(value = "CkdUserInfoSaveDTO", description = "CKD用户信息")
public class CkdUserInfoSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @NotEmpty(message = "openId不能为空")
    @Length(max = 100, message = "openId长度不能超过100")
    private String openId;

    @ApiModelProperty(value = "昵称")
    @Length(max = 200, message = "长度不能超过200")
    private String nickname;

    @ApiModelProperty(value = "头像")
    @Length(max = 400, message = "长度不能超过400")
    private String headImgUrl;
    /**
     * 体重
     */
    @ApiModelProperty(value = "体重")
    @NotNull
    private Double userWeight;
    /**
     * 身高
     */
    @ApiModelProperty(value = "身高")
    @NotNull
    private Double userHeight;
    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    @NotNull
    private LocalDate dateOfBirth;

    @ApiModelProperty(value = "是否高血压或高血压肾病")
    private Boolean hypertension;
    /**
     * 血肌酐
     */
    @ApiModelProperty(value = "血肌酐")
    private Double serumCreatinine;
    /**
     * 性别
     */
    @ApiModelProperty(value = "性别 male  female")
    @Length(max = 255, message = "性别长度不能超过255")
    @NotNull
    private String gender;

}
