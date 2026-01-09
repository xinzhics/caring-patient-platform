
import { createWebHistory, createRouter, RouteRecordRaw } from 'vue-router'
const routes: RouteRecordRaw[] = [
    // 登录
    {
        name: 'login',
        component: () => import('@/views/login/index.vue'),
        path: '/login',
        meta: {
            title: '注册登录',
        },
    },
    // 首页
    {
        name: 'index',
        component: () => import('@/views/index/index.vue'),
        path: '/index',
        meta: {
            title: '工作台',
        },
    },
    // 管理历史
    {
        name: 'history',
        component: () => import('@/views/administration/history.vue'),
        path: '/history',
        meta: {
            title: '管理历史',
        },
    },
    // 管理历史详情
    {
        name: 'historyInfo',
        component: () => import('@/views/administration/historyInfo.vue'),
        path: '/historyInfo',
        meta: {
            title: '管理历史详情',
        },
    },
    // 信息完整度
    {
        name: 'integrity',
        component: () => import('@/views/information/integrity.vue'),
        path: '/information/integrity',
        meta: {
            title: '信息完整度',
        },
    },
    // 信息完整度详情
    {
        name: 'informationInfo',
        component: () => import('@/views/information/info.vue'),
        path: '/information/info',
        meta: {
            title: '信息完整度详情',
        },
    },
    {
        name: 'integritystatistics',
        component: () => import('@/views/information/statistics.vue'),
        path: '/information/statistics',
        meta: {
            title: '信息完整度统计',
        },
    },
    //完整统计
    {
        name: 'monitordataStatistics',
        component: () => import('@/views/monitordata/statistics.vue'),
        path: '/monitordata/statistics',
        meta: {
            title: '监测统计',
        },
    },
    //详情
    {
        name: 'health',
        component: () => import('@/views/health/info.vue'),
        path: '/health/info',
        meta: {
            title: '详情',
        },
    },
    //血小板列表
    {
        name: 'platelet',
        component: () => import('@/views/monitordata/list.vue'),
        path: '/monitordata/list',
        meta: {
            title: '监测指标',
        },
    },
    //检测数据
    {
        name: 'monitordata',
        component: () => import('@/views/monitordata/index.vue'),
        path: '/monitordata/index',
        meta: {
            title: '监测数据',
        },
    },
    //用药预警
    {
        name: 'medication',
        component: () => import('@/views/medication/earlywarning.vue'),
        path: '/medication/index',
        meta: {
            title: '用药预警',
        },
    },
    {
        name: 'medicationstatistics',
        component: () => import('@/views/medication/statistics.vue'),
        path: '/medication/statistics',
        meta: {
            title: '预警数据',
        },
    },
    {
        name: 'statistics',
        component: () => import('@/views/abnormal/statistics/index.vue'),
        path: '/abnormal/statistics',
        meta: {
            title: '异常选项统计',
        },
    },
    {
        name: 'abnormal',
        component: () => import('@/views/abnormal/index.vue'),
        path: '/abnormal/index',
        meta: {
            title: '异常选项跟踪',
        },
    },
    {
        name: 'abnormal-list',
        component: () => import('@/views/abnormal/list.vue'),
        path: '/abnormal/list',
        meta: {
            title: '异常选项跟踪-患者列表',
        },
    },
    {
        name: 'incomplete',
        component: () => import('@/views/incomplete/index.vue'),
        path: '/incomplete/index',
        meta: {
            title: '未完成任务-患者列表',
        },
    },
    {
        name: 'incomplete-list',
        component: () => import('@/views/incomplete/list.vue'),
        path: '/incomplete/list',
        meta: {
            title: '未完成任务-患者列表',
        },
    }
]
const base: any = import.meta.env.VITE_PUBLIC_PATH
const router = createRouter({
    history: createWebHistory(base),
    routes,
})
router.beforeEach((to, form, next) => {
    // 先取 路径上的 医助ID 。 路径上没有 ，看本地缓存。 本地缓存也没有，则使用测试数据
    // token 和租户同样
    console.log('beforeEach', to, form)
    const userId = getQueryString('userId');
    let localUserId = localStorage.getItem("caring-userId")
    if (userId) {
        localStorage.setItem("caring-userId", userId)
    } else {
        if (!localUserId) {
            localStorage.setItem("caring-userId", '1506916742331367424')
        }
    }
    const token = getQueryString('token')
    let localToken = localStorage.getItem("caring-token")
    if (token) {
        localStorage.setItem("caring-token", token)
    } else {
        if (!localToken) {
            localStorage.setItem("caring-token", 'test')
        }
    }
    const currentDevice = getQueryString('currentDevice')
    if (currentDevice) {
        localStorage.setItem('caringCurrentDevice', currentDevice)
    }
    const tenant = getQueryString('tenant')
    let localTenant = localStorage.getItem("caring-tenant")
    if (tenant) {
        localStorage.setItem("caring-tenant", tenant)
    } else {
        if (!localTenant) {
            localStorage.setItem("caring-tenant", 'MDExMg==')
        }
    }

    next();
})

router.afterEach((to, form) => {
    const caringCurrentDevice = localStorage.getItem('caringCurrentDevice')
    localStorage.setItem('patient-manage-router-path', to.path)
    if (caringCurrentDevice && caringCurrentDevice === 'weixin') {
        if (to.path === form.path) {
            localStorage.setItem('canClosePatientManage', 'true')
        } else {
            localStorage.removeItem('canClosePatientManage')
        }
    }
})

function getQueryString(name: any) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = decodeURI(window.location.search.substr(1)).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

export default router
