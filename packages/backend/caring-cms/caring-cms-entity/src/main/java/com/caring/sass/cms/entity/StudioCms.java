package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.cms.constant.StudioCmsType;
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

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 医生cms详情表
 * </p>
 *
 * @author 杨帅
 * @since 2025-11-11
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_studio_cms")
@ApiModel(value = "StudioCms", description = "医生cms详情表")
@AllArgsConstructor
public class StudioCms extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    @Length(max = 150, message = "标题长度不能超过150")
    @TableField(value = "cms_title", condition = LIKE)
    @Excel(name = "标题")
    private String cmsTitle;

    /**
     * cms类型（文章，声音，视频）
     */
    @ApiModelProperty(value = "cms类型（文章，声音，视频）")
    @TableField(value = "cms_type", condition = LIKE)
    private StudioCmsType cmsType;

    /**
     * cms声音视频url
     */
    @ApiModelProperty(value = "cms声音视频url")

    @TableField("cms_file_url")
    @Excel(name = "cms声音视频url")
    private String cmsFileUrl;

    /**
     * cms文章内容
     */
    @ApiModelProperty(value = "cms文章内容")

    @TableField("cms_content")
    @Excel(name = "cms文章内容")
    private String cmsContent;

    /**
     * cms文件标题
     */
    @ApiModelProperty(value = "cms文件标题")
    @TableField("cms_file_title")
    @Excel(name = "cms文件标题")
    private String cmsFileTitle;

    /**
     * 文件大小
     */
    @ApiModelProperty(value = "文件大小")
    @TableField("cms_file_size")
    @Excel(name = "文件大小")
    private Long cmsFileSize;

    /**
     * 文件类型(mp4,avi,mp3...)
     */
    @ApiModelProperty(value = "文件类型(mp4,avi,mp3...)")
    @Length(max = 40, message = "文件类型(mp4,avi,mp3...)长度不能超过40")
    @TableField(value = "cms_file_type", condition = LIKE)
    @Excel(name = "文件类型(mp4,avi,mp3...)")
    private String cmsFileType;

    /**
     * cms文件备注(音频描述)
     */
    @ApiModelProperty(value = "cms文件备注(音频描述)")

    @TableField("cms_file_remark")
    @Excel(name = "cms文件备注(音频描述)")
    private String cmsFileRemark;

    /**
     * 发布状态 0: 草稿， 1: 已发布
     */
    @ApiModelProperty(value = "发布状态 0: 草稿， 1: 已发布")
    @TableField("release_status")
    @Excel(name = "发布状态 0: 草稿， 1: 已发布")
    private Integer releaseStatus;

    /**
     * 置顶排序
     */
    @ApiModelProperty(value = "置顶排序")
    @Length(max = 64, message = "置顶排序长度不能超过64")
    @TableField(value = "pin_to_top_sort", condition = LIKE)
    @Excel(name = "置顶排序")
    private String pinToTopSort;

    /**
     * 创建部门
     */
    @ApiModelProperty(value = "创建部门")
    @TableField("create_dept")
    @Excel(name = "创建部门")
    private Long createDept;

    /**
     * 数据所属医生
     */
    @ApiModelProperty(value = "数据所属医生")
    @TableField("doctor_id")
    @Excel(name = "数据所属医生")
    private Long doctorId;

    /**
     * 置顶状态0 不置顶， 1 置顶
     */
    @ApiModelProperty(value = "置顶状态0 不置顶， 1 置顶")
    @TableField("pin_to_top")
    @Excel(name = "置顶状态0 不置顶， 1 置顶")
    private Integer pinToTop;

    /**
     * 视频封面
     */
    @ApiModelProperty(value = "视频封面")
    @Length(max = 255, message = "视频封面长度不能超过255")
    @TableField(value = "cms_video_cover", condition = LIKE)
    @Excel(name = "视频封面")
    private String cmsVideoCover;

    /**
     * 科普创作数据id
     */
    @ApiModelProperty(value = "科普创作数据id")
    @TableField("article_data_id")
    @Excel(name = "科普创作数据id")
    private Long articleDataId;

    @ApiModelProperty(value = "科普创作数据修改状态 0 未修改 1 已修改")
    @TableField("article_data_update_status")
    private Boolean articleDataUpdateStatus;

    /**
     * 收藏状态 0: 未收藏， 1: 已收藏
     */
    @TableField(exist = false)
    private int collectStatus;


    @Builder
    public StudioCms(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime, 
                    String cmsTitle, StudioCmsType cmsType, String cmsFileUrl, String cmsContent, String cmsFileTitle,
                    Long cmsFileSize, String cmsFileType, String cmsFileRemark, Integer releaseStatus, String pinToTopSort, Long createDept, 
                    Long doctorId, Integer pinToTop, String cmsVideoCover, Long articleDataId) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.cmsTitle = cmsTitle;
        this.cmsType = cmsType;
        this.cmsFileUrl = cmsFileUrl;
        this.cmsContent = cmsContent;
        this.cmsFileTitle = cmsFileTitle;
        this.cmsFileSize = cmsFileSize;
        this.cmsFileType = cmsFileType;
        this.cmsFileRemark = cmsFileRemark;
        this.releaseStatus = releaseStatus;
        this.pinToTopSort = pinToTopSort;
        this.createDept = createDept;
        this.doctorId = doctorId;
        this.pinToTop = pinToTop;
        this.cmsVideoCover = cmsVideoCover;
        this.articleDataId = articleDataId;
    }


}
