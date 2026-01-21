# 网关服务配置修复说明

## 问题描述

在启动网关服务时出现以下错误：

```
org.springframework.beans.factory.BeanDefinitionStoreException: Failed to process import candidates for configuration class [com.caring.sass.mq.MyRabbitMqConfiguration$RabbitMqConfiguration]; nested exception is java.io.FileNotFoundException: class path resource [org/springframework/web/servlet/config/annotation/WebMvcConfigurer.class] cannot be opened because it does not exist
```

## 问题分析

- 网关服务基于 Spring WebFlux 构建（使用 `spring-boot-starter-webflux`）
- 项目中依赖的 [caring-mq-starter](file:///home/admin/app/src/main/java/com/caring/sass/mq/MqAutoConfiguration.java) 包含了一个配置类 [MyRabbitMqConfiguration](file:///home/admin/app/src/main/java/com/caring/sass/mq/MyRabbitMqConfiguration.java#L22-L94)
- 该配置类的内部类 [RabbitMqConfiguration](file:///home/admin/app/src/main/java/com/caring/sass/mq/MyRabbitMqConfiguration.java#L23-L23) 实现了 [WebMvcConfigurer](file:///home/admin/.maven/repository/org/springframework/spring-webmvc/5.3.20/spring-webmvc-5.3.20.jar!/org/springframework/web/servlet/config/annotation/WebMvcConfigurer.class) 接口
- WebFlux 项目中不包含 Spring MVC 的依赖，因此找不到 [WebMvcConfigurer](file:///home/admin/.maven/repository/org/springframework/spring-webmvc/5.3.20/spring-webmvc-5.3.20.jar!/org/springframework/web/servlet/config/annotation/WebMvcConfigurer.class) 类

## 解决方案

1. **条件化配置**：使用 `@ConditionalOnMissingClass` 注解，仅在 [WebMvcConfigurer](file:///home/admin/.maven/repository/org/springframework/spring-webmvc/5.3.20/spring-webmvc-5.3.20.jar!/org/springframework/web/servlet/config/annotation/WebMvcConfigurer.class) 类不存在时加载特定配置
2. **自动配置排除**：在启动类中排除可能导致问题的自动配置
3. **属性控制**：通过配置属性控制MQ相关配置的启用状态

## 关键修复文件

- [GatewayServerApplication.java](src/main/java/com/caring/sass/GatewayServerApplication.java) - 启动类，排除问题配置
- [WebMvcConfigExcludeConfiguration.java](src/main/java/com/caring/sass/config/WebMvcConfigExcludeConfiguration.java) - 条件化配置排除
- [RabbitMqWebFluxConfiguration.java](src/main/java/com/caring/sass/config/RabbitMqWebFluxConfiguration.java) - WebFlux兼容的MQ配置
- [MqConfigurationProperties.java](src/main/java/com/caring/sass/config/MqConfigurationProperties.java) - 配置属性控制
- [RabbitMqConditionalConfiguration.java](src/main/java/com/caring/sass/config/RabbitMqConditionalConfiguration.java) - 条件化MQ配置

## 配置属性

在 [application.yml](../../../config-example/standalone/application.yml) 或 [bootstrap.yml](../../../caring-ai/caring-ai-server/src/main/resources/bootstrap.yml) 中可以使用以下属性：

```yaml
caring:
  mq:
    enabled: false  # 是否启用MQ配置，默认false
    enableWebConfig: false  # 是否启用Web配置，默认false
```

## 验证

启动网关服务后，应不再出现 `WebMvcConfigurer` 相关的错误。