package com.caring.sass.ai.entity.know;

public enum KnowDoctorType {


    /**
     * 主任医生
     */
    CHIEF_PHYSICIAN,

    /**
     * 小程序用户，
     * 小程序用户只能使用点记。除非通过创建博主接口，给他升级成 主任。
     */
    MINI_USER,

    /**
     * 普通医生
     */
    GENERAL_PRACTITIONER;




}
