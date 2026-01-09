<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :title="pageTitle ? pageTitle : '健康档案'"
                      @onBack="back"></headNavigation>
    </van-sticky>
    <div style="padding: 0 15px;background: #F7F7F7 ;overflow:auto!important; ">
      <div class="title" v-if="disease.length>0">疾病信息</div>
      <div class="content" v-if="disease.length>0">
        <div v-for="(item,index) in disease" :key="index" class="list"
             :style="{'border-bottom': index === disease.length-1?'none':'1px solid #F5F5F5'}">
          <div>{{ item.label }}：</div>
          <div class="value" v-if="item.widgetType!=='MultiImageUpload'&&item.widgetType!=='Avatar'">
            {{ getValues(item) }}
          </div>
          <!--            多选切有备注-->
          <div v-if="(item.widgetType==='CheckBox'||item.widgetType==='Radio') && item.values&&item.values.length>0"
               style="width: 100%">
            <div v-for="(m,n) in item.values" v-if="m.desc||item.otherValue" :key="n"
                 :style="{'border-bottom':n!==item.values.length-1?' 1px solid #f5f5f5':''}">
              <div style="margin-top: 13px;font-weight: 600">{{ m.valueText }}</div>
              <div v-if="m.desc||item.otherValue" class="textarea">
                {{ m.valueText !== '其他' ? m.desc : item.otherValue }}
              </div>
              <div class="textarea" v-else style="color: #999999">请输入内容</div>
              <!--                出发新题-->
              <div v-if="m.questions&&m.questions.length>0" v-for="(i,k) in m.questions"
                   :style="{'border-bottom':k===m.questions.length-1?'none':'','padding-bottom':k===m.questions.length-1?'0':'13px'}"
                   :key="k" class="list">
                <div>{{ i.label }}：</div>
                <div class="value" v-if="i.widgetType!=='MultiImageUpload'">{{ getValues(i) }}</div>
                <div
                  v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar') && item.values&&item.values.length>0"
                  :style="{'margin-top':item.values.length>=3?'13px':'',width:item.values.length>=3?'100%':''}">
                  <van-row gutter="20">
                    <van-col span="8" v-for="(m,n) in item.values" :key="n">
                      <img class="image" width="94px" height="71px"
                           :style="{'border-radius': '8px','margin-left': '7px','margin-bottom':item.values.length>=3?'7px':''}"
                           :src="m.attrValue" alt="">
                    </van-col>
                  </van-row>
                </div>
                <div class="value"
                     v-if="(i.widgetType==='MultiImageUpload'||item.widgetType==='Avatar') && i.values.length===0">
                  请输入内容
                </div>
              </div>
            </div>
          </div>
          <div
            v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar')&&item.values&&item.values.length>0"
            :style="{'margin-top':item.values.length>=2?'13px':'',width:item.values.length>=2?'100%':''}">
            <van-row gutter="20">
              <van-col span="8" v-for="(m,n) in item.values" :key="n">
                <img class="image" width="94px" height="71px"
                     :style="{'border-radius': '8px','margin-left': '7px','margin-bottom':item.values.length>=2?'7px':''}"
                     :src="m.attrValue?m.attrValue:url" alt="">
              </van-col>
            </van-row>
          </div>

          <div class="value"
               v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar')&&item.values.length===0">
            请输入内容
          </div>
        </div>
      </div>
      <div class="title" v-if="diagnostic.length>0">诊断类型</div>
      <div class="content" v-if="diagnostic.length>0">
        <div v-for="(item,index) in diagnostic" :key="index" class="list"
             :style="{'border-bottom': index === diagnostic.length-1?'none':'1px solid #F5F5F5'}">
          <div>{{ item.label }}：</div>
          <div class="value" v-if="item.widgetType!=='MultiImageUpload'&&item.widgetType!=='Avatar'">
            {{ getValues(item) }}
          </div>
          <!--            多选切有备注-->
          <div v-if="(item.widgetType==='CheckBox'||item.widgetType==='Radio')&&item.values&&item.values.length>0"
               style="width: 100%">
            <div v-for="(m,n) in item.values" :key="n" v-if="m.desc||item.otherValue"
                 :style="{'border-bottom':n!==item.values.length-1?' 1px solid #f5f5f5':''}">
              <div style="margin-top: 13px;font-weight: 600">{{ m.valueText }}</div>
              <div class="textarea">{{ m.valueText !== '其他' ? m.desc : item.otherValue }}</div>
              <!--                出发新题-->
              <div v-if="m.questions&&m.questions.length>0" v-for="(i,k) in m.questions"
                   :style="{'border-bottom':k===m.questions.length-1?'none':'','padding-bottom':k===m.questions.length-1?'0':'13px'}"
                   :key="k" class="list">
                <div>{{ i.label }}：</div>
                <div class="value" v-if="i.widgetType!=='MultiImageUpload'">{{ getValues(i) }}</div>
                <div
                  v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar')&&item.values&&item.values.length>0"
                  :style="{'margin-top':item.values.length>=3?'13px':'',width:item.values.length>=3?'100%':''}">
                  <van-row gutter="20">
                    <van-col span="8" v-for="(m,n) in item.values" :key="n">
                      <img class="image" width="94px" height="71px"
                           :style="{'border-radius': '8px','margin-left': '7px','margin-bottom':item.values.length>=3?'7px':''}"
                           :src="m.attrValue?m.attrValue:url" alt="">
                    </van-col>
                  </van-row>
                </div>
                <div class="value"
                     v-if="(i.widgetType==='MultiImageUpload'||item.widgetType==='Avatar')&&i.values.length===0">
                  请输入内容
                </div>
              </div>
            </div>
          </div>
          <div
            v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar') && item.values&&item.values.length>0"
            :style="{'margin-top':item.values.length>=2?'13px':'',width:item.values.length>=2?'100%':''}">
            <van-row gutter="20">
              <van-col span="8" v-for="(m,n) in item.values" :key="n">
                <img class="image" width="94px" height="71px"
                     :style="{'border-radius': '8px','margin-left': '7px','margin-bottom':item.values.length>=2?'7px':''}"
                     :src="m.attrValue?m.attrValue:url" alt="">
              </van-col>
            </van-row>
          </div>

          <div class="value"
               v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar') && item.values.length===0">
            请输入内容
          </div>
        </div>
      </div>
      <div class="content" style="margin-top: 19px" v-if="noTypeList.length>0">
        <div v-for="(item,index) in noTypeList" :key="index" class="list"
             :style="{'border-bottom': index === noTypeList.length-1?'none':'1px solid #F5F5F5'}">
          <div>{{ item.label }}：</div>
          <div class="value" v-if="item.widgetType!=='MultiImageUpload'&&item.widgetType!=='Avatar'">
            {{ getValues(item) }}
          </div>
          <!--            多选切有备注-->
          <div v-if="(item.widgetType==='CheckBox'||item.widgetType==='Radio') && item.values&&item.values.length>0"
               style="width: 100%">
            <div v-for="(m,n) in item.values" :key="n" v-if="m.desc||item.otherValue"
                 :style="{'border-bottom':n!==item.values.length-1?' 1px solid #f5f5f5':''}">
              <div style="margin-top: 13px;font-weight: 600">{{ m.valueText }}</div>
              <div class="textarea">{{ m.valueText !== '其他' ? m.desc : item.otherValue }}</div>
              <!--                出发新题-->
              <div v-if="m.questions&&m.questions.length>0" v-for="(i,k) in m.questions"
                   :style="{'border-bottom':k===m.questions.length-1?'none':'','padding-bottom':k===m.questions.length-1?'0':'13px'}"
                   :key="k" class="list">
                <div>{{ i.label }}：</div>
                <div class="value" v-if="i.widgetType!=='MultiImageUpload'">{{ getValues(i) }}</div>
                <div
                  v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar') && item.values&&item.values.length>0"
                  :style="{'margin-top':item.values.length>=3?'13px':'',width:item.values.length>=3?'100%':''}">
                  <van-row gutter="20">
                    <van-col span="8" v-for="(m,n) in item.values" :key="n">
                      <img class="image" width="94px" height="71px"
                           :style="{'border-radius': '8px','margin-left': '7px','margin-bottom':item.values.length>=3?'7px':''}"
                           :src="m.attrValue?m.attrValue:url" alt="">
                    </van-col>
                  </van-row>
                </div>
                <div class="value"
                     v-if="(i.widgetType==='MultiImageUpload'||item.widgetType==='Avatar')&&i.values.length===0">
                  请输入内容
                </div>
              </div>
            </div>
          </div>
          <div
            v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar') && item.values&&item.values.length>0"
            :style="{'margin-top':item.values.length>=3?'13px':'',width:item.values.length>=3?'100%':''}">
            <van-row gutter="20">
              <van-col span="8" v-for="(m,n) in item.values" :key="n">
                <img class="image" width="94px" height="71px"
                     :style="{'border-radius': '8px','margin-left': '7px','margin-bottom':item.values.length>=3?'7px':''}"
                     :src="m.attrValue?m.attrValue:url" alt="">
              </van-col>
            </van-row>
          </div>

          <div class="value"
               v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar') && item.values.length===0">
            请输入内容
          </div>
        </div>
      </div>
      <div
        style="height: 47px;text-align: center;background: #67E0A7;color: #fff;margin: 19px 0 13px 0;line-height: 47px;border-radius: 24px"
        @click="goEditor">编辑
      </div>
    </div>
  </div>
</template>
<script>
import {getCheckDataformResult} from '@/api/Content.js'
import attrPage from '@/components/arrt/editorIndex'
import Vue from 'vue'
import {Row, Col, Icon, Toast, Sticky} from 'vant'

Vue.use(Toast)
Vue.use(Sticky)
Vue.use(Row)
Vue.use(Col)
Vue.use(Icon)
export default {
  components: {attrPage},
  data () {
    return {
      pageTitle: localStorage.getItem('pageTitle'),
      historyImg: require('@/assets/patient/set.png'),
      fields: [],
      allArray: [],
      allInfo: {},
      formId: '',
      diseaseInformationStatus: 0,
      showhis: false,
      submitStatus: false,
      disease: [], // 疾病信息
      diagnostic: [], // 诊断类型
      noTypeList: [],
      formResultId: '',
      url: 'https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/head-portrait-patient.png'
    }
  },
  mounted () {
    this.formResultId = this.$route.query.formId
    this.getInfo()
  },
  methods: {
    goEditor () {
      this.$router.push({
        path: '/health/editor',
        query: {
          formId: this.$route.query.formId,
          isHealth: 0
        }
      })
    },
    toHistory () {
      this.$router.push({
        path: `/patient/baseinfo/index/history`,
        query: {
          id: this.formResultId
        }
      })
    },
    getInfo () {
      getCheckDataformResult({id: this.formResultId}).then((res) => {
        if (res.code === 0) {
          this.allInfo = res.data
          let arr = JSON.parse(res.data.jsonContent)
          this.fields = []
          this.fields.push(...arr)
          this.formId = res.data.id
          console.log('this.fields-------', this.fields)
          this.fields.forEach(item => {
            if (item.widgetType !== 'Desc' && item.widgetType !== 'SplitLine' && item.widgetType !== 'Page') {
              if (item.healthFieldShowDisplayArea === 'diagnostic') {
                this.diagnostic.push(item)
              } else if (item.healthFieldShowDisplayArea === 'disease') {
                this.disease.push(item)
              } else {
                this.noTypeList.push(item)
              }
            }
          })
        }
      })
    },
    back () {
      this.$router.replace({
        path: '/patient/center'
      })
    },
    getValues (item) {
      const value = item.values
      if (value && value.length > 0) {
        if (item.widgetType === 'SingleLineText' & item.exactType === 'CourseOfDisease' || item.exactType === 'BMI') {
          if (value[0].attrValue === '-' || !value[0].attrValue) {
            return '请输入内容'
          }
          if (value[0].attrValue && value[0].attrValue !== '-') {
            return value[0].attrValue
          }
        }
        if (item.widgetType === 'SingleLineText' & item.exactType !== 'hospital' | item.widgetType === 'MultiLineText' | item.widgetType === 'Number' || item.widgetType === 'Time' |
          item.widgetType === 'Date' || item.widgetType === 'FullName') { // 单行 多行 数字 日期 姓名
          const attrValue = value[0].attrValue ? value[0].attrValue : '请输入内容'
          // console.log(item.widgetType ==='SingleLineText','attrValue=================',attrValue)
          console.log(item.exactType === 'Weight', attrValue)
          if (attrValue !== '请输入内容' && item.rightUnit) {
            return attrValue + item.rightUnit
          } else if (!item.rightUnit) {
            return attrValue
          } else {
            return attrValue
          }
        }
        if (item.widgetType === 'Radio' || item.widgetType === 'DropdownSelect' | item.widgetType === 'SingleLineText' & item.exactType === 'hospital') { // 单选 下拉
          if (value[0].valueText) {
            return value[0].valueText ? value[0].valueText : '请输入内容'
          } else {
            return '请输入内容'
          }
        }
        if (item.widgetType === 'CheckBox') { // 多选
          let str = ''
          value.forEach((item, index) => {
            if (index === 0) {
              str = item.valueText ? item.valueText : '请输入内容'
            } else {
              str += '、' + item.valueText
            }
          })
          return str
        }
        if (item.widgetType === 'Address') {
          console.log('我是地址', value[0].attrValue && value[0].attrValue.length > 0, value)
          if (value[0].attrValue && value[0].attrValue.length > 0) {
            let str = ''
            value[0].attrValue.forEach(item => {
              str += item
            })
            // 如果有备注
            if (item.value) {
              str += item.value
            }
            console.log('走地址了么')
            return str
          } else {
            return '请输入内容'
          }
        }
      } else {
        return '请输入内容'
      }
    }
  }
}
</script>

<style lang="less" scoped>
.title{
  font-weight: 600;
  font-size: 15px;
  margin: 19px 0 13px 0;
}
.textarea{
  border:1px solid #999999;
  border-radius: 8px;
  padding: 11px 9px;
  font-weight: 600;
  min-height: 89px;
  background: #F7F7F7;
  margin: 13px 0;
}
.content{
  background: #FFFFFF;
  border-radius: 9px;
  padding: 0px 15px 0px 15px;
  font-size: 13px;
  .list{
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    padding: 13px 0;
    border-bottom: 1px solid #F5F5F5;
    .value{
      font-size: 13px;
      font-weight: 600;
    }
    .DropdownSelect{
      width: 100%;
      background: #F7F7F7;
      border: 1px solid #999999;
      display: flex;
      justify-content: space-between;
      padding: 11px 9px;
      margin-top: 13px;
      border-radius: 8px;
      align-items: center;
    }
  }
}
</style>
