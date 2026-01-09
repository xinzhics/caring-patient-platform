package com.caring.sass.ai.tools;

import com.caring.sass.ai.config.SearchProperties;
import com.caring.sass.ai.entity.search.SearchResult;
import com.caring.sass.ai.entity.search.WebPageSummary;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 搜索工具类
 *
 * @author leizhi
 */
@Component
public class SearchTools {
    private final SearchProperties searchProperties;

    public SearchTools(SearchProperties searchProperties) {
        this.searchProperties = searchProperties;
    }

    @Tool("useful when you need to search the web.")
    public String bingSearch(@P("query message") String query) throws Exception {
        OkHttpClient client = new OkHttpClient();
        String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
        int count = 5;
        String requestUrl = searchProperties.getEndpoint() + "?q=" + encodedQuery + "&count=" + count;
        Request request = new Request.Builder()
                .url(requestUrl)
                .addHeader("Ocp-Apim-Subscription-Key", searchProperties.getSubscriptionKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || Objects.isNull(response.body())) {
                throw new IOException("Unexpected code " + response);
            }
            String json = response.body().string();
            Gson gson = new Gson();
            SearchResult results = gson.fromJson(json, new TypeToken<SearchResult>() {
            }.getType());
            List<SearchResult.WebPages.WebPage> webPages = results.getWebPages().getValue();
            List<WebPageSummary> summaries = webPages.stream()
                    .map(webPage -> new WebPageSummary(webPage.getName(), webPage.getUrl(), webPage.getSnippet()))
                    .collect(Collectors.toList());

            return new GsonBuilder().setPrettyPrinting().create().toJson(summaries);
        }
    }
}
