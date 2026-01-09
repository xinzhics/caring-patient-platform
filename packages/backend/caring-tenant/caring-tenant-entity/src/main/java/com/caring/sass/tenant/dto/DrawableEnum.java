package com.caring.sass.tenant.dto;

/**
 * icon尺寸
 * ldpi: 36*36
 * mdpi: 48*48
 * hdpi: 72*72
 * xhdpi: 96*96
 * xxhdpi: 144*144
 * xxxhdpi: 192*192
 * <p>
 * push尺寸：
 * ldpi: 48*48
 * mdpi: 64*64
 * hdpi: 96*96
 * xhdpi: 128*128
 * xxhdpi: 192*192
 * <p>
 * splash尺寸
 * 竖屏 高×宽
 * ldpi 320*200
 * mdpi 480*320
 * hdpi 800*480
 * xhdpi 1280*720
 * xxhdpi 1600*960
 * xxxhdpi 1920*1280
 *
 * @author xinz
 */
public enum DrawableEnum {
    ldpi("drawable-ldpi", 36, 36, 48, 48, 200, 320),
    mdpi("drawable-mdpi",48, 48, 64, 64,  320,480),
    hdpi("drawable-hdpi",72, 72, 96, 96,  480,800),
    xhdpi("drawable-xhdpi",96, 96, 128, 128,  720,1280),
    xxhdpi("drawable-xxhdpi",144, 144, 192, 192,  1080,1920),
    xxxhdpi("drawable-xxxhdpi",192, 192, 192, 192,  1280,1920),
    ;

    /**
     * 文件夹名称
     */
    private String foldName;

    /**
     * icon宽
     */
    private int iconWidth;

    /**
     * icon高
     */
    private int iconHeight;

    private int pushWidth;

    private int pushHeight;

    /**
     * 启动图宽
     */
    private int splashWidth;

    private int splashHeight;

    public String getFoldName() {
        return foldName;
    }


    public int getIconWidth() {
        return iconWidth;
    }

    public int getIconHeight() {
        return iconHeight;
    }

    public int getPushWidth() {
        return pushWidth;
    }

    public int getPushHeight() {
        return pushHeight;
    }

    public int getSplashWidth() {
        return splashWidth;
    }

    public int getSplashHeight() {
        return splashHeight;
    }

    DrawableEnum(String foldName, int iconWidth, int iconHeight, int pushWidth, int pushHeight, int splashWidth, int splashHeight) {
        this.foldName = foldName;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.pushWidth = pushWidth;
        this.pushHeight = pushHeight;
        this.splashWidth = splashWidth;
        this.splashHeight = splashHeight;
    }


}
