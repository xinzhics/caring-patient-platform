package com.caring.sass.ai.dto.know;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.ai.entity.know.MembershipLevel;
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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

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
@ApiModel(value = "KnowledgeUserPageDTO", description = "知识库用户")
public class KnowledgeUserPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    @Length(max = 20, message = "用户名称长度不能超过20")
    private String userName;
    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 300, message = "用户手机号长度不能超过300")
    private String userMobile;

    @ApiModelProperty(value = "科室")
    private String department;

    @ApiModelProperty(value = "菜单ID")
    private String knowledgeMenuId;

    @ApiModelProperty(value = "专科域名")
    private String menuDomain;

    @ApiModelProperty(value = "职称")
    private String doctorTitle;


    @ApiModelProperty(value = "用户类型 会员等级")
    private MembershipLevel membershipLevel;



    /**
     * 租户(一个主任一个租户)
     */
    @ApiModelProperty(value = "租户(一个主任一个租户)")
    @Length(max = 50, message = "租户(一个主任一个租户)长度不能超过50")
    private String userDomain;



}
