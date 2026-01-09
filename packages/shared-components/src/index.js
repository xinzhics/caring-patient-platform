/**
 * 慢病管理平台 - 共享组件库入口文件
 */

// 组件导出
export { default as MobileHeader } from './components/mobile/MobileHeader'
export { default as DataTable } from './components/desktop/DataTable'
export { default as ChartContainer } from './components/charts/ChartContainer'

// 工具函数导出
export { default as request } from './utils/request'
export { formatDate, formatPhone } from './utils/format'
export { getToken, setToken } from './utils/auth'

// 混入导出
export { default as AuthMixin } from './mixins/AuthMixin'
export { default as TableMixin } from './mixins/TableMixin'

// 指令导出
export { default as Permission } from './directives/Permission'
export { default as Loading } from './directives/Loading'

// 过滤器导出
export { dateFilter, phoneFilter } from './filters'

// Vue插件安装方法
const components = [
  MobileHeader,
  DataTable,
  ChartContainer
]

const install = function(Vue, opts = {}) {
  // 注册组件
  components.forEach(component => {
    Vue.component(component.name, component)
  })

  // 注册指令
  Vue.directive('permission', Permission)
  Vue.directive('loading', Loading)

  // 注册过滤器
  Vue.filter('date', dateFilter)
  Vue.filter('phone', phoneFilter)

  // 添加全局方法
  Vue.prototype.$request = request
  Vue.prototype.$formatDate = formatDate
}

// 自动安装
if (typeof window !== 'undefined' && window.Vue) {
  install(window.Vue)
}

export default {
  version: '1.0.0',
  install,
  ...components
}