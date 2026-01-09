package com.caring.sass.cms.dto;

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
 * 医生订阅最新文章推送表
 * </p>
 *
 * @author 杨帅
 * @since 2023-09-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "DoctorLastPullPageDTO", description = "医生订阅最新文章推送表")
public class DoctorLastPullPageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID")
    @NotNull(message = "医生ID不能为空")
    private Long doctorId;
    /**
     * 文章ID
     */
    @ApiModelProperty(value = "文章ID")
    @NotNull(message = "文章ID不能为空")
    private Long cmsContentId;

}
