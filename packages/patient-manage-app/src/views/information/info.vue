<template>
  <div class="info_box">
    <van-sticky>
      <appHeader :title="'详情'" />
    </van-sticky>
    <div class="info_top">
      <div class="h_i_left" @click="jumpPatientInfo()">
        <!-- <img :src="info?.avatar" /> -->
        <img :src="info?.avatar ? info?.avatar : default_avatar" />

        <div>
          <p class="h_title">{{ info?.patientName }}</p>
          <div class="h_doctor">所属医生：{{ info?.doctorName }}</div>
        </div>
      </div>
      <div class="line"></div>
      <div class="i_t_bottom">
        <div>
          <p class="i_t_b_info">信息完整度：{{ info?.completion }}%</p>
          <p>上 次 填 写：{{ info?.lastWriteTime || "-" }}</p>
        </div>
        <div
          class="i_t_b_right"
          @click="handelShow"
          v-if="info?.completion != 100"
        >
          <img src="../../assets/images/info_all.png" alt="" />
          <span>提醒TA完善</span>
        </div>
      </div>
    </div>
    <!-- 基本信息 -->
    <commonList :list="base" :title="'基本信息'" />
    <!-- 疾病信息 -->
    <commonList :list="jkda" :title="'疾病信息'" />
    <!-- 复查提醒 -->
    <commonList :list="fctx" :title="'复查提醒'" />
    <!-- 健康日志 -->
    <commonList :list="jkrz" :title="'健康日志'" />
    <!-- 循环一下 map -->
    <!-- 监测计划 -->
    <div v-for="(name, index) in planNames">
      <commonList :list="planFields.get(name)" :title="name" />
    </div>
    <!-- 自定义监测计划 -->
<!--    <commonList :list="zdyjcjh" :title="'自定义监测计划'" />-->
    <!-- 自定义护理计划 -->
<!--    <commonList :list="dzyhljy" :title="'自定义护理计划'" />-->
  </div>
  <commonDialog
    :title="'提醒TA完善疾病信息?'"
    :isShow="isShow"
    @handelCancel="handelCancel"
    @handelConfirm="handelConfirm"
  />
</template>
<script lang="ts" setup>
import appHeader from "@/components/header/index.vue";
import commonDialog from "@/components/commonDialog/index.vue";
import useInfo from "./useInfo";
import commonList from "./components/index.vue";
import default_avatar from "@/assets/images/default_ avatar.png";
import {useRoute} from "vue-router";
const route = useRoute();

const {
  isShow,
  handelShow,
  handelCancel,
  handelConfirm,
  info,
  base,
  planNames,
  planFields,
  jkda,
  fctx,
  jkrz,
  jcjh,
  zdyjcjh,
  dzyhljy,
} = useInfo();

const jumpPatientInfo = () => {
  console.log('==============', info.value)
  const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
  if (caringCurrentDevice) {
    let history = { path: '/information/info', query: {id: route.query.id}}
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageSeePatient', data: info.value.patientId}, '*')
  } else if ((window as any).android) {
    (window as any).android.jumpPatientInfo(info.value.patientId);
  } else if (window.parent) {
    let history = { path: '/information/info', query: {id: route.query.id}}
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageSeePatient', data: info.value.patientId}, '*')
  }
}

</script>
<style lang="less" scoped>
.info_box {
  background: rgba(255, 255, 255, 0.39);
  .info_top {
    background: url("../../assets/images/info_bg.png");
    height: 192px;
    background-size: 100% 100%;
    .h_i_left {
      display: flex;
      padding: 32px 15px 20px 15px;
      img {
        width: 63px;
        height: 63px;
        border-radius: 50%;
        margin-right: 13px;
      }
      .h_title {
        font-size: 23px;
        font-weight: 500;
        line-height: 39px;
        color: #ffffff;
      }
      .h_doctor {
        font-size: 15px;
        font-weight: 400;
        line-height: 25px;
        opacity: 0.8;
        color: #ffffff;
      }
    }
    .line {
      border-top: 1px dashed #f0f0f0;
      opacity: 0.32;
      width: 342px;
      //   text-align: center;
      margin: 0px auto;
    }
    .i_t_bottom {
      //   margin-top: 16px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16px;
      p {
        font-size: 11px;
        font-weight: 400;
        line-height: 19px;
        color: #ffffff;
        opacity: 0.8;
      }
    }
    .i_t_b_right {
      width: 119px;
      height: 33px;
      background: #ffffff;
      border: 1px solid #ffffff;
      box-shadow: 0px 2px 3px rgba(0, 0, 0, 0.1);
      opacity: 1;
      border-radius: 104px;
      display: flex;
      align-items: center;
      img {
        width: 20px;
        height: 20px;
        margin-left: 10px;
        margin-right: 7px;
      }

      span {
        font-size: 13px;
        font-weight: 400;
        line-height: 21px;
        color: #333333;
        opacity: 0.8;
      }
    }
  }
}
</style>
