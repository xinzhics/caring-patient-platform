import config from "./WebIMConfig";

function ack(message) {
  var bodyId = message.id; // 需要发送已读回执的消息id
  var msg = new WebIM.message("read", WebIM.conn.getUniqueId());
  msg.set({
    id: bodyId,
    to: message.from
  });
  WebIM.conn.send(msg.body);
}

// 初始化IM SDK
let WebIM = {};
// 检查 websdk 是否已从 CDN 加载
if (typeof websdk !== 'undefined' && websdk && websdk.default) {
  WebIM = window.WebIM = websdk.default;
  WebIM.WebRTC = webrtc;
  WebIM.EMedia = emedia;
} else {
  console.error('环信 IM SDK 未正确加载，请检查网络连接或联系管理员.');
  // 创建一个模拟对象防止完全崩溃，但会显示错误
  WebIM = window.WebIM = {
    message: function() {},
    connection: function() {
      return {
        listen: function() {},
        getUniqueId: function() { return 'mock-id'; }
      };
    }
  };
}
WebIM.config = config;
WebIM.conn = new WebIM.connection({
  appKey: config.appkey,
  isHttpDNS: config.isHttpDNS,
  isMultiLoginSessions: config.isMultiLoginSessions,
  https: config.https,
  url: config.xmppURL,
  apiUrl: config.apiURL,
  isAutoLogin: true,
  heartBeatWait: config.heartBeatWait,
  autoReconnectNumMax: config.autoReconnectNumMax,
  autoReconnectInterval: config.autoReconnectInterval,
  isStropheLog: config.isStropheLog,
  delivery: config.delivery
});
if (!WebIM.conn.apiUrl) {
  WebIM.conn.apiUrl = WebIM.config.apiURL;
}

// 注册监听回调
WebIM.conn.listen({
  onOpened: function (message) { // 连接成功回调
    console.log('Opened',message)
  },
  onClosed: function (message) {
    //Vue.$router.push({path: "/login"});
  }, // 连接关闭回调
  onTextMessage: function (message) {
    Vue.$store.commit("updateMessage", {message: message});
  }, // 收到文本消息
  onEmojiMessage: function (message) {
    console.log("onEmojiMessage", message);
    const {type} = message;
    type === 'chat' && ack(message);
  }, // 收到表情消息
  onPictureMessage: function (message) {
    Vue.$store.commit("updateMessage", {message: message});
  }, // 收到图片消息
  onOnline: function () {
    console.log("onOnline 网络已连接");
  }, // 本机网络连接成功
  onOffline: function () {
    console.log("onOffline 网络已断开");
  }, // 本机网络掉线
  onError: function (message) {
    console.log('onError', message.data)
    // if (message.type == 0) {
    // 	console.log('请输入账号密码')
    // } else if (message.type == 28) {
    // 	console.log("未登陆")
    // } else if (JSON.parse(message.data.data).error_description == "user not found") {
    // 	Message.error("用户名不存在！")
    // } else if (JSON.parse(message.data.data).error_description == "invalid password") {
    // 	console.log('密码无效！')
    // } else if (JSON.parse(message.data.data).error_description == "user not activated") {
    // 	Message.error("用户已被封禁！")
    // } else if (message.type == "504") {
    // 	Message("消息撤回失败");
    // }
    // 报错返回到登录页面
    // Vue.$router.push({ path: '/login' });
  }, // 失败回调
});


export default WebIM;
