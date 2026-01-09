package com.caring.sass.nursing.dto;

import cn.hutool.json.JSONUtil;
import com.caring.sass.nursing.enumeration.PlanEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NursingTaskExecuteParams {

        String tenantCode;

        String taskType;

        LocalDateTime startDate;

        PlanEnum planEnum;

        public String toJSONString() {
            return JSONUtil.toJsonStr(this);
        }

        public NursingTaskExecuteParams fromJSONString(String json) {
            return JSONUtil.toBean(json, NursingTaskExecuteParams.class);
        }

    }