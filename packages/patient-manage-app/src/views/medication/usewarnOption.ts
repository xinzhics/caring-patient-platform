import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import appHeader from "@/components/header/index.vue";
import { Dialog, List, PullRefresh, Toast } from "vant";
import medicationApi from '@/api/medicationwarn'
export default () => {
    const router: any = useRouter()
    const active = ref<number>(1); //选择TAB
    const deficiencylist = ref<Array<any>>([])//余药不足列表
    const deficiencynum = ref<number>(0)//余药不足总数
    const overduelist = ref<Array<any>>([])//逾期列表
    const overduenum = ref<number>(0)//逾期总数
    const handledlist = ref<Array<any>>([])//已处理列表
    const handlednum = ref<number>(0)//已处理总数
    const showexplain = ref<boolean>(false)//预警说明弹窗
    const leftisdown = ref<boolean>(false)//排序
    const isdown = ref<boolean>(false)//排序
    const showTips = ref<boolean>(false); //监测说明弹窗
    const showwarn = ref<boolean>(false); //全部处理弹窗
    const showclear = ref<boolean>(false); //全部清除弹窗
    const current = ref<number>(1)
    const seachval = ref<string>('')
    const height = ref<number>(0)
    const params = ref<any>({
        "current": 1,
        "size": 20,
        "timeSort": 1,
        "drugsSort": 0,

    })
    const model = ref<any>({
        1: showwarn,
        2: showwarn,
        3: showclear
    }); //v-model数据
    const more = ref<any>({
        1: {
            loadmore: false,
            nomore: false
        },
        2: {
            loadmore: false,
            nomore: false
        },
        3: {
            loadmore: false,
            nomore: false
        }
    })
    const Moredata = () => {
        current.value = current.value + 1;
        FuncGetData[active.value]({ current: current.value }, 'more');
    };
    const Tostatistics = () => {
        router.push({
            path: `/medication/statistics`,
            query: {},
        });
    }
    //获取数据
    const FuncGetData: any = {
        1: (data: any, type: string) => {
            medicationApi.getDrugsDeficiency({ ...params.value, ...data }).then(res => {
                if (res && res.data) {
                    deficiencylist.value = type == 'more' ? [...deficiencylist.value, ...res.data.records] : res.data.records
                    deficiencynum.value = res.data.total
                    more.value[1].loadmore = res.data.total > current.value * 20
                    more.value[1].nomore = res.data.total <= current.value * 20
                }
            })
        },
        2: (data: any, type: string) => {
            medicationApi.getDrugsBeOverdue({ ...params.value, ...data }).then(res => {
                if (res && res.data) {
                    overduelist.value = type == 'more' ? [...overduelist.value, ...res.data.records] : res.data.records
                    overduenum.value = res.data.total
                    more.value[2].loadmore = res.data.total > current.value * 20
                    more.value[2].nomore = res.data.total <= current.value * 20
                }
            })
        },
        3: (data: any, type: string) => {
            medicationApi.getDrugsHandled({ ...params.value, ...data }).then(res => {
                if (res && res.data) {
                    handledlist.value = type == 'more' ? [...handledlist.value, ...res.data.records] : res.data.records
                    handlednum.value = res.data.total
                    more.value[3].loadmore = res.data.total > current.value * 20
                    more.value[3].nomore = res.data.total <= current.value * 20
                }
            })
        },
    }
    //处理全部数据
    const FuncHandleAll: any = {
        1: () => {
            medicationApi.getallDrugsDeficiencyHandle().then(res => {
                if (res && res.data) {
                    Toast("已全部处理")
                    FuncGetData[1]()
                    FuncGetData[3]()
                }
            })
        },
        2: () => {
            medicationApi.getallDrugsBeOverdueHandle().then(res => {
                if (res && res.data) {
                    Toast("已全部处理")
                    FuncGetData[2]()
                    FuncGetData[3]()
                }
            })
        },
        3: () => {
            medicationApi.getDrugsEliminate().then(res => {
                if (res && res.data) {
                    Toast("已全部清空")
                    FuncGetData[3]()
                }
            })
        },
    }
    //处理单个数据
    const FuncHandleOne: any = {
        1: (id: string) => {
            medicationApi.getDrugsDeficiencyHandle(id).then(res => {
                if (res && res.data) {
                    Toast("已处理")
                    FuncGetData[1]()
                    FuncGetData[3]()
                }
            })
        },
        2: (id: string) => {
            medicationApi.getDrugsBeOverdueHandle(id).then(res => {
                if (res && res.data) {
                    Toast("已处理")
                    FuncGetData[2]()
                    FuncGetData[3]()
                }
            })
        }
    }
    onMounted(() => {
        FuncGetData[1]()
        FuncGetData[2]()
        FuncGetData[3]()
        height.value = window.innerHeight - 320
    })
    //提示弹窗
    const Showexplain = () => {
        showTips.value = !showTips.value
    }
    //对话框弹窗
    const Showmodal = () => {
        model.value[active.value] = !model.value[active.value]
    }
    //搜索
    const Seach = (val: string) => {
        FuncGetData[active.value]({ patientName: val })
    }
    //切换TAB
    const CheckTab = (num: number) => {
        active.value = num
        current.value = 1
        seachval.value = "";
        more.value[active.value].loadmore = false;
        more.value[active.value].nomore = false;
        FuncGetData[num]()
        isdown.value = false
        leftisdown.value = false
    }
    //排序
    const Sortlist = (type: string) => {
        if (type == 'left') {
            leftisdown.value = !leftisdown.value
            FuncGetData[active.value](leftisdown.value ? {
                "drugsSort": 2,
                "timeSort": 0
            } : {
                "drugsSort": 1,
                "timeSort": 0
            })
        } else {
            isdown.value = !isdown.value
            FuncGetData[active.value](isdown.value ? {
                "drugsSort": 0,
                "timeSort": active.value == 3 ? 2 : 1
            } : {
                "drugsSort": 0,
                "timeSort": active.value == 3 ? 1 : 2

            })
        }
    }

    //单个处理
    const Handleddeficiency = (id: string) => {
        FuncHandleOne[active.value](id)

    }
    //全部处理
    const Handlelist = () => {
        FuncHandleAll[active.value]()
    }
    //APP跳转到患者详情界面
    const Jumppatientinfo = (item: string) => {
        const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
        if (caringCurrentDevice) {
            let history = { path: '/medication/index' }
            localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
            window.parent.postMessage({action: 'patientManageSeePatient', data: item}, '*')
        } else if ((window as any).android) {
            (window as any).android.jumpPatientInfo(item);
        } else if (window.parent) {
            let history = { path: '/medication/index' }
            localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
            window.parent.postMessage({action: 'patientManageSeePatient', data: item}, '*')
        }
    };
    const backIndex = () => {
        router.replace("/index")
    }
    const Jumppatientchat = (item: string) => {
        const caringCurrentDevice = localStorage.getItem("caringCurrentDevice")
        if (caringCurrentDevice) {
            let history = { path: '/medication/index' }
            localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
            window.parent.postMessage({action: 'patientManageChatPatient', data: item}, '*')
        } else if ((window as any).android) {
            (window as any).android.jumpPatientChat(item);
        } else if (window.parent) {
            let history = { path: '/medication/index' }
            localStorage.setItem('patientManageChatPatientRouterHistory', JSON.stringify(history))
            window.parent.postMessage({action: 'patientManageChatPatient', data: item}, '*')
        }
    };
    return {
        Tostatistics,
        Showexplain,
        Showmodal,
        Seach,
        CheckTab,
        Sortlist,
        Handleddeficiency,
        Handlelist,
        deficiencylist,
        deficiencynum,
        overduelist,
        overduenum,
        handledlist,
        handlednum,
        showexplain,
        leftisdown,
        isdown,
        showTips,
        model,
        active,
        appHeader,
        Dialog,
        List,
        PullRefresh,
        Jumppatientinfo,
        Jumppatientchat,
        more,
        Moredata,
        seachval,
        height,
        backIndex
    }
}