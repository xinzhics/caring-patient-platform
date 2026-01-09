import WebIM from "../WebIM";
import Api from '../../api/Content'

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
    firstloading: true,
    doctorImCount: 0,
  },
  mutations: {
    //接收到环信消息更新
    async updateMessage(state, payload) {
      const {message} = payload;
      if (!(state.receiverImAccount === message.ext.receiverImAccount) && !('groupchat' === message.type)) {
        if (message.ext.type === 'withdraw'){
          if (state.doctorImCount > 0) {
            state.doctorImCount--;
          } else {
            state.doctorImCount = 0;
          }
        }else {
          let clong = moment().unix()
          let olong = moment(localStorage.getItem('imLoginTime')).unix()
          console.log(clong - olong)
          if (clong - olong > 5) {
            state.doctorImCount++;
          }
        }
      }
      if (state.firstloading) {
        return
      }

      if (message.ext.type === 'remind') {
        // 推荐功能
        let index = state.messageList.findIndex(item => item.id === message.ext.id)
        if (index > -1) {
          let recommendationFunctionParams = JSON.parse(state.messageList[index].recommendationFunctionParams)
          state.messageList[index].recommendationFunctionParams = JSON.stringify({
            ...recommendationFunctionParams,
            fillOut: 1
          })
        }
        return
      }

      if (state.receiverImAccount && message.ext.type === 'withdraw') {
        //有患者im账号
        let index = state.messageList.findIndex(item => item.id === message.ext.id)
        let item = state.messageList[index]
        item.withdrawChatStatus = 1
        item.withdrawChatUserId = message.ext.senderId
        state.messageList.splice(index, item)
        return
      } else if (message.ext.type === 'withdraw') {
        //没有im账号，说明在医生列表
        let index = state.doctorMessageList.findIndex(item => item.receiverImAccount === message.ext.receiverImAccount)
        if (index == -1) {
          return
        }
        let res = await Api.getChatUserNewMsg(state.doctorMessageList[index].id)
        Vue.$set(state.doctorMessageList, index, res.data.data)
        return
      }
      console.log('==================' + (state.receiverImAccount === message.ext.receiverImAccount), message.ext)
      if (state.receiverImAccount === message.ext.receiverImAccount) {
        // 在聊天列表里面显示
        state.isloading = true;
        if ("cms" === message.ext.type) {
          state.messageList.push({
            createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
            senderAvatar: message.ext.senderAvatar,
            content: message.ext.content,
            type: 'cms',
            id: message.ext.id,
            senderName: message.ext.senderName,
            senderRoleType: message.ext.senderRoleType,
          })
        } else if ("voice" === message.ext.type) {
          state.messageList.push({
            createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
            senderAvatar: message.ext.senderAvatar,
            content: message.ext.content,
            type: 'voice',
            id: message.ext.id,
            senderName: message.ext.senderName,
            senderRoleType: message.ext.senderRoleType,
          })
        } else if ('image' === message.ext.type) {
          state.messageList.push({
            createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss.SSS'),
            senderAvatar: message.ext.senderAvatar,
            content: message.ext.content,
            type: 'image',
            id: message.ext.id,
            senderName: message.ext.senderName,
            senderRoleType: message.ext.senderRoleType,
          })
        } else {
          state.messageList.push({
            createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
            senderAvatar: message.ext.senderAvatar,
            content: message.ext.content,
            type: message.ext.type,
            id: message.ext.id,
            isNewMsg: true,
            senderId: message.ext.senderId,
            aiHostedSend: message.ext.aiHostedSend,
            senderName: message.ext.senderName,
            senderImAccount: message.ext.senderImAccount,
            senderRoleType: message.ext.senderRoleType,
            recommendationFunction: message.ext.recommendationFunction,
            recommendationFunctionParams: message.ext.recommendationFunctionParams,
          })
        }
        state.doctorMessageList.forEach(item => {
          if (item.receiverImAccount === message.ext.senderImAccount) {
            item.chat.content = message.ext.content
          }
        })
      } else if ('groupchat' === message.type) {
        let groupId = localStorage.getItem("groupId")
        if (message.ext.groupId === groupId) {
          state.isGrouploading = true;
          if ('text' === message.ext.type) {
            if ("cms" === message.ext.type) {
              state.groupMessageList.push({
                createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
                senderAvatar: message.ext.senderAvatar,
                content: message.ext.content,
                type: 'cms',
                id: message.ext.id,
                senderName: message.ext.senderName,
                senderRoleType: message.ext.senderRoleType,
                senderImAccount: message.ext.senderImAccount,
                senderRoleRemark: message.ext.senderRoleRemark
              })
            } else {
              state.groupMessageList.push({
                createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
                senderAvatar: message.ext.senderAvatar,
                content: message.ext.content,
                type: 'text',
                id: message.ext.id,
                senderName: message.ext.senderName,
                senderRoleType: message.ext.senderRoleType,
                senderImAccount: message.ext.senderImAccount,
                senderRoleRemark: message.ext.senderRoleRemark
              })
            }
          } else if ('image' === message.ext.type) {
            state.groupMessageList.push({
              createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss.SSS'),
              senderAvatar: message.ext.senderAvatar,
              content: message.ext.content,
              type: 'image',
              id: message.ext.id,
              senderName: message.ext.senderName,
              senderRoleType: message.ext.senderRoleType,
              senderImAccount: message.ext.senderImAccount,
              senderRoleRemark: message.ext.senderRoleRemark
            })
          }
        }
      } else {
        //判断发送该消息的患者是否在聊天列表数据里面存在
        var result = state.doctorMessageList.some(item => {
          if (item.patientId === message.ext.patientId) {
            return true
          }
        })
        //存在就更新患者内容，不存在添加患者
        if (result) {
          let resultItem = {};
          let flag = true
          if (message.ext.sendReads && message.ext.sendReads.length > 0) {
            message.ext.sendReads.forEach(item => {
              if (item.roleType === "doctor" && item.imAccount === localStorage.getItem('userImAccount') && item.noCreateReadLog) {
                flag = false
              }
            })
          }

          state.doctorMessageList.forEach(item => {
            if (item.patientId === message.ext.patientId) {
              item.chat.content = message.ext.content,
                item.chat.diagnosisName = message.ext.diagnosisName,
                item.noReadTotal = flag ? item.noReadTotal + 1 : item.noReadTotal,
                item.chatId = message.ext.id,
                item.chatAtRecords = message.ext.chatAtRecords,
                item.chat.type = 'cms' === message.ext.type ? "cms" : 'voice' === message.ext.type ? 'voice' : 'image' === message.ext.type ? 'image' : "text",
                resultItem = item
            }
          })
          let index = state.doctorMessageList.indexOf(resultItem);
          state.doctorMessageList.splice(index, 1);
          state.doctorMessageList.unshift(resultItem);
        } else {
          let res = await Api.findChatUserNewMsg(message.ext.receiverImAccount)
          state.doctorMessageList.unshift({
            patientId: message.ext.patientId,
            patientAvatar: message.ext.patientAvatar,
            patientName: message.ext.patientName,
            receiverImAccount: message.ext.receiverImAccount,
            chatAtRecords: message.ext.chatAtRecords,
            noReadTotal: 1,
            id: res.data.data.id,
            chatId: message.ext.id,
            type: 'cms' === message.ext.type ? "cms" : 'voice' === message.ext.type ? 'voice' : 'image' === message.ext.type ? 'image' : "text",
            content: message.ext.content,
            chat: {
              type: 'cms' === message.ext.type ? "cms" : 'voice' === message.ext.type ? 'voice' : 'image' === message.ext.type ? 'image' : "text",
              content: message.ext.content,
              diagnosisName: message.ext.diagnosisName
            }
          })
        }
      }
    },

    //自己发送消息更新
    updateMyMessage(state, payload) {
      const {message} = payload;
      state.messageList.push(message)
    },
    setFirstLoading(state, payload) {
      state.firstloading = false
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
    //删除某个消息
    deleteMessageList(state, payload) {
      const {index} = payload;
      state.messageList.splice(index, 1)
    },
    //设置当前患者imId
    updatePatient(state, payload) {
      const {id} = payload;
      state.receiverImAccount = id;
    },
    //设置医生消息为读书
    updateDoctorImCount(state, payload) {
      const {count} = payload;
      state.doctorImCount = count;
    },
    //获取医生聊天列表
    onDoctorMessageList(state, payload) {
      const {messageList} = payload;
      state.doctorMessageList = []
      messageList.forEach(item => {
        state.doctorMessageList.push(item)
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
    },
    onGetDoctorImCount(state) {
      return state.doctorImCount;
    },
  }

};
export default Chat;
