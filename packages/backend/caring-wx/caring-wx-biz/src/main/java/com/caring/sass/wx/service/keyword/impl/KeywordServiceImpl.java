package com.caring.sass.wx.service.keyword.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.Wraps;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.wx.dao.keyword.KeywordMapper;
import com.caring.sass.wx.dto.config.UploadTemporaryMatrialForm;
import com.caring.sass.wx.dto.keyword.AutomaticReplyDto;
import com.caring.sass.wx.constant.WeiXinConstants;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import com.caring.sass.wx.entity.keyword.Keyword;
import com.caring.sass.wx.service.config.ConfigAdditionalService;
import com.caring.sass.wx.service.config.ConfigService;
import com.caring.sass.wx.service.keyword.KeywordService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 业务实现类
 * 微信服务号自动回复关键词
 * </p>
 *
 * @author leizhi
 * @date 2020-09-15
 */
@Slf4j
@Service

public class KeywordServiceImpl extends SuperServiceImpl<KeywordMapper, Keyword> implements KeywordService {


    @Autowired
    ConfigAdditionalService configAdditionalService;

    @Autowired
    ConfigService configService;

    @Override
    public Keyword matchKeyword(String receiveMsg) {
        ConfigAdditional additional = configAdditionalService.getOne(Wrappers.lambdaQuery());
        Integer keyWordStatus = additional.getKeyWordStatus();
        if (Objects.equals(keyWordStatus, 1) && !StringUtils.isEmpty(receiveMsg)) {
            // 使用 关键字匹配 全匹配
            LbqWrapper<Keyword> matchAllQ = Wraps.<Keyword>lbQ().eq(Keyword::getMatchType, Keyword.MATCH_TYPE_ALL)
                    .eq(Keyword::getKeyWord, receiveMsg);
            List<Keyword> keyword = baseMapper.selectList(matchAllQ);
            if (CollUtil.isNotEmpty(keyword)) {
                return keyword.get(0);
            }

            // 使用关键字 进行 半匹配
            LbqWrapper<Keyword> matchHalfQ = Wraps.<Keyword>lbQ().eq(Keyword::getMatchType, Keyword.MATCH_TYPE_HALF);
            List<Keyword> keywordList = baseMapper.selectList(matchHalfQ);
            for (Keyword word : keywordList) {
                String keyWord = word.getKeyWord();
                if (receiveMsg.contains(keyWord)) {
                    return word;
                }
            }
        }

        // 使用收到消息回复
        Integer automaticReply = additional.getAutomaticReply();
        if (automaticReply != null && automaticReply == 1) {
            return getAutomaticReply();
        }
        return null;
    }


    /**
     * @return com.caring.sass.wx.entity.keyword.Keyword
     * @Author yangShuai
     * @Description 自动回复
     * @Date 2020/9/17 14:12
     */
    @Override
    public Keyword getAutomaticReply() {
        LbqWrapper<Keyword> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(Keyword::getMatchType, Keyword.MATCH_TYPE_NO);
        return baseMapper.selectOne(queryWrapper);
    }


    @Override
    public boolean save(Keyword model) {
        dealWithMediaId(model, null);
        int insert = baseMapper.insert(model);
        return insert > 0;
    }

    @Override
    public boolean updateById(Keyword model) {
        Keyword keyword = baseMapper.selectById(model.getId());
        dealWithMediaId(model, keyword);
        int update = baseMapper.updateById(model);
        return update > 0;
    }

    /**
     * @return java.lang.String
     * @Author yangShuai
     * @Description 处理 当是 图片时 的素材
     * @Date 2020/9/17 15:33
     */
    private void dealWithMediaId(Keyword newKeyword, Keyword oldKeyWord) {
        if (oldKeyWord != null && Objects.equals(oldKeyWord.getReply(), newKeyword.getReply())) {
            if (!StringUtils.isEmpty(oldKeyWord.getMediaId())) {
                configService.deleteMaterial(oldKeyWord.getMediaId());
            }
        }
        if (newKeyword.getMsgType() != null && Objects.equals(1, newKeyword.getMsgType())) {
            // 上传 图片到 微信素材。永久保存
            UploadTemporaryMatrialForm form = new UploadTemporaryMatrialForm();
            form.setMediaType(WeiXinConstants.MediaType.image);
            form.setUrl(newKeyword.getReply());
            // TODO: 上传素材失败。会导致空指针异常。
            WxMpMaterialUploadResult permanentMatrial = configService.uploadPermanentMatrial(form);
            if (Objects.isNull(permanentMatrial)) {
                return;
            }
            newKeyword.setMediaId(permanentMatrial.getMediaId());
        }
    }

    /**
     * @return com.caring.sass.wx.entity.keyword.Keyword
     * @Author yangShuai
     * @Description 更新自动回复 设置
     * @Date 2020/9/17 16:36
     */
    @Override
    public Keyword updateAutomaticReply(AutomaticReplyDto automaticReply) {
        Keyword keyword = getAutomaticReply();
        Keyword oldKeyword = new Keyword();
        if (keyword == null) {
            oldKeyword = null;
            keyword = new Keyword();
            BeanUtils.copyProperties(automaticReply, keyword);
        } else {
            BeanUtils.copyProperties(keyword, oldKeyword);
            keyword.setMsgType(automaticReply.getMsgType());
            keyword.setReply(automaticReply.getReply());
        }
        keyword.setMatchType(Keyword.MATCH_TYPE_NO);
        dealWithMediaId(keyword, oldKeyword);
        if (keyword.getId() != null) {
            baseMapper.updateAllById(keyword);
        } else {
            baseMapper.insert(keyword);
        }
        return keyword;
    }
}
