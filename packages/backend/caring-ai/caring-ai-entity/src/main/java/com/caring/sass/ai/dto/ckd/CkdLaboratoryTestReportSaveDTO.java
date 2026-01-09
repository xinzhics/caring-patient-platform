package com.caring.sass.ai.dto.ckd;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "CkdLaboratoryTestReportSaveDTO", description = "ckd用户化验单")
public class CkdLaboratoryTestReportSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * openId
     */
    @ApiModelProperty(value = "openId")
    @NotNull
    @Length(max = 100, message = "openId长度不能超过100")
    private String openId;


    @ApiModelProperty(value = "化验单")
    @Length(max = 200, message = "长度不能超过200")
    private String fileUrl;


    /**
     * 血肌酐
     */
    @ApiModelProperty(value = "血肌酐")
    private Double serumCreatinine;

    @ApiModelProperty(value = "总胆固醇TC")
    private Double tcOfBloodLipid; // 总胆固醇TC

    /*** 钾 ***/
    @ApiModelProperty(value = "钾")
    private Double kOfelectrolyte;

    /*** 钠 ***/
    @ApiModelProperty(value = "钠")
    private Double naOfelectrolyte;


    /*** 钙 ***/
    @ApiModelProperty(value = "钙")
    private Double caOfelectrolyte;

    /*** //磷 ***/
    @ApiModelProperty(value = "磷")
    private Double pOfelectrolyte;

    /*** //尿酸 ***/
    @ApiModelProperty(value = "尿酸")
    private Double uricAcid;

    @ApiModelProperty(value = "解读内容")
    private String markHtmlContent;


}
