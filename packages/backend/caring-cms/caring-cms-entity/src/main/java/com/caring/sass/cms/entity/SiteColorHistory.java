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
 * 建站最近使用颜色
 * </p>
 *
 * @author 杨帅
 * @since 2023-11-27
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_build_site_color_history")
@ApiModel(value = "SiteColorHistory", description = "建站最近使用颜色")
@AllArgsConstructor
public class SiteColorHistory extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 颜色
     */
    @ApiModelProperty(value = "颜色")
    @Length(max = 50, message = "颜色长度不能超过50")
    @TableField(value = "color", condition = LIKE)
    @Excel(name = "颜色")
    private String color;


    @Builder
    public SiteColorHistory(Long id, LocalDateTime createTime, Long createUser, LocalDateTime updateTime, Long updateUser, 
                    String color) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.color = color;
    }

}
