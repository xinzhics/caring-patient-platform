package com.caring.sass.user.entity;

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
 * 医生的自定义小组
 * </p>
 *
 * @author yangshuai
 * @since 2022-08-11
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("u_user_doctor_custom_group")
@ApiModel(value = "DoctorCustomGroup", description = "医生的自定义小组")
@AllArgsConstructor
public class DoctorCustomGroup extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @TableField("doctor_id")
    @Excel(name = "医生ID")
    private Long doctorId;

    /**
     * 小组名称
     */
    @ApiModelProperty(value = "小组名称")
    @Length(max = 300, message = "小组名称长度不能超过300")
    @TableField(value = "group_name", condition = LIKE)
    @Excel(name = "小组名称")
    private String groupName;

    /**
     * 小组人员数量
     */
    @ApiModelProperty(value = "小组人员数量")
    @TableField("group_number_total")
    @Excel(name = "小组人员数量")
    private Integer groupNumberTotal;


    @Builder
    public DoctorCustomGroup(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long doctorId, String groupName, Integer groupNumberTotal) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.doctorId = doctorId;
        this.groupName = groupName;
        this.groupNumberTotal = groupNumberTotal;
    }

}
