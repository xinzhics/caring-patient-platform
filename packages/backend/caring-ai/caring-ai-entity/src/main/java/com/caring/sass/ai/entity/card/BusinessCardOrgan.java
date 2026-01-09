package com.caring.sass.ai.entity.card;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;

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
 * 科普名片组织
 * </p>
 *
 * @author 杨帅
 * @since 2024-11-26
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_business_card_organ")
@ApiModel(value = "BusinessCardOrgan", description = "科普名片组织")
@AllArgsConstructor
public class BusinessCardOrgan extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 机构名称
     */
    @ApiModelProperty(value = "机构名称")
    @NotEmpty(message = "机构名称不能为空")
    @Length(max = 255, message = "机构名称长度不能超过255")
    @TableField(value = "organ_name", condition = LIKE)
    @Excel(name = "机构名称")
    private String organName;


    @Builder
    public BusinessCardOrgan(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String organName) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.organName = organName;
    }

}
