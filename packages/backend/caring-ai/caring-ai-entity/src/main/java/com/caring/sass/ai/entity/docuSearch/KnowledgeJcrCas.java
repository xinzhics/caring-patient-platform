package com.caring.sass.ai.entity.docuSearch;

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
 * jcr和cas分区信息表
 * </p>
 *
 * @author 杨帅
 * @since 2024-10-18
 */
@Data
@NoArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_knowledge_jcr_cas")
@ApiModel(value = "KnowledgeJcrCas", description = "jcr和cas分区信息表")
@AllArgsConstructor
public class KnowledgeJcrCas extends Entity<Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * 期刊的全名
     */
    @ApiModelProperty(value = "期刊的全名")
    @Length(max = 255, message = "期刊的全名长度不能超过255")
    @TableField(value = "journal_name", condition = LIKE)
    @Excel(name = "期刊的全名")
    private String journalName;

    /**
     * 期刊的缩写名
     */
    @ApiModelProperty(value = "期刊的缩写名")
    @Length(max = 255, message = "期刊的缩写名长度不能超过255")
    @TableField(value = "jcr_abbr_name", condition = LIKE)
    @Excel(name = "期刊的缩写名")
    private String jcrAbbrName;

    /**
     * 期刊的国际标准序列号-印刷版
     */
    @ApiModelProperty(value = "期刊的国际标准序列号-印刷版")
    @Length(max = 54, message = "期刊的国际标准序列号-印刷版长度不能超过54")
    @TableField(value = "issn", condition = LIKE)
    @Excel(name = "期刊的国际标准序列号-印刷版")
    private String issn;

    /**
     * 期刊的国际标准序列号-电子版
     */
    @ApiModelProperty(value = "期刊的国际标准序列号-电子版")
    @Length(max = 64, message = "期刊的国际标准序列号-电子版长度不能超过64")
    @TableField(value = "eissn", condition = LIKE)
    @Excel(name = "期刊的国际标准序列号-电子版")
    private String eissn;

    /**
     * 期刊影响因子
     */
    @ApiModelProperty(value = "期刊影响因子")
    @Length(max = 32, message = "期刊影响因子长度不能超过32")
    @TableField(value = "jif", condition = LIKE)
    @Excel(name = "期刊影响因子")
    private String jif;

    /**
     * 期刊五年影响因子
     */
    @ApiModelProperty(value = "期刊五年影响因子")
    @Length(max = 32, message = "期刊五年影响因子长度不能超过32")
    @TableField(value = "jif_5_years", condition = LIKE)
    @Excel(name = "期刊五年影响因子")
    private String jif5Years;

    /**
     * 期刊所属的学科类别
     */
    @ApiModelProperty(value = "期刊所属的学科类别")
    @Length(max = 128, message = "期刊所属的学科类别长度不能超过128")
    @TableField(value = "category", condition = LIKE)
    @Excel(name = "期刊所属的学科类别")
    private String category;

    /**
     * 期刊分区
     */
    @ApiModelProperty(value = "期刊分区")
    @Length(max = 32, message = "期刊分区长度不能超过32")
    @TableField(value = "jif_quartile", condition = LIKE)
    @Excel(name = "期刊分区")
    private String jifQuartile;

    /**
     * 期刊排名
     */
    @ApiModelProperty(value = "期刊排名")
    @Length(max = 32, message = "期刊排名长度不能超过32")
    @TableField(value = "jcr_rank", condition = LIKE)
    @Excel(name = "期刊排名")
    private String jcrRank;

    /**
     * 中科院小类
     */
    @ApiModelProperty(value = "中科院小类")
    @Length(max = 128, message = "中科院小类长度不能超过128")
    @TableField(value = "cas_sub_category", condition = LIKE)
    @Excel(name = "中科院小类")
    private String casSubCategory;

    /**
     * 中科院大类
     */
    @ApiModelProperty(value = "中科院大类")
    @Length(max = 128, message = "中科院大类长度不能超过128")
    @TableField(value = "cas_category", condition = LIKE)
    @Excel(name = "中科院大类")
    private String casCategory;

    /**
     * 中科院大类期刊分区
     */
    @ApiModelProperty(value = "中科院大类期刊分区")
    @Length(max = 32, message = "中科院大类期刊分区长度不能超过32")
    @TableField(value = "cas_category_quartile", condition = LIKE)
    @Excel(name = "中科院大类期刊分区")
    private String casCategoryQuartile;

    /**
     * 中科院小类期刊分区
     */
    @ApiModelProperty(value = "中科院小类期刊分区")
    @Length(max = 32, message = "中科院小类期刊分区长度不能超过32")
    @TableField(value = "cas_sub_category_quartile", condition = LIKE)
    @Excel(name = "中科院小类期刊分区")
    private String casSubCategoryQuartile;



}
