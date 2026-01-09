<template>

    <section>
      <navBar pageTitle="我的收藏"></navBar>
         <div class="content" :style="{padding:contentList.length===0?'0':'15px'}">
            <div class="itemContent" v-for="(x,y) in contentList" :key="y" @click="goItem(x.channelContent)">
                <div class="contentInner">
                    <p class="title">{{x.channelContent.title}}</p>
                    <div class="decs">
                        <p class="time">{{(/\d{4}-\d{1,2}-\d{1,2}/g.exec(x.channelContent.createTime)[0]).replace(/-/g, '.')}}</p>
                        <div  class="watch">
                            <span class="watchOne"><img  :src="watch" />{{x.channelContent.hitCount}}</span>
                            <span class="watchTwo"><img  :src="say"/>{{x.channelContent.messageNum}}</span>
                        </div>
                    </div>
                </div>
                <img class="itemImg" :src="x.channelContent.icon"/>
            </div>
            <div v-if="contentList&&contentList.length<=contentList.total" style="text-align:center;line-height:30px;font-size:12px;padding:5px 0px 25px"
                @click="params.current = params.current+1;getInfo()">
                <span>点击加载更多...</span>
            </div>
         </div>
        <div class="nodata" v-if="contentList&&contentList.length<1" @click="getInfo()">
            <img :src="noData" alt="">
            <p>暂无数据</p>
        </div>
    </section>
</template>
<script>
import Api from '@/api/Content.js'
export default {
    data(){
        return{
            contentList:[],
            noData: require('@/assets/my/medicineImg.png'),
            watch:require('@/assets/my/watch.png'),
            say:require('@/assets/my/say.png'),
            params:{
               current: 1,
               size:10
            }
        }
    },
  components:{
    navBar: () => import('@/components/headers/navBar'),
  },
    mounted(){
        this.getInfo()
    },
    methods:{
        getInfo(){
            const params= {
                "current": this.params.current,
                "map": {},
                "model": {
                    "userId":localStorage.getItem('userId'),
                    "roleType": 'patient'
                },
                "order": "descending",
                "size": this.params.size,
                "sort": "id"
            }
            Api.contentCollectList(params).then((res) => {
                if(res.data.code === 0){
                    this.contentList=this.contentList.concat(res.data.data.records)
                }
            })
        },
        goItem(i){
            if(i.link){
                location.href=i.link
            }else{
                this.$router.push({path:'/cms/show',query:{id:i.id}})
            }

        }
    }
}
</script>
<style lang="less" scoped>
.nodata {
  width: 100%;
  text-align: center;
  padding: 80px 0px;
  background: #f5f5f5;
}
.content{
    background: #fff;
    .itemContent{
        // background: red;
        display: flex;
        justify-content: space-between;
        line-height: 30px;
        padding: 15px 0px;
        border-bottom: 1px solid rgba(102,102,102,0.1);
        .contentInner{
            width: 62%;
            margin-right: 3%;
            .title{
                font-size: 15px;
                font-weight: 600;
                line-height: 25px;
                margin-bottom: 16px;
                height: 50px;
                text-overflow: -o-ellipsis-lastline;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                line-clamp: 2;
                -webkit-box-orient: vertical;

            }
            .decs{
                line-height: 14px;
                font-size: 14px;
                display: flex;
                justify-content: space-between;
                color: #999;
                .watch{
                    .watchOne{
                        margin-right: 10px;
                        img{
                            width:15px;
                            vertical-align: middle;
                            margin-right:5px
                        }
                    }
                    .watchTwo{
                        img{
                            width:13px;
                            vertical-align: middle;
                            margin-right:5px
                        }
                    }
                }
            }
        }
        .itemImg{
            width: 35%;
            height: 80px;
        }
    }
}
</style>


<style lang="less" scoped>
/deep/ .vux-header {
  height: 50px;
}
</style>
