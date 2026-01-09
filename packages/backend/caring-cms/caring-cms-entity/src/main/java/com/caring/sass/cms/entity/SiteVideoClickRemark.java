package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 视频点击播放
 * </p>
 *
 * @author 杨帅
 * @since 2023-11-07
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_build_site_video_click_remark")
@ApiModel(value = "SiteVideoClickRemark", description = "视频点击播放")
@AllArgsConstructor
public class SiteVideoClickRemark extends SuperEntity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 微信openId
     */
    @ApiModelProperty(value = "微信openId")
    @NotEmpty(message = "微信openId不能为空")
    @Length(max = 40, message = "微信openId长度不能超过40")
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "微信openId")
    private String openId;


    /**
     * 视频id
     */
    @ApiModelProperty(value = "视频id")
    @NotNull(message = "视频id不能为空")
    @TableField("video_id")
    @Excel(name = "视频id")
    private Long videoId;


    @Builder
    public SiteVideoClickRemark(Long id, LocalDateTime createTime, Long createUser, 
                    String openId, Long userId, Long videoId, String roleType) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.openId = openId;
        this.videoId = videoId;
    }

}
