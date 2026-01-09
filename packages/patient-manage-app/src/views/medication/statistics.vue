<template>
  <van-sticky>
    <appHeader :title="'用药预警统计'" />
  </van-sticky>
  <div class="statistics__topTime">
    <div class="left-content">
      <p class="title">
        {{ data.drugsName ? data.drugsName : "暂无药品信息" }}
      </p>
      <p class="size">
        <span>{{ data.spec }}</span>
        <span v-if="data.spec || data.manufactor"> |</span>
        <span>{{ data.manufactor }}</span>
      </p>
      <p class="sum">
        <img src="@/assets/images/user.png" alt="" /><span>当前用药：</span>
        <span>{{ data.drugsPatient ? data.drugsPatient : "0 " }}人</span>
      </p>
    </div>
    <div class="down" @click="check">
      <img src="@/assets/images/down.png" alt="" />
    </div>
  </div>
  <div class="sumdata"></div>
  <div id="myChart"></div>
  <div class="statistics__table">
    <div class="statistics__table__tr">
      <div>用药预警统计</div>
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
        {{ item.name }}
      </div>
      <div>{{ item.sum }}</div>
      <div>{{ item.value + " " }}人</div>
    </div>
    <van-popup
      v-model:show="showpopup"
      position="bottom"
      close-on-popstate
      :style="{ height: '60%' }"
    >
      <div
        :class="index == checkindex ? 'checkitem' : 'item'"
        v-for="(item, index) in drugslist"
        :key="item.drugsId"
        @click="Changeitem(item, index)"
      >
        <div>
          <p class="title">{{ item.drugsName }}</p>
          <p class="size">
            <span>{{ item.spec }}</span
            >|<span>{{ item.manufactor }}</span>
          </p>
        </div>
        <img
          src="@/assets/images/info_no_check.png"
          v-if="index != checkindex"
        />
        <img src="@/assets/images/info_check.png" v-else />
      </div>
    </van-popup>
  </div>
</template>
<script  lang="ts" setup>
import * as echarts from "echarts";
import appHeader from "@/components/header/index.vue";
import medicationApi from "@/api/medicationwarn";
import { onMounted, ref } from "vue";
const list = ref<Array<any>>([]);
const data = ref<any>({});
const showpopup = ref<boolean>(false);
const drugslist = ref<Array<any>>([]);
const checkindex = ref<number>(0);
const myChart = ref();
const colors = ref([
  "rgba(178, 220, 159, 1)",
  "rgba(243, 149, 149, 1)",
  "rgba(251, 216, 138, 1)",
]);

//请求数据
const getData = () => {
  medicationApi.getDrugsList().then((res) => {
    if (res && res.data) {
      drugslist.value = res.data;
      const item = res.data[0] || {};
      GetStatistics(item);
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
const check = () => {
  showpopup.value = !showpopup.value;
};
const Changeitem = (item: any, index: number) => {
  checkindex.value = index;
  GetStatistics(item);
  check();
};
const GetStatistics = async (item: object) => {
  const { drugsId }: any = item;
  if (drugsId) {
    const res = await medicationApi.getDrugsStatistics(drugsId);
    if (res && res.data) {
      data.value = res.data;
      list.value = [
        {
          name: "正常",
          sum: res.data.normalPatientRatio || "0%",
          value: res.data.normalPatient || 0,
        },
        {
          name: "购药逾期",
          sum: res.data.drugsOverduePatientRatio || "0%",
          value: res.data.drugsOverduePatient || 0,
        },
        {
          name: "余药不足",
          sum: res.data.drugsDeficiencyPatientRatio || "0%",
          value: res.data.drugsDeficiencyPatient || 0,
        },
      ];
    }
  } else {
    list.value = [
      {
        name: "正常",
        sum: "0%",
        value: 0,
      },
      {
        name: "购药逾期",
        sum: "0%",
        value: 0,
      },
      {
        name: "余药不足",
        sum: "0%",
        value: 0,
      },
    ];
  }

  drawChart();
};
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

.sumdata {
  width: 350px;
  margin: auto;
  line-height: 42px;
  font-size: 15px;
  color: #999999;
  display: flex;
  justify-content: space-between;
}

.statistics__topTime {
  height: 100px;
  background: linear-gradient(90deg, #3f86ff 0%, #6ea8ff 100%);
  border-radius: 6px 6px 6px 6px;
  margin: 13px;
  display: flex;
  justify-content: space-between;

  .left-content {
    margin-left: 16px;
    margin-top: 19px;
  }
  .down {
    margin-right: 19px;
    margin-top: 18px;
    img {
      width: 15px;
      height: 15px;
    }
  }
  .title {
    font-size: 17px;
    font-weight: 500;
    color: #ffffff;
  }
  .size {
    font-size: 9px;
    color: #ffffff;
    margin-top: 7px;
    span:first-child {
      padding-right: 6px;
    }
    span:last-child {
      padding-left: 6px;
    }
  }
  .sum {
    font-size: 14px;
    color: #ffffff;
    margin-top: 19px;
    padding-bottom: 14px;
    img {
      width: 11px;
      height: 11px;
      margin-right: 6px;
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
  .item {
    width: 335px;
    height: 110px;
    margin: auto;
    display: flex;
    justify-content: space-between;
    border-bottom: 0.5px solid #e8e8e8;
    div {
      margin-left: 25px;
      margin-top: 41px;
    }
    img {
      width: 33px;
      height: 33px;
      margin-top: 45px;
      margin-right: 15px;
    }
    .title {
      font-size: 17px;
      color: #666666;
      max-width: 220px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .size {
      font-size: 13px;
      padding-bottom: 1px;
      max-width: 220px;
      color: #999999;
      margin-top: 10px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      span:first-child {
        padding-right: 6px;
      }
      span:last-child {
        padding-left: 6px;
      }
    }
  }
  .checkitem {
    width: 335px;
    height: 110px;
    margin: auto;
    display: flex;
    justify-content: space-between;
    border-bottom: 0.5px solid #e8e8e8;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    div {
      margin-left: 25px;
      margin-top: 41px;
    }
    img {
      width: 33px;
      height: 33px;
      margin-top: 45px;
      margin-right: 15px;
    }
    .title {
      font-size: 17px;
      color: #1890ff;
      max-width: 220px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    .size {
      font-size: 13px;
      padding-bottom: 1px;
      color: #1890ff;
      max-width: 220px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      margin-top: 10px;
      span:first-child {
        padding-right: 6px;
      }
      span:last-child {
        padding-left: 6px;
      }
    }
  }
}
</style>
