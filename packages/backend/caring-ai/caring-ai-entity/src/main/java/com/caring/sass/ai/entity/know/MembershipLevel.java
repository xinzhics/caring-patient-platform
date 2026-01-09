package com.caring.sass.ai.entity.know;

public enum MembershipLevel {


    // 免费版
    FREE_VERSION,

    // 月度会员
    TRIAL_VERSION,

    // 会员
    PROFESSIONAL_EDITION;


    public static Integer getGrade(MembershipLevel membershipLevel) {
        if (membershipLevel == null) {
            return 0;
        }
        if (membershipLevel.equals(FREE_VERSION)) {
            return 1;
        }
        if (membershipLevel.equals(PROFESSIONAL_EDITION)) {
            return 3;
        }
        return 4;
    }

}
