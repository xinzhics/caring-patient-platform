<template>
  <div>
    <van-sticky>
      <headNavigation v-if="showNavBar" leftIcon="arrow-left" rightIcon="setting-o" @showpop="goReservationSetUp" title="预约管理" @onBack="onBack"></headNavigation>
      <div v-if="!showNavBar" style="display: flex;align-items: center;padding: 40px 13px 5px 13px;background: #FFFFFF;" :style="{padding: isWeiXin ? '10px 13px 5px 13px' : '40px 13px 5px 13px'}">
        <van-icon size="26" @click="onBack" color="#333333" name="arrow-left" />
        <div @click="goSearchDoctor" style="margin-left: 15px;padding: 10px 13px;background: #F5F5F5;border-radius: 43px;width: 100%">{{this.$route.query.doctorName}}</div>
      </div>
<!--      顶部搜索+预约审核-->
      <div class="search" :style="{'margin-bottom':showNavBar?'38px':'16px',height:showNavBar?'54px':'42px'}">
<!--        顶部跳转到审核-->
        <div style="display: flex;align-items: center;justify-content: space-between" @click="goReviewedList">
          <div style="font-size: 17px"><van-icon style="margin-right: 9px" size="17" name="todo-list-o" />待审核预约</div>
          <div style="font-size: 17px">
            <span>{{approvalNumber}}</span>
            <van-icon size="17" name="arrow" />
          </div>
        </div>
<!--        跳转到搜索-->
        <div v-if="showNavBar" @click="goSearchDoctor" style="margin-top: 13px;background: #FFFFFF;height: 22px;border-radius: 43px;color:#B8B8B8;padding: 11px 0 11px 13px;margin-bottom: 13px">
          <van-icon style="margin-right: 5px" name="search" />按{{this.$getDictItem('doctor')}}名搜索
        </div>
      </div>
      <div style="background: #FFFFFF; overflow-y: scroll" :style="{height: height + 'px'}">
        <div :class="!showDate?'clickDate':''" style="display: flex;align-items: center;justify-content: center;padding: 13px 0">
          <van-icon v-if="showDate" style="margin-right: 9px" name="arrow-left" @click="updateQueryDate(-1)" />
          <div style="width: 120px;height: 26px;text-align: center;line-height: 26px;background: #F5F5F5;border-radius: 43px;">
          <span @click="openOrCloseWeek">
            {{ isToday(this.queryDate) ? '今天' : formatQueryDate(this.queryDate) + ' ' + weekName}}
          </span>
          </div>
          <van-icon v-if="showDate" style="margin-left: 9px" name="arrow" @click="updateQueryDate(1)" />
        </div>
        <div v-if="!showDate" style="border-top: 1px solid #EBEBEB;padding: 22px 13px;background: #F5F5F5">
          <div style="display: flex;justify-content: space-between;align-items: center">
            <div class="week" @click="updateWeek(-1)" >
              <van-icon name="arrow-left"/>
              上周
            </div>
            <div>{{ queryWeekIndex === 0 ? '本周' : statisticsWeekData.mondayDay.substring(5,7) + '月' }}</div>
            <div class="week"  @click="updateWeek(1)">
              下周
              <van-icon name="arrow"/>
            </div>
          </div>
          <div style="display: flex;justify-content: space-between;align-items: center;margin-top: 22px">
            <div v-for="(item,index) in weekday" :key="index">
              <div :class="index>=5?'weekend weekends':'weekends'" style="">{{item}}</div>
            </div>
          </div>
          <div style="display: flex;justify-content: space-between;align-items: center;margin-top: 22px">
            <!-- 周一 -->
            <div>
              <div @click="selectDay(statisticsWeekData.mondayDay)" :class="dataEqual(statisticsWeekData.mondayDay) ? 'people-click people-number': ' people-number'" style="height: 61px;">
                <div>{{statisticsWeekData.mondayDay.substring(8,10)}}</div>
                <div style="font-size: 13px;color: #999999; overflow: hidden;text-overflow: ellipsis; white-space: nowrap;">
                  {{statisticsWeekData.mondayUserTotal}}人</div>
              </div>
            </div>
            <div>
              <div @click="selectDay(statisticsWeekData.tuesdayDay)" :class="dataEqual(statisticsWeekData.tuesdayDay) ? 'people-click people-number': 'people-number'" style="height: 61px;">
                <div>{{statisticsWeekData.tuesdayDay.substring(8,10)}}</div>
                <div style="font-size: 13px;color: #999999; overflow: hidden;text-overflow: ellipsis; white-space: nowrap;">
                  {{statisticsWeekData.tuesdayUserTotal}}人</div>
              </div>
            </div>
            <div>
              <div @click="selectDay(statisticsWeekData.wednesdayDay)" :class="dataEqual(statisticsWeekData.wednesdayDay) ? 'people-click people-number': 'people-number'" style="height: 61px;">
                <div>{{statisticsWeekData.wednesdayDay.substring(8,10)}}</div>
                <div style="font-size: 13px;color: #999999; overflow: hidden;text-overflow: ellipsis; white-space: nowrap;">
                  {{statisticsWeekData.wednesdayUserTotal}}人</div>
              </div>
            </div>
            <div>
              <div @click="selectDay(statisticsWeekData.thursdayDay)" :class="dataEqual(statisticsWeekData.thursdayDay) ? 'people-click people-number': 'people-number'" style="height: 61px;">
                <div>{{statisticsWeekData.thursdayDay.substring(8,10)}}</div>
                <div style="font-size: 13px;color: #999999; overflow: hidden;text-overflow: ellipsis; white-space: nowrap;">
                  {{statisticsWeekData.thursdayUserTotal}}人</div>
              </div>
            </div>
            <div>
              <div @click="selectDay(statisticsWeekData.fridayDay)" :class="dataEqual(statisticsWeekData.fridayDay) ? 'people-click people-number': 'people-number'" style="height: 61px;">
                <div>{{statisticsWeekData.fridayDay.substring(8,10)}}</div>
                <div style="font-size: 13px;color: #999999; overflow: hidden;text-overflow: ellipsis; white-space: nowrap;">
                  {{statisticsWeekData.fridayUserTotal}}人</div>
              </div>
            </div>
            <div>
              <div @click="selectDay(statisticsWeekData.saturdayDay)" :class="dataEqual(statisticsWeekData.saturdayDay) ? 'people-click people-number': 'people-number'" style="height: 61px;">
                <div>{{statisticsWeekData.saturdayDay.substring(8,10)}}</div>
                <div style="font-size: 13px;color: #999999; overflow: hidden;text-overflow: ellipsis; white-space: nowrap;">
                  {{statisticsWeekData.saturdayUserTotal}}人</div>
              </div>
            </div>
            <div>
              <div @click="selectDay(statisticsWeekData.sundayDay)" :class="dataEqual(statisticsWeekData.sundayDay) ? 'people-click people-number': 'people-number'" style="height: 61px;">
                <div>{{statisticsWeekData.sundayDay.substring(8,10)}}</div>
                <div style="font-size: 13px;color: #999999; overflow: hidden;text-overflow: ellipsis; white-space: nowrap;">
                  {{statisticsWeekData.sundayUserTotal}}人</div>
              </div>
            </div>
          </div>
        </div>
        <div  v-if="showDate" style="margin-top: 10px;padding: 0 13px 13px 13px">
          <div style="width: 100%;display: flex;justify-content: space-between;align-items: center;border-radius: 7px;background: #F5F5F5">
            <div class="box">
              <div style="border-right: 1px solid #eee;padding: 0 27px 0 27px">
                <div class="box-top">预约人数</div>
                <div class="box-bottom" style="color: #FF6F00">{{statisticsDayData.appointmentTotal}}</div>
              </div>
            </div>
            <div class="box">
              <div style="padding: 17px 27px 17px 27px">
                <div class="box-top">上午预约</div>
                <div class="box-bottom">{{statisticsDayData.appointmentMorningTotal}}</div>
              </div>
            </div>
            <div class="box">
              <div style="padding: 17px 27px 17px 27px">
                <div class="box-top">下午预约</div>
                <div class="box-bottom">{{statisticsDayData.appointmentAfternoonTotal}}</div>
              </div>
            </div>
          </div>
          <div style="width: 100%;background:#F5F5F5;display: flex;justify-content: space-between;align-items: center;border-radius: 7px;margin-top: 13px">
            <div class="box">
              <div style="border-right: 1px solid #eee;padding: 0 27px 0 27px">
                <div class="box-top">就诊人数</div>
                <div class="box-bottom" style="color: #337EFF">{{statisticsDayData.seeDoctorTotal}}</div>
              </div>
            </div>
            <div class="box">
              <div style="padding: 17px 27px 17px 27px">
                <div class="box-top">上午就诊</div>
                <div class="box-bottom">{{statisticsDayData.seeDoctorMorningTotal}}</div>
              </div>
            </div>
            <div class="box">
              <div style="padding: 17px 27px 17px 27px">
                <div class="box-top">下午就诊</div>
                <div class="box-bottom">{{statisticsDayData.seeDoctorAfternoonTotal}}</div>
              </div>
            </div>
          </div>
        </div>

        <div>
          <van-tabs color="#3F86FF" v-model="active" swipeable @change="beforeChange">
            <van-tab title="待就诊">
              <van-pull-refresh v-if="active === 0" v-model='refreshing' @refresh='onRefresh'>
                <van-list
                  v-model='loading'
                  :finished='finished'
                  :style="{minHeight: listHeight + 'px'}"
                  :finished-text='morningAppointmentList.length > 0 || afternoonAppointmentList.length > 0 ? "没有更多了" : ""'
                  @load='pageAppointmentQuery'>
                  <div v-if="morningAppointmentList.length > 0 || afternoonAppointmentList.length > 0">
                    <!--            上午-->
                    <div style="background: #fff;padding: 16px 13px 16px 13px;margin-top: 13px" v-if="morningAppointmentList.length > 0">
                      <div style="padding-bottom: 15px;color: #666666;">上午</div>
                      <div v-for="(appointment, index) in morningAppointmentList" :key="appointment.id"
                           style="border-top: 1px solid #EBEBEB;padding: 13px 0 13px 0" >
                        <van-row  gutter="20" type="flex" justify="space-between" align="center">
                          <van-col span="4">
                            <van-image
                              round
                              width="48px"
                              height="48px"
                              :src="appointment.avatar"
                            />
                          </van-col>
                          <van-col span="14">
                            <div class="list-name">{{appointment.patientName}}</div>
                            <div class="list-doc">预约{{$getDictItem('doctor')}}: {{appointment.doctorName}}</div>
                          </van-col>
                          <van-col span="6">
                            <div style="text-align: right">
                              <van-button size="small" style="width: 52px" round type="info" @click="signIn(1, appointment, index)">签到</van-button>
                            </div>
                          </van-col>
                        </van-row>
                      </div>
                      <div style="display: flex;justify-content: center;font-size: 13px;color: #999999;align-items: center" @click="selectOpenAndClose(true)">
                        {{ isMorningOpen?'收起':'展开' }}
                        <img width="17px" height="17px" :src="isMorningOpen ? require('@/assets/my/booking_patient_footer_close.png') : require('@/assets/my/booking_patient_footer_open.png')" alt=""></div>
                    </div>
                    <!--            下午-->
                    <div style="margin-top: 13px;background: #ffffff;padding: 16px 13px 16px 13px;" v-if="afternoonAppointmentList.length > 0">
                      <div style="padding-bottom: 15px;color: #666666;">下午</div>
                      <div v-for="(appointment, index) in afternoonAppointmentList" :key="appointment.id" style="border-top: 1px solid #EBEBEB;padding: 13px 0 13px 0" >
                        <van-row  gutter="20" type="flex" justify="space-between" align="center">
                          <van-col span="4">
                            <van-image
                              round
                              width="48px"
                              height="48px"
                              :src="appointment.avatar"
                            />
                          </van-col>
                          <van-col span="14">
                            <div class="list-name">{{appointment.patientName}}</div>
                            <div class="list-doc">预约{{$getDictItem('doctor')}}: {{appointment.doctorName}}</div>
                          </van-col>
                          <van-col span="6">
                            <div style="text-align: right">
                              <van-button size="small" style="width: 52px" round type="info" @click="signIn(2 ,appointment, index)">签到</van-button>
                            </div>
                          </van-col>
                        </van-row>
                      </div>
                      <div style="display: flex;justify-content: center;font-size: 13px;color: #999999;align-items: center"  @click="selectOpenAndClose(false)">
                        {{ isAfterOpen?'收起':'展开' }}
                        <img width="17px" height="17px" :src="isAfterOpen ? require('@/assets/my/booking_patient_footer_close.png') : require('@/assets/my/booking_patient_footer_open.png')" alt=""></div>
                    </div>
                  </div>
                  <!--          没数据-->
                  <div v-else style="padding-bottom: 30px; height: 300px">
                    <div style="width: 152px;margin:27px auto 9px auto"><img src="../../assets/noMessage.png" style="width: 100%" alt=""></div>
                    <div style="color: #999999;font-size: 15px;text-align: center">暂无预约信息</div>
                  </div>
                </van-list>
              </van-pull-refresh>
            </van-tab>
            <van-tab title="已就诊">
              <van-pull-refresh v-if="active === 1" v-model='refreshing' @refresh='onRefresh'>
                <van-list
                  v-model='loading'
                  :finished-text='morningAppointmentList.length > 0 || afternoonAppointmentList.length > 0 ? "没有更多了" : ""'
                  :finished='finished'
                  :style="{minHeight: listHeight + 'px'}"
                  @load='pageAppointmentQuery'>
                  <div  v-if="morningAppointmentList.length > 0 || afternoonAppointmentList.length > 0">
                    <!--            上午-->
                    <div style="background: #fff;padding: 16px 13px 16px 13px;" v-if="morningAppointmentList.length > 0">
                      <div style="padding-bottom: 15px;color: #666666;">上午</div>
                      <div style="border-top: 1px solid #EBEBEB;padding: 13px 0 13px 0" v-for="appointment in morningAppointmentList" :key="appointment.id">
                        <van-row  gutter="20" type="flex" justify="space-between" align="center">
                          <van-col span="4">
                            <van-image
                              round
                              width="48px"
                              height="48px"
                              :src="appointment.avatar"
                            />
                          </van-col>
                          <van-col span="14">
                            <div class="list-name">{{appointment.patientName}}</div>
                            <div class="list-doc">预约{{$getDictItem('doctor')}}: {{appointment.doctorName}}</div>
                          </van-col>
                          <van-col span="6">
                            <div class="yjz" style="text-align: right">
                              已就诊
                            </div>
                          </van-col>
                        </van-row>
                      </div>
                    </div>
                    <!--            下午-->
                    <div style="margin-top: 13px;background: #ffffff;padding: 16px 13px 16px 13px;"  v-if="afternoonAppointmentList.length > 0">
                      <div style="padding-bottom: 15px;color: #666666;">下午</div>
                      <div style="border-top: 1px solid #EBEBEB;padding: 13px 0 13px 0" v-for="appointment in afternoonAppointmentList" :key="appointment.id">
                        <van-row  gutter="20" type="flex" justify="space-between" align="center">
                          <van-col span="4">
                            <van-image
                              round
                              width="48px"
                              height="48px"
                              :src="appointment.avatar"
                            />
                          </van-col>
                          <van-col span="14">
                            <div class="list-name">{{appointment.patientName}}</div>
                            <div class="list-doc">预约{{$getDictItem('doctor')}}: {{appointment.doctorName}}</div>
                          </van-col>
                          <van-col span="6">
                            <div class="yjz" style="text-align: right">
                              已就诊
                            </div>
                          </van-col>
                        </van-row>
                      </div>
                    </div>
                  </div>
                  <!--          没数据-->
                  <div v-else style="padding-bottom: 30px; height: 300px">
                    <div style="width: 152px;margin:27px auto 9px auto"><img src="../../assets/noMessage.png" style="width: 100%" alt=""></div>
                    <div style="color: #999999;font-size: 15px;text-align: center">暂无信息</div>
                  </div>
                </van-list>
              </van-pull-refresh>
            </van-tab>
          </van-tabs>
        </div>
      </div>
    </van-sticky>
<!--    预约详细信息部分-->

<!--    就诊部分-->
<!--    签到弹窗-->
    <showDialog @showDialogs="closeDialog" :showDialog="showDialog"></showDialog>
  </div>
</template>
<script>
import Vue from 'vue'
import moment from 'moment'
import {
  Popup,
  DropdownMenu,
  DropdownItem,
  Sticky,
  Row,
  Col,
  Icon,
  Tab,
  Image,
  Tabs,
  Dialog,
  Toast,
  PullRefresh,
  Button,
  List
} from 'vant'
import { approvalNumber, appointmentPage } from '@/api/doctorApi.js'
import {statisticsWeek, statisticsDay, updateAppointment} from '@/api/appointment.js'
import showDialog from './components/signDialog'
Vue.use(DropdownMenu)
Vue.use(DropdownItem)
Vue.use(Popup)
Vue.use(Sticky)
Vue.use(Row)
Vue.use(Col)
Vue.use(Icon)
Vue.use(Tab)
Vue.use(Button)
Vue.use(List)
Vue.use(PullRefresh)
Vue.use(Tabs)
Vue.use(Image)
Vue.use(Dialog)
Vue.use(Toast)
export default {
  name: 'reservation',
  components: {
    showDialog
  },
  data () {
    return {
      isMorningOpen: true,
      isWeiXin: false,
      height: 600,
      listHeight: 300,
      isAfterOpen: true,
      nursingId: localStorage.getItem('caringNursingId'),
      firstSelectId: 'all',
      firstSelectName: '全部',
      doctorSelectId: '',
      statisticsDayData: {},
      weekName: '',
      queryWeekIndex: 0,
      queryDateIndex: 0,
      queryDateWeekIndex: 0,
      queryDate: new Date(),
      statisticsWeekData: {},
      show: false,
      showDate: true, // 点击时间后显示隐藏
      // 选中的名字
      selectName: '全部',
      doctorList: [],
      active: 0,
      weekday: ['一', '二', '三', '四', '五', '六', '日'], // 周一至周日数据
      queryDoctorId: this.$route.query.doctorId,
      queryWeek: 0,
      queryDay: moment().format('yyyy-MM-DD'),
      // 就诊状态
      queryStatus: 0,
      current: 1,
      morningAppointmentList: [],
      afternoonAppointmentList: [],
      signInItem: {},
      signInTime: 0,
      signInIndex: 0,
      loading: false,
      finished: false,
      refreshing: false,
      showDialog: false, // 签到弹窗
      approvalNumber: undefined, // 待审核数量
      showNavBar: true,
      listData: [],
      closeDialogStatus: false
    }
  },
  mounted () {
    const device = localStorage.getItem('caringCurrentDevice')
    if (device === 'weixin') {
      this.isWeiXin = true
    }
    this.height = window.innerHeight - 125
    this.listHeight = window.innerHeight - 52 - 214 - 125 - 44
    if (this.queryDoctorId) {
      this.showNavBar = false
      this.getApprovalNumber(this.queryDoctorId, this.nursingId)
    } else {
      this.getApprovalNumber('', this.nursingId)
    }
    this.statisticsWeek(this.queryDoctorId, this.queryWeek)
    this.statisticsDay(this.queryDay, this.queryDoctorId)
    this.onRefresh()
  },
  methods: {
    selectOpenAndClose (flag) {
      // flag 为true表示上午, false
      const morning = []
      const afternoon = []
      this.listData.forEach(item => {
        if (item.time === 1) {
          morning.push(item)
        } else {
          afternoon.push(item)
        }
      })
      console.log(morning)
      if (flag && morning.length > 0) {
        this.isMorningOpen = !this.isMorningOpen
        this.morningAppointmentList = this.isMorningOpen ? morning : [morning[0]]
      } else if (!flag && afternoon.length > 0) {
        this.isAfterOpen = !this.isAfterOpen
        this.afternoonAppointmentList = this.isAfterOpen ? afternoon : [afternoon[0]]
      }
    },
    // 点击搜索
    goSearchDoctor () {
      console.log(123)
      this.$router.push({
        path: '/reservation/searchDoctor'
      })
    },
    // 跳转到待审核列表
    goReviewedList () {
      this.$router.push({
        path: '/reservation/reviewedList',
        query: {
          doctorId: this.queryDoctorId
        }
      })
    },
    // 获取待审核数量
    getApprovalNumber (doctorId) {
      approvalNumber(doctorId, this.nursingId).then(res => {
        if (res.code === 0) {
          this.approvalNumber = res.data
          console.log(this.approvalNumber)
        }
      })
    },
    onBack () {
      this.$h5Close()
    },
    pageAppointmentQuery () {
      const params = {
        model: {
          appointDate: this.queryDay,
          status: this.active,
          doctorId: this.queryDoctorId,
          nursingId: this.nursingId
        },
        current: this.current,
        size: 20
      }
      appointmentPage(params).then(res => {
        console.log(res.data)
        if (res.data) {
          this.listData = res.data.records
          for (let i = 0; i < this.listData.length; i++) {
            if (this.listData[i].time === 1) {
              this.morningAppointmentList.push(this.listData[i])
            } else {
              this.afternoonAppointmentList.push(this.listData[i])
            }
          }
          if (res.data.pages === 0 || res.data.pages === this.current) {
            this.finished = true
          } else {
            this.finished = false
            this.current++
          }
        }
        this.loading = false
        this.refreshing = false
      })
    },
    /**
     * 判断日期是否是今天
     */
    isToday () {
      return this.queryDay === moment().format('yyyy-MM-DD')
    },
    /**
     * 关闭或者打开 周
     */
    openOrCloseWeek () {
      this.showDate = !this.showDate
      console.log('showDate', this.showDate)
      if (this.showDate) {
        this.queryWeekIndex = this.queryDateWeekIndex
        this.statisticsWeek(this.queryDoctorId, this.queryWeekIndex)
      }
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      this.current = 1
      this.morningAppointmentList = []
      this.afternoonAppointmentList = []
      this.loading = true
      this.pageAppointmentQuery()
    },
    /**
     * 更新日期
     */
    updateQueryDate (day) {
      this.queryDate = moment(this.queryDate).add(day, 'days').toDate()
      const week = moment(this.queryDate).day()
      // 点的上一天
      if (day === -1) {
        // 判断当前日期是否是一个周日。
        // 是周日。 week 要 -1
        if (week === 0) {
          this.queryDateWeekIndex--
          this.updateWeek(-1)
        }
      }
      if (day === 1) {
        // 判断当前日期是否是一个 周一
        // 是周一。 week 要 +1
        if (week === 1) {
          this.queryDateWeekIndex++
          this.updateWeek(1)
        }
      }
      this.queryDay = moment(this.queryDate).format('yyyy-MM-DD')
      this.statisticsDay(this.queryDay, this.queryDoctorId)
      this.onRefresh()
    },
    formatQueryDate (queryDate) {
      let localWeekName = ''
      const week = moment(this.queryDate).day()
      if (week === 0) {
        localWeekName = '周日'
      } else if (week === 1) {
        localWeekName = '周一'
      } else if (week === 2) {
        localWeekName = '周二'
      } else if (week === 3) {
        localWeekName = '周三'
      } else if (week === 4) {
        localWeekName = '周四'
      } else if (week === 5) {
        localWeekName = '周五'
      } else if (week === 6) {
        localWeekName = '周六'
      }
      this.weekName = localWeekName
      return moment(queryDate).format('MM') + '月' + moment(queryDate).format('DD') + '日'
    },
    /**
     * 更新周次
     */
    updateWeek (week) {
      this.queryWeekIndex += week
      console.log('queryWeekIndex', this.queryWeekIndex)
      this.statisticsWeek(this.queryDoctorId, this.queryWeekIndex)
    },
    /**
     * 选择的日期
     */
    selectDay (date) {
      this.queryDate = date
      this.queryDay = moment(this.queryDate).format('yyyy-MM-DD')
      this.statisticsDay(this.queryDay, this.queryDoctorId)
      this.onRefresh()
      this.queryDateWeekIndex = this.queryWeekIndex
      this.openOrCloseWeek()
    },
    /**
     * 判断date 是否和 this.queryDay 相等
     * @param date yyyy-MM-dd
     */
    dataEqual (date) {
      if (date.substring(0, 10) === this.queryDay) {
        return true
      }
      return false
    },
    /**
     * 周的统计
     */
    statisticsWeek (doctorId, week) {
      statisticsWeek(this.nursingId, doctorId, week).then(res => {
        console.log('statisticsWeek', res.data)
        this.statisticsWeekData = res.data
      })
    },
    /**
     * 日的统计
     */
    statisticsDay (day, doctorId) {
      statisticsDay(this.nursingId, doctorId, day).then(res => {
        console.log('statisticsDay', res.data)
        this.statisticsDayData = res.data
      })
    },
    /**
     * 跳转到预约设置
     */
    goReservationSetUp () {
      this.$router.push({
        path: '/reservation/reservationSetUp',
        query: {}
      })
    },
    /**
     * 切换
     */
    beforeChange () {
      this.onRefresh()
    },
    /**
     * 签到
     */
    signIn (time, item, index) {
      this.signInTime = time
      this.signInIndex = index
      this.signInItem.id = item.id
      this.showDialog = true
    },
    /**
     * 关闭签到弹窗
     * @param val false
     */
    closeDialog (val) {
      if (val === true) {
        const params = {
          id: this.signInItem.id,
          status: 1
        }
        if (this.closeDialogStatus) {
          return
        }
        this.closeDialogStatus = true
        updateAppointment(params).then(res => {
          this.closeDialogStatus = false
          if (res.code === 0) {
            this.statisticsWeek(this.queryDoctorId, this.queryWeekIndex)
            this.statisticsDay(this.queryDay, this.queryDoctorId)
            if (this.signInTime === 1) {
              this.morningAppointmentList.splice(this.signInIndex, 1)
            } else if (this.signInTime === 2) {
              this.afternoonAppointmentList.splice(this.signInIndex, 1)
            }
          }
        }).catch(res => {
          this.closeDialogStatus = false
        })
      } else {
        console.log('取消')
      }
      this.showDialog = false
    }

  }
}
</script>

<style scoped lang="less">
.top-box {
  /*display: flex;*/
  /*justify-content: space-between;*/
  padding: 48px 13px 13px 13px;
  color: #333;
  font-size: 19px;
  line-height: 1;
  background: #fff;
  text-align: center;
}
.list{
  padding: 14px 16px 18px 16px;
  border-bottom: 1px solid #F4F4F4;
}
.list-bgc{
 background: #DCE9FC;
  color: #47A6DE;
}
.box{
  text-align: center;
  //padding: 17px;
}
.box-top{
  color: #999999;
  font-size: 13px;
}
.box-bottom{
  color: #333;
  font-size: 26px;
  margin-top: 8px;
}
/deep/.van-tabs {
  .van-tabs__wrap{
    .van-tabs__nav{
      border-top: 1px solid #EBEBEB !important;
      background: #FFFFFF !important;
      .van-tab__text{
        font-weight: 700;
        font-size: 16px;
      }
    }
  }
  .van-tabs__content{
    background: #f5f5f5;
  }
}
.list-name{
  color: #333;
  margin-bottom: 6px;
  overflow: hidden; //超出的文本隐藏
  text-overflow: ellipsis; //溢出用省略号显示
  white-space: nowrap;  // 默认不换行；
}
.list-doc{
  font-size: 13px;
  color: #999;
}
.yjz{
  color: #666666;
  font-size: 13px;
}
.clickDate{
  background: #ffffff;
}
.week{
 font-size: 13px;
  color: #999;
}
.weekend{
  background: #FFF5EC !important;
}
.weekends{
  width: 40px;
  height: 40px;
  color: #666;
  background: #E7F0FF;
  text-align: center;
  line-height: 40px;
  font-size: 13px
}
/**
 日期+人数 默认样式
 */
.people-number{
  width: 40px;
  height: 40px;
  color: #666;
  background: #E7F0FF;
  text-align: center;
  font-size: 13px;
  line-height: 2.3;
}
/**
日期+人数 点击后样式背景颜色为蓝色字体白色
 */
.people-click{
  background: #3F86FF;
  div{
    color: #ffffff !important;
  }
}
/deep/.van-dropdown-menu{
  .van-dropdown-menu__bar{
    box-shadow: none !important;
  }
}
.search{
  height: 54px;
  background: linear-gradient(90deg, #3F86FF 0%, #6EA8FF 100%);
  color: #FFFFFF;
  padding: 17px 13px 0 13px ;
  margin-bottom: 38px;
}
</style>
