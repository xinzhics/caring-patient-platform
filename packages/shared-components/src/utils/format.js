/**
 * 数据格式化工具函数
 */
import moment from 'moment'

/**
 * 格式化日期
 * @param {Date|string|number} date 日期
 * @param {string} format 格式字符串，默认 'YYYY-MM-DD'
 * @returns {string} 格式化后的日期
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  return moment(date).format(format)
}

/**
 * 格式化手机号
 * @param {string} phone 手机号
 * @returns {string} 格式化后的手机号
 */
export function formatPhone(phone) {
  if (!phone) return ''
  const phoneStr = phone.toString()
  if (phoneStr.length === 11) {
    return phoneStr.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  }
  return phoneStr
}

/**
 * 格式化身份证号
 * @param {string} idCard 身份证号
 * @returns {string} 格式化后的身份证号
 */
export function formatIdCard(idCard) {
  if (!idCard) return ''
  const idCardStr = idCard.toString()
  if (idCardStr.length === 18) {
    return idCardStr.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
  } else if (idCardStr.length === 15) {
    return idCardStr.replace(/(\d{6})\d{6}(\d{3})/, '$1******$2')
  }
  return idCardStr
}

/**
 * 格式化金额
 * @param {number} amount 金额
 * @param {number} decimals 小数位数，默认 2
 * @param {string} decimalPoint 小数点符号，默认 '.'
 * @param {string} thousandsSeparator 千分位符号，默认 ','
 * @returns {string} 格式化后的金额
 */
export function formatAmount(amount, decimals = 2, decimalPoint = '.', thousandsSeparator = ',') {
  if (!amount && amount !== 0) return ''
  
  const num = parseFloat(amount)
  if (isNaN(num)) return ''
  
  const fixedNum = num.toFixed(decimals)
  const parts = fixedNum.split('.')
  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, thousandsSeparator)
  
  return parts.join(decimalPoint)
}

/**
 * 格式化百分比
 * @param {number} value 数值
 * @param {number} decimals 小数位数，默认 2
 * @returns {string} 格式化后的百分比
 */
export function formatPercent(value, decimals = 2) {
  if (!value && value !== 0) return ''
  const num = parseFloat(value)
  if (isNaN(num)) return ''
  return `${(num * 100).toFixed(decimals)}%`
}

/**
 * 格式化文件大小
 * @param {number} bytes 字节数
 * @param {number} decimals 小数位数，默认 2
 * @returns {string} 格式化后的文件大小
 */
export function formatFileSize(bytes, decimals = 2) {
  if (!bytes && bytes !== 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(decimals))} ${sizes[i]}`
}

/**
 * 格式化性别
 * @param {number} gender 性别 0-男 1-女
 * @returns {string} 格式化后的性别
 */
export function formatGender(gender) {
  const genderMap = {
    0: '男',
    1: '女',
    male: '男',
    female: '女',
    Male: '男',
    Female: '女'
  }
  return genderMap[gender] || ''
}

/**
 * 格式化年龄
 * @param {string|Date} birthDate 出生日期
 * @returns {string} 格式化后的年龄
 */
export function formatAge(birthDate) {
  if (!birthDate) return ''
  
  const birth = moment(birthDate)
  const now = moment()
  const years = now.diff(birth, 'years')
  
  if (years < 1) {
    const months = now.diff(birth, 'months')
    return `${months}个月`
  }
  
  return `${years}岁`
}

/**
 * 格式化时间段
 * @param {number} minutes 分钟数
 * @returns {string} 格式化后的时间段
 */
export function formatDuration(minutes) {
  if (!minutes && minutes !== 0) return ''
  
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  
  if (hours > 0) {
    return `${hours}小时${mins}分钟`
  }
  
  return `${mins}分钟`
}