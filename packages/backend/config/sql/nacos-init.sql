/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

-- Nacos 数据库初始化脚本
-- 创建 nacos_config 数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `nacos_config` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;

-- 使用 nacos_config 数据库
USE `nacos_config`;

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info   */
/******************************************/
CREATE TABLE IF NOT EXISTS `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) DEFAULT NULL,
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) DEFAULT NULL,
  `c_use` varchar(64) DEFAULT NULL,
  `effect` varchar(64) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `c_schema` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_aggr   */
/******************************************/
CREATE TABLE IF NOT EXISTS `config_info_aggr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) NOT NULL COMMENT 'datum_id',
  `content` longtext NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='增加租户字段';


/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_beta   */
/******************************************/
CREATE TABLE IF NOT EXISTS `config_info_beta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_tag   */
/******************************************/
CREATE TABLE IF NOT EXISTS `config_info_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_tags_relation   */
/******************************************/
CREATE TABLE IF NOT EXISTS `config_tags_relation` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = group_capacity   */
/******************************************/
CREATE TABLE IF NOT EXISTS `group_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = his_config_info   */
/******************************************/
CREATE TABLE IF NOT EXISTS `his_config_info` (
  `id` bigint(64) unsigned NOT NULL,
  `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text,
  `src_ip` varchar(50) DEFAULT NULL,
  `op_type` char(10) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';


/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = tenant_capacity   */
/******************************************/
CREATE TABLE IF NOT EXISTS `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';


CREATE TABLE IF NOT EXISTS `tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) default '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) default '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE IF NOT EXISTS `users` (
	`username` varchar(50) NOT NULL PRIMARY KEY,
	`password` varchar(500) NOT NULL,
	`enabled` boolean NOT NULL
);

CREATE TABLE IF NOT EXISTS `roles` (
	`username` varchar(50) NOT NULL,
	`role` varchar(50) NOT NULL,
	UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE IF NOT EXISTS `permissions` (
    `role` varchar(50) NOT NULL,
    `resource` varchar(255) NOT NULL,
    `action` varchar(8) NOT NULL,
    UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

-- 插入默认用户
INSERT INTO users (username, password, enabled) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);

INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN');

-- Nacos 配置初始化
-- 先创建 dev 命名空间(如果不存在),然后获取其 UUID
INSERT IGNORE INTO tenant_info (kp, tenant_id, tenant_name, tenant_desc, create_source, gmt_create, gmt_modified)
VALUES ('1', UUID(), 'dev', '开发环境命名空间', 'sql', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

-- 获取 dev 命名空间的实际 UUID
SELECT @namespace_id := tenant_id 
FROM tenant_info 
WHERE tenant_name = 'dev' 
LIMIT 1;

SET @group_id = 'sass-cloud';

-- 1. 导入 common.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'common.yml',
    @group_id,
'# 在这里配置 所有的服务 和 所有环境 都相同的配置
caring:
  nacos:
    ip: localhost
    port: 8848
    username: nacos
    password: nacos
  seata:
    ip: localhost
    port: 8091
    namespace: public
  zipkin:
    enabled: false
  swagger:
    enabled: true
    license: Powered By caring
    licenseUrl: https://github.com/caring
    termsOfServiceUrl: https://github.com/caring
    version: 2.5.1
    contact:
      url: https://github.com/caring
      name: zuiou
      email: 306479353@qq.com
    global-operation-parameters:
      - name: token
        description: 用户身份token
        modelRef: String
        parameterType: header
        required: true
        # 默认值只是方便本地开发时，少填参数，生产环境请禁用swagger或者禁用默认参数
        defaultValue: "Bearer test"
      - name: Authorization
        description: 客户端信息
        modelRef: String
        parameterType: header
        required: true
        defaultValue: "Basic Y2FyaW5nX3VpOmNhcmluZ191aV9zZWNyZXQ="
      - name: tenant
        description: 租户编码
        modelRef: String
        parameterType: header
        required: true
        defaultValue: "MDAwMA=="
  injection:
    # 是否启用 远程数据 手动注入
    enabled: true
    # 是否启用 远程数据 注解注入
    aop-enabled: true
    guavaCache:
      enabled: true
      maximumSize: 1000
      refreshWriteTime: 10
      refreshThreadPoolSize: 1000
  security:
    enabled: true
    type: FEIGN
  log:
    enabled: true
    type: DB
  context:
    # 上下文拦截器
    pathPatterns: ''/**''
    order: -20
  # 任务调度中心配置
  job:
    enabled: false
    executor:
      log-path: ${logging.file.path}/${spring.application.name}/jobhandler.log
      app-name: ${spring.application.name}
      port: 39999
      access-token: lk9hjKIc5Yym
    admin:
      admin-addresses: http://localhost:8080/xxl-job-admin

server:
  undertow: # jetty  undertow
    io-threads: 4 # 设置IO线程数, 它主要执行非阻塞的任务,它们会负责多个连接, 默认设置每个CPU核心一个线程
    worker-threads: 32  # 阻塞任务线程池, 当执行类似servlet请求阻塞操作, undertow会从这个线程池中取得线程,它的值设置取决于系统的负载
    buffer-size: 2048  # 以下的配置会影响buffer,这些buffer会用于服务器连接的IO操作,有点类似netty的池化内存管理 , 每块buffer的空间大小,越小的空间被利用越充分
    direct-buffers: true  # 是否分配的直接内存

spring:
  zipkin:
    sender:
      type: RABBIT
    enabled: ${caring.zipkin.enabled}
    discovery-client-enabled: false
    baseUrl: http://localhost:9411/      # 老版本可以使用这个连接 http://caring-zipkin:8772/
    compression: # 压缩
      enabled: true
    locator:    # 通过nacos动态获取地址
      discovery:
        enabled: true
    rabbitmq: # 使用指定的队列
      queue: caring_zipkin
  #被追踪的可能性，默认是0.1 表示百分之10
  sleuth:
    enabled: ${caring.zipkin.enabled}
    sampler:
      probability: 1.0
  http:
    encoding:
      charset: UTF-8
      force: true
      enabled: true
  servlet:
    multipart:
      max-file-size: 512MB      # Max file size，默认1M
      max-request-size: 512MB   # Max request size，默认10M

dozer:
  mappingFiles:
    - classpath*:dozer/*.dozer.xml

management:
  endpoints:
    web:
      base-path: /actuator
      exposure:
        include: ''*''
  endpoint:
    health:
      show-details: ALWAYS
      enabled: true

feign:
  httpclient:
    enabled: false
  okhttp:
    enabled: true
  hystrix:
    enabled: true   # feign 熔断机制是否开启
    #支持压缩的mime types
  compression:  # 请求压缩
    request:
      enabled: true
      mime-types: text/xml,application/xml,application/json
      min-request-size: 2048
    response:  # 响应压缩
      enabled: true

ribbon:
  httpclient:
    enabled: false
  okhttp:
    enabled: true
  ReadTimeout: 30000     #
  ConnectTimeout: 30000  # [ribbon超时时间]大于[熔断超时],那么会先走熔断，相当于你配的ribbon超时就不生效了  ribbon和hystrix是同时生效的，哪个值小哪个生效
  MaxAutoRetries: 0             # 最大自动重试
  MaxAutoRetriesNextServer: 1   # 最大自动像下一个服务重试
  OkToRetryOnAllOperations: false  #无论是请求超时或者socket read timeout都进行重试，

caring-file-server:
  ribbon:
    ConnectTimeout: 300000
    ReadTimeout: 300000

hystrix:
  threadpool:
    default:
      coreSize: 1000 # #并发执行的最大线程数，默认10
      maxQueueSize: 1000 # #BlockingQueue的最大队列数
      queueSizeRejectionThreshold: 500 # #即使maxQueueSize没有达到，达到queueSizeRejectionThreshold该值后，请求也会被拒绝
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 120000  # 熔断超时 ribbon和hystrix是同时生效的，哪个值小哪个生效
    caring-file-server:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 1200000',
    MD5('common.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '公共配置 - 所有服务和环境共享',
    '全局配置',
    NULL,
    'yaml',
    NULL
);

-- 2. 导入 mysql.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'mysql.yml',
    @group_id,
'# mysql 个性化配置， 不同的环境，需要配置不同的链接信息，只需要将这段信息复制到具体环境的配置文件中进行修改即可
caring:
  mysql:
    ip: localhost
    port: 3306
    driverClassName: com.mysql.cj.jdbc.Driver
    database: caring_base_0000
    username: root
    password: change-this-password
    url: jdbc:mysql://${caring.mysql.ip}:${caring.mysql.port}/${caring.mysql.database}?serverTimezone=CTT&characterEncoding=utf8&useUnicode=true&useSSL=false&autoReconnect=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true
  database:
    tenantDatabasePrefix: caring_base
    multiTenantType: SCHEMA
    worker-id: 0
    data-center-id: 0
    isNotWrite: false
    isBlockAttack: false
    isSeata: false

# mysql 通用配置
spring:
  datasource:
    # 从这里开始(dynamic)，中间的这段配置用于 caring.database.multiTenantType == DATASOURCE 时
    # 联系群主
    # 从这里结束(dynamic)，中间的这段配置用于 caring.database.multiTenantType == DATASOURCE 时
    druid:
      enable: true
      # 从这里开始(druid)，中间的这段配置用于 caring.database.multiTenantType != DATASOURCE 时
      username: ${caring.mysql.username}
      password: ${caring.mysql.password}
      driver-class-name: ${caring.mysql.driverClassName}
      url: jdbc:mysql://${caring.mysql.ip}:${caring.mysql.port}/${caring.mysql.database}?serverTimezone=CTT&characterEncoding=utf8&useUnicode=true&useSSL=false&autoReconnect=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true
      db-type: mysql
      initialSize: 10
      minIdle: 10
      maxActive: 200
      max-wait: 60000
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
      validation-query: SELECT ''x''
      test-on-borrow: false
      test-on-return: false
      test-while-idle: true
      time-between-eviction-runs-millis: 60000  #配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      min-evictable-idle-time-millis: 300000    #配置一个连接在池中最小生存的时间，单位是毫秒
      filters: stat,wall
      filter:
        wall:
          enabled: true
          config:
            commentAllow: true
            multiStatementAllow: true
            noneBaseStatementAllow: true
      # 从这里结束(druid)，中间的这段配置用于 caring.database.multiTenantType != DATASOURCE 时

      # 以下的2段配置，同时适用于所有模式
      web-stat-filter:  # WebStatFilter配置，说明请参考Druid Wiki，配置_配置WebStatFilter
        enabled: true
        url-pattern: /*
        exclusions: "*.js , *.gif ,*.jpg ,*.png ,*.css ,*.ico , /druid/*"
        session-stat-max-count: 1000
        profile-enable: true
        session-stat-enable: false
      stat-view-servlet:  #展示Druid的统计信息,StatViewServlet的用途包括：1.提供监控信息展示的html页面2.提供监控信息的JSON API
        enabled: true
        url-pattern: /druid/*   #根据配置中的url-pattern来访问内置监控页面，如果是上面的配置，内置监控页面的首页是/druid/index.html例如：http://127.0.0.1:9000/druid/index.html
        reset-enable: true    #允许清空统计数据
        login-username: ${DRUID_USERNAME:admin}
        login-password: ${DRUID_PASSWORD:}

mybatis-plus:
  mapper-locations:
    - classpath*:mapper_**/**/*Mapper.xml
  #实体扫描，多个package用逗号或者分号分隔
  typeAliasesPackage: com.caring.sass.*.entity;com.caring.sass.database.mybatis.typehandler
  typeEnumsPackage: com.caring.sass.*.enumeration
  global-config:
    db-config:
      id-type: INPUT
      insert-strategy: NOT_NULL
      update-strategy: NOT_NULL
      select-strategy: NOT_EMPTY
  configuration:
    #配置返回数据库(column下划线命名&&返回java实体是驼峰命名)，自动匹配无需as（没开启这个，SQL需要写as： select user_id as userId）
    map-underscore-to-camel-case: true
    cache-enabled: false
    #配置JdbcTypeForNull, oracle数据库必须配置
    jdbc-type-for-null: ''null''

# 分布式事务相关
seata:
  enabled: ${caring.database.isSeata}
  enableAutoDataSourceProxy: true
  tx-service-group: caring_admin_seata_tx_group
  registry:
    type: nacos
    nacos:
      server-addr: ${caring.nacos.ip}:${caring.nacos.port}
      username: ${caring.nacos.username}
      password: ${caring.nacos.password}
      namespace: ${caring.seata.namespace}
  config:
    type: nacos
    nacos:
      server-addr: ${caring.nacos.ip}:${caring.nacos.port}
      username: ${caring.nacos.username}
      password: ${caring.nacos.password}
      namespace: ${caring.seata.namespace}
  service:
    grouplist:
      default: ${caring.seata.ip:}:${caring.seata.port:}
    vgroup-mapping:
      caring_admin_seata_tx_group: default
    disable-global-transaction: false
  client:
    rm:
      report-success-enable: false',
    MD5('mysql.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    'MySQL 数据库配置',
    '数据库连接配置',
    NULL,
    'yaml',
    NULL
);

-- 3. 导入 redis.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'redis.yml',
    @group_id,
'# redis 通用配置， 不同的环境，需要配置不同的链接信息，只需要将这段信息复制到具体环境的配置文件中进行修改即可
# 如：复制到caring-authority-server-dev.yml中将数据库名和ip改掉
caring:
  redis:
    ip: localhost
    port: 6379
    password: change-this-password
    database: 0

spring:
  cache:
    type: GENERIC
  redis:
    host: ${caring.redis.ip}
    port: ${caring.redis.port}
    database: ${caring.redis.database}
    password: ${caring.redis.password}
j2cache:
  #  config-location: /j2cache.properties
  open-spring-cache: true
  cache-clean-mode: passive
  allow-null-values: true
  redis-client: lettuce
  l2-cache-open: true
  # l2-cache-open: false     # 关闭二级缓存
  broadcast: net.oschina.j2cache.cache.support.redis.SpringRedisPubSubPolicy
  #  broadcast: jgroups       # 关闭二级缓存
  L1:
    provider_class: caffeine
  L2:
    provider_class: net.oschina.j2cache.cache.support.redis.SpringRedisProvider
    config_section: lettuce
  sync_ttl_to_redis: true
  default_cache_null_object: false
  serialization: fst
caffeine:
  properties: /j2cache/caffeine.properties   # 这个配置文件需要放在项目中
lettuce:
  mode: single
  namespace: ''''
  storage: generic
  channel: j2cache
  scheme: redis
  hosts: ${caring.redis.ip}:${caring.redis.port}
  database: ${caring.redis.database}
  password: ${caring.redis.password}
  sentinelMasterId: ''''
  maxTotal: 100
  maxIdle: 10
  minIdle: 10
  timeout: 10000',
    MD5('redis.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    'Redis 缓存配置',
    'Redis连接配置',
    NULL,
    'yaml',
    NULL
);

-- 4. 导入 rabbitmq.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'rabbitmq.yml',
    @group_id,
'caring:
  rabbitmq:
    # 若系统中有除了zipkin之外的地方使用了mq ，则一定不能设置成false
    enabled: false
    ip: localhost
    port: 5672
    username: caring
    password: caring

spring:
  rabbitmq:
    enabled: ${caring.rabbitmq.enabled}
    host: ${caring.rabbitmq.ip}
    port: ${caring.rabbitmq.port}
    username: ${caring.rabbitmq.username}
    password: ${caring.rabbitmq.password}
    listener:
      type: direct # simple direct',
    MD5('rabbitmq.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    'RabbitMQ 消息队列配置',
    'RabbitMQ连接配置',
    NULL,
    'yaml',
    NULL
);

-- 5. 导入 third-party.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'third-party.yml',
    @group_id,
'# 第三方服务配置
# 根据实际需求填入配置值

# 阿里云OSS配置
aliyun:
  oss:
    endpoint: YOUR_OSS_ENDPOINT
    accessKeyId: ${ALIYUN_OSS_ACCESS_KEY_ID:}
    accessKeySecret: ${ALIYUN_OSS_ACCESS_KEY_SECRET:}
    bucketName: YOUR_BUCKET_NAME

# 火山引擎配置
volcengine:
  api:
    accessKeyId: ${VOLCENGINE_ACCESS_KEY_ID:}
    secretAccessKey: ${VOLCENGINE_SECRET_ACCESS_KEY:}

# MiniMax API配置
minimax:
  api:
    key: ${MINIMAX_API_KEY:}

# 豆包TTS配置
doubao:
  tts:
    token: ${DOUBAO_TTS_TOKEN:}

# 短信服务配置
sms:
  zhongzhengyun:
    password: ${SMS_ZHONGZHENGYUN_PASSWORD:}
    restServiceUrl: YOUR_SMS_SERVICE_URL
  aliyun:
    accessKeyId: ${ALIYUN_SMS_ACCESS_KEY_ID:}
    accessKeySecret: ${ALIYUN_SMS_ACCESS_KEY_SECRET:}

# 微信配置
wechat:
  app:
    appId: ${WECHAT_APP_ID:}
    appSecret: ${WECHAT_APP_SECRET:}
    token: ${WECHAT_TOKEN:}
    aesKey: ${WECHAT_AES_KEY:}
  pay:
    appId: ${WECHAT_PAY_APP_ID:}
    mchId: ${WECHAT_PAY_MCH_ID:}
    apiKey: ${WECHAT_PAY_API_KEY:}
    certPath: ${WECHAT_PAY_CERT_PATH:}
    keyPath: ${WECHAT_PAY_KEY_PATH:}
    notifyUrl: YOUR_WECHAT_PAY_NOTIFY_URL

# 华为云OBS配置
huawei:
  obs:
    endpoint: YOUR_OBS_ENDPOINT
    accessKeyId: ${HUAWEI_OBS_ACCESS_KEY_ID:}
    accessKeySecret: ${HUAWEI_OBS_ACCESS_KEY_SECRET:}
    bucketName: YOUR_BUCKET_NAME

# 文件服务器配置
file:
  server:
    uriPrefix: YOUR_FILE_SERVER_URI_PREFIX

# 搜索服务配置
search:
  searxng:
    url: YOUR_SEARXNG_URL

# API服务配置
api:
  baseUrl: YOUR_API_BASE_URL',
    MD5('third-party.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '第三方服务配置',
    '第三方API配置',
    NULL,
    'yaml',
    NULL
);

-- 6. 导入 caring-authority-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-authority-server.yml',
    @group_id,
'# 在这里配置 权限服务 所有环境都能使用的配置
caring:
  swagger:
    enabled: true
    docket:
      auth:
        title: 权限模块
        base-package: com.caring.sass.authority.controller.auth
      common:
        title: 公共模块
        base-package: com.caring.sass.authority.controller.common
      core:
        title: 组织模块
        base-package: com.caring.sass.authority.controller.core

server:
  port: 8764',
    MD5('caring-authority-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '权限服务配置',
    '权限服务 - caring-authority-server',
    NULL,
    'yaml',
    NULL
);

-- 7. 导入 caring-gateway-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-gateway-server.yml',
    @group_id,
'caring:
  log:
    enabled: false
  webmvc:
    enabled: false
  database:
    tenantDatabasePrefix: caring_extend

spring:
  autoconfigure:
    exclude: io.seata.integration.http.HttpAutoConfiguration
  cloud:
    gateway:
      x-forwarded:
        prefixEnabled: false
      discovery:
        locator:
          enabled: true
          lowerCaseServiceId: true
      routes:
        - id: oauth
          uri: lb://caring-oauth-server
          predicates:
            - Path=/oauth/**
          filters:
            - StripPrefix=1
        - id: tenant
          uri: lb://caring-tenant-server
          predicates:
            - Path=/tenant/**
          filters:
            - StripPrefix=1
        - id: file
          uri: lb://caring-file-server
          predicates:
            - Path=/file/**
          filters:
            - StripPrefix=1
        - id: authority
          uri: lb://caring-authority-server
          predicates:
            - Path=/authority/**
          filters:
            - StripPrefix=1
        - id: msgs
          uri: lb://caring-msgs-server
          predicates:
            - Path=/msgs/**
          filters:
            - StripPrefix=1
        - id: demo
          uri: lb://caring-demo-server
          predicates:
            - Path=/demo/**
          filters:
            - StripPrefix=1
        - id: order
          uri: lb://caring-order-server
          predicates:
            - Path=/order/**
          filters:
            - StripPrefix=1
        - id: center
          uri: lb://caring-center-server
          predicates:
            - Path=/center/**
          filters:
            - StripPrefix=1
      default-filters:
        - name: Hystrix
          args:
            name: default
            fallbackUri: ''forward:/fallback''


server:
  port: 8760
  servlet:
    context-path: /api



ignore:
  token:
    baseUri:
      - /error
      - /actuator/**
      - /gate/**
      - /static/**
      - /anno/**
      - /**/anno/**
      - /**/swagger-ui.html
      - /**/doc.html
      - /**/webjars/**
      - /**/v2/api-docs/**
      - /**/v2/api-docs-ext/**
      - /**/swagger-resources/**
      - /ds/**',
    MD5('caring-gateway-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '网关服务配置',
    '网关服务 - caring-gateway-server',
    NULL,
    'yaml',
    NULL
);

-- 8. 导入 caring-file-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-file-server.yml',
    @group_id,
'caring:
  nginx:
    ip: localhost   # 正式环境需要将该ip设置成nginx对应的 公网ip
    port: 80    # 正式环境需要将该ip设置成nginx对应的 公网端口
  swagger:
    enabled: true
    docket:
      file:
        title: 文件服务
        base-package: com.caring.sass.file.controller
  file:
    type: LOCAL # FAST_DFS LOCAL
    storage-path: /data/projects/uploadfile/file/     # 文件存储路径  （ 某些版本的 window 需要改成  D:\\data\\projects\\uploadfile\\file\\  ）
    uriPrefix: http://${caring.nginx.ip}:${caring.nginx.port}/file/   # 文件访问 需要通过这个uri前缀进行访问
    inner-uri-prefix: null  #  内网的url前缀
    down-by-id: http://${caring.nginx.ip}:${caring.nginx.port}/api/file/attachment/download?ids[]=%s
    down-by-biz-id: http://${caring.nginx.ip}:${caring.nginx.port}/api/file/attachment/download/biz?bizIds[]=%s
    down-by-url: http://${caring.nginx.ip}:${caring.nginx.port}/api/file/attachment/download/url?url=%s&filename=%s
    ali:
      # 请填写自己的阿里云存储配置
      uriPrefix: http://test.oss-cn-shenzhen.aliyuncs.com/
      bucket-name: test
      endpoint: http://oss-cn-shenzhen.aliyuncs.com
      access-key-id: test
      access-key-secret: test

#FAST_DFS配置
fdfs:
  soTimeout: 1500
  connectTimeout: 600
  thumb-image:
    width: 150
    height: 150
  tracker-list:
    - 192.168.1.2:22122
  pool:
    #从池中借出的对象的最大数目
    max-total: 153
    max-wait-millis: 102
    jmx-name-base: 1
    jmx-name-prefix: 1


server:
  servlet:
    context-path:
  port: 8765',
    MD5('caring-file-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '文件服务配置',
    '文件服务 - caring-file-server',
    NULL,
    'yaml',
    NULL
);

-- 9. 导入 caring-ucenter-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-ucenter-server.yml',
    @group_id,
'caring:
  swagger:
    docket:
      center:
        title: 核心业务服务
        base-package: com.caring.sass.center.controller

server:
  port: 31005',
    MD5('caring-ucenter-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '核心业务服务配置',
    '核心业务服务 - caring-center-server',
    NULL,
    'yaml',
    NULL
);

-- 10. 导入 caring-cms-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-cms-server.yml',
    @group_id,
'caring:
  swagger:
    docket:
      cms:
        title: 内容管理服务
        base-package: com.caring.sass.cms.controller

server:
  port: 30008',
    MD5('caring-cms-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '内容管理服务配置',
    '内容管理服务 - caring-cms-server',
    NULL,
    'yaml',
    NULL
);

-- 11. 导入 caring-demo-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-demo-server.yml',
    @group_id,
'caring:
  database:
    isSeata: false
    tenantDatabasePrefix: caring_extend
  swagger:
    docket:
      demo:
        title: 演示服务
        base-package: com.caring.sass.demo.controller

logging:
  io.seata: debug
  org.springframework.cloud.alibaba.seata.web: debug

server:
  servlet:
    context-path:
  port: 8770



spring:
  cloud:
    nacos:
      discovery:
        metadata:
          grayversion: demo',
    MD5('caring-demo-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '演示服务配置',
    '演示服务 - caring-demo-server',
    NULL,
    'yaml',
    NULL
);

-- 12. 导入 caring-monitor.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-monitor.yml',
    @group_id,
'server:
  port: 8762
  servlet:
    context-path: /${spring.application.name}   # 监控系统项目名

turbine:
  stream:
    port: 8763

spring:
  #  mail:
  #    host: smtp.163.com
  #    username: adadminfocus@163.com
  #    password: adsugar123
  #    properties:
  #      smtp:
  #        auth: true
  #        starttls:
  #          enable: true
  #          required: true
  boot:
    admin:
      routes:
        endpoints: env,metrics,dump,jolokia,info,configprops,trace,logfile,refresh,flyway,liquibase,heapdump,loggers,auditevents,hystrix.stream
      turbine:
        clusters: default
        location: http://${caring.local-ip}:${turbine.stream.port}${server.servlet.context-path}
  #      context-path: ${management.context-path}
  #      notify:
  #        mail:
  #          to: 306479353@qq.com
  #          from: adadminfocus@163.com
  #          ignore-changes: UNKNOWN:UP
  security:
    user:
      name: caring
      password: caring
      roles: USER

management:
  endpoints:
    web:
      base-path: /actuator
      exposure:
        include: ''*''
  endpoint:
    health:
      show-details: ALWAYS
      enabled: true',
    MD5('caring-monitor.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '监控服务配置',
    '监控服务 - caring-monitor',
    NULL,
    'yaml',
    NULL
);

-- 13. 导入 caring-msgs-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-msgs-server.yml',
    @group_id,
'caring:
  swagger:
    enabled: false
    docket:
      sms:
        title: 短信模块
        base-package: com.caring.sass.sms.controller
      msgs:
        title: 消息模块
        base-package: com.caring.sass.msgs.controller
      mail:
        title: 邮件模块
        base-package: com.caring.sass.mail.controller
  cache:
    type: REDIS   # CAFFEINE
    def:
      keyPrefix: ${spring.profiles.active}
    configs:
      caring:
        timeToLive: 1h
        keyPrefix: caring
      test:
        timeToLive: 2h
        keyPrefix: test

# 环信IM配置
hx:
  im:
    appName: ${HX_APP_NAME:your_app_name}
    clientId: ${HX_CLIENT_ID:your_client_id}
    clientSecret: ${HX_CLIENT_SECRET:your_client_secret}
    grantType: client_credentials
    orgName: ${HX_ORG_NAME:your_org_name}
    userPassword: ${HX_USER_PASSWORD:default_password}

# 短信签名配置
signature:
  config:
    name: ${SIGNATURE_NAME:卡柠科技}
    allergicName: ${ALLERGIC_NAME:敏瑞健康}
    systemEnvironment: ${SYSTEM_ENV:p}
    # 患者延迟推送咨询通知模版code
    patient_time_out_consultation_notice: ${SMS_PATIENT_TIME_OUT:SMS_TEMPLATE_CODE}
    # 患者今日待办模版code
    patient_today_to_do_list: ${SMS_PATIENT_TODO:SMS_TEMPLATE_CODE}
    # 患者病例讨论开始通知模版code
    patient_notice_of_the_start_case_discussion: ${SMS_PATIENT_CASE_START:SMS_TEMPLATE_CODE}
    # 患者转诊通知
    patient_referral_notice: ${SMS_PATIENT_REFERRAL:SMS_TEMPLATE_CODE}
    患者@咨询通知模版code
    patient_immediately_consultation_notice: ${SMS_PATIENT_IMMEDIATE:SMS_TEMPLATE_CODE}
    # 医生延时咨询通知
    doctor_time_out_consultation_notice: ${SMS_DOCTOR_TIME_OUT:SMS_TEMPLATE_CODE}
    # 医生今日待办模版
    doctor_today_to_do_list: ${SMS_DOCTOR_TODO:SMS_TEMPLATE_CODE}
    # 医生病例讨论邀请通知
    doctor_notice_of_invite_case_discussion: ${SMS_DOCTOR_CASE_INVITE:SMS_TEMPLATE_CODE}
    # 患者信息完整度
    patient_integrity: ${SMS_PATIENT_INTEGRITY:SMS_TEMPLATE_CODE}
    # 患者购药预警
    patient_drug_purchase_warning: ${SMS_PATIENT_DRUG_WARNING:SMS_TEMPLATE_CODE}

server:
  port: 8768',
    MD5('caring-msgs-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '消息服务配置',
    '消息服务 - caring-msgs-server',
    NULL,
    'yaml',
    NULL
);

-- 14. 导入 caring-nursing-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-nursing-server.yml',
    @group_id,
'caring:
  swagger:
    docket:
      drugs:
        title: 用药模块
        base-package: com.caring.sass.nursing.controller.drugs
      form:
        title: 表单模块
        base-package: com.caring.sass.nursing.controller.form
      plan:
        title: 护理计划模块
        base-package: com.caring.sass.nursing.controller.plan
      statistics:
        title: 统计模块
        base-package: com.caring.sass.nursing.controller.statistics
      tag:
        title: 标签模块
        base-package: com.caring.sass.nursing.controller.tag
server:
  port: 31005',
    MD5('caring-nursing-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '护理服务配置',
    '护理服务 - caring-nursing-server',
    NULL,
    'yaml',
    NULL
);

-- 15. 导入 caring-oauth-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-oauth-server.yml',
    @group_id,
'server:
  port: 8773

caring:
  swagger:
    enabled: true
    title: 认证服务
    base-package: com.caring.sass


authentication:
  expire: 28800               # token有效期为8小时
  refreshExpire: 86400        # 刷新token有效期为24小时',
    MD5('caring-oauth-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '认证服务配置',
    '认证服务 - caring-oauth-server',
    NULL,
    'yaml',
    NULL
);

-- 16. 导入 caring-order-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-order-server.yml',
    @group_id,
'caring:
  database:
    isSeata: false
    tenantDatabasePrefix: caring_extend
  swagger:
    base-package: com.caring.sass.order.controller
    title: 订单服务



logging:
  io.seata: debug
  org.springframework.cloud.alibaba.seata.web: debug

server:
  servlet:
    context-path:
  port: 8769',
    MD5('caring-order-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '订单服务配置',
    '订单服务 - caring-order-server',
    NULL,
    'yaml',
    NULL
);

-- 17. 导入 caring-tenant-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-tenant-server.yml',
    @group_id,
'# 在这里配置 权限服务 所有环境都能使用的配置
caring:
  swagger:
    enabled: true
    docket:
      tenant:
        title: 租户模块
        base-package: com.caring.sass.tenant.controller

server:
  port: 8771',
    MD5('caring-tenant-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '租户服务配置',
    '租户服务 - caring-tenant-server',
    NULL,
    'yaml',
    NULL
);

-- 18. 导入 caring-wx-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-wx-server.yml',
    @group_id,
'caring:
  swagger:
    docket:
      config:
        title: 配置模块
        base-package: com.caring.sass.wx.controller.config
      keyword:
          title: 关键字模块
          base-package: com.caring.sass.wx.controller.keyword
      menu:
          title: 菜单模块
          base-package: com.caring.sass.wx.controller.menu
      template:
          title: 模板模块
          base-package: com.caring.sass.wx.controller.template
      guide:
          title: 注册引导模块
          base-package: com.caring.sass.wx.controller.guide

server:
  port: 30605',
    MD5('caring-wx-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    '微信服务配置',
    '微信服务 - caring-wx-server',
    NULL,
    'yaml',
    NULL
);

-- 19. 导入 caring-zuul-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-zuul-server.yml',
    @group_id,
'caring:
  swagger:
    enabled: true
    title: 网关模块
    base-package: com.caring.sass.zuul.controller
  log:
    enabled: false
  webmvc:
    enabled: true
  database:
    tenantDatabasePrefix: caring_extend

spring:
  mvc:
    servlet:
      path: /gate

server:
  port: 8760
  servlet:
    context-path: /api  # = server.servlet.context-path

zuul:
  # 一些比较敏感的请求头，不想通过zuul传递过去， 可以通过该属性进行设置
  sensitiveHeaders: ''''
  #添加代理头
  add-proxy-headers: true
  #  debug:
  #    request: true
  #  include-debug-header: true
  retryable: false
  servlet-path: /         # 默认是/zuul , 上传文件需要加/zuul前缀才不会出现乱码，这个改成/ 即可不加前缀
  ignored-services: "*"   # 忽略eureka上的所有服务
  #  prefix: /api #为zuul设置一个公共的前缀
  #  strip-prefix: false     #对于代理前缀默认会被移除   故加入false  表示不要移除
  routes:  # 路由配置方式
    authority: # 其中 authority 是路由名称，可以随便定义，但是path和service-id需要一一对应
      path: /authority/**
      serviceId: caring-authority-server
    oauth:
      path: /oauth/**
      serviceId: caring-oauth-server
    tenant:
      path: /tenant/**
      serviceId: caring-tenant-server
    file:
      path: /file/**
      serviceId: caring-file-server
    msgs:
      path: /msgs/**
      serviceId: caring-msgs-server
    order:
      path: /order/**
      serviceId: caring-order-server
    demo:
      path: /demo/**
      serviceId: caring-demo-server
    center:
      path: /center/**
      serviceId: caring-center-server


ignore:
  token:
    url:
      - /error
      - /actuator/**
      - /gate/**
      - /static/**
      - /anno/**
      - /**/anno/**
      - /**/swagger-ui.html
      - /**/doc.html
      - /**/webjars/**
      - /**/v2/api-docs/**
      - /**/v2/api-docs-ext/**
      - /**/swagger-resources/**
      - /api/gate/ds/**',
    MD5('caring-zuul-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    'Zuul网关服务配置',
    'Zuul网关服务 - caring-zuul-server',
    NULL,
    'yaml',
    NULL
);

-- 20. 导入 caring-ai-server.yml 配置
INSERT INTO config_info (data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema)
VALUES (
    'caring-ai-server.yml',
    @group_id,
'# Ai配置
caring:
  swagger:
    docket:
      config:
        title: ai助手
        base-package: com.caring.sass.ai.controller
  bing:
    subscriptionKey: ${BING_SUBSCRIPTION_KEY:your_subscription_key}
    endpoint: https://api.bing.microsoft.com/v7.0/search

langchain4j:
  open-ai:
    streaming-chat-model:
      api-key: ${OPENAI_API_KEY:your_api_key}
      base-url: ${OPENAI_BASE_URL:http://openai.caringsaas.cn/v1}
      model-name: gpt-3.5-turbo
      temperature: 0.0
      timeout: PT60S
      log-requests: false
      log-responses: false
    chat-model:
      api-key: ${OPENAI_API_KEY:your_api_key}
      base-url: ${OPENAI_BASE_URL:http://openai.caringsaas.cn/v1}
      model-name: gpt-4o-mini
      temperature: 0.0
      timeout: PT60S
      log-requests: true
      log-responses: true

card:
  config:
    appId: ${CARD_APP_ID:your_wechat_app_id}

signature:
  config:
    name: ${SIGNATURE_NAME:敏瑞健康}
    templateCode: ${SMS_TEMPLATE_CODE:SMS_TEMPLATE_CODE}
    ALIBABA_CLOUD_ACCESS_KEY_ID: ${ALIBABA_CLOUD_ACCESS_KEY_ID:your_access_key_id}
    ALIBABA_CLOUD_ACCESS_KEY_SECRET: ${ALIBABA_CLOUD_ACCESS_KEY_SECRET:your_access_key_secret}

face:
  config:
    apiKey: ${FACE_API_KEY:your_api_key}
    apiSecret: ${FACE_API_SECRET:your_api_secret}
    merge_rate: 100
    feature_rate: 100
    new_user_free: 1
    merge_image_cost: 990
    merge_image_goods_name: 制作艺术头像
    reqKey: faceswap
    source_similarity: 1
    gpen: 0.2
    skin: 0

dify:
  config:
    APIKey: ${DIFY_API_KEY:dataset_your_api_key}
    apiDomain: ${DIFY_API_DOMAIN:http://ai.caringsaas.cn/v1}
    difyKnowledgeId: ${DIFY_KNOWLEDGE_ID:your_knowledge_id}
    metadataCategoryId: ${DIFY_METADATA_CATEGORY_ID:your_category_id}
    metadataOwnershipId: ${DIFY_METADATA_OWNERSHIP_ID:your_ownership_id}
    flowControl: 1000
    textIndexingTechnique: economy
    case_create_key: ${DIFY_CASE_CREATE_KEY:app_your_key}
    textProcessRule: automatic
    titleCreateWorkApiKey: ${DIFY_TITLE_CREATE_KEY:app_your_key}
    zhuan_ye_xue_shu_key_word_apikey: ${DIFY_ZHUAN_YE_XUE_SHU_KEY:app_your_key}
    ge_ren_cheng_guo_key_word_apikey: ${DIFY_GE_REN_CHENG_GUO_KEY:app_your_key}
    bing_li_key_word_apikey: ${DIFY_BING_LI_KEY:app_your_key}
    ri_chang_shou_ji_key_word_apikey: ${DIFY_RI_CHANG_SHOU_JI_KEY:app_your_key}
    zhuanyexueshu_condition_extract_apikey: ${DIFY_ZHUANYEXUESHU_CONDITION_KEY:app_your_key}
    bingliku_condition_extract_apikey: ${DIFY_BINGLIKU_CONDITION_KEY:app_your_key}
    gerenchengguo_condition_extract_apikey: ${DIFY_GERENCHENGGUO_CONDITION_KEY:app_your_key}
    richangshouji_condition_extract_apikey: ${DIFY_RICHANGSHOUJI_CONDITION_KEY:app_your_key}
    docu_search_translation_and_summary: ${DIFY_DOCU_SEARCH_KEY:app_your_key}
    general_question_key: ${DIFY_GENERAL_QUESTION_KEY:app_your_key}
    card_human_video_text_create_key: ${DIFY_CARD_HUMAN_VIDEO_KEY:app_your_key}
    podcastApiDomain: ${DIFY_PODCAST_DOMAIN:http://ai.caringsaas.cn/v1}
    podcast_bot_key: ${DIFY_PODCAST_BOT_KEY:app_your_key}
    textual_bot_key: ${DIFY_TEXTUAL_BOT_KEY:app_your_key}
    podcast_yi_zhisheng: ${DIFY_PODCAST_YI_ZHISHENG:app_your_key}

knowpay:
  config:
    merchantId: ${KNOWPAY_MERCHANT_ID:your_merchant_id}
    appId: ${KNOWPAY_APP_ID:your_wechat_app_id}
    basicMembershipPrice: 99900
    basicMembershipName: 体验版
    professionalVersionPrice: 499900
    professionalVersionName: 专业版
    gong_zhong_hao_app_id: ${KNOWPAY_GZH_APP_ID:your_wechat_app_id}

knowchild:
  config:
    saibolanDomain: https://openapi.caringcloud.cn
    aiStudioDomain: https://cstudio.allercura.cn

cardpay:
  config:
    merchantId: ${CARDPAY_MERCHANT_ID:your_merchant_id}
    appId: ${CARDPAY_APP_ID:your_wechat_app_id}
    basicMembershipPrice: 29900
    basicMembershipName: 科普名片基础版
    membershipPrice: 500000
    membershipName: 科普名片会员版

ckdpay:
  config:
    merchantId: ${CKDPAY_MERCHANT_ID:your_merchant_id}
    appId: ${CKDPAY_APP_ID:your_wechat_app_id}
    monthlyExpenses: 990
    monthlyExpensesName: 包月会员
    annualCost: 9900
    annualCostName: 包年会员
    lifetime: 19900
    lifeTimeName: 终身会员

human:
  config:
    speakerAppId: ${HUMAN_SPEAKER_APP_ID:your_speaker_app_id}
    speakerAccessToken: ${HUMAN_SPEAKER_ACCESS_TOKEN:your_access_token}
    baidu123AppId: ${HUMAN_BAIDU_APP_ID:your_baidu_app_id}
    baidu123AppSecret: ${HUMAN_BAIDU_APP_SECRET:your_baidu_app_secret}

# 阿里云oss配置
aliyun:
  oss:
    accessKeyId: ${ALIYUN_OSS_ACCESS_KEY_ID:your_access_key_id}
    secret: ${ALIYUN_OSS_SECRET:your_secret}
    endpoint: https://oss-cn-beijing-internal.aliyuncs.com
    bucketName: ${ALIYUN_OSS_BUCKET_NAME:your_bucket_name}
    TemplateId: ${ALIYUN_OSS_TEMPLATE_ID:your_template_id}
    pipelineId: ${ALIYUN_OSS_PIPELINE_ID:your_pipeline_id}

# 请填写自己的华为云存储配置
file:
  hw:
    bucket-name: ${HW_BUCKET_NAME:your_bucket_name}
    end-point: obs.cn-north-4.myhuaweicloud.com
    access-key: ${HW_ACCESS_KEY:your_access_key}
    security-key: ${HW_SECURITY_KEY:your_security_key}
    # 北京四的项目ID
    x-project_id: ${HW_PROJECT_ID:your_project_id}

textual:
  wxpay:
    merchantId: ${TEXTUAL_MERCHANT_ID:your_merchant_id}
    appId: ${TEXTUAL_APP_ID:your_wechat_app_id}
    gongnZhifuAppId: ${TEXTUAL_GZH_APP_ID:your_wechat_app_id}
    annualCost: 1
    annualCostName: 包年会员
    newUserRegister: 100
    chiefPhysicianUseRegister: 100
    annualGetEnergyBeans: 0
    energyBeans200Cost:  10000
    energyBeans800Cost:  30000
    energyBeans500Cost:  50000
    energyBeans1000Cost: 100000
    energyBeans1500Cost: 150000
    energyBeans2000Cost: 200000
    energyBeans3000Cost: 100000
    energyBeans5000Cost: 500000
    soundCloningSpend: 30
    imageTextSpend: 2
    reStartTextSpend: 2
    createPodcastSpend: 10
    createPptSpend: 20
    reCreatePptOutlineSpend: 20
    createVideoSpend: 60
    humanTemplateSpend: 0

article:
  wxpay:
    merchantId: ${ARTICLE_MERCHANT_ID:your_merchant_id}
    appId: ${ARTICLE_APP_ID:your_wechat_app_id}
    gongnZhifuAppId: ${ARTICLE_GZH_APP_ID:your_wechat_app_id}
    annualCost: 499900
    annualCostName: 包年会员
    newUserRegister: 200
    chiefPhysicianUseRegister: 500
    annualGetEnergyBeans: 2000
    energyBeans200Cost:  10000
    energyBeans800Cost:  30000
    energyBeans500Cost:  50000
    energyBeans1000Cost: 100000
    energyBeans1500Cost: 150000
    energyBeans2000Cost: 200000
    energyBeans3000Cost: 100000
    energyBeans5000Cost: 500000
    soundCloningSpend: 30
    imageTextSpend: 1
    createPodcastSpend: 10
    podcastQuickListeningEssence: 20
    podcastDeepDiscovery: 20
    createVideoSpend: 60
    humanTemplateSpend: 0

call:
  config:
    amountPerMinute: 20

minimax:
  config:
    maxRpm: 30

coze:
  config:
    domain: https://api.coze.cn
    authorization: ${COZE_AUTHORIZATION:Bearer_your_token}
    faceBotId: ${COZE_FACE_BOT_ID:your_bot_id}

server:
  port: 30607',
    MD5('caring-ai-server.yml'),
    NOW(),
    NOW(),
    'nacos',
    '127.0.0.1',
    NULL,
    @namespace_id,
    'AI服务配置',
    'AI服务 - caring-ai-server',
    NULL,
    'yaml',
    NULL
);

-- 显示导入结果
SELECT 'Nacos 数据库和配置初始化完成！' AS message;
SELECT data_id, group_id, tenant_id, c_desc, gmt_create
FROM config_info
WHERE tenant_id = @namespace_id AND group_id = @group_id
ORDER BY gmt_create;