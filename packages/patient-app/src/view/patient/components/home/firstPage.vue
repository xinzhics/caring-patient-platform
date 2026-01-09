<template>
  <section class="content" id="content">
    <!--  轮播图  -->
    <van-swipe v-if="switchStatus === 1"  :autoplay="5000" :duration="400" indicator-color="white">
      <van-swipe-item class="my-swipe" v-for="(item, index) in bannerList" :key="index"
                      @touchstart="startTouch" @touchmove="moveTouch" @touchend="endTouch(item)">
        <van-image style="width: 100%; height: 100%" lazy-load :src="item.fileUrl ? item.fileUrl : require('@/assets/my/swiper-banner-default.jpg')" />
      </van-swipe-item>
    </van-swipe>
    <!-- 我的功能 -->
    <div class="container">
      <div class="scrollable-list" ref="scrollableList">
        <div v-for="(item,index) in patientMyFeatures"
             :style="{flexBasis: patientMyFeatures.length === 1 ? '100%' : patientMyFeatures.length === 2 ? '50%' : '33.33333%' }"
             :key="index" class="item" style="flex-shrink: 0;" @click="clickMenu(item)">
          <div class="item-box">
            <div style="display: flex;align-items: center;flex-direction: column;width: 100%"
                 v-if="item.dictItemId !== '38'">
              <div>
                <img width="30px" height="30px" :src="item.iconUrl" alt="">
              </div>
              <div class="item-name"
                   :style="{width: patientMyFeatures.length === 1 ? '250px' : patientMyFeatures.length === 2 ? '130px' : '90px'  }">
                {{ item.name }}
              </div>
            </div>
            <div
              style="display: flex; align-items: center; justify-content: center; flex-direction: column;position: relative;width: 100%; height: 100%"
              v-else>
              <div v-if="item.dictItemId === '38'">
                <img width="30px" height="30px" style="z-index: 300" :src="item.iconUrl">
              </div>
              <div v-if="item.dictItemId === '38'" class="item-name" style="z-index:300;"
                   :style="{width: patientMyFeatures.length === 1 ? '250px' : patientMyFeatures.length === 2 ? '130px' : '90px'  }">
                {{ item.name }}
              </div>
              <div v-if="item.dictItemId === '38'" style="z-index: 99999; opacity: 0;">
                <wx-open-launch-weapp
                  style="position: absolute;top: 0;left: 0;width: 100%;height: 100%"
                  v-if="item.dictItemId === '38'"
                  :id="'launch-btn' + item.id"
                  :username="item.username"
                  :path="item.appPath">
                  <script type="text/wxtag-template">
                    <style>
                      .wx-btn {
                        border: 0;
                        background-color: white;
                        color: #646566;
                        font-size: 12px;
                        line-height: 1.5;
                        word-break: break-all;
                        width: 100%;
                        height: 100%;
                        z-index: 9999;
                        opacity: 0;
                        position: absolute;
                        top: 0;
                        left: 0;

                      }
                    </style>
                    <button class="wx-btn">{{ item.name }}</button>
                  </script>
                </wx-open-launch-weapp>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!--  代办事项  -->
    <div class="deputy-event" v-show="coreFunctions.calendarStatus === 1">
      <div class="deputy-event-box">
        <div class="deputy-event-box-title">
          <div class="deputy-event-box-title-round-green">
            <div class="deputy-event-box-title-round-white"/>
          </div>
          <div class="deputy-event-box-title-name">今日待办事项</div>
        </div>

        <div class="deputy-event-box-content">
          <div v-if="deputyLoading" style="height: 100%">
            <div v-for="(item, key) in followData" :key="key" style="padding: 0px 15px;">
              <div class="deputy-event-box-content-box">
                <div style="display: flex; align-items: center;">
                  <div class="deputy-event-box-content-box-title">{{ item.name }}</div>
                  <div class="deputy-event-box-content-box-value">{{ item.remindContent }}</div>
                </div>
                <div class="deputy-event-box-content-box-btn" @click="jumpForm(item)"
                     :style="{background: item.timeOut === 1 ? '#999999' : '#67E0A7'}">
                  <!-- 用药状态的 记录 为 签到，，添加用药后，需要修改一下代码 -->
                  {{ item.timeOut === 1 ? '补记' : '记录' }}
                </div>
              </div>
            </div>
            <div v-if="followData.length === 0"
                 style="display: flex; justify-content: center; align-items: center; height: 100%">
              <div style="width: 30px; height: 1px; background: #333; margin-right: 10px;"/>
              <div style="font-size: 17px; color: #333">今日无待办内容</div>
              <div style="width: 30px; height: 1px; background: #333; margin-left: 10px;"/>
            </div>
          </div>

        </div>
      </div>
    </div>

    <div class="deputy-event">
      <div
        class="detail-section"
      >
        <h3 class="section-title-main">在线科普</h3>
        <!-- CMS 列表 -->
        <van-pull-refresh v-model="refreshingCms" @refresh="onRefreshCms" v-if="contentList.length > 0">
          <van-list
            v-model="loadingCms"
            :finished="finishedCms"
            @load="onLoadCms"
          >
            <div class="cms-list">
              <div
                v-for="cms in contentList"
                :key="cms.id"
                class="cms-item"
                @click="onCmsClick(cms)"
              >
                <div class="cms-header">
                  <div style="display: flex; align-items: center; gap: 7px; min-width: 0">
                    <span class="cms-type">{{ getCmsTypeText(cms.cmsType) }}</span>
                    <div class="cms-title">{{ cms.cmsTitle }}</div>
                  </div>

                  <van-icon name="arrow" color="#888888"/>
                </div>

                <!-- 文章内容预览 -->
                <div v-if="cms.cmsType === 'CMS_TYPE_TEXT' && cms.cmsContent" class="cms-content" style="margin-top: 8px;">
                  <div class="" v-html="cms.cmsContent"></div>
                </div>
              </div>
            </div>
          </van-list>
        </van-pull-refresh>

        <div v-if="contentList.length === 0 && !loadingCms" class="empty-state">
          <div class="empty-icon">
            <img src="../../../../assets/cms_empty_icon_.png" style="width: 160px;"/>
          </div>
          <div class="empty-text">当前暂无内容</div>
        </div>
      </div>
    </div>

    <!-- 文章 -->
<!--    <div style="width: 100%">-->
<!--      <van-tabs v-model="active" title-inactive-color="'#999999'" :duration="0.1" title-active-color="'#333333'"-->
<!--                color="#67E0A7" @change="onTabClick">-->
<!--        <van-tab title="推荐" :title-style="{fontSize: '16px', fontWeight: 'bolder'}"></van-tab>-->
<!--        <van-tab title="⽂章" :title-style="{fontSize: '16px', fontWeight: 'bolder'}"></van-tab>-->
<!--        <van-tab title="视频" :title-style="{fontSize: '16px', fontWeight: 'bolder'}"></van-tab>-->
<!--      </van-tabs>-->
<!--    </div>-->

    <!-- 文章 -->
<!--    <div class="deputy-event" style="padding-bottom: 80px; padding-top: 5px; background: #f7f7f7">-->
<!--      <div class="deputy-event-box" style="padding: 12px;">-->
<!--        <div style="text-align: center">-->
<!--          <van-loading type="spinner" v-if="cmsLoading" />-->
<!--        </div>-->
<!--        <div class="card" v-for="(item,index) in articleData" :key="index" @click="jumpCms(item)"-->
<!--             :style="{ borderBottom: articleData.length - 1 === index ? '0px solid #F5F5F5' : '1px solid #F5F5F5'}">-->
<!--          <div>-->
<!--            <div style="font-size: 17px;font-weight: 600" class="text">{{ item.title }}</div>-->
<!--            <div style="margin-top: 5px;font-size: 13px;color: #999; min-height: 20px" class="text"> {{-->
<!--                item.summary-->
<!--              }}-->
<!--            </div>-->
<!--            <div style="display: flex;margin-top: 22px">-->
<!--              <span style="font-size: 12px;color:#999;display: flex;align-items: center;margin-right: 27px">-->
<!--                <img style="margin-right: 4px" width="17px" height="15px" src="@/assets/my/cms_watch.png" alt="">-->
<!--                {{ item.hitCount ? item.hitCount : 0 }}-->
<!--              </span>-->
<!--              <span style="font-size: 12px;color:#999;display: flex;align-items: center">-->
<!--                <img style="margin-right: 4px" width="17px" height="15px" src="@/assets/my/cms_comment.png" alt="">-->
<!--                {{ item.messageNum ? item.messageNum : 0 }}-->
<!--              </span>-->

<!--            </div>-->
<!--          </div>-->
<!--          <van-image-->
<!--            style="border-radius: 6px;border: none"-->
<!--            width="141"-->
<!--            height="94"-->
<!--            lazy-load-->
<!--            :src="item.icon ? item.icon:require('@/assets/my/cms_backImg.jpg')"-->
<!--          />-->
<!--        </div>-->
<!--        <div v-if="articleData.length === 0 && !cmsLoading"-->
<!--             style="display: flex; justify-content: center; align-items: center; height: 100%">-->
<!--          <div style="width: 30px; height: 1px; background: #333; margin-right: 10px;"/>-->
<!--          <div style="font-size: 17px; color: #333">暂无数据</div>-->
<!--          <div style="width: 30px; height: 1px; background: #333; margin-left: 10px;"/>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->

  </section>
</template>

<script>
import Vue from 'vue';
import {Button, Tab, Tabs, Swipe, SwipeItem, Loading, Lazyload} from 'vant';
import { getCmsList } from '@/api/cms';
import ContentApi from "@/api/Content";
import marked from 'marked';
import wx from "weixin-js-sdk";
import {Base64} from "js-base64";

Vue.use(Tab);
Vue.use(Tabs);
Vue.use(Button);
Vue.use(Swipe);
Vue.use(SwipeItem);
Vue.use(Loading);
Vue.use(Lazyload);
export default {
  name: "firstpage",
  data() {
    return {
      active: 0,
      cmsLoading: false,
      patientMyFeatures: [],
      coreFunctions: JSON.parse(localStorage.getItem('routerData')).h5CoreFunctions,
      patientMyFile: JSON.parse(localStorage.getItem('routerData')).patientMyFile,
      followData: [],
      articleData: [],
      deputyLoading: false,
      switchStatus: 0,
      bannerList: [],
      startX: 0,
      currentX: 0,
      contentList: [],
      finishedCms: false,
      refreshingCms: false,
      loadingCms: false,
      params: {
        model: {
          releaseStatus: 1, // 只获取已发布的内容
        },
        order: "descending",
        size: 20,
        current: 1,
        sort: "id"
      }
    }
  },
  mounted() {
    this.initWx()
    this.getBannerSwitch()
    this.patientMyFeatures = []
    this.patientMyFeatures.push({
      "name": "在线咨询",
      "iconUrl": "https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/online_im.png",
      "sortValue": 1,
      "dictItemId": "16",
      "dictItemName": "在线咨询",
      "dictItemType": "IM_INDEX",
      "path": "im/index",
    })
    const routerList = JSON.parse(localStorage.getItem('routerData')).patientMyFeatures
    const list = []
    if (routerList) {
      for (let i = 0; i < routerList.length; i++) {
        if (routerList[i].dictItemId === '38') {
          const appPath = JSON.parse(routerList[i].path)
          routerList[i].username = appPath.username
          routerList[i].appPath = appPath.path
        }
        if (routerList[i].dictItemId === '2') {
          this.healthNmame = routerList[i].name
        }
        if (routerList[i].status) {
          list.push(routerList[i])
        }
      }
    }
    this.patientMyFeatures.push(...list)
    this.patientMenuFollow()
    this.getPatientHomeContent()
    const myallInfo = localStorage.getItem("myallInfo")
    const patientInfo = JSON.parse(myallInfo)
    this.params.model.doctorId = patientInfo.doctorId
    this.onRefreshCms()
  },

  methods: {
    onRefreshCms() {
      this.finishedCms = false;
      this.onLoadCms();
    },
    /**
     * 格式转换
     * @param content
     * @returns {*}
     */
    markdownToHtml(content) {
      return marked(content);
    },
    onLoadCms() {
      if (this.refreshingCms) {
        this.contentList = [];
        this.params.current = 1;
        this.refreshingCms = false;
      }
      getCmsList(this.params).then((res) => {
        if (res.data.code === 0) {
          if (this.params.current === 1) {
            this.contentList = res.data.data.records
          } else {
            this.contentList.push(...res.data.data.records)
          }
          this.loadingCms = false;
          if (res.data.data.pages <= this.params.current) {
            this.finishedCms = true;
          } else {
            this.params.current++;
          }
        }
      })
    },

    // 开始移动
    startTouch(event) {
      this.startX = event.touches[0].clientX;
    },
    // 移动结束
    moveTouch(event) {
      this.currentX = event.touches[0].clientX;
    },
    // 移动结束
    endTouch(item) {
      if (this.startX === 0 || this.currentX === 0) {
        this.jumpBanner(item)
        return
      }
      this.startX = 0
      this.currentX = 0
    },
    getCmsTypeText(cmsType) {
      switch (cmsType) {
        case 'CMS_TYPE_TEXT':
          return '文章'
        case 'CMS_TYPE_VOICE':
          return '音频'
        case 'CMS_TYPE_VIDEO':
          return '视频'
        default:
          return '未知'
      }
    },

    // CMS项点击
    onCmsClick(cms) {
      // 根据类型跳转到不同的详情页
      let routePath = '/studio/cms/textDetail';
      if (cms.cmsType === 'CMS_TYPE_VIDEO') {
        // 视频
        routePath = '/studio/cms/videoDetail';
      } else if (cms.cmsType === 'CMS_TYPE_VOICE') {
        // 视频
        routePath = '/studio/cms/voiceDetail';
      }

      this.$router.push({
        path: routePath,
        query: {
          cmsId: cms.id
        }
      })
    },

    getBannerSwitch() {
      ContentApi.getBannerSwitch({
        userRole: 'patient',
        tenantCode: Base64.decode(localStorage.getItem('headerTenant'))
      }).then(res => {
        if (res.data.data && res.data.data.length > 0) {
          this.switchStatus = res.data.data[0].switchStatus
          if (this.switchStatus === 1) {
            ContentApi.getBannerList({
              userRole: 'patient',
              tenantCode: Base64.decode(localStorage.getItem('headerTenant'))
            }).then(res => {
              this.bannerList = res.data.data
            })
          }
        }
      })
    },
    jumpCms(item) {
      const params = {
        id: item.id,
        userId: localStorage.getItem('userId'),
        roleType: 'patient'
      }
      ContentApi.channelContentWithReply(params).then((res) => {
        if (res.data.code === 0) {
          // 跳转到cms文章
          if (res.data.data && res.data.data.link) {
            window.location.href = res.data.data.link;
          } else {
            this.$router.push({path: '/cms/show', query: {id: item.id}})
          }
        }
      })
    },
    // banner图点击
    jumpBanner(item) {
      if (item.jumpType === 'noJump') {
        return
      }

      if (item.jumpType === 'web') {
        // 网址跳转
        window.location.href = item.jumpWebsite;
      } else {
        let systemFunction = JSON.parse(item.systemFunction)
        localStorage.setItem("isBannerTitle", systemFunction.name);
        localStorage.setItem("pageTitle", systemFunction.name);
        this.$router.push({
          path: systemFunction.path
        })
      }
    },
    // 点击标题
    onTabClick(name, title) {
      this.getPatientHomeContent()
    },
    // 获取文章
    getPatientHomeContent() {
      this.cmsLoading = true
      ContentApi.getPatientHomeContent(this.active === 0 ? 'recommend' : this.active === 1 ? 'article' : 'video')
        .then(res => {
          if (res.data.data) {
            this.articleData = res.data.data
          } else {
            this.articleData = []
          }
          this.cmsLoading = false
        })
    },
    // 跳转到表单
    jumpForm(item) {
      console.log('=======',item)
      // 设置名称
      let name = ''
      this.patientMyFile.forEach(pItem => {
        if (pItem.planId === item.planId) {
          name = pItem.name
        }
      })
      if (!name) {
        name = item.name
      }

      localStorage.setItem("pageTitle", name);
      // 跳转配置
      if (item.cmsContentUrl) {
        // 如果有链接。。表示跳转到外链
        // 设置已读
        ContentApi.patientSubmitCms(item.messageId, item.planDetailTimeId).then(res => {
          window.location.href = item.cmsContentUrl;
        })
      } else if (item.functionTypeEnum.code === "BLOOD_PRESSURE") {
        // "血压提醒 --- 跳转完成"
        this.$router.push({
          path: 'monitor/pressureEditor',
          query: {planDetailTimeId: item.planDetailTimeId, planId: item.planId}
        })
      } else if (item.functionTypeEnum.code === "BLOOD_SUGAR") {
        // "血糖提醒 --- 跳转完成 "
        this.$router.push({
          path: '/monitor/glucoseEditor',
          query: {planDetailTimeId: item.planDetailTimeId}
        })
      } else if (item.functionTypeEnum.code === "CUSTOM_FOLLOW_UP") {
        // "自定义随访 --- 有问题 "
        this.$router.push({
          path: '/custom/follow/' + item.planId + '/editor',
          query: {planDetailTimeId: item.planDetailTimeId, pageTitle: name}
        })
      } else if (item.functionTypeEnum.code === "REVIEW_MANAGE") {
        // "复查提醒"     -----     跳转完成
        this.$router.push({path: '/testNumber/editor', query: {planDetailTimeId: item.planDetailTimeId}})
      } else if (item.functionTypeEnum.code === "INDICATOR_MONITORING") {
        // "指标监测 ---- 跳转完成"
        this.$router.push({
          path: '/monitor/add',
          query: {planDetailTimeId: item.planDetailTimeId, id: item.planId, title: name}
        })
      } else if (item.functionTypeEnum.code === "HEALTH_LOG") {
        // "健康日志   ---  跳转完成"
        this.$router.push({
          path: '/healthCalendar/editor',
          query: {planDetailTimeId: item.planDetailTimeId}
        })
      } else if (item.functionTypeEnum.code === "MEDICATION") {
        // "用药"
        this.$router.push({
          path: '/calendar/index'
        })
      } else if (item.functionTypeEnum.code === "LEARNING_PLAN") {
        const params = {
          id: item.cmsId,
          userId: localStorage.getItem('userId'),
          roleType: 'patient'
        }
        ContentApi.channelContentWithReply(params).then((res) => {
          if (res.data.code === 0) {
            ContentApi.patientSubmitCms(item.messageId, item.planDetailTimeId).then(() => {
              // 跳转到cms文章
              if (res.data.data && res.data.data.link) {
                window.location.href = res.data.data.link;
              } else {
                this.$router.push({path: '/cms/show', query: {id: item.cmsId}})
              }
            })
          }
        })
      }
    },
    // 获取随访计划列表
    patientMenuFollow() {
      ContentApi.patientMenuFollow()
        .then(res => {
          this.deputyLoading = true
          this.followData = res.data.data
        })
    },
    /**
     * 点击菜单
     */
    clickMenu(item) {
      if (item.dictItemId === '38') {
        return
      }
      if (item.dictItemId === '9') {
        window.location.href = item.path
        return;
      }
      if (item.dictItemType === 'CUSTOM_FOLLOW_UP') {
        // 自定义随访跳转
        // window.location.href = item.path
        if (item.path && item.path.length > 0 && item.path.lastIndexOf("/") > -1) {
          const planId = item.path.substring(item.path.lastIndexOf("/") + 1)
          ContentApi.planDetail(planId).then(res => {
            console.log('planDetail', res)
            if (res.data.code === 0 && res.data.data) {
              const planDetail = res.data.data
              if (planDetail.pushType === 2) {
                if (planDetail.content) {
                  window.location.href = planDetail.content
                  return;
                }
              } else {
                console.log(item.name)
                localStorage.setItem("pageTitle", item.name);
                this.$router.push({
                  path: item.path
                })
              }
            }
          })
        }
        return;
      }
      localStorage.setItem("pageTitle", item.name);
      this.$router.push({
        path: item.path
      })
    },
    /**
     * 初始化微信js授权
     */
    initWx() {
      const params = {
        url: this.$getWxConfigSignatureUrl(),
        wxAppId: localStorage.getItem('wxAppId')
      }
      ContentApi.annoWxSignature(params).then(res => {
        if (res.data.code === 0) {
          wx.config({
            // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
            debug: false,
            // 必填，公众号的唯一标识
            appId: localStorage.getItem('wxAppId'),
            // 必填，生成签名的时间戳
            timestamp: res.data.data.timestamp,
            // 必填，生成签名的随机串
            nonceStr: res.data.data.nonceStr,
            // 必填，签名
            signature: res.data.data.signature,
            // 必填，需要使用的JS接口列表，所有JS接口列表
            jsApiList: [
              'updateAppMessageShareData',
            ],
            openTagList: ['wx-open-launch-weapp']
          });
          wx.ready(() => {
          })
          wx.error((err) => {
            console.log('wxError', err)
          })
        }
      })
    },
  }
}
</script>

<style lang="less" scoped>

.content {
  background: #F7f7f7;
  padding-bottom: 40px; /* 与底部导航栏高度一致 */
  overflow-y: auto;

  .my-swipe{
    display: flex;
    height: 180px;
    width: 100%;
  }

  .container {
    overflow: hidden;
    padding-left: 15px;

    .scrollable-list {
      display: flex;
      overflow-x: scroll;
      scroll-snap-type: x mandatory;
      margin-top: 10px;
      width: 100%;

      .item {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-basis: 33.3333%; /* 初始宽度平分 */
        scroll-snap-align: start;
        padding-top: 10px;
        box-sizing: border-box;
        white-space: nowrap;
        padding-right: 15px;

        .item-box {
          background: #FFF;
          height: 100px;
          width: 100%;
          border-radius: 10px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;

          .item-name {
            font-size: 16px;
            text-align: center;
            margin-top: 5px;
            font-weight: 600;
            color: #333333;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
      }
    }
  }

  /* 空数据状态样式 */
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80px 20px;
    text-align: center;
  }

  .empty-icon {
    margin-bottom: 5px;
  }

  .empty-text {
    height: 21px;
    font-family: PingFangSC, PingFang SC;
    font-weight: 400;
    font-size: 15px;
    color: #939393;
    line-height: 21px;
    text-align: center;
    font-style: normal;
  }


  .detail-section {
    padding: 20px 15px 20px 15px;
    background-color: #fff;
    border-radius: 10px;
  }

  .section-title-main {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 0 0 6px 0;
  }

  .section-content-text {
    font-size: 14px;
    color: #888888;
    line-height: 1.7;
    white-space: pre-wrap;
    word-break: break-all; /* 强制断行 */
    /* 或者 */
    overflow-wrap: anywhere; /* 现代浏览器推荐 */
  }

  .cms-list {
    margin-top: 10px;
  }

  .cms-item {
    border-radius: 12px;
    padding: 12px 12px;
    margin-bottom: 12px;
    border: 1px solid #E8EAF2;
  }

  .cms-header {
    display: flex;
    align-items: center;
    width: 100%;
    justify-content: space-between;
    gap:5px;
  }

  .cms-title {
    font-size: 16px;
    font-weight: 500;
    color: #1a1a1a;
    line-height: 1.4;
    -webkit-box-flex: 1;
    -webkit-flex: 1;
    flex: 1;
    min-width: 0;
    margin-top: 1px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .cms-type {
    color: #4562BA;
    border: 1px solid #4562BA;
    padding: 1px 4px;
    border-radius: 6px;
    font-size: 11px;
    white-space: nowrap;
  }

  .cms-content {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3; /* 限制显示3行 */
    overflow: hidden;
    text-overflow: ellipsis;
    max-height: calc(1.4em * 3); /* 可选：增强兼容性 */

    font-size: 14px;
    color: #666;
    line-height: 1.5;
    margin-bottom: 12px;
    overflow: hidden;
  }

  .cms-preview {
    font-size: 12px;
    color: #666;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    margin-top: 10px;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .deputy-event {
    padding: 15px 15px 0px 15px;

    .deputy-event-box {
      background: white;
      border-radius: 10px;
      padding: 12px 0px;

      .card {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 11px 0px;
      }

      .text {
        width: 171px;
        overflow: hidden;
        text-overflow: ellipsis; //文本溢出显示省略号
        white-space: nowrap
      }

      .deputy-event-box-title {
        display: flex;
        align-items: center;
        border-bottom: 2px solid #f7f7f7;
        padding: 0px 0px 8px 0px;

        .deputy-event-box-title-round-green {
          width: 16px;
          height: 16px;
          border-radius: 16px;
          background: #67E0A7;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-left: 15px
        }

        .deputy-event-box-title-round-white {
          width: 12px;
          height: 12px;
          border-radius: 11px;
          background: #fff;
          display: flex;
          align-items: center;
          justify-content: center
        }
      }

      .deputy-event-box-title-name {
        font-size: 16px;
        font-weight: 600;
        color: #333333;
        margin-left: 5px
      }

      .deputy-event-box-content {
        height: 100px;
        overflow: auto;

        .deputy-event-box-content-box {
          border-bottom: 1px solid #f5f5f5;
          height: 50px;
          display: flex;
          justify-content: space-between;
          align-items: center;

          .deputy-event-box-content-box-title {
            font-size: 13px;
            font-weight: 600;
            color: #333333;
            width: 80px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .deputy-event-box-content-box-value {
            font-size: 13px;
            color: #333333;
            margin-left: 10px;
            width: 180px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .deputy-event-box-content-box-btn {
            font-size: 11px;
            color: white;
            padding: 3px 10px;
            border-radius: 20px;
          }
        }
      }
    }
  }

  .scrollable-list::-webkit-scrollbar {
    display: none; /* Chrome Safari */
  }

}

/deep/ .van-tabs__nav {
  background: unset;
}

/deep/ .van-tabs__line {
  width: 25px;
  bottom: 18px;
}

</style>
