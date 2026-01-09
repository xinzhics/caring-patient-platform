

import http from '@/utils/http'

const userApiUrl = {
    sava: '/', 
}
const sava = (params: any) => {
    return http({
        url: userApiUrl.sava,
        method: 'GET',
        params
    })
}



export default {
    sava
}
