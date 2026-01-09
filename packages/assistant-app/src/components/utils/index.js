import {Constants} from '@/api/constants.js'

/**
 * Parse the time to string
 * @param {(Object|string|number)} time
 * @param {string} cFormat
 * @returns {string}
 */
export function parseTime (time, cFormat) {
  if (arguments.length === 0) {
    return null
  }
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
      time = parseInt(time)
    }
    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  return format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value]
    }
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
}

/**
 * @param {number} time
 * @param {string} option
 * @returns {string}
 */
export function formatTime (time, option) {
  if (('' + time).length === 10) {
    time = parseInt(time) * 1000
  } else {
    time = +time
  }
  const d = new Date(time)
  const now = Date.now()

  const diff = (now - d) / 1000

  if (diff < 30) {
    return '刚刚'
  } else if (diff < 3600) {
    // less 1 hour
    return Math.ceil(diff / 60) + '分钟前'
  } else if (diff < 3600 * 24) {
    return Math.ceil(diff / 3600) + '小时前'
  } else if (diff < 3600 * 24 * 2) {
    return '1天前'
  }
  if (option) {
    return parseTime(time, option)
  } else {
    return (
      d.getMonth() +
      1 +
      '月' +
      d.getDate() +
      '日' +
      d.getHours() +
      '时' +
      d.getMinutes() +
      '分'
    )
  }
}

/**
 * @param {string} url
 * @returns {Object}
 */
export function getQueryObject (url) {
  url = url == null ? window.location.href : url
  const search = url.substring(url.lastIndexOf('?') + 1)
  const obj = {}
  const reg = /([^?&=]+)=([^?&=]*)/g
  search.replace(reg, (rs, $1, $2) => {
    const name = decodeURIComponent($1)
    let val = decodeURIComponent($2)
    val = String(val)
    obj[name] = val
    return rs
  })
  return obj
}

/**
 * @param {string} input value
 * @returns {number} output value
 */
export function byteLength (str) {
  // returns the byte length of an utf8 string
  let s = str.length
  for (let i = str.length - 1; i >= 0; i--) {
    const code = str.charCodeAt(i)
    if (code > 0x7f && code <= 0x7ff) s++
    else if (code > 0x7ff && code <= 0xffff) s += 2
    if (code >= 0xDC00 && code <= 0xDFFF) i--
  }
  return s
}

/**
 * @param {Array} actual
 * @returns {Array}
 */
export function cleanArray (actual) {
  const newArray = []
  for (let i = 0; i < actual.length; i++) {
    if (actual[i]) {
      newArray.push(actual[i])
    }
  }
  return newArray
}

/**
 * @param {Object} json
 * @returns {Array}
 */
export function param (json) {
  if (!json) return ''
  return cleanArray(
    Object.keys(json).map(key => {
      if (json[key] === undefined) return ''
      return encodeURIComponent(key) + '=' + encodeURIComponent(json[key])
    })
  ).join('&')
}

/**
 * @param {string} url
 * @returns {Object}
 */
export function param2Obj (url) {
  const search = url.split('?')[1]
  if (!search) {
    return {}
  }
  return JSON.parse(
    '{"' +
    decodeURIComponent(search)
      .replace(/"/g, '\\"')
      .replace(/&/g, '","')
      .replace(/=/g, '":"')
      .replace(/\+/g, ' ') +
    '"}'
  )
}

/**
 * @param {string} val
 * @returns {string}
 */
export function html2Text (val) {
  const div = document.createElement('div')
  div.innerHTML = val
  return div.textContent || div.innerText
}

/**
 * Merges two objects, giving the last one precedence
 * @param {Object} target
 * @param {(Object|Array)} source
 * @returns {Object}
 */
export function objectMerge (target, source) {
  if (typeof target !== 'object') {
    target = {}
  }
  if (Array.isArray(source)) {
    return source.slice()
  }
  Object.keys(source).forEach(property => {
    const sourceProperty = source[property]
    if (typeof sourceProperty === 'object') {
      target[property] = objectMerge(target[property], sourceProperty)
    } else {
      target[property] = sourceProperty
    }
  })
  return target
}

/**
 * @param {HTMLElement} element
 * @param {string} className
 */
export function toggleClass (element, className) {
  if (!element || !className) {
    return
  }
  let classString = element.className
  const nameIndex = classString.findIndex(className)
  if (nameIndex === -1) {
    classString += '' + className
  } else {
    classString =
      classString.substr(0, nameIndex) +
      classString.substr(nameIndex + className.length)
  }
  element.className = classString
}

/**
 * @param {string} type
 * @returns {Date}
 */
export function getTime (type) {
  if (type === 'start') {
    return new Date().getTime() - 3600 * 1000 * 24 * 90
  } else {
    return new Date(new Date().toDateString())
  }
}

/**
 * @param {Function} func
 * @param {number} wait
 * @param {boolean} immediate
 * @return {*}
 */
export function debounce (func, wait, immediate) {
  let timeout, args, context, timestamp, result

  const later = function () {
    // 据上一次触发时间间隔
    const last = +new Date() - timestamp

    // 上次被包装函数被调用时间间隔 last 小于设定时间间隔 wait
    if (last < wait && last > 0) {
      timeout = setTimeout(later, wait - last)
    } else {
      timeout = null
      // 如果设定为immediate===true，因为开始边界已经调用过了此处无需调用
      if (!immediate) {
        result = func.apply(context, args)
        if (!timeout) context = args = null
      }
    }
  }

  return function (...args) {
    context = this
    timestamp = +new Date()
    const callNow = immediate && !timeout
    // 如果延时不存在，重新设定延时
    if (!timeout) timeout = setTimeout(later, wait)
    if (callNow) {
      result = func.apply(context, args)
      context = args = null
    }

    return result
  }
}

/**
 * This is just a simple version of deep copy
 * Has a lot of edge cases bug
 * If you want to use a perfect deep copy, use lodash's _.cloneDeep
 * @param {Object} source
 * @returns {Object}
 */
export function deepClone (source) {
  if (!source && typeof source !== 'object') {
    throw new Error('error arguments', 'deepClone')
  }
  const targetObj = source.constructor === Array ? [] : {}
  Object.keys(source).forEach(keys => {
    if (source[keys] && typeof source[keys] === 'object') {
      targetObj[keys] = deepClone(source[keys])
    } else {
      targetObj[keys] = source[keys]
    }
  })
  return targetObj
}

/**
 * @param {Array} arr
 * @returns {Array}
 */
export function uniqueArr (arr) {
  return Array.from(new Set(arr))
}

/**
 * @returns {string}
 */
export function createUniqueString () {
  const timestamp = +new Date() + ''
  const randomNum = parseInt((1 + Math.random()) * 65536) + ''
  return (+(randomNum + timestamp)).toString(32)
}

/**
 * Check if an element has a class
 * @param {HTMLElement} elm
 * @param {string} cls
 * @returns {boolean}
 */
export function hasClass (ele, cls) {
  return !!ele.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'))
}

/**
 * Add class to element
 * @param {HTMLElement} elm
 * @param {string} cls
 */
export function addClass (ele, cls) {
  if (!hasClass(ele, cls)) ele.className += ' ' + cls
}

/**
 * Remove class from element
 * @param {HTMLElement} elm
 * @param {string} cls
 */
export function removeClass (ele, cls) {
  if (hasClass(ele, cls)) {
    const reg = new RegExp('(\\s|^)' + cls + '(\\s|$)')
    ele.className = ele.className.replace(reg, ' ')
  }
}

/**
 * Judge param is empty Object
 * @param {json} param
 */
export function isEmptyObj (param) {
  if (param) {
    return JSON.stringify(param) === '{}'
  } else {
    return true
  }
}

/**
 * @description 判断当前系统是否IOS
 */
export function isIos () {
  let u = navigator.userAgent
  return !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)
}

/**
 * @description 判断当前系统是否安卓
 */
export function isAndroid () {
  let u = navigator.userAgent
  return !!u.findIndex('Android') > -1 || u.findIndex('Linux') > -1
}

/**
 * @description判断是否非空数组
 * @param {any} arr
 */
export function isNotEmptyArr (arr) {
  if (Array.isArray(arr)) {
    if (arr.length > 0) {
      return true
    }
  }
  return false
}

export function getValue (field) {
  if (field.widgetType === Constants.CustomFormWidgetType.SingleLineText &&
    field.exactType === 'hospital') {
    return {
      type: 'text',
      value: field.values && field.values[0] && field.values[0].valueText ? field.values[0].valueText : '-'
    }
  } else if (field.widgetType === Constants.CustomFormWidgetType.SingleLineText ||
    field.widgetType === Constants.CustomFormWidgetType.Number ||
    field.widgetType === Constants.CustomFormWidgetType.FullName ||
    field.widgetType === Constants.CustomFormWidgetType.MultiLineText ||
    field.widgetType === Constants.CustomFormWidgetType.Time ||
    field.widgetType === Constants.CustomFormWidgetType.Date) {
    return {
      type: 'text',
      value: field.values && field.values[0] && field.values[0].attrValue ? field.values[0].attrValue : '-',
      unit: field.rightUnit
    }
  } else if (field.widgetType === Constants.CustomFormWidgetType.Radio ||
    field.widgetType === Constants.CustomFormWidgetType.CheckBox ||
    field.widgetType === Constants.CustomFormWidgetType.DropdownSelect) {
    return {
      type: 'text',
      value: getFieldsResult(field.values, field.otherValue) || '-',
      unit: field.rightUnit
    }
  } else if (field.widgetType === Constants.CustomFormWidgetType.MultiLevelDropdownSelect) {
    return {
      type: 'text',
      value: getDropdownSelectResult(field.values) || '-',
      unit: field.rightUnit
    }
  } else if (field.widgetType === Constants.CustomFormWidgetType.Address) {
    return {
      type: 'text',
      value: getAddressResult(field.values, field) || '-',
      unit: field.rightUnit
    }
  } else if (field.widgetType === Constants.CustomFormWidgetType.SingleImageUpload ||
    field.widgetType === Constants.CustomFormWidgetType.MultiImageUpload) {
    return {
      type: 'image',
      value: field.values
    }
  }
  return '-'
}
/**
 * 检查字段的值 所在的数据反馈 normalAnomaly
 * @param formField
 * @returns {number}
 */
export function getValueStatus (formField) {
  let result = 1
  if (formField.showDataFeedback) {
    if (formField.dataFeedBacks && formField.dataFeedBacks.length > 0) {
      if (formField.values && formField.values.length > 0) {
        if (formField.values[0].attrValue) {
          let num = parseFloat(formField.values[0].attrValue)
          for (let i = 0; i < formField.dataFeedBacks.length; i++) {
            if (num <= parseFloat(formField.dataFeedBacks[i].maxValue) && num >= parseFloat(formField.dataFeedBacks[i].minValue)) {
              result = Number(formField.dataFeedBacks[i].normalAnomaly)
              break
            }
          }
        }
      }
    }
  }
  return result
}
// 多级下拉
export function getDropdownSelectResult (values) {
  if (values && values.length > 0) {
    return values[values.length - 1].attrValue
  } else {
    return '-'
  }
}

// 地址的选择
export function getAddressResult (values, field) {
  if (values && values.length > 0) {
    const address = values[0].attrValue
    if (address && address.length > 0) {
      const addressString = address.join('-')
      if (field.hasAddressDetail) {
        return addressString
      }
      return addressString
    } else {
      return '-'
    }
  } else {
    return '-'
  }
}

// 普通的选择
export function getFieldsResult (values, otherValue) {
  let backVal = ''
  if (typeof values !== 'object') {
    values = JSON.parse(values)
  }

  if (values && values.length > 0) {
    //   console.log(values)
    const result = []

    for (let i = 0; i < values.length; i++) {
      let value

      if (values[i].desc && values[i].desc.length > 0) {
        value = values[i].valueText + '(' + values[i].desc + ')'
      } else {
        value = values[i].valueText
      }

      if (values[i].valueText === '其他') {
        if (otherValue && otherValue.length > 0) {
          value = values[i].valueText + '(' + otherValue + ')'
        } else {
          value = values[i].valueText
        }
      }
      result.push(value)
    }
    // console.log(result)
    if (result && result.length > 0) {
      backVal = result.join('、')
    } else {
      backVal = '-'
    }
  } else {
    backVal = '-'
  }
  return backVal
}
