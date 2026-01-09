package com.caring.sass.ai.know.model;

import lombok.Data;

import java.util.List;

@Data
public class DocumentText {

    private String id;
    private int position;
    private String document_id;
    private String content;
    private String answer;
    private int word_count;
    private int tokens;
    private List<String> keywords;
    private String index_node_id;
    private String index_node_hash;
    private int hit_count;
    private boolean enabled;
//    private Date disabled_at;
    private String disabled_by;
    private String status;
    private String created_by;
    private long created_at;
    private long indexing_at;
    private long completed_at;
    private String error;
//    private Date stopped_at;
}
