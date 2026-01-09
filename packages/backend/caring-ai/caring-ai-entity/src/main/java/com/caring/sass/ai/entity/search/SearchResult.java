package com.caring.sass.ai.entity.search;

import java.util.List;
import java.util.Map;

/**
 * Bing搜索结果类
 *
 * @author leizhi
 */
public class SearchResult {

    private String _type;
    private QueryContext queryContext;
    private WebPages webPages;
    private RelatedSearches relatedSearches;
    private RankingResponse rankingResponse;

    // Getters and setters for each field

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public QueryContext getQueryContext() {
        return queryContext;
    }

    public void setQueryContext(QueryContext queryContext) {
        this.queryContext = queryContext;
    }

    public WebPages getWebPages() {
        return webPages;
    }

    public void setWebPages(WebPages webPages) {
        this.webPages = webPages;
    }

    public RelatedSearches getRelatedSearches() {
        return relatedSearches;
    }

    public void setRelatedSearches(RelatedSearches relatedSearches) {
        this.relatedSearches = relatedSearches;
    }

    public RankingResponse getRankingResponse() {
        return rankingResponse;
    }

    public void setRankingResponse(RankingResponse rankingResponse) {
        this.rankingResponse = rankingResponse;
    }

    // Nested classes for QueryContext, WebPages, RelatedSearches, and RankingResponse

    public static class QueryContext {
        private String originalQuery;

        // Getters and setters for originalQuery

        public String getOriginalQuery() {
            return originalQuery;
        }

        public void setOriginalQuery(String originalQuery) {
            this.originalQuery = originalQuery;
        }
    }

    public static class WebPages {
        private String webSearchUrl;
        private int totalEstimatedMatches;
        private List<WebPage> value;

        // Getters and setters for each field


        public String getWebSearchUrl() {
            return webSearchUrl;
        }

        public void setWebSearchUrl(String webSearchUrl) {
            this.webSearchUrl = webSearchUrl;
        }

        public int getTotalEstimatedMatches() {
            return totalEstimatedMatches;
        }

        public void setTotalEstimatedMatches(int totalEstimatedMatches) {
            this.totalEstimatedMatches = totalEstimatedMatches;
        }

        public List<WebPage> getValue() {
            return value;
        }

        public void setValue(List<WebPage> value) {
            this.value = value;
        }

        public static class WebPage {
            private String id;
            private String name;
            private String url;
            private String displayUrl;
            private String snippet;
            private List<DeepLink> deepLinks;

            // Getters and setters for each field

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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

            public String getDisplayUrl() {
                return displayUrl;
            }

            public void setDisplayUrl(String displayUrl) {
                this.displayUrl = displayUrl;
            }

            public String getSnippet() {
                return snippet;
            }

            public void setSnippet(String snippet) {
                this.snippet = snippet;
            }

            public List<DeepLink> getDeepLinks() {
                return deepLinks;
            }

            public void setDeepLinks(List<DeepLink> deepLinks) {
                this.deepLinks = deepLinks;
            }

            public static class DeepLink {
                private String name;
                private String url;
                private String snippet;

                // Getters and setters for each field

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
        }
    }

    public static class RelatedSearches {
        private List<RelatedSearch> value;

        // Getters and setters for each field
        public List<RelatedSearch> getValue() {
            return value;
        }

        public void setValue(List<RelatedSearch> value) {
            this.value = value;
        }

        public static class RelatedSearch {
            private String text;
            private String displayText;
            private String webSearchUrl;

            // Getters and setters for each field

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getDisplayText() {
                return displayText;
            }

            public void setDisplayText(String displayText) {
                this.displayText = displayText;
            }

            public String getWebSearchUrl() {
                return webSearchUrl;
            }

            public void setWebSearchUrl(String webSearchUrl) {
                this.webSearchUrl = webSearchUrl;
            }
        }
    }

    public static class RankingResponse {
        private Map<String, List<Item>> mainline;
        private Map<String, List<Item>> sidebar;

        // Getters and setters for each field
        public Map<String, List<Item>> getMainline() {
            return mainline;
        }

        public void setMainline(Map<String, List<Item>> mainline) {
            this.mainline = mainline;
        }

        public Map<String, List<Item>> getSidebar() {
            return sidebar;
        }

        public void setSidebar(Map<String, List<Item>> sidebar) {
            this.sidebar = sidebar;
        }

        public static class Item {
            private String answerType;
            private int resultIndex;
            private Object value;

            // Getters and setters for each field

            public String getAnswerType() {
                return answerType;
            }

            public void setAnswerType(String answerType) {
                this.answerType = answerType;
            }

            public int getResultIndex() {
                return resultIndex;
            }

            public void setResultIndex(int resultIndex) {
                this.resultIndex = resultIndex;
            }

            public Object getValue() {
                return value;
            }

            public void setValue(Object value) {
                this.value = value;
            }
        }
    }
}
