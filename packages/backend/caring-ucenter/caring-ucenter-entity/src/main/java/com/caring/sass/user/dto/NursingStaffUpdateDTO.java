package com.caring.sass.user.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
import java.time.LocalDate;

/**
 * <p>
 * 实体类
 * 用户-医助
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
@ApiModel(value = "NursingStaffUpdateDTO", description = "用户-医助")
public class NursingStaffUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    @Length(max = 50, message = "用户名长度不能超过50")
    private String loginName;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @Length(max = 100, message = "密码长度不能超过100")
    private String password;
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @Length(max = 100, message = "姓名长度不能超过100")
    private String name;
    /**
     * 头像url
     */
    @ApiModelProperty(value = "头像url")
    @Length(max = 500, message = "头像url长度不能超过500")
    private String avatar;
    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    @Length(max = 20, message = "手机号码长度不能超过20")
    private String mobile;
    /**
     * 手势密码
     */
    @ApiModelProperty(value = "手势密码")
    @Length(max = 100, message = "手势密码长度不能超过100")
    private String gesturePwd;
    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    @Length(max = 20, message = "身份证号长度不能超过20")
    private String certificate;
    /**
     *  所属机构代码
     */
    @ApiModelProperty(value = " 所属机构代码")
    @Length(max = 50, message = " 所属机构代码长度不能超过50")
    private String organCode;
    /**
     * 性别 0:男 1：女
     */
    @ApiModelProperty(value = "性别 0:男 1：女")
    private Integer sex;
    /**
     * 出生年月
     */
    @ApiModelProperty(value = "出生年月")
    @Length(max = 32, message = "出生年月长度不能超过32")
    private String birthday;

    @ApiModelProperty(value = "是否展开聊天小组, 0 关闭，1展示")
    private Integer imGroupStatus;


    @ApiModelProperty(value = "最新访问日期")
    private LocalDate latestAccessTime;
}
