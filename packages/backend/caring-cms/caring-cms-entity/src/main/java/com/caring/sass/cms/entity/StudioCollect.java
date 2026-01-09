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
 * 医生CMS收藏记录
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
@TableName("t_cms_studio_collect")
@ApiModel(value = "StudioCollect", description = "医生CMS收藏记录")
@AllArgsConstructor
public class StudioCollect extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 内容id
     */
    @ApiModelProperty(value = "内容id")
    @NotNull(message = "内容id不能为空")
    @TableField("cms_id")
    @Excel(name = "内容id")
    private Long cmsId;

    /**
     * 收藏状态1收藏0取消
     */
    @ApiModelProperty(value = "收藏状态1收藏0取消")
    @TableField("collect_status")
    @Excel(name = "收藏状态1收藏0取消")
    private Integer collectStatus;

    @ApiModelProperty(value = "收藏的cms信息")
    @TableField(exist = false)
    private StudioCms studioCms;


    @Builder
    public StudioCollect(Long id, LocalDateTime createTime, Long createUser, 
                    Long userId, Long cmsId, Integer collectStatus) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.userId = userId;
        this.cmsId = cmsId;
        this.collectStatus = collectStatus;
    }

}
