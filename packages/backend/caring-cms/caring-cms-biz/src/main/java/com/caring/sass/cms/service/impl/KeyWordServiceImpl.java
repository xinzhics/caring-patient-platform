package com.caring.sass.cms.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.caring.sass.base.R;
import com.caring.sass.base.entity.Entity;
import com.caring.sass.base.entity.SuperEntity;
import com.caring.sass.cms.dao.ChannelContentMapper;
import com.caring.sass.cms.dao.DoctorSubscribeKeyWordMapper;
import com.caring.sass.cms.dao.KeyWordMapper;
import com.caring.sass.cms.entity.ChannelContent;
import com.caring.sass.cms.entity.CmsKeyWord;
import com.caring.sass.cms.entity.DoctorSubscribeKeyWord;
import com.caring.sass.cms.service.KeyWordService;
import com.caring.sass.base.service.SuperServiceImpl;

import com.caring.sass.common.constant.BizConstant;
import com.caring.sass.common.constant.CommonStatus;
import com.caring.sass.context.BaseContextHandler;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.msgs.api.GptDoctorChatApi;
import com.caring.sass.msgs.dto.GptDoctorChatSaveDTO;
import com.caring.sass.msgs.entity.GptDoctorChat;
import com.caring.sass.oauth.api.DoctorApi;
import com.caring.sass.user.entity.Doctor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 关键词表
 * </p>
 *
 * @author 杨帅
 * @date 2023-09-14
 */
@Slf4j
@Service

public class KeyWordServiceImpl extends SuperServiceImpl<KeyWordMapper, CmsKeyWord> implements KeyWordService {


    @Autowired
    ChannelContentMapper channelContentMapper;


    @Override
    public void getKeyWord() {

        List<ChannelContent> contents = channelContentMapper.selectList(Wraps.<ChannelContent>lbQ()
                .select(ChannelContent::getKeyWord, SuperEntity::getId)
                .eq(ChannelContent::getChannelId, 1701850991646736384L));

        for (ChannelContent content : contents) {
            List<CmsKeyWord> words = new ArrayList<>();
            String keyWord = content.getKeyWord();
            if (StrUtil.isEmpty(keyWord)) {
                continue;
            }
            String[] split = keyWord.split(";");
            for (String s : split) {
                String trim = s.trim();
                if (StrUtil.isEmpty(trim)) {
                    continue;
                }
                String[] strings = trim.split(",");
                for (String string : strings) {
                    trim = string.trim();
                    if (StrUtil.isEmpty(trim)) {
                        continue;
                    }
                    CmsKeyWord word = new CmsKeyWord().setKeyWord(trim).setCmsId(content.getId());
                    words.add(word);
                }
            }
            if (CollUtil.isNotEmpty(words)) {
                baseMapper.insertBatchSomeColumn(words);
            }
        }

    }


    @Override
    public void filterKeyWord() {

        String []string = {"哮喘","鼻炎","食物过敏","皮肤点刺试验","变态反应","过敏原", "花粉", "免疫治疗", "糖皮质激素", "皮内试验"};

        int a = string.length;
        for (String s : string) {
            baseMapper.delete(Wraps.<CmsKeyWord>lbQ().eq(CmsKeyWord::getKeyWord, s));
            CmsKeyWord cmsKeyWord = new CmsKeyWord().setKeyWord(s).setShowStatus(CommonStatus.YES).setOrder(a);
            baseMapper.insert(cmsKeyWord);
            a--;
        }
    }

    @Autowired
    DoctorSubscribeKeyWordMapper doctorSubscribeKeyWordMapper;

    @Override
    public boolean checkDoctorHasKeyWord(Long doctorId) {
        LbqWrapper<DoctorSubscribeKeyWord> wrapper = Wraps.<DoctorSubscribeKeyWord>lbQ().eq(DoctorSubscribeKeyWord::getDoctorId, doctorId);
        Integer integer = doctorSubscribeKeyWordMapper.selectCount(wrapper);
        if (integer == null || integer.equals(0)) {
            return false;
        }
        return true;

    }



    @Override
    public List<CmsKeyWord>  queryKeyWord(Long doctorId) {

        LbqWrapper<DoctorSubscribeKeyWord> wrapper = Wraps.<DoctorSubscribeKeyWord>lbQ().eq(DoctorSubscribeKeyWord::getDoctorId, doctorId);
        List<DoctorSubscribeKeyWord> keyWordList = doctorSubscribeKeyWordMapper.selectList(wrapper);
        Set<Long> keyWordMap = new HashSet<>();
        if (CollUtil.isNotEmpty(keyWordList)) {
            keyWordMap = keyWordList.stream().map(DoctorSubscribeKeyWord::getKeyWordId).collect(Collectors.toSet());
        }
        List<CmsKeyWord> keyWords = baseMapper.selectList(Wraps.<CmsKeyWord>lbQ().eq(CmsKeyWord::getShowStatus, CommonStatus.YES).orderByDesc(CmsKeyWord::getOrder));
        if (CollUtil.isNotEmpty(keyWords)) {
            for (CmsKeyWord keyWord : keyWords) {
                if (keyWordMap.contains(keyWord.getId())) {
                    keyWord.setSubscribe(true);
                } else {
                    keyWord.setSubscribe(false);
                }
            }
        }
        return keyWords;
    }


    @Override
    public void subscribeKeyword(Long doctorId, List<Long> keyWordIds) {
        LbqWrapper<DoctorSubscribeKeyWord> wrapper = Wraps.<DoctorSubscribeKeyWord>lbQ().eq(DoctorSubscribeKeyWord::getDoctorId, doctorId);
        doctorSubscribeKeyWordMapper.delete(wrapper);
        if (CollUtil.isNotEmpty(keyWordIds)) {
            List<DoctorSubscribeKeyWord> doctorSubscribeKeyWords = new ArrayList<>();
            for (Long keyWordId : keyWordIds) {
                DoctorSubscribeKeyWord keyWord = DoctorSubscribeKeyWord.builder().doctorId(doctorId).keyWordId(keyWordId).build();
                doctorSubscribeKeyWords.add(keyWord);
            }
            doctorSubscribeKeyWordMapper.insertBatchSomeColumn(doctorSubscribeKeyWords);
        }
    }

    /**
     * 通过关键词名称 取消医生订阅的关键词
     * @param doctorId
     * @param keyWord
     */
    @Override
    public boolean cancelSubscribeKeyWord(Long doctorId, String keyWord) {
        // 查询医生所有的关键字
        List<DoctorSubscribeKeyWord> doctorSubscribeKeyWords = doctorSubscribeKeyWordMapper.selectList(Wraps.<DoctorSubscribeKeyWord>lbQ()
                .eq(DoctorSubscribeKeyWord::getDoctorId, doctorId));
        if (CollUtil.isEmpty(doctorSubscribeKeyWords)) {
            return false;
        }
        Set<Long> keywordIds = doctorSubscribeKeyWords.stream().map(DoctorSubscribeKeyWord::getKeyWordId).collect(Collectors.toSet());

        // 查询关键词的名称
        List<CmsKeyWord> cmsKeyWords = baseMapper.selectList(Wraps.<CmsKeyWord>lbQ().in(SuperEntity::getId, keywordIds)
                .eq(CmsKeyWord::getShowStatus, CommonStatus.YES));
        if (CollUtil.isEmpty(cmsKeyWords)) {
            return false;
        }

        // 确定要清理掉的关键词ID
        List<Long> deleteIds = new ArrayList<>();
        for (CmsKeyWord cmsKeyWord : cmsKeyWords) {
            if (keyWord.contains(cmsKeyWord.getKeyWord())) {
                deleteIds.add(cmsKeyWord.getId());
            }
        }

        // 清理掉关键词
        if (CollUtil.isNotEmpty(deleteIds)) {
            LbqWrapper<DoctorSubscribeKeyWord> wrapper = Wraps.<DoctorSubscribeKeyWord>lbQ()
                    .eq(DoctorSubscribeKeyWord::getDoctorId, doctorId)
                    .in(DoctorSubscribeKeyWord::getKeyWordId, deleteIds);
            doctorSubscribeKeyWordMapper.delete(wrapper);
            return true;
        }
        return false;
    }

    @Autowired
    DoctorApi doctorApi;

    @Autowired
    GptDoctorChatApi gptDoctorChatApi;

    /**
     * 获取医生在这个文章中的订阅的关键词名称
     * @param cmsKeyWord
     * @param keyWordMaps
     * @param subscribeKeyWords
     * @return
     */
    public List<String> getDoctorKeyWordName(String cmsKeyWord, Map<Long, String> keyWordMaps, List<DoctorSubscribeKeyWord> subscribeKeyWords) {

        if (CollUtil.isEmpty(keyWordMaps) || CollUtil.isEmpty(subscribeKeyWords)) {
            return new ArrayList<>();
        }
        if (StrUtil.isEmpty(cmsKeyWord)) {
            return new ArrayList<>();
        }
        List<String> strings = new ArrayList<>();
        for (DoctorSubscribeKeyWord keyWord : subscribeKeyWords) {
            String keyName = keyWordMaps.get(keyWord.getKeyWordId());
            if (StrUtil.isEmpty(keyName)) {
                continue;
            }
            if (cmsKeyWord.contains(keyName)) {
                strings.add(keyName);
            }
        }
        return strings;

    }

    @Override
    public void sendKeyWordToDoctor(String tenant, Long doctorId, List<Long> keyWordIds) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<CmsKeyWord> cmsKeyWords = baseMapper.selectList(Wraps.<CmsKeyWord>lbQ().in(SuperEntity::getId, keyWordIds));
        ArrayList<Long> objects = new ArrayList<>();
        objects.add(doctorId);
        R<List<Doctor>> doctors = doctorApi.findByIdsNoTenant(objects);
        if (doctors.getIsSuccess()) {
            List<Doctor> doctorList = doctors.getData();
            if (CollUtil.isEmpty(doctorList)) {
                return;
            }
            Doctor doctor = doctorList.get(0);
            for (CmsKeyWord keyWord : cmsKeyWords) {
                BaseContextHandler.setTenant(BizConstant.SUPER_TENANT);
                List<ChannelContent> channelContents = channelContentMapper.selectList(Wraps.<ChannelContent>lbQ().select()
                        .eq(ChannelContent::getChannelId, 1701850991646736384L)
                        .like(ChannelContent::getKeyWord, keyWord.getKeyWord())
                        .orderByDesc(ChannelContent::getCmsPeriod).orderByDesc(Entity::getUpdateTime).last(" limit 0,2 "));

                if (CollUtil.isNotEmpty(channelContents)) {
                    BaseContextHandler.setTenant(tenant);
                    for (ChannelContent cmsContent : channelContents) {

                        List<String> keyWordName = new ArrayList<>();
                        keyWordName.add(keyWord.getKeyWord());
                        JSONObject content = new JSONObject();

                        content.set("cmsTitle", cmsContent.getTitle());
                        content.set("cmsEnglishTitle", cmsContent.getEnglishTitle());
                        content.set("cmsSummary", cmsContent.getSummary());
                        content.set("cmsLink", cmsContent.getLink());
                        content.set("keyWord", keyWordName);

                        GptDoctorChatSaveDTO chatSaveDTO = new GptDoctorChatSaveDTO();
                        chatSaveDTO.setImGroupId(doctor.getId());
                        chatSaveDTO.setSenderId(doctor.getId());
                        chatSaveDTO.setSenderImAccount(doctor.getImAccount());
                        chatSaveDTO.setSenderRoleType(GptDoctorChat.AiRole);
                        chatSaveDTO.setType("KEY_WORD_CMS");
                        chatSaveDTO.setContent(content.toString());
                        BaseContextHandler.setTenant(doctor.getTenantCode());
                        gptDoctorChatApi.sendCms(chatSaveDTO);

                    }
                }
            }
        }
    }

    /**
     * 推送一个文章到医生
     * @param cmsId
     * @param doctorIds
     */
    @Override
    public void sendKeyWordCmsToDoctor(Long cmsId, List<Long> doctorIds) {

        ChannelContent channelContent = channelContentMapper.selectTitleByIdWithoutTenant(cmsId);
        if (Objects.isNull(channelContent)) {
            return;
        }
        List<DoctorSubscribeKeyWord> doctorSubscribeKeyWordList = doctorSubscribeKeyWordMapper.selectList(Wraps.<DoctorSubscribeKeyWord>lbQ()
                .in(DoctorSubscribeKeyWord::getDoctorId, doctorIds)
                .select(SuperEntity::getId, DoctorSubscribeKeyWord::getKeyWordId, DoctorSubscribeKeyWord::getDoctorId));
        List<Long> keywordIds = doctorSubscribeKeyWordList.stream().map(DoctorSubscribeKeyWord::getKeyWordId).collect(Collectors.toList());
        Map<Long, String> keyWordMaps = new HashMap<>();
        if (CollUtil.isNotEmpty(keywordIds)) {
            List<CmsKeyWord> cmsKeyWords = baseMapper.selectList(Wraps.<CmsKeyWord>lbQ().in(SuperEntity::getId, keywordIds));
            keyWordMaps = cmsKeyWords.stream().collect(Collectors.toMap(SuperEntity::getId, CmsKeyWord::getKeyWord));
        }

        Map<Long, List<DoctorSubscribeKeyWord>> doctorKeywordGroup = doctorSubscribeKeyWordList.stream().collect(Collectors.groupingBy(DoctorSubscribeKeyWord::getDoctorId));
        R<List<Doctor>> byIdsNoTenant = doctorApi.findByIdsNoTenant(doctorIds);
        if (byIdsNoTenant.getIsSuccess()) {
            List<Doctor> doctors = byIdsNoTenant.getData();
            for (Doctor doctor : doctors) {
                Long doctorId = doctor.getId();
                List<DoctorSubscribeKeyWord> subscribeKeyWords = doctorKeywordGroup.get(doctorId);
                String keyWord = channelContent.getKeyWord();
                List<String> keyWordName = getDoctorKeyWordName(keyWord, keyWordMaps, subscribeKeyWords);
                JSONObject content = new JSONObject();

                content.set("cmsTitle", channelContent.getTitle());
                content.set("cmsEnglishTitle", channelContent.getEnglishTitle());
                content.set("cmsSummary", channelContent.getSummary());
                content.set("cmsLink", channelContent.getLink());
                content.set("keyWord", keyWordName);

                GptDoctorChatSaveDTO chatSaveDTO = new GptDoctorChatSaveDTO();
                chatSaveDTO.setImGroupId(doctor.getId());
                chatSaveDTO.setSenderId(doctorId);
                chatSaveDTO.setSenderImAccount(doctor.getImAccount());
                chatSaveDTO.setSenderRoleType(GptDoctorChat.AiRole);
                chatSaveDTO.setType("KEY_WORD_CMS");
                chatSaveDTO.setContent(content.toString());
                BaseContextHandler.setTenant(doctor.getTenantCode());
                gptDoctorChatApi.sendCms(chatSaveDTO);

            }
        }
    }
}
