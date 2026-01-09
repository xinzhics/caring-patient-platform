<template>
  <section>
    <navBar :pageTitle=" pageTitle !== undefined ? pageTitle:'疾病信息'"/>
    <div style="padding: 0 15px;background: #F7F7F7 ;overflow:auto!important; ">
      <div class="title" v-if="disease.length>0">疾病信息</div>
      <div class="content" v-if="disease.length>0">
        <div v-for="(item,index) in disease" :key="index" class="list"
             :style="{'border-bottom': index === disease.length-1?'none':'1px solid #F5F5F5'}">
          <div>{{ item.label }}：</div>
          <div class="value" v-if="item.widgetType!=='MultiImageUpload'&&item.widgetType!=='Avatar'">
            {{ getValues(item) }}
          </div>
          <!--            选择题 且有备注-->
          <div v-if="item.widgetType==='CheckBox'||item.widgetType==='Radio'||item.widgetType==='DropdownSelect'"
               style="width: 100%">
            <div v-for="(m,n) in item.values" :key="n" v-if="showValuesDetail(item)"
                 :style="{'border-bottom':n!==item.values.length-1?' 1px solid #f5f5f5':''}">
              <div style="margin-top: 13px;font-weight: 600">{{ m.valueText }}</div>
              <div v-if="item.itemAfterHasEnter === 1 && m.valueText!=='其他' && m.desc" class="textarea">{{ m.desc }}
              </div>
              <div v-else-if="m.valueText==='其他' && item.otherValue" class="textarea">{{ item.otherValue }}</div>
              <div class="textarea" v-else-if="item.itemAfterHasEnter === 1 || m.valueText === '其他'"
                   style="color: #999999">请输入内容
              </div>
              <!--                出发新题-->
              <div v-if="m.questions&&m.questions.length>0" v-for="(i,k) in m.questions"
                   :style="{'border-bottom':k===m.questions.length-1?'none':'','padding-bottom':k===m.questions.length-1?'0':'13px'}"
                   :key="k" class="list">
                <div>{{ i.label }}：</div>
                <div class="value" v-if="i.widgetType!=='MultiImageUpload'">{{ getValues(i) }}</div>
                <div
                  v-if="item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar'&&item.values&&item.values.length>0"
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
                     v-if="i.widgetType==='MultiImageUpload'||item.widgetType==='Avatar'&&i.values.length===0">
                  请输入内容
                </div>
              </div>
            </div>
          </div>
          <div
            v-if="item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar'&&item.values&&item.values.length>0"
            :style="{'margin-top': item.values.length>=2 ? '13px' : '' ,width:item.values.length>=2?'100%':''}">
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
          <!--            选择题且有备注-->
          <div v-if="item.widgetType==='CheckBox'||item.widgetType==='Radio'||item.widgetType==='DropdownSelect'"
               style="width: 100%">
            <div v-for="(m,n) in item.values" :key="n" v-if="showValuesDetail(item)"
                 :style="{'border-bottom':n!==item.values.length-1?' 1px solid #f5f5f5':''}">
              <div style="margin-top: 13px;font-weight: 600">{{ m.valueText }}</div>
              <div v-if="item.itemAfterHasEnter === 1 && m.valueText!=='其他' && m.desc" class="textarea">{{ m.desc }}
              </div>
              <div v-else-if="m.valueText==='其他' && item.otherValue" class="textarea">{{ item.otherValue }}</div>
              <div class="textarea" v-else-if="item.itemAfterHasEnter === 1 || m.valueText === '其他'"
                   style="color: #999999">请输入内容
              </div>
              <!--                出发新题-->
              <div v-if="m.questions&&m.questions.length>0" v-for="(i,k) in m.questions"
                   :style="{'border-bottom':k===m.questions.length-1?'none':'','padding-bottom':k===m.questions.length-1?'0':'13px'}"
                   :key="k" class="list">
                <div>{{ i.label }}：</div>
                <div class="value" v-if="i.widgetType!=='MultiImageUpload'">{{ getValues(i) }}</div>
                <div
                  v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar') && item.values && item.values.length>0"
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
            v-if="(item.widgetType === 'MultiImageUpload' || item.widgetType==='Avatar') && item.values && item.values.length>0"
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
      <div class="content" style="margin-top: 19px" v-if="noTypeList.length>0">
        <div v-for="(item,index) in noTypeList" :key="index" class="list"
             :style="{'border-bottom': index === noTypeList.length-1?'none':'1px solid #F5F5F5'}">
          <div>{{ item.label }}：</div>
          <div class="value" v-if="item.widgetType!=='MultiImageUpload'&&item.widgetType!=='Avatar'">
            {{ getValues(item) }}
          </div>
          <!--            选择题且有备注-->
          <div v-if="item.widgetType==='CheckBox'||item.widgetType==='Radio'||item.widgetType==='DropdownSelect'"
               style="width: 100%">
            <div v-for="(m,n) in item.values" :key="n" v-if="showValuesDetail(item)"
                 :style="{'border-bottom':n!==item.values.length-1?' 1px solid #f5f5f5':''}">
              <div style="margin-top: 13px;font-weight: 600">{{ m.valueText }}</div>
              <div v-if="item.itemAfterHasEnter === 1 && m.valueText!=='其他' && m.desc" class="textarea">{{ m.desc }}
              </div>
              <div v-else-if="m.valueText==='其他' && item.otherValue" class="textarea">{{ item.otherValue }}</div>
              <div class="textarea" v-else-if="item.itemAfterHasEnter === 1 || m.valueText === '其他'"
                   style="color: #999999">请输入内容
              </div>
              <!--                出发新题-->
              <div v-if="m.questions&&m.questions.length>0" v-for="(i,k) in m.questions"
                   :style="{'border-bottom':k===m.questions.length-1?'none':'','padding-bottom':k===m.questions.length-1?'0':'13px'}"
                   :key="k" class="list">
                <div>{{ i.label }}：</div>
                <div class="value" v-if="i.widgetType!=='MultiImageUpload'">{{ getValues(i) }}</div>
                <div
                  v-if="item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar'&&item.values&&item.values.length>0"
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
            v-if="item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar'&&item.values&&item.values.length>0"
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
               v-if="item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar'&&item.values.length===0">
            请输入内容
          </div>
        </div>
      </div>
      <div v-show="editButtonShow"
           style="height: 47px;text-align: center;background: #67E0A7;color: #fff;margin: 19px 0 13px 0;line-height: 47px;border-radius: 24px"
           @click="goEditor">编辑
      </div>
    </div>
  </section>
</template>
<script>
import Vue from 'vue'
import Api from '@/api/Content.js'
import {Sticky, Icon, Col, Row} from 'vant'

Vue.use(Sticky)
Vue.use(Col)
Vue.use(Row)
Vue.use(Icon)
export default {
  components: {
    attrPage: () => import('@/components/arrt/editorIndex'),
    navBar: () => import('@/components/headers/navBar')
  },
  data() {
    return {
      pageTitle: '',
      historyImg: require('@/images/set.png'),
      editorImg: require('@/assets/my/editor.png'),
      fields: [],
      allArray: [],
      allInfo: {},
      formId: "",
      diseaseInformationStatus: 0,
      showhis: false,
      submitStatus: false,
      disease: [], // 疾病信息
      diagnostic: [], // 诊断类型
      noTypeList: [],
      editButtonShow: false,  // 延迟加载 编辑按钮
      url: 'https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/head-portrait-patient.png'
    }
  },
  created() {
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 100)
  },
  mounted() {
    this.getInfo()
    document.body.style.overflow = "visible"
    const diseaseInformationStatus = localStorage.getItem('diseaseInformationStatus')
    if (diseaseInformationStatus !== undefined) {
      this.diseaseInformationStatus = Number(diseaseInformationStatus)
    }
    if (this.diseaseInformationStatus === 0) {
      this.showhis = false
      return
    }
    this.showhis = this.$getShowHist() === 'true'

  },
  methods: {
    goEditor() {
      this.$router.push({
        path: '/health/editor',
        query: {
          formId: this.$route.query.formId,
          isHealth: 1,
          onback: this.$route.query.onback
        }
      })
    },
    // 判断是否显示选项下的详情
    showValuesDetail(item) {
      let show = false;
      const values = item.values
      if (values) {
        if (item.itemAfterHasEnter === 1) {
          // 要求所有选项有输入框。 显示详情
          return true;
        }
        for (let i = 0; i < values; i++) {
          const questions = values.questions
          // 答案下有子题目。 显示详情
          if (questions && questions.length > 0) {
            show = true;
            break
          } else if (values[i].valueText === '其他') {
            // 选择的答案中有其他
            show = true;
            break
          }
        }
      }
      return show;
    },
    getValues(item) {
      const value = item.values
      if (value && value.length > 0) {
        if (item.widgetType === 'SingleLineText' && item.exactType === 'CourseOfDisease' || item.exactType === 'BMI') {
          if (value[0].attrValue === '-' || !value[0].attrValue) {
            return '请输入内容'
          }
          if (value[0].attrValue && value[0].attrValue !== '-') {
            return value[0].attrValue
          }
        }
        if (item.widgetType === 'SingleLineText' && item.exactType !== 'hospital' || item.widgetType === 'MultiLineText' || item.widgetType === 'Number' || item.widgetType === 'Time'
          || item.widgetType === 'Date' || item.widgetType === 'FullName') { // 单行 多行 数字 日期 姓名
          const attrValue = value[0].attrValue ? value[0].attrValue : '请输入内容'
          if (attrValue !== '请输入内容' && item.rightUnit) {
            return attrValue + item.rightUnit
          } else if (!item.rightUnit) {
            return attrValue
          } else {
            return attrValue
          }
        }
        if (item.widgetType === 'Radio' || item.widgetType === 'DropdownSelect' || item.widgetType === 'SingleLineText' && item.exactType === 'hospital') { // 单选 下拉
          if (value[0].valueText) {
            return value[0].valueText ? value[0].valueText === '其他' && item.otherLabelRemark ? item.otherLabelRemark : value[0].valueText : '请输入内容'
          } else {
            return '请输入内容'
          }

        }
        if (item.widgetType === 'CheckBox') { // 多选
          let str = ''
          value.forEach((val, index) => {
            if (index === 0) {
              str = val.valueText ? val.valueText === '其他' && item.otherLabelRemark ? item.otherLabelRemark : val.valueText : '请输入内容'
            } else {
              str += '、' + (val.valueText === '其他' && item.otherLabelRemark ? item.otherLabelRemark : val.valueText)
            }
          })
          return str

        }
        if (item.widgetType === 'Address') {
          if (value[0].attrValue && value[0].attrValue.length > 0) {
            let str = ''
            value[0].attrValue.forEach(item => {
              str += item
            })
            // 如果有备注
            if (item.value) {
              str += item.value
            }
            return str
          } else {
            return '请输入内容'
          }

        }

      } else {
        return '请输入内容'
      }
    },
    getInfo() {
      Api.formResult(this.$route.query.formId).then((res) => {
        if (res.data.code === 0) {
          this.allInfo = res.data.data
          this.fields = []
          this.fields.push(...JSON.parse(res.data.data.jsonContent))
          this.formId = res.data.data.id
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
          this.$nextTick(() => {
            this.editButtonShow = true
          })
        }
      })
    },
    submit(k) {
      if (k.MarkTip === 'has') {
        if (k.content) {
          this.allArray = this.allArray.concat(k.content)
        }
        this.allArray.push(k.page)
        this.$refs.attrPage.nextPage();
      } else {
        if (this.submitStatus) {
          return
        }
        this.submitStatus = true
        if (k.content) {
          this.allArray = this.allArray.concat(k.content)
        }
        const params = {
          "businessId": this.allInfo.businessId,
          "category": this.allInfo.category,
          "formId": this.allInfo.formId,
          "jsonContent": JSON.stringify(this.allArray),
          "messageId": this.allInfo.messageId,
          "name": this.allInfo.name,
          "userId": this.allInfo.userId,
          fillInIndex: -1
        }
        if (this.allInfo.id) {
          params.id = this.allInfo.id
        }
        if (this.diseaseInformationStatus === 0) {
          Api.saveFormResultStage(params).then((res) => {
            this.submitStatus = false
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功!', 'center')
              this.$router.replace({
                path: '/home'
              })

            }
          })
        } else {
          if (this.allInfo.id) {
            // 修改的时候走这里。才会产生表单历史记录
            Api.gethealthformSubPut(params).then((res) => {
              this.submitStatus = false
              if (res.data.code === 0) {
                this.$vux.toast.text('提交成功', 'center')
                this.$router.replace('/home')
              }
            })
          } else {
            Api.saveFormResultStage(params).then((res) => {
              this.submitStatus = false
              if (res.data.code === 0) {
                this.$vux.toast.text('提交成功!', 'center')
                this.$router.replace({
                  path: '/home'
                })
              }
            })
          }
        }
      }
    }
  }
}
</script>


<style lang="less" scoped>
/deep/ .vux-header {
  height: 50px;
  position: relative;
}

.title {
  font-weight: 600;
  font-size: 15px;
  margin: 19px 0 13px 0;
}

.textarea {
  border: 1px solid #999999;
  border-radius: 8px;
  padding: 11px 9px;
  font-weight: 600;
  min-height: 89px;
  background: #F7F7F7;
  margin: 13px 0;
}

.content {
  background: #FFFFFF;
  border-radius: 9px;
  padding: 0px 15px 0px 15px;
  font-size: 13px;

  .list {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    padding: 13px 0;
    border-bottom: 1px solid #F5F5F5;

    .value {
      font-size: 13px;
      font-weight: 600;
    }

    .DropdownSelect {
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
