package com.caring.sass.cms.hystrix;

import com.caring.sass.cms.MassCallBackApi;
import com.caring.sass.cms.dto.MassCallBack;

/**
 * @ClassName MassCallBackApiFallback
 * @Description
 * @Author yangShuai
 * @Date 2021/11/24 11:29
 * @Version 1.0
 */
public class MassCallBackApiFallback implements MassCallBackApi {


    @Override
    public void massCallBack(MassCallBack massCallBack) {
        return;
    }

    @Override
    public void massMailing() {
        return;
    }
}
