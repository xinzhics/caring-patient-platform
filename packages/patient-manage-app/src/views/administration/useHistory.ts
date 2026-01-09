import { onMounted, ref } from "vue";
import { administration } from '@/api'
import { Toast } from "vant";
import { useRoute, useRouter } from "vue-router";
export default () => {

    const list = ref<any>([])
    const isShow = ref<boolean>(false);
    const router = useRouter()
    const route = useRoute()
    const finished = ref<any>(false); //是否已加载完成，加载完成后不再触发 load 事件
    const pages = ref<number>(1);
    const current = ref<number>(1);
    const loading = ref<any>(false);
    const total = ref<any>(0)
    const handelDel = () => {
        isShow.value = true;
    };
    const handelCancel = () => {
        isShow.value = false;
    };
    // 删除
    const handelConfirm = async () => {
        const data: any = await administration.historyDel({ 'ids[]': route.query.orgId })
        if (data.code === 0 && data.isSuccess) {
            Toast('删除成功')
            isShow.value = false;

            setTimeout(() => {
                router.go(-1)
            }, 400);
        }
    };
    onMounted(() => {
        handelInfo()
    })
    // 详情列表
    const handelInfo = async () => {
        if (pages.value < current.value) {
            finished.value = true;
            loading.value = true;
            return
        }


        const data: any = await administration.historyInfoPage({
            current: 1,
            size: 10,
            model: {
                historyId: route.query.orgId
            }
        })
        loading.value = false;

        if (data.isSuccess) {
            list.value = list.value.concat(data?.data.records);
            pages.value = data.data?.pages;
            current.value = current.value + 1;
            total.value = data.data.total
        }

        // list.value = [{}]

    }
    return {
        list,
        isShow,
        handelDel,
        handelCancel,
        handelConfirm,
        finished,
        loading,
        handelInfo,
        total

    }
}
