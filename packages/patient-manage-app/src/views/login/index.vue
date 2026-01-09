<template>
  <div id="myChart"></div>
</template>
<script >
import * as echarts from "echarts";
import { onMounted } from "vue";
import { defineComponent } from "vue";

export default defineComponent({
  setup() {
    onMounted(() => {
      var myChart = echarts.init(document.getElementById("myChart"));
      // 绘制图表
      var option = {
        series: [
          {
            name: "",
            type: "pie",
            radius: ["40%", "70%"],
            label: {
              position: "inner",
              fontSize: 14,
            },
            labelLine: {
              show: false,
            },
            data: [
              { value: 1048, name: "1048" },
              { value: 735 },
              { value: 580 },
              { value: 484 },
              { value: 300 },
            ],
          },
        ],
      };
      try {
        option && myChart.setOption(option);
      } catch (e) {

      }
    });
  },
});
</script>


<style lang="less" scoped>
#myChart {
  width: 300px;
  height:300px;
}
</style>
