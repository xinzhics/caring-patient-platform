<template>
  <div style="min-height: 100vh;background: #F5F5F5;position: relative">
    <headNavigation leftIcon="arrow-left" :rightIcon="clearImg" :myIcon="true" @showpop="showClearExpirationDilog" title="待审核" @onBack="onBack"></headNavigation>
    <van-search
        v-model="params.model.patientName"
        show-action
        shape="round"
        background="#ffffff"
        placeholder="请输入"
        @search="onSearch"
        :clearable="false"
    >
      <template #action>
        <div @click="onSearch">搜索</div>
      </template>
    </van-search>
    <van-list
        v-model="loading"
        :finished="finished"
        @load="onLoad"
    >
      <div v-if="showNoDataImg" style="width: 152px;position: absolute;top: 50%;left: 50%;transform: translate(-50%,-50%)">
        <img width="152px" height="152px" :src="noDataImg" alt="">
        <div style="margin-top: 7px;color: #999999;text-align: center;font-size: 15px">暂无待审核记录</div>
      </div>
      <div v-for="(item,index) in dataList" :key="index" class="list" v-else>
        <img width="65px" height="65px" class="reviewed-icon"
             :src="item.status===-2?reviewedImg:item.status===4?expiredImg:''" alt="">
        <!--        expired.png-->
        <div class="patient-info">
          <div style="margin-right: 10px;width: 43px;height: 43px;border-radius: 50%"  @click="toPatientDetail(item.patientId)">
            <img style="border-radius: 50%" width="43px" height="43px" :src="item.avatar" alt="">
          </div>
          <div  @click="toPatientDetail(item.patientId)">
            <div class="patient-name">
              {{ item.patientName }}
            </div>
            <div class="patient-sex-birthday">
              {{ item.sex === 0 ? '男' : '女' }} {{
                (item.sex == 0 || item.sex == 1) && jsGetAge(item.birthday) ? '|' : ''
              }} {{ jsGetAge(item.birthday) ? jsGetAge(item.birthday) + '岁' : '' }}
            </div>
          </div>
        </div>
        <div class="appointment-time">
          <div>预约时间: {{ getTime(item.appointDate) }} {{ item.time === 1 ? '上午' : '下午' }}</div>
          <div @click="jumpChat(item)"><img v-show="item.patientDoctorId === item.doctorId && item.status !== 4"
                                            width="26px" height="26px"
                                            src="@/assets/my/chat.png" alt=""></div>
        </div>
        <div v-if="item.appointmentSort===1" class="audit">
          <div style="color: #337EFF;font-size: 13px">
            <span style="color: #999999">该时段剩余可预约：</span>
            {{ item.remainingNumber }} 人
          </div>
          <div style="display: flex;align-items: center">
            <van-button round size="small" @click="refuseAppointment(item.id,index)" style="margin-right: 8px"
                        type="default">
              拒绝
            </van-button>
            <van-button round size="small" @click="doctorApprove(item,index)" type="info">通过</van-button>
          </div>
        </div>
      </div>
    </van-list>
    <!--    清空过期弹窗-->
    <van-dialog v-model="clearExpiration" :show-confirm-button="false" class="dialog-demo" hide-on-blur
    >
      <div style="margin:0px;line-height:30px;text-align:center;padding-top:20px">
        <img :src="warn" alt="" width="60px">
      </div>
      <div style="margin-bottom:10px">
        <p style="margin:0 0 20px;line-height:26px;text-align:center;color:#666">确定清除全部已过期预约信息？</p>
      </div>
      <div style="border-top:1px solid #EBEBEB;display: flex;justify-content: space-between;">
        <div style="padding:8px 0px;line-height:30px;text-align:center;color:#333;width:50%;border-right:1px solid #EBEBEB ;"
           @click="clearExpiration=false">取消</div>
        <div style="padding:8px 0px;line-height:30px;text-align:center;color:#333333;width:50%" @click="clearAppoint()">
          确定</div>
      </div>
    </van-dialog>
    <!--    拒绝弹窗-->
    <van-dialog v-model="refuseDilog" :show-confirm-button="false" class="dialog-demo" hide-on-blur
    >
      <div
          style="margin:0px 0 26px 0;line-height:30px;text-align:center;padding-top:26px;position: relative;font-size: 19px">
        <div>请选择拒绝理由</div>
        <van-icon @click="refuseDilog=false" color="#B8B8B8" name="cross"
                  style="position: absolute;right: 17px;top: 31px"/>
      </div>
      <div @click="doctorApproveParams.auditFailReason='ABOUT_FULL'"
           :class="doctorApproveParams.auditFailReason==='ABOUT_FULL'?'choose':''" class="choose-cause">
        <div>1.该时段已约满</div>
        <img width="20px" :src="doctorApproveParams.auditFailReason==='ABOUT_FULL'?checkbox_check:checkbox_round"
             alt="">
      </div>
      <div @click="doctorApproveParams.auditFailReason='OTHER'"
           :class="doctorApproveParams.auditFailReason!=='ABOUT_FULL'?'choose':''" class="choose-cause">
        <div>2.其他</div>
        <img width="20px" :src="doctorApproveParams.auditFailReason==='ABOUT_FULL'?checkbox_round:checkbox_check"
             alt="">
      </div>
      <div v-if="doctorApproveParams.auditFailReason==='OTHER'">
        <van-field
            :class="doctorApproveParams.auditFailReasonDesc===''?'errAuditFailReasonDesc':''"
            class="auditFailReasonDesc"
            v-model="doctorApproveParams.auditFailReasonDesc"
            rows="2"
            autosize
            type="textarea"
            maxlength="200"
            placeholder="请填写"
            show-word-limit
        />
      </div>

      <div
          :class="doctorApproveParams.auditFailReason==='OTHER'&&doctorApproveParams.auditFailReasonDesc===''?'errBtn':''"
          class="btn"
          @click="refuse()">
        确定
      </div>
    </van-dialog>
    <!--    号源已满弹窗-->
    <van-dialog v-model="isFull" :show-confirm-button="false" class="dialog-demo" hide-on-blur
    >
      <div style="margin:0px;line-height:30px;text-align:center;padding-top:20px">
        <img :src="warn" alt="" width="60px">
      </div>
      <div style="margin-bottom:10px">
        <div style="margin:0 0 5px;line-height:26px;text-align:center;color:#666">该时段已约满，是否增加1个号源
        </div>
        <div style="margin:0 0 20px;line-height:26px;text-align:center;color:#666">并通过审核</div>
      </div>
      <div style="border-top:1px solid #EBEBEB;display: flex;justify-content: space-between;">
        <div style="padding:8px 0px;line-height:30px;text-align:center;color:#333;width:50%;border-right:1px solid #EBEBEB ;"
           @click="isFull=false">取消</div>
        <div style="padding:8px 0px;line-height:30px;text-align:center;color:#333333;width:50%" @click="addSource()">
          确定</div>
      </div>
    </van-dialog>
  </div>
</template>
<script>
import {approval, directApproval, clearAppoint} from '@/api/doctorApi.js'
import Vue from 'vue'
import {Button, Field, List, Dialog, Search, Toast, Lazyload} from 'vant'
import moment from 'moment'
import {nursingApprove} from '@/api/doctorApi'
Vue.use(Field)
Vue.use(Lazyload, {
  lazyComponent: true
})
Vue.use(Search)
Vue.use(Toast)
Vue.use(List)
Vue.use(Button)
Vue.use(Dialog)
export default {
  name: 'reviewedList',
  data () {
    return {
      showNoDataImg: false,
      clearExpiration: false, // 控制清除已过期弹窗
      refuseDilog: false,
      warn: require('@/assets/my/warn.png'),
      loading: false,
      finished: false,
      clearImg: require('@/assets/my/clear.png'),
      checkbox_check: require('@/assets/my/checkbox_check.png'), // 选择
      checkbox_round: require('@/assets/my/checkbox_round.png'), // 没选
      noDataImg: require('@/assets/my/no_data.png'),
      reviewedImg: require('@/assets/my/reviewed-leftIcon.png'), // 待审核
      expiredImg: require('@/assets/my/expired.png'), // 已过期
      params: {
        current: 1,
        model: {
          nursingId: localStorage.getItem('caringNursingId'),
          doctorId: '',
          patientName: ''
        },
        order: 'descending',
        size: 10,
        sort: 'id'
      },
      dataList: [],
      doctorApproveParams: {
        appointmentId: '', // 预约记录ID
        status: 3, // 预约状态 通过：0 拒绝 3
        auditFailReason: 'ABOUT_FULL', // ABOUT_FULL: 该时段已约满， OTHER: 其他
        auditFailReasonDesc: '' // 审核拒绝原因说明
      },
      refuseIndex: undefined,
      doctorApproveStatus: false,
      addSourceStatus: false,
      refuseStatus: false,
      isFull: false,
      appointmentId: ''
    }
  },
  created () {
    this.onLoad()
  },
  methods: {
    onBack () {
      this.$router.replace('/reservation')
    },
    onSearch () {
      this.params.current = 1
      this.onLoad()
    },
    // 跳转im聊天
    jumpChat (item) {
      const history = {path: '/reservation/reviewedList', query: {}}
      localStorage.setItem('againShowH5IframePage', JSON.stringify(history))
      this.$jumpPatientChat(item.patientId)
    },
    // 增加号源
    addSource () {
      if (this.addSourceStatus) {
        return
      }
      this.addSourceStatus = true
      directApproval(this.appointmentId).then(res => {
        this.addSourceStatus = false
        console.log(res)
        if (res.code === 0) {
          this.isFull = false
          Toast('通过成功')
          this.$reservationApproval()
          this.dataList.splice(this.refuseIndex, 1)
        }
      }).catch(res => {
        this.addSourceStatus = false
      })
    },
    // 跳转去患者个人中心
    toPatientDetail (patientId) {
      this.$router.push({
        path: '/patient/center',
        query: {
          patientId: patientId,
          backUrl: '/reservation/reviewedList'
        }
      })
    },
    // 拒绝确定
    refuse () {
      if (this.doctorApproveParams.auditFailReason === 'OTHER' && this.doctorApproveParams.auditFailReasonDesc === '') {
        Toast('请输入拒绝原因')
      } else {
        if (this.refuseStatus) {
          return
        }
        this.refuseStatus = true
        console.log(this.doctorApproveParams)
        nursingApprove(this.doctorApproveParams).then(res => {
          console.log(res)
          this.refuseStatus = false
          if (res.code === 0) {
            if (this.dataList.length === 1) {
              this.dataList.splice(this.refuseIndex, 1)
              this.onSearch()
            } else {
              this.dataList.splice(this.refuseIndex, 1)
            }
            this.refuseDilog = false
            Toast('拒绝成功')
            this.$reservationApproval()
            this.doctorApproveParams = {
              appointmentId: '', // 预约记录ID
              status: 3, // 预约状态 通过：0 拒绝 3
              auditFailReason: 'ABOUT_FULL', // ABOUT_FULL: 该时段已约满， OTHER: 其他
              auditFailReasonDesc: '' // 审核拒绝原因说明
            }
          }
        }).catch(res => {
          this.refuseStatus = false
        })
      }
    },
    // 拒绝弹窗
    refuseAppointment (id, index) {
      this.doctorApproveParams.appointmentId = id
      this.refuseDilog = true
      this.refuseIndex = index
    },
    /**
     * 通过审核
     * @param id 预约id
     */
    doctorApprove (item, index) {
      this.refuseIndex = index
      if (item.remainingNumber === 0) {
        this.isFull = true
        this.appointmentId = item.id
      } else {
        const params = {
          appointmentId: item.id,
          status: 0
        }
        if (this.doctorApproveStatus) {
          return
        }
        this.doctorApproveStatus = true
        nursingApprove(params).then(res => {
          this.doctorApproveStatus = false
          if (res.code === 0) {
            this.params.current = 1
            Toast('通过成功')
            this.$reservationApproval()
            if (this.dataList.length === 1) {
              this.dataList.splice(this.refuseIndex, 1)
              this.onSearch()
            } else {
              this.dataList.splice(this.refuseIndex, 1)
            }
          } else {
            console.log('失败!!!!!!!!!!!!!!!!!!!!')
            this.appointmentId = item.id
            this.isFull = true
          }
        }).catch(res => {
          this.doctorApproveStatus = false
        })
      }
    },
    /**
     * 确定清除已过期
     */
    clearAppoint () {
      clearAppoint('', localStorage.getItem('caringNursingId')).then(res => {
        console.log(res)
        // 接口请求成功后循环删除数组里的已过期item
        if (res.code === 0) {
          for (let i = this.dataList.length - 1; i >= 0; i--) {
            if (this.dataList[i].status === 4) {
              this.dataList.splice(i, 1)
            }
          }
          Toast('清除成功')
          this.clearExpiration = false
        }
      })
      console.log(this.dataList)
    },
    // 点击左上角清除按钮
    showClearExpirationDilog () {
      this.clearExpiration = true
    },
    getTime (time) {
      return moment(time).format('yyyy年MM月DD日')
    },
    onLoad () {
      if (this.loading) {
        return
      }
      this.loading = true
      this.getApproval()
    },
    getApproval () {
      if (this.$route.query.doctorId) {
        this.params.model.doctorId = this.$route.query.doctorId
      }
      approval(this.params).then(res => {
        this.loading = false
        if (res.code === 0) {
          if (this.params.current >= res.data.pages) {
            this.finished = true
          } else {
            this.params.current++
          }
          if (this.params.current === 1) {
            this.dataList = []
            this.dataList = res.data.records
          } else {
            this.dataList.push(...res.data.records)
          }
          if (this.dataList.length === 0) {
            this.showNoDataImg = true
          } else {
            this.showNoDataImg = false
          }
        }
      }).catch(res => {
        this.loading = false
      })
    },
    jsGetAge (strBirthday) {
      let returnAge = ''
      let strBirthdayArr = ''
      if (strBirthday) {
        if (strBirthday.indexOf('-') !== -1) {
          strBirthdayArr = strBirthday.split('-')
        } else if (strBirthday.indexOf('/') !== -1) {
          strBirthdayArr = strBirthday.split('/')
        }
        let birthYear = strBirthdayArr[0]
        let birthMonth = strBirthdayArr[1]
        let birthDay = strBirthdayArr[2]

        let d = new Date()
        let nowYear = d.getFullYear()
        let nowMonth = d.getMonth() + 1
        let nowDay = d.getDate()

        if (nowYear === birthYear) {
          returnAge = 0 // 同年 则为0岁
        } else {
          let ageDiff = nowYear - birthYear // 年之差
          if (ageDiff > 0) {
            if (nowMonth === birthMonth) {
              let dayDiff = nowDay - birthDay // 日之差
              if (dayDiff < 0) {
                returnAge = ageDiff - 1
              } else {
                returnAge = ageDiff
              }
            } else {
              let monthDiff = nowMonth - birthMonth // 月之差
              if (monthDiff < 0) {
                returnAge = ageDiff - 1
              } else {
                returnAge = ageDiff
              }
            }
          } else {
            returnAge = -1 // 返回-1 表示出生日期输入错误 晚于今天
          }
        }
      } else {
        returnAge = ''
      }
      // 如果结果为 -1 或者为 0， 则不显示年龄
      if (returnAge === -1 || returnAge === 0) {
        returnAge = ''
      }
      return returnAge // 返回周岁年龄
    }
  }
}
</script>

<style scoped lang="less">
.list {
  margin: 13px 13px 0;
  background: #FFFFFF;
  border-radius: 4px;
  padding: 13px;
  position: relative;

  .reviewed-icon {
    position: absolute;
    right: 0;
    top: 0;
  }

  .patient-info {
    display: flex;
    align-items: center;
    border-bottom: 1px solid #EEEEEE;
    padding-bottom: 13px;
    margin-bottom: 22px;

    .patient-name {
      color: #333333;
    }

    .patient-sex-birthday {
      font-size: 13px;
      color: #999999;
    }
  }

  .appointment-time {
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: #666666;
    font-size: 15px;
    margin-bottom: 10px;
  }

  /deep/ .audit {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .van-button {
      width: 65px;
      height: 26px;
    }

    .van-button--default {
      color: #999999;
    }
  }
}

/deep/ .dialog-demo {
  border-radius: 9px !important;
}

.choose-cause {
  margin: 0px 17px 7px;
  padding: 16px 13px;
  background: #F5F5F5;
  border-radius: 3px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  color: #666666;
}

.choose {
  color: #3F86FF;
}

/deep/ .auditFailReasonDesc {
  //border: ;
  margin: 0px 17px 7px;
  padding: 16px 13px 9px;
  width: auto;
  border: 1px solid #f5f5f5;
  background: #f5f5f5;
  border-radius: 3px;

  .van-field__word-num {
    color: #337EFF;
  }
}

/deep/ .errAuditFailReasonDesc {
  border: 1px solid #FF5555 !important;
}

.btn {
  line-height: 47px;
  text-align: center;
  color: #FFFFFF;
  width: 58%;
  height: 47px;
  background: #3F86FF !important;
  border-radius: 43px;
  margin: 26px auto;
  border: none !important;
}

.errBtn {
  background: #DDDDDD !important;
}

</style>
