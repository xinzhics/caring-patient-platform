<template>
  <div class="index_box">
    <!-- app头部 -->
    <van-sticky>
      <appHeader :title="'异常选项跟踪'"
                 :leftCustomize="true"
                 @handelPage="backIndex"/>
    </van-sticky>
    <div class="cellbox">
      <div v-if="isLoading">
        <div v-if="displaydata.length > 0">
          <van-cell-group inset v-for="item in displaydata" :key="item">
            <div class="div-item" @click="jump(item)">
              <van-cell is-link :title="item.planName">
                <template #value>
                  <van-badge v-if="item.noHandlePatientNumber > 0" :content="item.noHandlePatientNumber" max="99" position="bottom-left"
                             class="badge"/>
                </template>
              </van-cell>
            </div>
          </van-cell-group>
        </div>
        <div v-else style="padding-top: 120px">
          <van-empty description="空空如也~" :image-size="[200, 100]">
            <template v-slot:image>
              <img src="@/assets/images/abnormal_no_data.png" alt=""/>
            </template>
          </van-empty>
        </div>
      </div>

    </div>
  </div>
</template>

<script lang="ts" setup>

import appHeader from "@/components/header/index.vue";
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";

const router = useRouter();
import configApi from "@/api/config";
import commApi from "@/api/commapi";
import {Empty, Image as VanImage} from 'vant';

const isLoading = ref<boolean>(false)

const displaydata = ref<any>([]);

onMounted(() => {
  getAppTracePlanList();
});

//跳转
function jump(item) {
  router.push({
    path: item.path,
    query: {
      planName: item.planName,
      formId: item.formId,
      planId: item.planId,
      planType: item.planType,
    }
  });
}

const backIndex = () =>{
  router.replace("/index")
}
// 异常选项跟踪
const getAppTracePlanList = () => {
  configApi.getAppTracePlanList()
      .then(res => {
        isLoading.value = true
        res.data.forEach(item => {
          displaydata.value.push({
            ...item,
            path: "/abnormal/list",
          })
        })
      })
}

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
}
</style>
