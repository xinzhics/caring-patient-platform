package com.caring.sass.oauth.service.impl;

import com.caring.sass.base.R;
import com.caring.sass.common.constant.CacheKey;
import com.caring.sass.exception.BizException;
import com.caring.sass.oauth.service.ValidateCodeService;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.caring.sass.exception.code.ExceptionCode.CAPTCHA_ERROR;

/**
 * 验证码服务
 *
 * @author caring
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {

    @Autowired
    private CacheChannel cache;

    @Override
    public void create(String key, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(key)) {
            throw BizException.validFail("验证码key不能为空");
        }
        setHeader(response, "arithmetic");

        Captcha captcha = createCaptcha("arithmetic");
        cache.set(CacheKey.CAPTCHA, key, StringUtils.lowerCase(captcha.text()));
        captcha.out(response.getOutputStream());
    }

    @Override
    public R<Boolean> check(String key, String value) {
        if (StringUtils.isBlank(value)) {
            return R.fail(CAPTCHA_ERROR.build("请输入验证码"));
        }
        CacheObject cacheObject = cache.get(CacheKey.CAPTCHA, key);
        if (cacheObject.getValue() == null) {
            return R.fail(CAPTCHA_ERROR.build("验证码已过期"));
        }
        if (!StringUtils.equalsIgnoreCase(value, String.valueOf(cacheObject.getValue()))) {
            return R.fail(CAPTCHA_ERROR.build("验证码不正确"));
        }
        cache.evict(CacheKey.CAPTCHA, key);
        return R.success(true);
    }

    private Captcha createCaptcha(String type) {
        Captcha captcha;
        if (StringUtils.equalsIgnoreCase(type, "gif")) {
            captcha = new GifCaptcha(115, 42, 4);
        } else if (StringUtils.equalsIgnoreCase(type, "png")) {
            captcha = new SpecCaptcha(115, 42, 4);
        } else if (StringUtils.equalsIgnoreCase(type, "chinese")) {
            captcha = new ChineseCaptcha(115, 42);
        } else  /*if (StringUtils.equalsIgnoreCase(type, "arithmetic")) */ {
            captcha = new ArithmeticCaptcha(115, 42);
        }
        captcha.setCharType(2);
        return captcha;
    }

    private void setHeader(HttpServletResponse response, String type) {
        if (StringUtils.equalsIgnoreCase(type, "gif")) {
            response.setContentType(MediaType.IMAGE_GIF_VALUE);
        } else {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
        }
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
    }
}
