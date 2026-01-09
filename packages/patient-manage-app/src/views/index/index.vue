<template>
  <div class="index_box">
    <!-- app头部 -->
    <van-sticky>
      <appHeader
          :title="'工作台'"
          @handelPage="closeWindow"
          :leftCustomize="1"
          :toHistory="1"
      />
    </van-sticky>
    <div class="i_img_top">
      <img class="workbenchBg" src="../../assets/images/workbenchBg2.png"/>
      <p class="workbenchP">患者管理工作台</p>
    </div>
    <div class="cellbox" v-if="displaydata.length > 0">
      <van-cell-group inset v-for="item in displaydata" :key="item">
        <div class="div-item" @click="item.func">
          <van-cell is-link>
            <template #title>
              <div class="item-left">
                <img class="item_img" :src="item.icon"/>
                <span class="title" v-text="item.title"></span>
              </div>
            </template>
            <template #value>
              <van-badge
                  v-if="item.content > 0"
                  :content="item.content"
                  max="99"
                  position="bottom-left"
                  class="badge"
                  :style="
                  item.content > 99
                    ? 'width:40px'
                    : item.content > 10
                    ? 'width:28px'
                    : ''
                "
              />
            </template>
          </van-cell>
        </div>
      </van-cell-group>
    </div>

    <div v-else-if="loading" class="no_data">
      <van-empty description="空空如也~" :image-size="[160, 90]">
        <template v-slot:image>
          <img src="@/assets/images/abnormal_no_data.png" alt=""/>
        </template>
      </van-empty>
    </div>
  </div>
</template>
<script lang="ts" setup>

import {Empty, Toast} from "vant";
import patientmonitorApi from "@/api/patientmonitor";
import medicationApi from "@/api/medicationwarn";
import commApi from "@/api/commapi";
import configApi from "@/api/config";
import {onMounted} from "vue";
import {ref} from "vue";
import {useRouter} from "vue-router";
import appHeader from "@/components/header/index.vue";

const router = useRouter();
const loading = ref<boolean>(false);

const displaydata = ref<any>([]);
onMounted(() => {
  getList();
  window.addEventListener('message', function (e) {
    if (e && e.data) {
      console.log('patientManage 首页收到打开管理平台的啦， 赶紧刷新一下数据', JSON.stringify(e.data))
      let msg = {type: ''}
      try {
        msg = JSON.parse(e.data)
      } catch (e) {
        return
      }
      const msgType = msg.type
      if (msgType === 'patientManageOpenHome') {
        getList();
      }
    }
  })
});
const getJs = () => {
  Toast("患者管理平台js")
}

// 获取列表
const getList = () => {
  configApi.getList()
      .then(res => {
        displaydata.value = []
        res.data.forEach((item:any) => {
          if (item.showStatus === 1) {
            let url
            if (item.menuType === 'INFORMATION_INTEGRITY') {
              // 信息完整度
              url = () => toIntegrity("/information/integrity")
            } else if (item.menuType === "MONITOR_DATA") {
              // 监测数据
              url = () => toIntegrity("/monitordata/index")
            } else if (item.menuType === "MEDICATION_WARNING") {
              // 用药监测
              url = () => toIntegrity("/medication/index")
            } else if (item.menuType === "EXCEPTION_OPTION_TRACKING") {
              // 异常选项跟踪
              url = () => toIntegrity("/abnormal/index")
            } else if (item.menuType === "NOT_FINISHED_TRACKING") {
              // 未完成任务跟踪
              url = () => toIntegrity("/incomplete/index")
            }
            displaydata.value.push({
              id: item.id,
              icon: item.icon,
              title: item.name,
              content: 0,
              func: url,
              menuType: item.menuType,
            })
          }
        })
        loading.value = true
        getCountPatientManageNumber();
      })
}
// 异常选项跟踪
const getCountPatientManageNumber = () => {
  configApi.getCountPatientManageNumber()
    .then(res => {
      if (!res) {
        return
      }
      for (let i = 0; i < displaydata.value.length; i++) {
        if (displaydata.value[i].menuType === 'EXCEPTION_OPTION_TRACKING') {
          // 异常选项跟踪
          displaydata.value[i].content = res.data.EXCEPTION_OPTION_TRACKING
        }else if (displaydata.value[i].menuType === 'INFORMATION_INTEGRITY') {
          // 信息完整度
          displaydata.value[i].content = res.data.INFORMATION_INTEGRITY
        } else if (displaydata.value[i].menuType === 'MONITOR_DATA') {
          // 监测数据
          if (res.data) {
            displaydata.value[i].content = res.data.MONITOR_DATA
          }
        } else if (displaydata.value[i].menuType === 'MEDICATION_WARNING') {
          // 用药监测
          displaydata.value[i].content = res.data.MEDICATION_WARNING
        } else if (displaydata.value[i].menuType === 'NOT_FINISHED_TRACKING') {
          // 未完成任务追踪
          displaydata.value[i].content = res.data.NOT_FINISHED_TRACKING
        }
      }
  })
}

//跳转
function toIntegrity(url: string) {
  router.push({
    path: url,
  });
}

const closeWindow = () => {
  //Android js交互，在关闭界面时将Android页面也一起关闭
  //请勿删除此代码，删除后Android端将无法在点击返回按钮退回
  const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
  if (caringCurrentDevice) {
    window.parent.postMessage({action: 'patientManageClose'}, '*')
  } else if ((window as any).android) {
    (window as any).android.finishActivity();
  } else if (window.parent) {
    window.parent.postMessage({action: 'patientManageClose'}, '*')
  } else {
    window.close();
  }

};
</script>
<style lang="less" scoped>
.index_box {
  background: rgba(245, 245, 245, 1);
  height: 100vh;

  .i_img_top {
    background: linear-gradient(90deg, #3f86ff 0%, #6ea8ff 100);
    position: relative;

    .workbenchBg {
      // width: 100%;
      // height: 100%;
      height: 150px;
      width: 100%;
      // z-index: -1;
    }
  }

  .workbenchP {
    z-index: 1000;
    position: absolute;
    top: 53px;
    left: 30px;
    color: white;
    font-size: 19px;
    font-weight: 400;
  }

  .title {
    font-size: 16px;
    line-height: 27px;
    color: #333333;
    margin-left: 20px;
  }

  .cellbox {
    // width: 350px;
    // position: absolute;
    // top: 165px;
    // left: 13px;
    margin-top: -35px;
    position: relative;
    z-index: 111;

    .van-badge--bottom-left {
      transform: none !important;
    }

    .badge {
      min-width: 22px;
      padding: 1px;
      // height: 21px;
      // line-height: 16.5px;
      background: rgba(255, 119, 119, 1);
      color: #ffffff;
      font-size: 14px;
    }

    .index_content {
      height: 22px;
      background: rgba(255, 119, 119, 0.39);
      border-radius: 11px;
      font-size: 14px;
      padding: 0px 8px;
      color: #ffffff;
    }
  }

  .div-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 63px;
    // margin-top: -30px;
    background: rgba(255, 255, 255);
    // opacity: 1;
    border-radius: 6px;

    // margin-right: 20px;
    .item_img {
      width: 21px;
      height: 21px;
      line-height: 21px;
    }

    .custom-title {
      display: inline-block;
      margin-right: 20px;
    }

    .item-left {
      display: flex;
      align-items: center;
    }
  }

  .van-cell-group--inset {
    margin-bottom: 12px;
  }

  .no_data {
    text-align: center;
    background: #FFF;
    margin: 0px 10px;
    border-radius: 10px;
    display: flex;
    height: calc(100vh - 300px);
    justify-content: center;
  }

  ::v-deep .van-cell__title, .van-cell__value {
    flex: 2.5;
  }
}
</style>
