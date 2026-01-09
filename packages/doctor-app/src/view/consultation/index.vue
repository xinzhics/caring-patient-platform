<template>
  <section class="allContent">
<!--    <x-header :left-options="{backText: ''}">{{ // pageTitle && pageTitle.concat('讨论') ? pageTitle : "病例讨论" }}-->
    <x-header :left-options="{backText: '',  preventGoBack:true}"  @on-click-back="() => $router.replace('/index')">病例讨论
      <van-icon name="add-o" size="20" style="font-size: 20px;" slot="right"
                @click="() => {
                  $router.push('/consultation/add')
                  $store.commit('addData', {})
                }"/>
    </x-header>
    <div style="padding-top: 50px;">
      <div>
        <tab v-model="active" prevent-default @on-before-index-change="switchTabItem">
          <tab-item active-class="active">待处理</tab-item>
          <tab-item active-class="active">进行中</tab-item>
          <tab-item active-class="active">已结束</tab-item>
        </tab>
      </div>

      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #fafafa;">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad">
          <div v-for="(item, key) in list" :key="key"
               @touchstart="gotouchstart(item)" @touchmove="gotouchmove" @touchend="gotouchend(item)">
            <div style="padding: 10px; margin-top: 10px; background: white;"   @click="showMember(item)">
              <div style="display: flex; justify-content: space-between; align-items: center; ">
                <van-image style="width: 45px; height: 45px;" round :src="item.memberAvatar"/>
                <div style="flex: 1; margin-left: 10px; margin-right: 10px; font-size: 15px;">{{ item.groupName }}</div>
                <div style="display: flex; align-items: center">
                  <div style="font-size: 15px; color: #B8B8B8" v-if="item.memberStatus == -3"
                       @click="()=>{
                     errorMessage = ''
                     getConsultationMember(item.id)
                   }">拒绝原因
                  </div>
                  <badge v-if="(item.memberStatus == 1 || item.memberStatus == -1) && item.noReadMessage > 0"
                         :text="item.noReadMessage"></badge>
                  <van-icon name="arrow" color="#B8B8B8"/>
                </div>

              </div>
              <div style="border-bottom: 1px solid #E4E4E4; margin-top: 10px;"/>
            </div>

            <div style="background: white; padding: 5px 12px 15px 12px; color: #666666; font-size: 14px">
              <van-row>
                <van-col :span="item.memberStatus != -3 ? '24' : '16'">
                  <div>
                    <span>会诊描述：</span>
                    <span>{{ item.groupDesc ? item.groupDesc : '-' }}</span>
                  </div>

                  <div style="margin-top: 5px;">
                    <span>创建时间：</span>
                    <span>{{ item.createTime }}</span>
                  </div>

                  <div style="margin-top: 5px;">
                    <span>发起人：</span>
                    <span>{{item.createUserType && item.createUserType === 'NursingStaff' ? (item.createUserName ? item.createUserName : '-') + '(' + assistantName + ')' :
                      item.createUser && item.createUser === userId ? '我自己' : (item.createUserName ? item.createUserName : '-') + '(' + doctorName+ ')'}}</span>
                    <!--<span> {{ item.createUser && item.createUser === userId ? '我自己' : (item.createUserName ? item.createUserName : '-') +
                      '(' +  (item.createUser === userId ? doctorName : item.nurseId && item.nurseId === item.createUser ? assistantName : doctorName) + ')' }}</span>-->
                  </div>
                </van-col>
                <van-col :span="item.memberStatus != -3 ? '0' : '8'">
                  <div style="display: flex; justify-content: center; align-items: center"
                       v-if="item.memberStatus == -3">
                    <img width="60px" height="60px" :src="require('@/assets/my/consultation_refuse_icon.png')">
                  </div>
                </van-col>
              </van-row>

              <div style="margin-top: 15px; display: flex; justify-content: right"
                   v-if="active === 0 && item.memberStatus != -3">
                <van-button round plain style="width: 80px;" type="danger" size="small" @click="()=> {
              groupId = item.id
              refuseMessage = ''
              isRefuse = true
            }">拒绝
                </van-button>
                <van-button round style="width: 80px; margin-left: 20px" type="info" size="small" @click="adopt(item)">
                  通过
                </van-button>
              </div>
            </div>

          </div>

        </van-list>
      </van-pull-refresh>

    </div>

    <van-dialog v-model="isRefuse"  :show-confirm-button="false">
      <div style="padding: 15px 0px;">
        <div
          style="position: relative; width: 100%; display: flex; align-items: center; margin-bottom: 10px;">
          <div style="display: flex; width: 100%; justify-content: center;">
            <div
              style=" max-width: 120px; text-overflow:ellipsis;overflow:hidden;white-space:nowrap; color: #333333; font-size: 16px;">
              请输入拒绝原因
            </div>
          </div>
          <van-icon name="cross" style="position: absolute; right: 0; margin-right: 15px;" size="15px;" color="#B8B8B8"
                    @click="isRefuse = !isRefuse"/>
        </div>
        <div style="display: flex; justify-content: center;">
          <div style="width: 80px; height: 5px;background: #FFBE8B; opacity: 1;border-radius: 8px;"></div>
        </div>

        <div style="margin: 0px 15px; background: #F5F5F5; border-radius: 5px; border: 1px solid #EBEBEB; margin-top: 20px; margin-bottom: 20px;">
          <van-field
            v-model="refuseMessage"
            rows="4"
            autosize
            style="background: #F5F5F5;"
            type="textarea"
            maxlength="80"
            placeholder="请输入留言"
            show-word-limit
          />
        </div>
        <div style="text-align: center">
          <van-button mini type="default" round
                      style="height:44px; width: 200px; margin-top: 10px; color: white"
                      :style="{background: refuseMessage ? '#66728B' : '#D6D6D6'}"
                      @click.native="refuse()">确定
          </van-button>
        </div>
      </div>
    </van-dialog>

    <van-dialog v-model="isMessage" :show-confirm-button="false">
      <div style="padding: 15px; display: grid; justify-content: center; justify-items: center">
        <div style="margin-top: 30px; width: 60px; height: 60px;">
          <img width="60px" height="60px" :src="require('@/assets/my/consultation_refuse_tips_icon.png')">
        </div>
        <div style="font-size: 20px; color: #333333; margin-top: 15px;">
          拒绝原因
        </div>
        <div style="font-size: 14px; color: #666666; margin-top: 15px; text-align: center">
          {{ errorMessage }}
        </div>
        <van-button mini type="primary" action-type="button" round
                  style="height:44px; background: #337EFF; width: 200px; margin-top: 30px; margin-bottom: 15px;"
                  @click.native="() => {isMessage = false}">确定
        </van-button>
      </div>
    </van-dialog>
  </section>
</template>

<script>
import {Tab, TabItem, Badge} from 'vux'
import Vue from 'vue'
import { Dialog } from 'vant';
import Api from "@/api/doctor.js";

Vue.use(Dialog)
export default {
  name: "index",
  components: {
    Badge,
    Tab,
    TabItem
  },
  data() {
    return {
      pageTitle: localStorage.getItem('pageTitle'),
      active: 0,
      list: [],
      userId: localStorage.getItem('caring_doctor_id'),
      errorMessage: '',
      isRefuse: false,
      isMessage: false,
      refuseMessage: '',
      groupId: '',
      imAccount: undefined,
      param: {
        current: 1,
        size: 10,
        model: {
          doctorOpenId: '',
          imAccount: '',
          memberState: 1,
          userId: localStorage.getItem('caring_doctor_id'),
          userRole: 'roleDoctor',
        }
      },
      doctorName: '',
      assistantName: '',
      loading: false,
      finished: false,
      refreshing: false,
      isItemClick: false,

    }
  },
  created() {
    this.doctorName = this.$getDictItem('doctor')
    this.assistantName = this.$getDictItem('assistant')

    this.getDoctorInfo()

  },
  methods: {
    showMember(item) {
      if (this.active === 0 && item.memberStatus === -2) {
        localStorage.setItem('groupId', item.id)
        this.$router.push('/consultation/member')
      }
    },
    gotouchstart(item) {
      let that = this
      this.isItemClick = true
      window.clearTimeout(this.timeOutEvent);
      this.timeOutEvent = 0
      this.timeOutEvent = window.setTimeout(function () {
        that.isItemClick = false
        if (item.memberStatus == -3) {
          //长摁删除拒绝的邀请
          Dialog.confirm({
            title: '提示',
            message: '确认删除此拒绝记录吗？',
          })
            .then(() => {
              Api.deleteRejectConsultation(item.id)
                .then(res => {
                  if (res.data.code === 0) {
                    that.refreshing = true
                    that.onRefresh()
                  }
                })
            })
            .catch(() => {
              // on cancel
            });
        }else if (item.memberStatus == 2) {
          //长摁删除结束的会诊
          Dialog.confirm({
            title: '提示',
            message: '确认删除此小组吗？',
          })
            .then(() => {
              Api.deleteConsultationGroup([item.id])
                .then(res => {
                  if (res.data.code === 0) {
                    that.refreshing = true
                    that.onRefresh()
                  }
                })
            })
            .catch(() => {
              // on cancel
            });
        }
      }, 1000);//这里设置定时
    },
    //手释放，如果在500毫秒内就释放，则取消长按事件，清除timeout
    gotouchend(item) {
      window.clearTimeout(this.timeOutEvent);
      if (this.timeOutEvent != 0) {
        if (this.isItemClick) {
          this.isItemClick = false
          this.jumpClick(item)
        }
      }
    },
    //如果手指有移动，则取消所有事件，此时说明用户只是要移动而不是长按，清除timeout
    gotouchmove() {
      window.clearTimeout(this.timeOutEvent);//清除定时器
      this.timeOutEvent = 0;
    },
    getDoctorInfo() {
      Api.getContent({id: localStorage.getItem('caring_doctor_id')})
        .then(res => {
          if (res.data.code === 0) {
            this.param.model.doctorOpenId = res.data.data.openId
            this.imAccount = res.data.data.imAccount
            if (this.$route.params.state === 'list') {
              this.active = 1
            } else {
              this.active = 0
            }
            this.onRefresh()
          }
        })
    },
    onRefresh() {
      // 清空列表数据
      this.finished = false;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = false
      this.list = [];
      this.onLoad();
    },
    onLoad() {
      if (this.refreshing) {
        // 需要吧list清空。防止重复加载数据
        this.list = [];
        this.param.current = 1;
        this.refreshing = false;
      }
      this.getDataPage()
    },
    //获取拒绝信息
    getConsultationMember(groupId) {
      Api.getConsultationMember(groupId)
        .then(res => {
          if (res.data && res.data.code === 0) {
            this.errorMessage = res.data.data.memberRefuseMessage
            this.isMessage = true
          }
        })
    },
    jumpClick(item) {
      if (item.memberStatus == 1 || item.memberStatus == -1) {
        localStorage.setItem('groupId', item.id)
        this.$store.commit('onGroupMessageList', {messageList: [], upload: false})
        this.$router.push('/consultation/message')
      }
    },
    getDataPage() {
      this.param.model.memberState = this.active == 0 ? -2 : this.active
      if (!this.imAccount) {
        return
      }
      if (this.loading) {
        return;
      }
      this.loading = true;
      this.param.model.imAccount = this.imAccount
      Api.consultationGroupPage(this.param)
        .then(res => {
          if (res.data && res.data.data) {
            if (this.param.current === 1) {
              this.list = res.data.data.records
            } else {
              this.list.push(...res.data.data.records)
            }
            this.param.current++;
            this.loading = false;
            if (this.param.current >= res.data.data.pages) {
              this.finished = true;
            }
          }
          this.loading = false
        })
    },
    switchTabItem(index) {
      if (this.active == index)
        return
      this.active = index
      this.refreshing = true
      this.onRefresh()
    },
    refuse() {
      if (this.refuseMessage === '') {
        this.$vux.toast.text("请输入拒绝内容", 'center');
        return
      }
      this.consultatiAcceptOrReject('reject')
    },
    adopt(item) {
      const that = this
      Dialog.confirm({
        title: '提示',
        message: '确认加入此会诊小组吗？',
      })
        .then(() => {
          that.groupId = item.id
          that.consultatiAcceptOrReject('accept')
        })
        .catch(() => {
          // on cancel
        });
    },
    consultatiAcceptOrReject(action) {

      let param = {
        action: action,
        groupId: this.groupId,
        imAccount: this.imAccount,
        rejectMessage: action === 'reject' ? this.refuseMessage : '',
      }
      Api.consultatiAcceptOrReject(param)
        .then(res => {
          if (res.data && res.data.data) {
            this.isRefuse = false
            this.active = 1
            this.refreshing = true
            this.onRefresh()
          }
        })
    }
  }
}
</script>

<style lang="less" scoped>
.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;
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

.active {
  color: rgba(63, 134, 255, 1) !important;
}

/deep/ .vux-tab-ink-bar {
  background-color: #3F86FF;
}
</style>
