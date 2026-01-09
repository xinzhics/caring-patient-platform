<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :rightIcon="doctorId ? 'delete' : ''" @showpop="deleteDoctorFunc"
                      :title="( doctorId ? '编辑' : '添加' ) + $getDictItem('doctor')" @onBack="goback"></headNavigation>
    </van-sticky>
    <div style="margin-top: 13px">
      <div style="background: #fff;height: 57px;display: flex;justify-content: space-between;align-items: center;padding: 17px 13px;border-bottom: 1px solid #eeeeee">
        <div class="list-left">头像</div>
        <div>
          <div style="display: flex;justify-content: space-between;align-items: center">
            <headPortrait :avatar="doctor.avatar ? doctor.avatar : $getDoctorDefaultAvatar('noAvatar')" :uploadCompleted="uploadCompleted"></headPortrait>
            <div>
              <van-icon name="arrow" color="#B8B8B8" />
            </div>
          </div>
        </div>
      </div>
      <div class="box " @click="openNameDialog">
        <div class="list-left box2">姓名</div>
        <div>
          <span v-if="doctor.name" style="color: #333">{{ doctor.name }}</span>
          <span v-else style="color: #B8B8B8">请输入</span>
          <van-icon name="arrow" color="#B8B8B8" />
        </div>
      </div>
      <div class="box " @click="openDoctorPhone" style="margin-bottom: 11px">
        <div class="list-left box2">电话</div>
        <div>
          <span v-if="doctor.mobile" style="color: #333">{{ doctor.mobile }}</span>
          <span v-else style="color: #B8B8B8">请输入</span>
          <van-icon name="arrow" color="#B8B8B8" />
        </div>
      </div>
      <div class="box" @click="show3=true">
        <div class="list-left">医院</div>
        <div class="list-right">
          <span v-if="doctor.hospitalName" style="color: #333">{{ doctor.hospitalName }}</span>
          <span v-else style="color: #B8B8B8">请输入</span>
          <van-icon name="arrow" color="#B8B8B8" />
        </div>
      </div>
      <div class="box" @click="openDoctorDepartment">
        <div class="list-left">科室</div>
        <div class="list-right">
          <div v-if="doctor.deptartmentName" class="left-box">{{ doctor.deptartmentName }}</div>
          <div v-else class="left-box" style="color: #B8B8B8">请输入</div>
          <van-icon name="arrow" color="#B8B8B8" />
        </div>
      </div>
      <div class="box" @click="openDoctorTitle">
        <div class="list-left">职称</div>
        <div class="list-right">
          <div v-if="doctor.title" class="left-box">{{ doctor.title }}</div>
          <div v-else class="left-box" style="color: #B8B8B8">请输入</div>
          <van-icon name="arrow" color="#B8B8B8" />
        </div>
      </div>
      <div class="box" @click="goSpeciality">
        <div class="list-left">专业特长</div>
        <div class="list-right">
          <div v-if="specialitys" class="left-box">{{ specialitys}}</div>
          <div v-else class="left-box" style="color: #B8B8B8">请输入</div>
          <van-icon name="arrow" color="#B8B8B8" />
        </div>
      </div>
      <div class="box" @click="goIntroduce">
        <div class="list-left">详细介绍</div>
        <div class="list-right">
          <div v-if="introduces" class="left-box">{{ introduces}}</div>
          <div v-else class="left-box" style="color: #B8B8B8">请输入</div>
          <van-icon name="arrow" color="#B8B8B8" />
        </div>
      </div>
    </div>
    <!--      弹窗部分-->
    <!--      姓名弹窗-->
    <editDoctorDialog ref="setDoctorName" maxTextNumber="10" title="姓名" type="text" @value="setDocName" ></editDoctorDialog>
    <!--      电话号弹窗-->
    <editDoctorDialog ref="setDoctorPhone" maxTextNumber="11" title="手机号" type="tel" @value="setDocPhone" ></editDoctorDialog>
    <!--      科室弹窗-->
    <editDoctorDialog ref="setDoctorDepartment" maxTextNumber="10" title="科室" type="text" @value="setDocDepartment" ></editDoctorDialog>
    <!--      职称弹窗-->
    <editDoctorDialog ref="setDoctorTitle" maxTextNumber="10" title="职称" type="text" @value="setDocTitle" ></editDoctorDialog>

    <!--      医院弹窗-->
    <van-popup  class="hosp-pop" v-model="show3" position="bottom" :style="{ height: '100%',padding:'0' }">
      <section style="background: #fafafa; padding: 0">
        <van-sticky>
          <headNavigation leftIcon="arrow-left" :rightIcon="''" title="选择医院" @onBack="hide"></headNavigation>
          <van-search v-model="hospitalName" placeholder="搜索" @input="onInput" @blur="onBlur" @focus="onFocus" @search="onSearch" show-action>
            <div slot="action" @click="onSearch">
              <van-button type="info" @click="onSearch">搜索</van-button>
            </div>
          </van-search>
        </van-sticky>

        <!-- 三个选项 -->

        <van-row type="flex" justify="space-between" align="center" style="height: 48px" v-show="type !== 4">
          <van-col offset="2" @click="chooseOptions(1)" span="8" :style="{ color: type >= 1 ? '#337EFF' : '' }">省份
            <van-icon name="arrow-down" :style="{ color: type >= 1 ? '#337EFF' : '' }" />
          </van-col>
          <van-col span="8" @click="chooseOptions(2)" :style="{ color: type >= 2 ? '#337EFF' : '' }">城市
            <van-icon name="arrow-down" :style="{ color: type >= 2 ? '#337EFF' : '' }" />
          </van-col>
          <van-col span="6" @click="chooseOptions(3)" :style="{ color: type === 3 ? '#337EFF' : '' }">医院
            <van-icon name="arrow-down" :style="{ color: type === 3 ? '#337EFF' : '' }" />
          </van-col>
        </van-row>
        <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
          <van-list v-if="focus" v-model="loading" :finished="finished" :finished-text="focus?'没有更多了':''" @load="onLoad">
            <div class="showData" v-for="(i, index) in list" :key="index">
              <div class="options" @click="chooseOption(i)">
                {{ getItem(i) }}
              </div>
            </div>
          </van-list>
        </van-pull-refresh>
      </section>
    </van-popup>
    <!--      专业特长弹窗-->
    <speciality ref="specialitys" @message="setSpeciality"></speciality>

<!--    详细介绍弹窗-->
    <introduce ref="introduces" @message="setIntroduce"></introduce>

    <div style="text-align: center;">
      <van-button
        :class="disabledStatus() ? 'disable' : ''"
        :disabled="disabledStatus()"
        :loading="submitDoctorStatus"
        @click="submitDoctor"
        style="width: 217px;background: #337EFF;height: 48px;border-radius: 43px;color: #fff;text-align: center;line-height: 48px;margin: 48px auto">
        完成
      </van-button>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import editDoctorDialog from '../../../components/editDoctorDilog/editDoctorDialog'
import headPortrait from './headPortrait'
import { doctorDetails, createDoctor, updateDoctorInfo, deleteDoctor } from '@/api/doctorApi.js'
import speciality from './Speciality'
import introduce from './introduce'
import { queryProvince, queryCity, queryHospital } from '@/api/hospitalApi.js'
import '@vant/touch-emulator'
import {Col, Row, Icon, List, Cell, PullRefresh, Uploader, Popup, Field, Search, Button, Dialog, Toast, Sticky} from 'vant'
Vue.use(Uploader)
Vue.use(Sticky)
Vue.use(Popup)
Vue.use(Button)
Vue.use(Icon)
Vue.use(List)
Vue.use(Field)
Vue.use(Cell)
Vue.use(PullRefresh)
Vue.use(Col)
Vue.use(Row)
Vue.use(Search)
Vue.use(Toast)
Vue.use(Dialog)
export default {
  components: {
    editDoctorDialog, headPortrait, speciality, introduce
  },
  data () {
    return {
      doctor: {},
      nursingId: localStorage.getItem('caringNursingId'),
      doctorId: this.$route.query.doctorId,
      groupId: this.$route.query.groupId,
      backPathUrl: this.$route.query.pathUrl,
      specialitys: '', // 特长数据
      introduces: '', // 详细介绍数据
      // 控制医院弹窗显示隐藏
      show3: false,
      hospitalName: '',
      issousuo: 0,
      type: 1,
      provinceId: '',
      cityId: '',
      list: [],
      focus: true,
      loading: false,
      finished: false,
      refreshing: false,
      // 提交医生信息的状态
      submitDoctorStatus: false,
      params: {
        order: 'ascending',
        current: 1,
        model: {},
        size: 100
      }
    }
  },
  created () {
    if (this.doctorId) {
      this.getDoctorDetail()
    }
    this.pageProvince()
  },
  methods: {
    openNameDialog () {
      this.$refs.setDoctorName.setValue(this.doctor.name)
      this.$refs.setDoctorName.openDialog()
    },
    openDoctorPhone () {
      this.$refs.setDoctorPhone.setValue(this.doctor.mobile)
      this.$refs.setDoctorPhone.openDialog()
    },
    openDoctorDepartment () {
      this.$refs.setDoctorDepartment.setValue(this.doctor.deptartmentName)
      this.$refs.setDoctorDepartment.openDialog()
    },
    openDoctorTitle () {
      this.$refs.setDoctorTitle.setValue(this.doctor.title)
      this.$refs.setDoctorTitle.openDialog()
    },

    getDoctorDetail () {
      doctorDetails(this.doctorId).then(res => {
        console.log(res)
        if (res.data) {
          this.doctor = res.data
          if (this.doctor.extraInfo) {
            const extraInfo = JSON.parse(this.doctor.extraInfo)
            this.specialitys = extraInfo.Specialties
            this.introduces = extraInfo.Introduction
          }
        }
      })
    },
    /**
     * 创建医生
     */
    createDoctor () {
      createDoctor(this.doctor).then(res => {
        console.log('createDoctor res', res)
        this.submitDoctorStatus = false
        if (res.code === 0) {
          Toast({message: '添加成功', closeOnClick: true, position: 'bottom'})
          this.goback()
        }
      })
    },
    /**
     * 提交医生信息到后端
     */
    submitDoctor () {
      if (this.disabledStatus()) {
        return
      }
      this.submitDoctorStatus = true
      const extraInfo = {
        'Specialties': this.specialitys,
        'Introduction': this.introduces
      }
      this.doctor.extraInfo = JSON.stringify(extraInfo)
      console.log(this.doctor)
      if (this.groupId) {
        this.doctor.groupId = this.groupId
        this.doctor.independence = 0
      } else {
        this.doctor.independence = 1
      }
      this.doctor.nursingId = this.nursingId
      if (this.doctor.id) {
        this.updateDoctor()
      } else {
        this.createDoctor()
      }
    },
    /**
     * 上传完成
     */
    uploadCompleted (val) {
      console.log('上传完成', val)
      this.doctor.avatar = val
    },
    /**
     * 修改医生信息
     */
    updateDoctor () {
      updateDoctorInfo(this.doctor).then(res => {
        this.submitDoctorStatus = false
        if (res.code === 0) {
          Toast({message: '编辑成功', closeOnClick: true, position: 'bottom'})
          this.goback()
        }
      })
    },
    deleteDoctorFunc () {
      if (this.doctor.totalPatientCount && this.doctor.totalPatientCount > 0) {
        Toast({message: '该' + this.$getDictItem('doctor') + '已有' + this.$getDictItem('patient') + '，不可删除', closeOnClick: true})
      } else {
        Dialog.confirm({
          message: '是否删除该' + this.$getDictItem('doctor') + '?',
          confirmButtonColor: '#337EFF'
        })
          .then(() => {
            // on confirm
            deleteDoctor(this.doctorId).then(res => {
              if (res.code === 0) {
                Toast({message: '删除成功', closeOnClick: true, duration: 1500})
                // 小组id 和医生id 都在。说明之前是从小组详情进入医生详情
                if (this.doctorId && this.groupId) {
                  this.$router.replace({
                    path: '/mydoctor/groupDetails',
                    query: {
                      groupId: this.groupId
                    }
                  })
                } else {
                  this.$router.replace({
                    path: '/mydoctor'
                  })
                }
              }
            })
          })
          .catch(() => {
          })
      }
    },
    /**
     * 提交按钮的禁用
     */
    disabledStatus () {
      if (!this.doctor.name) {
        return true
      }
      if (!this.doctor.mobile) {
        return true
      } else if (this.doctor.mobile.length !== 11) {
        return true
      }
      return false
    },
    /**
     * 跳转到专业特长
     */
    setSpeciality (val) {
      this.specialitys = val
    },
    setIntroduce (val) {
      this.introduces = val
    },
    // 跳转到专业特长
    goSpeciality () {
      this.$refs.specialitys.setMessage(this.specialitys)
    },
    /**
     * 跳转到详细介绍
     */
    goIntroduce () {
      this.$refs.introduces.setMessage(this.introduces)
    },
    /**
     * 设置名字
     */
    setDocName (val) {
      this.$set(this.doctor, 'name', val)
      console.log(this.doctor.name)
    },

    /**
     *设置手机号
     */
    setDocPhone (val) {
      console.log(val)
      this.$set(this.doctor, 'mobile', val)
    },

    /**
     * 设置科室
     */
    setDocDepartment (val) {
      console.log(val)
      this.$set(this.doctor, 'deptartmentName', val)
    },

    /**
     * 设置职称
     * @param val
     */
    setDocTitle (val) {
      console.log(val)
      this.$set(this.doctor, 'title', val)
    },
    /**
     * 医院组件中的选择省市区
     * @param count
     */
    chooseOptions (count) {
      if (count === 1 && this.type !== 1) {
        this.list = []
        this.type = 1
        this.pageProvince()
      }
      if (count === 2 && this.type !== 2 && this.provinceId) {
        this.list = []
        this.type = 2
        this.params.current = 1
        this.pageCity()
      }
      if (count === 3 && this.type !== 3 && this.cityId) {
        this.list = []
        this.type = 3
        this.params.current = 1
        this.hospitalPage()
      }
    },
    /**
     * 分页查询省
     */
    pageProvince () {
      // this.params.model = {};
      this.params.current = 1
      queryProvince(this.params).then(res => {
        if (res.code === 0) {
          if (this.params.current === 1) {
            this.list = res.data.records
          } else {
            this.list.push(...res.data.records)
          }
          this.loading = false
          if (res.data.records && res.data.records.length < 100) {
            this.finished = true
          }
          console.log(this.list)
          this.params.current++
        }
      })
    },
    /**
     * 分页查询市
     */
    pageCity () {
      this.params.model.provinceId = this.provinceId
      queryCity(this.params).then(res => {
        if (res.data && res.code === 0) {
          if (res.data && res.code === 0) {
            if (this.params.current === 1) {
              this.list = res.data.records
            } else {
              this.list.push(...res.data.records)
            }
            this.params.current++
            this.loading = false
            if (res.data.records && res.data.records.length < 100) {
              this.finished = true
            }
          }
        }
      })
    },
    /**
     * 点省选项
     * @param val
     */
    chooseOption (val) {
      this.finished = false
      if (this.type === 1) {
        // this.params.model.provinceId = val.id
        this.provinceId = val.id
        this.params.current = 1
        this.type = 2
        this.list = []
        this.finished = false
        this.pageCity()
      } else if (this.type === 2) {
        this.cityId = val.id
        this.params.current = 1
        this.type = 3
        this.list = []
        this.finished = false

        this.hospitalPage()
      } else {
        this.chooseHospitalOption2(val)
      }
    },
    /**
     * 选择医院后。设置医院信息到doctor中
     */
    chooseHospitalOption2 (val) {
      console.log('选择的医院', val)
      this.doctor.hospitalId = val.id
      this.doctor.hospitalName = val.hospitalName
      this.hide()
    },
    /**
     * 查医院
     */
    hospitalPage () {
      this.params.model = {
        cityId: this.cityId,
        hospitalName: this.hospitalName,
        provinceId: this.provinceId
      }
      queryHospital(this.params).then(res => {
        if (res.data && res.code === 0) {
          if (res.data && res.code === 0) {
            if (this.params.current === 1) {
              this.list = res.data.records
            } else {
              this.list.push(...res.data.records)
            }
            this.params.current++
            this.loading = false
            if (res.data.records && res.data.records.length < 100) {
              this.finished = true
            }
          }
        }
      })
    },
    goback () {
      // 返回到医生详情页面
      if (this.doctorId) {
        this.$router.replace({
          path: this.backPathUrl,
          query: {
            doctorId: this.doctorId,
            groupId: this.groupId
          }
        })
        return
      }
      // 返回到小组的详情页面
      if (this.groupId) {
        console.log('后退的页面', this.backPathUrl)
        this.$router.replace({
          path: this.backPathUrl,
          query: {
            groupId: this.groupId
          }
        })
      } else {
        // 返回到我的医生页面
        this.$router.replace({
          path: '/mydoctor'
        })
      }
    },
    // 医院弹窗返回上一页面按钮
    hide () {
      // this.$emit("hideList", false);
      // this.list=[]
      this.hospitalName = ''
      this.list = []
      this.type = 1
      this.cityId = ''
      this.provinceId = ''
      this.pageProvince()
      this.show3 = false
    },
    onInput (val) {
      if (val !== '') {
        this.list = []
        this.issousuo = 2
      }
      if (val === '') {
        this.type = this.beforeSearchType
        this.refreshing = true
        this.finished = false
        this.onLoad()
      }
    },
    // 渲染数据
    getItem (item) {
      if (this.type === 1) {
        return item.province
      } else if (this.type === 2) {
        return item.city
      } else {
        return item.hospitalName
      }
    },
    onBlur () {
      this.focus = true
      document.body.style.overflow = 'visible'
    },
    onFocus () {
      this.focus = false
      document.body.style.overflow = 'hidden'
    },
    onSearch () {
      if (this.issousuo === 2) {
        this.type = 4
        this.refreshing = true
        this.finished = false
        // this.onLoad()
        this.issousuo = 1
      } else {
        this.issousuo = 3
      }
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true
      this.onLoad()
    },
    onLoad () {
      setTimeout(() => {
        if (this.refreshing) {
          this.list = []
          this.params.current = 1
          this.refreshing = false
        }
        if (this.type === 1) {
          this.pageProvince()
        } else if (this.type === 2) {
          this.pageCity()
        } else if (this.type === 3 || this.type === 4) {
          this.hospitalPage()
        }
      }, 500)
    }

  }
}
</script>

<style scoped lang="less">

.list-left{
  color: #666666;
}
.box{
  background: #fff;
  height: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 17px 13px;
  border-bottom: 1px solid #EEEEEE;
}
/deep/.van-nav-bar__text{
  color: white !important;
}
.hosp-pop{
  border-radius: 0 !important;
}
.showData {
  width: 100%;
  background-color: #fff;
}
.options {
  padding-left: 13px;
  border-bottom: 1px solid #ececec;
  min-height: 48px;
  line-height: 48px;
  color: #666666;
  font-size: 16px;
  font-family: Source Han Sans CN;
}
/deep/.van-search{
  background: #f0f0f0;
}
/deep/.van-search__content{
  background: #fff;
  height: 46px;
  border-radius: 4px;
  padding: 0;
}
/deep/.van-cell{
  padding: 0 !important;
  line-height: 46px;
  margin: 0 !important;
  padding-left: 13px !important;
}
i{
  font-size: 15px;
  color: #000;
  margin-top: 0 !important;
}
/deep/.van-cell__value{
  padding: 0 !important;
}
/deep/.van-icon-clear{
  margin-right: 10px !important;
}
.headTitle{
  line-height: 50px;
}
.box2::after {
  color: #ee0a24;
  font-size: 14px;
  content: '*';
}
.list-right{
  display: flex;align-items: center;justify-content: right
}
.left-box{
  color: #333;width: 200px;text-align: right;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;
}
</style>
