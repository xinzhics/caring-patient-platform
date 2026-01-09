<template>
  <div class="index_box">
    <!-- app头部 -->
    <van-sticky>
      <appHeader :title="'监测数据'" :leftCustomize="true" @handelPage="backIndex" />
    </van-sticky>
    <div class="cellbox">
      <van-cell-group inset v-for="item in displaydata" :key="item">
        <div class="div-item" @click="item.func">
          <van-cell is-link :title="item.planName">
            <template #value>
              <van-badge v-if="item.patientNumber > 0" :content="item.patientNumber" max="99" position="bottom-left"
                class="badge" />
            </template>
          </van-cell>
        </div>
      </van-cell-group>
    </div>
  </div>
</template>
<script lang="ts" setup>
import appHeader from "@/components/header/index.vue";
import patientmonitor from "@/api/patientmonitor";
import { Cell, CellGroup, Badge } from "vant";
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();
//页面展示数据配置
const displaydata = ref<any>([]);
function toIntegrity(url: string, id: string, planName: string) {
  router.push({
    path: url,
    query: {
      planId: id,
      planName: planName,
    },
  });
}
const backIndex = () => {
  router.replace("/index")
}
onMounted(() => {
  patientmonitor.abnormalData().then((res: any) => {
    if (res.isSuccess) {
      displaydata.value = res.data.map((v: any, i: number) => {
        return {
          ...v,
          func: () => toIntegrity("/monitordata/list", v.planId, v.planName),
        };
      });
    }
  });
});
</script>
<style lang="less" scoped>
.index_box {
  background: rgba(245, 245, 245, 1);
  min-height: 100vh;

  .cellbox {
    // width: 98%;
    // position: absolute;
    // top: 60px;
    // left: 1%;
    margin-top: 13px;

    .van-badge--bottom-left {
      transform: none !important;
    }

    .badge {
      // min-width: 22px;
      // height: 21px;
      // line-height: 21px;
      min-width: 22px;
      // height: 21px;
      // line-height: 16.5px;
      background: rgba(255, 119, 119, 1);
      color: #ffffff;
      font-size: 17px;
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
    .item-left {
      display: flex;
      align-items: center;
    }
  }

  .van-cell-group--inset {
    margin: 0px 13px 13px 13px;
  }
  :deep(.van-cell){
    font-size: 16px;
  }
}
</style>
