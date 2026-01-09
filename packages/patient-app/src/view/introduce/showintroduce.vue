<template>
    <section style="background:#fff">
        <x-header @on-click-back="$router.back()" title="slot:overwrite-title" :left-options="{backText: ''}">
            服务协议
        </x-header>
        <div style="padding:10px;text-align:left;" v-html="alldata.agreement">
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
        this.getInfo()
    },
    methods:{
        getInfo() {
            Api.regGuidegetGuide({}).then((res) => {
                if (res.data.code === 0) {
                    this.alldata = res.data.data
                }
            })
        }
    }
}
</script>