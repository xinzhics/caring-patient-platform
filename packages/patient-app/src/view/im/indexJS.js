import Api from '@/api/Content.js'
import {Flexbox, FlexboxItem, Group} from 'vux'
import {mapGetters} from "vuex";
import {ImagePreview, Toast, Dialog, Field, Cell} from "vant";
import wx from 'weixin-js-sdk';
import loading from '../../components/loading/ChrysanthemumLoading'
import cPopover from '../../components/customPopover/index';
import MonitorKeyboard from '../../pubilc/MonitorKeyboard'
import Vue from "vue";

Vue.use(Field)
Vue.use(Cell)

export default {
    name: "index",
    components: {
        Flexbox,
        loading,
        FlexboxItem,
        Group,
        cPopover,
        [ImagePreview.Component.name]: ImagePreview.Component,
        navBar: () => import('@/components/headers/navBar'),
    },
    data() {
        return {
            pageTitle: '',
            message: "",
            headerImg: require('@/assets/my/im_up.png'),
            sendImg: require('@/assets/my/send.png'),
            isLoading: false,
            isVoice: false,
            voiceImg: require('@/assets/my/im_voice.png'),
            isRecording: false,
            startTime: '',
            endTime: '',
            timeLength: '',
            isPlay: false,
            playType: '',
            position: 0,
            appUserCall: "",//医助
            wxUserCall: '',//医生
            imAccount: '',
            isSend: false,
            routerData: [],
            independence: 0,
            imGroupStatus: 1,    //1是展开聊天组头像     0为不展开
            doctorInfo: {},
            NursingStaffInfo: {},
            patientInfo: {},
            patientImGroup: [],
            imagePreviewShow: false,
            index: 0,
            images: [],
            currentTime: '',
            chatAtRecords: [],
            AITShow: false,
            AITType: 0,
            popupList: [],
            name: "",
            footerHeight: 0,
            baseFooterHeight: 0,
            isApple: false,
            recordingFooterHeight: 0,
            actions: [],
            indexPopover: 0,
            isPopoverClick: false,
            isImageLeftShow: false,
            isImageRightShow: false,
            monitorKeyboard: null,
            isKeyboard: false,
            officialAccountType: '',
            aiHosted: false,
            isDoctorAiHosted: false,
        };
    },
    computed: {
        ...mapGetters({
            messageList: "onGetMessageList",
            updateMessage: "isUpdateMessage",
            tempMsgId: "getTempMsgId",
        }),
        msgContent() {
            return this.$refs.msgContent;
        }
    },
    watch: {
        messageList: function (val, old) {
            if (val[val.length - 1].isNewMsg && (val[val.length - 1].type === 'openChat' || val[val.length - 1].type === 'exitChat')) {
                if (val[val.length - 1].senderRoleType === 'NursingStaff' && val[val.length - 1].senderId === this.NursingStaffInfo.id) {
                    if (val[val.length - 1].type === 'openChat') {
                        this.patientImGroup.push(this.NursingStaffInfo)
                    } else {
                        this.patientImGroup = this.patientImGroup.filter(item => item.id !== val[val.length - 1].senderId)
                    }
                    this.imGroupStatus = this.patientImGroup.length > 1 ? 1 : 0;
                }
            }
        }
    },
    updated() {
        if (this.updateMessage) {
            this.scollBottom();
            this.updateMessageState();
            this.$store.commit("setLoading", {loading: false});
        }
    },
    methods: {
        // 点击发送过来的消息
        onClickText(item) {
            if (item.recommendationFunction && item.recommendationFunctionParams && JSON.parse(item.recommendationFunctionParams).name) {
                localStorage.setItem('pageTitle', JSON.parse(item.recommendationFunctionParams).name)
            }

            setTimeout(() => {
                if (item.recommendationFunction === "TEST_NUMBER") {
                    // 检验数据
                    this.$router.push({path: '/testNumber/editor', query: {imMessageId: item.id}})
                } else if (item.recommendationFunction === "HEALTH_CALENDAR") {
                    // 健康日志
                    this.$router.push({path: '/healthCalendar/editor', query: {imMessageId: item.id}})
                } else if (item.recommendationFunction === "OTHER") {
                    // 外部链接
                    let path = JSON.parse(item.recommendationFunctionParams).path;
                    window.location.href = path;
                } else if (item.recommendationFunction === "BASE_INFO") {
                    // 基本信息
                    this.$router.push({path: '/baseinfo/index', query: {imMessageId: item.id}})
                } else if (item.recommendationFunction === "HEALTH") {
                    // 疾病信息
                    this.$router.push({path: '/health/editor', query: {imMessageId: item.id}})
                } else if (item.recommendationFunction === "MEDICINE") {
                    // 我的药箱
                    this.$router.push({path: '/calendar/index', query: {imMessageId: item.id, type: '1'}})
                } else if (item.recommendationFunction === "CALENDAR") {
                    // 我的药箱
                    this.$router.push({path: '/calendar/index', query: {imMessageId: item.id}})
                } else if (item.recommendationFunction === "CUSTOM_FOLLOW_UP") {
                    // 自定义随访
                    if (JSON.parse(item.recommendationFunctionParams).isLink) {
                        window.location.href = JSON.parse(item.recommendationFunctionParams).path
                    } else {
                        this.$router.push({
                            path: '/' + JSON.parse(item.recommendationFunctionParams).path + '/editor',
                            query: {imMessageId: item.id}
                        })
                    }
                } else if (item.recommendationFunction === "MONITOR") {
                    let dictItemId = JSON.parse(item.recommendationFunctionParams).dictItemId;
                    if (dictItemId === '1') {
                        // 血压
                        this.$router.push({path: '/monitor/pressureEditor', query: {imMessageId: item.id}})
                    } else if (dictItemId === '2') {
                        // 血压
                        this.$router.push({path: '/monitor/glucoseEditor', query: {imMessageId: item.id}})
                    } else {
                        // 自定义监测计划
                        this.$router.push({
                            path: '/monitor/add',
                            query: {
                                imMessageId: item.id,
                                id: JSON.parse(item.recommendationFunctionParams).id,
                                title: JSON.parse(item.recommendationFunctionParams).name
                            }
                        })
                    }
                }
            }, 400)
        },
        // 开启或退出内容
        getChatContent(item) {
            if (this.doctorId === item.senderId) {
                return item.senderName + '(我)已' + (item.type === 'exitChat' ? '退出' : '加入') + '聊天'
            } else {
                if (item.senderRoleType === 'doctor') {
                    return item.senderName + '(' + this.$getDictItem('doctor') + ')已' + (item.type === 'exitChat' ? '退出' : '加入') + '聊天'
                } else {
                    return item.senderName + '(' + this.$getDictItem('assistant') + ')已' + (item.type === 'exitChat' ? '退出' : '加入') + '聊天'
                }
            }
        },
        getKeyboardState() {
            this.monitorKeyboard = new MonitorKeyboard();
            this.monitorKeyboard.onStart();

            // 监听虚拟键盘弹出事件
            this.monitorKeyboard.onShow(() => {
                this.isKeyboard = true
                this.scollBottom()
            })

            //监听键盘收起的事件
            this.monitorKeyboard.onHidden(() => {
                this.isKeyboard = false
            })
        },
        inputBlur() {
        },
        //设置撤回内容
        getWithdrawContent(withdrawChatUserId) {
            let content;
            this.popupList.forEach(item => {
                if (withdrawChatUserId === item.id) {
                    if ("NursingStaff" === item.type) {
                        content = item.name + '(' + this.$getDictItem('assistant') + ')撤回了一条消息'
                    } else if ("doctor" === item.type) {
                        content = item.name + '(' + this.$getDictItem('doctor') + ')撤回了一条消息'
                    } else {
                        content = item.name + '(' + this.$getDictItem('patient') + ')撤回了一条消息'
                    }
                }
            })
            return content;
        },
        //点击其他地方关闭弹窗
        closePopover() {
            if (this.messageList[this.indexPopover].showPopover) {
                let item = this.messageList[this.indexPopover]
                item.showPopover = false
                this.messageList.splice(this.indexPopover, item)
            }
        },
        changeAiHosted() {
            if (this.aiHosted) {
                this.aiHosted = false
                localStorage.removeItem('ai-studio-patient-aiHosted')
            } else {
                this.aiHosted = true
                localStorage.setItem('ai-studio-patient-aiHosted', "1")
                Api.sendManualTemplate()
            }
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
        onSelect(item, index) {
            let value = this.messageList[index]
            value.showPopover = false
            this.messageList.splice(index, value)
            if (item.text === '复制') {
                Dialog.confirm({
                    title: '复制',
                    message: '确定要复制此内容到剪切板吗'
                })
                    .then(() => {
                        var input = document.createElement('input') // 创建input对象
                        input.value = this.messageList[index].content // 设置复制内容
                        document.body.appendChild(input) // 添加临时实例
                        input.select() // 选择实例内容
                        document.execCommand('Copy') // 执行复制
                        document.body.removeChild(input) // 删除临时实例
                    })
                    .catch(() => {
                        // on cancel
                    })
            } else if (item.text === '删除') {
                Dialog.confirm({
                    title: '删除',
                    message: '是否删除该消息'
                })
                    .then(() => {
                        Api.deleteStatusChat(this.messageList[index].id)
                            .then(res => {
                                this.$vux.toast.text('删除成功', 'center')
                                this.$store.commit('deleteMessageList', {
                                    index: index
                                })
                            })
                    })
                    .catch(() => {
                        // on cancel
                    })
            } else if (item.text === '撤回') {
                Dialog.confirm({
                    title: '撤回',
                    message: '确定撤回此消息吗'
                })
                    .then(() => {
                        Api.withdrawChat(this.patientInfo.id, this.messageList[index].id)
                            .then(res => {
                            })
                    })
                    .catch(() => {
                        // on cancel
                    })
            }
        },
        inputChange() {
            this.footerHeight = this.$refs.footer.offsetHeight
            if (this.message == "") {
                this.chatAtRecords = []
                this.sendImg = require('@/assets/my/send.png')
            } else {
                this.sendImg = require('@/assets/my/blue_send.png')
                for (let i = this.chatAtRecords.length - 1; i >= 0; i--) {
                    if (this.message.indexOf('@' + this.chatAtRecords[i].name) === -1) {
                        this.chatAtRecords.splice(i, 1)
                    }
                }
                if (this.AITType === 0 && '@' === this.message.substring(this.message.length - 1, this.message.length)) {
                    this.AITShow = true
                    this.AITType = 1
                } else {
                    this.AITType = 0
                }
            }
        },
        popoverGotouchstart(item, index, type) {
            let that = this
            this.messageList[this.indexPopover].showPopover = false
            clearTimeout(this.timeOutEvent) //清除定时器
            this.timeOutEvent = 0
            this.isPopoverClick = true
            this.timeOutEvent = setTimeout(function () {
                that.indexPopover = index
                that.isPopoverClick = false
                //执行长按要执行的内容，...
                if (type === 'my') {
                    //超过两分钟 || 其他角色发送的文本消息
                    if (that.differenceTime(moment(item.createTime).format('YYYY-MM-DD HH:mm:ss'), moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss')) <= 120) {
                        if (item.type === 'text') {
                            that.actions = [{text: '复制'}, {text: '撤回'}, {text: '删除'},]
                        } else {
                            that.actions = [{text: '撤回'}, {text: '删除'},]
                        }
                    } else if (item.type === 'text') {
                        that.actions = [{text: '复制'}, {text: '删除'},]
                    } else {
                        that.actions = [{text: '删除'},]
                    }
                } else {
                    if (item.type === 'text') {
                        that.actions = [{text: '复制'}, {text: '删除'},]
                    } else {
                        that.actions = [{text: '删除'},]
                    }
                }
                item.showPopover = true
                that.messageList.splice(index, item)
                //that.messageList.splice(index, item)
            }, 500) //这里设置定时
        },
        //手释放，如果在500毫秒内就释放，则取消长按事件，清除timeout
        popoverGotouchend(item, index, type) {
            clearTimeout(this.timeOutEvent)
            if (!this.messageList[index].showPopover && this.isPopoverClick) {
                this.isPopoverClick = false
                if ('voice' === item.type) {
                    this.playing(type, JSON.parse(item.content).url, index)
                } else if ('cms' === item.type) {
                    let content = JSON.parse(item.content)
                    if (content.link) {
                        location.href = content.link
                    } else {
                        this.$router.push({path: '/cms/show', query: {id: content.id}})
                    }
                } else if ('image' === item.type) {
                    this.imagePreviewShow = true
                    this.images = [item.content]
                    this.currentTime = item.createTime
                    this.isImageLeftShow = false
                    this.isImageRightShow = false
                    let leftParams = {
                        direction: 'Previous',
                        patientImAccount: this.imAccount,
                        createTimeString: this.currentTime
                    }

                    Api.getChatImage(leftParams).then(res => {
                        if (res.data.code === 0 && res.data.data) {
                            this.isImageLeftShow = true
                        }
                    })

                    let params = {
                        direction: 'Next',
                        patientImAccount: this.imAccount,
                        createTimeString: this.currentTime
                    }
                    Api.getChatImage(params).then(res => {
                        if (res.data.code === 0 && res.data.data) {
                            this.isImageRightShow = true
                        }
                    })
                } else {
                    if (item.recommendationFunction) {
                        this.onClickText(item)
                    } else {
                        this.clickLink(item.content)
                    }
                }
            }
        },
        clickLink(content) {
            var reg = /(https?|http|ftp|file):\/\/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]/g
            content = content.match(reg)
            if (content && content.length > 0) {
                location.href = content[0]
            }
        },
        //如果手指有移动，则取消所有事件，此时说明用户只是要移动而不是长按，清除timeout
        popoverGotouchmove() {
            clearTimeout(this.timeOutEvent) //清除定时器
            this.timeOutEvent = 0
            this.isPopoverClick = false
        },
        popupItem(item) {
            this.AITShow = false
            this.$data.message = this.$data.message + item.name + ' '
            this.chatAtRecords.push({atUserId: item.id, userType: item.type, name: item.name})

            clearTimeout(this.timeOutEvent) //清除定时器
            let that = this
            this.timeOutEvent = setTimeout(function () {
                that.$refs.txtDom.selectionStart = that.message.length
                that.$refs.txtDom.selectionEnd = that.message.length
            }, 500) //这里设置定时
        },
        loadImageLeft() {
            //this.images = ['https://img.yzcdn.cn/vant/apple-1.jpg']
            let params = {
                direction: 'Previous',
                patientImAccount: this.imAccount,
                createTimeString: this.currentTime
            }

            Api.getChatImage(params).then(res => {
                if (res.data.code === 0 && res.data.data) {
                    this.images = [res.data.data.content]
                    this.currentTime = res.data.data.createTime
                    this.isImageRightShow = true
                    let params1 = {
                        direction: 'Previous',
                        patientImAccount: this.imAccount,
                        createTimeString: this.currentTime
                    }
                    Api.getChatImage(params1)
                        .then(res => {
                            if (res.data.code === 0 && res.data.data) {
                                this.isImageLeftShow = true
                            } else {
                                this.isImageLeftShow = false
                            }
                        })
                } else {
                    this.$vux.toast.text('已无图片', 'center')
                }
            })
        },
        loadImageRight() {
            let params = {
                direction: 'Next',
                patientImAccount: this.imAccount,
                createTimeString: this.currentTime
            }

            Api.getChatImage(params).then(res => {
                if (res.data.code === 0 && res.data.data) {
                    this.images = [res.data.data.content]
                    this.currentTime = res.data.data.createTime
                    this.isImageLeftShow = true
                    let params1 = {
                        direction: 'Next',
                        patientImAccount: this.imAccount,
                        createTimeString: this.currentTime
                    }
                    Api.getChatImage(params1)
                        .then(res => {
                            if (res.data.code === 0 && res.data.data) {
                                this.isImageRightShow = true
                            } else {
                                this.isImageRightShow = false
                            }
                        })
                } else {
                    this.$vux.toast.text('已无图片', 'center')
                }
            })
        },
        gotouchstartImage(item) {
            let that = this
            clearTimeout(that.timeOutEvent);//清除定时器
            that.timeOutEvent = 0;
            this.timeOutEvent = setTimeout(function () {
                that.message = that.message + "@" + item.senderName + ' '
                that.chatAtRecords.push({atUserId: item.senderId, userType: item.senderRoleType, name: item.senderName})
                that.$refs.txtDom.selectionStart = 0;
                that.$refs.txtDom.selectionEnd = that.message.length;
            }, 1000);//这里设置定时
        },
        //手释放，如果在500毫秒内就释放，则取消长按事件，清除timeout
        gotouchchendImage() {
            clearTimeout(this.timeOutEvent);
        },

        //是否隐藏患者聊天群组
        isImGroup() {
            const params = {
                id: localStorage.getItem('userId'),
                imGroupStatus: this.imGroupStatus === 1 ? 0 : 1
            }
            Api.isImGroup(params).then((res) => {
                this.headerImg = this.imGroupStatus === 1 ? require('@/assets/my/im_up.png') : require('@/assets/my/im_down.png'),
                    this.imGroupStatus = this.imGroupStatus === 1 ? 0 : 1
            })
        },
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
        getWidth(time) {
            return (40 + (2 * time) > 170 ? 170 : 40 + (2 * time))
        },
        playing(type, url, i) {
            if (this.playType === 'left') {
                let img = document.getElementById('img' + this.position)
                if (img) {
                    img.src = require('@/assets/my/im_voice_playing.png')
                }
            } else {
                let img = document.getElementById('img' + this.position)
                if (img) {
                    img.src = require('@/assets/my/im_right_voice_playing.png')
                }
            }
            this.playType = type
            let audio = document.getElementById('audio' + this.position)
            if (audio) {
                audio.pause()
            }
            let currentAudio = document.getElementById('audio' + i)
            if (currentAudio && this.position !== i) {
                currentAudio.src = url
                currentAudio.play()
                this.position = i
            } else {
                this.position = -1
                currentAudio.pause()
            }
        },
        onPlay() {
            if (this.playType === 'left') {
                let img = document.getElementById('img' + this.position)
                if (img) {
                    img.src = require('@/assets/my/im_left_play.gif')
                }
            } else {
                let img = document.getElementById('img' + this.position)
                if (img) {
                    img.src = require('@/assets/my/im_right_play.gif')
                }
            }
        },
        //播放完毕执行
        overAudio() {
            if (this.playType === 'left') {
                let img = document.getElementById('img' + this.position)
                if (img) {
                    img.src = require('@/assets/my/im_voice_playing.png')
                }
            } else {
                let img = document.getElementById('img' + this.position)
                if (img) {
                    img.src = require('@/assets/my/im_right_voice_playing.png')
                }
            }
        },
        gotouchstart() {
            //长摁结束播放
            let audio = document.getElementById('audio' + this.position)
            if (audio) {
                console.log(audio)
                audio.pause()
                this.overAudio()
            }
            if (this.isRecording) {
                this.isRecording = false;
                //停止录音
                wx.stopRecord();
            } else {
                let that = this;
                clearTimeout(this.timeOutEvent);//清除定时器
                this.timeOutEvent = 0;
                this.startTime = this.getCurrentTime()
                this.endTime = ''
                this.timeLength = ''
                this.isPlay = false

                this.timeOutEvent = setTimeout(function () {
                    wx.startRecord({
                        success: function (e) {
                            that.isRecording = true
                        },
                        cancel: function (e) {
                            wx.stopRecord();
                            that.isRecording = false
                            that.$toast.fail('开启录音失败', 'cneter');
                        }
                    })
                }, 300);//这里设置定时
            }
        },
        stopRecord() {
            let that = this
            clearTimeout(this.timeOutEvent)
            if (this.isRecording) {
                this.timeOutEvent = 0
                wx.stopRecord({
                    success: function (res) {
                        that.isRecording = false
                        that.uploadRecording(res.localId)
                    },
                    fail: function (res) {
                    }
                })
            }
        },
        //手释放，如果在500毫秒内就释放，则取消长按事件，清除timeout
        gotouchend() {
            let that = this
            clearTimeout(this.timeOutEvent);
            if (this.isRecording) {
                this.timeOutEvent = 0;
                wx.stopRecord({
                    success: function (res) {
                        that.isRecording = false;
                        that.uploadRecording(res.localId)
                    },
                    fail: function (res) {
                        that.isRecording = false;
                    }
                });

                wx.onVoiceRecordEnd({
                    // 录音时间超过一分钟没有停止的时候会执行 complete 回调
                    complete: function (res) {
                        that.isRecording = false;
                        that.uploadRecording(res.localId)
                    }
                });
            }
        },
        //计算时间差
        differenceTime(startTime, endTime) {
            const u = navigator.userAgent;
            const isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
            var dateBegin
            var dateEnd
            //判断是否为苹果
            if (isiOS) {
                dateBegin = new Date(startTime.replace(/-/g, "/"))
                dateEnd = new Date(endTime.replace(/-/g, "/"))
            } else {
                dateBegin = new Date(startTime);
                dateEnd = new Date(endTime);
            }
            var dateDiff = dateEnd.getTime() - dateBegin.getTime(); //时间差的毫秒数
            var seconds = Math.round(dateDiff / 1000);
            return seconds
        },
        //获取当前时间
        getCurrentTime() {
            //获取当前时间
            let yy = new Date().getFullYear();
            let mm = new Date().getMonth() + 1;
            let dd = new Date().getDate();
            let hh = new Date().getHours();
            let mf = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes();
            let ss = new Date().getSeconds() < 10 ? '0' + new Date().getSeconds() : new Date().getSeconds();
            return yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mf + ':' + ss;
        },
        VoiceChange() {
            if (!this.isVoice) {
                this.recordingFooterHeight = this.$refs.footer.offsetHeight
            }
            //this.isVoice = !this.isVoice
            this.isRecording = false
            if (!this.isVoice) {
                let that = this
                that.footerHeight = this.baseFooterHeight
                this.timeOutEvent = setTimeout(function () {
                    wx.startRecord({
                        success: function (e) {
                            wx.stopRecord();
                            that.$nextTick(() => {
                                that.$set(that, 'isVoice', true)
                                that.voiceImg = require('@/assets/my/im_input.png')
                                that.sendImg = require('@/assets/my/send.png')
                                this.$forceUpdate()
                            })
                        },
                        cancel: function (e) {
                            wx.stopRecord();
                        }
                    })
                }, 100);//这里设置定时
            } else {
                this.isVoice = false
                if (this.message !== "") {
                    this.sendImg = require('@/assets/my/blue_send.png')
                }
                this.voiceImg = require('@/assets/my/im_voice.png')
                this.footerHeight = this.recordingFooterHeight
            }

        },
        inputSend() {
            if (this.message == "") {
                this.chatAtRecords = []
                this.sendImg = require('@/assets/my/send.png')
            } else {
                this.sendImg = require('@/assets/my/blue_send.png')
                for (let i = this.chatAtRecords.length - 1; i >= 0; i--) {
                    if (this.message.indexOf('@' + this.chatAtRecords[i].name) === -1) {
                        this.chatAtRecords.splice(i, 1);
                    }
                }
                if (this.AITType === 0 && '@' === this.message.substring(this.message.length - 1, this.message.length)) {
                    this.AITShow = true
                    this.AITType = 1
                } else {
                    this.AITType = 0
                }
            }
        },
        uploadRecording(id) {
            let that = this
            that.endTime = that.getCurrentTime()
            let time = that.differenceTime(this.startTime, this.endTime)
            if (time > 0) {
                wx.uploadVoice({
                    localId: id, // 需要上传的音频的本地ID，由stopRecord接口获得
                    isShowProgressTips: 1, // 默认为1，显示进度提示
                    success: function (res) {
                        that.uploadServerId(res.serverId, time)// 返回音频的服务器端ID
                    }
                });
            }
        },
        uploadServerId(serverId, time) {
            Api.uploadServerId({mediaId: serverId, wxAppId: localStorage.getItem("wxAppId")})
                .then(response => {
                    if (response.data && response.data.data) {
                        var data = {
                            url: response.data.data,
                            timeLength: time + ""
                        }
                        var message = JSON.stringify(data)
                        this.onSendTextMsg('voice', message)
                    }
                })
        },
        loadingAvatar(avatar, type) {
            if (type === 'patient') {
                return avatar ? avatar : 'https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/head-portrait-patient.png'
            } else if (type === 'doctor') {
                return avatar ? avatar : 'https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/doctor_default_avator.png'
            } else {
                return avatar ? avatar : require('@/assets/my/nursingstaff_avatar.png')
            }
        },
        //获取聊天消息
        getMessageList(upload) {
            const params = {
                receiverImAccount: this.imAccount,
                createTime: !upload ? Date.parse(new Date()) : this.messageList.length > 0 ? (moment(this.messageList[0].createTime).unix() * 1000) : Date.parse(new Date()),
                size: !upload ? 5 : 20
            }
            let that = this
            let dom = this.$refs.msgContent
            let msgHeight = dom.scrollHeight
            Api.getMessageList(params).then(response => {
                if (response.data.data.length > 0) {
                    this.$store.commit("onMessageList", {
                        messageList: response.data.data,
                        upload: upload,
                    });
                }
                this.isLoading = false;

                if (upload) {
                    this.$nextTick(() => {
                        let dom = this.$refs.msgContent
                        dom.scrollTop = dom.scrollHeight - msgHeight
                    });
                } else {
                    this.scollBottom()
                    this.timeOutEvent = setTimeout(function () {
                        that.$store.commit("setFirstLoading", {
                            messageList: response.data.data,
                            upload: upload,
                        });

                        if (this.isApple) {
                            this.$forceUpdate()
                        }

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
            this.getMessageList(true)
        },
        //查看大图
        showBigPicture(item) {
            this.imagePreviewShow = true
            console.log(item)
            this.images = [item.content]
            this.currentTime = item.createTime
            //this.currentTime = '2022-11-11 11:46:56.417'
        },
        //cms链接
        seeCMS(item) {
            if (item.link) {
                location.href = item.link
            } else {
                this.$router.push({path: '/cmsz/show', query: {id: item.id}})
            }
        },
        //发送文本信息
        onSendTextMsg(type, message) {
            if (this.isSend || (this.isVoice && type === 'text')) {
                return;
            }
            if ((type === 'text' && this.$data.message == "") || (type === 'text' && this.$data.message == "\n")) {
                this.$data.message = "";
                return;
            }
            this.isSend = true
            const params = {
                receiverId: this.patientInfo.id,
                senderId: localStorage.getItem('userId'),
                type: type,
                content: type === 'text' ? this.$data.message : message,
                chatAtRecords: type === 'text' ? this.chatAtRecords : [],
                forcedManualReply: this.aiHosted ? '1' : '2'
            }
            let that = this
            Api.sendMessage(params).then((res) => {
                this.chatAtRecords = []
                this.$store.commit("updateMyMessage", {
                    message: {
                        createTime: moment().locale('zh-cn').format('YYYY-MM-DD HH:mm:ss.SSS'),
                        content: type === 'text' ? this.$data.message : message,
                        type: type,
                        id: res.data.data.id,
                        forcedManualReply: this.aiHosted ? '1' : '2',
                        senderAvatar: this.patientInfo.avatar,
                        senderRoleType: "patient",
                        senderImAccount: this.imAccount,
                    }
                });
                setTimeout(() => {
                    this.getMessageList(false);
                    this.$store.commit("setTempMsgId", {});
                }, 13000)
                if (type === 'text') {
                    this.$data.message = "";
                    this.footerHeight = this.baseFooterHeight;
                    this.sendImg = require('@/assets/my/send.png')
                }
                that.scollBottom()
                that.isSend = false
            }).catch(function (error) { // 请求失败处理
                that.isSend = false
            });
        },
        // TODO 可以抽离到utils
        renderTime(time) {
            return moment(time).format("MM-DD HH:mm") === 'Invalid date' ? "" : moment(time).format("MM-DD HH:mm");
        },

        scollBottom() {
            setTimeout(() => {
                const dom = this.$refs.msgContent;
                if (!dom) return;
                dom.scrollTop = dom.scrollHeight - dom.clientHeight;
            }, 200);
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
        //微信配置
        getwxSignature() {
            const that = this
            const params = {
                url: this.$getWxConfigSignatureUrl(),
                wxAppId: localStorage.getItem('wxAppId')
            }
            Api.wxSignature(params).then(res => {
                if (res.data.code === 0) {
                    wx.config({
                        // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                        debug: false,
                        // 必填，公众号的唯一标识
                        appId: localStorage.getItem('wxAppId'),
                        // 必填，生成签名的时间戳
                        timestamp: res.data.data.timestamp,
                        // 必填，生成签名的随机串
                        nonceStr: res.data.data.nonceStr,
                        // 必填，签名
                        signature: res.data.data.signature,
                        // 必填，需要使用的JS接口列表，所有JS接口列表
                        jsApiList: [
                            'startRecord',
                            'stopRecord',
                            'playVoice',
                            'onVoiceRecordEnd',
                            'uploadVoice',
                            'onVoicePlayEnd',
                            'downloadVoice',
                            'translateVoice'
                        ]
                    });
                    wx.ready(function (res) {
                        //that.$toast.fail('配置成功：'+ JSON.stringify(res),'cneter');
                    });
                    wx.error(function (res) {
                        //that.$toast.fail('配置失败：'+ JSON.stringify(res),'cneter');
                        //console.log('配置功能3'+ JSON.stringify(res))
                    });
                }
            })
        },
        JumpPatient() {
            this.$router.push({path: '/im/messageDetail'})
        },
        //更新消息为已读
        updateMessageState() {
            const msgParams = {
                id: localStorage.getItem('userId')
            }
            Api.setMsgStatus(msgParams).then((el) => {
            })
        },
        getPatientDoctorInfo(params) {
            Api.getPatientDoctorInfo(params)
                .then((res) => {
                    if (res.data.code === 0) {
                        this.independence = res.data.data.independence //1 为独立医生， 0 为非独立医生
                        Api.getPatientImGroup(params).then((response) => {
                            if (response.data.code === 0) {
                                this.aiHosted = true
                                response.data.data.forEach(item => {
                                    //type    "patient" 患者      "NursingStaff" 服务专员         "doctor" 医生
                                    //如果是患者则记录患者im账号
                                    if ("NursingStaff" === item.type) {
                                        if (this.independence === 1) {
                                            this.popupList.push(item)
                                        }
                                        this.NursingStaffInfo = item;
                                    } else if ("doctor" === item.type) {
                                        if (this.independence === 1) {
                                            this.popupList.push(item)
                                        }
                                        if (item.aiHosted) {
                                            this.aiHosted = false
                                            this.isDoctorAiHosted = true

                                            let aiHost = localStorage.getItem('ai-studio-patient-aiHosted')
                                            if (aiHost) {
                                                this.aiHosted = true
                                            }
                                        } else {
                                            this.aiHosted = true
                                        }
                                        this.$store.commit("setDoctorInfo", {data: item});
                                        this.doctorInfo = item;
                                    } else if ("patient" === item.type) {
                                        //通过患者IM账号获取数据
                                        this.patientInfo = item;
                                    }
                                })
                                if (this.independence === 1) {
                                    if (this.patientInfo.patient.doctorExitChat == 1 && this.patientInfo.patient.nursingExitChat == 1) {
                                        // 医生医助都退出
                                        this.patientImGroup = []
                                    } else if (this.patientInfo.patient.doctorExitChat === 1 ||
                                        this.patientInfo.patient.nursingExitChat === 1) {
                                        response.data.data.forEach(item => {
                                            if ("patient" === item.type) {
                                                this.patientImGroup.push(item)
                                            } else if ("doctor" === item.type && this.patientInfo.patient.doctorExitChat !== 1) {
                                                this.patientImGroup.push(item)
                                            } else if ("NursingStaff" === item.type && this.patientInfo.patient.nursingExitChat !== 1) {
                                                this.patientImGroup.push(item)
                                            }
                                        })
                                    } else {
                                        this.patientImGroup = response.data.data
                                    }
                                }


                            }
                        })
                        Api.getContent(params).then((pRes) => {
                            this.imGroupStatus = this.patientImGroup.length > 1 ? pRes.data.data.imGroupStatus : 0;
                            if (this.independence === 0) {
                                this.headerImg = require('@/assets/my/patient_chat_group.png')
                            } else {
                                this.headerImg = pRes.data.data.imGroupStatus === 1 ? require('@/assets/my/im_down.png') : require('@/assets/my/im_up.png');
                            }
                            this.imAccount = pRes.data.data.imAccount;
                            const userName = pRes.data.data.imAccount;
                            const password = "123456";
                            var options = {
                                apiUrl: WebIM.config.apiURL,
                                user: userName,
                                pwd: password,
                                appKey: WebIM.config.appkey
                            };
                            WebIM.conn.open(options);
                            this.getMessageList(false);
                        })
                        if (this.independence === 0) {
                            this.getGroupDetail()
                        }
                    }
                })
        },
        //群组成员获取
        getGroupDetail() {
            Api.getPatientGroupDetail()
                .then((res) => {
                    if (res.data.code === 0) {
                        res.data.data.groupMembers.forEach(item => {
                            if (!item.me) {
                                this.popupList.push({
                                    id: item.id,
                                    type: item.type,
                                    name: item.name,
                                    avatar: item.avatar
                                })
                            }
                        })
                    }
                })
        },
        getIos() {
            var u = navigator.userAgent;
            var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
            if (isiOS) {
                return true
            }
            return false
        },
        JudgmentEquipment() {
            var u = navigator.userAgent;
            var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
            if (window.history.length === 1 && isiOS) {
                this.isApple = true
            }
        },
        listScroll() {
            var startY = 0;
            let that = this
            var scrollBox = this.$refs.msgContent
            /*
            * 在这个修改后的代码中，我们在 listScroll 方法中创建了 handleTouchStart 和 handleTouchMove 函数，
            * 并通过 .bind(this) 将它们绑定到当前组件实例上，这样我们就可以在 removeTouchListeners 方法中引用这些函数并正确地移除它们
            * */
            this.handleTouchStart = function (e) {
                startY = e.touches[0].pageY;
            }.bind(this);
            this.handleTouchMove = function (e) {
                var moveY = e.touches[0].pageY;
                var top = scrollBox.scrollTop;
                var ch = scrollBox.clientHeight;
                var sh = scrollBox.scrollHeight;
                if (!that.isChildTarget(e.target, scrollBox)) {
                    e.preventDefault();
                } else if ((top === 0 && moveY > startY) || (top + ch === sh && moveY < startY)) {
                    e.preventDefault();
                }
            }.bind(this);

            document.body.addEventListener('touchstart', this.handleTouchStart, {passive: false});
            document.body.addEventListener('touchmove', this.handleTouchMove, {passive: false});
        },
        isChildTarget(child, parent, justChild) {
            // justChild为true则只判断是否为子元素，若为false则判断是否为本身或者子元素 默认为false
            var parentNode;
            if (justChild) {
                parentNode = child.parentNode;
            } else {
                parentNode = child;
            }

            if (child && parent) {
                while (parentNode) {
                    if (parent === parentNode) {
                        return true;
                    }
                    parentNode = parentNode.parentNode;
                }
            }
            return false;
        }
    },
    mounted() {
        this.wxUserCall = this.$getDictItem('doctor')
        this.appUserCall = this.$getDictItem('assistant')
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
        if (iOS) {
            this.listScroll()
        }
    },
    created() {
        this.monitorKeyboard = new MonitorKeyboard();
        this.pageTitle = '在线咨询'
        // this.routerData = JSON.parse(localStorage.getItem('routerData'))
        this.officialAccountType = localStorage.getItem("officialAccountType")
        this.JudgmentEquipment()

        this.getwxSignature()

        const params = {
            id: localStorage.getItem('userId')
        }
        this.getPatientDoctorInfo(params)

        //查询聊天
        Api.getATPatientList()
            .then(res => {
                if (res.data.code === 0 && res.data.data && res.data.data.length > 0) {
                    Dialog.alert({
                        title: '提示',
                        message: '有人聊天中@你啦， 请查阅聊天记录',
                    }).then(() => {
                        // on close
                        this.updateMessageState();
                    });
                }
            })
    },
    beforeDestroy() {
        // 如果有监听，请移除事件监听器， 不然会影响其他页面
        document.body.removeEventListener('touchstart', this.handleTouchStart);
        document.body.removeEventListener('touchmove', this.handleTouchMove);
    },
    destroyed() {
        this.monitorKeyboard.onEnd();
        wx.stopRecord()
        clearTimeout(this.timeOutEvent);//清除定时器
        Api.setPatientMessageStatus()
    }
};
