package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.mybaits.EncryptedStringTypeHandler;
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
 * Ai用户能量豆账户
 * </p>
 *
 * @author 杨帅
 * @since 2025-09-12
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "m_ai_article_user_account", autoResultMap = true)
@ApiModel(value = "ArticleUserAccount", description = "Ai用户能量豆账户")
@AllArgsConstructor
public class ArticleUserAccount extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 100, message = "用户手机号长度不能超过100")
    @TableField(value = "user_mobile", condition = LIKE, typeHandler = EncryptedStringTypeHandler.class)
    @Excel(name = "用户手机号")
    private String userMobile;

    /**
     * 会员能量豆
     */
    @ApiModelProperty(value = "会员能量豆")
    @TableField("energy_beans")
    @Excel(name = "会员能量豆")
    private Long energyBeans;

    /**
     * 注册用户赠送的20能量豆
     */
    @ApiModelProperty(value = "注册用户赠送的20能量豆")
    @TableField("free_energy_beans")
    @Excel(name = "注册用户赠送的20能量豆")
    private Long freeEnergyBeans;


    @Builder
    public ArticleUserAccount(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String userMobile, Long energyBeans, Long freeEnergyBeans) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userMobile = userMobile;
        this.energyBeans = energyBeans;
        this.freeEnergyBeans = freeEnergyBeans;
    }

}
