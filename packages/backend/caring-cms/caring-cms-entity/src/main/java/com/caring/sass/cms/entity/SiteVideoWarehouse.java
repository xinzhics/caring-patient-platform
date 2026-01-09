package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
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
 * 建站视频库
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
@TableName("t_build_site_video_warehouse")
@ApiModel(value = "SiteVideoWarehouse", description = "建站视频库")
@AllArgsConstructor
public class SiteVideoWarehouse extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    @Length(max = 300, message = "长度不能超过300")
    @TableField(value = "video_url", condition = LIKE)
    private String videoUrl;

    @ApiModelProperty(value = "")
    @Length(max = 50, message = "长度不能超过50")
    @TableField(value = "video_title", condition = LIKE)
    private String videoTitle;

    /**
     * 视频时长
     */
    @ApiModelProperty(value = "视频时长")
    @TableField("video_duration")
    @Excel(name = "视频时长")
    private Float videoDuration;

    /**
     * 视频大小(kb)
     */
    @ApiModelProperty(value = "视频大小(kb)")
    @TableField("video_file_size")
    @Excel(name = "视频大小(kb)")
    private Float videoFileSize;

    /**
     * 视频封面
     */
    @ApiModelProperty(value = "视频封面")
    @Length(max = 200, message = "视频封面长度不能超过200")
    @TableField(value = "video_cover", condition = LIKE)
    @Excel(name = "视频封面")
    private String videoCover;

    @ApiModelProperty(value = "obs视频对象名称")
    @TableField(value = "obs_file_name", condition = EQUAL)
    private String obsFileName;

    @ApiModelProperty(value = "视频描述")
    @Length(max = 120, message = "视频描述长度不能超过120")
    @TableField(value = "video_desc", condition = EQUAL)
    private String videoDesc;

    @ApiModelProperty(value = "视频名称拼音")
    @TableField(value = "title_pinyin", condition = LIKE)
    private String titlePinyin;

    @ApiModelProperty(value = "视频名首字母")
    @TableField(value = "title_first_letter", condition = EQUAL)
    @Excel(name = "名称首字母")
    private String titleFirstLetter;

    @ApiModelProperty(value = "浏览次数")
    @TableField(value = "number_views")
    private Integer numberViews;


    @ApiModelProperty(value = "留言次数")
    @TableField(value = "message_num")
    private Integer messageNum;

    @ApiModelProperty(value = "删除标记")
    @TableField(value = "delete_mark", condition = EQUAL)
    private Integer deleteMark;



}
