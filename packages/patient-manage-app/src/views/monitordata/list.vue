<template>
  <div class="list_box">
    <van-sticky>
      <appHeader
        :title="planName"
        :leftCustomize="true"
        @handelPage="backIndex"
        rightIcon="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAAAXNSR0IArs4c6QAAA1xJREFUaEPtmYGxTTEQhv9XASpABagAFaACVIAKUAEqQAWoABWgAlSACpjPnJ3ZOW+TbJJz7ps78zJzZ+69J9nst7vZbHJOdOTt5Mj11znAWXvw3AMrD1yRdFnSreV/fv9Yvn+V9FvS5y29toUHrku6L+muJBRuNSDeL58Prc6t5zMAWPmps3Zrrug53nkm6e3IYMaMAFyU9ELSg2BSwoNQ4fPaPb8nCU8BfTMY90nSk2VcF0svAEq8W4XKT0kvJb1ZYtwU+Os08fNgAMLtsaRrrg+hBQRy0q0HAItjeRSg/Vncj/JRKwH4vshk/AX3JyH1PEuQBcD1H53Qb4sVLcOMAjAOg7CofWgBAEizZQAIG5Q3y7PgcD8ur7WMB/x4QodsZo11A1i1tQBQGuWBoJH2iN9M6wVApofAQLdbC7sFgBtJlTTChlBqWd7gRgAYSwazxU12AqLYagBsSt/dyBsta6xmGQVgXiBsYQMASNhqAN6dxH2U92vGGQVApvc8ieJqLwCx/8sNQkAt40TyZwCYn/nMC0XvlzyAtW0nHbE+QDMAjGd/eLRY5tWS+U4ZqgRA+rqz9E6ls8AFswBkvi+L3GIYlQD85Jc6Mo/nmAVAFhmvGkYRgI9/ijOr7TO5f2sAso/t0GE2igB82bAVwKgRWIuW/R5GhV4LoLh4Eu7wIZTo3uwS1kctgHRRFUzPwuN4uVUbApgJIbIIG5IVgTMgGCMsIFsemAGYUTg9tpVGq9t4epYdO5YAfPz2FnE7qntadAnAb+MzC3l3mBIAhxYO77SzCiP2Ds4F1GLFM0itnPbbeLiJ7GhefxapFpM1gHRNvgOILyarIdw6UvrFPLMr9zD68OXqBm8MhRCT+nMBvw8RSv4kyEVX6d7pv1FaHqBP901Bj7mDvraD2+5bFZcBQIC/KTiUJ1J2yAJQz1Cb+7tMXMsCy16zpBTq7ZQFQG50BYjyBtI7N7dwFGjI5bDSe2mQXgNrxdZXgDy3lxZ4idu7yCsoigfJMuuXIcO7fY8HPAi7JPtEdNdv/bCoWZWFWSqruZ5H3sE84EGwJCB+bWRDCcUZ2/U+YC181ANrOfbSwkIjAkJhrGzvx4YsvhdAyer+LWXWM139tvJA16Rbdj4H2NKaI7KO3gP/AJEArjGaojNCAAAAAElFTkSuQmCC"
        @click-right="Tostatistics"
      />
      <!--  -->
      <div class="list_top">
        <div class="list_top_content">
          <div class="list_top_flex">
            <div class="list_top_left">
              <div class="line"></div>
              <div class="list_num">
                <div class="list_total">{{ planName }}</div>
                <div class="list_help_icon">
                  以下患者需要您及时跟进
                  <div @click="getplanmsg">
                    <img src="../../assets/images/list_help_icon.png" alt="" />
                  </div>
                </div>
              </div>
            </div>
            <div
              class="list_all"
              v-if="
                (active == 1 && listsum != 0) ||
                (active == 2 && handlelistsum != 0)
              "
              @click="showmodal(active == 1 ? 'showwarn' : 'showclear')"
            >
              {{ active == 1 ? "全部处理" : "全部清空" }}
            </div>
          </div>
          <div style="margin-top: 26px">
            <van-search
              placeholder="搜索"
              v-model="seachval"
              @update:model-value="seach"
              :maxlength="50"
            />
          </div>
          <!-- 处理 -->
          <div class="list_status">
            <div
              :class="active == 1 ? 'tab active' : 'tab'"
              @click="checkTab(1)"
            >
              {{ `未处理(${listsum})` }}
              <div v-if="active == 1" class="tab_line"></div>
            </div>
            <div
              :class="active == 2 ? 'tab active' : 'tab'"
              style="margin-left: 32px"
              @click="checkTab(2)"
            >
              {{ `已处理(${handlelistsum})` }}
              <div v-if="active == 2" class="tab_line"></div>
            </div>
          </div>
        </div>
      </div>
    </van-sticky>
    <div class="list_title">
      <p>姓名</p>
      <div class="list_time">
        {{ active == 1 ? "上传时间" : "处理时间" }}
        <div style="margin-left: 3px">
          <div class="list_item_img_top" @click="sortlist">
            <img
              class="list_time_img"
              src="../../assets/images/info_up.png"
              v-if="!isDown"
            />
            <img
              class="list_time_img"
              src="../../assets/images/info_up_check.png"
              v-else
            />
          </div>
          <div @click="sortlist">
            <img
              class="list_time_img"
              src="../../assets/images/info_down.png"
              v-if="isDown"
            />
            <img
              class="list_time_img"
              src="../../assets/images/info_down_check.png"
              v-else
            />
          </div>
        </div>
      </div>
    </div>
    <!-- list -->
    <div class="list_content">
      <!-- 列表 -->

      <div v-if="active == 1" class="listbox" :style="`height:${height}px`">
        <div class="list_item" v-for="item in list" :key="item.patientId">
          <div class="list_item_top" @click="jumpPatientInfo(item.patientId)">
            <div class="list_item_top_left">
              <div class="list_item_img">
                <img :src="item.patientAvatar" />
              </div>
              <div class="list_item_info">
                <p style="margin-top: 5px">{{ item.patientName }}</p>
                <div>所属医生：{{ item.doctorName }}</div>
              </div>
            </div>
            <div class="list_check" @click.stop="handlecheck(item.patientId)">
              <img src="../../assets/images/info_no_check.png" />
            </div>
          </div>
          <!-- 文本内容 -->
          <div class="list_item_content">
            <div
              class="list_item_list"
              v-for="(v, i) in item.valueAndTimeVo"
              :key="v.formWriteTime + i"
            >
              <p>
                <span class="fieldlabel"> {{ v.fieldLabel }}</span>
                ：<img src="../../assets/images/warning_icon.png" />
                <span v-text="v.realWriteValue" style="padding-left: 2px"></span
                ><span v-text="v.company"></span>
              </p>
              <p
                v-text="v.formWriteTime && v.formWriteTime.replace(/-/g, '.')"
              ></p>
            </div>
          </div>
          <!-- 联系他 -->
          <div class="list_contact" @click="jumpPatientChat(item.patientId)">
            <img src="../../assets/images/wx_icon.png" alt="" />
            联系TA
          </div>
        </div>
        <div class="more">
          <p v-if="more[1].loadmore && listsum != 0" @click="moredata">
            点击加载更多...
          </p>
          <p v-if="more[1].nomore && listsum != 0">没有更多了</p>
        </div>
        <div class="no_data" v-if="listsum == 0">
          <img src="../../assets/images/no_data.png" />
          <p>暂无数据</p>
        </div>
      </div>
      <!-- 列表 -->
      <div v-if="active == 2" class="listbox" :style="`height:${height}px`">
        <div class="list_item" v-for="item in handlelist" :key="item.patientId">
          <div class="list_item_top boder">
            <div
              class="list_item_top_left"
              @click="jumpPatientInfo(item.patientId)"
            >
              <div class="list_item_img">
                <img :src="item.patientAvatar" />
              </div>
              <div class="list_item_info-handle">
                <p style="margin-top: 5px">{{ item.patientName }}</p>
                <div>
                  <span style="color: #999999"
                    >所属医生：{{ item.doctorName }}</span
                  ><span style="color: #666666">{{
                    item.handleTime && item.handleTime.replace(/-/g, ".")
                  }}</span>
                </div>
              </div>
            </div>
          </div>
          <!-- 联系他 -->
          <div class="list_contact" @click="jumpPatientChat(item.patientId)">
            <img src="../../assets/images/wx_icon.png" alt="" />
            联系TA
          </div>
        </div>
        <div class="more">
          <p v-if="more[2].loadmore && handlelistsum != 0" @click="moredata">
            点击加载更多...
          </p>
          <p v-if="more[2].nomore && handlelistsum != 0">没有更多了</p>
        </div>
        <div class="no_data" v-if="handlelistsum == 0">
          <img src="../../assets/images/no_data.png" />
          <p>暂无数据</p>
        </div>
      </div>
    </div>
  </div>
  <Dialog.Component
    v-model:show="showTips"
    close-on-click-overlay
    close-on-popstate
    :show-confirm-button="false"
  >
    <div class="tipsbox">
      <div class="tipsbox-title">
        <p>监测说明</p>
        <img src="@/assets/images/close.png" alt="" @click="getplanmsg" />
      </div>
      <div class="tipsbox-content">
        <p>当患者提交的监测数据满足：</p>
        <p
          v-for="(item, index) in planDescription"
          :key="item.realWriteValueRightUnit + index"
        >
          <span style="color: red"
            >{{
              item?.minConditionValue
                ? `${item?.minConditionValue}${
                    item?.realWriteValueRightUnit
                      ? item?.realWriteValueRightUnit
                      : ""
                  }${formoption[item?.minConditionSymbol - 1]}`
                : ""
            }}
            <span style="color: #1890ff">{{ item?.fieldLabel }}</span>
            {{
              item?.maxConditionValue
                ? `${formoption[item?.maxConditionSymbol - 1]}${
                    item?.maxConditionValue
                  }${
                    item?.realWriteValueRightUnit
                      ? item?.realWriteValueRightUnit
                      : ""
                  }`
                : ""
            }}</span
          >
        </p>
        <p>
          将纳入[<span style="font-weight: bold">监测数据</span>]预警中进行管理
        </p>
      </div>
    </div>
  </Dialog.Component>
  <Dialog.Component
    v-model:show="model[active]"
    show-cancel-button
    @confirm="clearhandlelist(active == 1 ? 'handle' : 'clear')"
  >
    <div class="modalbox">
      是否全部{{ active == 1 ? "改为" : "清空" }}[<span>已处理</span>]{{
        active == 1 ? "状态" : "记录"
      }}
    </div>
  </Dialog.Component>
</template>
<script  lang="ts" setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import appHeader from "@/components/header/index.vue";
import patientmonitor from "@/api/patientmonitor";
import { Dialog, List, PullRefresh, Empty, Toast } from "vant";
const active = ref<number>(1); //选择TAB
const isDown = ref<boolean>(false); //上传时间排序
const showTips = ref<boolean>(false); //监测说明弹窗
const showwarn = ref<boolean>(false); //全部处理弹窗
const showclear = ref<boolean>(false); //全部清除弹窗
const list = ref<Array<any>>([]); //未处理列表
const listsum = ref<number>(0); //未处理总数
const handlelist = ref<Array<any>>([]); //已处理列表
const handlelistsum = ref<number>(0); //已处理总数
const more = ref<any>({
  1: {
    loadmore: false,
    nomore: false,
  },
  2: {
    loadmore: false,
    nomore: false,
  },
});
const planId = ref<any>(""); //计划id
const planName = ref<any>(""); //计划名称
const seachval = ref<string>("");
const planDescription = ref<any>([]); //监测说明数据
const formoption = ref(["<", "≤", "="]); //符号
const model = ref<any>({
  1: showwarn,
  2: showclear,
}); //v-model数据
const router = useRouter();
const route = useRoute();
const height = ref<number>(0);
const current = ref<number>(1);
const param = ref<any>({
  model: {
    planId: planId,
    sort: 1,
  },
  current: 1,
  order: "descending",
  size: 10,
}); //请求默认数据
//跳转统计页
const Tostatistics = () => {
  router.push({
    path: `/monitordata/statistics`,
    query: {
      id: planId.value,
      planName: planName.value,
    },
  });
};
const FuncGetData: any = {
  1: (data: any, type: string) => {
    patientmonitor
      .dataUnprocessedList({ ...param.value, ...data })
      .then((res) => {
        if (res.data) {
          list.value =
            type == "more"
              ? [...list.value, ...res.data.monitorProcessedVo]
              : res.data.monitorProcessedVo;
          listsum.value = res.data.total;
          more.value[1].loadmore = current.value * 10 < res.data.total;
          more.value[1].nomore = current.value * 10 >= res.data.total;
        }
      });
  },
  2: (data: any, type: string) => {
    patientmonitor
      .dataProcessedList({ ...param.value, sort: "updateTime", ...data })
      .then((res) => {
        if (res.data) {
          handlelist.value =
            type == "more"
              ? [...handlelist.value, ...res.data.monitorProcessedVo]
              : res.data.monitorProcessedVo;
          handlelistsum.value = res.data.total;
          more.value[2].loadmore = current.value * 10 < res.data.total;
          more.value[2].nomore = current.value * 10 >= res.data.total;
        }
      });
  },
};
const moredata = () => {
  current.value = current.value + 1;
  FuncGetData[active.value]({ current: current.value }, "more");
};
const seach = (val: string) => {
  seachval.value != "" &&
    FuncGetData[active.value]({
      model: { patientName: seachval.value, planId: planId.value },
    });
};
const backIndex = () => {
  console.log('backIndex')
  router.replace("/monitordata/index")
}
//排序
const sortlist = () => {
  isDown.value = !isDown.value;
  let sort = {
    model: {
      planId: planId.value,
      sort: isDown.value ? 2 : 1,
    },
  };
  FuncGetData[active.value](sort);
};
//选择tab
const checkTab = (tab: number) => {
  active.value = tab;
  current.value = 1;
  seachval.value = "";
  isDown.value = false;
  more.value[active.value].loadmore = false;
  more.value[active.value].nomore = false;
  FuncGetData[active.value]();
};

//单个处理未处理数据
const handlecheck = async (id: string) => {
  let res = await patientmonitor.unprocessedEliminate({
    patientId: id,
    planId: planId.value,
  });
  res.data.data && Toast("已处理");
  const timer = setTimeout(() => {
    FuncGetData[1]();
    FuncGetData[2]();
    clearTimeout(timer);
  }, 1000);
};
//清除已处理数据
const clearhandlelist = async (type: string) => {
  type == "clear" &&
    patientmonitor.processedEliminate(planId.value).then((res: any) => {
      if (res.isSuccess) {
        Toast("已清空");
        const timer = setTimeout(() => {
          FuncGetData[active.value]();
          clearTimeout(timer);
        }, 1000);
      }
    });
  type == "handle" &&
    patientmonitor
      .unprocessedEliminate({ planId: planId.value })
      .then((res: any) => {
        if (res.isSuccess) {
          Toast("处理成功");
          const timer = setTimeout(() => {
            FuncGetData[1]();
            FuncGetData[2]();
            clearTimeout(timer);
          }, 1000);
        }
      });
};

//获取监测计划信息
const getplanmsg = () => {
  showTips.value = !showTips.value;
  if (showTips.value) {
    patientmonitor.planDescription(planId.value).then((res: any) => {
      if (res.isSuccess) {
        planDescription.value = res.data;
      }
    });
  }
};
onMounted(() => {
  planId.value = route.query.planId;
  planName.value = route.query.planName;
  FuncGetData[1]();
  FuncGetData[2]();
  height.value = window.innerHeight - 320;
});
//显示弹框
const showmodal = (type: string) => {
  type == "showwarn"
    ? (showwarn.value = !showwarn.value)
    : (showclear.value = !showclear.value);
};
//APP跳转到患者详情界面
const jumpPatientInfo = (item: string) => {
  const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
  if (caringCurrentDevice) {
    let history = { path: '/monitordata/list', query: {planId: planId.value, planName: planName.value} }
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageSeePatient', data: item}, '*')
  } else if ((window as any).android) {
    (window as any).android.jumpPatientInfo(item);
  } else if (window.parent) {
    let history = { path: '/monitordata/list', query: {planId: planId.value, planName: planName.value} }
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageSeePatient', data: item}, '*')
  }
};

const jumpPatientChat = (item: string) => {
  const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
  if (caringCurrentDevice) {
    let history = { path: '/monitordata/list', query: {planId: planId.value, planName: planName.value} }
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageChatPatient', data: item}, '*')
  } else if ((window as any).android) {
    (window as any).android.jumpPatientChat(item);
  } else if (window.parent) {
    let history = { path: '/monitordata/list', query: {planId: planId.value, planName: planName.value} }
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageChatPatient', data: item}, '*')
  }
};
</script>

<style lang="less" scoped>
.list_box {
  background: rgba(245, 245, 245, 1);
  position: relative;
  height: 100vh;

  .list_top {
    height: 212px;
    background: linear-gradient(90deg, #3f86ff 0%, #6ea8ff 100%);
    box-shadow: 0px 2px 2px 1px rgba(51, 126, 255, 0.08);
    .list_top_content {
      padding: 30px 20px 7px 20px;

      .list_top_flex {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .list_top_left {
          display: flex;
          justify-content: center;
        }

        .line {
          width: 4px;
          height: 50px;
          background: rgba(255, 255, 255, 0.4);
          border-radius: 52px 52px 52px 52px;
        }

        .list_num {
          margin-left: 13px;

          .list_total {
            max-width: 220px;
            font-size: 27px;
            font-weight: bold;
            color: #ffffff;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .list_help {
            margin-top: 10px;
            font-size: 13px;
            font-weight: 400;
            color: #ffffff;
            line-height: 16px;
          }

          .list_help_icon {
            margin-top: 10px;
            font-size: 13px;
            font-weight: 400;
            color: #ffffff;
            line-height: 16px;
            display: flex;

            img {
              width: 15px;
              height: 15px;
              margin-left: 3px;
            }
          }
        }

        .list_all {
          /*position: absolute;*/
          /*left: 264px;*/
          /*top: 102px;*/
          width: 88px;
          height: 28px;
          background: rgba(0, 132, 255, 0);
          border-radius: 52px 52px 52px 52px;
          opacity: 1;
          border: 1px solid #ffffff;
          font-size: 12px;
          font-weight: 400;
          color: #ffffff;
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }
    }

    .list_status {
      margin-top: 28px;
      display: flex;
      justify-content: center;
      font-size: 17px;

      .tab {
        font-size: 16px;
        font-weight: 400;
        color: #ffffff;
        line-height: 0px;
        opacity: 0.7;
      }

      .active {
        font-size: 17px;
        font-weight: 500;
        opacity: 1;
      }

      .tab_line {
        width: 23px;
        height: 6px;
        background: #ffffff;
        border-radius: 4px 4px 4px 4px;
        // margin-top: 10px;
        margin: 15px auto 0px auto;
        text-align: center;
      }
    }
  }

  .list_title {
    position: sticky;
    top: 0;
    margin: 16px 13px -15px 0;
    padding-bottom: 16px;
    // height: 30px;
    z-index: 9;
    font-size: 13px;
    font-weight: 400;
    color: #666666;
    background-color: #f5f5f5;
    // line-height: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    p {
      padding: 0px 18px;
    }

    .list_time {
      display: flex;
      align-items: center;

      .list_time_img {
        width: 6px;
        height: 6px;
      }

      .list_item_img_top {
        margin-top: -3px;
        height: 7px;
      }
    }
  }

  .list_content {
    // padding-bottom: ;
    padding: 13px;
    width: 100%;
    position: fixed;
    height: 60%;
    overflow-y: scroll;
    .listbox {
      overflow: auto;
      div:first-child {
        margin-top: 2px;
      }
      .more {
        font-size: 14px;
        color: #666666;
        text-align: center;
        p {
          padding: 5px 22px 10px 0;
        }
      }
      .list_item {
        background: #ffffff;
        border-radius: 4px 4px 4px 4px;
        padding: 13px;
        width: 90%;
        margin: 20px auto;

        .list_item_top {
          display: flex;
          align-items: center;
          justify-content: space-between;
          height: 55px;
        }

        .list_item_img {
          width: 42px;
          height: 42px;
          border-radius: 50%;
          border: 0.5px solid #f5f5f5;
          img {
            width: 42px;
            height: 42px;
            border-radius: 50%;
          }
        }

        .list_item_info {
          margin-left: 8px;

          p {
            font-size: 16px;
            font-weight: 500;
            color: #333333;
            line-height: 19px;
          }

          div {
            display: flex;
            align-items: center;
            justify-content: space-between;
            font-size: 13px;
            font-weight: 400;
            color: #999999;
            line-height: 25px;
            width: 240px;
            // margin-top: 5px;
          }
        }
        .list_item_info-handle {
          margin-left: 8px;

          p {
            font-size: 16px;
            font-weight: 500;
            color: #333333;
            line-height: 19px;
          }

          div {
            display: flex;
            align-items: center;
            justify-content: space-between;
            font-size: 13px;
            font-weight: 400;
            color: #999999;
            line-height: 25px;
            width: 274px;
            // margin-top: 5px;
          }
        }
        .list_item_top_left {
          display: flex;
          align-items: center;
        }
        .boder {
          border-bottom: 0.5px solid #ebebeb;
          padding-bottom: 13px;
        }
        .list_item_content {
          background: rgba(245, 245, 245, 0.6);
          border-radius: 4px 4px 4px 4px;
          padding: 18px 13px;
          margin-top: 13px;
          div:first-child {
            padding-top: 1px;
          }
          .list_item_list {
            display: flex;
            justify-content: space-between;
            padding-top: 15px;
            p {
              display: flex;
              font-size: 13px;
              color: #666666;

              span {
                font-size: 13px;
                color: #fb403e;
                margin-left: 3px;
              }
            }

            img {
              width: 13px;
              height: 13px;
            }
            .fieldlabel {
              color: #666666;
              max-width: 100px;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            }
          }
        }
      }
    }
    .list_contact {
      width: 105px;
      height: 28px;
      background: #1890ff;
      border-radius: 42px 42px 42px 42px;
      margin: 14px auto 0px auto;
      font-size: 13px;
      color: #ffffff;
      display: flex;
      align-items: center;
      justify-content: center;

      img {
        width: 17px;
        height: 14px;
        margin-right: 7px;
      }
    }

    .list_check {
      margin-top: 3px;
      img {
        width: 33px;
        height: 33px;
      }
    }
  }

  :deep(.van-search) {
    // margin: 0px 20px;
    height: 44px;
    border-radius: 22px;
  }

  :deep(.van-search__content) {
    background: #ffffff;

    input {
      font-size: 17px;
    }
  }
}

.modalbox {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;

  span {
    font-weight: bold;
  }
}

.tipsbox {
  min-height: 195px;

  .tipsbox-title {
    margin: auto;
    width: 90%;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 50px;
    border-bottom: 1px solid #bababa;
    position: relative;

    img {
      width: 16px;
      height: 16px;
      position: absolute;
      right: 0;
    }
  }

  .tipsbox-content {
    width: 90%;
    margin: 30px auto 0px;
    padding-bottom: 30px;
    p {
      margin-top: 15px;
    }
  }
}

.no_data {
  margin: 125px 21px 0 0 !important;
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
