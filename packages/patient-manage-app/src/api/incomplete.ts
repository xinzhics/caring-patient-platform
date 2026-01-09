import http from '@/utils/http'
// 获取未完成随访计划列表
const getIncompleteList = () => {
    return http({
        url: '/api/nursing/unfinishedPatientRecord/getUnFinishedPlanList?nursingId=' + localStorage.getItem('caring-userId'),
        method: 'GET'
    })
}

const getDataList = (data: any) => {
    return http({
        url: '/api/nursing/unfinishedPatientRecord/appPage',
        method: 'POST',
        data
    })
}

// 统计未处理已处理数量
const getCountHandleNumber = (data: {}) => {
    return http({
        url: '/api/nursing/unfinishedPatientRecord/countHandleNumber?',
        method: 'POST',
        data
    })
}

// 修改未处理变成已处理
const setIncomplete = (recordId: string) => {
    return http({
        url: '/api/nursing/unfinishedPatientRecord/handleOneResult?recordId=' + recordId,
        method: 'GET',
    })
}



// 全部清空数据
const allClearData = (data: {}) => {
    return http({
        url: '/api/nursing/unfinishedPatientRecord/allClearData',
        method: 'POST',
        data
    })
}

// 全部清空数据
const allHandleData = (data: {}) => {
    return http({
        url: '/api/nursing/unfinishedPatientRecord/allHandleResult',
        method: 'POST',
        data
    })
}

// 全部医生
const getAllDoctor = (data: {}) => {
    return http({
        url: '/api/ucenter/doctor/doctorPage',
        method: 'POST',
        data
    })
}

// 历史医生
const getDoctorHistory = () => {
    return http({
        url: '/api/nursing/unfinishedPatientRecord/queryDoctorHistory?nursingId=' + localStorage.getItem('caring-userId'),
        method: 'GET'
    })
}

// app查看一个记录
const seeOneResult = (recordId:string) => {
    return http({
        url: '/api/nursing/unfinishedPatientRecord/seeOneResult?recordId=' + recordId,
        method: 'GET'
    })
}

export default {
    getIncompleteList,
    getDataList,
    getCountHandleNumber,
    setIncomplete,
    allClearData,
    allHandleData,
    getAllDoctor,
    seeOneResult,
    getDoctorHistory
}
