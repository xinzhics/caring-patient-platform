<template>
  <div class="history_box">
    <!-- app头部 -->
    <van-sticky>
      <appHeader :title="'管理历史'" />
    </van-sticky>
    <div v-if="list.length > 0">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="handelList"
      >
        <div
          class="history_item"
          v-for="(item, index) in list"
          :key="index"
          @click="handelPage(item)"
        >
          <div>
            <p class="h_title">{{ item.historyType }}</p>
            <div class="h_time">
              {{ IStatus[item.sendType] }}丨{{ item.createTime }}
            </div>
          </div>
          <div class="h_right">
            <img
              src="../../assets/images/history_doctor_icon.png"
              class="doctor_icon"
            />
            <p class="p_num">{{ item.patientNumber }}人</p>
            <img src="../../assets/images/back.png" />
          </div>
        </div>
      </van-list>
    </div>
    <div class="no_data" v-else>
      <img src="../../assets/images/no_data.png" />
      <p>暂无数据</p>
    </div>
  </div>
</template>
<script lang="ts" setup>
import appHeader from "@/components/header/index.vue";
import { useRouter } from "vue-router";
import { administration } from "@/api";
import { onMounted } from "@vue/runtime-core";
import { IStatus } from "@/utils/enum";
import { ref } from "vue";
const router = useRouter();
const list = ref<any>([]);
const finished = ref<any>(false); //是否已加载完成，加载完成后不再触发 load 事件
const pages = ref<number>(1);
const current = ref<number>(1);
const loading = ref<any>(false);
const handelPage = (item: any) => {
  router.push({
    path: "/historyInfo",
    query: {
      orgId: item.id,
      sendType: item.sendType,
    },
  });
};
onMounted(() => {
  handelList();
});
// 获取历史
const handelList = async () => {
  if (pages.value < current.value) {
    finished.value = true;
    console.log(12);
    return;
  }
  const data: any = await administration.historyPage({
    current: current.value,
    size: 10,
    model: {
      nursingId: localStorage.getItem("caring-userId"),
    },
  });
  loading.value = false;
  if (data.isSuccess) {
    list.value = list.value.concat(data?.data.records);
    pages.value = data.data?.pages;
    current.value = current.value + 1;
  }
};
</script>
<style lang="less" scoped>
.history_box {
  background: rgba(245, 245, 245, 0.39);
  min-height: 100vh;

  .history_item {
    margin-top: 13px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 17px 13px;
    background: #ffffff;
    .h_title {
      font-size: 16px;
      font-weight: 400;
      line-height: 27px;
      color: #333333;
    }
    .h_time {
      font-size: 13px;
      font-weight: 400;
      line-height: 21px;
      color: #999999;
    }
    .h_right {
      display: flex;
      align-items: center;
      .p_num {
        font-size: 16px;
        font-weight: 400;
        line-height: 27px;
        color: #3f86ff;
      }
      .doctor_icon {
        width: 16px;
        height: 16px;
        margin-right: 7px;
      }
      img {
        width: 21px;
        height: 21px;
        margin-left: 8px;
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
