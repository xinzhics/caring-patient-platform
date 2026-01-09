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
 * 医生订阅最新文章推送表
 * </p>
 *
 * @author 杨帅
 * @since 2023-09-14
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cms_doctor_last_pull")
@ApiModel(value = "DoctorLastPull", description = "医生订阅最新文章推送表")
@AllArgsConstructor
public class DoctorLastPull extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @NotNull(message = "医生ID不能为空")
    @TableField("doctor_id")
    @Excel(name = "医生ID")
    private Long doctorId;

    /**
     * 文章ID
     */
    @ApiModelProperty(value = "文章ID")
    @NotNull(message = "文章ID不能为空")
    @TableField("cms_content_id")
    @Excel(name = "文章ID")
    private Long cmsContentId;


    @Builder
    public DoctorLastPull(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    Long doctorId, Long cmsContentId) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.doctorId = doctorId;
        this.cmsContentId = cmsContentId;
    }

}
