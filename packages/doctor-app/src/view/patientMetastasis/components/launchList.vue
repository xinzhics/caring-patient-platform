<template>
    <div class="content">
        <div class="item" v-for="(i,k) in list" :key="k">
            <div class="itemHeader">
                <div class="left">
                    <img :src="i.patientAvatar ? i.patientAvatar : require('@/assets/my/touxiang.png')" alt="" srcset="" style="border-radius:50%;width: 50px;height: 50px">
                    <div class="detail">
                        <p class="name">{{i.launchDoctorPatientRemark ?i.patientName+'('+ i.launchDoctorPatientRemark+')' : i.patientName}}</p>
                        <div class="doc">
                          <span v-if="i.patientSex!==null">{{i.patientSex==0?'男':'女'}}</span>
                          <span v-if="i.patientSex!=null&&i.patientAge!=null&&i.patientAge!==0">|</span>
                          <span v-if="i.patientAge!=null&&i.patientAge!==0">{{i.patientAge}} 岁</span>
                        </div>
                    </div>
                </div>
                <div class="right">
                    <span class="quxiao" v-if="i.referralStatus==0" @click="show=true;obj=i">取消</span>
                    <span class="got" v-if="i.referralStatus==1">已接收</span>
                     <span class="quxiao" v-if="i.referralStatus==2" @click="delshow=true;obj=i">删除</span>
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
                <img  v-if="i.referralStatus==2" src="~@/assets/my/cancelImg.png" width="90px" alt="" srcset="">
            </div>
        </div>
        <confirm v-model="show"
            @on-cancel="show=false"
            @on-confirm="onConfirm">
            <div style="text-align:center;">
                <div><img height="60" width="60" :src="require('@/assets/my/warning.png')"/></div>
                <p style="margin-top: 10px">确定取消本次转诊？</p>
            </div>
        </confirm>
        <confirm v-model="delshow"
            @on-cancel="delshow=false"
            @on-confirm="delonConfirm">
            <div style="text-align:center;">
                <div><img height="60" width="60" :src="require('@/assets/my/warning.png')"/></div>
                <p style="margin-top: 10px">确定删除本次转诊？</p>
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
            allList:[],
            show:false,
            obj:{},
            delshow:false,
            name: this.$getDictItem('doctor')
         }
     },
     mounted(){
           // var varperson =JSON.parse(localStorage.getItem("dictionaryItem"))
      // window.dictionaryItem = new Map()
      //     for (let index = 0; index < varperson.length; index++) {
      //       window.dictionaryItem.set(varperson[index].code, varperson[index].name)
      //     }
      //   if (window.dictionaryItem) {
      //   this.name = window.dictionaryItem.get('doctor')
      // }
     },
     methods:{
        onCancel () {
            console.log('on cancel')
        },
        onConfirm (msg) {
            this.obj.referralStatus = 2
            const params = this.obj
             Api.referralPut(params).then((res) => {
                 this.$emit('getInfo')
                if (res.data.code === 0) {
                   this.$vux.toast.text('取消成功', 'center')
                }else{
                    this.$vux.toast.text(res.data.msg, 'center')
                }
            })
        },
        delonConfirm(){
            const params = {id:this.obj.id}
             Api.DelreferralPut(params).then((res) => {
                 this.$emit('getInfo')
                if (res.data.code === 0) {
                   this.$vux.toast.text('删除成功', 'center')
                }else{
                    this.$vux.toast.text(res.data.msg, 'center')
                }
            })

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
    background: #f5f5f5;
    // position: relative;
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
            }
        }
        .itemContent{
            position: relative;
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
            img{
                position: absolute;
                right: 20px;
                bottom: 20px;
            }
        }
    }
}
</style>
