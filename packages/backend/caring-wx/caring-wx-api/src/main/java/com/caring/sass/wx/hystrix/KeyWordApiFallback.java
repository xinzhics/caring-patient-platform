package com.caring.sass.wx.hystrix;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.wx.KeyWordApi;
import com.caring.sass.wx.dto.keyword.*;
import com.caring.sass.wx.entity.keyword.Keyword;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeyWordApiFallback implements KeyWordApi {
    @Override
    public R<Void> copyKeyword(String oldProjectId, String newProjectId) {
        return R.timeout();
    }

    @Override
    public R<Keyword> matchKeyword(KeyWordDto keyWordDto) {
        return R.timeout();
    }

    @Override
    public R<Keyword> getAutomaticReply() {
        return R.timeout();
    }

    @Override
    public R<Keyword> updateAutomaticReply(AutomaticReplyDto automaticReply) {
        return R.timeout();
    }

    @Override
    public R<Boolean> delete(List<Long> longs) {
        return R.timeout();
    }

    @Override
    public R<Keyword> get(Long aLong) {
        return R.timeout();
    }

    @Override
    public R<IPage<Keyword>> page(PageParams<KeywordPageDTO> params) {
        return R.timeout();
    }

    @Override
    public R<List<Keyword>> query(Keyword data) {
        return R.timeout();
    }

    @Override
    public R<List<Keyword>> listByIds(List<Long> longs) {
        return R.timeout();
    }

    @Override
    public R<Keyword> save(KeywordSaveDTO keywordSaveDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> saveBatch(List<KeywordSaveDTO> keywordSaveDTOS) {
        return R.timeout();
    }

    @Override
    public R<Keyword> saveOrUpdate(Keyword keyword) {
        return R.timeout();
    }

    @Override
    public R<Keyword> update(KeywordUpdateDTO keywordUpdateDTO) {
        return R.timeout();
    }
}
