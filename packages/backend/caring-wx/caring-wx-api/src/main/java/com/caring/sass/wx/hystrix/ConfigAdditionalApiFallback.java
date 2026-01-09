package com.caring.sass.wx.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.wx.ConfigAdditionalApi;
import com.caring.sass.wx.dto.config.GeneralForm;
import com.caring.sass.wx.entity.config.ConfigAdditional;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class ConfigAdditionalApiFallback implements ConfigAdditionalApi {
    @Override
    public R<ConfigAdditional> getConfigAdditionalByWxAppId(GeneralForm paramGeneralForm) {
        return R.timeout();
    }

    @Override
    public R updateConfigAdditional(ConfigAdditional configAdditional) {
        return R.timeout();
    }

    @Override
    public R<Boolean> switchKeyword(Integer status) {
        return R.timeout();
    }

    @Override
    public R<Boolean> switchAutomaticReply(Integer status) {
        return R.timeout();
    }

    @Override
    public R<Integer> switchStatus(@NotNull(message = "开关类型不能为空") Integer type) {
        return R.timeout();
    }
}
