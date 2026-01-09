package com.caring.sass.tenant.api.hystrix;

import com.caring.sass.base.R;
import com.caring.sass.tenant.api.PatientManageMenuApi;
import com.caring.sass.tenant.entity.router.PatientManageMenu;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientManageMenuApiFallback implements PatientManageMenuApi {


    @Override
    public R<List<PatientManageMenu>> queryList(String code) {
        return R.timeout();
    }
}
