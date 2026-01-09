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

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 建站组件主题样式表
 * </p>
 *
 * @author 杨帅
 * @since 2023-09-05
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_build_site_module_title_style")
@ApiModel(value = "SiteModuleTitleStyle", description = "建站组件主题样式表")
@AllArgsConstructor
public class SiteModuleTitleStyle extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    @TableField("folder_id")
    private Long folderId;

    @ApiModelProperty(value = "")
    @TableField("page_id")
    private Long pageId;

    @ApiModelProperty(value = "")
    @TableField("module_id")
    private Long moduleId;

    /**
     * 标题类型(主标题，文章标题，文章时间，文章摘要，视频标题，视频描述)
     */
    @ApiModelProperty(value = "标题类型(主标题，文章标题，文章时间，文章摘要，视频标题，视频描述)")
    @Length(max = 50, message = "标题类型(主标题，文章标题，文章时间，文章摘要，视频标题，视频描述)长度不能超过50")
    @TableField(value = "title_type", condition = LIKE)
    @Excel(name = "标题类型(主标题，文章标题，文章时间，文章摘要，视频标题，视频描述)")
    private String titleType;

    /**
     * json格式的样式Style对象
     * {@link com.caring.sass.cms.dto.site.SiteJsonStyleDTO}
     */
    @ApiModelProperty(value = "json格式的样式Style对象")
    @Length(max = 65535, message = "json格式的样式Style对象长度不能超过65535")
    @TableField("title_style")
    @Excel(name = "json格式的样式Style对象")
    private String titleStyle;


    @Builder
    public SiteModuleTitleStyle(Long id, LocalDateTime createTime, Long createUser, LocalDateTime updateTime, Long updateUser, 
                    Long folderId, Long pageId, Long moduleId, String titleType, String titleStyle) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.folderId = folderId;
        this.pageId = pageId;
        this.moduleId = moduleId;
        this.titleType = titleType;
        this.titleStyle = titleStyle;
    }

}
