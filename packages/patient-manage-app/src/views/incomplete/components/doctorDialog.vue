<template>
  <div style="position: relative">
    <van-action-sheet :show="isShow" @close="isShow = false" @cancel="isShow = false" title="患者筛选条件设置">
      <div slot="default" class="content">
        <div class="title" style="margin-top: 0px">
          <img src="@/assets/images/patient-icon-1.png" style="width: 18px; height: 18px; margin-right: 5px"/>
          所属医生
        </div>
        <div v-if="doctorList.length > 0">
          <van-grid :column-num="3" :border="false">
            <van-grid-item v-for="(item, index) in doctorList" :key="index" >
              <div :class="isDoctorSelect(item.id) ? 'select' : 'normal'" @click="doctorSelectClick(item.name, item.id)">{{ item.name }}</div>
            </van-grid-item>
          </van-grid>
        </div>
        <div v-else>
          <van-grid :column-num="3" :border="false">
            <van-grid-item v-for="(item, index) in 1" :key="index">
              <div class="normal" style="color: #CFCFCF; ">暂未绑定医生</div>
            </van-grid-item>
          </van-grid>
        </div>

        <div class="title">
          <img src="@/assets/images/patient-icon-2.png" style="width: 18px; height: 18px; margin-right: 5px"/>
          推送时间
        </div>
        <div>
          <van-grid :column-num="3" :border="false">
            <van-grid-item v-for="(item, index) in pushTime" :key="index" @click="pushTimeSelectClick(index)">
              <div :class="pushTimeSelect === index ? 'select' : 'normal'">{{ item.name }}</div>
            </van-grid-item>
          </van-grid>

          <div v-if="pushTimeSelect === 5"
               style="display: flex; align-items: center; margin-top: 10px; justify-content: space-between; padding-left: 6px; padding-right: 6px">
            <div class="normal" style="width: 45%" :style="{color: startTime ? '#333333' : '#999999'}"
                 @click="isTime = true, timeType = 'start'">
              {{ startTime ? startTime : '开始日期' }}
            </div>
            <div style="flex: 1; background: #DDDDDD; height: 2px; margin-left: 10px; margin-right: 10px"></div>
            <div class="normal" style="width: 45%" :style="{color: endTime ? '#333333' : '#999999'}"
                 @click="isTime = true, timeType = 'end'">
              {{ endTime ? endTime : '结束日期' }}
            </div>
          </div>
        </div>

        <div class="title">
          <img src="@/assets/images/patient-icon-3.png" style="width: 18px; height: 18px; margin-right: 5px"/>
          查看状态
        </div>
        <div>
          <van-grid :column-num="3" :border="false">
            <van-grid-item v-for="(item, index) in seeStatue" :key="index" @click="seeSelectClick(index)">
              <div :class="index === seeSelect ? 'select' : 'normal'">{{ item.name }}</div>
            </van-grid-item>
          </van-grid>
        </div>

        <div class="title">
          <img src="@/assets/images/patient-icon-4.png" style="width: 18px; height: 18px; margin-right: 5px"/>
          关注状态
        </div>
        <div>
          <van-grid :column-num="3" :border="false">
            <van-grid-item v-for="(item, index) in followStatue" :key="index" @click="followSelectClick(index)">
              <div :class="index === followSelect ? 'select' : 'normal'">{{ item.name }}</div>
            </van-grid-item>
          </van-grid>
        </div>
        <div
            style="border-top: 1px solid #E9E9E9; display: flex; align-items: center; justify-content: space-between; padding-top: 15px; padding-bottom: 15px; margin-top: 15px;">
          <div @click="reset()"
               style="color: #333; font-size: 14px; padding: 10px 15px; border: 1px solid #E9E9E9; border-radius: 20px; width: 25%; text-align: center; margin-right: 25px">
            重置
          </div>
          <div @click="getCommit()"
               style="color: #FFF; font-size: 14px; padding: 10px 15px; background: #1890FF; border-radius: 20px; flex: 1; text-align: center">
            确定
          </div>
        </div>
      </div>
    </van-action-sheet>
    <van-overlay :show="isTime" @click="isTime = false" z-index="3000">
      <div style="display: flex; height: 100%; width: 100%; position: relative" @click.stop>
        <van-datetime-picker
            @cancel="isTime = false" @confirm="timeConfirm"
            style="position: absolute;  width: 100%; bottom: 0"
            type="date"
            v-model="currentDate"
            title="选择年月日"
            :min-date="minDate"
            :max-date="maxDate"
        />
      </div>
    </van-overlay>
    <div v-if="isDoctorShow" style="position: fixed; top: 0; bottom: 0; left: 0; right: 0; height: 100%; width: 100%; z-index: 3000">
      <!-- 内部滚动内容 -->
      <div class="docotr_box">
        <appHeader
            title="全部医生" :leftCustomize="true"
            @handelPage="isDoctorShow = false, isShow = true"
        />
        <div
            style="background: white; display: flex; align-items: center; padding-left: 15px; padding-right: 15px; padding-bottom: 10px">
          <div
              style="display: flex; flex:1; align-items: center; border-radius: 20px; height: 40px; background: #F5F7F9; border-radius: 30px;">
            <van-icon name="search" size="20px" style="padding-left: 15px;"/>
            <van-field v-model="searchDoctor" placeholder="请输入医生姓名"
                       style="font-size: 16px; background: unset !important; flex: 1;border-radius: 30px;"/>
          </div>
          <div style="color: #333; font-size: 18px; padding-left: 15px"
               @click="onDoctorSearch">搜索
          </div>
        </div>
        <!--  历史搜索记录    -->
        <div style="padding: 15px; background: white; " v-if="historyDoctor.length > 0">
          <div class="title" style="color: #666666; margin-top: 0px">
            历史搜索记录
          </div>
          <van-grid :column-num="4" :border="false">
            <van-grid-item v-for="(item, index) in historyDoctor" :key="index"
                           @click="doctorSelectClick(item.name, item.id)">
              <div :class="isDoctorSelect(item.id) ? 'history_doctor_select' : 'history_doctor_normal'">
                <img :src="item.avatar" style="width: 45px; height: 45px; border-radius: 50%"/>
                <div :style="{background: isDoctorSelect(item.id) ? '#EDF6FF' : '#F6F6F6'}"
                     class="history_doctor_name">
                  {{ item.name }}
                </div>
              </div>
            </van-grid-item>
          </van-grid>
        </div>
        <div style="width: 100%; background: rgba(245, 245, 245, 1); height: 10px"></div>
        <!--  全部医生    -->
        <div style="padding:15px; background: white">
          <div class="title" style="color: #666666; margin-top: 0px;">
            全部医生列表
          </div>
          <van-list style="padding-bottom: 100px; overflow: scroll">
            <div v-for="(item, index) in allDoctorList" :key="index" @click="doctorSelectClick(item.name, item.id)"
                 style="display: flex; align-items: center; justify-content: space-between; padding-top: 15px;">
              <div style="display: flex; align-items: center">
                <img :src="item.avatar" style="width: 70px; height: 70px; border-radius: 50%"/>
                <div style="display: flex; flex-direction: column; justify-content: center; margin-left: 10px;">
                  <div style="color: #333; font-size: 16px">{{ item.name }}</div>
                </div>
              </div>
              <div>
                <img v-if="isDoctorSelect(item.id)" src="@/assets/images/doctor_select.png"
                     style="width: 20px; height: 20px"/>
                <img v-else src="@/assets/images/doctor_normal.png" style="width: 20px; height: 20px"/>
              </div>
            </div>
          </van-list>

        </div>

        <div
            style="position: fixed; bottom: 0; width: 100%; background: #FFF; border-top: 1px solid #E9E9E9; display: flex; align-items: center; justify-content: space-between; padding-top: 15px; padding-bottom: 15px; margin-top: 15px;">
          <div @click="doctorReset()"
               style="color: #333; font-size: 14px; padding: 10px 15px; border: 1px solid #E9E9E9; border-radius: 20px; width: 25%; text-align: center; margin-left: 15px;">
            清空
          </div>
          <div @click="isDoctorShow = false, isShow = true"
               style="color: #FFF; font-size: 14px; padding: 10px 15px; background: #1890FF; border-radius: 20px; flex: 1; margin-left: 20px; text-align: center; margin-right: 15px;">
            确定
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, defineEmits} from "vue";
import Api from "@/api/incomplete";
import moment from "moment";
import appHeader from "@/components/header/index.vue";

const emit = defineEmits(['result-data']);
const param = ref<any>();
const searchDoctor = ref<string>();
const isShow = ref<boolean>(false);
const isDoctorShow = ref<boolean>(false);
const isTime = ref<boolean>(false);
// 关注状态
const followSelect = ref<number>();
// 查看状态
const seeSelect = ref<number>();
// 推送时间
const pushTimeSelect = ref<number>();
// 开始时间
const startTime = ref<string>();
// 结束时间
const endTime = ref<string>();
// 时间选择最小时间
const minDate = moment('2020-01-01').toDate();
// 时间选择最大时间
const maxDate = moment().toDate();
// 当前时间
const currentDate = moment().toDate();
// 时间选择类型
const timeType = ref<string>('start');
// 选中的医生id
const doctorId = ref<any>([]);
// 全部医生
const allDoctor = ref<any>([]);
const allDoctorList = ref<any>([]);

const historyDoctor = ref<any>([]);
// 显示弹窗
const setShow = (val: boolean, params: any) => {
  isShow.value = val;
  param.value = params;
  getAllDoctor();
}
const pushTime = ref([{name: '全部'}, {name: '近一周'}, {name: '近两周'}, {name: '近一个月'}, {name: '近两个月'}, {name: '自定义'}])
const doctorList = ref<any>([])
const seeStatue = ref([{name: '全部'}, {name: '已查看'}, {name: '未查看'}])
const followStatue = ref([{name: '全部'}, {name: '已关注'}, {name: '未关注'}])
// 选择时间
const timeConfirm = (val: string) => {
  isTime.value = false;
  if (timeType.value === 'start') {
    startTime.value = moment(val).format('YYYY-MM-DD')
    param.value.model.startDate = moment(val).format('YYYY-MM-DD')
  } else {
    endTime.value = moment(val).format('YYYY-MM-DD')
    param.value.model.endDate = moment(val).format('YYYY-MM-DD')
  }
}
// 全部医生搜索
const onDoctorSearch = (val: string) => {
  if (searchDoctor.value) {
    // 有搜索内容
    allDoctorList.value = []
    allDoctor.value.forEach((item: any) => {
      if (item.name.indexOf(searchDoctor.value) !== -1) {
        allDoctorList.value.push(item)
      }
    })
  } else {
    allDoctorList.value = allDoctor.value;
  }
}
// 历史医生
const getDoctorHistory = () => {
  Api.getDoctorHistory()
      .then((res: any) => {
        historyDoctor.value = res.data;
      })
}
// 关注状态点击
const followSelectClick = (index: number) => {
  followSelect.value = index;
  if (index === 0) {
    // 全部
    param.value.model.patientFollowStatus = ''
  } else if (index === 1) {
    // 已关注
    param.value.model.patientFollowStatus = 1
  } else if (index === 2) {
    // 已取关
    param.value.model.patientFollowStatus = 2
  }
}
// 重置
const reset = () => {
  isShow.value = false;
  doctorId.value = []
  followSelect.value = undefined
  seeSelect.value = undefined
  pushTimeSelect.value = undefined
  startTime.value = ''
  endTime.value = ''
  emit('result-data', {
    model: {
      unFinishedSettingId: param.value.model.unFinishedSettingId,
      nursingId: localStorage.getItem('caring-userId'),
      patientName: '',
      sort: 'desc',
      doctorIds: [], // 医生id
      startDate: '', // 开始时间
      endDate: '', // 开始时间
    },
    current: 1,
    size: 20,
  })
}

const doctorReset = () => {
  doctorId.value = []
  searchDoctor.value = ''
  allDoctorList.value = allDoctor.value;
}

const getCommit = () => {
  isShow.value = false;
  param.value.model.doctorIds = doctorId.value;
  emit('result-data', param.value)
}

// 查看状态点击
const seeSelectClick = (index: number) => {
  seeSelect.value = index;
  if (index === 0) {
    // 全部
    param.value.model.seeStatus = ''
  } else if (index === 1) {
    // 已查看
    param.value.model.seeStatus = 1
  } else if (index === 2) {
    // 未查看
    param.value.model.seeStatus = 0
  }
}

// 查看状态点击
const pushTimeSelectClick = (index: number) => {
  pushTimeSelect.value = index;
  if (index === 0) {
    param.value.model.startDate = ''
    param.value.model.endDate = ''
  } else if (index === 1) {
    // 近一周
    param.value.model.endDate = moment().format('YYYY-MM-DD');
    param.value.model.startDate = moment().subtract(6, 'days').format('YYYY-MM-DD');
  } else if (index === 2) {
    // 近两周
    param.value.model.endDate = moment().format('YYYY-MM-DD');
    param.value.model.startDate = moment().subtract(13, 'days').format('YYYY-MM-DD');
  } else if (index === 3) {
    // 近一个月
    param.value.model.endDate = moment().format('YYYY-MM-DD');
    param.value.model.startDate = moment().subtract(29, 'days').format('YYYY-MM-DD');
  } else if (index === 4) {
    // 近两个月
    param.value.model.endDate = moment().format('YYYY-MM-DD');
    param.value.model.startDate = moment().subtract(59, 'days').format('YYYY-MM-DD');
  }
}

const getAllDoctor = () => {
  Api.getAllDoctor({
    current: 1, size: 200, model: {
      nursingId: localStorage.getItem('caring-userId')
    }
  }).then(res => {
    allDoctor.value = res.data.records;
    allDoctorList.value = res.data.records;
    doctorList.value = []
    allDoctor.value.forEach((item: {}, index: number) => {
      if (index < 5) {
        doctorList.value.push(item)
      }
    })
    if(doctorList.value.length > 0) {
      doctorList.value.push({name: '全部'})
    }
  })
}
// 医生是否选择
const isDoctorSelect = (id: string) => {
  if (doctorId.value.includes(id)) {
    return true;
  } else {
    return false;
  }
}

// 点击医生
const doctorSelectClick = (name: string, id: string) => {
  if (name === '全部' && !id) {
    isDoctorShow.value = true;
    allDoctorList.value = allDoctor.value;
    searchDoctor.value = ''
    isShow.value = false;
    getDoctorHistory()
  } else {
    if (doctorId.value.includes(id)) {
      let elementIndex = doctorId.value.findIndex((item: string) => item === id);
      if (elementIndex > -1) {
        doctorId.value.splice(elementIndex, 1)
      }
    } else {
      doctorId.value.push(id)
    }
  }

}

defineExpose({
  setShow,
})

</script>

<style scoped lang="less">
/deep/ .van-action-sheet__header {
  font-weight: 500;
  font-size: 16px;
  color: #333333;
}

/deep/ .van-grid-item__content {
  padding: 13px 6px 0px 6px;
}

.title {
  font-weight: 500;
  font-size: 15px;
  margin-top: 15px;
  color: #333333;
  display: flex;
  align-items: center;
}

.normal {
  background: #F5F7F9;
  border-radius: 5px;
  font-size: 13px;
  width: 100%;
  text-align: center;
  padding-top: 8px;
  padding-bottom: 8px;
  font-weight: 500;
}

.select {
  background: #EDF6FF;
  border-radius: 5px;
  border: 2px solid #1890FF;
  font-size: 14px;
  width: 100%;
  text-align: center;
  padding-top: 6px;
  padding-bottom: 6px;
  font-weight: 500;
}

.history_doctor_normal {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  border: 1px solid #E6E6E6;
  border-radius: 5px;
  width: 100%;
  padding-top: 10px
}

.history_doctor_select {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  width: 100%;
  border: 1px solid #1890FF;
  border-radius: 5px;
  padding-top: 10px
}

.history_doctor_name {
  display: flex;
  justify-content: center;
  font-size: 14px;
  margin-top: 10px;
  padding: 10px 0px;
  width: 100%;
  border-bottom-left-radius: 5px;
  border-bottom-right-radius: 5px
}

.content {
  padding: 16px 16px;
}

.docotr_box {
  background: #FFF;
  position: relative;
  height: 100vh;
  overflow:scroll;
}
</style>
