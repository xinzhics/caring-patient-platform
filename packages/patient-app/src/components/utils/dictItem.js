import Api from '@/api/Content.js'

let dictItem = new Map();
let dict = new Map()
dict.set('doctor', '医生')
dict.set('patient', '患者')
dict.set('assistant', '医助')
dict.set('register', '注册')
dict.set('notregister', '未注册')
dict.set('activation', '激活')
dict.set('notactivation', '未激活')
dict.set('registerrate', '注册转化率')
dict.set('unfollowrate', '会员取关率')
dict.set('diagnostictype', '诊断类型')

const dictionaryItem = {

  getDictItem(code) {
    if (dictItem.size === 0) {
      // 尝试从本地缓存中查询
      const dictionaryItem =  localStorage.getItem("dictionaryItem")
      if (dictionaryItem) {
        const distItemList = JSON.parse(dictionaryItem)
        if (distItemList.length > 0) {
          for (let i = 0; i < distItemList.length; i++) {
            dict.set(distItemList[i].code, distItemList[i].name)
          }
          return dict.get(code)
        } else {
          return dict.get(code)
        }
      } else {
        return dict.get(code)
      }
    } else {
      return dictItem.get(code)
    }
  },
  initDict() {
    if (dictItem.size === 0) {
      Api.getdictionaryItem().then(res => {
        if (res.data.code === 0) {
          dictItem = new Map()
          const dict = res.data.data
          if (dict && dict.length > 0) {
            localStorage.setItem("dictionaryItem", JSON.stringify(dict))
            for (let i = 0; i < dict.length; i++) {
              dictItem.set(dict[i].code, dict[i].name)
            }
          }
        }
      })
    }
  },
  showHist () {
    if (localStorage.getItem('userId') && localStorage.getItem('token')) {
      Api.formHistoryRecord().then(res => {
        localStorage.setItem("formHistoryRecord", res.data.data)
      })
    }
  },
  getShowHist () {
    if (localStorage.getItem("formHistoryRecord")) {
      let showhis = localStorage.getItem("formHistoryRecord")
      return showhis
    }
  }
}

export default dictionaryItem
