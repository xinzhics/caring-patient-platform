package com.caring.sass.ai.know.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.ai.dto.know.CreateMedicalRecordsModel;
import com.caring.sass.ai.entity.know.KnowledgeDailyCollection;
import com.caring.sass.base.service.SuperService;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 业务接口
 * 日常收集文本内容
 * </p>
 *
 * @author 杨帅
 * @date 2024-09-20
 */
public interface KnowledgeDailyCollectionService extends SuperService<KnowledgeDailyCollection> {

    boolean update(KnowledgeDailyCollection model);

    void delete(Long id);

    String getTitle(@NotNull String textContent);

    void page(IPage<KnowledgeDailyCollection> builtPage, String query, Long userId);

    void createMedicalRecords(CreateMedicalRecordsModel createMedicalRecordsModel);


    String getMedicalContent(String uid);
}
