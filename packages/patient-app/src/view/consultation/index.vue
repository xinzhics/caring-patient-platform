<template>
  <div style="height: 100%; width: 100%; margin: 0 auto" class="messagebox">
    <x-header :left-options="{showBack: false}" @on-click-back="backParent">{{ pageTitle ? pageTitle : '小组' }}
      <img slot="right" :src="this.headerImg"
           width="20px" height="20px"
           @click="OnClick">
    </x-header>
    <!--聊天列表页面-->
    <div class="messagebox-content" ref="msgContent"
         id="resultScroll"
         :style="computeHeight()">

      <van-pull-refresh v-model="isLoading" @refresh="loadMoreMsgs">
        <div>
          <div

            v-for="(item,i) in messageList"
            :key="i"
            class="message-group"
          >
            <div
              v-if="i % 10 === 0 || item.content === 'guest_join_group_event' || item.content === 'guest_drop_out_group_event'"
              style="display: flex; align-items: center; justify-content: center; font-size: 12px; color: #9B9B9B; margin-top: 8px; margin-bottom: 8px">
              {{ renderTime(item.createTime) }}
            </div>

            <div v-if="item.content === 'guest_join_group_event' || item.content === 'guest_drop_out_group_event'"
                 style="color: #999999; font-size: 10px; margin-bottom: 5px; text-align: center; margin-left: 15px; margin-right: 15px;">
              {{ getTipsString(item.senderName, item.senderRoleRemark, item.content) }}
            </div>

            <div v-else-if="item.senderRoleType !== 'patient'"
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
                         @click="seeCMS(JSON.parse(item.content))">
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
        </div>
      </van-pull-refresh>
    </div>
    <!--聊天输入框-->
    <div class="messagebox-footer" ref="footer">
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
          <div class="fotter-send">

            <van-field
              ref="txtDom"
              id="caring_im_input"
              v-model="message"
              rows="1"
              autosize
              @input="inputSend()"
              class="sengTxt"
              type="textarea"
              style="resize:none"
              placeholder="请输入"
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
import Vue from 'vue';
import "./consultationImIndex.less";
import Api from '@/api/Content.js'
import {Flexbox, FlexboxItem} from 'vux'
import {mapActions, mapGetters} from "vuex";
import {ImagePreview, Toast, Field} from "vant";
import UploadImg from "../../components/utils/UploadImg";
import loading from '../../components/loading/ChrysanthemumLoading'

Vue.use(Field);
import wx from 'weixin-js-sdk'
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
      pageTitle: '',
      message: "",
      headerImg: require('@/assets/my/dc_spot.png'),
      sendImg: require('@/assets/my/send.png'),
      isLoading: false,
      imAccount: "",
      groupId: "",
      isSend: false,
      name: "",
      patient: "",
      offsetHeight: 0,
    };
  },
  computed: {
    ...mapGetters({
      messageList: "onGetGroupMessageList",
      updateMessage: "isUpdateGroupMessage",
    }),
  },
  mounted() {
    // 监听滚动事件
    document.getElementById('resultScroll').addEventListener('scroll', this.handleScroll, true)

    this.name = this.$getDictItem('doctor')
    this.patient = this.$getDictItem('patient')
    setTimeout(() => {
      this.footerHeight = this.$refs.footer.offsetHeight
    }, 100)
    window.addEventListener('popstate', this.onPopstate);
  },
  beforeDestroy() {
    // 在组件销毁前移除事件监听
    window.removeEventListener('popstate', this.onPopstate);
  },
  updated() {
    console.log(this.updateMessage)
    if (this.updateMessage) {

      this.scollBottom();
      //this.updateMessageState();
      this.$store.commit("setGroupLoading", {loading: false});
    }
  },

  methods: {
    onPopstate() {
      wx.closeWindow()
    },
    backParent() {
      wx.closeWindow()
    },
    computeHeight() {
      return 'height: calc(100vh - ' + (130 + this.offsetHeight) + 'px)'
    },
    inputSend() {
      if (this.$data.message == "") {
        this.sendImg = require('@/assets/my/send.png')
      } else {
        this.sendImg = require('@/assets/my/blue_send.png')
      }
    },
    getTipsString(name, senderRoleRemark, content) {
      if ("guest_join_group_event" === content) {
        return name + "(" + senderRoleRemark + ") 加入小组"
      } else {
        return name + "(" + senderRoleRemark + ") 已离开小组"
      }
    },
    getRole(roleType) {
      if ("roleDoctor" === roleType) {
        return this.name
      } else if ("rolePharmacist" === roleType) {
        return "药师";
      } else if ("roleChineseDoctor" === roleType) {
        return "中医师";
      } else if ("roleTherapists" === roleType) {
        return "康复师";
      } else if ("roleDietitian" === roleType) {
        return "营养师";
      } else if ("roleOther" === roleType) {
        return "其他";
      } else if ("patient" === roleType) {
        return this.patient
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
        createTime: !upload ? Date.parse(new Date()) : this.messageList.length > 0 ? (moment(this.messageList[0].createTime).unix() * 1000) : Date.parse(new Date()),
      }
      let that = this
      Api.getGroupMessageList(params).then(response => {
        if (response.data.data.length > 0) {
          this.$store.commit("onGroupMessageList", {
            messageList: response.data.data,
            upload: upload,
          });
        }
        this.isLoading = false;
        if (!upload) {
          this.scollBottom()
          this.timeOutEvent = setTimeout(function () {
            that.$store.commit("setFirstLoading", {
              messageList: response.data.data,
              upload: upload,
            });
          }, 1500);//这里设置定时
        }
      })
        .catch(function (error) { // 请求失败处理
          that.isLoading = false;
          if (!upload) {
            that.timeOutEvent = setTimeout(function () {
              that.$store.commit("setFirstLoading", {
                messageList: [],
                upload: upload,
              });
            }, 1500);//这里设置定时
          }
        });

    },
    //加载更多
    loadMoreMsgs() {
      this.$refs.txtDom.blur();
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
    seeCMS(item) {
      if (item.link) {
        location.href = item.link
      } else {
        this.$router.push({path: '/cmsz/show', query: {id: item.id}})
      }
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

      Api.sendGroupMessage(this.imAccount, this.groupId, params).then((res) => {
        this.$store.commit("updateMyGroupMessage", {
          message: {
            createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
            content: type === 'text' ? this.$data.message : message,
            type: type,
            senderAvatar: res.data.data.senderAvatar,
            senderRoleType: "patient",
          }
        });
        if (type === 'text') {
          this.$data.message = "";
        }
        this.scollBottom()
        this.isSend = false
      }).catch(function (error) { // 请求失败处理
        console.log(error);
        this.isSend = false
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
        this.$compressionImg.compressImg(files.file).then(f => {
          var formData = new FormData()
          formData.append('file', f)
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
      }
    },
    //压缩图片
    compressImg(file) {
      let fileSize = file.file.size / 1024 / 1024;
      let read = new FileReader();
      read.readAsDataURL(file.file);
      return new Promise(function (resolve, reject) {
        read.onload = function (e) {
          let img = new Image();
          img.src = e.target.result;
          img.onload = function () {
            //默认按比例压缩
            let w = this.width,
              h = this.height;
            //生成canvas
            let canvas = document.createElement('canvas');
            let ctx = canvas.getContext('2d');
            let base64;
            // 创建属性节点
            canvas.setAttribute("width", w);
            canvas.setAttribute("height", h);
            ctx.drawImage(this, 0, 0, w, h);
            if (fileSize < 1) {
              //如果图片小于一兆 那么不执行压缩操作
              base64 = canvas.toDataURL(file['type'], 1);
            } else if (fileSize > 1 && fileSize < 2) {
              //如果图片大于1M并且小于2M 那么压缩0.5
              base64 = canvas.toDataURL(file['type'], 0.5);
            } else {
              //如果图片超过2m 那么压缩0.2
              base64 = canvas.toDataURL(file['type'], 0.2);
            }
            // 回调函数返回file的值（将base64编码转成file）
            let arr = base64.split(',')
            let mime = arr[0].match(/:(.*?);/)[1]
            let suffix = mime.split('/')[1]
            let bstr = atob(arr[1])
            let n = bstr.length
            let u8arr = new Uint8Array(n)
            while (n--) {
              u8arr[n] = bstr.charCodeAt(n)
            }
            let newFile = new File([u8arr], `${'huanxin'}.${suffix}`, {
              type: mime
            })
            resolve(newFile)
          };
        };
      })
    },

    //获取群组消息
    getConsultationDetail() {
      Api.getConsultationGroupDetail(this.groupId).then((res) => {
        if (res.data.code === 0) {
          this.pageTitle = res.data.data.groupName
        }
      })
    },
    //更新消息为已读
    updateMessageState() {
      const msgParams = {
        id: localStorage.getItem('userId')
      }
      Api.setMsgStatus(msgParams).then((el) => {
      })
    }
  },

  created() {
    //获取服务器IM历史聊天页面
    this.groupId = this.$route.params.groupId
    localStorage.setItem('groupId', this.groupId)
    //患者im在线
    const msgParams = {
      id: localStorage.getItem('userId')
    }
    Api.getContent(msgParams).then((res) => {
      this.imAccount = res.data.data.imAccount;
      const password = "123456";
      var options = {
        apiUrl: WebIM.config.apiURL,
        user: this.imAccount,
        pwd: password,
        appKey: WebIM.config.appkey
      };
    //2026daxiong调试注释
    //WebIM.conn.open(options);
      this.getConsultationDetail()
      this.getMessageList(false);
    })
  },

  destroyed() {
    clearTimeout(this.timeOutEvent);//清除定时器
  }
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

.messagebox-footer {
  border-top: 1px solid #d6dce0;
  width: 100%;
  color: rgba(0, 0, 0, 0.65);
  background: #fff;
  display: block;
  align-items: center;
  height: unset;

  .footer-icon {
    padding: 15px 0px;
    text-align: left;
    height: 40px;
    display: flex;

    .van-icon {
      margin-left: 8px;
      cursor: pointer;
    }

    .icon {
      color: rgba(0, 0, 0, 0.65);
      font-size: 20px;
      margin-left: 8px;
    }
  }

  .recording {
    border: 1px solid #D6D6D6;
    height: 40px;
    width: 100%;
    display: flex;
    justify-content: center;
    font-size: 14px;
    align-items: center;
    -webkit-touch-callout: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
  }

  .arrow-left {
    position: absolute;
    top: 50%;
    z-index: 9999;
    width: 100%;
  }

  .arrow-right {
    width: 40px;
    float: right;
    display: flex;
    justify-content: center
  }

  .fotter-send {
    background-color: #FFF;
    display: flex;
    align-items: center;
    width: 100%;
    min-height: 60px;


    .sengTxt {
      text-align: left;
      height: 100%;
      width: 100%;
      max-height: 100px;
      box-sizing: border-box;
      font-size: 14px;
      box-shadow: 0 0 0 0;
      margin-top: 5px;
      margin-bottom: 5px;
      background-color: #F5F5F9;
      border: 1px solid #F5F5F9;
      padding: 0px 10px;
      outline-style: none;
      border-radius: 5px;
      color: #333333;

      ::-webkit-scrollbar {
        width: 0 !important;
      }

      ::-webkit-scrollbar {
        width: 0 !important;
        height: 0;
      }
    }

    .van-icon {
      position: initial;
      right: 10px;
      top: 45px;
    }
  }
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

/deep/ .van-cell {
  padding: 0px 10px !important;
  min-height: 45px;
  display: flex;
  align-items: center;
}

/deep/ .van-field__body {
  max-height: 100px;

  .van-field__control {
    max-height: 100px;
  }
}
</style>
