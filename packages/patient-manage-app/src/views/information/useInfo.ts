import {onMounted, ref} from "vue";
import {information} from '@/api'
import {useRoute} from "vue-router";
import {Toast} from "vant";

export default () => {

    const isShow = ref<boolean>(false);
    const route = useRoute()
    const info = ref<any>()
    const base = ref<any>([]) //基本信息
    const jkda = ref<any>([]) //疾病信息
    const fctx = ref<any>([])//复查提醒
    const jkrz = ref<any>([]) //健康日志
    const planNames = ref<any>([]) // 监测计划， 自定义监测计划，自定义护理计划中 的 planName 顺序
    const planFields = ref<any>(new Map()) // 监测计划， 自定义监测计划，自定义护理计划中的东西
    const jcjh = ref<any>([])// 监测计划
    const zdyjcjh = ref<any>([]) //自定义监测计划
    const dzyhljy = ref<any>([]) //自定义护理计划
    const listAll = ref<any>([
        {
            base: []
        }
    ])
    const handelShow = () => {
        isShow.value = true;
    };
    const handelCancel = () => {
        isShow.value = false;
    };
    const handelConfirm = async () => {
        const data: any = await information.sendNotification({
            patientId: info.value.patientId
            // reminder: {
            //     patientIds: []
            // }
        })
        if (data?.isSuccess) {
            Toast('操作成功')
            isShow.value = false;
            // setTimeout(() => {
            //     handelInfo()
            // }, 300);

        }
    };
    onMounted(() => {
        handelInfo()
    })
    // 获取详情
    const handelInfo = async () => {
        const data: any = await information.information(route.query.id)
        if (data?.isSuccess) {
            info.value = data.data;
            handelList(data)

        }

    }
    const handelList = (payload: any) => {
        payload?.data?.patientInformationFields.forEach((element: any) => {
            switch (element.businessType) {
                case '基本信息':
                    base.value.push(element)
                    break
                case '疾病信息':
                    jkda.value.push(element)
                    break
                case '复查提醒':
                    fctx.value.push(element)
                    break
                case '健康日志':
                    jkrz.value.push(element)
                    break
                case '监测计划':
                // jcjh.value.push(element)
                // break
                case '自定义监测计划':
                // zdyjcjh.value.push(element)
                // break
                case '自定义护理计划':
                    // dzyhljy.value.push(element)
                    // break
                {
                    if (planNames.value.indexOf(element.planName) == -1) {
                        planNames.value.push(element.planName)
                    }
                    let fields = planFields.value.get(element.planName)
                    if (!fields) {
                        fields = []
                    }
                    fields.push(element)
                    planFields.value.set(element.planName, fields)
                }
            }

        });
    }
    return {
        isShow,
        handelShow,
        handelCancel,
        handelConfirm,
        info,
        base,
        jkda,
        fctx,
        jkrz,
        jcjh,
        planNames,
        planFields,
        zdyjcjh,
        dzyhljy,
    }
}