import http from '@/utils/http'
import {Base64} from 'js-base64'

// 统计未处理已处理数量
const getCountHandleNumber = (planId: string) => {
    return http({
        url: '/api/nursing/traceIntoFormResultLastPushTime/countHandleNumber?nursingId=' + localStorage.getItem('caring-userId') + '&planId=' + planId,
        method: 'GET',
    })
}

// 统计未处理已处理数量
const getDataList = (data: any) => {
    return http({
        url: '/api/nursing/traceIntoFormResultLastPushTime/appPage',
        method: 'POST',
        data
    })
}

// app处理全部患者异常数据
const allHandleResult = (formId: string) => {
    return http({
        url: '/api/nursing/traceIntoFormResultLastPushTime/allHandleResult?nursingId=' + localStorage.getItem('caring-userId') + '&formId=' + formId,
        method: 'GET',
    })
}

// app处理某患者的异常数据
const onePatientResult = (formId: string, patientId: string) => {
    return http({
        url: '/api/nursing/traceIntoFormResultLastPushTime/handleOnePatientResult?patientId=' + patientId + '&formId=' + formId,
        method: 'GET',
    })
}

// 全部清空数据
const allClearData = (formId: string) => {
    return http({
        url: '/api/nursing/traceIntoFormResultLastPushTime/allClearData?nursingId=' + localStorage.getItem('caring-userId') + '&formId=' + formId,
        method: 'GET',
    })
}

// 统计
const statistics = (data: any) => {
    return http({
        url: '/api/nursing/traceIntoResultFieldAbnormal/statistics?formId=' +data.formId+'&localDate=' + data.localDate + '&nursingId=' + localStorage.getItem('caring-userId'),
        method: 'GET',
    })
}

export default {
    getCountHandleNumber,
    getDataList,
    allHandleResult,
    onePatientResult,
    allClearData,
    statistics
}
