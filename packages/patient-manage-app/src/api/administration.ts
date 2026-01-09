

import http from '@/utils/http'

const adminApiUrl = {
    page: '/api/nursing/managementHistory/page',
    historyInfoPage: '/api/nursing/managementHistoryDetail/page',
    historyDel: '/api/nursing/managementHistory', //删除
    findIncompleteInformation: '/api/nursing/completenessInformation/anno/findIncompleteInformation', //查询患者信息部完善的字段
}
// 历史记录
const historyPage = (data: any) => {
    return http({
        url: adminApiUrl.page,
        method: 'POST',
        data
    })
}
// 历史详情
const historyInfoPage = (data: any) => {
    return http({
        url: adminApiUrl.historyInfoPage,
        method: 'POST',
        data
    })
}
// 删除
const historyDel = (params: any) => {
    return http({
        url: adminApiUrl.historyDel,
        method: 'DELETE',
        params
    })
}
// findIncompleteInformation
const findIncompleteInformation = (params: any) => {
    return http({
        url: adminApiUrl.findIncompleteInformation,
        method: 'GET',
        params
    })
}
export default {
    historyPage,
    historyInfoPage,
    historyDel,
    findIncompleteInformation
}
