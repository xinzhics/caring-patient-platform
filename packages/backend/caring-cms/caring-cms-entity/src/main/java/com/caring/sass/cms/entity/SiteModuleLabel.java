package com.caring.sass.cms.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
 * 建站多功能导航标签
 * </p>
 *
 * @author 杨帅
 * @since 2023-09-05
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_build_site_module_label")
@ApiModel(value = "SiteModuleLabel", description = "建站多功能导航标签")
@AllArgsConstructor
public class SiteModuleLabel extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 文件夹ID
     */
    @ApiModelProperty(value = "文件夹ID")
    @TableField("folder_id")
    @Excel(name = "文件夹ID")
    private Long folderId;

    /**
     * 页面ID
     */
    @ApiModelProperty(value = "页面ID")
    @TableField("page_id")
    @Excel(name = "页面ID")
    private Long pageId;

    /**
     * 组件ID
     */
    @ApiModelProperty(value = "组件ID")
    @TableField("module_id")
    @Excel(name = "组件ID")
    private Long moduleId;

    /**
     * 标签的名称
     */
    @ApiModelProperty(value = "标签的名称")
    @Length(max = 100, message = "标签的名称长度不能超过100")
    @TableField(value = "label_name", condition = LIKE)
    @Excel(name = "标签的名称")
    private String labelName;

    /**
     * 标签排序下标(从小到大)
     */
    @ApiModelProperty(value = "标签排序下标(从小到大)")
    @TableField("label_sort_index")
    @Excel(name = "标签排序下标(从小到大)")
    private Integer labelSortIndex;

    @ApiModelProperty(value = "多功能导航标签中的板块")
    @TableField(exist = false)
    private List<SiteModulePlate> labelPlates;

    public static void sort(List<SiteModuleLabel> labelList) {
        if (CollUtil.isEmpty(labelList)) {
            return;
        }
        labelList.sort((o1, o2) -> {
            if (Objects.isNull(o1.getLabelSortIndex()) || Objects.isNull(o2.getLabelSortIndex())) {
                return 0;
            }
            return o1.getLabelSortIndex() > o2.getLabelSortIndex() ? 1 : -1;
        });
    }

    public void setLabelPlates(List<SiteModulePlate> modulePlates) {
        SiteModulePlate.sort(modulePlates);
        this.labelPlates = modulePlates;
    }
}
