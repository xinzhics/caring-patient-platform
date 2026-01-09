package com.caring.sass.tenant.dto.router;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 患者路由
 * </p>
 *
 * @author leizhi
 * @since 2021-03-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "H5RouterSaveDTO", description = "患者路由")
public class H5RouterSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "名称不能为空")
    @Length(max = 64, message = "名称长度不能超过64")
    private String name;

    /**
     * 显示状态
     */
    @ApiModelProperty(value = "显示状态")
    private Boolean status;

    /**
     * 禁止删除
     */
    @ApiModelProperty(value = "禁止删除")
    private Boolean banDelete;

    /**
     * icon地址
     */
    @ApiModelProperty(value = "icon地址")
    @Length(max = 255, message = "icon地址长度不能超过255")
    private String iconUrl;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sortValue;
    /**
     * 类型ID
     */
    @ApiModelProperty(value = "类型ID")
    @NotNull(message = "类型ID不能为空")
    private Long dictItemId;
    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    @NotEmpty(message = "类型不能为空")
    @Length(max = 64, message = "类型长度不能超过64")
    private String dictItemName;

    /**
     * 路径
     */
    @ApiModelProperty(value = "路径")
    @Length(max = 1000, message = "路径长度不能超过1000")
    private String path;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "患者菜单医生可见状态")
    private Boolean patientMenuDoctorStatus;

    @ApiModelProperty(value = "患者菜单医助可见状态")
    private Boolean patientMenuNursingStatus;
}
