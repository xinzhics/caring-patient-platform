package com.caring.sass.ai.dto.ckd;

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
 * ckd会员订单
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CkdUserOrderSaveDTO", description = "ckd会员订单")
public class CkdUserOrderSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会员版本 9.9， 99， 199
     */
    @ApiModelProperty(value = "会员版本 member9_9， member99， member199")
    private CkdUserGoodsType goodsType;
    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 40, message = "openId长度不能超过40")
    private String openId;

}
