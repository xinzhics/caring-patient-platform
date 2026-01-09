package com.caring.sass.ai.know.model;

import lombok.Data;

@Data
public class DifyDocument {

    String id;
    String position;
    String data_source_type;
    DataSourceInfo data_source_info;
    String dataset_process_rule_id;
    String name;
    String created_from;
    String created_by;
    String created_at;
    String tokens;
    String indexing_status;
    String error;
    String enabled;
    String disabled_at;
    String disabled_by;
    String archived;
    String display_status;
    String word_count;
    String hit_count;

}