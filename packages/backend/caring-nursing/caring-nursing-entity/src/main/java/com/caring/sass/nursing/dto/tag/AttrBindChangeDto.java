package com.caring.sass.nursing.dto.tag;

import com.caring.sass.nursing.constant.AttrBindEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName AttrBindChangeDto
 * @Description
 * @Author yangShuai
 * @Date 2022/4/24 13:38
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttrBindChangeDto implements Serializable {

    /**
     * 标签绑定事件
     */
    @NotNull
    private AttrBindEvent event;

    /**
     * 事件所属于的租户
     */
    @NotEmpty
    private String tenantCode;

    /**
     * 标签的新增 修改 时 此ID不能为空
     */
    private Long tagId;

    /**
     * 基本信息，疾病信息，监测计划的 表单结果ID
     */
    private Long formResultId;

    /**
     * 用户药箱变化时 此ID不能为空
     */
    private Long drugsId;


    /**
     * 除了 标签新增和修改事件，其他事件都需要传 患者ID
     */
    private Long patientId;

}
