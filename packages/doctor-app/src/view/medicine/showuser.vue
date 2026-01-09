<template>
    <section>
        <x-header @on-click-back="$router.back()" title="slot:overwrite-title" :left-options="{backText: ''}">
        使用说明
        </x-header>
        <div style="padding:10px 15px 30px;text-align:left;overflow-y: scroll;background:#fff">
            <div>
            <span style="color:rgba(0,0,0,0.85);font-size:13px">药品名称：</span><span style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.name}}</span>
            </div>
            <div style="margin-top:5px">
            <span style="color:rgba(0,0,0,0.85);font-size:13px">药品通用名称：</span><span style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.genericName}}</span>
            </div>
            <div style="margin:5px 0px">
            <p style="color:rgba(0,0,0,0.85);font-size:13px;margin:5px 0px">【适用症状】</p>
            <p style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.applicableDisease||'暂无'}}</p>
            </div>
            <div style="margin:5px 0px">
            <p style="color:rgba(0,0,0,0.85);font-size:13px;margin:5px 0px">【用法用量】</p>
            <p style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.dosage||'暂无'}}</p>
            </div>
            <div style="margin:5px 0px">
            <p style="color:rgba(0,0,0,0.85);font-size:13px;margin:5px 0px">【禁忌】</p>
            <p style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.taboo||'暂无'}}</p>
            </div>
            <div style="margin:5px 0px">
            <p style="color:rgba(0,0,0,0.85);font-size:13px;margin:5px 0px">【不良反应】</p>
            <p style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.sideEffects||'暂无'}}</p>
            </div>
            <div style="margin:5px 0px">
            <p style="color:rgba(0,0,0,0.85);font-size:13px;margin:5px 0px">【药物互作用】</p>
            <p style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.interaction||'暂无'}}</p>
            </div>

        </div>
    </section>
</template>
<script>
import Api from '@/api/Content.js'
export default {
    data(){
        return{
            alldata:{}
        }
    },
    mounted(){
        let that = this
        if(that.$route.query&&that.$route.query.drugsId){
            that.getInfo(that.$route.query.drugsId)
        }
    },
    methods:{
        getInfo(i) {
            const params = {
                id: i,
            }
            // TODO: 接口要换成单个查询的
            Api.sysDrugsPagedetail(params).then((res) => {
                if (res.data.code === 0&&res.data.data&&res.data.data.length>0) {
                this.alldata = res.data.data[0]
                }
            })
        }
    }
}
</script>
