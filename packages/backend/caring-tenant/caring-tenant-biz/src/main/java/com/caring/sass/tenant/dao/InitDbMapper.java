package com.caring.sass.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 初始化数据库DAO
 *
 * @author caring
 * @date 2019/09/02
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface InitDbMapper {
    /**
     * 创建数据库
     *
     * @param database
     * @return
     */
    int createDatabase(@Param("database") String database);


    /**
     * 删除数据库
     *
     * @param database
     * @return
     */
    int dropDatabase(@Param("database") String database);

    /**
     * 根据项目编码删除数据
     *
     * @param tenantCode 项目编码
     * @return
     */
    int deleteDate(@Param("tenantCode") String tenantCode);

}
