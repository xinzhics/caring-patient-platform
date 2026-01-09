<template>
  <div style="height: 100%; width: 100%; margin: 0 auto" class="messagebox">
    <x-header :left-options="{showBack: false}">{{ pageTitle ? pageTitle : '小组' }}
      <img slot="right" :src="this.headerImg"
           width="20px" height="20px"
           @click="OnClick">
    </x-header>
    <!--聊天列表页面-->
    <div class="messagebox-content" ref="msgContent"
         id="resultScroll"
         :style="'height: calc(100vh - 122px)'">

      <van-pull-refresh v-model="isLoading" @refresh="loadMoreMsgs">
        <div
          v-for="(item,i) in messageList"
          :key="i"
          class="message-group"
        >
          <!--第一条信息显示时间-->
          <div v-if="i === 0"
               style="display: flex; align-items: center; justify-content: center; font-size: 12px; color: #9B9B9B; margin-top: 8px; margin-bottom: 8px">
            {{ renderTime(item.createTime) }}
          </div>
          <!--大于5分组显示时间-->
          <div v-else-if="isShowDate(item.createTime, messageList[i - 1].createTime)"
               style="display: flex; align-items: center; justify-content: center; font-size: 12px; color: #9B9B9B; margin-top: 8px; margin-bottom: 8px">
            {{ renderTime(item.createTime) }}
          </div>

          <div v-if="item.content === 'guest_join_group_event' || item.content === 'guest_drop_out_group_event'"
               style="color: #999999; font-size: 10px; margin-bottom: 5px; text-align: center; margin-left: 15px; margin-right: 15px;">
            {{ getTipsString(item.senderName, item.senderRoleRemark, item.content) }}
          </div>

          <div v-else-if="item.senderImAccount !== imAccount"
               style="display: inline-block; margin-left: 15px; margin-top: 8px">
            <div style="display: flex; float: left;">
              <div>
                <div>
                  <img width="35px" height="35px"
                       style="border-radius: 50%;"
                       v-bind:src=loadingAvatar(item.senderAvatar,item.senderRoleType)
                  />
                </div>
                <div style="font-size: 12px; color: #3a3a3a; width: 40px; text-align: center;
                  display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp:1; overflow: hidden">{{
                    item.senderName
                  }}
                </div>
              </div>

              <div style="display: flex;">
                <div
                  style="margin-top: 6px; right: -16px; width:0;height:0; font-size:0; border:solid 8px;border-color:#F5F5F9 #ffffff #F5F5F9 #F5F5F9">
                </div>
                <div
                  v-if="'text' ===item.type"
                  style="text-align: left; margin-right: 60px; word-wrap: break-word; word-break: break-all; overflow: hidden;
                background: #FFF; padding: 10px; border-radius: 5px; height: fit-content">
                  {{ item.content }}
                </div>
                <div
                  v-if="'image' ===item.type"
                  style="text-align: left; margin-right: 60px; word-wrap: break-word; word-break: break-all; overflow: hidden;
                background: #FFF; padding: 10px; border-radius: 5px">
                  <van-image width="100" height="100" :src="item.content"
                             @click="showBigPicture(item.content)"/>
                </div>
                <div
                  v-if="'cms' ===item.type"
                  style="text-align: left; margin-right: 60px; word-wrap: break-word; word-break: break-all; overflow: hidden;
                background: #FFF; padding: 10px; border-radius: 5px; height: fit-content">
                  <div style="background: #F5F5F5; align-items: center; width: 100%; height: 100%; display: flex"
                       @click="seeCMS(JSON.parse(item.content).id)">
                    <div style="width: 30px; height: 30px">
                      <van-image
                        width="30"
                        height="30"
                        fit="cover"
                        :src="JSON.parse(item.content).icon"/>
                    </div>

                    <div style="margin-left: 7px; margin-right: 7px;
                          display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp:2; overflow: hidden">
                      {{ JSON.parse(item.content).title }}
                    </div>
                  </div>
                </div>
              </div>
            </div>

          </div>

          <div v-else
               style="display: flex; float: right; margin-right: 15px; margin-top: 8px">
            <div style="display: flex">
              <div
                v-if="'text' ===item.type"
                style="text-align: left; margin-left: 60px; word-wrap: break-word; word-break: break-all; overflow: hidden;
                background: #3F86FF; padding: 10px; border-radius: 5px; color: white">
                {{ item.content }}
              </div>

              <div
                v-if="'image' ===item.type"
                style="text-align: right; margin-left: 60px; word-wrap: break-word; word-break:
              break-all; overflow: hidden; font-size: 14px; color: #3a3a3a; background: #3F86FF; padding: 10px; border-radius: 5px">
                <van-image width="100" height="100" :src="item.content"
                           @click="showBigPicture(item.content)"/>
              </div>

              <div
                style="text-align: right; margin-top: 6px; right: -16px; width:0;height:0; font-size:0; border:solid 8px;border-color: #F5F5F9 #F5F5F9 #F5F5F9 #3F86FF;">
              </div>
            </div>
            <img width="35px" height="35px"
                 style="border-radius: 50%"
                 v-bind:src=item.senderAvatar
            />
          </div>
        </div>
      </van-pull-refresh>
    </div>
    <!--聊天输入框-->
    <div class="messagebox-footer">
      <van-row style="width: 100%; height: 100%; display: flex; align-items: center">
        <van-col span="3">
          <van-uploader style="display: flex; align-items: center; justify-content: center"
                        :after-read="onUpload">
            <img :src="require('@/assets/my/photo.png')"
                 @click="onSendTextMsg('text', '')"
                 width="25px" height="25px">
          </van-uploader>


        </van-col>
        <van-col span="18">
          <div class="fotter-send" style="background-color: #FFF ;width: 100%; height: 40px;">
            <a-input
              v-model="message"
              equired
              placeholder="请输入消息"
              class="sengTxt"
              style="resize:none"
              :change="inputSend()"
              ref="txtDom"
            />
          </div>
        </van-col>
        <van-col span="3"
                 style="display: flex; align-items: center; justify-content: center">
          <img :src="sendImg"
               @click="onSendTextMsg('text', '')"
               width="25px" height="25px">
        </van-col>
      </van-row>
    </div>

    <loading :show="isSend" @click="isSend = true"/>
  </div>
</template>

<script>
import "./consultationImIndex.less";
import moment from "moment";
import Api from '@/api/Content.js'
import {Flexbox, FlexboxItem} from 'vux'
import {mapActions, mapGetters} from "vuex";
import {ImagePreview, Toast} from "vant";
import UploadImg from "../../components/utils/UploadImg";
import loading from '../../components/loading/ChrysanthemumLoading'
import wx from "weixin-js-sdk";

export default {
  name: "index",
  components: {
    Flexbox,
    loading,
    FlexboxItem,
    [ImagePreview.Component.name]: ImagePreview.Component,
  },
  data() {
    return {
      message: "",
      headerImg: require('@/assets/my/dc_spot.png'),
      sendImg: require('@/assets/my/send.png'),
      isLoading: false,
      imAccount: "",
      groupId: "",
      userId: "",
      memberRole: "",
      pageTitle: "",
      isSend: false,
      dictionaryList: {}
    };
  },
  computed: {
    ...mapGetters({
      messageList: "onGetGroupMessageList",
      updateMessage: "isUpdateGroupMessage",
      isQRCode: "getIsQRCode"
    }),
  },
  updated() {
    if (this.updateMessage) {
      this.scollBottom();
      this.$store.commit("setGroupLoading", {loading: false});
    }
  },
  methods: {
    //是否加载日期， 如当前时间大于之前时间5分钟， 则显示日期
    isShowDate(currentTime, oldTime) {
      let clong = moment(currentTime).unix()
      let olong = moment(oldTime).unix()
      if ((clong - olong) > 300) {
        return true
      } else {
        return false
      }
    },
    getTipsString(name, senderRoleRemark, content) {
      if ("guest_join_group_event" === content) {
        if (senderRoleRemark == '患者') {
          return name + "(" + this.dictionaryList.get('patient') + ") 加入小组"
        } else if (senderRoleRemark == '医生') {
          return name + "(" + this.dictionaryList.get('doctor') + ") 加入小组"
        } else {
          return name + "(" + senderRoleRemark + ") 加入小组"
        }
      } else {
        if (senderRoleRemark == '患者') {
          return name + "(" + this.dictionaryList.get('patient') + ") 已离开小组"
        } else if (senderRoleRemark == '医生') {
          return name + "(" + this.dictionaryList.get('doctor') + ") 已离开小组"
        } else {
          return name + "(" + senderRoleRemark + ") 已离开小组"
        }
      }
    },
    inputSend() {
      if (this.$data.message == "") {
        this.sendImg = require('@/assets/my/send.png')
      } else {
        this.sendImg = require('@/assets/my/blue_send.png')
      }
    },

    loadingAvatar(avatar, type) {
      if ("NursingStaff" === type) {
        return avatar ? avatar : require('@/assets/my/nursingstaff_avatar.png')
      } else if ("doctor" === type) {
        return avatar ? avatar : require('@/assets/my/doctor_avatar.png')
      } else {
        return avatar
      }
    },

    handleScroll() {
      var that = this
      var st = that.$refs['msgContent'].scrollTop // 滚动条距离顶部的距离
      if (st === 0) {
        this.getMessageList(true)
      }
    },
    //获取聊天消息
    getMessageList(upload) {
      const params = {
        consultationId: this.groupId,
        createTime: !upload ? Date.parse(new Date()) : (moment(this.messageList[0].createTime).unix() * 1000),
      }
      let that = this
      Api.getGroupMessageList(params).then(response => {
        if (response.data.data.length > 0) {
          if (upload) {
            let data = response.data.data.reverse()
            that.$store.commit("onGroupMessageList", {
              messageList: data,
              upload: upload,
            });
          } else {
            that.$store.commit("onGroupMessageList", {
              messageList: response.data.data,
              upload: upload,
            });
          }
        }
        that.isLoading = false;
        if (!upload) {
          that.scollBottom()
        }
      })
        .catch(function (error) { // 请求失败处理
          console.log(error);
          that.isLoading = false;
        });

    },
    //加载更多
    loadMoreMsgs() {
      this.getMessageList(true)
    },
    //查看大图
    showBigPicture(image) {
      ImagePreview({
        images: [
          image
        ],
        maxZoom: 10
      });
    },
    //cms链接
    seeCMS(id) {
      this.$router.push({path: '/cmsz/show', query: {id: id}})
    },
    //是否隐藏患者聊天群组
    OnClick() {
      this.$router.push({path: '/consultationDetail', query: {id: this.groupId}})
    },
    //发送文本信息
    onSendTextMsg(type, message) {
      if ((type === 'text' && this.$data.message == "") || (type === 'text' && this.$data.message == "\n")) {
        this.$data.message = "";
        return;
      }
      this.isSend = true
      const params = {
        type: type,
        content: type === 'text' ? this.$data.message : message,
      }
      let that = this
      Api.sendGroupMessage(this.imAccount, this.groupId, params).then((res) => {
        if (res.data.code !== 0) {
          this.$vux.toast.text(res.data.msg, 'center')
        } else {
          this.$store.commit("updateMyGroupMessage", {
            message: {
              createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
              content: type === 'text' ? this.$data.message : message,
              type: type,
              senderAvatar: res.data.data.senderAvatar,
              senderRoleType: this.memberRole,
              senderImAccount: this.imAccount,
              senderId: this.userId
            }
          });
          if (type === 'text') {
            this.$data.message = "";
          }
          this.scollBottom()
        }
        that.isSend = false
      }).catch(function (error) { // 请求失败处理
        console.log(error);
        that.isSend = false
      });
    },
    // TODO 可以抽离到utils
    renderTime(time) {
      const nowStr = new Date();
      const localStr = time ? new Date(time.replace(/-/g, '/')) : nowStr;
      const localMoment = moment(localStr);
      const localFormat = localMoment.format("MM-DD HH:mm");
      return localFormat === 'Invalid date' ? "" : localFormat;
    },

    scollBottom() {
      setTimeout(() => {
        const dom = this.$refs.msgContent;
        if (!dom) return;
        dom.scrollTop = dom.scrollHeight;
      }, 0);
    },
    //上传图片
    async onUpload(files) {
      this.$vux.loading.show({
        text: '上传中'
      })

      if (files) {
        const isJPG = (files.file.type === 'image/jpeg' || files.file.type === 'image/png')
        if (!isJPG) {
          this.$vux.loading.hide()
          Toast('上传图片只能为jpg或者png!')
          return
        }
        let formData = new FormData()
        const isLt2M = files.file.size / 1024 / 1024 < 2
        if (!isLt2M) {
          let maxSize = 100 * 1024;
          UploadImg.imageHandle(files.file, maxSize).then(file => {
            formData.append('file', file)
            formData.append('folderId', 1308295372556206080)

            Api.updateImg(formData).then((res) => {
              if (res.data.code === 0) {
                this.onSendTextMsg('image', res.data.data.url)
              } else {
                this.$message({
                  message: '文件上传失败！',
                  type: 'error'
                })
              }
              this.$vux.loading.hide()
            })
          })

          return;
        } else {
          formData.append('file', files.file)
          formData.append('folderId', 1308295372556206080)
          Api.updateImg(formData).then((res) => {
            if (res.data.code === 0) {
              this.onSendTextMsg('image', res.data.data.url)
            } else {
              this.$message({
                message: '文件上传失败！',
                type: 'error'
              })
            }
            this.$vux.loading.hide()
          })
        }
      }
    },
  },
  created() {
    //获取服务器IM历史聊天页面
    this.groupId = this.$route.query.groupId
    localStorage.setItem('groupId', this.groupId)
    this.pageTitle = this.$route.query.pageTitle
    this.memberRole = localStorage.getItem("memberRole");
    this.userId = localStorage.getItem("consultationUserId");
    this.imAccount = localStorage.getItem("memberImAccount");
    let dict = JSON.parse(localStorage.getItem("dictionary"))
    this.dictionaryList = new Map()
    if (dict && dict.length > 0) {
      for (let i = 0; i < dict.length; i++) {
        this.dictionaryList.set(dict[i].code, dict[i].name)
      }
    }else {
      this.dictionaryList.set('doctor', '医生')
      this.dictionaryList.set('patient', '患者')
      this.dictionaryList.set('assistant', '医助')
      this.dictionaryList.set('register', '注册')
      this.dictionaryList.set('notregister', '未注册')
      this.dictionaryList.set('activation', '激活')
      this.dictionaryList.set('notactivation', '未激活')
      this.dictionaryList.set('registerrate', '注册转化率')
      this.dictionaryList.set('unfollowrate', '会员取关率')
      this.dictionaryList.set('diagnostictype', '诊断类型')
    }
    const password = "123456";
    var options = {
      apiUrl: WebIM.config.apiURL,
      user: this.imAccount,
      pwd: password,
      appKey: WebIM.config.appkey
    };
    WebIM.conn.open(options);

    setTimeout(() => {
      if (!this.updateMessage) {
        this.getMessageList(false);
      }
    }, 2000);
  },
};
</script>

<style scoped lang='less'>
.byself {
  float: right;
}

.recallMsg {
  font-size: 12px;
  color: #aaa;
  width: 100%;
  text-align: center;
}

.custom-title {
  font-weight: 500;
}

/*initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no*/
.moreMsgs {
  background: #ccc !important;
  border-radius: 8px;
  width: calc(100% - 30);
  width: 50%;
  cursor: pointer;
  text-align: center;
}

.status {
  display: inline;
  position: relative;
  top: 20px;
  font-size: 12px;
  left: -6px;
  color: #736c6c;
  float: left;
}

.unreadNum {
  float: left;
  width: 100%;
}

.icon-style {
  display: inline-block;
  background-color: #f04134;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  color: white;
  line-height: 1.5;
  text-align: center;
}

.emoji-style {
  width: 22px;
  float: left;
}

.img-style {
  max-width: 350px;
}

.time-style {
  clear: both;
  margin-left: 2px;
  margin-top: 3px;
  margin-top: 1px;
  font-size: 12px;
  color: #888c98;
}

.file-style {
  width: 240px;
  margin: 2px 2px 2px 0;
  font-size: 13px;

  h2 {
    border-bottom: 1px solid #e0e0e0;
    font-weight: 300;
    text-align: left;
  }

  h3 {
    max-width: 100%;
    font-size: 15px;
    height: 20px;
    line-height: 20px;
    font-weight: 600;
    -o-text-overflow: ellipsis;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    text-align: left;
    margin-bottom: 20px;
  }

  .bottom {
    span {
      color: #999999;
      text-align: left;
    }
  }

  a {
    color: #999999;
    float: right;
    text-decoration: none;
  }

  .el-dropdown-link {
    cursor: pointer;
    color: #409eff;
  }

  .el-icon-arrow-down {
    font-size: 12px;
  }
}

/deep/ .vux-header {
  margin-bottom: 0px;
  height: 50px;
}

/deep/ .van-uploader__input-wrapper {
  display: flex;
}

</style>
