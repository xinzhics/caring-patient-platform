package com.caring.sass.tenant.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.caring.sass.base.service.SuperServiceImpl;
import com.caring.sass.database.mybatis.conditions.query.LbqWrapper;
import com.caring.sass.tenant.dao.AppVersionMapper;
import com.caring.sass.tenant.entity.AppVersion;
import com.caring.sass.tenant.service.AppVersionService;
import com.caring.sass.utils.BizAssert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 业务实现类
 * 版本表
 * </p>
 *
 * @author leizhi
 * @date 2020-09-22
 */
@Slf4j
@Service

public class AppVersionServiceImpl extends SuperServiceImpl<AppVersionMapper, AppVersion> implements AppVersionService {

    /**
     * @return boolean
     * @Author yangShuai
     * @Description 更新版本更新时，记录当前版本号。
     * @Date 2020/9/22 16:14
     */
    @Override
    public boolean save(AppVersion version) {
        checkParams(version.getPlatform());
        checkSaveVersionParams(version);
        Integer versionCodeCount = selectCountByVersionCode(version.getPlatform(), version.getVersionCode());
        if (versionCodeCount != null && versionCodeCount > 1) {
            return false;
        }

        AppVersion newsVersion = getNewestVersion(version.getPlatform());
        if (newsVersion != null && version.getVersionCode() <= newsVersion.getVersionCode()) {
            return false;
        }

/*
        if (StringUtils.isEmpty(version.getQrcodeImageUrl())) {
            String qrCodeImageUrl;
            try {
                qrCodeImageUrl = qrCodeUtils.createQrCode(version.getUrl(), true);
                version.setQrcodeImageUrl(qrCodeImageUrl);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
*/
        baseMapper.insert(version);
        return true;
    }

    public AppVersion getNewestVersion(Integer platform) {
        LbqWrapper<AppVersion> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(AppVersion::getPlatform, platform);
        queryWrapper.orderByDesc(AppVersion::getVersionCode);
        List<AppVersion> versions = baseMapper.selectList(queryWrapper);
        return CollUtil.isNotEmpty(versions) ? versions.get(0) : null;
    }


    public AppVersion getByVersionCode(Integer platform, Integer versionCode) {
        LbqWrapper<AppVersion> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(AppVersion::getPlatform, platform);
        queryWrapper.eq(AppVersion::getVersionCode, versionCode);
        return baseMapper.selectOne(queryWrapper);
    }

    public Integer selectCountByVersionCode(Integer platform, Integer versionCode) {
        LbqWrapper<AppVersion> queryWrapper = new LbqWrapper<>();
        queryWrapper.eq(AppVersion::getPlatform, platform);
        queryWrapper.eq(AppVersion::getVersionCode, versionCode);
        return baseMapper.selectCount(queryWrapper);
    }

    public void checkSaveVersionParams(AppVersion version) throws NullPointerException {
        BizAssert.notNull(version);
        String appName = version.getAppName();
        String versionName = version.getVersionName();
        Integer versionCode = version.getVersionCode();
        String boundleId = version.getBoundleId();
        String url = version.getUrl();
        Integer enable = version.getEnable();
        if (enable != null && enable != 0 && enable != 1) {
            throw new NullPointerException("参数enable值错误");
        }
        if (enable == null) {
            version.setEnable(1);
        }

        version.setEnable(1);
        BizAssert.notEmpty(appName, "参数appName不能为空");
        BizAssert.notEmpty(versionName, "参数versionName不能为空");
        BizAssert.notNull(versionCode, "参数versionCode不能为空");
        BizAssert.notEmpty(boundleId, "参数boundleId[应用标识]不能为空");
        BizAssert.notEmpty(url, "参数url不能为空");
    }

    public void checkParams(Integer platform) throws NullPointerException {
        if (platform == null) {
            throw new NullPointerException("参数platform不能为空");
        } else if (platform != 0 && platform != 1) {
            throw new NullPointerException("platform参数值错误");
        }
    }
}
