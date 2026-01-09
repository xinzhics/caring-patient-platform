<template>
    <section>
        <x-header title="slot:overwrite-title" :left-options="{backText: ''}">
           使用说明
        </x-header>
        <div style="padding:5vw;#fff;width:90vw;height:80vh;background:#fff">
            {{allData.interaction}}
        </div>
    </section>
</template>
<script>
export default {
    data(){
        return{
            allData:{}
        }
    },
    mounted(){
        if(this.$route.query){
            console.log(this.$route.query)
            this.allData = this.$route.query
        }
    }
}
</script>

<style lang="less" scoped>
/deep/ .vux-header {
  height: 50px;
}
</style>
