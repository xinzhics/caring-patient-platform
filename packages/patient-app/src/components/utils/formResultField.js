/**
 * 清除 用户提交 时，可能存在一个字段多次提交的情况
 * 比如，
 * 用户修改完第一页的字段结果，进入第二页后。再返回到第一页进行修改。
 *
 * 比如用户提交失败。
 * 用户对已经提交到缓存的字段结果进行修改。导致提交时，缓存中可能有重复字段。
 *
 * 增加此方法，根据缓存和当前编辑字段合并出最终的结果。
 * 最终的结果： 所有字段的值都是用户最后编辑点击提交的数据
 * @param submitField 提交到后端的字段的缓存
 * @param currentEditField 当前正在编辑的字段
 */
export function cleanFieldRepeat(submitField, currentEditField) {
  if (!submitField || submitField.length === 0) {
    return currentEditField;
  }
  for (let i = 0; i < currentEditField.length; i++) {
    const id = currentEditField[i].id
    const index = submitField.findIndex(item => item.id === id)
    if (index > -1) {
      submitField[index] = currentEditField[i]
    } else {
      submitField.push(currentEditField[i])
    }
  }
  return submitField;
}

