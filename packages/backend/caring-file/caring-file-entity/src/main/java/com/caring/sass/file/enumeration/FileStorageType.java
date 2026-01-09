package com.caring.sass.file.enumeration;

/**
 * 文件 存储类型 枚举
 *
 * @author caring
 * @date 2019/05/06
 */
public enum FileStorageType {
    /**
     * 本地
     */
    LOCAL,
    /**
     * FastDFS
     */
    FAST_DFS,

    ALI,

    QINIU,

    /** 华为 */
    HW,
    ;

    public boolean eq(FileStorageType type) {
        for (FileStorageType t : FileStorageType.values()) {
            return t.equals(type);
        }
        return false;
    }
}
