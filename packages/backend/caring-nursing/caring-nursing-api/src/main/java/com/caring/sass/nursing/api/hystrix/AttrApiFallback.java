package com.caring.sass.nursing.api.hystrix;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.nursing.api.AttrApi;
import com.caring.sass.nursing.dto.tag.AttrPageDTO;
import com.caring.sass.nursing.dto.tag.AttrSaveDTO;
import com.caring.sass.nursing.dto.tag.AttrUpdateDTO;
import com.caring.sass.nursing.entity.tag.Attr;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 标签管理服务降级
 *
 * @author leizhi
 */
@Component
public class AttrApiFallback implements AttrApi {
    @Override
    public R<Boolean> delete(List<Long> longs) {
        return R.timeout();
    }

    @Override
    public R<Attr> get(Long aLong) {
        return R.timeout();
    }

    @Override
    public R<IPage<Attr>> page(PageParams<AttrPageDTO> params) {
        return R.timeout();
    }

    @Override
    public R<List<Attr>> query(Attr data) {
        return R.timeout();
    }

    @Override
    public R<Attr> save(AttrSaveDTO attrSaveDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> saveBatch(List<AttrSaveDTO> attrSaveDTOS) {
        return R.timeout();
    }

    @Override
    public R<Attr> saveOrUpdate(Attr attr) {
        return R.timeout();
    }

    @Override
    public R<Attr> update(AttrUpdateDTO attrUpdateDTO) {
        return R.timeout();
    }

    @Override
    public R<List<Attr>> listByIds(List<Long> longs) {
        return R.timeout();
    }
}
