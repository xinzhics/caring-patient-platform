<template>
  <div id="app">
    <keep-alive v-if="$route.meta.keepAlive">
      <router-view></router-view>
    </keep-alive>
    <router-view v-if="!$route.meta.keepAlive"></router-view>
  </div>
</template>

<script>
import headNavigation from '@/components/headNavigation/headNavigation'
import {Toast} from 'vant'
import Vue from 'vue'

Vue.use(Toast)
export default {
  components: {
    headNavigation
  },
  name: 'App',
  created () {
    this.$initDict()
    this.$showHist()
  },
  data () {
    return {
      currentPath: '',
      formUrlQuery: '',
      formUrlPath: '',
      h5ToPath: '',
      h5ToQuery: ''
    }
  },
  methods: {
    /**
     * 切换router
     * @param path
     * @param query
     */
    routerChange (path, query) {
      const caringCurrentDevice = localStorage.getItem('caringCurrentDevice')
      if (caringCurrentDevice && caringCurrentDevice === 'weixin') {
        this.$router.push({
          path: path,
          query: query
        })
        window.parent.postMessage({action: 'goSuccess'}, '*')
      } else {
        this.$router.replace({
          path: path,
          query: query
        }, () => {
          window.parent.postMessage({action: 'goSuccess'}, '*')
        })
      }
    }
  },
  mounted () {
    let that = this
    window.addEventListener('message', function (e) {
      if (e && e.data) {
        console.log('message', e.data)
        let msg = {}
        try {
          msg = JSON.parse(e.data)
        } catch (e) {
          return
        }
        const msgType = msg.type
        if (msgType === 'go') {
          const query = msg.params
          const networkStatus = msg.networkStatus
          const h5Back = msg.h5Back // uniApp 指定本次go的页面。返回时要返回的地方
          if (h5Back) {
            localStorage.setItem('CARING_H5_BACK', h5Back)
          }
          const doctorYinDao = msg.doctorYinDao
          if (doctorYinDao) {
            localStorage.setItem('doc_yindao', true)
          }
          if (networkStatus) {
            localStorage.setItem('NET_WORK_STATUS', networkStatus)
          }
          const h5ToQuery = JSON.stringify(query)
          const path = msg.path.replace('assistant', '')
          // 如果app没有更换打开 h5的路径和参数。则不需要处理。
          if (that.h5ToPath === that.currentPath && that.h5ToQuery === h5ToQuery) {
            window.parent.postMessage({action: 'goSuccess'}, '*')
            return
          }
          // 如果uni是去患者中心的。则吧缓存中的后退路径清除
          if (path === '/patient/center' || path === '/patient/form/editor') {
            localStorage.removeItem('backUrl')
          }
          that.h5ToQuery = h5ToQuery
          that.h5ToPath = path
          that.routerChange(path, query)
        } else if (msgType === 'againShowH5Iframe') {
          // 兼容H5浏览器的处理。
          const againShowH5IframePage = localStorage.getItem('againShowH5IframePage')
          if (againShowH5IframePage) {
            const page = JSON.parse(againShowH5IframePage)
            console.log('againShowH5Iframe', page)
            that.$router.push({
              path: page.path,
              query: page.query
            })
          }
          window.parent.postMessage({action: 'goSuccess'}, '*')
        } else if (msgType === 'uniCallH5BackPage') {
          console.log('uniCallH5BackPage')
          // 在WEIXIN H5中，此代码不会执行。因为物理返回键事件被上面的popstate事件拦截
          if (that.h5ToPath === that.currentPath) {
            that.$h5Close()
          } else {
            // 物理返回键事件。 如果当前是在无网络首页。则
            if (that.currentPath === '/index') {
              if (that.formUrlPath && that.formUrlPath !== '/index') {
                that.$router.replace({
                  path: that.formUrlPath,
                  query: that.formUrlQuery
                })
              } else {
                that.$h5Close()
              }
            } else {
              that.$router.go(-1)
            }
          }
        } else if (msgType === 'reSetRouter') {
          that.$router.push({
            path: '/index'
          })
        } else if (msgType === 'networkStatus') {
          if (msg.data === 'none') {
            localStorage.setItem('NET_WORK_STATUS', 'none')
          } else {
            localStorage.setItem('NET_WORK_STATUS', '4g')
          }
        }
      }
    })

    window.addEventListener('online', function (e) {
      localStorage.setItem('NET_WORK_STATUS', '4g')
    })
    window.addEventListener('offline', function (e) {
      localStorage.setItem('NET_WORK_STATUS', 'none')
    })
  },
  watch: {
    // 使用watch 监听$router的变化
    $route (to, form) {
      // 如果to索引大于from索引,判断为前进状态,反之则为后退状态
      // 从路由中。找到当前最新的标题
      console.log(to.path, form.path, 'this.h5ToPath', this.h5ToPath)
      if (to.path === '/index' && form.path !== '/') {
        console.log('to.query.reSetRouter', to.query.reSetRouter)
        this.$h5Close()
      }
      if (!this.h5ToPath) {
        this.h5ToPath = to.path
      }
      this.currentPath = to.path
      this.formUrlPath = form.path
      this.formUrlQuery = form.query
    }
  }
}
</script>

<style lang="less">
  #app {
    font-family: Source Han Sans CN;
  }
  :root{
    // 往下补充
    --caring-tab-action-color: #3BD26A;      //vant tab 组件的标签选中的 文字颜色和 文字下标签的颜色
    --caring-common-type-select-color: #3BD26A;      // 常用语分类选中时颜色
    --caring-common-title-icon-bg: #67E0A7;  // 常用语标题前面icon的背景色
    --caring-common-open-content-a-text: #3BD26A; // 常用语内容下打开全部的文字的颜色
    --caring-common-button-plain-text: #66E0A7;   // 常用语自定义添加按钮的文字颜色
    --caring-common-button-plain-border: #66E0A8; // 常用语自定义添加按钮边框颜色
    --caring-common-button-default-bg: #66E0A7;   // 常用语按钮默认背景色
    --caring-common-button-disable-bg: #999999;   // 常用语按钮默认背景色
    --caring-search-text-highlight: #2596E8;   // 常用语按钮默认背景色
    --caring-common-disable-select: #C3C2C2;  // 常用语模版。已经被用户选择导致的禁用状态
    --caring-common-no-select: #999999;  // 常用语模版。没有被用户选择时的状态
    --caring-common-no-select-text: #666666;  // 常用语模版。没有被用户选择时的状态
    --caring-common-search-result-bg: #F7F7F7; // 常用语搜索结果页面的背景色
    --caring-common-type-no-select-bg: #EFEFF2; //添加常用语分类未选择时背景色
    --caring-no-data-text: #666666; // 无数据时提示文字的颜色
    --caring-common-bg-color: #FFFFFF;    // 常用语卡片背景色
    --caring-first-title: #333;   // 一级主标题颜色
    --caring-first-title-bg: #3BD26A; // 一级主题文字背景填充色
    --caring-first-a-text: #3BD26A; // 一级主题文字背景填充色
    --caring-line: #DEDEDE; // 边框线条的颜色
    --caring-first-title-white: #FFFFFF; // 深色背景下的文字的颜色
    --caring-body-bg: #F5F5F5; // 页面内容背景底色
    --caring-search-action: #1D97FF; // 搜索按钮的蓝色
  }
body{
  min-height: 100vh;
  background: #F5F5F5;
  -webkit-touch-callout: none !important;
  -webkit-user-select: none;
}

.top-box {
  display: flex;
  justify-content: space-between;
  padding: 48px 13px 13px 13px;
  background: #ffffff;
}

.disable{
  background: #989898 !important;
  border-color: #989898 !important;
}
.van-field__word-num{
  color: #3F86FF;
}
.doctor-mobile-toast{
  border-radius: 22px !important;
  background-color: rgba(0,0,0,.5) !important;
  text-align: left;
}
.text-ellipsis{
  width: 65vw;
  overflow:hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  -o-text-overflow:ellipsis;
}

/* 设置滚动条宽度 */
::-webkit-scrollbar {
  width: 1px;
}
/* 设置滚动条轨道样式 */
::-webkit-scrollbar-track {
  background: #f1f1f1;
}
/* 设置滚动滑块样式 */
::-webkit-scrollbar-thumb {
  background: #888;
}
/* 添加滚动滑块hover效果 */
::-webkit-scrollbar-thumb:hover {
  background: #555;
}
.caring-form-div-icon{
  width: 3px;
  height: 21px;
  background: #66E0A7;
  border-radius: 0;
  opacity: 1;
}
.caring-form-result-status{
  width: 61px;
  height: 21px;
  color: white;
  background: rgb(255, 90, 90);
  line-height: 21px;
  font-size: 12px;
  font-weight: 500;
  text-align: center;
  border-radius: 16px;
}

.caring-form-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 34px;
  text-align: center;
  border-radius: 17px 17px 17px 17px;
  opacity: 1;
  border: 1px solid #66E0A8;
  font-weight: 500;
  color: #66E0A7;
  font-size: 14px;
}
</style>

<style>

  .studiocms-content {

  }

  .studiocms-content h1,
  .studiocms-content h2,
  .studiocms-content h3 {
    margin-top: 1.5em;
    margin-bottom: 0.8em;
    font-weight: 600;
  }
  .studiocms-content p {
    margin-bottom: 1em;
  }
  .studiocms-content a {
    color: #007bff;
    text-decoration: none;
  }
  .studiocms-content a:hover {
    text-decoration: underline;
  }
  .studiocms-content code {
    background-color: #f4f4f4;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: monospace;
  }
  .studiocms-content pre {
    background-color: #2d2d2d;
    color: #f8f8f2;
    padding: 16px;
    border-radius: 6px;
    overflow-x: auto;
  }
  .studiocms-content pre code {
    background: none;
    padding: 0;
  }
  .studiocms-content ul, ol {
    padding-left: 2em;
  }
  .studiocms-content img {
    max-width: 100%;
    height: auto;
    border-radius: 4px;
  }

  .studiocms-content blockquote {
    display: block;
    border-left: 8px solid #d0e5f2;
    padding: 5px 10px;
    margin: 10px 0;
    line-height: 1.4;
    font-size: 100%;
    background-color: #f1f1f1;
  }

</style>
