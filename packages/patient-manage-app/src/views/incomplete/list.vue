<template>
  <div class="list_box">
    <doctorDialog ref="refDoctor" @result-data="resultData"/>
    <van-sticky>
      <appHeader
          :title="planName"
          :leftCustomize="true"
          @handelPage="backIndex"
      />
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
                <div style="font-size: 13px; color: #CADEFF">已处理的数据仅统计一周的人数，一周后清空</div>
              </div>
            </div>
            <div
                class="list_all"
                @click="showmodal(active == 1 ? 'showwarn' : 'showclear')"
            >
              {{ active == 1 ? "全部处理" : "全部清空" }}
            </div>
          </div>
          <div style="margin-top: 20px">
            <van-search
                placeholder="搜索"
                v-model="seachval" :clearable="false"
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
    <div class="list_title" style="justify-content: center">
      <div class="list_time" @click="sortlist">
        排序
        <div style="margin-left: 3px">
          <div class="list_item_img_top">
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
          <div>
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
      <div class="list_time" style="margin-left: 100px;" @click="doctorFilter">
        筛选
        <div style="margin-left: 3px">
          <van-icon name="filter-o"/>
        </div>
      </div>
    </div>
    <!-- list -->
    <div class="list_content">
      <!-- 未列表 -->
      <div class="listbox" :style="{height: height + 'px'}">
        <van-list
            v-model="loading"
            :finished="finished"
            @load="onLoad">
          <div v-for="(item, index) in list" :key="index">
            <div style="padding: 0px 15px 15px 15px" v-if="active === 1">
              <van-swipe-cell>
                <div class="list_item" style="margin: 0px">
                  <div>
                    <div class="list_item_top" @click="jumpPatientInfo(item)">
                      <div class="list_item_top_left">
                        <div class="list_item_img">
                          <img :src="item.patientAvatar"/>
                        </div>
                        <div class="list_item_info">
                          <p style="margin-top: 5px">{{ item.patientName }}</p>
                          <div class="list_item_is_follow">{{
                              item.patientFollowStatus !== 2 ? '已关注' : '未关注'
                            }}
                          </div>
                        </div>
                      </div>
                      <div class="list_check" @click.stop="handlecheck(item.id)">
                        <img src="../../assets/images/info_no_check.png"/>
                      </div>
                    </div>
                    <!-- 文本内容 -->
                    <div class="list_item_content" @click="jumpPatientInfo(item)">
                      <div>
                        <div style="display: flex; align-items: center">
                          所属医生：{{ item.doctorName }}
                        </div>
                        <div style="display: flex; align-items: center; margin-top: 10px;">
                          任务推送时间：{{ getTime(item.remindTime) }}
                        </div>
                      </div>
                      <div>
                        <van-icon name="arrow" color="#999"/>
                      </div>
                    </div>
                    <!-- 联系他 -->
                    <div class="list_contact" @click="jumpPatientChat(item.patientId)">
                      <img src="../../assets/images/wx_icon.png" alt=""/>
                      联系TA
                    </div>
                  </div>
                  <div class="list_item_seeStatus" v-if="item.seeStatus === 1">
                    已查看
                  </div>
                </div>
                <template #right>
                  <div style="background: #FD5249; width: 70px; height: 100%; display: flex; align-items: center;
                  justify-content: center; flex-direction: column" @click="deleteIncomplete(item.id)">
                    <van-icon name="delete-o" color="#FFF" size="20px"/>
                    <span style="color: #FFF; font-size: 14px; margin-top: 5px;">删除</span>
                  </div>
                </template>
              </van-swipe-cell>
            </div>

            <div class="list_item" v-else>
              <div>
                <div class="list_item_top" @click="jumpPatientInfo(item)">
                  <div class="list_item_top_left">
                    <div class="list_item_img">
                      <img :src="item.patientAvatar"/>
                    </div>
                    <div class="list_item_info">
                      <p style="margin-top: 5px">{{ item.patientName }}</p>
                      <div class="list_item_is_follow">{{ item.patientFollowStatus !== 2 ? '已关注' : '未关注' }}</div>
                    </div>
                  </div>
                </div>
                <!-- 文本内容 -->
                <div class="list_item_content">
                  <div>
                    <div style="display: flex; align-items: center">
                      所属医生：{{ item.doctorName }}
                    </div>
                    <div style="display: flex; align-items: center; margin-top: 10px;">
                      任务推送时间：{{ getTime(item.remindTime) }}
                    </div>
                  </div>
                  <div>
                    <van-icon name="arrow" color="#999"/>
                  </div>
                </div>
                <!-- 联系他 -->
                <div class="list_contact" @click="jumpPatientChat(item.patientId)">
                  <img src="../../assets/images/wx_icon.png" alt=""/>
                  联系TA
                </div>
              </div>
              <div class="list_item_seeStatus" v-if="item.seeStatus === 1">
                已查看
              </div>
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
import doctorDialog from "./components/doctorDialog.vue";
import Api from "@/api/incomplete";
import {Dialog, SwipeCell, Toast} from "vant";

const active = ref<number>(1); //选择TAB
const isDown = ref<boolean>(true); //上传时间排序
const showTips = ref<boolean>(false); //监测说明弹窗
const showwarn = ref<boolean>(false); //全部处理弹窗
const showclear = ref<boolean>(false); //全部清除弹窗
const list = ref<Array<any>>([]); //未处理列表
const doctorIds = ref<Array<any>>([]); //医生id
const listsum = ref<number>(0); //未处理总数
const handlelistsum = ref<number>(0); //已处理总数
const planId = ref<any>(""); //计划id
const unFinishedSettingId = ref<any>(""); // 配置id
const planName = ref<any>(""); //计划名称
const planType = ref<any>(""); //计划名称
const seachval = ref<string>("");
const startDate = ref<string>("");
const endDate = ref<string>("");
const model = ref<any>({
  1: showwarn,
  2: showclear,
}); //v-model数据
const refDoctor = ref();
const router = useRouter();
const route = useRoute();
const height = ref<number>(0);
const loading = ref<boolean>(true);
const finished = ref<boolean>(false);
const param = ref<any>({
  model: {
    unFinishedSettingId: '',
    nursingId: localStorage.getItem('caring-userId'),
    patientName: '',
    handleStatus: active.value === 1 ? 0 : 1,
    sort: 'desc',
    doctorIds: [], // 医生id
    startDate: '', // 开始时间
    endDate: '', // 开始时间
  },
  current: 1,
  size: 20,
}); //请求默认数据
import moment from "moment";
// 获取总数
const FuncGetData = () => {
  let data = {
    doctorIds: param.value.model.doctorIds,
    handleStatus: active.value === 1 ? 0 : 1,
    nursingId: localStorage.getItem('caring-userId'),
    patientName: seachval.value ? seachval.value : '',
    unFinishedSettingId: unFinishedSettingId.value,
    startDate: startDate.value,
    endDate: endDate.value,
    seeStatus: param.value.model.seeStatus,
    patientFollowStatus: param.value.model.patientFollowStatus,
  }
  Api.getCountHandleNumber(data)
      .then((res: {}) => {
        if (res.data.noHandlePatientNumber) {
          listsum.value = res.data.noHandlePatientNumber;
        } else {
          listsum.value = 0
        }
        if (res.data.handlePatientNumber) {
          handlelistsum.value = res.data.handlePatientNumber;
        } else {
          handlelistsum.value = 0
        }
      })
}
// 未完成表单删除
const deleteIncomplete = (id: any) => {
  Dialog.confirm({
    title: '操作提醒',
    message: '您是否将该患者移除？移除后则表示任务已完成',
  })
      .then(() => {
        Api.setIncomplete(id)
            .then(res => {
              Toast("处理成功");
              FuncGetData();
              param.value.current = 1;
              getDataList();
            })
      })
      .catch(() => {
        // on cancel
      });
}
// 筛选
const resultData = (val: any) => {
  param.value = val
  param.value.current = 1
  param.value.model.handleStatus = active.value === 1 ? 0 : 1
  getDataList();
  startDate.value = param.value.model.startDate
  endDate.value = param.value.model.endDate
  FuncGetData();
}
// list 加载数据
const onLoad = () => {
  getDataList();
}

// 查询未处理记录、已处理记录
const getDataList = () => {
  Api.getDataList(param.value)
      .then((res: {}) => {
        if (res.data && res.data.records && res.data.records.length > 0) {
          let records = res.data.records
          if (param.value.current === 1) {
            list.value = records
          } else {
            list.value = [...list.value, ...records]
          }
        }else if (param.value.current === 1) {
          list.value = []
        }
        if (list.value.length === res.data.total) {
          finished.value = true
        } else {
          finished.value = false
        }
        loading.value = false;
        param.value.current = param.value.current + 1
      })
}
const backIndex = () =>{
  router.replace("/incomplete/index")
}
//选择tab
const checkTab = (tab: number) => {
  active.value = tab;
  param.value.current = 1
  param.value.model.handleStatus = active.value === 1 ? 0 : 1
  param.value.model.sort = 'desc'
  isDown.value = false
  getDataList()
};

// 医生筛选
const doctorFilter = () => {
  if (refDoctor.value) {
    refDoctor.value.setShow(true, param.value);
  }
};

//排序
const sortlist = () => {
  isDown.value = !isDown.value;
  param.value.current = 1
  param.value.model.sort = isDown.value ? 'desc' : 'asc'
  getDataList()
};

// 搜索
const seach = (val: string) => {
  param.value.model.patientName = seachval.value;
  param.value.current = 1;
  FuncGetData();
  getDataList();
};

// 获取计划时间
const getTime = (time: string) => {
  return moment(time).format("yyyy-MM-DD")
}

// 获取字段选择内容
const getFiledContent = (optionInfos: []) => {
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
const isTwoShow = (optionInfos: []) => {
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
  let data = {
    unFinishedSettingId: unFinishedSettingId.value,
    nursingId: localStorage.getItem('caring-userId'),
    patientName: '',
    handleStatus: active.value === 1 ? 0 : 1,
  }
  if (type == "clear") {
    Api.allClearData(data)
        .then(res => {
          Toast("清空成功");
          FuncGetData()
          param.value.current = 1;
          getDataList();
        })
  } else {
    Api.allHandleData(data)
        .then(res => {
          Toast("处理成功");
          FuncGetData()
          param.value.current = 1;
          getDataList();
        })
  }
};

//单个处理未处理数据
const handlecheck = async (id: string) => {
  Api.setIncomplete(id)
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
window.callJsFunction = () => {
  // Android 表单更新后，调用方法， 更新数据
  param.value.current = 1;
  getDataList();
};

onMounted(() => {
  planId.value = route.query.planId;
  unFinishedSettingId.value = route.query.unFinishedSettingId;
  planName.value = route.query.planName;
  planType.value = route.query.planType;
  param.value.model.unFinishedSettingId = unFinishedSettingId.value
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
  Api.seeOneResult(item.id)
  const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
  if (caringCurrentDevice) {
    let history = { path: '/incomplete/list', query: {planId: route.query.planId, unFinishedSettingId: route.query.unFinishedSettingId, planName: route.query.planName, planType: route.query.planType}}
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageSeePatient', data: item.patientId}, '*')
  } else if ((window as any).android) {
    (window as any).android.jumpPatientInfo(item.patientId);
  } else if (window.parent) {
    let history = { path: '/incomplete/list', query: {planId: route.query.planId, unFinishedSettingId: route.query.unFinishedSettingId, planName: route.query.planName, planType: route.query.planType}}
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageSeePatient', data: item.patientId}, '*')
  }
};

// 跳转到患者聊天
const jumpPatientChat = (item: string) => {
  const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
  if (caringCurrentDevice) {
    let history = { path: '/incomplete/list', query: {planId: route.query.planId, unFinishedSettingId: route.query.unFinishedSettingId, planName: route.query.planName, planType: route.query.planType}}
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageChatPatient', data: item}, '*')
  } else if ((window as any).android) {
    (window as any).android.jumpPatientChat(item);
  } else if (window.parent) {
    let history = { path: '/incomplete/list', query: {planId: route.query.planId, unFinishedSettingId: route.query.unFinishedSettingId, planName: route.query.planName, planType: route.query.planType}}
    localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
    window.parent.postMessage({action: 'patientManageChatPatient', data: item}, '*')
  }
};

// 跳转到患者表单
const jumpForm = (formId: string, patientId: string) => {
  const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
  if (caringCurrentDevice) {
    window.parent.postMessage({
      action: 'patientForm',
      data: {
        planName: planName.value,
        planId: planId.value,
        formId: formId,
        planType: planType.value,
        patientId: patientId
      }
    }, '*')
  } else if ((window as any).android) {
    (window as any).android.jumpPatientForm(formId, planType.value, patientId, planName.value);
  } else if (window.parent) {
    window.parent.postMessage({
      action: 'patientForm',
      data: {
        planName: planName.value,
        planId: planId.value,
        formId: formId,
        planType: planType.value,
        patientId: patientId
      }
    }, '*')
  }
};
</script>

<style lang="less" scoped>
.list_box {
  background: rgba(245, 245, 245, 1);
  position: relative;
  height: 100vh;

  .list_top {
    background: linear-gradient(90deg, #3f86ff 0%, #6ea8ff 100%);
    box-shadow: 0px 2px 2px 1px rgba(51, 126, 255, 0.08);

    .list_top_content {
      padding: 20px 20px 7px 20px;

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
        border-radius: 5px;
        margin: 15px;
        padding: 12px;
        position: relative;

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
          font-size: 16px;
          fonw-weight: 600;
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
          padding: 10px 13px;
          margin-top: 13px;
          color: #666;
          font-size: 12px;
          display: flex;
          align-items: center;
          justify-content: space-between;
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
    height: 40px;
    border-radius: 22px;
  }

  :deep(.van-search__content) {
    background: #ffffff;

    input {
      font-size: 14px;
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

.list_item_is_follow {
  font-size: 11px;
  color: #1890FF;
  padding: 3px 6px;
  width: fit-content;
  text-align: center;
  margin-top: 6px;
  border-radius: 3px;
  border: 1px solid #1890FF;
}

.list_item_seeStatus {
  position: absolute;
  top: 0;
  right: 0;
  font-size: 12px;
  background: #D8EDFF;
  color: #1890FF;
  border-top-right-radius: 5px;
  border-bottom-left-radius: 5px;
  padding: 5px 8px
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
