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
 * 用户中心 启动类
 *
 * @author leizhi
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@Configuration
@EnableFeignClients(value = {"com.caring.sass"})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
@EnableAsync
@EnableLoginArgResolver
@EnableFormValidator
public class UcenterApplication {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(UcenterApplication.class, args);
        Environment env = application.getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                        "应用 '{}' 运行成功! 访问连接:\n\t" +
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
