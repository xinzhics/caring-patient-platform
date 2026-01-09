package com.caring.sass.ai.entity.article;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.ai.entity.textual.TextualConsumptionType;
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
 * 能量豆明细关联表
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
@TableName(value = "m_ai_article_account_consumption", autoResultMap = true)
@ApiModel(value = "ArticleAccountConsumption", description = "能量豆明细关联表")
@AllArgsConstructor
public class ArticleAccountConsumption extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static String sourceTypeArticle = "article";
    public static String sourceTypeTextual = "textual";

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @Length(max = 100, message = "用户手机号长度不能超过100")
    @TableField(value = "user_mobile", condition = LIKE, typeHandler = EncryptedStringTypeHandler.class)
    @Excel(name = "用户手机号")
    private String userMobile;

    /**
     * 能量豆明细类型 article, textual
     */
    @ApiModelProperty(value = "能量豆明细类型 article, textual")
    @Length(max = 50, message = "能量豆明细类型 article, textual长度不能超过50")
    @TableField(value = "source_type", condition = LIKE)
    @Excel(name = "能量豆明细类型 article, textual")
    private String sourceType;

    /**
     * 能量豆明细ID
     */
    @ApiModelProperty(value = "能量豆明细ID")
    @TableField("consumption_id")
    @Excel(name = "能量豆明细ID")
    private Long consumptionId;


    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    @TableField(exist = false)
    private Long userId;

    /**
     * 消费类型
     */
    @ApiModelProperty(value = "消费类型")
    @TableField(exist = false)
    private Enum<?> consumptionType;

    /**
     * 消耗能量币数量
     */
    @ApiModelProperty(value = "消耗能量币数量")
    @TableField(exist = false)
    private Integer consumptionAmount;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @TableField(exist = false)
    private String messageRemark;



    @Builder
    public ArticleAccountConsumption(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String userMobile, String sourceType, Long consumptionId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userMobile = userMobile;
        this.sourceType = sourceType;
        this.consumptionId = consumptionId;
    }

}
