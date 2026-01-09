package com.caring.sass.nursing.dto.drugs;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.caring.sass.nursing.entity.drugs.SysDrugsCategoryLink;
import com.caring.sass.nursing.entity.drugs.SysDrugsImage;
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
import java.util.List;

/**
 * <p>
 * 实体类
 * 药品
 * </p>
 *
 * @author leizhi
 * @since 2020-09-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "SysDrugsSaveDTO", description = "药品")
public class SysDrugsSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 名字
     */
    @ApiModelProperty(value = "名字")
    @Length(max = 60, message = "名字长度不能超过60")
    private String name;
    /**
     * 通用名
     */
    @ApiModelProperty(value = "通用名")
    @Length(max = 60, message = "通用名长度不能超过60")
    private String genericName;
    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    @Length(max = 500, message = "图标长度不能超过500")
    private String icon;
    /**
     * 国药准字
     */
    @ApiModelProperty(value = "国药准字")
    @Length(max = 32, message = "国药准字长度不能超过32")
    private String gyzz;
    /**
     * 厂家
     */
    @ApiModelProperty(value = "厂家")
    @Length(max = 100, message = "厂家长度不能超过100")
    private String manufactor;
    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    @Length(max = 400, message = "规格长度不能超过400")
    private String spec;
    /**
     * 说明书
     */
    @ApiModelProperty(value = "说明书")
    @Length(max = 65535, message = "说明书长度不能超过65,535")
    private String instructions;
    /**
     * 适用症
     */
    @ApiModelProperty(value = "适用症")
    @Length(max = 65535, message = "适用症长度不能超过65,535")
    private String applicableDisease;
    /**
     * 用量
     */
    @ApiModelProperty(value = "用法用量")
    @Length(max = 65535, message = "用量长度不能超过65,535")
    private String dosage;
    /**
     * 禁忌症
     */
    @ApiModelProperty(value = "禁忌症")
    @Length(max = 65535, message = "禁忌症长度不能超过65,535")
    private String taboo;
    /**
     * 不良反应
     */
    @ApiModelProperty(value = "不良反应")
    @Length(max = 65535, message = "不良反应长度不能超过65,535")
    private String sideEffects;
    /**
     * 药物相互作用
     */
    @ApiModelProperty(value = "药物相互作用")
    @Length(max = 65535, message = "药物相互作用长度不能超过65,535")
    private String interaction;
    /**
     * 删除标记(0：未删除)
     */
    @ApiModelProperty(value = "删除标记(0：未删除)")
    private Integer delFlag;
    /**
     * 每日用药次数
     */
    @ApiModelProperty(value = "每日用药次数")
    private Integer number;
    /**
     * 每次剂量
     */
    @ApiModelProperty(value = "每次剂量")
    private Float dose;
    /**
     * 用药时间
     */
    @ApiModelProperty(value = "用药时间")
    @Length(max = 50, message = "用药时间长度不能超过50")
    private String time;
    /**
     * 用药周期(0:长期   N :N天)
     */
    @ApiModelProperty(value = "用药周期(0:长期   N :N天)")
    private Integer cycle;
    /**
     * 单位（粒、瓶、毫升等）
     */
    @ApiModelProperty(value = "单位（粒、瓶、毫升等）")
    @Length(max = 20, message = "单位（粒、瓶、毫升等）长度不能超过20")
    private String unit;
    /**
     * 条码
     */
    @ApiModelProperty(value = "条码")
    @Length(max = 50, message = "条码长度不能超过50")
    private String code;
    /**
     * 汉语拼音首字母
     */
    @ApiModelProperty(value = "汉语拼音首字母")
    @Length(max = 200, message = "汉语拼音首字母长度不能超过200")
    private String pyszm;
    /**
     * 药品简称或者说俗名
     */
    @ApiModelProperty(value = "药品简称或者说俗名")
    @Length(max = 200, message = "药品简称或者说俗名长度不能超过200")
    private String commondityName;
    /**
     * 组成成分
     */
    @ApiModelProperty(value = "组成成分")
    @Length(max = 200, message = "组成成分长度不能超过200")
    private String characters;
    /**
     * 英文名称
     */
    @ApiModelProperty(value = "英文名称")
    @Length(max = 200, message = "英文名称长度不能超过200")
    private String englishName;
    /**
     * 执行标准
     */
    @ApiModelProperty(value = "执行标准")
    @Length(max = 200, message = "执行标准长度不能超过200")
    private String executiveStandard;
    /**
     * 是否MI用药
     */
    @ApiModelProperty(value = "是否MI用药")
    private Boolean isMi;
    /**
     * 是否OTC用药
     */
    @ApiModelProperty(value = "是否OTC用药")
    private Boolean isOtc;
    /**
     * 包装
     */
    @ApiModelProperty(value = "包装")
    @Length(max = 200, message = "包装长度不能超过200")
    private String pack;
    /**
     * 储藏说明
     */
    @ApiModelProperty(value = "储藏说明")
    @Length(max = 200, message = "储藏说明长度不能超过200")
    private String storage;
    /**
     * 分类ID
     */
    @ApiModelProperty(value = "分类ID")
    private Long categoryId;
    /**
     * 药品有效期
     */
    @ApiModelProperty(value = "药品有效期")
    @Length(max = 200, message = "药品有效期长度不能超过200")
    private String validTime;
    /**
     * 是否置顶 1 置顶  -1 不置顶
     */
    @ApiModelProperty(value = "是否置顶 1 置顶  -1 不置顶")
    private Integer sticky;
    /**
     * 冗余字段 类别名称
     */
    @ApiModelProperty(value = "冗余字段 类别名称")
    @Length(max = 255, message = "冗余字段 类别名称长度不能超过255")
    private String categoryName;

    @ApiModelProperty(value = "剂型")
    @Length(max = 200, message = "剂型")
    private String dosageForm;

    @ApiModelProperty(value = "冗余字段 类别")
    private List<SysDrugsCategoryLink> sysDrugsCategoryLinkList;

    @ApiModelProperty(value = "冗余字段详细图片列表")
    private List<SysDrugsImage> sysDrugsImages;

    @ApiModelProperty(value = "药量计数")
    private Integer dosageCount;

}
