<template>
  <section class="allContent">
    <x-header :left-options="{showBack: false}">AI助手</x-header>
    <div style="padding-top: 50px;">
      <div style="overflow: scroll;" :style="computeHeight()" ref="msgContent"
           @click="closePopover()" @touchmove="msgBoxContentTouchmove">
        <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #f5f5f5"
                          :disabled="isDisabled">
          <van-list
            v-model="loading"
            :finished="finished"
            :style="computeListHeight()"
            @load="onLoad">
            <div v-for="(item, key) in list" :key="key"
                 style="background: #f5f5f5; outline: none; width: 100%; padding-top: 10px"
                 :id="'item' + key" :tabindex="key">
              <div v-if="key === 0 && isShow"
                   style="font-size: 12px; color: #999; text-align: center; margin-bottom: 10px">&lt;下拉加载更多&gt;
              </div>
              <!-- 时间 -->
              <div style="width: 100%; display: flex; justify-content: center">
                <div class="time"> {{ getTime(key) }}</div>
              </div>
              <!-- 发送消息后提示 -->
              <div v-if="item.senderRoleType === 'system'"
                   style="display: flex; align-items: center; flex-direction: column;justify-content: right; margin-top: 10px">
                <van-image
                  round
                  width="35"
                  height="35"
                  fit="cover"
                  :src="require('@/assets/my/ai_default.png')"
                />
                <div
                  style="background: white; padding: 9px 18px; border-radius: 25px; margin-top: -6px; font-size: 12px">
                  <span>【{{ aiInfo.aiAssistantName }}】正在回复，请稍等...</span>
                </div>
              </div>
              <!-- ai消息 -->
              <div v-else-if="item.senderRoleType === 'Ai'"
                   style="display: flex; margin-left: 15px; padding-top: 10px;">
                <div class="user">
                  <van-image
                    round
                    width="40"
                    height="40"
                    fit="cover"
                    :src="aiInfo.aiAssistantImage"
                  />

                </div>

                <div style="display: flex; width: 100%; flex-direction: column;">
                  <div class="user-text">{{ aiInfo.aiAssistantName }}</div>
                  <div style="display: flex;">
                    <img
                      src="@/assets/my/popleftarrow2x.png"
                      class="msg_popleftarrow"
                    />
                    <c-popover placement="bottom"
                               width="60"
                               trigger="manual"
                               v-model="item.showPopover">
                      <template>
                        <div style="width: 60px">
                          <van-cell v-for="(aitem, aIndex) in actions" :key="aIndex" :title="aitem.text"
                                    @click="onSelect(item, key)"/>
                        </div>
                      </template>
                      <div slot="reference" style="user-select: none; display: flex; outline: 0 !important;"
                           @touchstart="popoverGotouchstart(item, key)" @touchmove="popoverGotouchmove()"
                           @touchend="popoverGotouchend(item, key)">
                        <div class="msg" style="margin-right: 65px">
                          <guide v-if="item.content === 'ASSISTANT_INTRODUCTION'" :key-list="keyList"/>
                          <div v-else-if="item.type === 'HTML'" v-html="item.content"></div>
                          <system-article v-else-if="item.type === 'KEY_WORD_CMS'" :content="item.content"/>
                          <div v-else>
                            {{ item.content }}
                          </div>

                        </div>
                      </div>
                    </c-popover>

                  </div>
                </div>
              </div>
              <!-- 医生消息 -->
              <div v-else
                   style="display: flex; justify-content: flex-end; align-items: start; margin-right: 15px; padding-top: 10px;">

                <div style="width: 100%">
                  <div class="user-text" style="text-align: right; padding-right: 12px">{{ doctorInfo.name }}</div>

                  <div style="display: flex;  justify-content: flex-end; align-items: start; width: 100%">
                    <c-popover placement="bottom"
                               width="60"
                               trigger="manual"
                               v-model="item.showPopover">
                      <template>
                        <div style="width: 60px">
                          <van-cell v-for="(aitem, aIndex) in actions" :key="aIndex" :title="aitem.text"
                                    @click="onSelect(item, key)"/>
                        </div>
                      </template>
                      <div slot="reference" style="user-select: none; display: flex; outline: 0 !important;"
                           @touchstart="popoverGotouchstart(item, key)" @touchmove="popoverGotouchmove()"
                           @touchend="popoverGotouchend(item, key)">
                        <div class="msg" style="background-color: #0873DE; color: #fff; margin-left: 65px">
                          {{ item.content }}
                        </div>
                      </div>
                    </c-popover>

                    <img
                      v-if="item.senderRoleType !== 'Ai'"
                      src="@/assets/my/poprightarrow2x.png"
                      class="msg_poprightarrow"
                    />
                  </div>
                </div>
                <div class="user" style="float: right">
                  <van-image
                    round
                    width="40"
                    height="40"
                    fit="cover"
                    :src="doctorInfo.avatar"
                  />
                </div>
              </div>
              <div style="padding-bottom: 15px" v-if="key === list.length - 1"></div>
            </div>
          </van-list>
        </van-pull-refresh>
      </div>
      <!--输入框-->
      <div class="room_bar" ref="footer">
        <div class="room_bar_box" :style="{'padding-bottom': getIos() ? '20px' : '5px'}">
          <van-field
            ref="txtDom"
            id="caring_im_input"
            v-model="message"
            rows="1"
            :autosize="{ maxHeight: 100 }"
            @input="inputChange()"
            maxlength="500"
            :style="{'font-size': getIos() ? '16px' : '15px'}"
            class="sengTxt"
            type="textarea"
            placeholder="请输入"
          />

          <img :src="sendImg"
               style="margin-left: 8px"
               @click="createSse()"
               width="30px" height="30px">
        </div>

      </div>
    </div>

  </section>
</template>

<script>

import doctorApi from '@/api/doctor.js'
import {mapGetters} from "vuex";
import MonitorKeyboard from "../../pubilc/MonitorKeyboard";
import cPopover from '../../components/customPopover/index';
import {Dialog} from "vant";
import guide from "./components/guide";
import systemArticle from "./components/systemArticle";
import {EventSourcePolyfill} from 'event-source-polyfill';
import baseUrl from "../../api/baseUrl.js";

export default {
  name: "aiMessage",
  components: {
    cPopover,
    guide,
    systemArticle
  },
  data() {
    return {
      loading: false,
      finished: true,
      refreshing: false,
      message: '',
      imAccount: '',
      sendImg: require('@/assets/my/send.png'),
      aiInfo: {},
      doctorInfo: {},
      footerHeight: 0,
      baseFooterHeight: 0,
      isDisabled: false,
      actions: [{text: '复制'}],
      indexPopover: 0, //记录上一个长按的位置
      keyList: [],
      isDoctorKey: false,
      isShow: false,
    }
  },
  watch: {
    aiMsgStatus: function (val, old) {
      if (val) {
        this.sendImg = require('@/assets/my/blue_send.png')
      } else {
        this.sendImg = require('@/assets/my/send.png')
      }
    },
    msgPos: function (val, old) {
      setTimeout(() => {
        this.$nextTick(() => {
          const PageId = document.querySelector('#item' + val)
          PageId.scrollIntoView({behavior: "smooth", block: "center", inline: "start"})
        });
      }, 200)
    }
  },
  created() {
    this.getDoctorSendMsgStatus()
    this.getDocotrKeyWord()
    this.getContent()
  },
  computed: {
    ...mapGetters({
      aiMsgStatus: 'onAiMsgStatus',
      list: 'onAiMsgList',
      msgPos: 'onMsgPos',
    }),
  },
  mounted() {
    this.footerHeight = this.$refs.footer.offsetHeight
    this.baseFooterHeight = this.$refs.footer.offsetHeight
    this.getKeyboardState();
    const input = document.getElementById('caring_im_input')
    const ua = navigator.userAgent;
    const iOS = /iPad|iPhone|iPod/.test(ua);
    input.addEventListener('blur', () => {
      document.body.scrollIntoView();
    });
    input.addEventListener('focus', () => {
      setTimeout(() => {
        if (iOS) {
          if (!/OS 11_[0-3]D/.test(ua)) {
            document.body.scrollTop = document.body.scrollHeight;
          }
        } else {
          input.scrollIntoView(false);
        }
      }, 300);
    })
  },
  methods: {
    // 生成UUID
    generateUUID() {
      const uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = (Math.random() * 16) | 0,
          v = c === 'x' ? r : (r & 0x3) | 0x8;
        return v.toString(16);
      });
      return uuid;
    },

    // 查询关键词并返回订阅状态
    getDoctorHasKeyWord(userName) {
      doctorApi.getDoctorHasKeyWord(userName)
        .then(res => {
          this.isDoctorKey = res.data.data
        })
    },
    // 查询关键词并返回订阅状态
    getDocotrKeyWord() {
      doctorApi.getDocotrKeyWord()
        .then(res => {
          this.keyList = res.data.data
          console.log(this.keyList)
        })
    },
    /**
     * 消息内容页面被触屏 并 移动。
     * 猜测可能是用户在滑动屏幕 。此时收起软键盘
     */
    msgBoxContentTouchmove() {
      if (this.isKeyboard) {
        document.activeElement.blur();
      }
      this.closePopover()
    },
    //点击其他地方关闭弹窗
    closePopover() {
      if (this.list[this.indexPopover].showPopover) {
        this.list[this.indexPopover].showPopover = false
      }
    },
    // 长按文本
    popoverGotouchstart(item, index) {
      let that = this
      this.list[this.indexPopover].showPopover = false
      this.timeOutEvent = setTimeout(() => {
        that.indexPopover = index
        item.showPopover = true
        that.list.splice(index, item)
      }, 500)
    },
    // 移除长按
    popoverGotouchmove() {
      clearTimeout(this.timeOutEvent)
    },
    // 手指抬起
    popoverGotouchend(item, index) {
      clearTimeout(this.timeOutEvent)
    },
    // 复制文本
    onSelect(item, index) {
      let value = this.list[index]
      value.showPopover = false
      this.list.splice(index, value)
      Dialog.confirm({
        title: '复制',
        message: '确定要复制此内容到剪切板吗'
      })
        .then(() => {
          var input = document.createElement('input') // 创建input对象
          input.value = this.list[index].content // 设置复制内容
          document.body.appendChild(input) // 添加临时实例
          input.select() // 选择实例内容
          document.execCommand('Copy') // 执行复制
          document.body.removeChild(input) // 删除临时实例
        })
        .catch(() => {
          // on cancel
        })
    },
    //设置时间
    getTime(index) {
      if (index === 0) {
        return moment(this.list[index].createTime).format('yyyy-MM-DD HH:mm')
      } else {
        //当前时间大于上一条消息 15分钟
        if (moment(this.list[index].createTime).diff(moment(this.list[index - 1].createTime), "seconds") > 300) {
          return moment(this.list[index].createTime).format('yyyy-MM-DD HH:mm')
        } else {
          return ''
        }
      }
    },
    scollBottom() {
      setTimeout(() => {
        const dom = this.$refs.msgContent
        if (!dom) return
        dom.scrollTop = dom.scrollHeight - dom.clientHeight;
      }, 0)
    },
    getKeyboardState() {
      this.monitorKeyboard = new MonitorKeyboard();
      this.monitorKeyboard.onStart();

      // 监听虚拟键盘弹出事件
      this.monitorKeyboard.onShow(() => {
        this.scollBottom()
        this.isKeyboard = true
        this.closePopover()
      })

      //监听键盘收起的事件
      this.monitorKeyboard.onHidden(() => {
        this.isKeyboard = false
      })
    },
    inputChange() {
      this.footerHeight = this.$refs.footer.offsetHeight
    },
    // 发送消息
    onSendTextMsg() {
      if (this.message.length == 0) {
        return
      }
      if (!this.aiMsgStatus) {
        this.$toast('\n【' + this.aiInfo.aiAssistantName + '】\n\r    正在回复，请稍等...    \n ');
        return
      }
      let params = {
        content: this.message,
        imGroupId: localStorage.getItem('caring_doctor_id'),
        senderId: localStorage.getItem('caring_doctor_id'),
        senderImAccount: this.doctorInfo.imAccount,
        senderRoleType: 'doctor',
        type: 'text',
      }

      doctorApi.sendGPTMsg(params)
        .then(res => {
          if (res.data.code === 0 && res.data) {
            Vue.$store.commit('updateAiMessage', {
              message: {
                ext: {
                  content: res.data.data.content,
                  createTime: res.data.data.createTime,
                  senderRoleType: 'doctor',
                }
              }
            })
            if (res.data.data.cancelKeyWordMsg === 1) {
              this.$vux.toast.text('取消订阅成功', 'center')
            } else if (res.data.data.cancelKeyWordMsg === 2) {
              this.$vux.toast.text('无效退订，您未订阅该内容', 'center')
            } else {
              Vue.$store.commit('aiMsgStatus', {
                aiMsgStatus: false
              })
              this.sendImg = require('@/assets/my/send.png')
            }
            this.message = ''
            this.footerHeight = this.baseFooterHeight
          }
        })
    },

    createSse() {
      if (this.message.length == 0) {
        return
      }
      if (!this.aiMsgStatus) {
        this.$toast('\n【' + this.aiInfo.aiAssistantName + '】\n\r    正在回复，请稍等...    \n ');
        return
      }

      let uid = this.generateUUID()
      let url = baseUrl + '/msgs/gptDoctorChat/createSse';
      let eventSource = new EventSourcePolyfill(url, {
        headers: {
          'token': 'Bearer ' + localStorage.getItem('doctortoken'),
          'uid': uid
        }
      });

      let sse;
      eventSource.onopen = (event) => {
        console.log("开始输出后端返回值");
        sse = event.target;
        Vue.$store.commit('aiMsgStatus', {
          aiMsgStatus: true
        })
      };

      let text = '';
      let flag = true
      eventSource.onmessage = (event) => {
        if (event.data == "[DONE]") {
          // 结束
          if (sse) {
            console.log('sse会话结束')
            sse.close();
          }
          return;
        }

        let json_data = JSON.parse(event.data);
        if (json_data.content == null || json_data.content == "null") {
          return;
        }

        text = text + json_data.content;
        if (flag) {
          // 如果是新消息，需要添加到消息列表里面
          Vue.$store.commit('updateAiMessage', {
            message: {
              ext: {
                content: text,
                senderRoleType: 'Ai',
              }
            }
          })
          flag = false
        } else {
          // 不是新消息，则需要更新文本内容
          Vue.$store.commit('updateCurrentAiMessage', {
            message: {
              ext: {
                content: text,
              }
            }
          })
          setTimeout(() => {
            this.scollBottom()
          }, 200)
        }

      };

      eventSource.onerror = (event) => {
        console.log("服务连接异常请重试！");
        if (event.readyState === EventSource.CLOSED) {
          console.log("connection is closed");
        } else {
          console.log("Error occured", event);
        }
        event.target.close();
      };

      let params = {
        content: this.message,
        imGroupId: localStorage.getItem('caring_doctor_id'),
        senderId: localStorage.getItem('caring_doctor_id'),
        senderImAccount: this.doctorInfo.imAccount,
        senderRoleType: 'doctor',
        type: 'text',
        uid: uid,
      }

      doctorApi.sseChat(params)
        .then(res => {
          if (res.data.code === 0 && res.data) {
            Vue.$store.commit('updateAiMessage', {
              message: {
                ext: {
                  content: this.message,
                  createTime: res.data.data.createTime,
                  senderRoleType: 'doctor',
                }
              }
            })

            if (res.data.data.cancelKeyWordMsg === 1) {
              this.$vux.toast.text('取消订阅成功', 'center')
            } else if (res.data.data.cancelKeyWordMsg === 2) {
              this.$vux.toast.text('无效退订，您未订阅该内容', 'center')
            } else {
              Vue.$store.commit('aiMsgStatus', {
                aiMsgStatus: false
              })
              this.sendImg = require('@/assets/my/send.png')
            }
            this.message = ''
            this.footerHeight = this.baseFooterHeight
          }
        })
    },

    // 获取消息状态
    getDoctorSendMsgStatus() {
      doctorApi.getDoctorSendMsgStatus()
        .then(res => {
          Vue.$store.commit('aiMsgStatus', {
            aiMsgStatus: res.data.data
          })
          if (this.aiMsgStatus) {
            this.sendImg = require('@/assets/my/blue_send.png')
          } else {
            this.sendImg = require('@/assets/my/send.png')
          }
        })
    },
    // 获取消息列表
    getAiChatListPage() {
      Vue.$store.commit('updateMsgPos', {
        pos: 0
      })
      doctorApi.getAiChatListPage(this.list && this.list.length > 0 ? this.list[0].createTime : moment().format("yyyy-MM-DD HH:mm:ss.SSS"))
        .then(res => {
          if (this.list && this.list.length === 0 && res.data.data.length < 20) {
            this.isDisabled = true
          }
          if (res.data.data && res.data.data.length < 20) {
            this.isShow = false
          } else {
            this.isShow = true
          }
          Vue.$store.commit('setAiMessageList', {
            messageList: res.data.data, isEmpty: false, pos: res.data.data.length
          })
          this.loading = false;
        })
    },
    getGTPLastNewMessage() {
      Vue.$store.commit('updateMsgPos', {
        pos: 0
      })
      doctorApi.getGTPLastNewMessage()
        .then(res => {
          if (res.data.data) {
            Vue.$store.commit('setAiMessageList', {
              messageList: [res.data.data], isEmpty: false, pos: 0
            })
            if (res.data.data.content === 'ASSISTANT_INTRODUCTION') {
              this.isShow = false
            } else {
              this.isShow = true
            }
          }

          this.loading = false;
        })
    },
    // 获取AI信息
    getAIInfo() {
      doctorApi.getAiInfo()
        .then(res => {
          this.aiInfo = res.data.data
          this.getGTPLastNewMessage()
        })
    },
    onRefresh() {
      this.loading = true;
      this.onLoad();
    },
    onLoad() {
      setTimeout(() => {
        if (this.refreshing) {
          this.refreshing = false;
        }
        this.getAiChatListPage()
      }, 500);
    },
    getIos() {
      var u = navigator.userAgent;
      var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
      if (isiOS) {
        return true
      }
      return false
    },
    // 获取医生信息
    getContent() {
      const params = {
        id: localStorage.getItem('caring_doctor_id')
      }
      doctorApi.getContent(params)
        .then(res => {
          this.doctorInfo = res.data.data
          const userName = res.data.data.imAccount
          const password = '123456'
          this.imAccount = userName
          localStorage.setItem('userImAccount', userName)
          setTimeout(() => {
            this.getDoctorHasKeyWord(this.imAccount)
          }, 150)
          let options = {
            apiUrl: WebIM.config.apiURL,
            user: userName,
            pwd: password,
            appKey: WebIM.config.appkey
          }
          WebIM.conn.open(options)
          this.getAIInfo()
        })
    },
    //计算高度
    computeHeight() {
      return 'height: calc(100vh - ' + (50 + this.footerHeight + 10) + 'px)'
    },
    computeListHeight() {
      if (this.list && this.list.length === 1 && this.list[0].content && this.list[0].content.length < 300) {
        return 'min-height: calc(100vh - ' + (50 + this.footerHeight + 10) + 'px)'
      }else {
        return ''
      }
    }
  }
}
</script>

<style lang="less" scoped>

.allContent {
  width: 100vw;
  height: 100vh;
  background: #f5f5f5;

  .room_bar {
    width: 100%;
    height: auto;
    position: fixed;
    bottom: 0;
    right: 0;
    z-index: 1;
    border-top: 1px solid #d8d8d8;
    background-color: #FFFFFF;
    /* transform: translateZ(1000px); */

    .room_bar_box {
      margin: 12px 15px 15px 15px;
      display: flex;
      align-items: center;

      .sengTxt {
        text-align: left;
        box-sizing: border-box;
        font-size: 14px;
        box-shadow: 0 0 0 0;
        background-color: #F5F5F9;
        outline-style: none;
        color: #333333;
        border-radius: 5px;

        ::-webkit-scrollbar {
          width: 0 !important;
        }

        ::-webkit-scrollbar {
          width: 0 !important;
          height: 0;
        }
      }
    }
  }

  .user {
    font-size: 12px;
    display: flex;
    flex-direction: column;
  }

  .user-text {
    font-size: 12px;
    padding-left: 12px;
    color: #666666;
  }

  .time {
    width: fit-content;
    display: flex;
    justify-content: center;
    background: #d9d9d9;
    font-size: 12px;
    color: #999999;
    border-radius: 8px;
    padding: 0px 10px;
  }

  .msg_poprightarrow {
    height: 11px;
    width: 12px;
    margin-top: 15px;
  }

  .msg_popleftarrow {
    height: 11px;
    width: 12px;
    margin-top: 15px;
  }

  .msg {
    display: inline-block;
    padding: 8px 12px;
    font-size: 14px;
    /*overflow: hidden;*/
    text-align: left;
    word-break: break-all;
    background-color: #fff;
    border-radius: 8px;
    position: relative;
    height: fit-content;
    margin-top: 5px;
  }

  .self {
    display: flex;
    flex-direction: row-reverse;
  }

  .self .msg {
    background-color: #0873DE;
    color: #fff;
  }


  .self .user {
    font-size: 12px;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
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

/deep/ .van-cell {
  padding: 10px 16px;
}
</style>
