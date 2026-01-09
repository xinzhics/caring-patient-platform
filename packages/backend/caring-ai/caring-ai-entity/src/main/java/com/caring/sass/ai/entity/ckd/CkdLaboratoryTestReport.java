package com.caring.sass.ai.entity.ckd;

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
 * ckd用户化验单
 * </p>
 *
 * @author 杨帅
 * @since 2025-02-08
 */
@Data
@Builder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_ai_ckd_laboratory_test_report")
@ApiModel(value = "CkdLaboratoryTestReport", description = "ckd用户化验单")
@AllArgsConstructor
public class CkdLaboratoryTestReport extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 100, message = "openId长度不能超过100")
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "openId")
    private String openId;

    @Length(max = 200, message = "长度不能超过200")
    @TableField(value = "file_url", condition = LIKE)
    private String fileUrl;

    /**
     * 血肌酐
     */
    @ApiModelProperty(value = "血肌酐")
    @TableField("serum_creatinine")
    @Excel(name = "血肌酐")
    private Double serumCreatinine;

    @ApiModelProperty(value = "总胆固醇TC")
    @TableField("tc_of_blood_lipid")
    private Double tcOfBloodLipid; // 总胆固醇TC

    /*** 钾 ***/
    @ApiModelProperty(value = "钾")
    @TableField("k_of_electrolyte")
    private Double kOfelectrolyte;

    /*** 钠 ***/
    @ApiModelProperty(value = "钠")
    @TableField("na_of_electrolyte")
    private Double naOfelectrolyte;


    /*** 钙 ***/
    @ApiModelProperty(value = "钙")
    @TableField("ca_of_electrolyte")
    private Double caOfelectrolyte;

    /*** //磷 ***/
    @ApiModelProperty(value = "磷")
    @TableField("p_of_electrolyte")
    private Double pOfelectrolyte;

    /*** //尿酸 ***/
    @ApiModelProperty(value = "尿酸")
    @TableField("uric_acid")
    private Double uricAcid;


    @ApiModelProperty(value = "解读内容")
    @TableField("mark_html_content")
    private String markHtmlContent;


}
