

import http from '@/utils/http'

const infoApiUrl = {
    page: '/api/nursing/completenessInformation/search', //分页列表查询
    sendNotification: '/api/nursing/completenessInformation/sendOne', //发送通知消息群发或者单条发送
    information: '/api/nursing/completenessInformation/', //信息完整度类别详情接口
    query: '/api/nursing/informationMonitoringInterval/query', //批量查询

}
// 分页列表查询
const infoPage = (data: any) => {
    return http({
        url: infoApiUrl.page,
        method: 'POST',
        data
    })
}
// 发送通知消息群发或者单条发送
const sendNotification = (data: any) => {
    return http({
        url: infoApiUrl.sendNotification,
        method: 'POST',
        data
    })
}
// 信息完整度类别详情接口
const information = (params: any) => {
    return http({
        url: infoApiUrl.information + params,
        method: 'GET',
    })
}
// 批量查询区间
const queryAll = (data:any) => {
    return http({
        url: infoApiUrl.query ,
        method: 'POST',
        data
    })
}
export default {
    infoPage,
    sendNotification,
    information,
    queryAll
}
