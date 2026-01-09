package com.caring.sass.ai.entity.ckd;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * CKD用户coze的token
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-17
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_ckd_user_coze_token")
@ApiModel(value = "CkdUserCozeToken", description = "CKD用户coze的token")
@AllArgsConstructor
public class CkdUserCozeToken extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 100, message = "openId长度不能超过100")
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "openId")
    private String openId;

    /**
     * appId
     */
    @ApiModelProperty(value = "appId")
    @Length(max = 100, message = "appId长度不能超过100")
    @TableField(value = "app_id", condition = LIKE)
    @Excel(name = "appId")
    private String appId;

    /**
     * OAuth 应用的钥指纹
     */
    @ApiModelProperty(value = "OAuth 应用的钥指纹")
    @Length(max = 200, message = "OAuth 应用的钥指纹长度不能超过200")
    @TableField(value = "kid", condition = LIKE)
    @Excel(name = "OAuth 应用的钥指纹")
    private String kid;

    /**
     * OAuth 应用的钥指纹
     */
    @ApiModelProperty(value = "OAuth 应用的钥指纹")
    @Length(max = 200, message = "OAuth 应用的钥指纹长度不能超过200")
    @TableField(value = "access_token", condition = LIKE)
    @Excel(name = "OAuth 应用的钥指纹")
    private String accessToken;

    /**
     * 访问令牌的会话标识
     */
    @ApiModelProperty(value = "访问令牌的会话标识")
    @Length(max = 200, message = "访问令牌的会话标识长度不能超过200")
    @TableField(value = "session_name", condition = LIKE)
    @Excel(name = "访问令牌的会话标识")
    private String sessionName;

    /**
     * 过期时间
     */
    @ApiModelProperty(value = "过期时间")
    @TableField("expires_time")
    @Excel(name = "过期时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime expiresTime;


    @Builder
    public CkdUserCozeToken(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    String openId, String appId, String kid, String accessToken, String sessionName, LocalDateTime expiresTime) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.openId = openId;
        this.appId = appId;
        this.kid = kid;
        this.accessToken = accessToken;
        this.sessionName = sessionName;
        this.expiresTime = expiresTime;
    }

}
