<template>
  <section>
    <navBar :pageTitle=" pageTitle !== undefined ? pageTitle:'疾病信息'"/>
    <!--    <attrPage v-if="fields&&fields.length>=1" :all-fields="fields" ref="attrPage" :submit="submit"></attrPage>-->
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
          <div v-if="item.widgetType==='CheckBox'||item.widgetType==='Radio'&&item.values&&item.values.length>0"
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
          <div v-if="(item.widgetType==='CheckBox'||item.widgetType==='Radio')&& item.values && item.values.length>0"
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
            v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar') && item.values&&item.values.length>0"
            :style="{'margin-top':item.values.length>=2?'13px':'',width:item.values.length>=2?'100%':''}">
            <van-row gutter="20">
              <van-col span="8" v-for="(m,n) in item.values" :key="n">
                <img class="image" width="94px" height="71px"
                     :style="{'border-radius': '8px','margin-left': '7px','margin-bottom':item.values.length>=2?'7px':''}"
                     :src="m.attrValue ? m.attrValue:url" alt="">
              </van-col>
            </van-row>
          </div>

          <div class="value"
               v-if="(item.widgetType==='MultiImageUpload'||item.widgetType==='Avatar')&&item.values.length===0">
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
          <div v-if="item.widgetType==='CheckBox'||item.widgetType==='Radio'&&item.values&&item.values.length>0"
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

      <div v-if="messageId">
        <van-row
          style=" width: 100%; display: flex; align-items: center; margin: 19px 0 13px 0; justify-content: center">
          <div class="caring-form-button"
               style="height: 47px; background: #fff; color: #66E0A7; width: 47%; margin-right: 12px;; border-radius: 30px"
               @click="goEditor">
            <span>编辑</span>
          </div>

          <div class="caring-form-button"
               style="height: 47px; background: #66E0A7; color: #FFFFFF; width: 47%; margin-left: 12px; border-radius: 30px"
               @click="toDo">
            <span>评论</span>
          </div>
        </van-row>
      </div>

      <div v-else
           style="height: 47px;text-align: center;background: #67E0A7;color: #fff;margin: 19px 0 13px 0;line-height: 47px;border-radius: 24px"
           @click="goEditor">编辑
      </div>
    </div>
    <dialog-comment ref="commentRef" @submit="submitComment"></dialog-comment>
  </section>
</template>
<script>
import Vue from 'vue'
import Api from '@/api/Content.js'
import {Sticky, Icon, Col, Row} from 'vant'
import doctorApi from "@/api/doctor"
import dialogComment from "@/components/systemMessage/systemCommentDialog"

Vue.use(Sticky)
Vue.use(Col)
Vue.use(Row)
Vue.use(Icon)
export default {
  components: {
    dialogComment,
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
      url: 'https://caring.obs.cn-north-4.myhuaweicloud.com/wxIcon/head-portrait-patient.png',
      messageId: '',
      isLoading: false,
    }
  },
  mounted() {
    if (this.$route.query && this.$route.query.messageId) {
      this.messageId = this.$route.query.messageId
    }
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
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
    console.log('window.document.documentElement.scrollTop', window.document.documentElement.scrollTop)
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
        messageId: this.$route.query.messageId,
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
    goEditor() {
      this.$router.push({
        path: '/health/editor',
        query: {
          formId: this.$route.query.formId,
          isHealth: 0,
        }
      })
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
        if (item.widgetType === 'Radio' || item.widgetType === 'DropdownSelect' || item.widgetType === 'SingleLineText' && item.exactType === 'hospital') { // 单选 下拉
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
    },
    getInfo() {
      Api.getCheckDataformResult({id: this.$route.query.formId}).then((res) => {
        if (res.data.code === 0) {
          this.allInfo = res.data.data
          this.fields = []
          this.fields.push(...JSON.parse(res.data.data.jsonContent))
          this.formId = res.data.data.id
          console.log('this.fields', this.fields)
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
          console.log(this.diagnostic, this.disease)
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
