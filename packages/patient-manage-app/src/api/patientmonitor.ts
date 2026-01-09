import http from '@/utils/http'
//异常数据未处理列表
const dataUnprocessedList = (data: any) => {
    return http({
        url: '/api/nursing/indicatorsResultInformation/dataUnprocessedList',
        method: 'POST',
        data
    })
}
//异常数据已处理列表
const dataProcessedList = (data: any) => {
    return http({
        url: '/api/nursing/indicatorsResultInformation/dataProcessedList',
        method: 'POST',
        data
    })
}
//处理单个异常数据
const unprocessedEliminate = (data: any) => {
    return http({
        url: `/api/nursing/indicatorsResultInformation/UnprocessedEliminate`,
        method: 'POST',
        data
    })
}
//全部清除异常数据已处理列表
const processedEliminate = (patientId: any) => {
    return http({
        url: `/api/nursing/indicatorsResultInformation/ProcessedEliminate/${patientId}`,
        method: 'GET',
    })
}
//异常统计数量
const statisticsAbnormalQuantity = (years: string, planId: string) => {
    return http({
        url: `/api/nursing/indicatorsResultInformation/StatisticsAbnormalQuantity/${years}/${planId}`,
        method: 'GET',
    })
}
//监测计划说明
const planDescription = (planId: string) => {
    return http({
        url: `/api/nursing/indicatorsResultInformation/explain/${planId}`,
        method: 'GET',
    })
}
//监测数据处理数量
const monitorUnusualPatients = () => {
    return http({
        url: `/api/nursing/indicatorsResultInformation/MonitorUnusualPatients`,
        method: 'GET',
    })
}
//监测数据未处理数量
const abnormalData = () => {
    return http({
        url: `/api/nursing/indicatorsResultInformation/abnormalData`,
        method: 'GET',
    })
}
export default {
    dataUnprocessedList,
    dataProcessedList,
    processedEliminate,
    statisticsAbnormalQuantity,
    unprocessedEliminate,
    planDescription,
    monitorUnusualPatients,
    abnormalData
}