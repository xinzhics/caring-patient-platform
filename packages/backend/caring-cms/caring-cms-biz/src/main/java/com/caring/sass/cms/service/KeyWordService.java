package com.caring.sass.cms.service;

import com.caring.sass.base.service.SuperService;
import com.caring.sass.cms.entity.CmsKeyWord;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 关键词表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-14
 */
public interface KeyWordService extends SuperService<CmsKeyWord> {

    void getKeyWord();


    void filterKeyWord();


    boolean checkDoctorHasKeyWord(Long doctorId);


    List<CmsKeyWord> queryKeyWord(Long doctorId);

    void subscribeKeyword(Long doctorId, List<Long> keyWordIds);


    boolean cancelSubscribeKeyWord(Long doctorId, String keyWord);

    void sendKeyWordCmsToDoctor(Long cmsId, List<Long> doctorIds);


    void sendKeyWordToDoctor(String tenant, Long doctorId, List<Long> keyWordIds);

}
