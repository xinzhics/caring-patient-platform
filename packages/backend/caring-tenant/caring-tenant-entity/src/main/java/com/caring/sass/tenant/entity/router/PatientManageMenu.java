package com.caring.sass.tenant.entity.router;

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

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * 患者管理平台菜单
 * </p>
 *
 * @author 杨帅
 * @since 2023-08-07
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_patient_manage_menu")
@ApiModel(value = "PatientManageMenu", description = "患者管理平台菜单")
@AllArgsConstructor
public class PatientManageMenu extends Entity<Long> {


    public static final String INFORMATION_INTEGRITY = "INFORMATION_INTEGRITY";     // 信息完整度
    public static final String MONITOR_DATA = "MONITOR_DATA";           // 监测数据
    public static final String MEDICATION_WARNING = "MEDICATION_WARNING";   // 用药
    public static final String EXCEPTION_OPTION_TRACKING = "EXCEPTION_OPTION_TRACKING"; // 选项跟踪
    public static final String NOT_FINISHED_TRACKING = "NOT_FINISHED_TRACKING"; // 未完成跟踪

    private static final long serialVersionUID = 1L;

    @Length(max = 50, message = "长度不能超过50")
    @TableField(value = "name", condition = LIKE)
    private String name;

    @Length(max = 200, message = "长度不能超过200")
    @TableField(value = "icon", condition = LIKE)
    private String icon;

    @TableField("menu_sort")
    private Integer menuSort;

    /**
     * 显示或隐藏(0 隐藏 1显示)
     */
    @ApiModelProperty(value = "显示或隐藏(0 隐藏 1显示)")
    @TableField("show_status")
    @Excel(name = "显示或隐藏(0 隐藏 1显示)")
    private Integer showStatus;

    /**
     * 菜单类型(信息完整度，监测数据，用药预警)
     */
    @ApiModelProperty(value = "菜单类型(信息完整度，监测数据，用药预警)")
    @Length(max = 100, message = "菜单类型(信息完整度，监测数据，用药预警)长度不能超过100")
    @TableField(value = "menu_type", condition = LIKE)
    @Excel(name = "菜单类型(信息完整度，监测数据，用药预警)")
    private String menuType;


    @Builder
    public PatientManageMenu(Long id, LocalDateTime createTime, Long createUser, Long updateUser, LocalDateTime updateTime, 
                    String name, String icon, Integer menuSort, Integer showStatus, String menuType) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.updateTime = updateTime;
        this.name = name;
        this.icon = icon;
        this.menuSort = menuSort;
        this.showStatus = showStatus;
        this.menuType = menuType;
    }

}
