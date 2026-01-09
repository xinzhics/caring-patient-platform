package com.caring.sass.ai.entity.ckd;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.caring.sass.ai.dto.ckd.CkdMembershipLevel;
import com.caring.sass.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.caring.sass.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 * CKD用户信息
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
@TableName("m_ai_ckd_user_info")
@ApiModel(value = "CkdUserInfo", description = "CKD用户信息")
@AllArgsConstructor
public class CkdUserInfo extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    public static final String female = "female"; // 女
    public static final String male = "male"; // 男

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @Length(max = 100, message = "openId长度不能超过100")
    @TableField(value = "open_id", condition = LIKE)
    @Excel(name = "openId")
    private String openId;

    @ApiModelProperty(value = "")
    @Length(max = 200, message = "长度不能超过200")
    @TableField(value = "nickname", condition = LIKE)
    @Excel(name = "")
    private String nickname;

    @ApiModelProperty(value = "")
    @Length(max = 400, message = "长度不能超过400")
    @TableField(value = "head_img_url", condition = LIKE)
    @Excel(name = "")
    private String headImgUrl;

    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "user_role", condition = LIKE)
    @Excel(name = "")
    private String userRole;

    /**
     * 体重
     */
    @ApiModelProperty(value = "体重")
    @TableField("user_weight")
    @Excel(name = "体重")
    private Double userWeight;

    /**
     * 身高
     */
    @ApiModelProperty(value = "身高")
    @TableField("user_height")
    @Excel(name = "身高")
    private Double userHeight;

    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    @TableField("date_of_birth")
    @Excel(name = "出生日期", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDate dateOfBirth;

    /**
     * 血肌酐
     */
    @ApiModelProperty(value = "血肌酐")
    @TableField("serum_creatinine")
    @Excel(name = "血肌酐")
    private Double serumCreatinine;

    @TableField("gfr")
    private Double gfr;

    @ApiModelProperty(value = "建议蛋白质摄入量")
    @TableField("protein_in_take")
    private Double proteinInTake;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    @Length(max = 255, message = "性别长度不能超过255")
    @TableField(value = "gender", condition = LIKE)
    @Excel(name = "性别")
    private String gender;

    @ApiModelProperty(value = "是否高血压或高血压肾病")
    @TableField("hypertension")
    private Boolean hypertension;

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


    @ApiModelProperty(value = "肾病分期")
    @TableField(value = "kidney_disease_staging", condition = EQUAL)
    private String kidneyDiseaseStaging;  //肾病分期

    @ApiModelProperty(value = "每天蛋白质摄入量")
    @TableField(value = "protein_in_take_everyday", condition = EQUAL)
    private Double proteinInTakeEveryday;    //每天蛋白质摄入量

    @ApiModelProperty(value = "每天能量摄入量")
    @TableField(value = "in_take_energy_everyday", condition = EQUAL)
    private Double inTakeEnergyEveryday;     //每天摄入能量

    @ApiModelProperty(value = "高血压标签")
    @TableField(value = "high_blood_pressure_label", condition = EQUAL)
    private Boolean highBloodPressureLabel;   //高血压标签

    @ApiModelProperty(value = "高脂标签")
    @TableField(value = "hyperlipidemia_label", condition = EQUAL)
    private Boolean hyperlipidemiaLabel;      //高血脂标签

    @ApiModelProperty(value = "高尿酸标签")
    @TableField(value = "high_uric_acid_label", condition = EQUAL)
    private Boolean highUricAcidLabel;        //高尿酸标签

    @ApiModelProperty(value = "高磷标签")
    @TableField(value = "high_phosphorus_label", condition = EQUAL)
    private Boolean highPhosphorusLabel;     //高磷标签

    @ApiModelProperty(value = "高钾标签")
    @TableField(value = "high_potassium_label", condition = EQUAL)
    private Boolean highPotassiumLabel;      //高钾标签

    @ApiModelProperty(value = "肥胖标签")
    @TableField(value = "fat_label", condition = EQUAL)
    private Boolean fatLabel;                //肥胖标签

    @ApiModelProperty(value = "高龄标签")
    @TableField(value = "advanced_age_label", condition = EQUAL)
    private Boolean advancedAgeLabel;         //高龄标签

    /**
     * 会员等级
     */
    @ApiModelProperty(value = "会员等级")
    @Length(max = 255, message = "会员等级长度不能超过255")
    @TableField(value = "membership_level", condition = EQUAL)
    @Excel(name = "会员等级")
    private CkdMembershipLevel membershipLevel;

    /**
     * 到期时间
     */
    @ApiModelProperty(value = "到期时间")
    @TableField("expiration_date")
    @Excel(name = "到期时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime expirationDate;


    @ApiModelProperty(value = "粉丝免费时长")
    @TableField("free_duration_fans")
    private Integer freeDurationFans;


}
