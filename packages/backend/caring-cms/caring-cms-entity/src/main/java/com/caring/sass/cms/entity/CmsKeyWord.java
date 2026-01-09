package com.caring.sass.cms.entity;

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
 * 关键词表
 * </p>
 *
 * @author 杨帅
 * @since 2023-09-14
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_key_word")
@ApiModel(value = "KeyWord", description = "关键词表")
@AllArgsConstructor
public class CmsKeyWord extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "key_word", condition = LIKE)
    @Excel(name = "")
    private String keyWord;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级")
    @TableField("order_")
    @Excel(name = "优先级")
    private Integer order;

    @ApiModelProperty(value = "文章ID")
    @TableField("cms_id")
    private Long cmsId;


    @ApiModelProperty(value = "显示状态0 , 1")
    @TableField("show_status")
    private Integer showStatus;


    @ApiModelProperty(value = "医生订阅状态")
    @TableField(exist = false)
    private Boolean subscribe;


}
