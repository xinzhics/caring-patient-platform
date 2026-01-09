package com.caring.sass.ai.entity.know;

import org.hibernate.validator.constraints.Length;

public interface KnowledgeFileStatus {

    String UPLOADING = "UPLOADING";
    // 等待中
    String WAITING = "WAITING";


    // 分析中
    String ANALYSIS = "ANALYSIS";

    String ANALYSIS_KEYWORD = "ANALYSIS_KEYWORD";

    // 已完成
    String CHECK = "CHECK";

    // 完成
    String CHECKED = "CHECKED";


    String FAIL = "FAIL";
}
