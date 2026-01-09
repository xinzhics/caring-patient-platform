package com.caring.sass.nursing.service.form.impl;



import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.database.properties.DatabaseProperties;
import com.caring.sass.nursing.dao.form.FormFieldsGroupMapper;
import com.caring.sass.nursing.entity.form.FormFieldsGroup;
import com.caring.sass.nursing.service.form.FormFieldsGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务实现类
 * 表单题目的分组规则
 * </p>
 *
 * @author 杨帅
 * @date 2023-10-11
 */
@Slf4j
@Service

public class FormFieldsGroupServiceImpl extends SuperServiceImpl<FormFieldsGroupMapper, FormFieldsGroup> implements FormFieldsGroupService {

    @Autowired
    DatabaseProperties databaseProperties;

    @Override
    public boolean save(FormFieldsGroup model) {
        return super.save(model);
    }

    public static void main(String[] args) {
        UUID uuid = UUID.randomUUID();
        String string = uuid.toString();
        System.out.println(string);
    }

    /**
     * 创建一个临时的ID。给前端
     * @return
     */
    public Long createGroupTempId() {
        DatabaseProperties.Id id = databaseProperties.getId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());
        long nextId = snowflake.nextId();
        return nextId;
    }


}
