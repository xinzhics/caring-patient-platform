package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 建站文件夹表
 * </p>
 *
 * @author 杨帅
 * @since 2023-09-05
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_build_site_folder")
@ApiModel(value = "SiteFolder", description = "建站文件夹表")
@AllArgsConstructor
public class SiteFolder extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static String DEFAULT_FOLDER_NAME = "未命名文件";

    /**
     * 文件夹名称
     */
    @ApiModelProperty(value = "文件夹名称")
    @Length(max = 50, message = "文件夹名称长度不能超过50")
    @TableField(value = "folder_name", condition = LIKE)
    @Excel(name = "文件夹名称")
    private String folderName;

    /**
     * 是否为模板(0 不是， 1 是)
     * {@link com.caring.sass.common.constant.CommonStatus}
     */
    @ApiModelProperty(value = "是否为模板(0 不是， 1 是)")
    @TableField("template")
    @Excel(name = "是否为模板(0 不是， 1 是)")
    private Integer template;

    /**
     * 复制次数
     */
    @ApiModelProperty(value = "复制次数")
    @TableField("copy_number")
    @Excel(name = "复制次数")
    private Integer copyNumber;

    /**
     * 复制品（0, 1）
     * {@link com.caring.sass.common.constant.CommonStatus}
     */
    @ApiModelProperty(value = "复制品（0, 1）")
    @TableField("replica_")
    @Excel(name = "复制品（0, 1）")
    private Integer replica;

    @ApiModelProperty(value = "删除状态（0, 1）")
    @TableField(value = "delete_status", condition = EQUAL)
    private Integer deleteStatus;


    @ApiModelProperty(value = "可以分享0, 1")
    @TableField(value = "can_share", condition = EQUAL)
    private Integer canShare;

    @ApiModelProperty(value = "分享异常的原因")
    @TableField(value = "share_error_message")
    private String shareErrorMessage;

    @ApiModelProperty(value = "删除时间")
    @TableField("delete_time")
    private LocalDateTime deleteTime;

    @ApiModelProperty(value = "文件夹名称拼音")
    @Length(max = 50, message = "文件夹名称拼音长度不能超过50")
    @TableField(value = "folder_name_pinyin", condition = LIKE)
    @Excel(name = "文件夹名称")
    private String folderNamePinyin;

    @ApiModelProperty(value = "名称首字母")
    @TableField(value = "name_first_letter", condition = EQUAL)
    @Excel(name = "名称首字母")
    private String nameFirstLetter;

    @ApiModelProperty(value = "立即删除剩余天数")
    @TableField(value = "delete_day", condition = EQUAL)
    @Excel(name = "立即删除剩余天数")
    private Integer deleteDay;

    @ApiModelProperty(value = "租户")
    @TableField(exist = false)
    private String tenantCode;

    @ApiModelProperty(value = "文件夹中的页面")
    @TableField(exist = false)
    private List<SiteFolderPage> siteFolderPageList;

    @TableField(exist = false)
    private String updateUserName;


}
