<template>
  <div class="main">
    <navBar :pageTitle="'血糖监测'" :backUrl="backUrl"></navBar>
    <div>
      <van-cell-group>
        <van-field label="时间段" v-model="sugarTypes[formData.type]" right-icon="arrow" readonly
                   @click="typePickerShowClick" input-align="right"/>
        <van-field required label-width="120px" label="血糖值" placeholder="请输入血糖值" v-model="formData.sugarValue"
                   input-align="right" @input="sugarValueInput">
          <template slot="right-icon">
            <span style="display: flex"> <span style="color: #3D444D"> mmol/L</span></span>
          </template>
        </van-field>
        <van-field label="记录时间" :value="formatDateTime(currentDate)" right-icon="arrow" type="number" readonly
                   @click="dataTimeShowClick" input-align="right"/>
        <van-field label="备注" v-model="formData.remarks" placeholder="请输入备注"
                   input-align="right" maxlength="100" show-word-limit/>
      </van-cell-group>
      <van-popup v-model="typePickerShow" position="bottom" :style="{ height: '30%' }">
        <van-picker
            title="选择时间段"
            :default-index="formData.type"
            show-toolbar
            :columns="sugarTypes"
            @confirm="typeOnConfirm"
            @cancel="typeOnCancel"
        />
      </van-popup>
      <van-popup v-model="dataTimeShow" position="bottom" :style="{ height: '30%' }">
        <van-datetime-picker
            v-model="currentDate"
            type="datetime"
            title="选择记录时间"
            @confirm="dateTimeOnConfirm"
            @cancel="dataTimeOnCancel"
            :max-date="maxDate"
        />
      </van-popup>

      <div style=" position: absolute; bottom: 20px; width: 100%;">
        <div v-if="systemMsgId" style="width: 100%">
          <van-row style=" width: 100%; display: flex; align-items: center; margin: 19px 0 13px 0; justify-content: center">
            <div class="caring-form-button" style="height: 47px; background: #fff; color: #66E0A7; width: 47%; margin-right: 12px; margin-left: 20px; border-radius: 30px"  @click="submitForm">
              <span>提交</span>
            </div>

            <div class="caring-form-button" style="height: 47px; background: #66E0A7; color: #FFFFFF; width: 47%; margin-left: 12px; margin-right: 20px; border-radius: 30px" @click="toDo">
              <span>评论</span>
            </div>
          </van-row>
        </div>
        <van-col v-else span='24'
                 style='text-align: center; width: 100%; height: 75px; padding: 10px 25px 0 25px'>
          <van-col span='24' style="padding-bottom: 20px">
            <van-button :loading="submitStatus" loading-type="spinner" loading-text="正在提交"
                        style="border-radius: 42px;width: 100%;background: #67E0A7 !important;border: 1px solid #67E0A7 !important"
                        class='attr-button btn' type='primary' @click="submitForm">提交
            </van-button>
          </van-col>
        </van-col>
      </div>
    </div>
    <dialog-comment ref="commentRef" @submit="submitComment"></dialog-comment>
  </div>
</template>

<script>
import Api from '@/api/Content.js'
import doctorApi from '@/api/doctor.js'
import Vue from 'vue';
import {Field, CellGroup, Popup, DatetimePicker, Button, Picker, Toast} from 'vant';
import dialogComment from "@/components/systemMessage/systemCommentDialog"

Vue.use(Field);
Vue.use(Button);
Vue.use(Picker);
Vue.use(Popup);
Vue.use(Toast);
Vue.use(CellGroup);
Vue.use(DatetimePicker);
export default {
  components: {
    dialogComment,
    navBar: () => import('@/components/headers/navBar')
  },
  data() {
    return {
      bloodimg: require('@/assets/my/xuetang.png'),
      sugarTypes: [
        '凌晨血糖', '空腹血糖', '早餐后2小时血糖', '午餐前血糖', '午餐后2小时血糖', '晚餐前血糖', '晚餐后2小时血糖', '睡前血糖', '随机血糖'
      ],
      pageTitle: '',
      formData: {type: 1},
      typePickerShow: false,
      currentDate: new Date(),
      dataTimeShow: false,
      maxDate: new Date(),
      id: this.$route.query.id,
      backUrl: '/monitor/glucose',
      imMessageId: undefined,
      submitStatus: false,
      systemMsgId: '',
      isLoading: false,
    }
  },
  mounted() {
    if (this.$route.query && this.$route.query.systemMsgId) {
      this.systemMsgId = this.$route.query.systemMsgId
    }
    if (this.$route.query && this.$route.query.imMessageId) {
      this.backUrl = undefined
      this.imMessageId = this.$route.query.imMessageId
    }
    if (this.imMessageId) {
      this.getGlucoseMessageId()
    }else if (this.id) {
      this.getSugar()
    } else {
      this.currentDate = new Date()
    }
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    // 提交评论
    submitComment(val) {
      if (this.isLoading) {
        return
      }
      this.isLoading = true;
      let patientId = localStorage.getItem('patientId')
      doctorApi.doctorCommentMessage({
        commentContent: val,
        doctorId: this.$route.query.doctorId,
        doctorName: this.$route.query.doctorName,
        messageId: this.$route.query.systemMsgId,
        patientId: patientId,
      })
        .then(res => {
          this.$refs.commentRef.close()
          this.$toast('评论成功')
          this.$router.go(-1)
        })
    },
    toDo() {
      this.$refs.commentRef.open()
    },
    // 血糖只能输入一位小数
    sugarValueInput(e) {
      if (e && e.match) {
        e = e.match(/^\d*(\.?\d{0,1})/g)[0] || null
        this.$nextTick(() => {
          this.formData.sugarValue = e;
        });
      }
    },
    getGlucoseMessageId() {
      Api.getGlucoseMessageId(this.imMessageId).then(res => {
        if (res.data.data) {
          this.formData = res.data.data
          if (this.formData.createDay) {
            this.currentDate = new Date(this.formData.createDay * 1000)
          } else {
            this.currentDate = new Date()
          }
        }
      })
    },
    getSugar() {
      Api.getSugar(this.id).then(res => {
        console.log(res.data)
        this.formData = res.data.data
        if (this.formData.createDay) {
          this.currentDate = new Date(this.formData.createDay * 1000)
        } else {
          this.currentDate = new Date()
        }
      })
    },

    submitForm() {
      const params = {
        delFlag: 0,
        id: this.formData.id,
        remarks: this.formData.remarks,
        sugarValue: this.formData.sugarValue,
        type: this.formData.type,
        patientId: localStorage.getItem('patientId'),
        createDay: this.formData.createDay,
        time: this.formData.time,
      }
      if (!this.formData.sugarValue) {
        Toast('血糖值不能为空！');
        return;
      }
      if (this.submitStatus) {
        return
      }
      this.submitStatus = true
      if (this.formData.id) {
        if (this.imMessageId) {
          params.messageId = this.imMessageId
          params.imRecommendReceipt = 1
        }
        params.id = this.formData.id
        Api.PutsugarFormResult(params).then((res) => {
          this.submitStatus = false
          if (res.data.code === 0) {
            if (this.imMessageId) {
              this.$router.go(-1)
            } else {
              this.$router.replace('/monitor/glucose')
            }
          }
        }).catch(error => {
          this.submitStatus = false
        })
      } else {
        if (this.imMessageId) {
          params.messageId = this.imMessageId
          params.imRecommendReceipt = 1
        }
        if (this.$route.query.messageId) {
          params.messageId = this.$route.query.messageId
        }
        if (this.$route.query.planDetailTimeId) {
          params.planDetailTimeId = this.$route.query.planDetailTimeId
        }
        Api.sugarFormResult(params).then((res) => {
          this.submitStatus = false
          if (res.data.code === 0) {
            if (this.imMessageId) {
              this.$router.go(-1)
            } else {
              this.$router.replace('/monitor/glucose')
            }
          }
        }).catch(error => {
          this.submitStatus = false
        })
      }
    },

    typePickerShowClick() {
      console.log('typePickerShowClick')
      this.typePickerShow = true
    },

    dataTimeShowClick() {
      console.log('dataTimeShowClick')
      this.dataTimeShow = true
    },

    dateTimeOnConfirm(val) {
      console.log('dateTimeOnConfirm', val, this.currentDate)
      this.dataTimeShow = false
    },

    dataTimeOnCancel() {
      this.dataTimeShow = false
    },

    typeOnConfirm(val, index) {
      console.log('typeOnConfirm', val, index)
      this.formData.type = index
      this.typePickerShow = false
    },

    typeOnCancel() {
      this.typePickerShow = false
    },

    timestampToTime(timestamp) {
      let date
      if (timestamp) {
        date = new Date(timestamp * 1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
      } else {
        date = new Date();//时间戳为10位需*1000，时间戳为13位的话不需乘1000
      }
      let Y = date.getFullYear() + '/';
      let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '/';
      let D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' ';
      let h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
      let m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
      let s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
      return Y + M + D + h + m + s;
    },
    formatDateTime() {
      let date = this.currentDate;//时间戳为10位需*1000，时间戳为13位的话不需乘1000
      let Y = date.getFullYear() + '/';
      let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '/';
      let D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' ';
      let h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
      let m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes());
      this.formData.time = h + m
      this.formData.createDay = this.currentDate.getTime() / 1000
      return Y + M + D + h + m;
    },
  }
}
</script>
<style lang="less" scoped>


/deep/ .vux-header {
  height: 50px;
}

</style>

