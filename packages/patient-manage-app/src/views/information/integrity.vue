<template>
  <div class="information_box">
    <van-sticky>
      <appHeader
        :title="'信息完整度'"
        rightIcon="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAAAXNSR0IArs4c6QAAA1xJREFUaEPtmYGxTTEQhv9XASpABagAFaACVIAKUAEqQAWoABWgAlSACpjPnJ3ZOW+TbJJz7ps78zJzZ+69J9nst7vZbHJOdOTt5Mj11znAWXvw3AMrD1yRdFnSreV/fv9Yvn+V9FvS5y29toUHrku6L+muJBRuNSDeL58Prc6t5zMAWPmps3Zrrug53nkm6e3IYMaMAFyU9ELSg2BSwoNQ4fPaPb8nCU8BfTMY90nSk2VcF0svAEq8W4XKT0kvJb1ZYtwU+Os08fNgAMLtsaRrrg+hBQRy0q0HAItjeRSg/Vncj/JRKwH4vshk/AX3JyH1PEuQBcD1H53Qb4sVLcOMAjAOg7CofWgBAEizZQAIG5Q3y7PgcD8ur7WMB/x4QodsZo11A1i1tQBQGuWBoJH2iN9M6wVApofAQLdbC7sFgBtJlTTChlBqWd7gRgAYSwazxU12AqLYagBsSt/dyBsta6xmGQVgXiBsYQMASNhqAN6dxH2U92vGGQVApvc8ieJqLwCx/8sNQkAt40TyZwCYn/nMC0XvlzyAtW0nHbE+QDMAjGd/eLRY5tWS+U4ZqgRA+rqz9E6ls8AFswBkvi+L3GIYlQD85Jc6Mo/nmAVAFhmvGkYRgI9/ijOr7TO5f2sAso/t0GE2igB82bAVwKgRWIuW/R5GhV4LoLh4Eu7wIZTo3uwS1kctgHRRFUzPwuN4uVUbApgJIbIIG5IVgTMgGCMsIFsemAGYUTg9tpVGq9t4epYdO5YAfPz2FnE7qntadAnAb+MzC3l3mBIAhxYO77SzCiP2Ds4F1GLFM0itnPbbeLiJ7GhefxapFpM1gHRNvgOILyarIdw6UvrFPLMr9zD68OXqBm8MhRCT+nMBvw8RSv4kyEVX6d7pv1FaHqBP901Bj7mDvraD2+5bFZcBQIC/KTiUJ1J2yAJQz1Cb+7tMXMsCy16zpBTq7ZQFQG50BYjyBtI7N7dwFGjI5bDSe2mQXgNrxdZXgDy3lxZ4idu7yCsoigfJMuuXIcO7fY8HPAi7JPtEdNdv/bCoWZWFWSqruZ5H3sE84EGwJCB+bWRDCcUZ2/U+YC181ANrOfbSwkIjAkJhrGzvx4YsvhdAyer+LWXWM139tvJA16Rbdj4H2NKaI7KO3gP/AJEArjGaojNCAAAAAElFTkSuQmCC"
        @click-right="handleRightClick"
      />
      <div class="i_top">
        <div class="i_top_search">
          <div class="i_t_s_left" @click="handelInfo">
            <span>完整度：{{ title }}</span>
            <img src="../../assets/images/up.png" v-if="show" />
            <img src="../../assets/images/top.png" v-else />
          </div>
          <div class="i_t_s_right">
            <img src="../../assets/images/doctor_icon_infor.png" alt="" />
            <span> {{ total }}</span>
          </div>
        </div>
        <van-overlay :show="show" @click="show = false">
          <div class="in_popup">
            <div class="popup" v-for="(item, index) in actions" :key="index">
              <div
                :class="item.name === title ? 'p_item_check' : 'p_item'"
                @click="handelItem(item)"
              >
                {{ item.name }}
                <img
                  v-if="item.name === title"
                  src="../../assets/images/info_select.png"
                />
              </div>

              <div class="line"></div>
            </div>
          </div>
        </van-overlay>
        <van-search
          v-model="name"
          placeholder="搜索"
          @update:model-value="handleSearch"
          :maxlength="50"
        />
      </div>
    </van-sticky>
    <div class="h_list" v-if="list.length > 0">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="handelList"
      >
        <div v-for="(item, index) in list" :key="index">
          <div class="h_item" @click="handelGoDetail(item)">
            <div class="h_i_left">
              <img :src="item.avatar ? item.avatar : default_avatar" />
              <div>
                <p class="h_title">{{ item.patientName }}</p>
                <div class="h_doctor">所属医生：{{ item.doctorName }}</div>
              </div>
            </div>
            <div v-if="item.completion == 100" class="h_right_all">
              <p>{{ item.completion }}%</p>
            </div>
            <div v-else class="h_right">
              <p>{{ item.completion }}%</p>
            </div>
          </div>
        </div>
      </van-list>
    </div>
    <div class="no_data" v-else>
      <img src="../../assets/images/no_data.png" />
      <p>暂无数据</p>
    </div>
    <div class="i_bottom" @click="handelSendNotification">
      <img src="../../assets/images/remind.png" />
      <div>群发提醒</div>
    </div>
  </div>
  <massPopup ref="popup"></massPopup>
</template>
<script lang="ts" setup>
import appHeader from "@/components/header/index.vue";
import massPopup from "@/components/massPopup/mass-popup.vue";
import default_avatar from "@/assets/images/default_ avatar.png";

import useIntegrity from "./useIntegrity";
const {
  list,
  show,
  name,
  title,
  handelInfo,
  actions,
  onCancel,
  handelGoDetail,
  handelItem,
  handleSearch,
  handelList,
  loading,
  finished,
  total,
  handleRightClick,
  popup,
  handelSendNotification,
} = useIntegrity();
</script>
<style lang="less" scoped>
.information_box {
  background: #f5f5f5;
  min-height: 100vh;
  position: relative;
  .i_top {
    height: 136px;
    background: linear-gradient(90deg, #3f86ff 0%, #6ea8ff 100%);
    box-shadow: 0px 2px 2px rgba(51, 126, 255, 0.08);
    .i_top_search {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 27px 22px 22px 22px;
      .i_t_s_left {
        display: flex;
        justify-content: space-between;
        align-items: center;
        img {
          width: 21px;
          height: 21px;
          padding-left: 6px;
        }
        span {
          font-size: 18px;
          font-weight: 500;
          line-height: 16px;
          color: #ffffff;
        }
      }
      .i_t_s_right {
        display: flex;
        justify-content: space-between;
        align-items: center;
        img {
          width: 21px;
          height: 21px;
          padding-right: 5px;
        }
        span {
          font-size: 18px;
          font-weight: 400;
          line-height: 16px;
          color: #ffffff;
        }
      }
    }
  }
  .h_list {
    padding: 5px 12px 12px;
    .h_item {
      margin-top: 13px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px;
      background: #ffffff;
      border-radius: 4px;
      img {
        width: 42px;
        height: 42px;
        border-radius: 50%;
        margin-right: 8px;
      }
      .h_title {
        font-size: 16px;

        font-weight: 500;

        line-height: 19px;
        color: #333333;
      }
      .h_doctor {
        font-size: 13px;
        font-weight: 400;
        line-height: 25px;
        color: #999999;
      }
      .h_right {
        font-size: 17px;
        font-weight: 500;
        line-height: 28px;
        color: #ff7777;
      }
      .h_right_all {
        font-size: 17px;
        font-weight: 500;
        line-height: 28px;
        color: #70c37a;
      }
      .h_i_left {
        display: flex;
      }
    }
  }
  .i_bottom {
    width: 81px;
    height: 81px;
    background: linear-gradient(90deg, #3f86ff 0%, #6ea8ff 100%);
    box-shadow: 0px 2px 8px rgba(51, 126, 255, 0.32);
    opacity: 1;
    border-radius: 104px;
    font-size: 11px;
    font-weight: 500;
    line-height: 19px;
    color: #ffffff;
    margin: 0 auto;
    text-align: center;
    position: fixed;
    bottom: 0;
    transform: translate(-50%, -50%); /*50%为自身尺寸的一半*/
    left: 50%;
    img {
      width: 42px;
      height: 42px;
      margin: 6px auto 0px auto;
    }
    div {
      padding: 0px;
      margin-top: -3px;
    }
  }
  .p_item {
    padding: 16px 24px;
    font-size: 16px;
    font-weight: 400;
    line-height: 23px;
    color: #999999;
  }
  .p_item_check {
    padding: 16px 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    font-weight: 500;
    line-height: 23px;
    color: #337eff;
    img {
      width: 18px;
      height: 13px;
    }
  }
  .line {
    width: 100%;
    background: #ebebeb;
    height: 1px;
    padding: 0px 13px;
  }
  .in_popup {
    background: #ffffff;
    z-index: 11;
    position: absolute;
    width: 100%;
    top: 115px;
    .popup {
    }
  }
}
:deep(.van-search) {
  // padding: 0px;
  margin: 0px 20px;
  height: 44px;
  border-radius: 22px;
}
:deep(.van-search__content) {
  // padding: 20px;
  background: #ffffff;
  input {
    font-size: 17px;
  }
}
.no_data {
  margin-top: 125px;
  margin: 125px auto 0px auto;
  text-align: center;
  img {
    width: 146px;
    height: 146px;
  }
  p {
    font-size: 15px;
    line-height: 19px;
    color: #999999;
    margin-top: 13px;
    text-align: center;
  }
}
</style>
