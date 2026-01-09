<template>
  <section class="allContent">
    <x-header style="margin-bottom:0px !important" :left-options="{backText: '',preventGoBack:true}"
              @on-click-back="goBack">我的预约
      <a slot="right" @click="$router.push('/reservation/set')" class="fa fa-exist">
        <img :src="headerIcon" width='20px' alt="">
      </a>
    </x-header>
    <div class="contant">
      <div class="header">
        <div class="reviewed" @click="goReviewedList">
          <div class="leftIcon">
            <img width="22px" height="22px" src="@/assets/my/reviewed.png" alt="">
            <span style="margin-left: 7px">待审核预约</span>
          </div>
          <div class="rightNumber">
            <span style="margin-right: 4px">{{approvalNumber}}</span>
            <van-icon name="arrow"/>
          </div>
        </div>
        <div class="dateTime">
          <p>
            <span @click="getotherDate(0)"
                  style="border-right:6px solid #999;border-bottom:6px solid transparent;border-top:6px solid transparent;vertical-align: middle;display: inline-block;"></span>
            <span @click="showSelect=true" class="spanT">{{ nowDate }}</span>
            <span @click="getotherDate(1)"
                  style="border-left:6px solid #999;border-bottom:6px solid transparent;border-top:6px solid transparent;vertical-align: middle;display: inline-block;"></span>
          </p>
          <div class="selectContent" v-show="showSelect">
            <div class="selectheader">
              <div class="left" @click="getweek(0)">
                <div></div>
                <span>上周</span>
              </div>
              <div >{{weekIndex === 0 ? '本周' : getMonth()}}</div>
              <div class="right" @click="getweek(2)">
                <span>下周</span>
                <div></div>
              </div>
            </div>
            <div class="selectlist">
              <div class="selectWeek">
                <div>一</div>
                <div>二</div>
                <div>三</div>
                <div>四</div>
                <div>五</div>
                <div class="catweek">六</div>
                <div class="catweek">日</div>
              </div>
              <div style="padding:10px 0px 0px">
                <div style="height: 70px; display:flex;justify-content: space-between;font-size:15px;color:#666">
                  <div @click="selectedBtn(0,getCurrentWeek(weekinfo.mondayDay))"
                       :style="{width:'13.5%',textAlign:'center',borderRadius:'4px',background: isToday(weekinfo.mondayDay)?'#3F86FF':'#F5F5F5',padding:'5px 0px',color: isToday(weekinfo.mondayDay)?'#fff':'#666'}">
                    <div style="line-height:30px">{{ getCurrentDay(weekinfo.mondayDay) }}</div>
                    <div :style="{lineHeight:'24px',fontSize:'13px',color:isToday(weekinfo.mondayDay)?'#fff':'#999'}">
                      {{ weekinfo.mondayUserTotal }}人
                    </div>
                  </div>
                  <div @click="selectedBtn(1,getCurrentWeek(weekinfo.tuesdayDay))"
                       :style="{width:'13.5%',textAlign:'center',borderRadius:'4px',background:isToday(weekinfo.tuesdayDay)?'#3F86FF':'#F5F5F5',padding:'5px 0px',color:isToday(weekinfo.tuesdayDay)?'#fff':'#666'}">
                    <div style="line-height:30px">{{ getCurrentDay(weekinfo.tuesdayDay) }}</div>
                    <div :style="{lineHeight:'24px',fontSize:'13px',color:isToday(weekinfo.tuesdayDay)?'#fff':'#999'}">
                      {{ weekinfo.tuesdayUserTotal }}人
                    </div>
                  </div>
                  <div @click="selectedBtn(2,getCurrentWeek(weekinfo.wednesdayDay))"
                       :style="{width:'13.5%',textAlign:'center',borderRadius:'4px',background:isToday(weekinfo.wednesdayDay)?'#3F86FF':'#F5F5F5',padding:'5px 0px',color:isToday(weekinfo.wednesdayDay)?'#fff':'#666'}">
                    <div style="line-height:30px">{{ getCurrentDay(weekinfo.wednesdayDay) }}</div>
                    <div :style="{lineHeight:'24px',fontSize:'13px',color:isToday(weekinfo.wednesdayDay)?'#fff':'#999'}">
                      {{ weekinfo.wednesdayUserTotal }}人
                    </div>
                  </div>
                  <div @click="selectedBtn(3,getCurrentWeek(weekinfo.thursdayDay))"
                       :style="{width:'13.5%',textAlign:'center',borderRadius:'4px',background:isToday(weekinfo.thursdayDay)?'#3F86FF':'#F5F5F5',padding:'5px 0px',color:isToday(weekinfo.thursdayDay)?'#fff':'#666'}">
                    <div style="line-height:30px">{{ getCurrentDay(weekinfo.thursdayDay) }}</div>
                    <div :style="{lineHeight:'24px',fontSize:'13px',color:isToday(weekinfo.thursdayDay)?'#fff':'#999'}">
                      {{ weekinfo.thursdayUserTotal }}人
                    </div>
                  </div>
                  <div @click="selectedBtn(4,getCurrentWeek(weekinfo.fridayDay))"
                       :style="{width:'13.5%',textAlign:'center',borderRadius:'4px',background:isToday(weekinfo.fridayDay)?'#3F86FF':'#F5F5F5',padding:'5px 0px',color:isToday(weekinfo.fridayDay)?'#fff':'#666'}">
                    <div style="line-height:30px">{{ getCurrentDay(weekinfo.fridayDay) }}</div>
                    <div :style="{lineHeight:'24px',fontSize:'13px',color:isToday(weekinfo.fridayDay)?'#fff':'#999'}">
                      {{ weekinfo.fridayUserTotal }}人
                    </div>
                  </div>
                  <div @click="selectedBtn(5,getCurrentWeek(weekinfo.saturdayDay))"
                       :style="{width:'13.5%',textAlign:'center',borderRadius:'4px',background:isToday(weekinfo.saturdayDay)?'#3F86FF':'#F5F5F5',padding:'5px 0px',color:isToday(weekinfo.saturdayDay)?'#fff':'#666'}">
                    <div style="line-height:30px">{{ getCurrentDay(weekinfo.saturdayDay) }}</div>
                    <div :style="{lineHeight:'24px',fontSize:'13px',color:isToday(weekinfo.saturdayDay)?'#fff':'#999'}">
                      {{ weekinfo.saturdayUserTotal }}人
                    </div>
                  </div>
                  <div @click="selectedBtn(6,getCurrentWeek(weekinfo.sundayDay))"
                       :style="{width:'13.5%',textAlign:'center',borderRadius:'4px',background:isToday(weekinfo.sundayDay)?'#3F86FF':'#F5F5F5',padding:'5px 0px',color:isToday(weekinfo.sundayDay)?'#fff':'#666'}">
                    <div style="line-height:30px">{{ getCurrentDay(weekinfo.sundayDay) }}</div>
                    <div :style="{lineHeight:'24px',fontSize:'13px',color:isToday(weekinfo.sundayDay)?'#fff':'#999'}">
                      {{ weekinfo.sundayUserTotal }}人
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="showData">
          <div class="itemData">
            <div class="itemLeft">
              <p class="docs">预约人数</p>
              <p class="number" style="color:#FF6F00">{{ showData.appointmentTotal }}</p>
            </div>
            <div class="itemRight">
              <div class="item">
                <p class="docs">上午预约</p>
                <p class="number">{{ showData.appointmentMorningTotal }}</p>
              </div>
              <div class="item">
                <p class="docs">下午预约</p>
                <p class="number">{{ showData.appointmentAfternoonTotal }}</p>
              </div>
            </div>
          </div>
        </div>
        <div class="showData">
          <div class="itemData">
            <div class="itemLeft">

              <p class="number" style="color:#3F86FF">{{ showData.seeDoctorTotal }}</p>
              <p class="docs">就诊人数</p>
            </div>
            <div class="itemRight">
              <div class="item">

                <p class="number">{{ showData.seeDoctorMorningTotal }}</p>
                <p class="docs">上午就诊</p>
              </div>
              <div class="item">

                <p class="number">{{ showData.seeDoctorAfternoonTotal }}</p>
                <p class="docs">下午就诊</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="contentheader">
        <div class="tab">
          <div @click="selectTab()">
            <span :class="tabSelected?'selected':''">待就诊</span>
          </div>
          <div @click="selectTab()">
            <span :class="tabSelected?'':'selected'">已就诊</span>
          </div>
        </div>
      </div>
      <!--患者列表-->
      <div class="contentList">
        <!--上午-->
        <div style="background: #FFFFFF" v-if="list.morningData&&list.morningData.length>0">
          <group>
            <!--                  上午待就诊-->
            <cell>
              <div slot="title" style="color:#666;font-size:16px">上午</div>
            </cell>
            <cell v-for="(i,k) in list.morningData" :key="k">
              <div slot="title">
                <img :src="i.avatar" alt=""
                     style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px">
                <span
                  style="vertical-align: middle;">{{
                    doctorId === i.patientDoctorId && i.doctorRemark ? i.patientName + '(' + i.doctorRemark + ')' : i.patientName
                  }}</span>
              </div>
              <span
                v-if="tabSelected"
                style="padding:2px 12px;background:#3F86FF;color:#fff;border-radius:30px;"
                @click="myselectCont=i;showOne = true">签到</span>
              <span v-else style="color:#999">已就诊</span>
            </cell>
          </group>
          <div @click="selectOpenAndClose(true)"
            style="border-top: 1px solid #EBEBEB;background: white; height: 43px; display: flex; justify-content: center; align-items: center; font-size: 13px; color: #999;margin:0px 16px">
            <span>{{list.isMorningOpen ? '展开' : '收起'}}</span>
            <img width="17px" height="17px"
                 :src="list.isMorningOpen ? require('@/assets/my/booking_patient_footer_open.png') : require('@/assets/my/booking_patient_footer_close.png')">
          </div>
        </div>
        <!--下午-->
        <div style="background: #FFFFFF;margin-bottom: 80px" v-if="list.afterData&&list.afterData.length>0">
          <group>
            <cell>
              <div slot="title" style="color:#666;font-size:16px">下午</div>
            </cell>
            <!--下午待就诊-->
            <cell v-for="(i,k) in list.afterData" :key="k+'a'">
              <div slot="title">
                <img :src="i.avatar" alt=""
                     style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px">
                <span
                  style="vertical-align: middle;">{{
                    doctorId === i.patientDoctorId && i.doctorRemark ? i.patientName + '(' + i.doctorRemark + ')' : i.patientName
                  }}</span>
              </div>
              <span
                v-if="tabSelected"
                style="padding:2px 12px;background:#3F86FF;color:#fff;border-radius:30px;"
                @click="myselectCont=i;showOne = true">签到</span>
              <span v-else style="color:#999">已就诊</span>
            </cell>
          </group>
          <div @click="selectOpenAndClose(false)"
               style="border-top: 1px solid #EBEBEB;background: white; height: 43px; display: flex; justify-content: center; align-items: center; font-size: 13px; color: #999;margin:0px 16px">
            <span>{{list.isAfterOpen ? '展开' : '收起'}}</span>
            <img width="17px" height="17px"
                 :src="list.isAfterOpen ? require('@/assets/my/booking_patient_footer_open.png') : require('@/assets/my/booking_patient_footer_close.png')">
          </div>
        </div>
        <div v-if="showNoDataImg&&list.morningData.length<1&&list.afterData.length<1" style="width: 152px;margin: 87px auto 0">
          <img width="152px" height="152px" :src="noDataImg" alt="">
          <div style="margin-top: 7px;color: #999999;text-align: center;font-size: 15px">暂无预约信息</div>
        </div>
      </div>
    </div>

    <van-dialog v-model="showOne" :show-confirm-button="false" class="dialog-demo" hide-on-blur
                :dialog-style="{borderRadius:'10px',width:'70%'}">
      <div style="margin:0px;line-height:30px;text-align:center;padding-top:20px">
        <img :src="warn" alt="" width="60px">
      </div>
      <div style="margin-bottom:10px">
        <p style="margin:0px;line-height:26px;text-align:center;color:#666">确定将该会员</p>
        <p style="margin:0px;line-height:26px;text-align:center;color:#666">预约状态改为“已就诊”吗？</p>
      </div>
      <div style="border-top:1px solid #EBEBEB;display: flex;justify-content: space-between;">
        <p
          style="padding:8px 0px;line-height:30px;text-align:center;color:#333;width:50%;border-right:1px solid #EBEBEB"
          @click="showOne=false">取消</p>
        <p style="padding:8px 0px;line-height:30px;text-align:center;color:#3F86FF;width:50%" @click="putStatus()">
          确定</p>
      </div>
    </van-dialog>
  </section>
</template>
<script>
import {Drawer, Swiper, SwiperItem} from 'vux'
import Api from '@/api/Content.js'
import Vue from 'vue'
import {Dialog} from 'vant';
import moment from "moment";

Vue.use(Dialog)
export default {
  components: {
    Swiper,
    SwiperItem,
    Drawer
  },
  data() {
    return {
      tabSelected: true,
      noDataImg: require('@/assets/my/no_data.png'),
      headerImg: require('@/assets/my/doctor_avatar.png'),
      headerIcon: require('@/assets/drawable-xhdpi/setTab.png'),
      list: {
        morningData: [],
        afterData: [],
        isMorningOpen: false,
        isAfterOpen: false,
        tempMorningData: [],
        tempAfterData: [],
      },
      weekIndex: 0,
      ifshow: true,
      myselectedIndex: 0,
      showSelect: false,
      nowDate: '',
      current: 1,
      showData: {},
      weekinfo: {},
      dateDot: 0,
      showOne: false,
      warn: require('@/assets/drawable-xhdpi/warn.png'),
      myselectCont: {},
      doctorId: localStorage.getItem('caring_doctor_id'),
      approvalNumber: 0,
      appointDate: '',
      showNoDataImg:false,
      currentDay: ''
    }
  },
  mounted() {
    this.getthisDate()
    this.getweek(1)
    this.getApprovalNumber()
  },
  methods: {
    getMonth() {
      return moment(this.currentDay).format('MM') + '月'
    },
    goBack() {
      this.$router.replace('/index')
    },
    // 展开或者收起弹窗
    selectOpenAndClose(flag) {
      //flag 为true表示上午, false
      if (flag) {
        this.list.isMorningOpen = !this.list.isMorningOpen
        this.list.morningData = this.list.isMorningOpen ? [this.list.tempMorningData[0]] : this.list.tempMorningData
      }else {
        this.list.isAfterOpen = !this.list.isAfterOpen
        this.list.afterData = this.list.isAfterOpen ? [this.list.tempAfterData[0]] : this.list.tempAfterData
      }
    },
    // 患者状态切换
    selectTab() {
      this.tabSelected = !this.tabSelected
      this.current = 1
      this.getpatientStatus(this.appointDate)
    },
    goReviewedList() {
      this.$router.push({
        path: '/reservation/reviewedList'
      })
    },
    /**
     * 【精准预约】 医助统计待审核预约数量
     */
    getApprovalNumber() {
        Api.approvalNumber(this.doctorId).then(res=>{
          console.log(res)
          if (res.data.code === 0) {
            this.approvalNumber = res.data.data
          }
        })
    },
    getotherDate(i) {
      let day = ''
      if (i === 1) {
        // 当前日期往后1天
        day = moment(this.currentDay).add(1, 'days').format('YYYY-MM-DD');
      }else {
        // 当前日期往前1天
        day = moment(this.currentDay).subtract(1, 'days').format('YYYY-MM-DD')
      }
      this.currentDay = day
      let weekData = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      let index = moment(this.currentDay).format('d')
      this.nowDate = moment(this.currentDay).format('MM月DD日') + '  ' + weekData[index]
      this.getData(this.currentDay)
    },
    getweek(index) {
      if (index === 1) {
        // 本周
        this.weekIndex = 0
      }else if (index === 2) {
        // 下周
        this.weekIndex += 1
      }else{
        // 上周
        this.weekIndex -= 1
      }
      let params = {
        doctorId: localStorage.getItem('caring_doctor_id'),
        week: this.weekIndex,
      }
      Api.getappointmentweek(params).then((res) => {
        if (res.data.code === 0) {
          this.weekinfo = res.data.data
        }
      })
    },
    getCurrentDay(time) {
      return moment(time).format('DD')
    },
    getCurrentWeek(time) {
      return moment(time).format("YYYY-MM-DD")
    },
    isToday(time) {
      return moment(time).format("YYYY-MM-DD") === this.currentDay
    },
    getthisDate() {
      let now = new Date()
      now.setTime(now.getTime());
      if (now.getDay() === 0) {
        this.myselectedIndex = 6 + 7
      } else if (now.getDay() === 6) {
        this.myselectedIndex = 0 + 7
      } else {
        this.myselectedIndex = (now.getDay() - 1) + 7
      }
      const weekData = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      this.nowDate = moment(now).format('MM月DD日') + '  ' + weekData[now.getDay()];

      let mon = Number(now.getMonth()) + 1;
      let day = now.getDate()
      if (mon < 10) {
        mon = '0' + mon
      }
      if (day < 10) {
        day = '0' + day
      }
      this.getData(now.getFullYear() + "-" + mon + "-" + day)
      // this.getpatientStatus(now.getFullYear() + "-" + mon + "-" + day)
    },
    selectedBtn(i, n) {
      this.myselectedIndex = i
      this.currentDay = n
      this.getData(n)
      // this.getpatientStatus(n)
      let now = new Date(n)
      let weekData = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      this.nowDate =  moment(now).format('MM月DD日') + '  ' + weekData[now.getDay()];
      this.showSelect = false
    },
    getData(n) {
      this.currentDay = n
      const params = {
        doctorId: localStorage.getItem('caring_doctor_id'),
        day: n
      }
      Api.getappointment(params).then((res) => {
        if (res.data.code === 0) {
          // console.log(res)
          this.showData = res.data.data
          this.getpatientStatus(n)
        }
      })
    },
    getpatientStatus(n) {
      this.appointDate = n
      const params = {
        "current": this.current,
        "map": {},
        "model": {
          "appointDate": this.appointDate,
          "status": this.tabSelected ? 0 : 1,
          "doctorId": localStorage.getItem('caring_doctor_id')
        },
        "order": "descending",
        "size": this.showData.appointmentTotal,
        "sort": "id"
      }
      console.log(this.list)
      Api.doctorGetpatient(params).then((res) => {
        if (res.data.code === 0) {
          this.showNoDataImg=true
          this.list.tempMorningData = []
          this.list.tempAfterData = []
          res.data.data.records.forEach(element => {
            if (element.time === 1) {
              this.list.tempMorningData.push(element)
            } else if (element.time === 2) {
              this.list.tempAfterData.push(element)
            }
          });
          this.list.morningData = this.list.tempMorningData
          this.list.afterData = this.list.tempAfterData
          this.list.isMorningOpen = false
          this.list.isAfterOpen = false
        }
      })
    },
    putStatus(i) {
      // return
      const params = {
        "id": this.myselectCont.id,
        "appointDate": this.myselectCont.appointDate,
        "doctorId": localStorage.getItem('caring_doctor_id'),
        "patientId": this.myselectCont.patientId,
        "status": 1
      }
      Api.putappointmentStatus(params).then((res) => {
        this.getpatientStatus(this.myselectCont.appointDate)
        this.getData(this.myselectCont.appointDate)
        this.$vux.toast.text('修改成功', 'cneter');
        this.showOne = false
      })
    }
  }
}
</script>
<style lang="less" scoped>
.allContent {
  background: #f5f5f5;
  min-height: 100vh;
  .contant {
    .header {
      padding: 0;
      border-bottom: 1px solid rgba(102, 102, 102, 0.1);
      background: #fff;

      .reviewed {
        display: flex;
        align-items: center;
        justify-content: space-between;
        background: linear-gradient(90deg, #3F86FF 0%, #6EA8FF 100%);
        padding: 15px 11px;
        color: #FFFFFF;
        margin-bottom: 18px;

        .leftIcon {
          display: flex;
          align-items: center;
        }

        .rightNumber {
          display: flex;
          align-items: center;
        }
      }

      .dateTime {
        position: relative;

        p {
          text-align: center;

          .spanT {
            font-size: 14px;
            background: #F5F5F5;
            padding: 2px 20px;
            border-radius: 30px;
          }
        }

        .selectContent {
          position: absolute;
          top: 35px;
          left: 0;
          width: 100vw;
          height: 88.5vh;
          background: rgba(0, 0, 0, 0.3);
          border-top: 1px solid #d9d9d9;
          z-index: 99;

          .selectheader {
            background: #fff;
            padding: 0px 10px;
            line-height: 40px;
            display: flex;
            justify-content: space-between;
            font-size: 13px;

            .left {
              color: #999;

              div {
                border-right: 6px solid #999;
                border-bottom: 6px solid transparent;
                border-top: 6px solid transparent;
                vertical-align: middle;
                display: inline-block;
              }

              span {
                vertical-align: middle;
              }
            }

            .right {
              color: #999;

              div {
                border-left: 6px solid #999;
                border-bottom: 6px solid transparent;
                border-top: 6px solid transparent;
                vertical-align: middle;
                display: inline-block;
              }

              span {
                vertical-align: middle;
              }
            }
          }

          .selectlist {
            background: #fff;
            padding: 10px;

            .selectWeek {
              display: flex;
              justify-content: space-between;

              div {
                width: 13.5%;
                background: rgba(63, 134, 255, 0.3);
                // opacity: 0.6;
                text-align: center;
                // border-radius: 4px;
                color: #666666;
                font-size: 13px;
                line-height: 24px;
              }

              .catweek {
                background: rgba(255, 190, 139, 0.3);
              }
            }
          }
        }
      }

      .showData {
        padding: 0px 10px;
        margin: 20px 0px;

        .itemData {
          border-radius: 10px;
          background: #f5f5f5;
          display: flex;
          justify-content: space-between;
          padding: 10px 20px;
          text-align: center;

          .itemLeft {
            width: 35%;
            border-right: 1px solid #eee;

            .docs {
              font-size: 14px;
              color: #999;
              line-height: 30px;
            }

            .number {
              font-size: 18px;
            }
          }

          .itemRight {
            width: 65%;
            display: flex;
            justify-content: space-between;

            .item {
              width: 50%;

              .docs {
                font-size: 14px;
                color: #999;
                line-height: 30px;
              }

              .number {
                font-size: 18px;
              }
            }
          }
        }

      }
    }

    .contentheader {
      background: #fff;
      margin-bottom: 10px;

      .tab {
        display: flex;
        justify-content: space-between;
        padding: 10px 20vw;
        text-align: center;

        div {
          width: 50%;
          color: #999999;
          border-bottom: 2px solid #fff;

          span {
            padding-bottom: 5px;
            font-size: 15px;
          }

          .selected {
            color: #333333;
            font-size: 17px;
            border-bottom: 2px solid #3F86FF;
          }
        }
      }
    }

    /deep/.contentList {
      .weui-cell:before{
        right: 15px !important;
      }
      .weui-cells:after{
        border: none!important;
      }
    }
  }
}
</style>
