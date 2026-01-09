package com.caring.sass.ai.entity.card;

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
 * 医生名片头像模板
 * </p>
 *
 * @author 杨帅
 * @since 2024-09-10
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_business_card_diagram")
@ApiModel(value = "BusinessCardDiagram", description = "医生名片头像模板")
@AllArgsConstructor
public class BusinessCardDiagram extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 优先级
     */
    @ApiModelProperty(value = "优先级")
    @TableField("order_")
    @Excel(name = "优先级")
    private Integer order;

    /**
     * Male 男, Female 女
     */
    @ApiModelProperty(value = "Male 男, Female 女")
    @Length(max = 255, message = "Male 男, Female 女长度不能超过255")
    @TableField(value = "gender", condition = LIKE)
    @Excel(name = "Male 男, Female 女")
    private String gender;

    /**
     * 图片的obs地址
     */
    @ApiModelProperty(value = "图片的obs地址")
    @Length(max = 350, message = "图片的obs地址长度不能超过350")
    @TableField(value = "image_obs_url", condition = LIKE)
    @Excel(name = "图片的obs地址")
    private String imageObsUrl;


    @Builder
    public BusinessCardDiagram(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Integer order, String gender, String imageObsUrl) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.order = order;
        this.gender = gender;
        this.imageObsUrl = imageObsUrl;
    }

}
