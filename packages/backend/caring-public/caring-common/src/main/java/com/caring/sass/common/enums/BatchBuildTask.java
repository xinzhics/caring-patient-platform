package com.caring.sass.common.enums;

/**
 * @ClassName CmsRoleRemark
 * @Description
 * @Author yangShuai
 * @Date 2021/10/19 9:47
 * @Version 1.0
 */
public enum BatchBuildTask {

    /**
     * 任务被删除
     */
    DELETE,

    /**
     * 成功
     */
    SUCCESS,

    /**
     * 异常
     */
    ERROR,

    /**
     * RUNNING
     */
    RUNNING,

    /**
     * 停止 or 取消
     */
    STOP,

    /**
     * 只有子任务有wait状态
     */
    WAIT
}
