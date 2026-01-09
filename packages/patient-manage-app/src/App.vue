
<template>
  <router-view v-slot="{ Component }">
    <keep-alive>
      <component
        :is="Component"
        :key="$route.name"
        v-if="$route.meta.keepAlive"
      />
    </keep-alive>
    <component
      :is="Component"
      :key="$route.name"
      v-if="!$route.meta.keepAlive"
    />
  </router-view>
</template>
<script lang="ts" setup>
  import { onMounted } from "vue";
  import { useRouter } from "vue-router";
  import {Toast} from "vant";
  const router = useRouter();
  onMounted(() => {
    window.addEventListener('message', function (e) {
      //Toast(e)
      console.log('patientManage message', e)
      if (e && e.data) {
        console.log('patientManage收到消息啦', JSON.stringify(e.data))
        let msg = {type: ''}
        try {
          msg = JSON.parse(e.data)
        } catch (e) {
          return
        }
        const msgType = msg.type
        const caringCurrentDevice = localStorage.getItem('caringCurrentDevice')
        // 当页面时在微信的H5时
        if (caringCurrentDevice && caringCurrentDevice === 'weixin') {
          const header = document.getElementById('patientManageHeader')
          if (header) {
            header.style.padding = '0px 13px 0 13px'
          }
        } else if (!localStorage.getItem('H5_environment')) {
          const header = document.getElementById('patientManageHeader')
          if (header) {
            const isIphone = Boolean(navigator.userAgent.match(/iphone|ipod|iOS/ig)); // 是否是苹果浏览器
            if (isIphone) {
              header.style.padding = '55px 13px 0 13px'
            } else {
              header.style.padding = '45px 13px 0 13px'
            }
          }
        }
        localStorage.setItem('H5_environment', 'uniApp')
        if (msgType === 'patientManageOpenHome') {
          router.push({
            path: '/index'
          })
          window.parent.postMessage({action: 'patientManageGoHomeSuccess'}, '*')
        } else if (msgType === 'uniCallPatientManageBackPage') {
          const currentUrl = localStorage.getItem('patient-manage-router-path')
          if (currentUrl && (currentUrl.startsWith('/index'))) {
            window.parent.postMessage({action: 'patientManageClose'}, '*')
          } else {
            // 患者管理平台 后退一个页面
            router.go(-1)
          }
        } else if (msgType === 'windowOrIosBackPatientManage' || msgType === 'patientManageBack') {
          let history = localStorage.getItem('patientManageChatPatientRouterHistory')
          console.log('patientManageBack', history)
          if (history) {
            let queryParams = JSON.parse(history)
            router.push(queryParams)
          }
          window.parent.postMessage({action: 'patientManagePrepareSuccess'}, '*')
        } else {
          window.parent.postMessage({action: 'patientManagePrepareSuccess'}, '*')
        }
      }
    })

    window.addEventListener('online', function (e) {
      localStorage.setItem('NET_WORK_STATUS', '4g')
    })
    window.addEventListener('offline', function (e) {
      localStorage.setItem('NET_WORK_STATUS', 'none')
    })
    const caringCurrentDevice = localStorage.getItem('caringCurrentDevice')
    if (caringCurrentDevice && caringCurrentDevice === 'weixin') {
      window.addEventListener('popstate', (event) => {
        const path = localStorage.getItem('patient-manage-router-path')
        const canClosePatientManage = localStorage.getItem('canClosePatientManage')
        if (path && (path.startsWith('/index'))) {
          if (canClosePatientManage) {
            window.parent.postMessage({action: 'patientManageClose'}, '*')
          }
        }
      })
    }
  });
</script>
<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 全局覆盖 */
.van-toast {
  background: rgba(0,0,0, 0.6) !important;
}

/* 局部覆盖，例如使用 :deep() */
:deep(.van-toast__text) {
  color: #000 !important; /* 或者你想要的任何颜色 */
}

</style>
