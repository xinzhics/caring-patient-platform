package com.caring.sass.tenant.dto.router;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.tenant.enumeration.RouterModuleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.caring.sass.base.entity.SuperEntity;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

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
@ApiModel(value = "H5RouterUpdateDTO", description = "患者路由")
public class H5RouterUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;

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

    @ApiModelProperty(value = "患者菜单医生可见状态, 是 true, 否， false")
    private Boolean patientMenuDoctorStatus;

    @ApiModelProperty(value = "患者菜单医助可见状态,  是 true, 否， false")
    private Boolean patientMenuNursingStatus;


    @ApiModelProperty(value = "类型")
    @Length(max = 64, message = "类型长度不能超过64")
    private String dictItemType;


    @ApiModelProperty(value = "项目域名")
    @TableField(value = "tenant_domain", exist = false)
    private String tenantDomain;

    @ApiModelProperty(value = "BASE_INFO（基本信息），MY_FEATURES(我的功能)，MY_FILE(我的档案)")
    @TableField("module_type")
    private RouterModuleTypeEnum moduleType;

}

