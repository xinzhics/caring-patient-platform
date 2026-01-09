package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.cms.constant.SitePlateType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 建站组件的板块表
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
@TableName("t_build_site_module_plate")
@ApiModel(value = "SiteModulePlate", description = "建站组件的板块表")
@AllArgsConstructor
public class SiteModulePlate extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件夹ID")
    @TableField("folder_id")
    private Long folderId;

    @ApiModelProperty(value = "页面ID")
    @TableField("page_id")
    private Long pageId;

    @ApiModelProperty(value = "组件ID")
    @TableField("module_id")
    private Long moduleId;

    /**
     * 组件标签ID
     */
    @ApiModelProperty(value = "组件标签ID")
    @TableField("module_label_id")
    @Excel(name = "组件标签ID")
    private Long moduleLabelId;

    /**
     * 板块类型(图片，文章，视频)
     */
    @ApiModelProperty(value = "板块类型(图片，文章，视频)")
    @Length(max = 100, message = "板块类型(图片，文章，视频)长度不能超过100")
    @TableField(value = "plate_type", condition = LIKE)
    @Excel(name = "板块类型(图片，文章，视频)")
    @NotNull
    private SitePlateType plateType;


    @ApiModelProperty(value = "文章ID或视频ID或图片库文件ID")
    @TableField(value = "plate_content_id", condition = EQUAL)
    private Long plateContentId;

    @ApiModelProperty(value = "文章的摘要")
    @TableField(value = "cms_summary")
    private String cmsSummary;

    /**
     * 板块顺序
     */
    @ApiModelProperty(value = "板块顺序")
    @TableField("plate_sort_index")
    @Excel(name = "板块顺序")
    private Integer plateSortIndex;

    /**
     * 图片，文章视频封面地址
     */
    @ApiModelProperty(value = "图片，文章视频封面地址")
    @Length(max = 200, message = "图片，文章视频封面地址长度不能超过200")
    @TableField(value = "image_url", condition = LIKE)
    @Excel(name = "图片，文章视频封面地址")
    private String imageUrl;

    /**
     * 文章或视频的标题
     */
    @ApiModelProperty(value = "文章或视频的标题")
    @Length(max = 200, message = "文章或视频的标题长度不能超过200")
    @TableField(value = "plate_title", condition = LIKE)
    @Excel(name = "文章或视频的标题")
    private String plateTitle;

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

    @ApiModelProperty(value = "视频播放地址")
    @TableField(value = "video_url", condition = LIKE)
    private String videoUrl;

    @ApiModelProperty(value = "板块的跳转设置")
    @TableField(exist = false)
    private SiteJumpInformation jumpInformation;

    @ApiModelProperty(value = "文章视频展示的时间")
    @TableField(exist = false)
    private LocalDateTime showTime;

    @ApiModelProperty(value = "收藏数量")
    @TableField(exist = false)
    private Integer collectNumber;

    @ApiModelProperty(value = "点击浏览量")
    @TableField(exist = false)
    private Long clickNumber;

    @ApiModelProperty(value = "评论留言数量")
    @TableField(exist = false)
    private Integer commentNumber;

    public static void sort(List<SiteModulePlate> modulePlates) {
        if (CollUtil.isEmpty(modulePlates)) {
            return;
        }
        modulePlates.sort((o1, o2) -> {
            if (Objects.isNull(o1.getPlateSortIndex()) || Objects.isNull(o2.getPlateSortIndex())) {
                return 0;
            }
            return o1.getPlateSortIndex() > o2.getPlateSortIndex() ? 1 : -1;
        });
    }

}
