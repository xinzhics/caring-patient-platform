<template>
  <section class="allContent">
    <div style="height: 100%; width: 100%; margin: 0 auto;" class="messagebox">
      <x-header :left-options="{backText: '', preventGoBack:true}" @on-click-back="goBack()">医患互动
        <div slot="right">
          <img :src="require('@/assets/my/im_setting.png')"
               @click="$router.push('/im/messageSetting')"
               width="20px" height="20px">
        </div>
      </x-header>

      <div v-if="!startLoading">
        <div v-if="!doctorMsgChecked" style=" display: flow-root;">
          <div style="background: #fafafa; margin-top: 50px">
            <div style="display: flex; margin-top: 150px; justify-content: center">
              <div>
                <img width="150px" height="150px"
                     :src="require('@/assets/my/message.png')"/>

                <div style="font-size: 14px; color: #999999; text-align: center">功能未开启</div>
              </div>
            </div>

            <x-button style="margin-top:20px;width:40%;background-color:#3F86FF; font-size: 15px;" type="primary"
                      @click.native="$router.push('/im/messageSetting')">前去开启
            </x-button>

          </div>
        </div>
        <div v-else>
          <div style="padding-top: 50px; display: flow-root">
            <van-search
              v-model="patientName"
              show-action
              shape="round"
              :placeholder="'请输入'+patient+'名称'"
              @search="onSearch"
              @clear="onCancel"
            >
              <div slot="action" @click="onSearch">搜索</div>
            </van-search>
          </div>
          <div :style="computeHeight()" style="overflow: auto">
            <div v-for="(item,i) in msgList"
                 :key="i"
                 style="background: white; margin-top: 1px;position: relative"
                 @click="itemClick(item)">
              <van-cell>
                <template slot="icon">
                  <img width="45px" height="45px"
                       style="border-radius: 50%; margin-left: 15px"
                       v-bind:src=loadingAvatar(item.patientAvatar,item.requestRoleType)
                  />
                </template>
                <template slot="title">
                  <div style="margin-left: 10px; display: flex; align-items: center; width: calc(100vw - 180px)">
                    <div style="">
                      {{ item.patientRemark ? item.patientName + '(' + item.patientRemark + ')' : item.patientName }}
                    </div>
                    <div v-if="item.chat && (item.diagnosisName || item.chat.diagnosisName)">
                      <div class="diagnosis" style="flex: 1">{{ item.diagnosisName ? item.diagnosisName : item.chat.diagnosisName }}</div>
                    </div>
                  </div>
                </template>
                <template slot="label">
                  <div style="margin-left: 10px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; width: calc(100vw - 180px)">
                    {{ item.chat ? getContent(item.chat.type, item.chat.content, item.noReadTotal, item.chatAtRecords) : '' }}
                  </div>
                </template>
                <template slot="icon">
                  <div
                    style="width: 70px;z-index:9999;position: absolute;right: 0;top: 50%;transform: translateY(-50%);background: #FFFFFF;height: 100%;display: flex;justify-content: flex-end;align-items: center">
                    <div class="flex-demo"
                         style="text-align: right; margin-right: 10px; display: flex; align-items: center">
                      <img :src="require('@/assets/my/im_exit_chat.png')" style="margin-left: 5px; margin-right: 5px" width="20" height="20" v-if="item.doctorExitChat === 1">
                      <div
                        v-if="item.noReadTotal && item.noReadTotal != 0"
                        :class="item.noReadTotal>99?'message-99':''"
                        style="background: #FF7777; width: 21px; height: 21px; text-align: center;
                              color: #FFF; font-size: 12px; border-radius: 50%; display: flex; align-items: center; justify-content: center;margin-right:7px">
                        {{ item.noReadTotal > 99 ? '99+' : item.noReadTotal }}
                      </div>
                      <van-icon name="arrow" style="align-items: center; justify-content: center; display: flex"/>
                    </div>
                  </div>
                </template>
              </van-cell>

            </div>
          </div>
        </div>
      </div>

    </div>
  </section>
</template>

<script>
import {Flexbox, FlexboxItem, Divider} from 'vux'
import Api from '@/api/Content.js'
import {mapGetters} from "vuex";
import {List, Toast} from 'vant'

export default {
  name: "messageList",
  components: {
    [List.name]: List,
    Flexbox,
    FlexboxItem,
    Divider
  },
  computed: {
    ...mapGetters({
      msgList: "onGetDoctorMessageList"
    }),
  },
  data() {
    return {
      patientName: "",
      doctorMsgChecked: false,
      loading: false,
      finished: false,
      refreshing: false,
      startLoading: true,
      patient: this.$getDictItem('patient'),
      toastLoading: null,
    }
  },
  methods: {
    //计算高度
    computeHeight() {
      return 'height: calc(100vh - ' + 100 + 'px)'
    },
    goBack() {
      console.log('goBack messageList')
      this.$router.replace('/index')
    },
    getContent(type, content, noReadTotal, chatAtRecords) {
      if (type === "image") {
        content = '[图片]'
      } else if (type === "cms") {
        content = '[文章]'
      } else if (type === "voice") {
        content = '[语音]'
      } else if (type === 'text' && noReadTotal && noReadTotal > 0 && chatAtRecords && chatAtRecords.length > 0) {
        content = '[有人@你啦]'
      }
      return content
    },
    //上拉加载
    onLoad() {
      setTimeout(() => {
        if (this.refreshing) {
          this.list = [];
          this.refreshing = false;
        }
        this.loading = false;
        this.finished = true;
      }, 1000);
    },

    onRefresh() {
      // 清空列表数据
      this.finished = false;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true;
      this.onLoad();
    },
    //取消
    onCancel() {
      this.getDoctorMsgList(this.patientName)
    },
    //搜索点击
    onSearch() {
      this.getDoctorMsgList(this.patientName)
    },
    //聊天条目点击跳转
    itemClick(item) {
      Vue.$store.commit("clearMessageList", {});
      this.$router.push({
        path: '/im/index', query: {
          imAccount: item.receiverImAccount,
          imPatientId: item.patientId,
          isAt: (!item.chat ? false : this.getContent(item.chat.type, item.chat.content, item.noReadTotal, item.chatAtRecords) === '[有人@你啦]' ? true : false)
        }
      })
      localStorage.setItem('patientId', item.patientId)
      Vue.$store.commit("updateDoctorMessageList", {
        receiverImAccount: item.receiverImAccount,
      });
    },
    //判断患者有无头像
    loadingAvatar(avatar, type) {
      if ("NursingStaff" === type) {
        return avatar ? avatar : require('@/assets/my/nursingstaff_avatar.png')
      } else if ("doctor" === type) {
        return avatar ? avatar : require('@/assets/my/doctor_avatar.png')
      } else {
        return avatar
      }
    },
    //获取医生历史记录
    getDoctorMsgList(name) {
      const params = {
        current: 1,
        size: 200,
        model: {
          patientName: name ? name : "",
          requestId: localStorage.getItem('caring_doctor_id')
        },
      }
      Api.getDoctorMsgList(params).then((res) => {
        if (res.data.data && res.data.data.records) {
          Vue.$store.commit("onDoctorMessageList", {
            messageList: res.data.data.records,
          });
          this.timeOutEvent = setTimeout(function () {
            Vue.$store.commit("setFirstLoading", {});
          }, 1500);//这里设置定时
        }
      }).catch(function (error) { // 请求失败处理
        this.timeOutEvent = setTimeout(function () {
          Vue.$store.commit("setFirstLoading", {});
        }, 1500);//这里设置定时
      });
    },
    //查询医生互动开关是否关闭
    getDoctorInfo() {
      const params = {
        id: localStorage.getItem('caring_doctor_id')
      }
      Api.getDoctorInfo(params).then((res) => {
        if (res.data.data) {
          localStorage.setItem("avatar", res.data.data.avatar)
          if (res.data.data.imMsgStatus === 1) {
            this.doctorMsgChecked = true;
            this.getDoctorMsgList(this.patientName);
            const userName = localStorage.getItem("userImAccount");
            const password = "123456";
            let options = {
              apiUrl: WebIM.config.apiURL,
              user: userName,
              pwd: password,
              appKey: WebIM.config.appkey
            };
            WebIM.conn.open(options);
          } else {
            this.doctorMsgChecked = false;
          }
          this.startLoading = false
          this.toastLoading.clear()
        }
      })
    },
  },
  created() {
    this.toastLoading = Toast.loading({
      message: '加载中...',
      forbidClick: true,
      duration: 0
    });
    this.getDoctorInfo()
  }
}
</script>

<style scoped lang='less'>
.contentTitle {
  height: 15px;
  line-height: 15px;
  font-size: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #333333;
  margin: 0 7px 0 12px;
}

.contentValue {
  max-width: 230px;
  margin-left: 12px;
  overflow: hidden;
  color: #999999;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin: 5px 7px 0 12px;
}

.flex-demo {
  max-width: 100%;
}

.diagnosis {
  height: 15px;
  line-height: 15px;
  //max-width: 70px;
  overflow: hidden;
  //white-space: nowrap ;
  font-size: 12px;
  color: #FFBE8B;
  border-radius: 10px;
  padding-left: 5px;
  padding-right: 5px;
  border: 1px solid #FFBE8B;
  text-align: center;
  flex: 1;
}

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;
  overflow: hidden;
}

/deep/ .vux-header {
  margin-bottom: 0px;
  height: 50px;
  position: fixed;
  width: 100%;
  z-index: 999;
  top: 0;
  left: 0;
}

.message-99 {
  width: 35px !important;
  height: 21px !important;
  border-radius: 11px !important;
}
</style>
