import http from '@/utils/http'
import {Base64} from 'js-base64'

// 首页获取路由
const getList = () => {
    return http({
        url: '/api/tenant/patientManageMenu/queryList?code=' + Base64.decode(<string>localStorage.getItem('caring-tenant')),
        method: 'GET',
    })
}

// 获取异常追踪数量
const getAppTracePlanList = () => {
    return http({
        url: '/api/nursing/traceIntoFormResultLastPushTime/getAppTracePlanList?nursingId=' + localStorage.getItem('caring-userId'),
        method: 'GET',
    })
}


// 获取路由数量
const getCountPatientManageNumber = () => {
    return http({
        url: '/api/nursing/completenessInformation/countPatientManageNumber/' + localStorage.getItem('caring-userId'),
        method: 'GET',
    })
}


export default {
    getList,
    getAppTracePlanList,
    getCountPatientManageNumber
}
