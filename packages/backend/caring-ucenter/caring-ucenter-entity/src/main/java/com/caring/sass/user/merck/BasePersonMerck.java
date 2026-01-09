package com.caring.sass.user.merck;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class BasePersonMerck extends BaseDoctorMerck {

    public static final String  KEY_NEW_CUSTOMER_REPORT = "key_new_customer_report";    // 新患者
    public static final String  KEY_OLD_CUSTOMER_REPORT = "key_old_customer_report";    // 老患者

    // 患者当前是否已经被 某专员锁定。
    public static final String LOCKING_YES = "locking_yes";    // 已锁定
    public static final String LOCKING_NO = "locking_no";   // 未锁定

    public static final int hide_yes = 1;    // 隐藏
    public static final int hide_no = 2;   //  未隐藏

    // 此Id为本系统用户唯一标识，不论用户是否转换机构，都将只有一个此Id, 粉丝也一样适用。
    private String merckPersonId;

    @ApiModelProperty("锁定专员Name")
    private String lockSalesmanName;

    @ApiModelProperty("锁定专员Id")
    private Long lockSalesmanId;

    /**
     * locking_yes
     * locking_no
     */
    @ApiModelProperty("锁定状态")
    private String lockState;
    /**
     *
     * key_new_customer_report
     * key_old_customer_report
     */
    @ApiModelProperty("新老患者")
    private String isNewOrOldCustomer;

    @ApiModelProperty("开始针次")
    private Integer startNeedleNumber;

    @ApiModelProperty("注射完成数量")
    private Integer injectionCompleteNumber;

    @ApiModelProperty("最后注射Id")
    private Long lastInjectionId;


    @ApiModelProperty("档案号")
    private String fileNumber;


    @ApiModelProperty("报到状态")
    private String reportStatus;

    private String injectionMedicineName;


    private String injectionMedicine;

    // 当患者终止治疗时，复制患者 person 信息，
    // Person oldPerson = new Person()
    // 复制 当时的person信息到 oldPerson中，
    // oldPerson id 以 , 号拼接到 person 中 personDataId
    // oldPerson 治疗阶段 为终止治疗状态
    // oldPerson 治疗结束时间 为终止治疗 时 时间
    // oldPerson 的openId设置为null,防止用户关注时查找到。
    // 清除 person 中的 机构信息， 专员信息， 医生信息， pef值
    @ApiModelProperty("患者的Id，历史患者通过此Id查询历史数据")
    private String personDataId;

    @ApiModelProperty("隐藏状态 1 隐藏， 2 未隐藏")
    private Integer hide;

    // 此条数据是否已经被归档。归档的数据不参与用户其他数据的校验，计算，判断是否存在，是否拥有历史等。
    // 无论用户更换到什么机构。归档的记录只可查看，不作为今后任何业务数据的参考值。
    private Integer archive;


    @ApiModelProperty("治疗阶段")
    private String treatmentStage;

    @ApiModelProperty("治疗完成的原因")
    private String treatmentCompleted;

    @ApiModelProperty("治疗完成的原因备注")
    private String treatmentCompletedRemark;

    @ApiModelProperty("联系人")
    private String contact;

    @ApiModelProperty("患者与联系人的关系")
    private String relationship;

    @ApiModelProperty("联系人电话")
    private String contactPhone;

    @ApiModelProperty("初诊疾病")
    private Set<Constant> firstVisitDisease;

    @ApiModelProperty("并发症")
    private String complicationsNote;

    @ApiModelProperty("预计pef")
    private Integer evalPEF;

    @ApiModelProperty("联合用药")
    private String combinationMedication;

    @ApiModelProperty("最佳pef")
    private Integer bestPEF;

    @ApiModelProperty("医生姓名")
    private String doctorName;

    @ApiModelProperty("机构名称")
    private String orgName;

    @ApiModelProperty("脱落情况")
    private String lossSituation;


}
