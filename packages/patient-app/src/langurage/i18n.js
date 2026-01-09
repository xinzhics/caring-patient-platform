// I18n
import VueI18n from 'vue-i18n'
import Vue from 'vue'
import { Locale } from 'vant';
// 引入英文语言包
import enUS from 'vant/es/locale/lang/en-US';
import zhCN from 'vant/es/locale/lang/zh-CN';

// 引入自己定义的 I18n 文件
import myI18nEn from './en.js'
import myI18nZh from './zh.js'
// 如果还有新的语言在下面继续添加

// 注册 vue-i18n
Vue.use(VueI18n)

Locale.use('zh-CN', zhCN);

const messages = {
  en: {
    ...myI18nEn
  },
  zh: {
    ...myI18nZh
  }
}

export function getLanguage() {
  const chooseLanguage = localStorage.getItem('language')
  if (chooseLanguage) return chooseLanguage

  // 如果没有选择语言
  const language = (navigator.language || navigator.browserLanguage).toLowerCase()
  const locales = Object.keys(messages)
  for (const locale of locales) {
    if (language.indexOf(locale) > -1) {
      if (locale === 'en') {
        Locale.use('en-US', enUS);
      } else {
        Locale.use('zh-CN', zhCN);
      }
      return locale
    }
  }
  Locale.use('en-US', enUS);
  return 'en' // 默认语言
}

const i18n = new VueI18n({
  locale: getLanguage(),
  messages
})

export default i18n
