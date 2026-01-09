<template>
  <van-popup
    v-model:show="isOpen"
    :style="{ width: '78%', background: 'none' }"
  >
    <div class="popup_content">
      <div>
        <van-icon
          name="cross"
          color="#B8B8B8"
          size="20"
          class="popup_content__closeIcon"
          @click="isOpen = false"
        />
      </div>
      <div class="popup_content__title">群发完善健康档案提醒?</div>
      <div class="popup_content__line"></div>
      <van-checkbox-group v-model="checked" ref="checkboxGroup">
        <div
          class="popup_content__item"
          v-for="(item, index) in list"
          :key="index"
        >
          <span>{{ item.name }}</span>
          <van-checkbox
            v-if="index"
            :name="index"
            class="popup_content__checkbox"
          ></van-checkbox>
          <van-checkbox
            v-else
            :name="index"
            @click="toggleAll"
            class="popup_content__checkbox"
          ></van-checkbox>
        </div>
      </van-checkbox-group>
      <div class="popup_content__messageCount">
        将有<span>{{ numCount }}人</span>收到通知
      </div>
      <div class="popup_content__button">
        <van-button type="primary" round @click="handleClick" style="width: 60%;"
          >确定群发</van-button
        >
      </div>
    </div>
  </van-popup>
</template>
<script setup lang="ts">
import { ref, computed } from "vue";
import { Toast } from "vant";
import api from "@/api/commapi";
const isOpen = ref(false);
const isCheckdAll = ref(false);
const checked = ref();
const list = ref([] as any);
const checkboxGroup = ref();

const show = () => {
  isOpen.value = true;
  checked.value = [];
  checkboxGroup.value = null;
  isCheckdAll.value = false;
  getData();
};
const getData = () => {
  let nursingId = localStorage.getItem("caring-userId");
  api
    .getNursingIntervalStatistics({}, (url: string) =>
      url.replace("{nursingId}", nursingId as any)
    )
    .then((res) => {
      list.value = res.data || [];
    });
};

const sendNotification = () => {
  let nursingId = localStorage.getItem("caring-userId");
  let intervalId = selectIds.value;
  let params = {
    intervalId: intervalId,
    nursingId: nursingId,
  };
  api.sendNotification(params).then((e: any) => {
    if (e.isSuccess) {
      Toast("群发通知成功！");
      isOpen.value = false
    } else {
      Toast("群发通知失败！");
    }
  });
};

const handleClick = () => {
  if (!numCount.value) {
    Toast("沒有可通知的人数");
  } else {
    sendNotification();
  }
};

const toggleAll = () => {
  isCheckdAll.value = !isCheckdAll.value;
  checkboxGroup.value.toggleAll(isCheckdAll.value);
};
const numCount = computed(() => {
  let count = 0;
  if (checked.value) {
    checked.value.map((index: any) => {
      if (index) {
        let item = list.value[index];
        count += item.patientNumber;
      }
    });
  }
  return count;
});

const selectIds = computed(() => {
  let ids: Array<string> = [];
  if (checked.value) {
    checked.value.map((index: any) => {
      if (index) {
        let item = list.value[index];
        ids.push(item.id);
      }
    });
  }
  return ids;
});
defineExpose({
  show,
});
</script>


<style lang="less" scoped>
.popup_content {
  background: #ffffff;
  width: 100%;
  height: 100%;
  box-shadow: 0px 1px 2px 1px rgba(0, 0, 0, 0.04);
  border-radius: 6px 6px 6px 6px;
  &__checkbox {
    margin-left: auto;
    margin-right: 12px;
  }
  &__closeIcon {
    float: right;
    margin: 12px;
  }
  &__title {
    font-size: 18px;
    font-family: Source Han Sans CN-Medium, Source Han Sans CN;
    font-weight: 500;
    color: #333333;
    line-height: 32px;
    padding-top: 43px;
    margin: 0 auto;
    width: fit-content;
  }
  &__line {
    width: 40px;
    height: 4px;
    background: #ffbe8b;
    border-radius: 4px 4px 4px 4px;
    margin: 0 auto;
  }
  &__item {
    height: 44px;
    background: #f5f5f5;
    border-radius: 3px 3px 3px 3px;
    margin: 8px 16px;
    font-size: 14px;
    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
    font-weight: 400;
    color: #666666;
    display: flex;
    align-items: center;
    span {
      margin-left: 12px;
    }
  }
  &__messageCount {
    font-size: 12px;
    font-family: Source Han Sans CN-Regular, Source Han Sans CN;
    font-weight: 400;
    color: #999999;
    text-align: center;
    margin-top: 20px;
    span {
      color: #337eff;
    }
  }
  &__button {
    text-align: center;
    margin-top: 25px;
    padding-bottom: 25px;
  }
}
</style>
