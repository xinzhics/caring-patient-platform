package com.caring.sass.cms.dto.site;

import java.time.LocalDateTime;
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
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.caring.sass.common.constant.DictionaryType;
import java.io.Serializable;

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
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "SiteVideoClickRemarkPageDTO", description = "视频点击播放")
public class SiteVideoClickRemarkPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 微信openId
     */
    @ApiModelProperty(value = "微信openId")
    @NotEmpty(message = "微信openId不能为空")
    @Length(max = 40, message = "微信openId长度不能超过40")
    private String openId;

    /**
     * 视频id
     */
    @ApiModelProperty(value = "视频id")
    @NotNull(message = "视频id不能为空")
    private Long videoId;

}
