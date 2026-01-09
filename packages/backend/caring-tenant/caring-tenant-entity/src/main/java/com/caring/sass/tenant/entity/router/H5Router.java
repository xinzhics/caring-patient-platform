package com.caring.sass.tenant.entity.router;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.tenant.enumeration.RouterModuleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_h5_router")
@ApiModel(value = "H5Router", description = "患者路由")
@AllArgsConstructor
public class H5Router extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "名称不能为空")
    @Length(max = 64, message = "名称长度不能超过64")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "名称")
    private String name;

    /**
     * 显示状态
     */
    @ApiModelProperty(value = "显示状态")
    @TableField("status_")
    @Excel(name = "显示状态", replace = {"是_true", "否_false", "_null"})
    private Boolean status;

    /**
     * 禁止删除
     */
    @ApiModelProperty(value = "禁止删除")
    @TableField("ban_delete")
    @Excel(name = "禁止删除", replace = {"是_true", "否_false", "_null"})
    private Boolean banDelete;

    /**
     * icon地址
     */
    @ApiModelProperty(value = "icon地址")
    @Length(max = 255, message = "icon地址长度不能超过255")
    @TableField(value = "icon_url", condition = LIKE)
    @Excel(name = "icon地址")
    private String iconUrl;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    @TableField("sort_value")
    @Excel(name = "排序")
    private Integer sortValue;

    /**
     * 类型ID
     */
    @ApiModelProperty(value = "类型ID")
    @NotNull(message = "类型ID不能为空")
    @TableField("dict_item_id")
    @Excel(name = "类型ID")
    private Long dictItemId;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    @NotEmpty(message = "类型不能为空")
    @Length(max = 64, message = "类型长度不能超过64")
    @TableField(value = "dict_item_name", condition = LIKE)
    @Excel(name = "类型")
    private String dictItemName;

    @ApiModelProperty(value = "类型")
    @NotEmpty(message = "类型不能为空")
    @Length(max = 64, message = "类型长度不能超过64")
    @TableField(value = "dict_item_type", condition = EQUAL)
    @Excel(name = "类型")
    private String dictItemType;

    /**
     * 路径
     */
    @ApiModelProperty(value = "路径/小程序信息")
    @NotEmpty(message = "路径不能为空{username: 小程序ID ,path: 小程序路径}")
    @Length(max = 1000, message = "路径长度不能超过1000")
    @TableField(value = "path", condition = EQUAL)
    @Excel(name = "路径")
    private String path;

    @ApiModelProperty(value = "用户类型(patient,doctor,nursing)")
    @TableField(value = "user_type", condition = EQUAL)
    @Excel(name = "用户类型")
    private String userType;

    @ApiModelProperty(value = "患者菜单医生可见状态")
    @TableField("patient_menu_doctor_status")
    @Excel(name = "患者菜单医生可见状态", replace = {"是_true", "否_false", "_null"})
    private Boolean patientMenuDoctorStatus;

    @ApiModelProperty(value = "患者菜单医助可见状态")
    @TableField("patient_menu_nursing_status")
    @Excel(name = "患者菜单医助可见状态", replace = {"是_true", "否_false", "_null"})
    private Boolean patientMenuNursingStatus;

    // TODO: 未知的用途。
    @ApiModelProperty(value = "项目域名")
    @TableField(value = "tenant_domain", exist = false)
    private String tenantDomain;

    @ApiModelProperty(value = "项目CODE")
    @TableField(exist = false)
    private String tenantCode;

    @ApiModelProperty(value = "BASE_INFO（基本信息），MY_FEATURES(我的功能)，MY_FILE(我的档案)")
    @TableField("module_type")
    private RouterModuleTypeEnum moduleType;

    @Builder
    public H5Router(Long id, Long createUser, LocalDateTime createTime, Long updateUser, LocalDateTime updateTime,
                    String name, Boolean status, String iconUrl, Integer sortValue, Long dictItemId, String dictItemName, String dictItemType,
                    Boolean banDelete, String path, String userType, Boolean patientMenuDoctorStatus, Boolean patientMenuNursingStatus, RouterModuleTypeEnum moduleType) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.name = name;
        this.status = status;
        this.banDelete = banDelete;
        this.iconUrl = iconUrl;
        this.sortValue = sortValue;
        this.dictItemId = dictItemId;
        this.dictItemName = dictItemName;
        this.dictItemType = dictItemType;
        this.path = path;
        this.userType = userType;
        this.patientMenuNursingStatus = patientMenuNursingStatus;
        this.patientMenuDoctorStatus = patientMenuDoctorStatus;
        this.moduleType = moduleType;
    }

}
