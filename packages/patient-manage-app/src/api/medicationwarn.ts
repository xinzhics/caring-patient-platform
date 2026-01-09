import http from '@/utils/http'
//用药预警-购药逾期
const getDrugsBeOverdue = (data: any) => {
    return http({
        url: '/api/nursing/drugsResultInformation/getDrugsBeOverdue',
        method: 'POST',
        data
    })
}
//用药预警-用药逾期-单个处理
const getDrugsBeOverdueHandle = (id: string) => {
    return http({
        url: `/api/nursing/drugsResultInformation/getDrugsBeOverdueHandle/${id}`,
        method: 'GET',
    })
}
//用药预警-用药逾期-全部处理
const getallDrugsBeOverdueHandle = () => {
    return http({
        url: `/api/nursing/drugsResultInformation/getDrugsBeOverdueHandle`,
        method: 'GET',
    })
}
//用药预警-余药不足
const getDrugsDeficiency = (data: any) => {
    return http({
        url: `/api/nursing/drugsResultInformation/getDrugsDeficiency`,
        method: 'POST',
        data
    })
}
//用药预警-余药不足-单个处理
const getDrugsDeficiencyHandle = (id: string) => {
    return http({
        url: `/api/nursing/drugsResultInformation/getDrugsDeficiencyHandle/${id}`,
        method: 'GET',
    })
}
//用药预警-余药不足-（全部）处理
const getallDrugsDeficiencyHandle = () => {
    return http({
        url: `/api/nursing/drugsResultInformation/getDrugsDeficiencyHandle`,
        method: 'GET',
    })
}
//用药预警-统计-药品列表
const getDrugsList = () => {
    return http({
        url: `/api/nursing/drugsResultInformation/getDrugsList`,
        method: 'GET',
    })
}
//用药预警-统计
const getDrugsStatistics = (id: string) => {
    return http({
        url: `/api/nursing/drugsResultInformation/getDrugsStatistics/${id}`,
        method: 'GET',
    })
}
//用药预警-已处理列表
const getDrugsHandled = (data: any) => {
    return http({
        url: `/api/nursing/drugsResultHandleHis/getDrugsHandled`,
        method: 'POST',
        data
    })
}
//用药预警-已处理列表--清除标记
const getDrugsEliminate = () => {
    return http({
        url: `/api/nursing/drugsResultHandleHis/getDrugsEliminate`,
        method: 'GET',
    })
}
//用药预警-患者数
const getDrugsNumber = () => {
    return http({
        url: `/api/nursing/drugsResultInformation/getDrugsNumber`,
        method: 'GET',
    })
}
export default {
    getDrugsBeOverdue,
    getDrugsBeOverdueHandle,
    getallDrugsBeOverdueHandle,
    getDrugsDeficiencyHandle,
    getallDrugsDeficiencyHandle,
    getDrugsList,
    getDrugsStatistics,
    getDrugsDeficiency,
    getDrugsHandled,
    getDrugsEliminate,
    getDrugsNumber
}
