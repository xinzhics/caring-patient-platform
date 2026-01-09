import axios from 'axios'
import lanoConfig from '../../../../../lano-config';

import {
    Toast
} from "vant"
import store from "../store"
axios.defaults.headers.post['Content-Type'] = 'application/json';

// create an axios instance
const service = axios.create({
    // baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
    baseURL: process.env.NODE_ENV === 'development' ? 'http://192.168.1.133:3001' : "/", // url = base url + request url
    // withCredentials: true, // send cookies when cross-domain requests
    timeout: 15000 // request timeout
});

// response interceptor
service.interceptors.response.use(
    /**
     * If you want to get information such as headers or status
     * Please return  response => response
     */

    /**
     * Determine the request status by custom code
     * Here is just an example
     * You can also judge the status by HTTP Status Code.
     */
    response => {
        const res = response.data;

        // if the custom code is not 20000, it is judged as an error.
        if (!res.success) {
            // Toast.fail(res.message || 'error');

            // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
            if (res.code === 50008 || res.code === 50012 || res.code === 50014) {

                // to re-login
                // MessageBox.confirm('You have been logged out, you can cancel to stay on this page, or log in again', 'Confirm logout', {
                //   confirmButtonText: 'Re-Login',
                //   cancelButtonText: 'Cancel',
                //   type: 'warning'
                // }).then(() => {
                //   store.dispatch('user/resetToken').then(() => {
                //     location.reload()
                //   })
                // })
            }
            if (res.resultCode == -10001) {
                store.dispatch('set404', true).then(() => {
                    location.reload();
                });
            }
            if (res.resultCode == -10002) {
                store.dispatch('setNoLogin', true).then(() => {
                    location.reload();
                });
            }
            return Promise.resolve(res);
        } else {
            return res;
        }
    },
    error => {
        Toast.fail(error.message || 'error');
        return Promise.resolve(error);
    }
);

window.Request = {
    post: (url, data) => {
        return new Promise((res, rej) => {
            service({
                url: url,
                headers: {
                    'Content-type': 'application/json'
                },
                method: 'post',
                data
            }).then((result) => {
                res(result);
            }).catch((err) => {
                rej(err);
            });
        });
    },
    get: (url, data) => {
        return new Promise((res, rej) => {
            service({
                url: url,
                method: 'get',
                headers: {
                    'Content-type': 'application/json'
                },
                params: data
            }).then((result) => {
                res(result);
            }).catch((err) => {
                rej(err);
            });
        });
    }
};

window.Service = {
    post: (url, data) => {
        const time = Date.parse(new Date());
        return new Promise((res, rej) => {
            service({
                url: '/ajax?time=' + time,
                method: 'post',
                headers: {
                    'Content-type': 'application/json'
                },
                data: {
                    service: url,
                    body: "true",
                    params: data
                }
            }).then((result) => {
                res(result);
            }).catch((err) => {
                rej(err);
            });
        });
    },
    get: (url, datas) => {
        let time = Date.parse(new Date());
        return new Promise((res, rej) => {
            service({
                url: '/ajax?time=' + time,
                method: 'get',
                headers: {
                    'Content-type': 'application/json'
                },
                params: {
                    service: url,
                    params: datas
                }
            }).then((result) => {
                res(result);
            }).catch((err) => {
                rej(err);
            });
        });
    }
};

window.Upload = {
    uploadFile: (data) => {
        const time = Date.parse(new Date());
        return new Promise((res, rej) => {
            service({
                url: '/upfile?time=' + time,
                method: 'post',
                data
            }).then((result) => {
                res(result);
            }).catch((err) => {
                rej(err);
            });
        });
    }
};

export default {
    service,
    Request,
    Upload
}
