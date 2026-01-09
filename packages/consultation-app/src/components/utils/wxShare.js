// import { } from "./wx";
// function init(config) {
//     setAuth(config)
// }
// function setAuth(config) {
//     Service.get("/weixin/api//jssdk/signature", {
//         url: location.href
//     }).then((result) => {
//         if (result.success) {
//             console.log(result.data);
//             setAuthToWX(result.data, config)
//         }
//     })
// };
// function setAuthToWX(wcConfig, config) {
//     wx.config({
//         // debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
//         appId: wcConfig.appId, // 必填，公众号的唯一标识
//         timestamp: wcConfig.timestamp, // 必填，生成签名的时间戳
//         nonceStr: wcConfig.nonceStr, // 必填，生成签名的随机串
//         signature: wcConfig.signature,// 必填，签名
//         jsApiList: [
//             'checkJsApi',
//             'onMenuShareTimeline',//
//             'onMenuShareAppMessage',
//             'onMenuShareQQ',
//             'getNetworkType',
//             'previewImage',
//             "startRecord",
//             "stopRecord",
//             "stopVoice",
//             "onVoiceRecordEnd",
//             "uploadVoice",
//             "playVoice",
//             'updateAppMessageShareData'
//         ] // 必填，需要使用的JS接口列表
//     });
//     wx.ready(function () {   //需在用户可能点击分享按钮前就先调用
//         // wx.updateAppMessageShareData(config.friend) //朋友及qq
//         console.log("==============================config", config)
//         if(config){
//             wx.onMenuShareAppMessage(config.friend);
//             wx.onMenuShareTimeline(config.moment);
//
//             // wx.updateAppMessageShareData(config.friend);
//             // wx.updateTimelineShareData(config.moment);
//         }
//
//
//     });
// }
//
//
// export default init;
