<template>
  <van-sticky>
    <appHeader :title="planName" />
  </van-sticky>
  <div class="index_box">
    <!-- 顶部标题-->

    <!--    月份-->
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
        累计提交：<span style="color: #337eff" v-if="data.allFormResultNumber">{{
          data.allFormResultNumber + "条"
        }}</span>
        <span v-else>0条</span>
      </div>
      <div>
        异常数据：<span
          style="color: #ff7777"
          v-if="data.abnormalNumber"
      >{{ data.abnormalNumber + "条" }}</span
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
          <span class="color-panl" :style="{ background:item.name==='异常'?'#F39595':'#DCE9FE' }"></span>
          {{ item.name }}
        </div>
        <div>{{ item.sum }}</div>
        <div>{{ item.value }}条</div>
      </div>
    </div>
<!--    条形显示-->
    <div class="line-charts">
      <div v-if="data.formFieldInfoAbnormalList" v-for="item in data.formFieldInfoAbnormalList" :key = 'item.fieldId' style="margin-bottom: 72px">
        <div style="display: flex;align-items: center;padding-bottom: 17px;border-bottom: 1px solid #337eff">
          <div style="width: 2px;height: 13px;border-radius: 4px;background: #337EFF" />
          <div style="font-size: 16px;margin-left: 7px;color: #272727">{{item.fieldName}}</div>
        </div>
        <div style="margin-top: 17px">
          <div v-if="item.fieldOptionAbnormalCountList" v-for="m in item.fieldOptionAbnormalCountList" :key="m" style="margin-bottom: 17px">
            <div style="display: flex;justify-content: space-between;margin-bottom: 9px">
              <div style="font-size: 13px;color: #999999">{{m.fieldOptionNameId}}</div>
              <div style="font-size: 12px;margin-right: 24px">{{m.count}}条</div>
            </div>
            <van-progress :percentage="parseInt(m.count/data.abnormalNumber*100)" :show-pivot="false"  stroke-width = '7'/>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {useRouter, useRoute} from "vue-router";
import {computed, onMounted, ref, watchEffect} from "vue";
import appHeader from "@/components/header/index.vue";
import moment from "moment";
import * as echarts from "echarts";
import abnormal from "@/api/abnormal";
import {Toast} from "vant";

const router = useRouter();
const route = useRoute();
const data = ref<any>({});
const list = ref<Array<any>>([]);
const myChart = ref();
const planName = ref<any>("");
const month = moment(new Date()).format("YYYY/MM"); //当前月份
const isButtonShow = ref(false);
const formId = ref<any>(""); //计划id
const colors = ref([
  "#F39595",
  "#DCE9FE",
]);


onMounted(() => {
  myChart.value = echarts.init(document.getElementById("myChart") as any);
  formId.value = route.query.formId;
  planName.value = route.query.planName;
  getData();

})

let monthchange = ref<any>(month); //改变的月份

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
//显示月份
const showDate = computed(() => {
  if (monthchange.value && monthchange.value == month) {
    return '本月'
  } else {
    return monthchange.value.replace("/", ".")
  }
});
const onClickLeft = () => {
  console.log(123)
}

//请求数据
const getData = () => {

  const params = {
    formId: route.query.formId,
    localDate: monthchange.value.replace("/", "-") + '-01'
  }

  console.log(params)
  abnormal
      .statistics(params)
      .then((res) => {
        if (res && res.data) {
          data.value = res.data;
          list.value = [
            {
              name: "异常",
              sum: res.data.abnormalNumber,
              value: res.data.abnormalNumber,

            },
            {
              name: "正常",
              sum: res.data.allFormResultNumber - res.data.abnormalNumber,
              value: res.data.allFormResultNumber - res.data.abnormalNumber,

            },
          ];
          drawChart();
        }
      });
};
// 画图
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
        itemStyle: {
          borderWidth: 6,	//边框的宽度
          borderColor: '#fff'	//边框的颜色
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
  // 如果有一个是100% 就不要间隔
  let num = 0
  for (let i: number = 0; i < list.value.length; i++) {
    if (list.value[i].value === 0) {
      num ++
    }
  }
  if (num === 1) {
    option.series[0].itemStyle.borderWidth = 0
  }
  console.log('option', option)
  option && myChart.value.setOption(option);
};
watchEffect(() => {
  isButtonShow.value = monthchange.value != month;
  if (formId.value !== "") {
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

.index_box {
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

  .color-panl {
    width: 20px;
    height: 20px;
    border-radius: 5px;
    background: #b2dc9f;
    margin-right: 8px;
  }
  .line-charts{
    padding:0 13px;
  }
}

</style>
