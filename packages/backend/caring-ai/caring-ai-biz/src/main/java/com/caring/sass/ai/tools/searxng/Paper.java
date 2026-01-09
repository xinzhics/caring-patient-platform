package com.caring.sass.ai.tools.searxng;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 搜索引擎学术搜索返回对象
 *
 * @author leizhi
 */
@Accessors(chain = true)
@Data
@ToString
public class Paper {

    private String title;
    private List<String> authors;
    private String url;
    private String publishedDate;
    private String content;
    private String journal;
    private String doi;
    private String pdfUrl;
    private String comments;
    private List<String> tags;
    private String engine;
    private String category;

}
