package com.caring.sass.ai.entity.search;

/**
 * 网页内容汇总
 *
 * @author leizhi
 */
public class WebPageSummary {

    private String name;
    private String url;
    private String snippet;

    public WebPageSummary(String name, String url, String snippet) {
        this.name = name;
        this.url = url;
        this.snippet = snippet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}
