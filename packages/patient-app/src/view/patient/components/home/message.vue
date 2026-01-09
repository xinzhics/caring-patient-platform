<template>
  <section>
    <div style="padding: 10px 20px; background: white">
      <van-grid :border="false" :column-num="2">
        <van-grid-item @click="jumpSystem()">
          <template slot="text">
            <div style="font-size: 16px; color: #333">系统消息</div>
          </template>
          <template slot="icon">
            <div style="position: relative">
              <van-image
                width="60"
                height="60"
                :src="require('@/assets/my/message_system.png')"
              />
              <div v-if="systemMessageCount > 0" class="badge" style="position: absolute; top: -6px; right: -6px">
                {{ systemMessageCount > 99 ? '99+' : systemMessageCount }}
              </div>
            </div>
          </template>
        </van-grid-item>
        <van-grid-item @click="jumpChat()">
          <template slot="text">
            <div style="font-size: 16px; color: #333">咨询消息</div>
          </template>
          <template slot="icon">
            <div style="position: relative">
              <van-image
                width="60"
                height="60"
                :src="require('@/assets/my/message_consult.png')"
              />
              <div v-if="imMessageCount > 0" class="badge" style="position: absolute; top: -6px; right: -6px">
                {{ imMessageCount > 99 ? '99+' : imMessageCount }}
              </div>
            </div>
          </template>
        </van-grid-item>
      </van-grid>
    </div>

    <div style="margin-top: 15px">
      <van-cell-group v-for="(item, key) in messageList" :key="key" @click="jumpMessage(item)">
        <van-cell size="large">
          <template slot="icon">
            <van-image
              width="45"
              height="45"
              round
              fit="fill"
              :src="getIcon(item.doctorName, item.messageType, item.doctorAvatar)"
            />
          </template>
          <template slot="title">
            <div v-if="item.doctorName" style="font-size: 16px;font-weight: 600;color: #333333;">{{ item.doctorName }}
            </div>
            <div v-else style="font-size: 16px;font-weight: 600;color: #333333;">{{ item.messageName }}</div>
          </template>
          <template slot="label">
            <div
              style="font-size: 14px; color: #999999; white-space: nowrap; overflow: hidden;  text-overflow: ellipsis; width: 200px">
              {{ item.messageContent }}
            </div>
          </template>
          <template slot="extra">
            <div style="display: flex; flex-direction: column; align-items: center; justify-content: center">
              <div style="font-size: 12px; color: #999999; margin-bottom: 5px"
                   :style="{marginBottom: item.noReadCount && item.noReadCount > 0 ? '0px' : '20px' }">
                {{ getTime(item.messageSendTime) }}
              </div>
              <van-badge v-if="item.noReadCount && item.noReadCount > 0" max="99" :content="item.noReadCount">
              </van-badge>
            </div>
          </template>
        </van-cell>
      </van-cell-group>
    </div>
  </section>
</template>

<script>

import Vue from 'vue';
import {Grid, GridItem, Cell, CellGroup, Badge} from 'vant';
import ContentApi from "@/api/Content";
import {mapGetters} from "vuex";
Vue.use(Grid);
Vue.use(Cell);
Vue.use(CellGroup);
Vue.use(Badge);
Vue.use(GridItem);

export default {
  name: "message",
  data() {
    return {
      imAccount: undefined,
      messageList: [],
      imMessageCount: 0,
      systemMessageCount: 0,
      allMessageCount: 0,
    }
  },
  computed: {
    ...mapGetters({
      updateMessage: "onUpdateMessage",
    }),
  },
  watch: {
    updateMessage: function (val, old) {
      if (val.type === 'chat' && val.ext) {
        this.setPatientMessage(val.ext)
      }
    }
  },
  created() {
    if (localStorage.getItem('myallInfo')) {
      this.imAccount =  JSON.parse(localStorage.getItem('myallInfo')).imAccount
      this.getMessagePageData()
    } else {
      this.getInfo()
    }
  },
  methods: {
    setPatientMessage(val) {
      let index = this.messageList.findIndex(item =>  item.doctorName)
      this.imMessageCount = this.imMessageCount + 1
      this.allMessageCount = this.allMessageCount + 1
      this.$emit('setCount', this.allMessageCount)
      console.log('setPatientMessage', val)
      if (index > -1) {
        // 有患者聊天消息
        this.messageList[index].noReadCount = this.messageList[index].noReadCount + 1
        this.messageList[index].messageSendTime = val.createTime
        this.messageList[index].messageContent = this.handleMessage(val)
      }else {
        // 没有患者聊天消息
        this.messageList.unshift({
          doctorName: val.senderName,
          doctorAvatar: val.senderAvatar,
          messageContent: this.handleMessage(val),
          messageSendTime: val.createTime,
          noReadCount: 1,
        })
      }
    },
    handleMessage(val) {
      switch (val.type) {
        case "text": {
          return val.content
        }
        case "cms": {
          return this.$lang('chatCms')
        }
        case "remind": {
          return this.$lang('chatRemind')
        }
        case "image": {
          return this.$lang('chatImage')
        }
        case "voice": {
          return this.$lang('chatVoice')
        }
        case "video": {
          return this.$lang('chatVideo')
        }
        default: {
          return this.$lang('chatOther')
        }
      }
    },
    getInfo() {
      const params = {
        id: localStorage.getItem('userId')
      }
      ContentApi.getContent(params).then((res) => {
        if (res.data.code === 0) {
          if (res.data.data) {
            this.imAccount = res.data.data.imAccount
            this.getMessagePageData()
          }
        }
      })
    },
    jumpMessage(item) {
      if (item.doctorName) {
        this.jumpChat()
      } else {
        this.$router.push({path: '/patient/systemMessage', query: {messageType: item.messageType}})
      }
    },
    getTime(time) {
      return moment(time).format("HH:mm");
    },
    getMessagePageData() {
      ContentApi.getMessagePageData(this.imAccount)
        .then(res => {
          this.messageList = res.data.data.messageList
          this.imMessageCount = res.data.data.imMessageCount
          this.systemMessageCount = res.data.data.systemMessageCount
          this.allMessageCount = res.data.data.allMessageCount
          this.$emit('setCount', res.data.data.allMessageCount)
        })
    },
    jumpSystem() {
      this.$router.push({path: '/patient/systemMessage'})
    },
    jumpChat() {
      this.$router.push({path: '/im/index'})
    },
    getIcon(doctorName, messageType, doctorAvatar) {
      if (doctorName) {
        // 医生头像
        if (doctorAvatar) {
          return doctorAvatar
        } else {
          return require('@/assets/my/message_doctor_default.png')
        }
      } else {
        if (messageType === 'BOOKING_MANAGEMENT') {
          // 预约管理提醒 1
          return require('@/assets/my/message_1.png')
        } else if (messageType === 'BUY_DRUGS_REMINDER') {
          // 用药预警提醒 2
          return require('@/assets/my/message_2.png')
        } else if (messageType === 'COMPLETENESS_INFORMATION') {
          // 完善个人信息提醒 3
          return require('@/assets/my/message_3.png')
        } else if (messageType === 'MEDICATION') {
          // 用药提醒 4
          return require('@/assets/my/message_4.png')
        } else if (messageType === 'CUSTOM_FOLLOW_UP') {
          // 随访计划提醒 5
          return require('@/assets/my/message_5.png')
        } else if (messageType === 'INDICATOR_MONITORING' || messageType === 'BLOOD_PRESSURE' || messageType === 'BLOOD_SUGAR') {
          // 自定义指标监测 血压、血糖 6
          return require('@/assets/my/message_6.png')
        } else if (messageType === 'HEALTH_LOG') {
          // 健康日志提醒 7
          return require('@/assets/my/message_7.png')
        } else if (messageType === 'REVIEW_MANAGE') {
          // 复查消息提醒 8
          return require('@/assets/my/message_8.png')
        } else if (messageType === 'LEARNING_PLAN') {
          // 科普知识推送提醒 9
          return require('@/assets/my/message_9.png')
        } else if (messageType === 'CASE_DISCUSSION') {
          // 病例讨论 10
          return require('@/assets/my/message_10.png')
        } else if (messageType === 'REFERRAL_SERVICE') {
          // 转诊管理提醒 11
          return require('@/assets/my/message_11.png')
        } else if (messageType === 'Reply_after_following' || messageType === 'unregistered_reply') {
          // 关注提醒 12
          return require('@/assets/my/message_12.png')
        } else {
          return require('@/assets/my/message_13.png')
        }
      }
    }
  }
}
</script>

<style lang="less" scoped>

.badge {
  display: inline-block;
  box-sizing: border-box;
  min-width: 16px;
  padding: 0 3px;
  color: #fff;
  font-weight: 500;
  font-size: 12px;
  font-family: -apple-system-font, Helvetica Neue, Arial, sans-serif;
  line-height: 1.2;
  text-align: center;
  background-color: #ee0a24;
  border: 1px solid #fff;
  border-radius: 999px;
}

/deep/ .van-cell__title {
  flex: 2;
  margin-left: 8px;
}

</style>
