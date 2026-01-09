<template>
  <div id="patientManageHeader" class="header" :style="{padding: topHeight}">
    <div style="width: 15%; height: 30px; padding: 5px; text-align: left; display: flex; justify-content: left; align-items: center" @click="handelPage">
      <img src="../../assets/images/headerBack.png"  style="width: 30px; height: 30px;" />
    </div>
    <div style="width: 70%; text-align: center">
      <p>{{ title }}</p>
    </div>
    <div style="width: 15%; text-align: right; display: flex; justify-content: right; align-items: center" v-if="del || toHistory || rightIcon">
      <img style="width: 30px; height: 30px;" src="../../assets/images/del.png" v-if="del" @click="handelDel" />
      <img style="width: 30px; height: 30px;"
        src="../../assets/images/history.png"
        v-if="toHistory"
        @click="handelDel"
      />
      <img  style="width: 30px; height: 30px;" :src="rightIcon" v-if="rightIcon" @click="handelRightClick" />
    </div>
    <div v-else class="header_right"></div>
  </div>
</template>
<script lang="ts" setup>
import { useRouter } from "vue-router";
import { onMounted, ref } from "vue";
let topHeight = ref<string>("0 13px");
import useIntegrity from "@/views/information/useIntegrity";
const { title, del, leftCustomize, toHistory, rightIcon } = defineProps([
  "title",
  "del",
  "leftCustomize",
  "toHistory",
  "rightIcon",
]);
const emits = defineEmits(["handelDel", "handelPage", "click-right"]);
const router = useRouter();
onMounted(() => {
  const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
  if (caringCurrentDevice && caringCurrentDevice === 'weixin') {
    topHeight.value = '0px 13px 0 13px'
  } else {
    const isIphone = Boolean(navigator.userAgent.match(/iphone|ipod|iOS/ig)); // 是否是苹果浏览器
    if (isIphone) {
      topHeight.value = '55px 13px 0 13px'
    }
    const h5Environment = localStorage.getItem('H5_environment')
    if (h5Environment && h5Environment === 'uniApp') {
      topHeight.value = '45px 13px 0 13px'
    }
  }
})
const handelDel = () => {
  if (del) {
    emits("handelDel", false);
  }
  if (toHistory) {
    router.push({
      path: "/history",
    });
  }
};
const handelPage = () => {
  if (leftCustomize) {
    emits("handelPage", false);
  } else {
    router.go(-1);
  }
};
const handelRightClick = () => {
  emits("click-right");
};
</script>

<style lang="less" scoped>
.header {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  img {
    width: 25px;
    height: 25px;
  }
  p {
    font-size: 17px;
    font-weight: 500;
    color: #333333;
  }
  .header_right {
    width: 15%;
  }
}
</style>
