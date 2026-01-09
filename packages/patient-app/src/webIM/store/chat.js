import WebIM from "../WebIM";

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
        groupMessageList: [],
        isGrouploading: false,
        messageList: [],
        isloading: false,
        firstloading: true,
        updateMessage: {},
        tempMsgId: '',
        doctorInfo: {},
    },
    mutations: {//接收到环信消息更新
        updateMessage(state, payload) {
            const {message} = payload;
            if (message.type === 'chat') {
                // 单聊时给首页消息发送当前患者收到的消息
                state.updateMessage = message
            }
            if (state.firstloading) {
                return
            }
            let groupId = localStorage.getItem("groupId")
            // 如果是小组聊天
            if (message.type === "groupchat" && message.ext.groupId === groupId) {
                if (message.ext.senderRoleType !== "patient") {
                    state.isGrouploading = true;
                    if ("cms" === message.ext.type) {
                        state.groupMessageList.push({
                            createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
                            senderAvatar: message.ext.senderAvatar,
                            content: message.ext.content,
                            type: 'cms',
                            id: message.ext.id,
                            senderName: message.ext.senderName,
                            senderRoleType: message.ext.senderRoleType,
                            senderRoleRemark: message.ext.senderRoleRemark,
                            senderImAccount: message.ext.senderImAccount
                        })
                    } else if ('image' === message.ext.type) {
                        state.groupMessageList.push({
                            createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
                            senderAvatar: message.ext.senderAvatar,
                            content: message.ext.content,
                            type: 'image',
                            id: message.ext.id,

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
                            id: message.ext.id,
                            recommendationFunction: message.ext.recommendationFunction ? message.ext.recommendationFunction : '',
                            recommendationFunctionParams: message.ext.recommendationFunctionParams ? message.ext.recommendationFunctionParams : '',
                            senderName: message.ext.senderName,
                            senderRoleType: message.ext.senderRoleType,
                            senderRoleRemark: message.ext.senderRoleRemark,
                            senderImAccount: message.ext.senderImAccount
                        })
                    }
                }
            } else if (message.type !== "groupchat") {
                state.isloading = true;
                if (message.ext.type === 'withdraw') {
                    //有患者im账号
                    let index = state.messageList.findIndex(item => item.id === message.ext.id)
                    let item = state.messageList[index]
                    item.withdrawChatStatus = 1
                    item.withdrawChatUserId = message.ext.senderId
                    state.messageList.splice(index, item)
                    return
                }

                if ("cms" === message.ext.type) {
                    state.messageList.push({
                        createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
                        senderAvatar: message.ext.senderAvatar,
                        content: message.ext.content,
                        type: 'cms',
                        id: message.ext.id,
                        senderName: message.ext.senderName,
                        senderRoleType: message.ext.senderRoleType,
                        senderImAccount: message.ext.senderImAccount
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
                        senderImAccount: message.ext.senderImAccount
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
                        senderImAccount: message.ext.senderImAccount
                    })
                } else {
                    let index = state.messageList.findIndex(item => item.id === message.ext.id)
                    if (state.tempMsgId) {
                        // AI回答的内容
                        const tempIndex = state.messageList.findIndex(msg => msg.id === state.tempMsgId)
                        if (tempIndex !== -1) {
                            // 用服务器返回的数据替换临时消息
                            state.messageList.splice(tempIndex, 1, {
                              createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
                              senderAvatar: message.ext.senderAvatar,
                              content: message.ext.content,
                              type: message.ext.type,
                              id: message.ext.id,
                              isNewMsg: true,
                              aiHostedSend: message.ext.aiHostedSend,
                              recommendationFunction: message.ext.recommendationFunction ? message.ext.recommendationFunction : '',
                              recommendationFunctionParams: message.ext.recommendationFunctionParams ? message.ext.recommendationFunctionParams : '',
                              senderId: message.ext.senderId,
                              senderName: message.ext.senderName,
                              senderRoleType: message.ext.senderRoleType,
                              senderImAccount: message.ext.senderImAccount
                            })
                            state.tempMsgId = ''
                        }
                    } else if (index > -1) {
                        state.messageList[index].recommendationFunction = message.ext.recommendationFunction ? message.ext.recommendationFunction : ''
                        state.messageList[index].recommendationFunctionParams = message.ext.recommendationFunctionParams ? message.ext.recommendationFunctionParams : ''
                    } else {
                        state.messageList.push({
                            createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss'),
                            senderAvatar: message.ext.senderAvatar,
                            content: message.ext.content,
                            type: message.ext.type,
                            id: message.ext.id,
                            isNewMsg: true,
                            aiHostedSend: message.ext.aiHostedSend,
                            recommendationFunction: message.ext.recommendationFunction ? message.ext.recommendationFunction : '',
                            recommendationFunctionParams: message.ext.recommendationFunctionParams ? message.ext.recommendationFunctionParams : '',
                            senderId: message.ext.senderId,
                            senderName: message.ext.senderName,
                            senderRoleType: message.ext.senderRoleType,
                            senderImAccount: message.ext.senderImAccount
                        })
                    }
                }
            }
        },

        //自己发送消息更新
        updateMyMessage(state, payload) {
            const {message} = payload;
            state.messageList.push(message)
            if (message.forcedManualReply != '1') {
                // 需要AI回复
                state.tempMsgId = `tempMsg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
                const tempDoctorMsg = {
                    id: state.tempMsgId,
                    senderId: state.doctorInfo.id,
                    senderName: state.doctorInfo.name || '',
                    senderAvatar: state.doctorInfo.avatar || '',
                    type: 'text',
                    content: '',
                    createTimeString: moment().format('YYYY-MM-DD HH:mm:ss:SSS'),
                    withdrawChatStatus: 0,
                    withdrawChatUserId: '',
                    isTemp: true
                }
                state.messageList.push(tempDoctorMsg)
            }
        },

        //消息列表更新
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
        setFirstLoading(state, payload) {
            state.firstloading = false
        },
        setLoading(state, payload) {
            const {loading} = payload;
            state.isloading = loading;
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
        //删除某个消息
        deleteMessageList(state, payload) {
            const {index} = payload;
            state.messageList.splice(index, 1)
        },
        setGroupLoading(state, payload) {
            const {loading} = payload;
            state.isGrouploading = loading;
        },
        setDoctorAiHosted(state, payload) {
            const {aiHosted} = payload;
            state.isDoctorAiHosted = aiHosted;
        },
        setDoctorInfo(state, payload) {
            const {data} = payload;
            state.doctorInfo = data;
        },
        setTempMsgId(state, payload) {
            state.tempMsgId = '';
        }
    },
    getters: {
        onGetMessageList(state) {
            return state.messageList;
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
        onUpdateMessage(state) {
            return state.updateMessage;
        },
        getTempMsgId(state) {
            return state.tempMsgId;
        },
    }
};
export default Chat;
