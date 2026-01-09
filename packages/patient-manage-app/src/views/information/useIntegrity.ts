

import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { information } from '@/api'
export default () => {

    const router = useRouter();
    const list = ref<any>([])
    const show = ref<any>(false);
    const name = ref<any>("");
    const title = ref<any>("全部");
    const finished = ref<any>(false); //是否已加载完成，加载完成后不再触发 load 事件
    const pages = ref<number>(1);
    const current = ref<number>(1);
    const total = ref<number>(0);
    const loading = ref<any>(false);
    const intervalId = ref<string>('')
    const popup = ref()
    const handelInfo = () => {
        show.value = true;
    };
    const actions = ref<any>([{ name: "全部" }])
    onMounted(() => {
        handelAll()
        handelList()
    })
    const onCancel = () => {
        show.value = false;
    };
    const handelGoDetail = (payload: any) => {
        router.push({
            path: "/information/info",
            query: {
                id: payload.id
            }
        });
    };
    const handelItem = (payload: any) => {
        title.value = payload.name;
        intervalId.value = payload.id
        current.value = 1;
        pages.value = 1
        list.value = []

        handelList()
    };
    // 搜索
    const handleSearch = (item: any) => {
        name.value = item;
        current.value = 1
        pages.value = 1
        list.value = []
        handelList()
    };
    // 搜素
    const handelList = async () => {
        console.log(pages.value)
        let nursingId = localStorage.getItem("caring-userId");

        if (pages.value < current.value) {
            finished.value = true;
            return
        }
        const data: any = await information.infoPage({
            current: current.value,
            size: 10,
            model: {
                intervalId: intervalId.value,//完整度
                name: name.value,
                nursingId
            }
        })
        loading.value = false;
        if (data.isSuccess && data?.data?.records) {
            list.value = list.value.concat(data?.data?.records);
            pages.value = data.data?.pages;
            current.value = current.value + 1;
            total.value = data.data.total
        } else {
            pages.value = 0
        }



    }
    // 查询所有的区间
    const handelAll = async () => {
        const data: any = await information.queryAll({})
        if (data.isSuccess) {
            actions.value = data.data.map((item: any) => {
                return {
                    name: item.name,
                    id: item.id
                }
            })
            actions.value.unshift({
                name: '全部'
            })
        }

    }
    const handleRightClick = () => {
        router.push({
            path: "/information/statistics",
            query: { time: Date.now() }
        });
    };

    const handelSendNotification = () => {
        popup.value.show();
    }
    return {
        list,
        show,
        name,
        title,
        handelInfo,
        actions,
        onCancel,
        handelGoDetail,
        handelItem,
        handleSearch,
        handelList,
        finished,
        loading,
        total,
        handleRightClick,
        popup,
        handelSendNotification

    }
}
