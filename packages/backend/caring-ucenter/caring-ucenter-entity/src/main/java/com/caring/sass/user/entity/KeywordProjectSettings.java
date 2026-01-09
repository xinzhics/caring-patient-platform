package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
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
 * 项目关键字开关配置
 * </p>
 *
 * @author yangshuai
 * @since 2022-07-06
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_keyword_project_settings")
@ApiModel(value = "KeywordProjectSettings", description = "项目关键字开关配置")
@AllArgsConstructor
public class KeywordProjectSettings extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 快捷回复开关
     */
    @ApiModelProperty(value = "快捷回复开关功能开关(open, close)")
    @Length(max = 255, message = "快捷回复开关长度不能超过255")
    @TableField(value = "keyword_reply_switch", condition = LIKE)
    @Excel(name = "快捷回复开关")
    private String keywordReplySwitch;

    /**
     * 快捷回复形式
     */
    @ApiModelProperty(value = "回复形式(system，medical_assistance 医助)")
    @Length(max = 255, message = "快捷回复形式长度不能超过255")
    @TableField(value = "keyword_reply_form", condition = LIKE)
    @Excel(name = "快捷回复形式")
    private String keywordReplyForm;


    @Builder
    public KeywordProjectSettings(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String keywordReplySwitch, String keywordReplyForm) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.keywordReplySwitch = keywordReplySwitch;
        this.keywordReplyForm = keywordReplyForm;
    }

}
