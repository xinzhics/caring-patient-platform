package com.caring.sass.user.merck;

import lombok.Data;

import java.util.Date;

@Data
public class Image {

    private String comment;
    // 医生 上传头像
    public static final String PURPOSE_HEAD_WX_IMAGE = "purpose_head_wx_image";

    // cms图片
    public static final String PURPOSE_CMS_IMAGE = "purpose_cms_image";

    // 医生认证
    public static final String PURPOSE_DOCTOR_CERTIFICATION = "purpose_doctor_certification";

    // 患者 粉丝使用的 头像
    public static final String PURPOSE_HEAD_IMAGE = "purpose_head_image";
    public static final String PURPOSE_MNTOR_IMAGE = "purpose_monitor_hcheck_image";

    // 微信二维码
    public static final String weChatQrCode = "weChatQrCode";

    /**
     * 初诊病历中的 检查报告过敏原
     */
    public static final String PURPOSE_INSPECTION_REPORT_ALLERGEN_IMAGE = "purpose_inspection_report_allergen_image";
    /**
     * 初诊病历中的 检查报告肺功能
     */
    public static final String PURPOSE_INSPECTION_REPORT_PULMONARY_FUNCTION_IMAGE = "purpose_inspection_report_pulmonary_function_image";
    /**
     * 初诊病历中的 检查报告FeNO
     */
    public static final String PURPOSE_INSPECTION_REPORT_FENO_IMAGE = "purpose_inspection_report_feno_image";
    /**
     * 初诊病历中的 检查报告其他
     */
    public static final String PURPOSE_INSPECTION_REPORT_OTHER_IMAGE = "purpose_inspection_report_other_image";
    /**
     * 初诊病历中的 初诊病历过敏原报告
     */
    public static final String PURPOSE_ALLERGEN_REPORT_IMAGE = "purpose_allergen_report_image";
    /**
     *不良反应处理记录图片
     */
    public static final String PURPOSE_ADVERSE_REACTIONS_IMAGE = "purpose_adverse_reactions_image";
    /**
     * 图片尺寸
     */
    public static String SMALL = "small";

    private Long id;

    private Long ownerId;

    private Long personId;

    private Long certOwnerId;

    private Long healthFileId;

    private Long firstVisitCasesId;

    private Long injectionId;

    private String imageType;

    private String imagePurpose;

    private String name;

    private String imagePath;

    private String imageUrl;

    /**
     * 对一个检验报告进行分组
     * 一个检验报告多个图片
     * groupId 为null 表示是初诊病历上传的
     */
    private String groupId;

    /**
     * 区分是 初诊病历的检验报告，还是单独的检验报告
     */
    private String groupType;

    private Date inspectionDate;

    public String doMainImgPath;

    public String downloadUrl;

    public void setDoMainImgPath(String doMainImgPath) {
        this.doMainImgPath = doMainImgPath;
        setDownloadUrl("http://"+ doMainImgPath + ".caringsaas.com/caring_managers/api/file/download/image/" + this.id);
    }

}
