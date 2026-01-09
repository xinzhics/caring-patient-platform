package com.caring.sass.user.merck;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;


@Data
public class HealthFile extends BaseMerckHealthFile {

    private Long id;

    // 用户业务的所属的机构Id
    // 用户产生此记录时，所处的机构id
    private Long businessOrgId;

    // 此条数据是否已经被归档。归档的数据不参与用户其他数据的校验，计算，判断是否存在，是否拥有历史等。
    // 无论用户更换到什么机构。归档的记录只可查看，不作为今后任何业务数据的参考值。
    private Integer archive;

    // 粉丝提交 健康档案时， 携带粉丝的诊断类型
    @ApiModelProperty("初诊疾病(常量分组：const_first_visit_disease")
    private Set<Constant> firstVisitDisease;

    @ApiModelProperty("并发症")
    private String complicationsNote;



}
