package com.caring.sass.nursing.dao.plan;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.caring.sass.base.mapper.SuperMapper;
import com.caring.sass.nursing.entity.plan.ReminderLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ReminderLogMapper extends SuperMapper<ReminderLog> {


    /**
     *
     * @param pushTime
     * @return
     */
    @Select("SELECT tenant_code FROM t_nursing_plan_reminder_log where status_ = -1 and wait_push_time = #{pushTime} group by tenant_code")
    @Results({
            @Result(column="tenant_code", javaType=String.class, property="tenant_code"),
    })
    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    List<String> selectTenantCodeByNeedPushTime(@Param("pushTime") LocalDateTime pushTime);

}
