package com.caring.sass.nursing.api.hystrix;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.nursing.api.TagApi;
import com.caring.sass.nursing.dto.tag.TagPageDTO;
import com.caring.sass.nursing.dto.tag.TagSaveDTO;
import com.caring.sass.nursing.dto.tag.TagUpdateDTO;
import com.caring.sass.nursing.dto.tag.TagUpsertDTO;
import com.caring.sass.nursing.entity.tag.Tag;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 标签管理服务降级
 *
 * @author leizhi
 */
@Component
public class TagApiFallback implements TagApi {

    @Override
    public R<List<Tag>> query(Tag data) {
        return R.timeout();
    }

    @Override
    public R<Boolean> delete(List<Long> ids) {
        return R.timeout();
    }

    @Override
    public R<Boolean> upsert(TagUpsertDTO tagUpsertDTO) {
        return R.timeout();
    }

    @Override
    public R<List<TagUpsertDTO>> listTagWithAttr() {
        return R.timeout();
    }

    @Override
    public R<Tag> get(Long aLong) {
        return R.timeout();
    }

    @Override
    public R<IPage<Tag>> page(PageParams<TagPageDTO> params) {
        return R.timeout();
    }

    @Override
    public R<List<Tag>> listByIds(List<Long> longs) {
        return R.timeout();
    }

    @Override
    public R<Tag> save(TagSaveDTO tagSaveDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> saveBatch(List<TagSaveDTO> tagSaveDTOS) {
        return R.timeout();
    }

    @Override
    public R<Tag> saveOrUpdate(Tag tag) {
        return R.timeout();
    }

    @Override
    public R<Tag> update(TagUpdateDTO tagUpdateDTO) {
        return R.timeout();
    }

    @Override
    public R<TagUpsertDTO> getTagWithAttr(Long id) {
        return R.timeout();
    }

    @Override
    public R<Boolean> copyTag(String fromTenantCode, String toTenantCode) {
        return R.timeout();
    }
}
