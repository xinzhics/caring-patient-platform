package com.caring.sass.tenant.entity.router;

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
 * 患者个人中心核心功能
 * </p>
 *
 * @author 杨帅
 * @since 2023-06-27
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_h5_core_functions")
@ApiModel(value = "H5CoreFunctions", description = "患者个人中心核心功能")
@AllArgsConstructor
public class H5CoreFunctions extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 1展示：0 隐藏
     */
    @ApiModelProperty(value = "1展示：0 隐藏")
    @TableField("calendar_status")
    @Excel(name = "1展示：0 隐藏")
    private Integer calendarStatus;

    /**
     * 在线咨询显示文案
     */
    @ApiModelProperty(value = "在线咨询显示文案")
    @Length(max = 300, message = "在线咨询显示文案长度不能超过300")
    @TableField(value = "im_copywriting", condition = LIKE)
    @Excel(name = "在线咨询显示文案")
    private String imCopywriting;

    /**
     * 在线咨询按钮文本
     */
    @ApiModelProperty(value = "在线咨询按钮文本")
    @Length(max = 255, message = "在线咨询按钮文本长度不能超过255")
    @TableField(value = "im_button_text", condition = LIKE)
    @Excel(name = "在线咨询按钮文本")
    private String imButtonText;

    /**
     * 在线咨询按钮风格
     */
    @ApiModelProperty(value = "在线咨询按钮风格左侧颜色")
    @Length(max = 50, message = "在线咨询按钮风格长度不能超过50")
    @TableField(value = "im_button_style_left", condition = LIKE)
    @Excel(name = "在线咨询按钮风格")
    private String imButtonStyleLeft;

    @ApiModelProperty(value = "在线咨询按钮风格右侧颜色")
    @Length(max = 50, message = "在线咨询按钮风格长度不能超过50")
    @TableField(value = "im_button_style_right", condition = LIKE)
    @Excel(name = "在线咨询按钮风格")
    private String imButtonStyleRight;

    /**
     * 1 展示:  0 隐藏
     */
    @Deprecated
    @ApiModelProperty(value = "1 展示:  0 隐藏")
    @TableField("im_status")
    @Excel(name = "1 展示:  0 隐藏")
    private Integer imStatus;


    public static H5CoreFunctions init(Boolean imMenuStatus) {
        H5CoreFunctions coreFunctions = new H5CoreFunctions();
        coreFunctions.setImCopywriting("有问题找医生询问~");
        coreFunctions.setImButtonText("去咨询");
        coreFunctions.setImButtonStyleLeft("#5AE6C8");
        coreFunctions.setImButtonStyleRight("#2CCDBF");
        if (imMenuStatus == null) {
            coreFunctions.setImStatus(0);
            coreFunctions.setCalendarStatus(1);
        } else {
            coreFunctions.setImStatus(imMenuStatus ? 1 : 0);
            coreFunctions.setCalendarStatus(1);
        }
        return coreFunctions;
    }


}
