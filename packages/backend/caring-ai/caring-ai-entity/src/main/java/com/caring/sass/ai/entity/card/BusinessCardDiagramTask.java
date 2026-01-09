package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.common.constant.GenderType;
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
 * 用户名片任务
 * </p>
 *
 * @author 杨帅
 * @since 2025-03-18
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_business_card_diagram_task")
@ApiModel(value = "BusinessCardDiagramTask", description = "用户名片任务")
@AllArgsConstructor
public class BusinessCardDiagramTask extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "用户Id")
    @TableField("user_id")
    @Excel(name = "用户Id")
    private Long userId;

    /**
     * Male 男, Female 女
     */
    @ApiModelProperty(value = "Male 男, Female 女")
    @TableField(value = "gender", condition = LIKE)
    @Excel(name = "Male 男, Female 女")
    private GenderType gender;

    /**
     * 图片的obs地址
     */
    @ApiModelProperty(value = "图片的obs地址")
    @Length(max = 350, message = "图片的obs地址长度不能超过350")
    @TableField(value = "image_obs_url", condition = LIKE)
    @Excel(name = "图片的obs地址")
    private String imageObsUrl;


    @Builder
    public BusinessCardDiagramTask(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long userId, GenderType gender, String imageObsUrl) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.userId = userId;
        this.gender = gender;
        this.imageObsUrl = imageObsUrl;
    }

}
