package com.caring.sass.nursing.api.hystrix;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caring.sass.base.R;
import com.caring.sass.base.request.PageParams;
import com.caring.sass.nursing.api.FormApi;
import com.caring.sass.nursing.dto.form.CopyFormDTO;
import com.caring.sass.nursing.dto.form.FormPageDTO;
import com.caring.sass.nursing.dto.form.FormSaveDTO;
import com.caring.sass.nursing.dto.form.FormUpdateDTO;
import com.caring.sass.nursing.entity.form.Form;
import com.caring.sass.nursing.enumeration.FormEnum;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName PlanApiFallback
 * @Description
 * @Author yangShuai
 * @Date 2020/9/28 10:28
 * @Version 1.0
 **/
@Component
public class FormApiFallback implements FormApi {
    @Override
    public R<Form> saveOrUpdate(FormSaveDTO model) {
        return R.timeout();
    }

    @Override
    public R<List<Form>> getFromByCategory(String tenantCode, String categorys) {
        return R.timeout();
    }

    @Override
    public R<Integer> getFormIntoTheGroup(String category, Long planId) {
        return R.timeout();
    }

    @Override
    public R<Boolean> delete(List<Long> longs) {
        return R.timeout();
    }

    @Override
    public R<Form> get(Long aLong) {
        return R.timeout();
    }

    @Override
    public R<IPage<Form>> page(PageParams<FormPageDTO> params) {
        return R.timeout();
    }

    @Override
    public R<List<Form>> query(Form data) {
        return R.timeout();
    }

    @Override
    public R<List<Form>> listByIds(List<Long> longs) {
        return R.timeout();
    }

    @Override
    public R<Form> save(FormSaveDTO formSaveDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> saveBatch(List<FormSaveDTO> formSaveDTOS) {
        return R.timeout();
    }

    @Override
    public R<Form> saveOrUpdate(Form form) {
        return R.timeout();
    }

    @Override
    public R<Form> update(FormUpdateDTO formUpdateDTO) {
        return R.timeout();
    }

    @Override
    public R<Boolean> copyForm(CopyFormDTO copyFormDTO) {
        return R.timeout();
    }

    @Override
    public R<JSONArray> getDiagnosis() {
        return R.timeout();
    }

    @Override
    public R<Form> getFormByCategoryAndBizId(FormEnum category, String bizId) {
        return R.timeout();
    }
}
