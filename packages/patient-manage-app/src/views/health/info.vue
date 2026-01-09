<template>
  <div class="i_box">
    <div class="i_box_top">
      <div class="i_box_left">
        <div>
          <p class="i_title">您的健康档案 还未填写完成</p>
          <p class="i_doctor">{{ info.doctorName }}医生</p>
          <p class="i_message">提醒您尽快完善以下信息</p>
        </div>
        <div>
          <img src="../../assets/images/info_img.png" />
        </div>
      </div>
    </div>
    <div
      class="i_content"
      v-if="
        info.incompleteInformationFields &&
        info.incompleteInformationFields.length > 0
      "
    >
      <div
        v-for="(item, index) in info.incompleteInformationFields"
        :key="index"
        :class="index % 2 == 0 ? 'i_content_item' : 'i_content_item_next'"
      >
        <span></span> {{ item.planName }} - {{ item.fieldLabel }}
      </div>
    </div>
    <div class="i_box_bottom" @click="handelClose">我知道了</div>
  </div>
</template>
<script lang="ts" setup>
import { administration } from "@/api";
import { onMounted } from "@vue/runtime-core";
import { useRoute } from "vue-router";
import { ref } from "vue";
const route = useRoute();
const info = ref<any>({});
document.title = "详情";
const handelClose = () => {
  window.opener = null;
  window.open("", "_self", "");
  window.close();
  if (navigator.userAgent.indexOf("MicroMessenger") > 0) {
    window.WeixinJSBridge.call("closeWindow");
  }
};
onMounted(() => {
  if (route?.query?.tenant) {
    let tenant = "";
    if (route.query.tenant) {
      tenant = route.query.tenant;
    }
    const tenantEncode = encodeURI(tenant);
    // 对编码的字符串转化base64
    var base64 = btoa(tenantEncode);
    localStorage.setItem("caring-tenant", base64);
  }
  handelInfo();
});
const handelInfo = async () => {
  const data: any = await administration.findIncompleteInformation({
    // patientId: route.query.patientId || "1522825955616227328",
    // tenant: route.query.tenant || "MDExMg==",
    // code: route.query.tenant || "0112",
    patientId: route.query.patientId,
    tenant: route.query.tenant,
    code: route.query.tenant,
  });
  if (data.isSuccess) {
    info.value = data.data;
  }
};
</script>
<style lang="less" scoped>
.i_box {
  .i_box_top {
    // height: 192px;
    background: linear-gradient(90deg, #3f86ff 0%, #6ea8ff 100%);
    padding: 42px 42px 35px 42px;
    img {
      width: 125px;
      height: 125px;
    }
    .i_box_left {
      display: flex;
      justify-content: space-between;
      .i_title {
        font-size: 23px;
        font-weight: bold;
        line-height: 26px;
        color: #ffffff;
        opacity: 0.8;
      }
      .i_doctor {
        margin-top: 16px;
        font-size: 13px;
        font-weight: 500;
        line-height: 21px;
        color: #ffffff;
        opacity: 0.8;
      }
      .i_message {
        font-size: 13px;
        margin-top: 6px;
        font-weight: 400;
        line-height: 21px;
        color: #ffffff;
        opacity: 0.8;
      }
    }
  }
  .i_content {
    border: 1px solid #d6d6d6;
    border-radius: 6px;
    margin: 30px 28px 60px 30px;
    .i_content_item {
      background: linear-gradient(
        224deg,
        rgba(82, 146, 255, 0.1) 0%,
        rgba(110, 168, 255, 0.1) 100%
      );
      padding: 20px;
      display: flex;
      align-items: center;
      font-size: 15px;
      color: #333333;
      span {
        width: 4px;
        height: 4px;
        background: rgba(50, 126, 251, 1);
        border-radius: 50%;
        margin-right: 20px;
        display: inline-block;
      }
    }
    .i_content_item_next {
      padding: 20px;
      display: flex;
      align-items: center;
      font-size: 15px;
      color: #333333;
      span {
        width: 4px;
        height: 4px;
        background: rgba(50, 126, 251, 1);
        border-radius: 50%;
        margin-right: 20px;
        display: inline-block;
      }
    }
  }
  .i_box_bottom {
    width: 208px;
    height: 46px;
    background: rgba(51, 126, 255, 1);
    opacity: 1;
    border-radius: 42px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: auto;
    color: #ffffff;
    font-size: 15px;
    margin-bottom: 50px;
  }
}
</style>
