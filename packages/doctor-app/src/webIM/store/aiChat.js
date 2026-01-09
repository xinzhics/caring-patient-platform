const aiChat = {
  state: {
    messageList: [],
    aiMsgStatus: false,
    msgPos: 0,
  },

  mutations: {
    updateMsgPos(state, payload) {
      const {pos} = payload;
      state.msgPos = pos
    },
    // 更新数据文本
    updateCurrentAiMessage(state, payload) {
      const {message} = payload;
      state.messageList[state.msgPos].content = message.ext.content
    },
    // 添加数据
    updateAiMessage(state, payload) {
      const {message} = payload;
      if (state.messageList.length > 0 && state.messageList[state.messageList.length - 1].senderRoleType === 'system') {
        state.messageList.splice(state.messageList.length - 1, state.messageList.length - 1)
      }

      state.messageList.push({
        createTime: message.ext.createTime,
        content: message.ext.content,
        type: message.ext.type,
        senderRoleType: message.ext.senderRoleType,
      })
      state.msgPos = state.messageList.length - 1
      state.aiMsgStatus = true

    },
    setAiMessageList(state, payload) {
      const {messageList, isEmpty, pos} = payload;
      if (!isEmpty) {
        if (state.messageList.length > 0) {
          messageList.forEach(item => {
            state.messageList.unshift(item)
          })
        } else {
          state.messageList = messageList;
          state.messageList.reverse()
        }
        if (pos - 1 > 0) {
          state.msgPos = pos - 1
        }
      }else {
        state.messageList = []
        state.msgPos = 0
      }
    },
    aiMsgStatus(state, payload) {
      const {aiMsgStatus} = payload;
      state.aiMsgStatus = aiMsgStatus
      if (!aiMsgStatus) {
        state.messageList.push({
          senderRoleType: 'system',
          createTime: moment().format("yyyy-MM-DD HH:mm:ss.SSS")
        })
      }
    }
  },

  getters: {
    onAiMsgStatus(state) {
      return state.aiMsgStatus;
    },
    onAiMsgList(state) {
      return state.messageList;
    },
    onMsgPos(state) {
      return state.msgPos;
    },
  }
}

export default aiChat;
