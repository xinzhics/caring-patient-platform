package com.caring.sass.nursing.entity.form;

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

import static com.baomidou.mybatisplus.annotation.SqlCondition.EQUAL;
import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_custom_form_result_export")
@ApiModel(value = "FormResultExport", description = "表单结果导出记录表")
@AllArgsConstructor
@Builder
public class FormResultExport extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 导出的文件名字
     */
    @ApiModelProperty(value = "导出的文件名字")
    @Length(max = 500, message = "导出的文件名字长度不能超过500")
    @TableField(value = "export_file_name", condition = LIKE)
    @Excel(name = "导出的文件名字")
    private String exportFileName;

    /**
     * 导出类型(baseInfo 基本信息 ,follow_up 随访计划)
     */
    @ApiModelProperty(value = "导出类型(baseInfo 基本信息 ,follow_up 随访计划,  doctor: 医生， nursing: 医助， imMsg 聊天记录)")
    @Length(max = 255, message = "导出类型(baseInfo 基本信息 ,follow_up 随访计划)长度不能超过255")
    @TableField(value = "export_type", condition = EQUAL)
    @Excel(name = "导出类型(baseInfo 基本信息 ,follow_up 随访计划, doctor: 医生， nursing: 医助)")
    private String exportType;

    /**
     * 所属机构ID
     */
    @ApiModelProperty(value = "所属机构ID")
    @TableField(value = "org_id")
    @Excel(name = "所属机构ID")
    private Long organId;

    /**
     * 随访计划的id数组JSON格式存储
     */
    @ApiModelProperty(value = "随访计划的id数组JSON格式存储")
    @Length(max = 255, message = "随访计划的id数组JSON格式存储长度不能超过255")
    @TableField(value = "plan_id_array_json", condition = LIKE)
    @Excel(name = "随访计划的id数组JSON格式存储, 敏宁的评分类型")
    private String planIdArrayJson;

    @ApiModelProperty(value = "患者的IM账号")
    @Length(max = 255, message = "患者的IM账号")
    @TableField(value = "patient_im_account", condition = EQUAL)
    private String patientImAccount;

    /**
     * 基本信息导出的范围([BASE_INFO,HEALTH_RECORD])
     */
    @ApiModelProperty(value = "基本信息导出的范围([BASE_INFO,HEALTH_RECORD])")
    @Length(max = 255, message = "基本信息导出的范围([BASE_INFO,HEALTH_RECORD])长度不能超过255")
    @TableField(value = "base_info_scope_array_json", condition = LIKE)
    @Excel(name = "基本信息导出的范围([BASE_INFO,HEALTH_RECORD])")
    private String baseInfoScopeArrayJson;

    /**
     * 导出的进度(已导出患者数/患者总数)
     */
    @ApiModelProperty(value = "导出的进度(已导出患者数/患者总数)")
    @TableField("export_progress")
    @Excel(name = "导出的进度(已导出患者数/患者总数)")
    private Integer exportProgress;

    /**
     * 导出任务的序号ID
     */
    @ApiModelProperty(value = "导出任务的序号ID")
    @TableField("export_id")
    @Excel(name = "导出任务的序号ID")
    private Long exportId;

    /**
     * 导出的表格的下载链接
     */
    @ApiModelProperty(value = "导出的表格的下载链接")
    @Length(max = 500, message = "导出的表格的下载链接长度不能超过500")
    @TableField(value = "export_file_url", condition = LIKE)
    @Excel(name = "导出的表格的下载链接")
    private String exportFileUrl;

    @ApiModelProperty(value = "")
    @TableField("export_total")
    private Integer exportTotal;

    @ApiModelProperty(value = "消息")
    @TableField("message")
    private String message;

    @ApiModelProperty(value = "分页查询条件")
    @TableField("page_query_json")
    private String pageQueryJson;

    @ApiModelProperty(value = "当前登录用户角色")
    @TableField("current_user_type")
    private String currentUserType;

    @ApiModelProperty(value = "患者名称")
    @TableField(exist = false)
    private String patientName;

}
