package com.caring.sass.file.entity;

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
 * 我的图片
 * </p>
 *
 * @author 杨帅
 * @since 2022-08-29
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("f_file_my")
@ApiModel(value = "FileMy", description = "我的图片")
@Builder
@AllArgsConstructor
public class FileMy extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    @ApiModelProperty(value = "文件id")
    @NotNull(message = "文件id不能为空")
    @TableField("file_id")
    @Excel(name = "文件id")
    private Long fileId;

    /**
     * 图片分组ID
     */
    @ApiModelProperty(value = "图片分组ID")
    @TableField("file_classification_id")
    @Excel(name = "图片分组ID")
    private Long fileClassificationId;

    @ApiModelProperty(value = "文件名称")
    @TableField("file_name")
    private String fileName;



}
