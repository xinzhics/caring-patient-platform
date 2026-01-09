package com.caring.sass.nursing.service.information.impl;


import com.caring.sass.base.R;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.QueryWrap;
import com.caring.sass.nursing.dao.information.ManagementHistoryMapper;
import com.caring.sass.nursing.entity.information.ManagementHistory;
import com.caring.sass.nursing.entity.information.ManagementHistoryDetail;
import com.caring.sass.nursing.service.information.ManagementHistoryDetailService;
import com.caring.sass.nursing.service.information.ManagementHistoryService;
import com.caring.sass.oauth.api.PatientApi;
import com.caring.sass.user.entity.Patient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 管理历史
 * </p>
 *
 * @author yangshuai
 * @date 2022-05-24
 */
@Slf4j
@Service
@AllArgsConstructor
public class ManagementHistoryServiceImpl
        extends SuperServiceImpl<ManagementHistoryMapper, ManagementHistory> implements ManagementHistoryService {

    private final ManagementHistoryDetailService detailService;
    private final PatientApi patientApi;

    @Override
    public ManagementHistory findAll(String type) {
        QueryWrap<ManagementHistory> wrap = Wraps.q(new ManagementHistory().setHistoryType(type));
        ManagementHistory managementHistory = this.baseMapper.selectOne(wrap);
        if (managementHistory != null) {
            QueryWrap<ManagementHistoryDetail> queryWrap = Wraps.q(new ManagementHistoryDetail()
                    .setHistoryId(managementHistory.getId())).orderByDesc("create_time");
            List<ManagementHistoryDetail> details = detailService.list(queryWrap);
            if (!CollectionUtils.isEmpty(details)) {
                List<ManagementHistoryDetail> build = build(details);
                managementHistory.setHistoryDetails(build);
            }
        }
        return managementHistory;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ManagementHistory history) {
        this.save(history);
        List<ManagementHistoryDetail> historyDetails = history.getHistoryDetails();
        if (!CollectionUtils.isEmpty(historyDetails)) {
            for (ManagementHistoryDetail detail : historyDetails) {
                detail.setHistoryId(history.getId());
            }
            detailService.saveBatch(historyDetails);
        }
    }

    @Override
    public void deleteById(Long id) {
        this.removeById(id);
    }

    private List<ManagementHistoryDetail> build(List<ManagementHistoryDetail> details) {
        List<Long> collect = details.stream().map(ManagementHistoryDetail::getPatientId).collect(Collectors.toList());
        List<ManagementHistoryDetail> list = new ArrayList<>(16);
        R<List<Patient>> result = patientApi.findByIds(collect);
        if (result.getIsSuccess().equals(Boolean.TRUE) && result.getData() != null) {
            List<Patient> info = result.getData();
            Map<Long, Patient> map = new HashMap<>(16);
            info.forEach(e -> map.put(e.getId(), e));
            if (!CollectionUtils.isEmpty(info)) {
                details.forEach(e -> {
                    if (e != null) {
                        if (map.containsKey(e.getPatientId())) {
                            Patient patient = map.get(e.getPatientId());
                            e.setAvatar(patient.getAvatar());
                            e.setDoctorName(patient.getDoctorName());
                            e.setPatientName(patient.getName());
                            list.add(e);
                        }
                    }
                });
            }
        }
        return list;
    }
}
