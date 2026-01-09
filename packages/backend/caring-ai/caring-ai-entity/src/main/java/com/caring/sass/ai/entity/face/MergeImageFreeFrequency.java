package com.caring.sass.ai.entity.face;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 实体类
 * 融合图片免费次数
 * </p>
 *
 * @author 杨帅
 * @since 2024-06-21
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_megvii_merge_image_free_frequency")
@ApiModel(value = "MergeImageFreeFrequency", description = "融合图片免费次数")
@AllArgsConstructor
public class MergeImageFreeFrequency extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "剩余免费次数")
    @TableField("free_merge_total")
    private Integer freeMergeTotal;


    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private Long userId;

    @TableField(exist = false)
    @ApiModelProperty(value = "合成图片的费用，单位分")
    private int mergeImageCost;

}
