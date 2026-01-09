
import axios, { AxiosRequestConfig } from 'axios'
import { Toast } from 'vant'
// 取消重复请求
let pending: Array<any> = []
const CancelToken = axios.CancelToken

const removePending = (config: AxiosRequestConfig) => {
	for (let p in pending) {
		if (
			pending[p].u ===
			config.url + JSON.stringify(config.data) + '&' + config.method
		) {
			pending[p].f()
			// @ts-ignore
			pending.splice(p, 1)
		}
	}
}
const options: AxiosRequestConfig = {
	baseURL: import.meta.env.VITE_AXIOS_BASE_URL as string,
	// timeout: 15000,
	withCredentials: true,
	headers: {
		'Content-Type': 'application/json; charset=utf-8',
	},
}
// 创建axios实例
const http = axios.create(options)
let requestToast: any
// 添加请求拦截器
http.interceptors.request.use(
	function (config: any) {
		if (config.headers.isLoading !== false) {
			requestToast = Toast.loading({
				message: '加载中...',
				forbidClick: true,
				loadingType: 'spinner',
			})
		}
		removePending(config)

		config.cancelToken =

			new CancelToken((c) => {
				pending.push({
					u: config.url + JSON.stringify(config.data) + '&' + config.method,
					f: c,
				})
			})
		// 在发送请求之前做些什么
		const token = localStorage.getItem('caring-token')
		// 后端认证 又 app 提供·
		config.headers.token = 'Bearer ' + token
		// 客户端认证
		config.headers['Authorization'] = `Basic Y2FyaW5nX3VpOmNhcmluZ191aV9zZWNyZXQ=`
		// 租户， 用于后端区分请求者所属项目
		config.headers.tenant = localStorage.getItem('caring-tenant')

		return config
	},
	function (error) {
		requestToast && requestToast.clear()
		// 对请求错误做些什么
		return Promise.reject(error.response)
	}
)

// 添加响应拦截器
http.interceptors.response.use(
	function (response) {
		requestToast && requestToast.clear()
		removePending(response.config)
		// 对响应数据做点什么
		if (response.data.code === 200 || response?.status === 200) {
			return response.data
		} else {
			switch (response.data.code) {
				case 401:
					Toast('登录失效，请重新登录')
					localStorage.clear()
					location.href = `${import.meta.env.VITE_PUBLIC_PATH}login`
					break
				default:
					break
			}
		}
		Toast(response.data.msg)
	},
	function (error) {
		requestToast && requestToast.clear()
		try {
			const { status, data } = error.response || {}
			// 对响应错误做点什么
			switch (status) {
				case 401:
					Toast('登录失效，请重新登录')
					localStorage.clear()
					location.href = `${import.meta.env.VITE_PUBLIC_PATH}login`
					break
				case 403:
					Toast('暂无权限，请联系管理员')
					break
				case 404:
					Toast('资源请求未找到')
					break
				case 405:
					Toast('http请求方式有误')
					break
				case 500:
					data && Toast(data.msg)
					break
				case 501:
					Toast('服务器不支持当前请求所需要的某个功能')
					break
				case 504:
					Toast('网络超时，请稍后重试')
					break
				default:
					data && Toast(data.msg || '服务异常，请稍后重试')
					break
			}
		} catch (error) {
			return Promise.reject(error)
		}
	}
)


export default http
