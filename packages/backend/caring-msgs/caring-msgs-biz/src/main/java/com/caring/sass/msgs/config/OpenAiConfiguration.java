package com.caring.sass.msgs.config;

import com.caring.sass.common.constant.ApplicationProperties;
import com.caring.sass.msgs.bot.service.CaringOpenAiService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author leizhi
 */
@Slf4j
@Configuration
public class OpenAiConfiguration {

    private final ApplicationProperties applicationProperties;

    public OpenAiConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public CaringOpenAiService openAiService() {
        String token = ApplicationProperties.getOpenAiToken().get(0);
        return new CaringOpenAiService(token, Duration.ofSeconds((120)));
    }

    @Bean
    public OpenAiStreamClient openAiStreamClient() {
        //本地开发需要配置代理地址
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        //!!!!!!测试或者发布到服务器千万不要配置Level == BODY!!!!
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();

        return OpenAiStreamClient
                .builder()
                .apiHost(ApplicationProperties.getOpenAiHost())
                .apiKey(ApplicationProperties.getOpenAiToken())
                //自定义key使用策略 默认随机策略
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                .build();
    }

    @Bean
    public OpenAiClient openAiClient() {
        //本地开发需要配置代理地址
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        //!!!!!!测试或者发布到服务器千万不要配置Level == BODY!!!!
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();

        return OpenAiClient
                .builder()
                .apiHost(ApplicationProperties.getOpenAiHost())
                .apiKey(ApplicationProperties.getOpenAiToken())
                //自定义key使用策略 默认随机策略
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                .build();
    }

}
