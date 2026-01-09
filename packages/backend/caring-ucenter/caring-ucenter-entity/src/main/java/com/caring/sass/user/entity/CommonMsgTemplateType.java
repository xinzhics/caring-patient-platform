package com.caring.sass.user.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 常用语模板分类
 * </p>
 *
 * @author 杨帅
 * @since 2023-05-08
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_common_msg_template_type")
@ApiModel(value = "CommonMsgTemplateType", description = "常用语模板分类")
@AllArgsConstructor
public class CommonMsgTemplateType extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "名称")
    @TableField(value = "type_name", condition = LIKE)
    private String typeName;

    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    @TableField("type_sort")
    @Excel(name = "序号")
    private Integer typeSort;

    /**
     * 用户类型 NursingStaff, doctor
     */
    @ApiModelProperty(value = "用户类型 NursingStaff, doctor")
    @Length(max = 255, message = "用户类型 NursingStaff, doctor长度不能超过255")
    @TableField(value = "user_type", condition = EQUAL)
    @Excel(name = "用户类型 NursingStaff, doctor")
    private String userType;


    @Builder
    public CommonMsgTemplateType(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String typeName, Integer typeSort, String userType) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.typeName = typeName;
        this.typeSort = typeSort;
        this.userType = userType;
    }

}
