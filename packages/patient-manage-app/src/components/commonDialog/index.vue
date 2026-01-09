
<template>
  <Dialog.Component
    v-model:show="isDialog"
    :show-confirm-button="false"
    style="width: 280px"
  >
    <div class="dialog">
      <p
        :style="{
          padding: content ? '25px 0px 12px 0px' : '52px 0px 44px 0px',
        }"
      >
        {{ title }}
      </p>
      <div class="d_content" v-if="content">
        {{ content }}
      </div>
      <div class="btn_list">
        <div class="disagree" @click="handelCancel">{{ leftTitle }}</div>
        <div class="agree" @click="handelConfirm">{{ rightTitle }}</div>
      </div>
    </div>
  </Dialog.Component>
</template>

<script setup>

import { Dialog } from "vant";
import { defineProps, defineEmits, ref, watch } from "vue";
const props = defineProps({
  isShow: {
    type: Boolean,
    required: true,
  },
  title: {
    type: String,
    required: false,
    default: "",
  },
  content: {
    type: String,
    required: false,
    default: "",
  },

  leftTitle: {
    type: String,
    required: false,
    default: "取消",
  },
  rightTitle: {
    type: String,
    required: false,
    default: "确认",
  },
});

const emits = defineEmits(["handelConfirm", "handelCancel"]);
const isDialog = ref(false)

// 监听父组件传递的 isShow 变化并更新 localShow
watch(() => props.isShow, (newVal) => {
  isDialog.value = newVal;
});

const handelCancel = () => {
  isDialog.value = false
      emits("handelCancel", false);
};
const handelConfirm = () => {
  isDialog.value = false
  emits("handelConfirm", false);
};
</script>

<style scoped lang="less">
.dialog {
  box-sizing: border-box;
  p {
    font-size: 17px;
    font-weight: 500;
    color: #333333;
    text-align: center;
    padding: 25px 0px 12px 0px;
  }
  .d_content {
    text-align: center;
    font-size: 13px;
    font-weight: 400;
    color: #666666;
    padding-bottom: 20px;
  }
  .btn_list {
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-top: 0.5px solid #dedede;
    .disagree {
      width: 50%;

      font-size: 17px;
      font-weight: 400;
      color: #999999;
      text-align: center;
      position: relative;
      padding: 16px 0px;

      &::after {
        position: absolute;
        content: "";
        right: 0;
        top: 0px;
        width: 0.5px;
        height: 100%;
        background: #dedede;
      }
    }
    .agree {
      width: 50%;

      color: #333333;
      font-size: 17px;
      text-align: center;
      padding: 16px 0px;
    }
  }

  :deep(.van-divider) {
    margin-top: 12px;
    margin-bottom: 12px;
  }
  :deep(.van-popup) {
    width: 280px !important;
  }
}
</style>
