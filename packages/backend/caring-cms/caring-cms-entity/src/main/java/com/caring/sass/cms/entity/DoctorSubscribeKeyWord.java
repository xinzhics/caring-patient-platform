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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 医生订阅关键词表
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
@TableName("t_cms_doctor_subscribe_key_word")
@ApiModel(value = "DoctorSubscribeKeyWord", description = "医生订阅关键词表")
@AllArgsConstructor
public class DoctorSubscribeKeyWord extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @NotNull(message = "医生ID不能为空")
    @TableField("doctor_id")
    @Excel(name = "医生ID")
    private Long doctorId;

    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "key_word", condition = EQUAL)
    private Long keyWordId;



}
