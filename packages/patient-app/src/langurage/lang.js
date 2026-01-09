/**
 * 显示的语言转换
 * @param key 常量
 * @param info 常量的名称，便于理解常量的含义
 * @returns {*}
 */
function lang(key, info) {
  return this.$i18n.t(key)
}

//结尾处将该方法暴露出来供外部调用
export default {
  lang
}
