<template>
  <div style="background: #F5F5F9 ;height: 100vh">
    <x-header @on-click-back="gobackpage"   style="margin: 0 !important;" :left-options="{backText: '',preventGoBack:true}">{{ pageTitle }}
      <div slot="right" v-if="pageTitle == '编辑分组'">
        <van-icon style="font-size: 20px !important;" name="delete-o" @click="deleteGroup"/>
      </div>
    </x-header>
    <div style="padding: 8px">
      <van-field v-model="addDoctorGroup.groupName"
                 style="padding-top: 9px !important;padding-bottom: 7px !important;font-size: 17px"
                 placeholder="请输入分组名称" @input="setname"/>
    </div>
    <div
        style="width: 100%;height: 63px;background: #fff;color: #666;font-size: 16px;text-align: center;line-height: 63px;margin-bottom: 10px"
        @click="addPatient"
    >
          <span>
             <van-icon name="add-o"/>
          </span>
      <span>
          添加成员
        </span>
    </div>
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
      >
        <div class="list">
          <van-cell>
            <div
                v-for="(i,index) in patientData" :key="index"
                style="display: flex;justify-content: space-between;height: 49px;padding: 8px 14px 8px 13px;line-height: 49px">
              <div style="display: flex;">
                <div style="position: relative">
                  <img v-if="i.avatar"
                       style="width: 33px;height: 33px;position: absolute;top: 50%;transform: translateY(-50%)"
                       :src="i.avatar" alt="">
                  <img v-else style="width: 33px;height: 33px;position: absolute;top: 50%;transform: translateY(-50%)"
                       src="../../components/arrt/images/head-portrait.png" alt="">
                </div>
                <div style="margin-left: 40px"> {{ doctorId === i.doctorId && i.doctorRemark ? i.name + '('+i.doctorRemark + ')': i.name }}</div>
              </div>
              <!-- 使用 right-icon 插槽来自定义右侧图标 -->
              <template slot="right-icon">
                <div style="position: relative" @click="deletePatient(i.id)"><img
                    style="width: 25px;position: absolute;top: 50%;transform: translateY(-50%);right: 0"
                    src="../../images/delete.png" alt=""></div>
              </template>
            </div>

          </van-cell>
        </div>
      </van-list>
    </van-pull-refresh>
    <!--患者列表!!!!!!!!!!!!!!!-->
    <!--底部按钮-->
    <div style="width: 100%">
      <div
          :class="addDoctorGroup.groupName!=''?'btnBack1':'btnBack'"
          @click="creatGroup"
          style="width: 208px;height: 46px;color: #fff;border-radius: 42px; position: fixed;bottom:33px;left:50%;text-align: center;line-height: 46px;transform: translateX(-50%)">
        完成
      </div>
    </div>
    <!--    二次确认弹窗-->
    <van-overlay :show="showdlog">
      <div class="wrapper" @click.stop>
        <div class="block">
          <div style="width: 51px;height: 51px;margin: 25px auto">
            <img :src="require('@/assets/my/careful.png')" alt="">
          </div>
          <div style="height: 34px;margin: 25px auto;text-align: center;color: #666666;">
            <div>{{ Group ? '确定删除该群组？' : `确定移除${$getDictItem('patient')}` }}</div>
            <div>移除后{{ this.$getDictItem('patient') }}将不受影响</div>
          </div>
          <div style="width: 250px;height: 42px;margin: 25px auto;display: flex;justify-content: space-between">
            <div style="width: 121px;height: 42px;text-align: center;line-height: 42px;
              border-radius: 42px 42px 42px 42px;opacity: 1;border: 1px solid #3F86FF;color: #3F86FF"
                 @click="closeDlog"
            >
              取消
            </div>
            <div style="width: 121px;height: 42px;text-align: center;line-height: 42px
            ;border-radius: 8px;background: #3F86FF;border-radius: 42px 42px 42px 42px;opacity: 1;color: #fff"
                 @click="romvePatient"
            >
              确定
            </div>
          </div>
        </div>
      </div>
    </van-overlay>
  </div>
</template>

<script>
import Vue from 'vue';
import {Field, Toast} from 'vant';
import Api from '@/api/Content.js'

Vue.use(Toast);
Vue.use(Field);
export default {
  data() {
    return {
      loading: false,
      finished: false,
      refreshing: false,
      value: '',
      title: "",
      patientData: [],
      tempUuid: this.$route.query.tempUuid,
      params: {
        "current": 1,
        "map": {},
        "model": {
          "doctorCustomGroupId": '',
          "doctorId": '',
        },
        "order": "descending",
        "size": 100,
        "sort": "id"
      },
      addDoctorGroup: {
        "groupName": "",
        "id": 0,
      },
      newGroup:{
        "doctorId": 0,
        "groupName": "",
        "joinPatientIds": []
      },
      showdlog: false,  //删除弹窗显示隐藏
      id: '',
      Group: false,
      pageTitle:'',
      doctorId: localStorage.getItem('caring_doctor_id')
    };
  },
  beforeRouteEnter (to, from, next) {
    next((vm) => {
      if(from.name=='我的患者'){
        vm.pageTitle='新建分组'
      }else{
        vm.pageTitle='编辑分组'
      }
      if (from.query.id==''){
        vm.pageTitle='新建分组'
      }
    });
  },
  beforeMount() {
    if (this.$route.query.id) {
      this.params.model.doctorCustomGroupId = this.$route.query.id
      this.params.model.doctorId = localStorage.getItem('caring_doctor_id')
    }
    if (this.$route.query.title) {
      this.title = this.$route.query.title
      this.addDoctorGroup.groupName = this.$route.query.title
    }
    if (this.tempUuid) {
      const name = localStorage.getItem(this.tempUuid)
      if (name) {
        this.addDoctorGroup.groupName = name
        this.title = name
      }
    }
  },
  methods: {
    setname(a){
     this.title=a
    },
    //返回
    gobackpage(){
      if(this.pageTitle=='编辑分组'){
          this.$router.push({
            path: '/mypatient/GroupIdPatientList',
            query:{
              id:this.$route.query.id,
              title:this.title
            }
          })
        }else if (this.pageTitle=='新建分组'&&this.params.model.doctorCustomGroupId!==''){
          Api.deleteDoctorGroup(this.params.model.doctorCustomGroupId).then(res => {
            if (res.data.code == 0) {
              this.getPatientList()
              this.$router.push({
                path: '/mypatient'
              })
            }
          })
      }
      if(this.pageTitle=='新建分组'&&this.params.model.doctorCustomGroupId==''){
        this.$router.push({
          path: '/mypatient'
        })
      }
    },
    onSearch(val) {
      this.params.current=1
      this.getPatientList()
    },
    //删除小组
    deleteGroup() {
      this.Group = true
      this.showdlog = true

    },
    //点击确定
    creatGroup() {
      if (this.params.model.doctorCustomGroupId){
        this.addDoctorGroup.id = this.$route.query.id
        Api.updateDoctorGroup(this.addDoctorGroup).then(res => {
          if (this.tempUuid) {
            localStorage.removeItem(this.tempUuid)
          }
          if (res.data.code === 0) {
            Toast('修改成功');
            this.$router.push({
              path: '/mypatient',
            })
          }
        })
      }else {
        this.newGroup.doctorId=localStorage.getItem('caring_doctor_id')
        this.newGroup.groupName=this.addDoctorGroup.groupName
        Api.saveDoctorGroup(this.newGroup).then(res=>{
          if (this.tempUuid) {
            localStorage.removeItem(this.tempUuid)
          }
          if(res.data.code===0){
            Toast('创建成功');
            this.$router.push({
              path: '/mypatient',
            })
          }
        })
      }

    },
    //删除单个患者
    deletePatient(id) {
      console.log(id)
      this.id = id
      this.showdlog = true
    },
    closeDlog() {
      this.showdlog = false
      this.Group = false
    },
    romvePatient() {
      if (this.Group) {
        Api.deleteDoctorGroup(this.params.model.doctorCustomGroupId).then(res => {
          if (res.data.code == 0) {
            this.getPatientList()
            Toast('删除成功');
            this.$router.push({
              path: '/mypatient'
            })
          }
        })
      } else {
        Api.deleteDoctorGroupPatient(this.params.model.doctorCustomGroupId, this.id).then(res => {
          console.log(res.data.code)
          if (res.data.code == 0) {
            this.showdlog = false
            this.params.current=1
            this.getPatientList()
          }
        })
      }

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
      setTimeout(() => {
        if (this.refreshing) {
          this.refreshing = false;
        }
        this.getPatientList()
      }, 500);
    },
    getPatientList() {
      Api.pageGroupPatient(this.params).then(res => {
        console.log(res)
        if (res.data.code == 0) {
          if (this.params.current==1){
            this.patientData = res.data.data.records
          }else{
            this.patientData.push(...res.data.data.records)
          }
          this.params.current++;
          this.loading = false;
          if (res.data.data.records && res.data.data.records.length <100) {
            this.finished = true;
          }
        }
      })
    },
    addPatient() {
      if (this.addDoctorGroup.groupName==''){
        Toast('请先输入小组名称');
        return
      }else{
        if (this.tempUuid) {
          localStorage.setItem(this.tempUuid, this.addDoctorGroup.groupName)
        }
        if (this.params.model.doctorCustomGroupId) {
          this.$router.push({
            path: '/mypatient/patientList',
            query: {
              id: this.params.model.doctorCustomGroupId,
              title: this.title,
              name:this.addDoctorGroup.groupName
            },
          })
        } else {
          this.$router.push({
            path: '/mypatient/patientList',
            query: {
              title: this.title,
              name:this.addDoctorGroup.groupName,
              tempUuid: this.tempUuid
            },
          })
        }
      }
    }
  }
}
</script>

<style scoped lang="less">
@font-face {
  font-family: 'my-icon';
  src: url('../../images/delete.png') format('truetype');
}

.my-icon {
  font-family: 'my-icon';
}

.my-icon-extra::before {
  content: '\e626';
}

/deep/ .list {
  .van-cell {
    padding: 0 !important;
  }
}

.btnBack {
  background: #D6D6D6 !important;
  pointer-events: none;

}

.btnBack1 {
  background: #337EFF;
}

/deep/ .vux-header-right {
  .van-icon {
    font-size: 20px !important;
  }
}

.wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.block {
  width: 292px;
  height: 227px;
  background-color: #fff;
  border-radius: 8px;
}
</style>
