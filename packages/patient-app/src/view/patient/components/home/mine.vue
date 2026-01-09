<template>
  <div class="myCenter">
    <div class="headerContent" :style="{backgroundImage:'url('+h5UiImage.attribute1+')'}">

      <div style="margin-top: 100px;padding: 19px;background: #FFFFFF;border-radius: 9px;position: relative">
        <div class="headerImg">
          <img v-if="baseInfo.avatar!==''" :src="baseInfo.avatar" alt="" @click="showImage">
          <img v-if="baseInfo.avatar===''" src="@/components/arrt/images/head-portrait.png" alt="">
        </div>
        <div class="detail">
          <div style="display: flex;align-items: center;justify-content: space-between">
            <div style="display: flex;align-items: center;justify-content: space-between">
              <span class="name" style="margin-right: 23px">{{ baseInfo.name }}</span>
              <img v-if="baseInfo.sex!==null" width="7px" height="11px" style="margin-right: 14px" :src="baseInfo.sex===0?home_man:home_woman" alt="">
              <span style="color: #333333;font-size: 15px">{{ jsGetAge(baseInfo.birthday) ? jsGetAge(baseInfo.birthday) + '岁' : '' }}</span>
            </div>
            <div @click="goOthoerPage('/baseinfo/index')" class="setInfo">
              <span style="margin-left: 4px">{{$lang("langurage.update_info", '修改资料')}}</span>
            </div>
          </div>
        </div>
        <div v-if="baseInfo.mobile" style="color:#333;font-size: 15px;margin-top: 8px">{{getMobile(baseInfo.mobile)}}</div>
      </div>
<!--      系统功能-->
      <div style="font-size: 17px;color: #333;font-weight: 600;margin:19px 0 17px 0">{{$lang("langurage.follow_up_services", '随访服务')}}</div>
      <van-grid :border="false" :column-num="4" :clickable="true">
        <van-grid-item v-for="(item,index) in patientMyFile" :key="index" style="padding-right: 10px; padding-bottom: 10px; border-radius: 5px">
          <div>
            <div @click="clickMenu(item)" style="display: flex;align-items: center;flex-direction: column;margin-right: 24px;width: 100%" v-if="item.dictItemId !== '38'">
              <div>
                <img width="30px" height="30px" :src="item.iconUrl" alt="">
              </div>
              <div style="font-size: 13px;text-align: center">{{ item.name }}</div>
            </div>
            <div style="display: flex;align-items: center;flex-direction: column;margin-right: 24px;position: relative;width: 100%" v-else>
              <div v-if="item.dictItemId === '38'">
                <img width="30px" height="30px" style="z-index: 300" :src="item.iconUrl">
              </div>
              <div v-if="item.dictItemId === '38'" style="font-size: 13px;z-index:300 ">{{ item.name }}</div>
              <div v-if="item.dictItemId === '38'" style="z-index: 99999; opacity: 0">
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
        </van-grid-item>
      </van-grid>
      <!--      科普文章-->
      <div style="font-size: 17px;color: #333;font-weight: 600;margin:10px 0 17px 0">{{$lang("langurage.popular_science_articles", '随访服务')}}</div>
      <div class="cms">
        <van-tabs @click="onClick" title-active-color="#333333" title-inactive-color="#999999" color="#67E0A7" v-model="active">
          <van-tab :title="saveNmber">
            <cms_list ref="cmsList" v-if="active===0"  types="collect" style="margin-top: 15px"></cms_list>
          </van-tab>
          <van-tab :title="commentNmber">
            <cms_list ref="cmsList" v-if="active===1" types="comment" style="margin-top: 15px"></cms_list>
          </van-tab>
        </van-tabs>
      </div>
    </div>
    <van-dialog v-model:show="show" width="120px" :showConfirmButton="false" :showCancelButton="false">
      <div
        style="height: 120px; width: 120px; display: flex; justify-content: center; flex-direction: column; align-items: center">
        <van-loading type="spinner" color="#1989fa" vertical text-color="#666666">加载中...</van-loading>
      </div>
    </van-dialog>
  </div>
</template>

<script>
import Vue from 'vue'
import Api from '@/api/Content.js'
import {countPatientCms} from '@/api/cms'
import {Cell, Dialog, Grid, GridItem, Image as VanImage, ImagePreview, Skeleton, Toast,Col, Row,Tab, Tabs} from "vant";
import wx from "weixin-js-sdk";
Vue.use(Grid)
Vue.use(Tab)
Vue.use(Tabs)
Vue.use(Col)
Vue.use(Row)
Vue.use(Skeleton)
Vue.use(Dialog)
Vue.use(VanImage)
Vue.use(Cell)
Vue.use(GridItem)
Vue.use(Toast)
export default {
  name: 'Home',
  data() {
    return {
      headerImg: '',
      baseInfo: {
        birthday: ''
      },
      loading: false,
      active:0,
      home_man: require('@/assets/my/home_man.png'),
      home_woman: require('@/assets/my/home_woman.png'),
      h5UiImage: JSON.parse(localStorage.getItem('routerData')).h5UiImage,
      patientMyFile: JSON.parse(localStorage.getItem('routerData')).patientMyFile,
      show: false,
      listIndex:undefined,
      healthNmame:'',
      saveNmber: this.$lang("langurage.your_collection", '您的收藏'),  // 收藏数
      commentNmber: this.$lang("langurage.your_comment", '您的评论'), // 评论数
    }
  },
  components: {
    cms_list: () => import('./cmsList'),
  },
  watch: {
    //使用watch 监听$router的变化
    $route(to, from) {
      if (to.path === '/home' && from.path !== '/home') {
        if (localStorage.getItem('headerTenant')) {
          this.getInfo()
        }
      }
    }
  },

  created() {
    this.initWx()
    this.patientMyFile = []
    const routerList = JSON.parse(localStorage.getItem('routerData')).patientMyFile
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
    this.patientMyFile.push(...list)
    if (localStorage.getItem('headerTenant')) {
      this.getInfo()
    }
  },
  mounted() {
    this.getCountPatient()
  },
  methods: {
    onClick() {
      this.$refs.cmsList.current = 1
    },
    // 获取收藏 评论数
    getCountPatient() {
      const userId = localStorage.getItem('userId')
      countPatientCms(userId).then(res=>{
        if (res.data.code === 0) {
          if (res.data.data.collectCount && res.data.data.collectCount > 0) {
            this.saveNmber += '(' + res.data.data.collectCount + ')'
          }
          if (res.data.data.replyContentCount && res.data.data.replyContentCount > 0) {
            this.commentNmber += '(' + res.data.data.replyContentCount + ')'
          }
        }
      })
    },
    getMobile(mobile) {
      if (mobile) {
        return mobile.slice(0,3)+'******'+mobile.slice(-2)
      }
    },
    closeWXWindow() {
      wx.closeWindow()
    },
    goOthoerPage(path) {
      localStorage.setItem('pageTitle',this.healthNmame)
      this.$router.push({
        path: path
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
        // 查询菜单中的
        if (item.path && item.path.length > 0 && item.path.lastIndexOf("/") > -1) {
          let planId = undefined;
          if (item.path.indexOf('calendar') > -1) {
            const str = item.path
            const regex = /^(.*\/)([^\/]+)(\/.*)$/;
            const match = str.match(regex);
            if (match) {
              planId = match[2]
            }
          } else {
            planId = item.path.substring(item.path.lastIndexOf("/") + 1)
          }
          if (planId) {
            Api.planDetail(planId).then(res => {
              if (res.data.code === 0 && res.data.data) {
                const planDetail = res.data.data
                if (planDetail.pushType === 2) {
                  if (planDetail.content) {
                    window.location.href = planDetail.content
                  }
                } else {
                  localStorage.setItem("pageTitle", item.name);
                  this.$router.push({
                    path: item.path
                  })
                }
              }
            })
          }
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
      Api.annoWxSignature(params).then(res => {
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
    getInfo() {
      const params = {
        id: localStorage.getItem('userId')
      }
      Api.getContent(params).then((res) => {
        if (res.data.code === 0) {
          if (res.data.data) {
            if (res.data.data.isCompleteEnterGroup === 0) {
              this.$router.replace('/')
            } else {
              this.baseInfo = res.data.data
              localStorage.setItem('myallInfo', JSON.stringify(res.data.data))
            }
          } else {
            this.$router.replace('/')
          }
        }
      })
    },
    /*根据出生日期算出年龄*/
    jsGetAge(strBirthday) {
      var returnAge = '';
      var strBirthdayArr = '';
      if (strBirthday) {
        if (strBirthday.indexOf("-") != -1) {
          strBirthdayArr = strBirthday.split("-");
        } else if (strBirthday.indexOf("/") != -1) {
          strBirthdayArr = strBirthday.split("/");
        }
        var birthYear = strBirthdayArr[0];
        var birthMonth = strBirthdayArr[1];
        var birthDay = strBirthdayArr[2];

        var d = new Date();
        var nowYear = d.getFullYear();
        var nowMonth = d.getMonth() + 1;
        var nowDay = d.getDate();

        if (nowYear == birthYear) {
          returnAge = 0;//同年 则为0岁
        } else {
          var ageDiff = nowYear - birthYear; //年之差
          if (ageDiff > 0) {
            if (nowMonth == birthMonth) {
              var dayDiff = nowDay - birthDay;//日之差
              if (dayDiff < 0) {
                returnAge = ageDiff - 1;
              } else {
                returnAge = ageDiff;
              }
            } else {
              var monthDiff = nowMonth - birthMonth;//月之差
              if (monthDiff < 0) {
                returnAge = ageDiff - 1;
              } else {
                returnAge = ageDiff;
              }
            }
          } else {
            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
          }
        }
      } else {
        returnAge = ''
      }
      //如果结果为 -1 或者为 0， 则不显示年龄
      if (returnAge === -1 || returnAge === 0) {
        returnAge = ''
      }
      return returnAge;//返回周岁年龄
    },
    showImage() {
      if (this.baseInfo.avatar) {
        ImagePreview({images: [this.baseInfo.avatar], closeable: true});
      }
    }
  }
}
</script>

<style lang="less" scoped>
.myCenter {
  width: 100vw;
  height: 100vh;
  background: #FAFAFA;
  /deep/.cms{
    background: #FFFFFF;
    border-radius: 9px;
    padding: 17px;
    .van-tab{
      font-size: 15px !important;
    }
    .van-tab--active {
      font-weight: 600 !important;
    }
  }
  .headerContent {
    background-size: 100% 200px;
    background-repeat: no-repeat;
    padding: 26px 17px;
    // background: #727D94;
    overflow: hidden;
    //display: flex;
    //justify-content: space-between;
    position: relative;

    .headerImg {
      width: 94px;
      height: 94px;
      border-radius: 50%;
      //margin: 2.5rem auto 0.3rem;
      overflow: hidden;
      position: absolute;
      left: 19px;
      top: -47px;
      //border: 1px solid rgba(255, 255, 255, 0.2);

      img {
        width: 100%;
        height: 100%;
        border-radius: 50%;
        background: #fff;
        margin: 3%;
      }
    }
    .area{
      display: flex;
      width: 100%;
      overflow-x: auto;
    }

    .detail {
      text-align: left;
      color: rgba(255, 255, 255, 0.85);
      margin-top: 36px;
      .name {
        font-size: 26px;
        color: #333333;
        line-height: 1.5;
        font-weight: 600;
      }
    }

    .setInfo {
      position: absolute;
      right: 19px;
      top: 17px;
      //margin-left: 20vw;
      font-size: 12px;
      padding: 0px 13px;
      background: #ffffff;
      border-radius: 43px;
      color: #333333;
      border: 1px solid #333333;
      height: 26px;
      line-height: 26px;
      text-align: center;
      display: flex;
      align-items: center;
    }
  }
}
::-webkit-scrollbar{
  display:none;
}
/deep/ .van-grid-item__content {
  border-radius: 10px;
}
</style>
