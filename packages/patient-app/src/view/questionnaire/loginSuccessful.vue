<template>
  <div style="height: 100vh; background: #F8F8F8; position: relative; width: 100vw">
    <navBar :isGroup="true" pageTitle="注册成功"></navBar>
    <div class="mian-box">
      <div
        style="display: flex; align-items: center; justify-content: center; flex-direction: column; padding: 13px 0px;">
        <img width="130px" src="@/assets/my/loginSuccess.png" alt="">
        <div >
          <div style="font-size: 22px;color: #333333; font-weight: bolder; text-align: center;">
            注册成功！
          </div>
          <div style="font-size: 14px;color: #797979; margin-top: 10px;">您可以选择以下操作，将信息补充完整</div>
        </div>
      </div>

      <div class="whiteBox">
        <div class="healthBox" v-if="healthNmame">
          <div style="display: flex; align-items: center">
            <img width="50px" src="@/assets/my/loginSuccessHealth.png" alt="">
            <div style="margin-left: 10px">
              <div style="font-weight: 600; font-size: 15px; color: #343434; text-align: left">{{ healthNmame }}</div>
              <div style="color: #989898; font-size: 12px;">
                补充{{ healthNmame }}让{{ this.$getDictItem('doctor') }}更好的认识您
              </div>
            </div>
          </div>

          <div class="improveBtn" @click="goHealth">完善</div>
        </div>

        <div style="width: 100%; margin-top: 15px;" :style="{marginTop: healthNmame ? '15px' : '0px'}">
          <div  v-for="(item,k) in list" :key="k":style="{borderBottom: list.length - 1 === k ? '' : '1px solid #F7F7F7'}"
                style="display: flex; align-items: center; padding: 18px 5px; justify-content: space-between;">
            <div style="display: flex; align-items: center">
              <img width="35px" height="35px" :src="item.iconUrl" alt="">
              <div style="color: #333;font-size: 15px;font-weight: 600; margin-left: 10px">
                {{ item.dictItemType === 'MEDICINE' ? '用药信息' : item.name }}
              </div>
            </div>

            <div @click="goPage(item)" class="otherBtn">
              添加
            </div>
          </div>
        </div>

      </div>

      <div style="padding-bottom: 100px;">

      </div>
    </div>

    <div style="position: fixed; bottom: 20px; left: 20px; right: 20px">
      <div class="homeBtn" @click="()=>{$router.push({path: '/home'})}">返回首页</div>
    </div>
  </div>
</template>

<script>
import Api from '@/api/Content.js'
import Vue from 'vue';
import {Col, Divider, Icon, Row, Popup, Grid, GridItem} from 'vant';
import wx from "weixin-js-sdk";

Vue.use(Icon);
Vue.use(Divider);
Vue.use(Col);
Vue.use(Row);
Vue.use(Popup);
Vue.use(Grid);
Vue.use(GridItem);
export default {
  name: "loginSuccessful",
  components: {
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      list: [],
      healthNmame: '',
      show: false,
      height: window.innerHeight - 87,
      countdown: 3,
      countdownInterval: undefined
    }
  },
  mounted() {
    this.getPatientJoinGroupAfterMenu()
  },
  beforeDestroy() {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval); // 停止倒计时
    }
  },
  methods: {
    // 获取患者基本信息完成后的菜单
    getPatientJoinGroupAfterMenu() {
      Api.getPatientJoinGroupAfterMenu().then(res => {
        if (res.data.code === 0) {
          let arr = res.data.data
          const item = arr.find(item => item.dictItemType === 'HEALTH')
          if (item) {
            this.healthNmame = item.name
          }
          this.list = arr.filter(item => item.dictItemType !== 'HEALTH')
        }
      })
    },
    // 跳转其他功能
    goPage(item) {
      this.$router.push({
        path: '/' + item.path
      })
    },
    // 跳转疾病信息(健康档案)
    goHealth() {
      Api.getFormIntoTheGroup('HEALTH_RECORD').then(res => {
        if (res.data.code === 0) {
          console.log('=============', this.healthNmame)
          localStorage.setItem('pageTitle', this.healthNmame)
          if (res.data.data === 1) {
            // 跳转到一题一页
            this.$router.replace({path: '/questionnaire/editquestion', query: {status: 2, isGroup: 1,}})
          } else {
            // 跳转到正常表单
            this.$router.push({path: '/health/editor'})
          }
        }
      })
    }
  },
  beforeRouteEnter(to, from, next) {
    if (from.path === '/home' && to.path === '/questionnaire/loginSuccessful') {
      wx.closeWindow()
      next(false)
    } else if (to.path === '/two' && from.path === '/questionnaire/loginSuccessful') {
      next(false)
    } else {
      next()
    }

  }
}
</script>

<style scoped lang="less">

.homeBtn {
  background: #24C789;
  border-radius: 45px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  color: #fff;
}

.whiteBox {
  background: #FFFFFF;
  padding: 15px 12px;
  border-radius: 15px;
  color: #666;
  margin-top: 10px;
}

.healthBox {
  background: linear-gradient(to top, #FFFFFF 0%, #ECFAF3 100%);
  border: 1px solid #D6F4E9;
  border-radius: 10px;
  display: flex;
  align-items: center;
  padding: 10px 12px;
  justify-content: space-between;
}

.otherBtn {
  background: #fff;
  border-radius: 26px;
  border: 1px solid #24C789;
  padding: 5px 15px;
  color: #24C789;
  font-size: 12px;
}

.improveBtn {
  background: #24C789;
  border-radius: 26px;
  padding: 5px 15px;
  color: #FFF;
  font-size: 12px;
}

.mian-box {
  padding: 20px 13px;
  text-align: center;
  background: #F8F8F8;

  .jbxx {
    text-align: left;
    margin-top: 30px;
    padding: 13px;
    background: #FFFFFF;
    display: flex;
    align-items: center;
    border-radius: 13px;

    .left {
      display: flex;
      flex: 1;
      align-items: center;
    }

    .right {
      width: 13vw;
      text-align: center;
      padding: 3px 6px;
      font-size: 12px;
      color: #73A2FD;
      border-radius: 42px;
      border: 1px solid #73A2FD;
    }
  }

  .list-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    border-radius: 13px;
    box-shadow: 0px 1px 6px 1px #F8F8F8;
    padding: 17px;
    background: #FFFFFF;
  }


}
</style>
