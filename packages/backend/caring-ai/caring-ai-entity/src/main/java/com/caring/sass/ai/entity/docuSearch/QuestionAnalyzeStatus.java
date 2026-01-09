package com.caring.sass.ai.entity.docuSearch;

/**
 * 问题的分析状态
 */
public enum QuestionAnalyzeStatus {

    /**
     * 1.理解问题
     */
    UNDERSTANDING_QUESTION,

    /**
     * 2.分析问题
     */
    ANALYZING_QUESTION,

    /**
     * 3.查找证据
     */
    FINDING_EVIDENCE,

    /**
     * AI总结中
     */
    AI_SUMMARY,

    /**
     * 4.分析完毕
     */
    ANALYZE_COMPLETE,

    /**
     * 取消响应
     */
    CANCEL_RESPONSE;


}
