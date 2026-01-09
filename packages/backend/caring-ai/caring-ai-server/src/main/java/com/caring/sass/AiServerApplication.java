package com.caring.sass;

import com.caring.sass.security.annotation.EnableLoginArgResolver;
import com.caring.sass.validator.annotation.EnableFormValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * AI服务启动类
 *
 * @author leizhi
 * @date 2020-09-15
 */
@SpringBootApplication
@EnableDiscoveryClient
@Configuration
@EnableScheduling
@EnableAsync
@EnableFeignClients(value = { "com.caring.sass" })
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
@EnableLoginArgResolver
@EnableFormValidator
public class AiServerApplication {
    public static void main(String[] args) throws UnknownHostException {

        System.setProperty("ws.schild.jave.ffmpeg", "/usr/bin/ffmpeg");

        ConfigurableApplicationContext application = SpringApplication.run(AiServerApplication.class, args);
        Environment env = application.getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                        "应用 '{}' 启动成功! 访问连接:\n\t" +
                        "Swagger文档: \t\thttp://{}:{}/doc.html\n\t" +
                        "数据库监控: \t\thttp://{}:{}/druid\n" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                "127.0.0.1",
                env.getProperty("server.port"));
    }
}
