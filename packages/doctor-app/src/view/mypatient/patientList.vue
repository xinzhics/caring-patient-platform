<template>
  <div style="background: #F5F5F9 ;height: 100vh">
    <x-header @on-click-back="gobackpage" style="margin: 0 !important;" :left-options="{backText: '',preventGoBack:true}">所有{{ $getDictItem('patient') }}({{ total }})

    </x-header>
    <div class="list" style="margin-top: 8px;padding-left: 6px;padding-right: 6px">
      <form action="/">
        <van-search
            style="padding: 0 !important"
            v-model="params.model.patientName"
            placeholder="搜索"
            @search="onSearch"
        />
      </form>
    </div>
    <!--   患者列表-->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
      >
        <div v-for="(i,index) in patientData" :key="index">
          <div v-if="index==0||(index-1>=0&&i.nameFirstLetter!==patientData[index-1].nameFirstLetter)"
               style="height: 32px;background: #F7F4F8;line-height: 32px;padding-left: 15px;
           font-size: 12px;color: #B8B8B8;"
          >{{ i.nameFirstLetter=='Z#'?'#': i.nameFirstLetter}}
          </div>
          <div v-for="(item,k) in i.values" :key="k"
               :class="k<i.values.length-1?'boxBorder':''"
               style="display: flex;justify-content: space-between;background: #FFFFFF;position: relative;">
            <div style="height: 49px;line-height: 49px;padding-left: 15px;background: #FFFFFF">
              <img v-if="item.avatar" style="width: 33px;position: absolute;top: 50%;transform: translateY(-50%)" :src="item.avatar" alt="">
              <img v-if="!item.avatar" style="width: 35px;position: absolute;top: 50%;transform: translateY(-50%);left: 15px"
                   src="../../components/arrt/images/head-portrait.png" alt="">
              <span style="margin-left: 50px;color: #666666;">            {{ doctorId === item.doctorId && item.doctorRemark ? item.name + '('+item.doctorRemark + ')': item.name }}
              </span>
            </div>
            <div style="position: relative">
              <van-checkbox @change="isChoose(item)" style="position: absolute;right: 12px;top: 50%;transform: translateY(-50%)"
                            v-model="item.checked"></van-checkbox>
            </div>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
    <!--底部确定-->
    <div style="position: fixed;bottom: 0;height: 62px;background: #fff;width: 100%;">
      <div @click="choosePatient" style="width: 83px;height: 35px;border-radius: 42px;background: #3F86FF;color: #fff;text-align: center;line-height: 35px;margin-left: 277px;margin-top: 15px">确定</div>
    </div>
  </div>
</template>

<script>
import Api from '@/api/Content.js'

export default {
  data() {
    return {
      id:undefined,
      loading: false,
      finished: false,
      refreshing: false,
      title:'',
      value: '',
      params: {
        "current": 1,
        "map": {},
        "model": {
          "doctorCustomGroupId": 0,
          "doctorId": 0,
          "patientName": ""
        },
        "order": "descending",
        "size": 100,
        "sort": "id"
      },
      total: undefined,
      patientData: [],
      patientList:[],   //选中的患者数据
      removePatientIds:[]  ,//移出的患者id集合
      joinPatientIds:[],//要加入的患者id集合
      alreadyJoinId:[],//已经加入的患者id集合
      doctorId: localStorage.getItem('caring_doctor_id')

    };
  },
  mounted() {
    this.params.model.doctorId = localStorage.getItem('caring_doctor_id')
    if (this.$route.query.id){
      this.params.model.doctorCustomGroupId=this.$route.query.id
    }
    if (this.$route.query.title){
      this.title=this.$route.query.title
    }
  },
  methods:{
    gobackpage(){
        this.$router.push({
          path: '/mypatient/addteam',
          query: {
            title:this.title,
            id:this.$route.query.id
          },
        })
    },
    onSearch(val) {
      this.params.current=1
      this.getPatientList()
    },
    isChoose(a){
      console.log(a)
      if(a.checked==false){                             //如果是false而且是之前选中的就加入删除患者id数组
        if( this.alreadyJoinId.length>0){
          this.alreadyJoinId.forEach(item=>{
            if (item==a.id){
              this.removePatientIds.push(a.id)
            }
          })
        }

        if(this.joinPatientIds.length>0){
          this.joinPatientIds.forEach((item,index)=>{     //如果id在新增数组里 那就删除这个数组
            if(item==a.id){
              this.joinPatientIds.splice(index,1)
            }
          })
        }

      }else{                                    //如果是true 并且没有在已经确定的数组里面就添加到新增的患者id数组
        if(this.alreadyJoinId.length>0){
          let b =0
          this.alreadyJoinId.forEach(item=>{
            if (item==a.id){
              b++
            }
          })
          if(b==0){
            this.joinPatientIds.push(a.id)
          }
        }
        if(this.removePatientIds.length>0){
          this.removePatientIds.forEach((item,index)=>{
            if (item==a.id){
              this.removePatientIds.splice(index,1)
            }
          })
        }
        if(this.alreadyJoinId.length==0&&a.checked==true){
          this.joinPatientIds.push(a.id)
        }
      }
      console.log(this.joinPatientIds,this.removePatientIds)

    },
    onRefresh() {
      // 清空列表数据
      this.finished = false;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true;
      this.onLoad();
    },
    onLoad() {
      if (this.refreshing) {
        this.params.current = 1
        this.refreshing = false;
      }
      setTimeout(() => {
        this.getPatientList()
      }, 500);
    },
    getPatientList(){
      Api.pageDoctorPatient(this.params).then(res => {
        console.log(res.data.data)
        if (res.data.code == 0) {
          this.total = res.data.data.total
          if(this.params.current==1){
            this.patientData = res.data.data.records
          }else{
            this.patientData.push(...res.data.data.records)
          }
          res.data.data.records.forEach(item=>{
            item.values.forEach(i=>{
              if (i.checked==true){
                this.alreadyJoinId.push(i.id)
              }
            })
          })
          this.params.current++;

          this.loading = false;
          if (res.data.data.records && res.data.data.records.length <100) {
            this.finished = true;
          }

        }
      })
    },


    //点底部的确定
    choosePatient(){
      this.patientList=[]
      this.patientData.forEach((item,index)=>{
        item.values.forEach(i=>{
          if(i.checked==true){
            this.patientList.push(i.id)
          }
        })
      })
      if (this.$route.query.id){
        // 有id是修改

        let params={
          "groupName":this.$route.query.name,
          "id":this.$route.query.id,
          "joinPatientIds": this.joinPatientIds,
          "removePatientIds":this.removePatientIds
        }
        Api.updateDoctorGroup(params).then(res=>{
          console.log(res)
          if (res.data.code==0){
            this.$router.push({
              path: "/mypatient/addteam",
              query: {
                id:res.data.data.id,
                title:this.$route.query.name
              },
            });
          }
        })
      }else{
        //如果没有id就是新增
        let params={
          doctorId:localStorage.getItem('caring_doctor_id'),
          groupName:this.$route.query.name,
          joinPatientIds:this.patientList
        }
        Api.saveDoctorGroup(params).then(res=>{
          console.log(res)
          if (res.data.code==0){
            this.$router.push({
              path: "/mypatient/addteam",
              query: {
                id:res.data.data.id,
                title:this.$route.query.name
              },
            });
          }
        })
      }

    }
  },
}
</script>

<style scoped lang="less">
/deep/ .list {
  .van-search{
    padding-left: 0 !important;
    .van-search__content{
      background: #fff !important;
      padding-left: 6px !important;
    }
  }
}
.boxBorder{
  border-bottom: 1px solid #EEEEEE;
}
</style>
