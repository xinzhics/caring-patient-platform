package com.caring.sass.nursing.dto.form;

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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;

/**
 * <p>
 * 实体类
 * 表单结果导出记录表
 * </p>
 *
 * @author yangshuai
 * @since 2022-07-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@ApiModel(value = "FormResultExportSaveDTO", description = "表单结果导出记录表")
public class FormResultExportSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 导出类型(baseInfo 基本信息 ,follow_up 随访计划, doctor: 医生， nursing: 医助)
     */
    @ApiModelProperty(value = "导出类型(baseInfo 基本信息 ,follow_up 随访计划,  doctor: 医生， nursing: 医助, imMsg：聊天消息)")
    private String exportType;

    @ApiModelProperty(value = "患者的IM账号")
    private String patientImAccount;


    @ApiModelProperty(value = "患者的IM账号")
    private String planIdArrayJson;

    /**
     * 随访计划的id数组JSON格式存储
     */
    @ApiModelProperty(value = "随访计划的id数组JSON格式存储")
    private List<Long> planIds;
    /**
     * 基本信息导出的范围([BASE_INFO,HEALTH_RECORD])
     */
    @ApiModelProperty(value = "基本信息导出的范围([BASE_INFO,HEALTH_RECORD])")
    private List<String> baseInfoScopes;

    @ApiModelProperty(value = "患者分页查询条件")
    private String pageQueryJson;

}
