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
                    <img src="../../assets/images/list_help_icon.png" alt=""/>
                  </div>
                </div>
              </div>
            </div>
            <div
                class="list_all"
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
      <!-- 未列表 -->
      <div v-if="active == 1" class="listbox" :style="{height: height + 'px'}">
        <van-list
            v-model="loading"
            :finished="finished"
            @load="onLoad"
        >
          <div class="list_item" v-for="item in list" :key="item.patientId">
            <div class="list_item_top" @click="jumpPatientInfo(item.patientId)">
              <div class="list_item_top_left">
                <div class="list_item_img">
                  <img :src="item.patientAvatar"/>
                </div>
                <div class="list_item_info">
                  <p style="margin-top: 5px">{{ item.patientName }}</p>
                  <div>所属医生：{{ item.doctorName }}</div>
                </div>
              </div>
              <div class="list_check" @click.stop="handlecheck(item.patientId)">
                <img src="../../assets/images/info_no_check.png"/>
              </div>
            </div>
            <!-- 文本内容 -->
            <div
                class="list_item_content"
                v-for="(v, i) in item.formResultDto"
                :key="i">
              <div class="list_item_list" @click="jumpForm(v.formResultId, item.patientId)">
                <div style="width: 100%">
                  <div style="display: flex; justify-content: space-between; margin-top: 5px">
                    <div class="black_title" style="font-weight: bolder">
                      {{ v.fieldInfos[0].formFieldName + ":" }}
                    </div>
                    <div style="color: #666; font-size: 12px">{{getTime(v.createTime)}}</div>
                  </div>
                  <div style="display: flex; align-items: center; width: 100%">
                    <div style="flex: 1; overflow: hidden; width: 90%;">
                      <!-- 一级选项 -->
                      <div v-for="(itemFild, keyFild) in v.fieldInfos" :key="keyFild" style="margin-bottom: 10px">
                        <div v-if="keyFild !== 0" class="black_title" >
                          {{ itemFild.formFieldName }}
                        </div>
                        <div class="red_value" :style="{marginTop: keyFild !== 0 ? '10px' : '3px'}">{{ '异常选项：' +  getFiledContent(itemFild.optionInfos) }}</div>

                      </div>
                    </div>
                    <van-icon name="arrow" size="18px" color="#B8B8B8"/>
                  </div>
                </div>
              </div>
            </div>
            <!-- 联系他 -->
            <div class="list_contact" @click="jumpPatientChat(item.patientId)">
              <img src="../../assets/images/wx_icon.png" alt=""/>
              联系TA
            </div>
          </div>

          <template v-slot:finished>
            <div v-if="list.length > 0">没有更多了</div>
          </template>
        </van-list>

        <div class="no_data" v-if="list.length == 0" :style="{height: height + 'px'}">
          <van-empty description="空空如也~" :image-size="[160, 90]">
            <template v-slot:image>
              <img src="@/assets/images/abnormal_no_data.png" alt=""/>
            </template>
          </van-empty>
        </div>
      </div>
      <!-- 已列表 -->
      <div v-if="active == 2" class="listbox" :style="{height: height + 'px'}">
        <van-list
            v-model="loading"
            :finished="finished"
            @load="onLoad"
        >
          <div class="list_item" v-for="item in handlelist" :key="item.patientId">
            <div class="list_item_top" @click="jumpPatientInfo(item.patientId)">
              <div class="list_item_top_left">
                <div class="list_item_img">
                  <img :src="item.patientAvatar"/>
                </div>
                <div class="list_item_info">
                  <p style="margin-top: 5px">{{ item.patientName }}</p>
                  <div>所属医生：{{ item.doctorName }}</div>
                </div>
              </div>
              <div style="font-size: 14px; color: #666">
                <div> {{ getTime(item.handleTime) }}</div>
              </div>
            </div>
            <!-- 文本内容 -->
            <div
                class="list_item_content"
                v-for="(v, i) in item.formResultDto"
                :key="i">
              <div class="list_item_list" @click="jumpForm(v.formResultId, item.patientId)">
                <div style="width: 100%">
                  <div style="display: flex; justify-content: space-between; margin-top: 5px">
                    <div class="black_title" style="font-weight: bolder">
                      {{ v.fieldInfos[0].formFieldName + ":" }}
                    </div>
                    <div style="color: #666; font-size: 12px">{{getTime(v.createTime)}}</div>
                  </div>
                  <div style="display: flex; align-items: center; width: 100%">
                    <div style="flex: 1; overflow: hidden; width: 90%;">
                      <!-- 一级选项 -->
                      <div v-for="(itemFild, keyFild) in v.fieldInfos" :key="keyFild" style="margin-bottom: 10px">
                        <div v-if="keyFild !== 0" class="black_title" >
                          {{ itemFild.formFieldName }}
                        </div>
                        <div class="red_value" style="color: #1890FF" :style="{marginTop: keyFild !== 0 ? '10px' : '3px'}">{{ '异常选项：' + getFiledContent(itemFild.optionInfos) }}</div>

                      </div>
                    </div>
                    <van-icon name="arrow" size="18px" color="#B8B8B8"/>
                  </div>
                </div>
              </div>
            </div>
            <!-- 联系他 -->
            <div class="list_contact" @click="jumpPatientChat(item.patientId)">
              <img src="../../assets/images/wx_icon.png" alt=""/>
              联系TA
            </div>
          </div>
          <template v-slot:finished>
            <div v-if="handlelist.length > 0">没有更多了</div>
          </template>
        </van-list>
        <div class="no_data" v-if="handlelist.length == 0" :style="{height: height + 'px'}">
          <van-empty description="空空如也~" :image-size="[160, 90]">
            <template v-slot:image>
              <img src="@/assets/images/abnormal_no_data.png" alt=""/>
            </template>
          </van-empty>
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
        <p>说明</p>
        <img src="@/assets/images/close.png" alt="" @click="getplanmsg"/>
      </div>
      <div class="tipsbox-content">
        <p>以下患者所填选项需要您重点关注</p>
        <p>请与患者联系并及时跟进</p>
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
<script lang="ts" setup>
import {ref, onMounted, onUnmounted} from "vue";
import {useRouter, useRoute} from "vue-router";
import appHeader from "@/components/header/index.vue";
import patientmonitor from "@/api/patientmonitor";
import abnormalApi from "@/api/abnormal";
import {Dialog, List, PullRefresh, Empty, Toast, Cell} from "vant";

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
const formId = ref<any>(""); //表单id
const planName = ref<any>(""); //计划名称
const planType = ref<any>(""); //计划名称
const seachval = ref<string>("");
const model = ref<any>({
  1: showwarn,
  2: showclear,
}); //v-model数据
const router = useRouter();
const route = useRoute();
const height = ref<number>(0);
const loading = ref<boolean>(true);
const queryListStatus = ref<boolean>(false);
const finished = ref<boolean>(false);
const param = ref<any>({
  model: {
    planId: planId,
    nursingId: localStorage.getItem('caring-userId'),
    patientName: '',
    handleStatus: active.value === 1 ? 0 : 1,
  },
  current: 1,
  order: "descending",
  size: 5,
  sort: 'pushTime'
}); //请求默认数据
import moment from "moment";
//跳转统计页
const Tostatistics = () => {
  router.push({
    path: `/abnormal/statistics`,
    query: {
      formId: formId.value,
      planName: planName.value,
    },
  });
};
// list 加载数据
const onLoad = () => {
  getDataList();
}
const backIndex = () =>{
  router.replace("/abnormal/index")
}
// 获取总数
const FuncGetData = () => {
  abnormalApi.getCountHandleNumber(planId.value)
      .then(res => {
        if (res.data.noHandleNumber) {
          listsum.value = res.data.noHandleNumber;
        } else {
          listsum.value = 0
        }
        if (res.data.handleNumber) {
          handlelistsum.value = res.data.handleNumber;
        } else {
          handlelistsum.value = 0
        }
      })
}

// 查询未处理记录、已处理记录
const getDataList = () => {
  if (queryListStatus.value === true) {
    return
  }
  queryListStatus.value = true;
  abnormalApi.getDataList(param.value)
      .then(res => {
        if (active.value === 1) {
          // 未处理记录
          if (param.value.current === 1) {
            list.value = [...res.data.records]
          } else {
            list.value = [...list.value, ...res.data.records]
          }
          if (list.value.length === res.data.total) {
            finished.value = true
          } else {
            finished.value = false
          }
          loading.value = false;
          param.value.current = param.value.current + 1
        } else {
          // 已处理记录
          if (param.value.current === 1) {
            handlelist.value = [...res.data.records]
          } else {
            handlelist.value = [...handlelist.value, ...res.data.records]
          }
          if (handlelist.value.length === res.data.total) {
            finished.value = true
          }
          loading.value = false;
          param.value.current = param.value.current + 1
        }
        queryListStatus.value = false;
      }).catch(res => {
          queryListStatus.value = false;
      })
}

//选择tab
const checkTab = (tab: number) => {
  active.value = tab;
  param.value.current = 1
  param.value.model.handleStatus = active.value === 1 ? 0 : 1
  param.value.sort = active.value === 1 ? 'pushTime' : 'handleTime'
  isDown.value = false
  param.value.order = isDown.value ? 'ascending' : 'descending'
  getDataList()
};

//排序
const sortlist = () => {
  isDown.value = !isDown.value;
  param.value.current = 1
  param.value.order = isDown.value ? 'ascending' : 'descending'
  getDataList()
};

// 搜索
const seach = (val: string) => {
  param.value.model.patientName = seachval.value;
  param.value.current = 1;
  getDataList();
};

// 获取计划时间
const getTime = (time: string) => {
  return moment(time).format("yyyy.MM.DD HH:mm")
}

// 获取字段选择内容
const getFiledContent = (optionInfos) => {
  let content = ''
  optionInfos.forEach(item => {
    if (content === '') {
      content = item.fieldOptionName
    } else {
      content = content + '、' + item.fieldOptionName
    }
  })
  return content
}

// 判断二级页面是否显示
const isTwoShow = (optionInfos) => {
  let isShow = false
  if (optionInfos.length > 0) {
    optionInfos.forEach(item => {
      if (item.fieldInfos) {
        isShow = true
      }
    })
  }
  return isShow
}

//全部处理 或 清空数据
const clearhandlelist = async (type: string) => {
  type == "clear" &&
  abnormalApi.allClearData(formId.value)
      .then(res => {
        Toast("清空成功");
        FuncGetData()
        param.value.current = 1;
        getDataList();
      })
  type == "handle" &&
  abnormalApi.allHandleResult(formId.value)
      .then(res => {
        Toast("处理成功");
        FuncGetData()
        param.value.current = 1;
        getDataList();
      })
};

//单个处理未处理数据
const handlecheck = async (id: string) => {
  abnormalApi.onePatientResult(formId.value, id)
      .then(res => {
        Toast("处理成功");
        FuncGetData();
        param.value.current = 1;
        getDataList();
      })
};

//获取提示信息
const getplanmsg = () => {
  showTips.value = !showTips.value;
};

// Android调用js方法
window.callJsFunction = (str) => {
  // Android 表单更新后，调用方法， 更新数据
  param.value.current = 1;
  FuncGetData();
  getDataList();
};

onMounted(() => {
  planId.value = route.query.planId;
  formId.value = route.query.formId;
  planName.value = route.query.planName;
  planType.value = route.query.planType;
  height.value = window.innerHeight - 320;
  FuncGetData();
  window.addEventListener('message', function (e) {
    if (e && e.data) {
      console.log('patientManage 医助修改了表单，赶紧刷新一下数据', JSON.stringify(e.data))
      let msg = {type: ''}
      try {
        msg = JSON.parse(e.data)
      } catch (e) {
        return
      }
      if (msg.type === 'patientManageBack') {
        param.value.current = 1;
        getDataList();
        setTimeout(() => {
          FuncGetData();
        }, 500)
      }
    }
  })
});

onUnmounted(() => {
  Toast("测试一下")
  console.log('页面/组件退出，触发了onUnmounted钩子函数')
})

//显示全部处理 或 全部清理弹框
const showmodal = (type: string) => {
  type == "showwarn"
      ? (showwarn.value = !showwarn.value)
      : (showclear.value = !showclear.value);
};

//APP跳转到患者详情界面
const jumpPatientInfo = (item: string) => {
  const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
  if (caringCurrentDevice) {
    let history = { path: '/abnormal/list', query: {planName: planName.value, planId: planId.value, formId: formId, planType: planType.value} }
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageSeePatient', data: item}, '*')
  } else if ((window as any).android) {
    (window as any).android.jumpPatientInfo(item);
  } else if (window.parent) {
    let history = { path: '/abnormal/list', query: {planName: planName.value, planId: planId.value, formId: formId, planType: planType.value} }
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageSeePatient', data: item}, '*')
  }
};

// 跳转到患者聊天
const jumpPatientChat = (item: string) => {
  const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
  if (caringCurrentDevice) {
    let history = { path: '/abnormal/list', query: {planName: planName.value, planId: planId.value, formId: formId, planType: planType.value} }
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageChatPatient', data: item}, '*')
  } else if ((window as any).android) {
    (window as any).android.jumpPatientChat(item);
  } else if (window.parent) {
    let history = { path: '/abnormal/list', query: {planName: planName.value, planId: planId.value, formId: formId, planType: planType.value} }
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageChatPatient', data: item}, '*')
  }
};

// 跳转到患者表单
const jumpForm = (formId: string, patientId: string) => {
  const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
  if (caringCurrentDevice) {
    let history = { path: '/abnormal/list', query: {planName: planName.value, planId: planId.value, formId: formId, planType: planType.value} }
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientForm', data: {planName: planName.value, planId: planId.value, formId: formId, planType: planType.value, patientId: patientId}}, '*')
  } else if ((window as any).android) {
    (window as any).android.jumpPatientForm(formId, planType.value, patientId, planName.value);
  } else if (window.parent) {
    let history = { path: '/abnormal/list', query: {planName: planName.value, planId: planId.value, formId: formId, planType: planType.value} }
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientForm', data: {planName: planName.value, planId: planId.value, formId: formId, planType: planType.value, patientId: patientId}}, '*')
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
            max-width: 200px;
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
    z-index: 9;
    font-size: 13px;
    font-weight: 400;
    color: #666666;
    background-color: #f5f5f5;
    // line-height: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 10px 10px 15px;


    .list_time {
      display: flex;
      align-items: center;
      margin-right: 5px;

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
    width: 100%;
    position: fixed;
    overflow-y: scroll;

    .listbox {
      overflow: auto;
      height: 100%;

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
        border-radius: 10px;
        margin: 15px;
        padding: 12px;

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
            width: 140px;
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
          padding: 0px 13px;
          margin-top: 13px;

          ::v-deep .van-cell {
            background: rgba(245, 245, 245, 0.6);
            padding: 0px;
          }

          div:first-child {
            padding-top: 1px;
          }

          .list_item_list {
            display: flex;
            justify-content: space-between;
            padding-top: 15px;

            .black_title {
              font-size: 14px;
              color: #0E0E0E;
              font-weight: bolder;
            }

            .red_value {
              color: #EF4848;
              font-size: 12px;
              margin-top: 10px;
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
        width: 25px;
        height: 25px;
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
    width: 100%;
    height: 120px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;

    p {
      margin-top: 15px;
    }
  }
}

.no_data {
  text-align: center;
  background: #FFF;
  margin: 0px 10px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
}
</style>
