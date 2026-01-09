
import http from '@/utils/http'

const getCompletionInformationStatistics = (params: any) => {
    return http({
        url: '/api/nursing/completionInformationStatistics',
        method: 'GET',
        params
    })
}


/**
 * 统计 首页的 信息完整度 数量
 * @param params
 */
const getNursingCompletionInformationStatistics = () => {
    return http({
        url: '/api/nursing/completenessInformation/countNoComplete/' + localStorage.getItem('caring-userId'),
        method: 'GET',
    })
}

const getNursingIntervalStatistics = (params: any, callbak: Function) => {

    return http({
        url: callbak('/api/nursing/informationMonitoringInterval/nursingIntervalStatistics/{nursingId}'),
        method: 'GET',
        params
    })
}



const sendNotification = (data: any) => {
    return http({
        url: '/api/nursing/completenessInformation/sendNotification',
        method: 'POST',
        data
    })

}

export default {
    getCompletionInformationStatistics,
    getNursingIntervalStatistics,
    getNursingCompletionInformationStatistics,
    sendNotification

}
