<template>
  <van-sticky>
    <appHeader :title="planName" />
  </van-sticky>
  <div class="statistics__topTime">
    <div class="statistics__topTime__iconButton">
      <van-icon
        name="play"
        class="statistics__topTime__iconButton_icon"
        size="12"
        @click="changemonth('reduce')"
      />
    </div>
    <div class="statistics__topTime__center">
      <div class="statistics__topTime__center__time">
        {{ showDate }}
      </div>
    </div>
    <div class="statistics__topTime__iconButton">
      <van-icon
        name="play"
        v-if="isButtonShow"
        size="12"
        @click="changemonth('add')"
      />
    </div>
  </div>
  <div class="sumdata">
    <div>
      累计提交：<span style="color: #337eff" v-if="data.totalSubmissions">{{
        data.totalSubmissions + "条"
      }}</span>
      <span v-else>0条</span>
    </div>
    <div>
      异常数据：<span
        style="color: #ff7777"
        v-if="data.submitExceptionQuantity"
        >{{ data.submitExceptionQuantity + "条" }}</span
      >
      <span v-else>0条</span>
    </div>
  </div>
  <div id="myChart"></div>
  <div class="statistics__table">
    <div class="statistics__table__tr">
      <div>范围统计</div>
      <div>占比</div>
      <div>数据</div>
    </div>
    <div
      class="statistics__table__tr statistics__table__towtr"
      v-for="(item, index) in list"
      :key="index"
    >
      <div>
        <span class="color-panl" :style="{ background: colors[index] }"></span>
        {{ item.name }}
      </div>
      <div>{{ item.sum }}</div>
      <div>{{ item.value }}条</div>
    </div>
  </div>
</template>
<script  lang="ts" setup>
import * as echarts from "echarts";
import { useRoute } from "vue-router";
import appHeader from "@/components/header/index.vue";
import { onMounted, ref, watchEffect, computed } from "vue";
import patientmonitor from "@/api/patientmonitor";
import information from "@/api/information";
import moment from "moment";
const list = ref<Array<any>>([]);
const data = ref<any>({});
const myChart = ref();
const isButtonShow = ref(false);
const planId = ref<any>("");
const planName = ref<any>("");
const month = moment(new Date()).format("YYYY/MM"); //当前月份
let monthchange = ref<any>(month); //改变的月份
const route: any = useRoute();
const colors = ref([
  "rgba(178, 220, 159, 1)",
  "rgba(243, 149, 149, 1)",
  "rgba(220, 233, 254, 1)",
]);

//请求数据
const getData = () => {
  const date = monthchange.value.replace("/", ".")
  patientmonitor
    .statisticsAbnormalQuantity(date, planId.value)
    .then((res) => {
      if (res && res.data) {
        data.value = res.data;
        list.value = [
          {
            name: "异常(已处理)",
            sum: res.data.numberExceptionsQuantityPercentage,
            value: Math.round(res.data.numberExceptionsQuantity),
          },
          {
            name: "异常(未处理)",
            sum: res.data.untreatedExceptionsQuantityPercentage,
            value: Math.round(res.data.untreatedExceptionsQuantity),
          },
          {
            name: "正常",
            sum: res.data.normalQuantityPercentage,
            value: Math.round(res.data.normalQuantity),
          },
        ];

        drawChart();
      }
    });
};
const drawChart = () => {
  var option = {
    series: [
      {
        name: "",
        type: "pie",
        radius: ["32%", "70%"],
        labelLine: {
          show: false,
        },
        label: {
          normal: {
            show: false,
          },
        },
        data: list.value,
      },
    ],
    color: colors.value,
  };
  option && myChart.value.setOption(option);
};
//显示月份
const showDate = computed(() => {
  if (monthchange.value && monthchange.value == month) {
    return '本月'
  } else {
    return monthchange.value.replace("/", ".")
  }
});
//上一个月
const changemonth = (type: string) => {
  let arr = monthchange.value.split('/')
  let year = arr[0]; //获取当前日期的年份
  let month = arr[1]; //获取当前日期的月份
  if (type == "reduce") {
    let year2 = year;
    let month2 = parseInt(month) - 1;
    if (month2 == 0) {
      year2 = parseInt(year2) - 1;
      month2 = 12;
    }
    if (month2 < 10) {
      month = '0' + month2;
    } else {
      month = month2
    }
    monthchange.value = year2 + '/' + month;
  } else {
    let year2 = year;
    let month2 = parseInt(month) + 1;
    if (month2 == 13) {
      year2 = parseInt(year2) + 1;
      month2 = 1;
    }
    if (month2 < 10) {
      month = '0' + month2;
    } else {
      month = month2
    }
    monthchange.value = year2 + '/' + month;
  }
};
onMounted(() => {
  myChart.value = echarts.init(document.getElementById("myChart") as any);
  planId.value = route.query.id;
  planName.value = route.query.planName;
  getData();
});
watchEffect(() => {
  isButtonShow.value = monthchange.value != month;
  if (planId.value !== "") {
    getData();
  }
});
</script>


<style lang="less" scoped>
#myChart {
  width: 300px;
  height: 300px;
  margin: 0 auto;
}

.sumdata {
  width: 350px;
  height: 42px;
  margin: auto;
  line-height: 42px;
  font-size: 15px;
  color: #999999;
  display: flex;
  justify-content: space-between;
}

.statistics__topTime {
  height: 80px;
  background: linear-gradient(90deg, #3f86ff 0%, #6ea8ff 100%);
  border-radius: 6px 6px 6px 6px;
  margin: 12px 12px 0px 12px;
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
  margin: 0 12px 25px 12px;

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
