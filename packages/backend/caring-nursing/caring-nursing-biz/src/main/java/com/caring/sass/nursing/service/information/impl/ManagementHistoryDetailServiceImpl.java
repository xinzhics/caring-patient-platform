package com.caring.sass.nursing.service.information.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.nursing.dao.information.ManagementHistoryDetailMapper;
import com.caring.sass.nursing.entity.information.ManagementHistoryDetail;
import com.caring.sass.nursing.service.information.ManagementHistoryDetailService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Patient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 管理历史详细记录
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Service
@AllArgsConstructor
public class ManagementHistoryDetailServiceImpl
        extends SuperServiceImpl<ManagementHistoryDetailMapper, ManagementHistoryDetail> implements ManagementHistoryDetailService {

    private final PatientApi patientApi;

    @Override
    public IPage<ManagementHistoryDetail> build(IPage<ManagementHistoryDetail> page) {
        if (page != null) {
            List<ManagementHistoryDetail> records = page.getRecords();
            if (!CollectionUtils.isEmpty(records)) {
                List<ManagementHistoryDetail> build = build(records);
                page.setRecords(build);
            }
        }
        return page;
    }

    private List<ManagementHistoryDetail> build(List<ManagementHistoryDetail> details) {
        List<Long> collect = details.stream().map(ManagementHistoryDetail::getPatientId).collect(Collectors.toList());
        List<ManagementHistoryDetail> list = new ArrayList<>(16);
        R<List<Patient>> result = patientApi.listByIds(collect);
        if (result.getIsSuccess().equals(Boolean.TRUE) && result.getData() != null) {
            List<Patient> info = result.getData();
            Map<Long, Patient> map = new HashMap<>(16);
            info.forEach(e -> map.put(e.getId(), e));
            if (!CollectionUtils.isEmpty(info)) {
                details.forEach(e -> {
                    if (e != null) {
                        if (map.containsKey(e.getPatientId())) {
                            Patient patient = map.get(e.getPatientId());
                            if (patient != null) {
                                e.setAvatar(patient.getAvatar());
                                e.setDoctorName(patient.getDoctorName());
                                e.setPatientName(patient.getName());
                            }
                            list.add(e);
                        }
                    }
                });
            }
        }
        return list;
    }
}
