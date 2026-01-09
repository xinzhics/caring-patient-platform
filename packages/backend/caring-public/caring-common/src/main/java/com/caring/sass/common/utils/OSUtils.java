package com.caring.sass.common.utils;

public class OSUtils {
    public static final OSType osType = getOSType();
    public static final boolean WINDOWS;
    public static final boolean SOLARIS;
    public static final boolean MAC;
    public static final boolean FREEBSD;
    public static final boolean LINUX;
    public static final boolean OTHER;

    private static OSType getOSType() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            return OSType.OS_TYPE_WIN;
        } else if (!osName.contains("SunOS") && !osName.contains("Solaris")) {
            if (osName.contains("Mac")) {
                return OSType.OS_TYPE_MAC;
            } else if (osName.contains("FreeBSD")) {
                return OSType.OS_TYPE_FREEBSD;
            } else {
                return osName.startsWith("Linux") ? OSType.OS_TYPE_LINUX : OSType.OS_TYPE_OTHER;
            }
        } else {
            return OSType.OS_TYPE_SOLARIS;
        }
    }

    static {
        WINDOWS = osType == OSType.OS_TYPE_WIN;
        SOLARIS = osType == OSType.OS_TYPE_SOLARIS;
        MAC = osType == OSType.OS_TYPE_MAC;
        FREEBSD = osType == OSType.OS_TYPE_FREEBSD;
        LINUX = osType == OSType.OS_TYPE_LINUX;
        OTHER = osType == OSType.OS_TYPE_OTHER;
    }

    public enum OSType {
        OS_TYPE_LINUX,
        OS_TYPE_WIN,
        OS_TYPE_SOLARIS,
        OS_TYPE_MAC,
        OS_TYPE_FREEBSD,
        OS_TYPE_OTHER;
        
    }
}
