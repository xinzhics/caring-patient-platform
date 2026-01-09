<template>
  <div class="history_info_box">
    <!-- app头部 -->
    <van-sticky>
      <appHeader :title="'详情'" :del="'true'" @handelDel="handelDel" />
    </van-sticky>
    <div class="h_i_top">
      <div class="h_i_t_title">
        <p>信息完整度 - {{ IStatus[route?.query?.sendType] }}</p>
        <div class="h_i_t_right">
          <img src="../../assets/images/doctor_icon.png" alt="" />
          <span> 共{{ total }}人</span>
        </div>
      </div>
    </div>
    <div class="h_list" v-if="list.length > 0">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="handelInfo"
      >
        <div
          class="h_item"
          v-for="(item, index) in list"
          :key="index"
          :style="{ marginTop: index === 0 ? '-30px' : '13px' }"
        >
          <div class="h_i_left">
            <!-- <img src="../../assets/images/doctor_portrait.png" /> -->
            <img :src="item.avatar ? item.avatar : default_avatar" />
            <div>
              <p class="h_title">{{ item.patientName }}</p>
              <div class="h_doctor">所属医生：{{ item.doctorName }}</div>
            </div>
          </div>
          <div class="h_right">
            <p>{{ item.updateTime }}</p>
          </div>
        </div>
      </van-list>
    </div>
    <div class="no_data" v-else>
      <img src="../../assets/images/no_data.png" />
      <p>暂无数据</p>
    </div>
    <commonDialog
      :title="'删除确认'"
      :content="'确定要删除发送记录吗？'"
      :isShow="isShow"
      @handelCancel="handelCancel"
      @handelConfirm="handelConfirm"
    />
  </div>
</template>
<script lang="ts" setup>
import appHeader from "@/components/header/index.vue";
import commonDialog from "@/components/commonDialog/index.vue";
import default_avatar from "@/assets/images/default_ avatar.png";
import useHistory from "./useHistory";
import { IStatus } from "@/utils/enum";
import { useRoute } from "vue-router";
const route = useRoute();
const {
  list,
  isShow,
  handelDel,
  handelCancel,
  handelConfirm,
  finished,
  loading,
  handelInfo,
  total,
} = useHistory();
</script>
<style lang="less" scoped>
.history_info_box {
  background: rgba(245, 245, 245, 0.39);
  min-height: 100vh;
  .h_i_top {
    height: 84px;
    background: linear-gradient(90deg, #3f86ff 0%, #6ea8ff 100%);
    box-shadow: 0px 2px 2px rgba(51, 126, 255, 0.08);
    .h_i_t_title {
      padding: 27px 18px 23px 18px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      p {
        font-size: 14px;
        font-weight: 400;
        line-height: 23px;
        color: #ffffff;
      }
      .h_i_t_right {
        display: flex;
        align-items: center;
        justify-content: space-between;
      }

      img {
        width: 21px;
        height: 21px;
        padding-right: 4px;
      }
      span {
        font-size: 15px;
        line-height: 11px;
        color: #ffffff;
      }
    }
  }
  .h_list {
    padding: 12px;
    .h_item {
      margin-top: 13px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px;
      background: #ffffff;
      border-radius: 4px;
      img {
        width: 42px;
        height: 42px;
        border-radius: 50%;
        margin-right: 8px;
      }
      .h_title {
        font-size: 16px;

        font-weight: 500;

        line-height: 19px;
        color: #333333;
      }
      .h_doctor {
        font-size: 13px;
        font-weight: 400;
        line-height: 25px;
        color: #999999;
      }
      .h_right {
        font-size: 15px;
        font-weight: 400;
        line-height: 25px;
        color: #666666;
      }
      .h_i_left {
        display: flex;
      }
    }
  }
}
.no_data {
  margin-top: 125px;
  margin: 125px auto 0px auto;
  text-align: center;
  img {
    width: 146px;
    height: 146px;
  }
  p {
    font-size: 15px;
    line-height: 19px;
    color: #999999;
    margin-top: 13px;
    text-align: center;
  }
}
</style>
