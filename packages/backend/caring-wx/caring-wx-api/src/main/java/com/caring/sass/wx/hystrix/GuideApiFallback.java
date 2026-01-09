package com.caring.sass.wx.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.wx.GuideApi;
import com.caring.sass.wx.dto.guide.RegGuideSaveDTO;
import com.caring.sass.wx.entity.guide.RegGuide;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author leizhi
 */
@Component
public class GuideApiFallback implements GuideApi {


    @Override
    public R<RegGuide> getGuideByTenantCode(String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<RegGuide> getGuide() {
        return R.timeout();
    }

    @Override
    public R<RegGuide> save(RegGuideSaveDTO model) {
        return R.timeout();
    }

    @Override
    public R<RegGuide> upsertByCode(RegGuideSaveDTO model) {
        return R.timeout();
    }

    @Override
    public R<Boolean> copyRegGuide(String fromTenantCode, String toTenantCode) {
        return R.timeout();
    }

    @Override
    public R<List<RegGuide>> getOpenUnregisteredReminder() {
        return R.timeout();
    }

    @Override
    public R<Boolean> openFormHistoryRecord(String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<Integer> hasFillDrugs(String tenantCode) {
        return R.timeout();
    }

    @Override
    public R<String> initGuide(String tenantCode) {
        return R.timeout();
    }
}
