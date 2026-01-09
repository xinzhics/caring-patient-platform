// 判断系统版本是否是ios 14或以上
function isIos14AndMore () {
  // 判断ios手机版本号是否大于14，大于14就拿app.vue存储的url,否则拿当前页面的url
  try {
    const str = navigator.userAgent.toLowerCase()
    const ver = str.match(/cpu iphone os (.*?) like mac os/)
    if (!ver) {
      return false
    } else {
      return Number(ver[1].split('_')[0]) >= 14
    }
  } catch (e) {
    return false
  }
}

const sassWxJs = {

  /**
   * 获取微信js授权的路由地址
   */
  getWxConfigSignatureUrl() {
    let url = location.href.split("#")[0];
    return isIos14AndMore() ? localStorage.getItem("scanCodeUrl") : url
  }

}

export default sassWxJs;
