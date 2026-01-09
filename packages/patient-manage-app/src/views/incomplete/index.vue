<template>
  <div class="index_box">
    <!-- app头部 -->
    <van-sticky>
      <appHeader :title="'未完成任务跟踪'"
                 :leftCustomize="true"
                 @handelPage="backIndex"/>
    </van-sticky>
    <div class="cellbox">
      <div v-if="isLoading">
        <div v-if="list.length > 0">
          <van-cell-group inset v-for="item in list" :key="item">
            <div class="div-item" @click="jump(item)">
              <van-cell is-link :title="item.planName">
                <template #value>
                  <div>{{item.noHandlePatientNumber > 0 ?  item.noHandlePatientNumber + '人未完成' : '全部完成'}}</div>
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
import Api from "@/api/incomplete";
const isLoading = ref<boolean>(false)
const list = ref<any>([]);

onMounted(() => {
  getIncompleteList();
});

//跳转
function jump(item: any) {
  router.push({
    path: '/incomplete/list',
    query: {
      planName: item.planName,
      planId: item.planId,
      unFinishedSettingId: item.unFinishedSettingId,
      planType: item.planType,
    }
  });
}
const backIndex = () =>{
  router.replace("/index")
}
// 异常选项跟踪
const getIncompleteList = () => {
  Api.getIncompleteList()
      .then(res => {
        isLoading.value = true
        list.value = res.data
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
