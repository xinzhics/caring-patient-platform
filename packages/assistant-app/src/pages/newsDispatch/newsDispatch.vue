<template>
  <div>
    <!--顶部组件-->
    <van-sticky :offset-top="0">
      <headNavigation leftIcon="arrow-left" rightIcon="add-o" @showpop="sentMessage" title="群发消息" @onBack="onBack"></headNavigation>
    </van-sticky>
    <!--    下拉刷新-->
    <van-pull-refresh v-if="massMessageList.length>0" v-model="refreshing" style="min-height: 90vh" @refresh="onRefresh">
      <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad">
        <!--    中间列表内容  >-->
        <div v-if="show">
          <div v-for="message in massMessageList" :key="message.id"
              @touchstart="gtouchstart(message)"
              @touchmove="gtouchmove(message)"
              @touchend="showDeleteButton(message)"
              style="margin-top: 13px; padding: 11px 12px 14px 13px" :style="message.active ? 'background: #E2E2E2;' : 'background: #fff;'" >
            <div style="display: flex;align-items: center;padding-bottom: 11px;border-bottom: 1px solid #EEEEEE" :style="message.active ? 'background: #E2E2E2;' : 'background: #fff;'">
              <img src="../../assets/newsIcon.png" style="width: 26px;" alt="">
              <span style="margin-left: 7px;color: #333;font-weight: 700">成功发送 <span style="color: #3F86FF">{{ getMessagePeopleNumber(message) }}</span> 人</span>
            </div>
            <div v-if="message.type === 'text'" style="padding:9px 17px 9px 9px;border-radius: 4px;margin-top: 11px" :style="message.active ? 'background: #fff;' : 'background: #F5F5F5;'">
              <div style="display: flex;align-items: center">
                <div style="height: 40px;line-height:40px;
                    font-size: 15px;color: #666;margin-left: 9px;
                    overflow: hidden;text-overflow:ellipsis ;display:-webkit-box;-webkit-box-orient: vertical;-webkit-line-clamp: 2">
                  {{ message.content }}
                </div>
              </div>
            </div>
            <div v-if="message.type === 'image'" style="padding:9px 17px 9px 9px" :style="message.active ? 'background: #fff;' : 'background: #F5F5F5;'">
              <div style="display: flex;justify-content: space-between;align-items: center;">
                <div style="width: 65px;height: 65px;border-radius: 4px;display: flex;align-items: center;justify-content: center">
                  <img :src="message.content " style="width: 52px;height: 52px" alt="" @click="openImagePreview(message.content)">
                </div>
              </div>
            </div>

            <div v-if="message.type === 'cms'" style="padding:9px 17px 9px 9px;border-radius: 4px;margin-top: 11px;" :style="message.active ? 'background: #fff;' : 'background: #F5F5F5;'">
              <div style="display: flex;align-items: center;" @click="clickMessage(message)">
                  <div style="border-radius: 4px">
                    <img :src="getCmsImage(message)" style="width: 48px" alt="">
                  </div>
                <div style="height: 40px;line-height:40px;font-size: 15px;color: #666;margin-left: 9px;overflow: hidden;text-overflow:ellipsis;-webkit-line-clamp: 2;display: -webkit-box;-webkit-box-orient: vertical">
                  {{ getCmsContent(message) }}
                </div>
              </div>
            </div>
            <div style="border-top:  1px solid #EEEEEE;padding-top: 9px;margin-top: 11px">
              <div style="display: flex;justify-content: space-between;align-items: center">
                <div style="color: #999;font-size: 15px">{{ message.createTime.substring(0, 16) }}</div>
                <van-button @click="againSend(message)" round type="default">继续群发</van-button>
              </div>
            </div>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
<!--    空数据-->
    <div v-if="show&&massMessageList.length === 0" style="margin-top: 50vh;transform: translateY(-50%)">
      <div style="width: 152px;margin:27px auto 9px auto"><img src="../../assets/noNews.png" style="width: 100%" alt=""></div>
      <div style="color: #999999;font-size: 15px;text-align: center">暂无信息</div>
    </div>
    <!-- 确认删除 -->
    <van-dialog v-model="showDelete" title="标题" confirmButtonColor="#2F8DEB" show-cancel-button @confirm="confirmDelete()">
      <van-icon slot="title" name="warning-o" size="60px" color="#FDBE8E"/>
      <div style="color:#666666;font-size: 17px;padding: 35px 0;text-align: center">是否删除该群发消息?</div>
    </van-dialog>
    <!--继续群发弹出层-->
    <van-dialog confirmButtonColor="#2F8DEB" @confirm="confirm()" v-model="showDialog"  show-cancel-button>
      <div style="color:#666666;font-size: 17px;padding: 35px 0;text-align: center">是否继续群发该消息?</div>
    </van-dialog>
  </div>
</template>

<script>
import Vue from 'vue'
import {Col, Row, Icon, List, Cell, PullRefresh, Popup, ImagePreview, Dialog, Sticky, Button} from 'vant'
import {massMessagePage, deleteMessage} from '@/api/massMessaging.js'
import '@vant/touch-emulator'

Vue.use(Popup)
Vue.use(Icon)
Vue.use(Button)
Vue.use(Col)
Vue.use(Row)
Vue.use(List)
Vue.use(Cell)
Vue.use(PullRefresh)
Vue.use(ImagePreview)
Vue.use(Sticky)
Vue.use(Dialog)
export default {
  data () {
    return {
      list: [],
      showDelete: false,
      deleteItemId: 0,
      nursingId: localStorage.getItem('caringNursingId'),
      massMessageList: [],
      show: false,
      loading: false,
      finished: false,
      refreshing: false,
      showPop: false,
      showDialog: false,
      current: 1,
      timeOutEvent: 0,
      defaultImg: require('@/assets/articleNoImg.png'),
      dilogessage: {} // 点击继续发送获取的message
    }
  },
  created () {
    this.onLoad()
  },
  methods: {
    gtouchstart (item) {
      let self = this
      item.active = true
      this.timeOutEvent = setTimeout(function () {
        self.longPress(item)
      }, 500) // 这里设置定时器，定义长按500毫秒触发长按事件
      return false
    },
    // 手释放，如果在500毫秒内就释放，则取消长按事件，此时可以执行onclick应该执行的事件
    showDeleteButton (item) {
      item.active = false
      clearTimeout(this.timeOutEvent) // 清除定时器
      if (this.timeOutEvent !== 0) {
        // 这里写要执行的内容（如onclick事件）
        console.log('点击但未长按')
      }
      return false
    },
    // 如果手指有移动，则取消所有事件，此时说明用户只是要移动而不是长按
    gtouchmove (item) {
      item.active = false
      clearTimeout(this.timeOutEvent) // 清除定时器
      this.timeOutEvent = 0
    },
    // 真正长按后应该执行的内容
    longPress (item) {
      this.timeOutEvent = 0
      this.deleteItemId = item.id
      this.showDelete = true
      // 执行长按要执行的内容，如弹出菜单
      console.log('长按')
    },
    confirmDelete () {
      deleteMessage(this.deleteItemId).then(res => {
        this.showDelete = false
        if (res.code === 0) {
          let index = this.massMessageList.findIndex(item => item.id === this.deleteItemId)
          this.massMessageList.splice(index, 1)
        }
      })
    },
    onLoad () {
      this.loading = true
      if (this.loading) {
        const params = {
          'current': this.current,
          'model': {
            'senderId': this.nursingId
          },
          'size': 20
        }
        massMessagePage(params).then(res => {
          if (res.data) {
            this.massMessageList.push(...res.data.records)
            if (res.data.pages === 0 || res.data.pages === this.current) {
              this.finished = true
            } else {
              this.finished = false
              this.current++
            }
          }
          this.show = true
          this.loading = false
          this.refreshing = false
        })
      }
    },
    /**
     * 计算每个消息发送了多少人。
     */
    getMessagePeopleNumber (message) {
      const list = message.receiverIds.split(',')
      let length = 0
      for (let i = 0; i < list.length; i++) {
        if (list[i]) {
          length++
        }
      }
      return length
    },
    /**
     * 解析cms中的图片
     */
    getCmsImage (message) {
      const content = JSON.parse(message.content)
      if (content.icon === undefined) {
        return this.defaultImg
      } else {
        return content.icon
      }
    },
    getCmsContent (message) {
      const content = JSON.parse(message.content)
      return content.title
    },
    /**
     * 去看文章详细
     */
    clickMessage (message) {
      const content = JSON.parse(message.content)
      if (content.link) {
        this.$previewWeiXinCms(content.link)
      } else {
        this.$router.push({
          path: '/newsDispatch/articleDetails',
          query: {
            contentId: content.id
          }
        })
      }
    },
    /**
     * 继续群发
     */
    againSend (message) {
      this.showDialog = true
      this.dilogessage = message
    },
    /**
     * 点击继续群发确认按钮
     */
    confirm () {
      const params = {
        content: this.dilogessage.content,
        senderId: this.dilogessage.senderId,
        type: this.dilogessage.type
      }
      this.$router.push({
        path: '/newsDispatch/patientList',
        query: {
          sentType: 'againSend',
          continueSentMessageInfo: JSON.stringify(params)
        }
      })
    },
    /**
     * 打开一个图片预览
     */
    openImagePreview (image) {
      ImagePreview({
        images: [
          image
        ],
        showIndex: false,
        closeable: true
      })
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      this.massMessageList = []
      this.current = 1
      this.loading = true
      this.onLoad()
    },
    /**
     * 添加群发消息
     * @param val
     */
    sentMessage (val) {
      this.$router.push({
        path: '/newsDispatch/patientList',
        query: {
          sentType: 'add'
        }
      })
    },
    onBack () {
      this.$h5Close()
    }
  }
}
</script>

<style scoped>
.newDispatchList {
}

.newDispatchList-active{
  background: #E2E2E2 !important;
}

/deep/ .van-popup {
  border-radius: 9px !important;
  padding: 0 12px 32px 14px;
}
/deep/.van-button{
  height: 26px !important;
  border: 1px solid #B8B8B8 !important;
  color: #666 !important;
}
/deep/.van-dialog{
  border-radius: 7px !important;
}
</style>
