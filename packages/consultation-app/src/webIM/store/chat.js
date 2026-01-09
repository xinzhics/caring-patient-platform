import WebIM from "../WebIM";
import moment from "moment";

// import WebIM from "../utils/WebIM";

// TODO 处理页面刷新无法获取到音频url
const res = function (response) {
  let objectUrl = WebIM.utils.parseDownloadResponse.call(WebIM.conn, response);
  return objectUrl;  //  'blob:http://localhost:8080/536070e2-b3a0-444a-b1cc-f0723cf95588'
};

function test(url, func) {
  let options = {
    url: url,
    headers: {Accept: "audio/mp3"},
    onFileDownloadComplete: func,
    onFileDownloadError: function () {
      console.log("音频下载失败");
    }
  };
  WebIM.utils.download.call(WebIM.conn, options);
}

const Chat = {
  state: {
    messageList: [],
    doctorMessageList: [],
    receiverImAccount: "",
    isloading: false,
    groupMessageList: [],
    isGrouploading: false,
    isQRCode: false,
  },
  mutations: {
    //进入提示
    updateIsQRCode(state, payload) {
      const {isQRCode} = payload;
      state.isQRCode = isQRCode
    },

    //接收到环信消息更新
    updateMessage(state, payload) {
      const {message} = payload;
      console.log(message)
      if (state.receiverImAccount === message.ext.senderImAccount) {
        state.isloading = true;
        if ('text' === message.ext.type) {
          if ("cms" === message.ext.type) {
            state.messageList.push({
              createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
              senderAvatar: message.ext.senderAvatar,
              content: message.ext.content,
              type: 'cms',
              senderName: message.ext.senderName,
              senderRoleType: message.ext.senderRoleType,
            })
          } else {
            state.messageList.push({
              createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
              senderAvatar: message.ext.senderAvatar,
              content: message.ext.content,
              type: 'text',
              senderName: message.ext.senderName,
              senderRoleType: message.ext.senderRoleType,
            })
          }
        } else if ('image' === message.ext.type) {
          state.messageList.push({
            createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
            senderAvatar: message.ext.senderAvatar,
            content: message.ext.content,
            type: 'image',
            senderName: message.ext.senderName,
            senderRoleType: message.ext.senderRoleType,
          })
        }
        state.doctorMessageList.forEach(item => {
          if (item.receiverImAccount === message.ext.senderImAccount) {
            item.chat.content = message.ext.content
          }
        })
      } else {
        if ('groupchat' === message.type && message.ext.senderImAccount !== localStorage.getItem("memberImAccount")) {
          let groupId = localStorage.getItem("groupId")
          if(message.ext.groupId === groupId) {
            state.isGrouploading = true;
            if ('text' === message.ext.type) {
              if ("cms" === message.ext.type) {
                state.groupMessageList.push({
                  createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
                  senderAvatar: message.ext.senderAvatar,
                  content: message.ext.content,
                  type: 'cms',
                  senderName: message.ext.senderName,
                  senderRoleType: message.ext.senderRoleType,
                  senderRoleRemark: message.ext.senderRoleRemark,
                  senderImAccount: message.ext.senderImAccount
                })
              } else {
                state.groupMessageList.push({
                  createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
                  senderAvatar: message.ext.senderAvatar,
                  content: message.ext.content,
                  type: 'text',
                  senderName: message.ext.senderName,
                  senderRoleType: message.ext.senderRoleType,
                  senderRoleRemark: message.ext.senderRoleRemark,
                  senderImAccount: message.ext.senderImAccount
                })
              }
            } else if ('image' === message.ext.type) {
              state.groupMessageList.push({
                createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
                senderAvatar: message.ext.senderAvatar,
                content: message.ext.content,
                type: 'image',
                senderName: message.ext.senderName,
                senderRoleType: message.ext.senderRoleType,
                senderRoleRemark: message.ext.senderRoleRemark,
                senderImAccount: message.ext.senderImAccount
              })
            }
          }

        } else {
          //判断发送该消息的患者是否在聊天列表数据里面存在
          var result = state.doctorMessageList.some(item => {
            if (item.receiverImAccount === message.ext.senderImAccount) {
              return true
            }
          })

          //存在就更新患者内容，不存在添加患者
          if (result) {
            let resultItem = {};
            state.doctorMessageList.forEach(item => {
              if (item.receiverImAccount === message.ext.senderImAccount) {
                item.chat.content = message.ext.content,
                  item.chat.diagnosisName = message.ext.diagnosisName,
                  item.noReadTotal = item.noReadTotal + 1,
                  item.chat.type = message.contentsType === "TEXT" ? 'cms' === message.ext.type ? "cms" : 'text' : 'image',
                  resultItem = item
              }
            })

            let index = state.doctorMessageList.indexOf(resultItem);
            state.doctorMessageList.splice(index, 1);
            state.doctorMessageList.unshift(resultItem);
          } else {
            state.doctorMessageList.unshift({
              patientId: message.ext.patientId,
              patientAvatar: message.ext.patientAvatar,
              patientName: message.ext.patientName,
              receiverImAccount: message.ext.senderImAccount,
              noReadTotal: 1,
              type: message.contentsType === "TEXT" ? 'cms' === message.ext.type ? "cms" : 'text' : 'image',
              content: message.contentsType === "TEXT" ? message.data : message.url,
              chat: {
                type: message.contentsType === "TEXT" ? 'cms' === message.ext.type ? "cms" : 'text' : 'image',
                content: message.ext.content,
                diagnosisName: message.ext.diagnosisName,
              }
            })
          }
        }
      }
    },

    //自己发送消息更新
    updateMyMessage(state, payload) {
      const {message} = payload;
      state.messageList.push(message)
    },

    //单个患者消息列表更新
    onMessageList(state, payload) {
      const {messageList, upload} = payload;
      if (upload) {
        messageList.forEach(item => {
          state.messageList.unshift(item)
        })
      } else {
        state.messageList = messageList;
        state.messageList.reverse()
      }
    },

    //设置当前患者Id
    updatePatient(state, payload) {
      const {id} = payload;
      state.receiverImAccount = id;
    },

    //获取医生聊天列表
    onDoctorMessageList(state, payload) {
      const {messageList} = payload;
      state.doctorMessageList = []
      messageList.forEach(item => {
        state.doctorMessageList.unshift(item)
      })
    },

    //修改消息列表中消息为已读
    updateDoctorMessageList(state, payload) {
      const {receiverImAccount} = payload;
      state.doctorMessageList.forEach(item => {
        if (item.receiverImAccount === receiverImAccount) {
          item.noReadTotal = 0
        }
      })
    },

    setLoading(state, payload) {
      const {loading} = payload;
      state.isloading = loading;
    },

    clearMessageList(state, payload) {
      state.messageList = []
    },

    updateMyGroupMessage(state, payload) {
      const {message} = payload;
      state.groupMessageList.push(message)
    },

    onGroupMessageList(state, payload) {
      const {messageList, upload} = payload;
      if (upload) {
        messageList.forEach(item => {
          state.groupMessageList.unshift(item)
        })
      } else {
        state.groupMessageList = messageList;
      }
    },

    setGroupLoading(state, payload) {
      const {loading} = payload;
      state.isGrouploading = loading;
    },
  },
  getters: {
    getIsQRCode(state) {
      return state.isQRCode;
    },
    onGetMessageList(state) {
      return state.messageList;
    },
    onGetDoctorMessageList(state) {
      return state.doctorMessageList;
    },
    onGetCurrentPatient(state) {
      return state.receiverImAccount;
    },
    isUpdateMessage(state) {
      return state.isloading;
    },
    onGetGroupMessageList(state) {
      return state.groupMessageList;
    },
    isUpdateGroupMessage(state) {
      return state.isGrouploading;
    }
  }

};
export default Chat;
