package com.caring.sass.user.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public enum I18nUtils {

    ;
    private static final MessageSource messageSource;

    static {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("i18n/ucenterlang");
        source.setDefaultEncoding("UTF-8");
        messageSource = source;
    }

    public static String getMessageByTenantDefault(String key, String defaultLanguage, Object... args) {
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        if (defaultLanguage != null) {
            if (defaultLanguage.equals("en")) {
                locale = Locale.ENGLISH;
            }
        }
        return getMessage(key, locale, args);
    }

    public static String getMessageByTenantDefault(String key, String defaultLanguage) {
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        if (defaultLanguage != null) {
            if (defaultLanguage.equals("en")) {
                locale = Locale.ENGLISH;
            }
        }
        return getMessage(key, locale);
    }

    public static String getMessage(String key, Locale locale) {
        if (locale == null) {
            locale = Locale.SIMPLIFIED_CHINESE;
        }
        return messageSource.getMessage(key, null, locale);
    }

    public static String getMessage(String key, Locale locale, Object... args) {
        if (locale == null) {
            locale = Locale.SIMPLIFIED_CHINESE;
        }
        return messageSource.getMessage(key, args, locale);
    }

    public static String getMessage(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, locale);
    }


    public static String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }
}