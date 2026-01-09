<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" rightText="备注" :showRightText="true" :title="$getDictItem('patient') + '详情'" @onBack="back" @showpop="$router.push('/patient/update/remark')" ></headNavigation>
    </van-sticky>
    <div class="myCenter">
      <div class="headerContent" :style="{backgroundImage:'url('+h5UiImage.attribute1+')'}">
        <div style="display: flex;align-items: center">
          <div class="headerImg">
            <img v-if="baseInfo.avatar!==''" :src="baseInfo.avatar" alt="" @click="showImage">
            <img v-if="baseInfo.avatar===''" :src="require('@/assets/avatar/head-portrait.png')" alt="">
          </div>
          <div class="detail">
            <div style="display: flex;align-items: center;justify-content: space-between">
              <p class="name">{{ baseInfo.name }}</p>
              <div v-if="baseInfo.remark" style="font-size: 13px; color: #333333; margin-left: 5px">{{'('+baseInfo.remark+')'}}
              </div>
              <div v-if="patientBaseInfoMenu.patientMenuNursingStatus" @click="gridItemClick(patientBaseInfoMenu)" class="setInfo" >
                <img width="13px" height="13px" src="@/assets/my/set.png" alt="">
                <span style="margin-left: 4px">修改资料</span>
              </div>
            </div>
            <p class="sexAge">{{ baseInfo.sex == 0 ? '男性' : baseInfo.sex == 1 ? '女性' : '' }}
              {{ (baseInfo.sex == 0 || baseInfo.sex == 1) && jsGetAge(baseInfo.birthday) ? '|' : '' }}
              {{ jsGetAge(baseInfo.birthday) ? jsGetAge(baseInfo.birthday) + '岁' : '' }}</p>
          </div>
        </div>
        <!--    与医生沟通-->
        <div v-if="coreFunctions.imStatus === 1" class="askDoc" :style="{background:goImStyle()}">
          <div class="label">
            <img width="24px" height="24px" src="@/assets/my/goIm.png" alt="">
            <span style="margin-left: 2px">{{ coreFunctions.imCopywriting }}</span>
          </div>
          <div class="goPage" @click="goIm()">
            {{ coreFunctions.imButtonText }}
            <van-icon name="arrow"/>
          </div>
        </div>
        <!--      随访卡片-->
        <div v-if="coreFunctions.imStatus!==1" class="followUp">
          <div v-if="show" style="padding-bottom: 21px">
            <van-skeleton title :row="5"/>
          </div>

          <div v-if="!show">
            <div style="display: flex;justify-content: space-between">
              <div>{{ getTime() }}</div>
              <div>今日待完成：<span
                style="color: #337EFF">{{ followList.followCount ? followList.followCount + '项' : '0项' }}</span></div>
            </div>
            <div style="display: flex;justify-content: space-between;margin-top: 21px">
              <div ref="box" style="margin-right: 12px;width: 100%;max-height: 150px;overflow: auto">
                <div v-if="followList.followItems && followList.followItems.length > 0" v-for="(item,index) in followList.followItems" :key="index"
                     :id="'item' + index"
                     :class="item.timeOut===1?'timeOut':''"    style="font-size: 13px;display: flex;justify-content: space-between;;margin-bottom: 17px;padding: 8px 13px 8px 13px;background: #F5F5F5;border-radius: 7px">
                  <div>{{ followDateTime(item.followDateTime) }}</div>
                  <div>{{ item.name }}</div>
                </div>
                <div v-if="!followList.followItems">
                  <van-image
                    width="100%"
                    height="146px"
                    fit="fill"
                    :src="finishImg"
                  />
                </div>
              </div>
              <div style="display: flex;flex-direction: column;align-items: center">
                <van-image width="100px" style="border-radius: 7px" height="100px"
                           @click="goIm()"
                           :src="!followList.doctorAvatar?docImg:followList.doctorAvatar">
                  <template v-slot:loading>
                    <van-loading v-if="loading" type="spinner" size="20"/>
                  </template>
                </van-image>
                <div
                  @click="goIm()"
                  style="margin: 12px 0 21px 0;background: #337EFF;color: #fff;padding: 5px 10px;border-radius: 43px;font-size: 13px">
                  在线沟通
                </div>
              </div>
            </div>
          </div>
        </div>
        <!--      主要功能列表-->
        <div class="mainArea" v-if="patientMyFeatures.length>0">
          <van-row style="width: 100%" type="flex" :justify="patientMyFeatures.length<=4?'space-around':'start'">
            <van-col @click="gridItemClick(item)" v-for="(item,index) in patientMyFeatures" :key="index" :span="getSpanNumber()">
              <div style="display: flex;flex-direction: column;align-items: center;justify-content: center">
                <div style="display: flex;justify-content: center;margin-top: 26px">
                  <img width="43px" height="43px" :src="item.iconUrl" alt="">
                </div>
                <div style="color: #666666;font-size: 14px;margin-top: 6px">
                  {{ item.name }}
                </div>
                <div
                  style="width: 17vw;padding: 2px 2px;color: #999999;font-size: 12px;background: #F5F5F5;border-radius: 43px;margin-top: 6px">
                  {{ getNumber(item, index) }}
                </div>
              </div>

            </van-col>
          </van-row>
        </div>
        <!--      其他功能-->
        <div class="otherArea" v-if="patientMyFile.length>0">
          <van-cell @click="gridItemClick(item)" v-for="(item,index) in patientMyFile" :key="index" is-link>
            <!-- 使用 title 插槽来自定义标题 -->
            <template slot="title">
              <div style="display: flex;align-items: center" v-if="item.dictItemId !== '38'">
                <img width="18px" height="18px" :src="item.iconUrl" alt="">
                <span style="color: #666666;margin-left: 11px ">{{ item.name }}</span>
              </div>
              <div v-else style="display: flex;align-items: center">
                <img width="18px" height="18px" style="z-index: 300" :src="item.iconUrl" class="van-icon__image">
              </div>
            </template>
          </van-cell>
        </div>
      </div>
  </div>
  </div>
</template>

<script>
import Vue from 'vue'
import { patientDetail, getPatientNewHomeRouter, patientMenuFollow } from '@/api/patient.js'
import { planDetail } from '@/api/plan.js'
import { jsGetAge } from '@/utils/age.js'
import moment from 'moment'
import '@vant/touch-emulator'
import {ImagePreview, Cell, Loading, Grid, GridItem, Image, Sticky, Skeleton, Toast, Row, Col, Icon} from 'vant'
Vue.use(Grid)
Vue.use(GridItem)
Vue.use(Sticky)
Vue.use(Cell)
Vue.use(Loading)
Vue.use(Row)
Vue.use(Image)
Vue.use(Skeleton)
Vue.use(Toast)
Vue.use(Icon)
Vue.use(Col)
Vue.use(ImagePreview)
Vue.use(Grid)
Vue.use(Col)
Vue.use(Row)
Vue.use(GridItem)
export default {
  name: 'Home',
  data () {
    return {
      backUrl: this.$route.query.backUrl,
      baseInfo: {
        birthday: ''
      },
      loading: true,
      followList: [],
      finishImg: require('@/assets/my/finish.png'),
      docImg: require('@/assets/my/morenDoc.png'),
      patientBaseInfoMenu: {},
      h5UiImage: {},
      coreFunctions: {},
      patientMyFeatures: [],
      patientMyFile: [],
      show: false,
      listIndex: undefined
    }
  },
  created () {
    const patientId = this.$route.query.patientId
    if (this.$route.query.backUrl) {
      localStorage.setItem('backUrl', this.$route.query.backUrl)
    } else {
      this.backUrl = localStorage.getItem('backUrl')
    }
    if (patientId) {
      localStorage.setItem('patientId', patientId)
    }
    this.getInfo()
    this.getPatientHome()
  },
  methods: {
    // 咨询医生背景颜色
    goImStyle () {
      return `linear-gradient(to right,${this.coreFunctions.imButtonStyleLeft},${this.coreFunctions.imButtonStyleRight})`
    },
    goIm () {
      this.$h5Close()
    },
    followDateTime (time) {
      return time.slice(0, time.length - 3)
    },
    getTime () {
      moment.locale('zh-cn', {
        weekdays: '星期日_星期一_星期二_星期三_星期四_星期五_星期六'.split('_')
      })
      return moment().format('YYYY年MM月DD日  dddd')
    },
    getInfo () {
      patientDetail(localStorage.getItem('patientId')).then((res) => {
        if (res.code === 0) {
          this.baseInfo = res.data
          localStorage.setItem('patientBaseInfo', JSON.stringify(res.data))
          this.getPatientMenuFollow(this.baseInfo.id, this.baseInfo.doctorId)
        }
      })
    },
    getSpanNumber () {
      if (this.patientMyFeatures.length > 4) {
        return 6
      } else {
        return 24 / this.patientMyFeatures.length
      }
    },
    getNumber (item, index) {
      const menuDataCountList = this.followList.menuDataCountList
      if (menuDataCountList) {
        let dataCount = 0
        for (let i = 0; i < menuDataCountList.length; i++) {
          if (menuDataCountList[i].menuId === item.id) {
            dataCount = menuDataCountList[i].dataCount
            break
          }
        }
        if (item.dictItemType === 'MEDICINE') {
          return dataCount > 0 ? dataCount + '款药品' : '暂无药品'
        } else if (item.dictItemType === 'RESERVATION_INDEX') {
          return dataCount > 0 ? dataCount + '条预约' : '暂无预约'
        } else if (item.dictItemType === 'FOLLOW_UP') {
          return dataCount > 0 ? dataCount + '条记录' : '暂无记录'
        } else {
          return dataCount > 0 ? dataCount + '条提醒' : '暂无提醒'
        }
      } else {
        return '暂无提醒'
      }
    },
    getPatientMenuFollow (patientId, doctorId) {
      this.show = true
      this.loading = true
      patientMenuFollow(doctorId, patientId).then(res => {
        console.log(res)
        this.show = false
        this.loading = false
        if (res.code === 0) {
          this.followList = res.data
          if (this.coreFunctions.calendarStatus === 1 && this.followList.followItems) {
            this.listIndex = this.followList.followItems.findIndex(item => item.timeOut === 0)
            this.$nextTick(() => {
              const boxElement = this.$refs.box
              if (this.followList.followItems.length > 3) {
                if (this.followList.followItems.length - this.listIndex <= 2) {
                  boxElement.scrollTop = 41 * this.listIndex
                } else {
                  boxElement.scrollTop = 50 * this.listIndex
                }
              }
            })
          }
        }
      })
    },
    getPatientHome () {
      getPatientNewHomeRouter().then(res => {
        if (res.code === 0) {
          const dataMenu = res.data
          console.log('getPatientHome', dataMenu)
          this.h5UiImage = dataMenu.h5UiImage
          if (dataMenu.patientBaseInfo) {
            this.patientBaseInfoMenu = dataMenu.patientBaseInfo
          }
          this.coreFunctions = dataMenu.h5CoreFunctions
          if (dataMenu.patientMyFeatures) {
            this.patientMyFeatures = dataMenu.patientMyFeatures
          }
          if (dataMenu.patientMyFile) {
            this.patientMyFile = dataMenu.patientMyFile
          }
        }
      })
    },
    jsGetAge (datetime) {
      return jsGetAge(datetime)
    },
    /**
     * 去患者的各个页面
     */
    gridItemClick (item) {
      localStorage.setItem('pageTitle', item.name)
      console.log('item', item)
      const path = '/patient/' + item.path
      // 如果是去健康日志，复查提醒， 需要传一些参数
      // 健康日志
      if (item.dictItemId === '5') {
        this.$router.push({
          path: path,
          query: {
            planType: 5
          }
        })
      } else if (item.dictItemId === '3') {
        this.$router.push({
          path: path,
          query: {
            planType: 3
          }
        })
      } else if (item.dictItemType === 'OTHER') {
        if (item.path.startsWith('http')) {
          // 头部有http协议，不需要拼接
          this.$previewWeiXinCms(item.path)
        } else {
          this.$previewWeiXinCms('http://' + item.path)
        }
      } else if (item.dictItemType === 'CUSTOM_FOLLOW_UP') {
        // 查询菜单中的
        // window.location.href = item.path
        if (item.path && item.path.length > 0 && item.path.lastIndexOf('/') > -1) {
          let planId = item.path.substring(item.path.lastIndexOf('/') + 1)
          if (planId === 'calendar') {
            // 注射日历
            planId = item.path.substring(0, item.path.lastIndexOf('/'))
            planId = planId.substring(planId.lastIndexOf('/') + 1)
            this.$router.push({
              path: '/patient/injectionCalendar',
              query: { planId: planId }
            })
          } else {
            planDetail(planId).then(res => {
              if (res.code === 0 && res.data) {
                const planDetail = res.data
                if (planDetail.pushType === 2) {
                  if (planDetail.content) {
                    this.$previewWeiXinCms(planDetail.content)
                  }
                } else {
                  this.$router.push({
                    path: item.path
                  })
                }
              }
            })
          }
        }
      } else {
        this.$router.push({
          path: path
        })
      }
    },
    showImage () {
      if (this.baseInfo.avatar) {
        ImagePreview({images: [this.baseInfo.avatar], closeable: true})
      }
    },
    /**
     * 页面上的返回
     */
    back () {
      if (this.backUrl) {
        this.$router.replace({
          path: this.backUrl
        })
      } else {
        // 返回到app去
        this.$h5Close(new Date().getTime())
      }
    }
  }
}
</script>

<style lang="less" scoped>
  .myCenter {
    width: 100%;
    height: 100vh;
    background: #FAFAFA;
    .timeOut{
      color: #F5222D !important;
    }
    p{
      margin: 0;
      padding: 0;
    }
    .headerContent {
      background-size: 100% 200px;
      background-repeat: no-repeat;
      padding: 26px 17px;
      overflow: hidden;
      position: relative;

      .headerImg {
        width: 4.5rem;
        height: 4.5rem;
        border-radius: 50%;
        overflow: hidden;
        margin-right: 13px;

        img {
          width: 94%;
          height: 94%;
          border-radius: 50%;
          background: #fff;
          margin: 3%;
        }
      }

      .followUp {
        margin-top: 17px;
        border-radius: 13px;
        box-shadow: 0px 1px 7px 1px rgba(0, 0, 0, 0.04);
        padding: 21px 17px 0px 17px;
        background-image: url("../../assets/my/followBj.png");
        background-size: cover;
        color: #666666;
        font-size: 14px;
      }

      .mainArea {
        text-align: center;
        margin-top: 17px;
        border-radius: 13px;
        padding: 0 9px 9px 9px;
        box-shadow: 0px 1px 7px 1px rgba(0, 0, 0, 0.04);
        display: flex;
        justify-content: space-around;
        flex-wrap: wrap;
        background: #fff;
      }

      .otherArea {
        background: #fff;
        border-radius: 13px;
        box-shadow: 0px 1px 7px 1px rgba(0, 0, 0, 0.04);
        margin-top: 17px;
        border-radius: 13px;
        box-shadow: 0px 1px 7px 1px rgba(0, 0, 0, 0.04);
        padding: 2px;
      }

      .detail {
        text-align: left;
        color: rgba(255, 255, 255, 0.85);

        .name {
          font-size: 26px;
          color: #333333;
          line-height: 1.5;
        }

        .sexAge {
          line-height: 1.5;
          font-size: 13px;
          color: #666666;
        }
      }

      .setInfo {
        position: absolute;
        right: 17px;
        //margin-left: 20vw;
        font-size: 12px;
        padding: 0px 13px;
        background: #ffffff;
        border-radius: 43px;
        color: #666666;
        height: 26px;
        line-height: 26px;
        text-align: center;
        display: flex;
        align-items: center;
      }

      //  健康档案未完成
      .askDoc {
        margin-top: 26px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px 17px;
        border-radius: 13px;
        color: #fff;
        font-size: 17px;
        font-weight: 500;

        .label {
          display: flex;
          align-items: center;
        }

        .goPage {
          font-size: 13px;
          color: #2CCDBF;
          padding: 5px 11px;
          background: #fff;
          border-radius: 43px;
        }
      }
    }

    .content {
      background: #fff;
      width: 80vw;
      position: absolute;
      left: 5%;
      top: 30vh;
      border-radius: 1rem;
      padding: 2rem 5vw;

      .weui-grids {
        &::before {
          border: none !important;
        }

        .weui-grid {
          &::after {
            border: none !important;
          }
        }
      }
    }
  }
  ::-webkit-scrollbar{
    display:none;
  }
</style>
