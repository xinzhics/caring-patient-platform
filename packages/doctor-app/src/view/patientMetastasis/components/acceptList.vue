<template>
    <div class="content">
        <div class="item" v-for="(i,k) in list" :key="k">
            <div class="itemHeader">
                <div class="left">
                    <img :src="i.patientAvatar" alt="" srcset="" style="border-radius:50%;height: 50px;width: 50px">
                    <div class="detail">
                        <p class="name"> {{i.patientName}}</p>
                      <div class="doc">
                        <span v-if="i.patientSex!==null">{{i.patientSex==0?'男':'女'}}</span>
                        <span v-if="i.patientSex!=null&&i.patientAge!=null&&i.patientAge!==0">|</span>
                        <span v-if="i.patientAge!=null&&i.patientAge!==0">{{i.patientAge}} 岁</span>
                      </div>
                    </div>
                </div>
                <div class="right">
                    <span :class="i.referralStatus==0?'got active':'got'"  v-if="i.referralStatus==0" @click="obj=i;show=true">接收</span>
                    <span class="got" v-if="i.referralStatus==1">已接收</span>
                    <span class="got" v-if="i.referralStatus==2">已取消</span>
                </div>
            </div>
            <div class="itemContent">
                <p>
                    <span class="label">发起时间:</span>
                    <span>{{i.launchTime}}</span>
                </p>
                <p>
                    <span class="label">接收时间:</span>
                    <span>{{i.acceptTime}}</span>
                </p>
                <p>
                    <span class="label">接收{{name}}:</span>
                    <span>{{i.acceptDoctorName}}</span>
                </p>
                <p>
                    <span class="label">转诊性质:</span>
                    <span>{{i.referralCategory==0?'单次转诊':'长期转诊'}}</span>
                </p>
            </div>
        </div>
        <confirm v-model="show"
        @on-cancel="onCancel"
        @on-confirm="onConfirm">
        <div style="text-align:center;">
            <div><img height="60" width="60" :src="require('@/assets/my/warning.png')"/></div>
            <p style="margin-top: 10px">确定接收本次转诊？</p>
        </div>
      </confirm>
    </div>
</template>
<script>
import { Confirm  } from 'vux'
import Api from '@/api/Content.js'
export default {
     components: {
        Confirm
    },
     props: {
        list: {
            type: Array,
            default() {
                return []
            }
        }
     },
     data(){
         return{
              obj:{},
              allList:[],
              show:false,
              name: this.$getDictItem('doctor')
         }
     },
     methods:{
         acceptBtn(){
             const params={
                acceptDoctorId:this.obj.acceptDoctorId,
                id:this.obj.id
            }
            Api.acceptReferralId(params).then((res) => {
                if(res.data.code === 0){
                    this.$vux.toast.text('接收成功', 'center')
                    this.$emit('getInfo')
                }else{
                    this.$vux.toast.text(res.data.msg, 'center')
                }
            })
         },

        onCancel () {
            console.log('on cancel')
        },
        onConfirm (msg) {
            this.acceptBtn()
        },
        onHide () {
            console.log('on hide')
        },
        onShow () {
            console.log('on show')
        },
         cancel(){

         }
     }
}
</script>
<style lang="less" scoped>
.content{
    .item{
        margin-top: 10px;
        padding: 10px 20px;
        background: #fff;
        font-size: 14px;
        .itemHeader{
            display: flex;
            justify-content: space-between;
            border-bottom: 1px solid #ccc;
            padding-bottom:10px;
            margin-bottom: 10px;
            .left{
                display: flex;
                img{
                    width: 50px;
                    margin-right: 10px;
                }
                .detail{
                    p{

                        line-height: 25px;
                    }
                    .doc{
                        font-size:12px;
                        color:#999
                    }
                    .name{
                        font-size:16px;
                        font-weight:bolder
                    }
                }
            }
            .right{
                display:flex;
                .quxiao{
                    align-self:center;
                    padding: 0px 10px;
                    line-height: 20px;
                    border: 1px solid #FF9848;
                    color: #FF9848;
                    border-radius: 30px;
                }
                .got{
                    align-self:center;
                    padding: 0px 10px;
                    line-height: 20px;
                    color: #999;
                }
                .active{
                    border: 1px solid #3F86FF;
                    color: #3F86FF;
                    border-radius: 30px;
                }
            }
        }
        .itemContent{
            p{
                line-height: 30px;
                .label{
                    display: inline-block;
                    width: 70px;
                    color: #999;
                }
                span{
                    color: #555;
                }
            }
        }
    }
    .bottomBtn{
        padding: 30px 0px;
        div{
            width: 60%;
            background: #3F86FF;
            text-align: center;
            color: #fff;
            margin: 0px auto;
            border-radius: 60px;
            padding: 10px 0px;
        }

    }
}
</style>
