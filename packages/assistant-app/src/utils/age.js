export function jsGetAge (strBirthday) {
  let returnAge = ''
  let strBirthdayArr = ''
  if (strBirthday) {
    if (strBirthday.indexOf('-') !== -1) {
      strBirthdayArr = strBirthday.split('-')
    } else if (strBirthday.indexOf('/') !== -1) {
      strBirthdayArr = strBirthday.split('/')
    }
    let birthYear = strBirthdayArr[0]
    let birthMonth = strBirthdayArr[1]
    let birthDay = strBirthdayArr[2]

    let d = new Date()
    let nowYear = d.getFullYear()
    let nowMonth = d.getMonth() + 1
    let nowDay = d.getDate()

    if (nowYear === birthYear) {
      returnAge = 0 // 同年 则为0岁
    } else {
      let ageDiff = nowYear - birthYear // 年之差
      if (ageDiff > 0) {
        if (nowMonth === birthMonth) {
          let dayDiff = nowDay - birthDay // 日之差
          if (dayDiff < 0) {
            returnAge = ageDiff - 1
          } else {
            returnAge = ageDiff
          }
        } else {
          let monthDiff = nowMonth - birthMonth // 月之差
          if (monthDiff < 0) {
            returnAge = ageDiff - 1
          } else {
            returnAge = ageDiff
          }
        }
      } else {
        returnAge = -1 // 返回-1 表示出生日期输入错误 晚于今天
      }
    }
  } else {
    returnAge = ''
  }
  // 如果结果为 -1 或者为 0， 则不显示年龄
  if (returnAge === -1 || returnAge === 0) {
    returnAge = ''
  }
  return returnAge // 返回周岁年龄
}
