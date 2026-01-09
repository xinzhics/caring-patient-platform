<template>
  <van-sticky>
    <appHeader :title="'完整度统计'" />
  </van-sticky>
  <div class="statistics__topTime">
    <div class="statistics__topTime__iconButton">
      <van-icon
        name="play"
        class="statistics__topTime__iconButton_icon"
        size="12"
        @click="before"
      />
    </div>
    <div class="statistics__topTime__center">
      <div class="statistics__topTime__center__time">
        {{ showDate }}
      </div>
      <div class="statistics__topTime__center__count">
        <van-icon name="manager" size="10" />
        <span>{{ patientTotal }}</span>
      </div>
    </div>
    <div class="statistics__topTime__iconButton">
      <van-icon name="play" v-if="isButtonShow" size="12" @click="after" />
    </div>
  </div>
  <div id="myChart"></div>
  <div class="statistics__table">
    <div class="statistics__table__tr">
      <div>信息完整度统计</div>
      <div>占比</div>
      <div>患者数</div>
    </div>
    <div
      class="statistics__table__tr statistics__table__towtr"
      v-for="(item, index) in list"
      :key="index"
    >
      <div>
        <span class="color-panl" :style="{ background: colors[index] }"></span>
        {{ item.intervalName }}
      </div>
      <div>{{ item.intervalProportion }}%</div>
      <div>{{ item.patientNumber }}</div>
    </div>
  </div>
</template>
<script  lang="ts" setup>
import * as echarts from "echarts";
import appHeader from "@/components/header/index.vue";
import { onMounted, ref, watchEffect, computed } from "vue";

import api from "@/api/commapi";
import moment from "moment";
const list = ref([] as any);
const patientTotal = ref(0);
const today = ref(moment(new Date()).format("YYYY/MM/DD")); //今天
const currentDate = ref<string>(today.value);
const myChart = ref();
const isButtonShow = ref(false);
const date = ref(new Date());
const oneDay = 1000 * 60 * 60 * 24; //一天的毫秒数
const colors = ref([
  "rgba(178, 220, 159, 1)",
  "rgba(132, 154, 212, 1)",
  "rgba(251, 216, 138, 1)",
  "rgba(243, 149, 149, 1)",
  "rgba(178, 220, 159, 1)",
  "rgba(132, 154, 212, 1)",
  "rgba(251, 216, 138, 1)",
  "rgba(243, 149, 149, 1)",
  "rgba(178, 220, 159, 1)",
  "rgba(132, 154, 212, 1)",
  "rgba(251, 216, 138, 1)",
  "rgba(243, 149, 149, 1)",
]);

//请求数据
const getData = () => {
  let nursingId = localStorage.getItem("caring-userId");
  let params = {
    nursingId: nursingId,
    date: currentDate.value.replaceAll("/", "-"),
  };
  api.getCompletionInformationStatistics(params).then((res) => {
    if (res && res.data) {
      list.value = res.data.statisticsDetailList || [];
      patientTotal.value = res.data.patientTotal;
      list.value.map((item: { value: any; intervalProportion: any }) => {
        item.value = item.intervalProportion;
      });
    } else {
      list.value = [];
      patientTotal.value = 0;
    }
    drawChart();
  });
};

//计算前一天
const before = () => {
  let dd = date.value
  dd.setDate(dd.getDate() - 1);
  let y = dd.getFullYear();
  let m = dd.getMonth() + 1 < 10 ? "0" + (dd.getMonth() + 1) : dd.getMonth() + 1;
  let d = dd.getDate() < 10 ? "0" + dd.getDate() : dd.getDate();
  currentDate.value = y + "/" + m + "/" + d;
  date.value = new Date(y + "-" + m + "-" + d)
  getData();
};

//计算后一天
const after = () => {
  let dd = date.value
  dd.setDate(dd.getDate() + 1);
  let y = dd.getFullYear();
  let m = dd.getMonth() + 1 < 10 ? "0" + (dd.getMonth() + 1) : dd.getMonth() + 1;
  let d = dd.getDate() < 10 ? "0" + dd.getDate() : dd.getDate();
  currentDate.value = y + "/" + m + "/" + d;
  date.value = new Date(y + "-" + m + "-" + d)
  getData();
};


//封装日期格式化
const myGetDate = (day: Date) => {
  return `${day.getFullYear()}-${day.getMonth() + 1}-${day.getDate()}`;
};

const drawChart = () => {
  var option = {
    series: [
      {
        name: "",
        type: "pie",
        radius: ["40%", "70%"],
        labelLine: {
          show: false,
        },
        data: list.value,
      },
    ],
    color: colors.value,
  };
  option && myChart.value.setOption(option);
};

const showDate = computed(() => {
  if (currentDate.value  === "" || currentDate.value === today.value) {
    isButtonShow.value = false;
    return "今天"
  } else {
    isButtonShow.value = true;
    return currentDate.value.replaceAll("/", "-")
  }
});
onMounted(() => {
  myChart.value = echarts.init(document.getElementById("myChart") as any);
  getData();
});
</script>


<style lang="less" scoped>
#myChart {
  width: 300px;
  height: 300px;
  margin: 0 auto;
}

.statistics__topTime {
  height: 80px;
  background: linear-gradient(90deg, #3f86ff 0%, #6ea8ff 100%);
  border-radius: 6px 6px 6px 6px;
  margin: 12px;
  display: flex;

  &__iconButton {
    flex: 1;
    align-items: center;
    display: grid;
    color: #ffffff;

    &_icon {
      transform: rotate(180deg);
    }
  }

  &__iconButton:last-child {
    text-align: left;
  }

  &__center {
    flex: 3;
    display: grid;
    align-items: center;
    text-align: center;
    margin-top: 12px;
    margin-bottom: 5px;

    &__time {
      font-size: 24px;
      font-family: Source Han Sans CN-Medium, Source Han Sans CN;
      font-weight: 500;
      color: #ffffff;
    }

    &__count {
      font-size: 13px;
      font-weight: 400;
      color: #ffffff;
      line-height: 12px;
    }
  }
}

.color-panl {
  width: 20px;
  height: 20px;
  border-radius: 5px;
  background: #b2dc9f;
  margin-right: 8px;
}

.statistics__table {
  font-size: 14px;
  font-family: Source Han Sans CN-Regular, Source Han Sans CN;
  font-weight: 400;
  border-radius: 6px 6px 6px 6px;
  border: 1px solid #d6d6d6;
  margin: 28px 12px;

  &__tr {
    display: flex;
    height: 44px;
    padding: 0 16px;
    font-weight: 400;
    color: #333333;
    align-items: center;

    &:first-child {
      background: rgba(82, 146, 255, 0.08);
      color: #999999;
    }

    div {
      flex: 2;
      display: flex;
      align-items: center;

      &:nth-child(2) {
        flex: 1;
        flex-direction: column;
        width: 20px;
      }

      &:nth-child(3) {
        flex: 1;
        flex-direction: column;
        width: 20px;
      }
    }
  }

  &__towtr:nth-of-type(odd) {
    background: rgba(82, 146, 255, 0.02);
  }
}
</style>
