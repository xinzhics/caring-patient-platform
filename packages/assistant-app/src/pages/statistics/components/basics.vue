<template>
<div>
  <div style="height: 13px;background: #F5F5F5"></div>
    <div style="padding: 13px;background: #FFFFFF">
      <div style="text-align: center;color: #FFFFFF;border-radius: 7px;background: linear-gradient(224deg, #5292FF 0%, #6EA8FF 100%);padding: 22px 0;display: flex;align-items: center;justify-content: space-around">
        <div>
<!--          患者总数-->
          <div style="font-size: 26px">{{ getPatientNumberData('total') }}</div>
          <div style="font-size: 13px">{{$getDictItem('patient')}}总数(人)</div>
        </div>
        <div style="width: 1px;height: 43px;background: #EEEEEE"></div>
        <div>
<!--          注册转化率-->
          <div  style="font-size: 26px">{{!Math.trunc(getPatientNumberData('registeredPatient')/getPatientNumberData('total')*100)?0:Math.trunc(getPatientNumberData('registeredPatient')/getPatientNumberData('total')*100)}}%</div>
          <div style="font-size: 13px">{{$getDictItem('registerrate')}}</div>
        </div>
      </div>
      <div style="margin-top: 13px;text-align: center;color: #999999;border-radius: 7px;background: #FAFAFA;padding: 22px 0;font-size: 13px">
        <van-row>
          <van-col span="5" offset="1">
            <img src="../../../assets/registeredPatients.png" style="width: 22px;margin-bottom: 8px" alt="">
            <div>{{$getDictItem('register')}}{{$getDictItem('patient')}}</div>
          </van-col>
          <van-col span="1" offset="1">
            <div style="width: 1px;background: #EEEEEE;height: 43px"></div>
          </van-col>
          <van-col span="8">
<!--            全部注册患者-->
            <div style="color: #337EFF;font-size: 20px;margin-bottom: 8px">{{ getPatientNumberData('registeredPatient') }}</div>
            <div>全部</div>
          </van-col>
          <van-col span="8">
<!--            注册患者本月新增-->
            <div style="color: #337EFF;font-size: 20px;margin-bottom: 8px">{{ getPatientNumberData('registeredPatientCurrentMonth') }}</div>
            <div>本月新增</div>
          </van-col>
        </van-row>
      </div>
      <div style="margin-top: 13px;text-align: center;color: #999999;border-radius: 7px;background: #FAFAFA;padding: 22px 0;font-size: 13px">
        <van-row>
          <van-col span="5" offset="1">
            <img src="../../../assets/noRegisteredPatients.png" style="width: 22px;margin-bottom: 8px" alt="">
            <div>{{$getDictItem('notregister')}}{{$getDictItem('patient')}}</div>
          </van-col>
          <van-col span="1" offset="1">
            <div style="width: 1px;background: #EEEEEE;height: 43px"></div>
          </van-col>
          <van-col span="8">
<!--            未注册患者-->
            <div style="color: #FF7F1C;font-size: 20px;margin-bottom: 8px">{{ getPatientNumberData('noRegisteredPatient') }}</div>
            <div>全部</div>
          </van-col>
          <van-col span="8">
<!--            未注册新增-->
            <div style="color: #FF7F1C;font-size: 20px;margin-bottom: 8px">{{ getPatientNumberData('noRegisteredPatientCurrentMonth') }}</div>
            <div>本月新增</div>
          </van-col>
        </van-row>
      </div>
      <div style="margin-top: 13px;text-align: center;color: #999999;border-radius: 7px;background: #FAFAFA;padding: 22px 0;font-size: 13px">
        <van-row>
          <van-col span="5" offset="1">
            <img src="../../../assets/cancelFollow.png" style="width: 22px;margin-bottom: 8px" alt="">
            <div>取消关注</div>
          </van-col>
          <van-col span="1" offset="1">
            <div style="width: 1px;background: #EEEEEE;height: 43px"></div>
          </van-col>
          <van-col span="8">
<!--            全部取消关注全部-->
            <div style="color: #FF5D5D;font-size: 20px;margin-bottom: 8px">{{ getPatientNumberData('noSubscribePatient') }}</div>
            <div>全部</div>
          </van-col>
          <van-col span="8">
<!--            本月取消关注-->
            <div style="color: #FF5D5D;font-size: 20px;margin-bottom: 8px">{{ getPatientNumberData('noSubscribePatientCurrentMonth') }}</div>
            <div>本月新增</div>
          </van-col>
        </van-row>
      </div>
    </div>
  <div style="height: 13px;background: #F5F5F5"></div>
  <div style="padding: 17px 14px;background: #FFFFFF">
    <div style="display: flex;align-items: center;padding-bottom: 18px;border-bottom: 1px solid #EEEEEE">
      <div style="width: 2px;height: 13px;background: #337EFF;border-radius: 4px;margin-right: 8px"></div>
      <div>男女比例</div>
    </div>
    <div style="padding: 26px 0">
      <div style="display: flex;justify-content: space-between;align-items: center">
        <div style="color: #666666">{{ leftWdith?leftWdith:0 }}%</div>
        <div :style="'width:'+leftWdith+'%'" class="box-left"></div>
        <div :style="'width:'+rightWidth+'%'" class="box-right"></div>
        <div style="color: #666666">{{ rightWidth?rightWidth:0 }}%</div>
      </div>
      <div style="display: flex;justify-content: center;align-items: center;margin-top: 21px">
        <div style="display:flex;align-items: center;margin-right: 65px">
          <div style="width: 13px;height: 13px;border-radius: 50%;background: #669EFF;margin-right: 9px"></div>
          <span>男性</span>
        </div>
        <div style="display:flex;align-items: center">
          <div style="width: 13px;height: 13px;border-radius: 50%;background: #FF7777;margin-right: 9px"></div>
          <span>女性</span>
        </div>
      </div>
    </div>
  </div>
  <div style="padding: 17px 14px;background: #FFFFFF;margin-top: 13px">
    <div style="display: flex;align-items: center;padding-bottom: 18px;border-bottom: 1px solid #EEEEEE">
      <div style="width: 2px;height: 13px;background: #337EFF;border-radius: 4px;margin-right: 8px"></div>
      <div>年龄分布</div>

    </div>
    <div>
      <agePie :agePieData="agePieData"></agePie>
    </div>
  </div>
  <van-dialog confirmButtonText="我知道了" confirmButtonColor="#669EFF" v-model="show" style="text-align: center;width: 282px;border-radius: 7px">
    <div style="width: 50px;margin: 20px auto">
      <img src="../../../assets/careful.png"  style="width: 55px" alt="">
    </div>
    <div>
      小提示: 当前数据为空，
    </div>
    <div style="margin:8px 0 20px 0 ">
      以下为演示数据 ！
    </div>
  </van-dialog>
</div>
</template>

<script>
import { getStatistics } from '@/api/statisticsBasics'
import Vue from 'vue'
import {Dialog} from 'vant'
import agePie from './agePie'
Vue.use(Dialog)
export default {
  name: 'basics',
  components: {
    agePie
  },
  data () {
    return {
      leftWdith: 0,
      rightWidth: 0,
      agePieData: [],
      nursingId: localStorage.getItem('caringNursingId'),
      allData: [], // 所有数据
      patientList: [], // 患者列表数据
      useJiashujv: 0,
      jiashujv: ['{"woman":150,"diagnosis":[{"key":"ff1b2adcf0a242c1873fa0bfef448441","name":"高血压","count":20},{"key":"336d45028d674949969f575403967491",' +
      '"name":"高血糖","count":80},{"key":"da0308f4e5ce426fb59e03c4bcacf4cd","name":"高血脂","count":12},{"key":"1c13dc94aa0542029cfdb639492d6a74","name":"冠心病",' +
      '"count":63},{"key":"dc31589910744063aae568f62bcd4d27","name":"心衰","count":95},{"key":"205ae6877def4ec3b8701e93be8c14bd","name":"其他","count":30}],"man":250,"' +
      'age":[{"key":"less18","name":"18岁以下","count":41},{"key":"more30less50","name":"31-50岁","count":55},{"key":"more18less25","name":"18-24岁","count":32},' +
      '{"key":"more25less30","name":"25-30岁","count":10},{"key":"more51","name":"51岁以上","count":44}],"personCount":[{"key":"total","name":"患者总数","count":400},' +
      '{"key":"registeredPatient","name":"注册患者","count":400},{"key":"registeredPatientCurrentMonth","name":"注册患者本月新增","count": 80},{"key":"noRegisteredPatient",' +
      '"name":"未完成注册","count":50},{"key":"noRegisteredPatientCurrentMonth","name":"未完成注册本月新增",' +
      '"count":30},{"key":"noSubscribePatient","name":"取关患者","count":15},{"key":"noSubscribePatientCurrentMonth","name":"取关本月新增","count":5}]}'],
      show: false
    }
  },
  created () {
    // this.jiashujv = JSON.parse(this.jiashujv)
    this.getStatisticsData(this.nursingId)
  },
  mounted () {

  },
  methods: {
    getManOrWoman () {
      if (this.allData.man !== undefined && this.allData.woman !== undefined) {
        if (this.allData.man + this.allData.woman === 0) {
          this.leftWdith = 0
          this.rightWidth = 0
        } else {
          this.leftWdith = (this.allData.man / (this.allData.man + this.allData.woman) * 100).toFixed(1)
          this.rightWidth = (this.allData.woman / (this.allData.man + this.allData.woman) * 100).toFixed(1)
        }
      }
    },
    /**
     * 获取基础数据
     * @param data 医助id
     */
    getStatisticsData (data) {
      getStatistics(data).then(res => {
        if (res.code === 0) {
          this.allData = res.data
          this.agePieData = res.data.age
          this.patientList = res.data.personCount
          res.data.personCount.forEach((item, index) => {
            if (item.key === 'total' && item.count === 0) {
              this.allData = this.jiashujv
              this.agePieData = this.jiashujv.age
              this.patientList = this.jiashujv.personCount
              this.show = true
            }
          })
          this.getManOrWoman()
        }
      })
    },
    /**
     * 获取注册未注册等患者数量
     * @param data 字段key
     * @returns {数量}
     */
    getPatientNumberData (data) {
      let patient = 0
      if (this.patientList) {
        this.patientList.forEach(item => {
          if (data === item.key) {
            patient = item.count
          }
        })
      }
      return patient
    }
  }
}
</script>

<style scoped>
.box-left{
  background: #669EFF;
  border-radius: 4px 0px 0px 4px;
  margin-left: 11px;
  height: 43px;
  margin-right: 1px;
}
.box-right{
  background: #FF7777;
  border-radius: 0px 4px 4px 0px;
  margin-right: 11px;
  height: 43px;
}
</style>
