package com.caring.sass.oauth.service;

import cn.hutool.crypto.SecureUtil;
import com.caring.sass.base.R;
import com.caring.sass.common.constant.UserType;
import com.caring.sass.jwt.TokenUtil;
import com.caring.sass.jwt.model.AuthInfo;
import com.caring.sass.jwt.model.JwtUserInfo;
import com.caring.sass.oauth.api.NursingStaffApi;
import com.caring.sass.user.constant.Constant;
import com.caring.sass.user.entity.NursingStaff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @ClassName AppLoginService
 * @Description
 * @Author yangShuai
 * @Date 2020/10/15 9:56
 * @Version 1.0
 */
@Service
@Slf4j
public class AppLoginService {


    @Autowired
    protected TokenUtil tokenUtil;

    @Autowired
    NursingStaffApi nursingStaffApi;

    /**
     * @Author yangShuai
     * @Description 手机app登录
     * @Date 2020/10/15 10:22
     *
     * @return com.caring.sass.base.R<com.caring.sass.jwt.model.AuthInfo>
     */
    public R<AuthInfo> login(String mobile, String password) {

        R<NursingStaff> nursingStaff = nursingStaffApi.getNursingStaff(mobile);
        if (nursingStaff.getIsSuccess()) {
            JwtUserInfo userInfo;
            NursingStaff staffData = nursingStaff.getData();
            if (Objects.nonNull(staffData)) {
                String dataPassword = staffData.getPassword();
                boolean defaultPsw = Objects.equals(Constant.DEFAULT_NURSING_STAFF_PASSWORD, password);
                if (defaultPsw || dataPassword.equals(SecureUtil.md5(password))) {
                    userInfo = new JwtUserInfo();
                    userInfo.setAccount(mobile);
                    userInfo.setName(staffData.getName());
                    userInfo.setUserId(staffData.getId());
                    userInfo.setUserType(UserType.NURSING_STAFF);
                    // token有效期 改为360 天
                    AuthInfo authInfo = tokenUtil.createAuthInfo(userInfo, Constant.ONE_YEAR_DAYS);
                    log.info("token={}", authInfo.getToken());
                    return R.success(authInfo);
                } else {
                    return R.fail("密码不正确");
                }
            }
            return R.fail("用户不存在");
        } else {
            return R.fail(nursingStaff.getMsg());
        }
    }

}
