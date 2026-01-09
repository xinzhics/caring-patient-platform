<template>
  <section class="allcontent">
    <div class="topContent">
      <x-header :left-options="{backText: '',showBack: Canparent,preventGoBack:true}" @on-click-back="backParent"
                style="margin-bottom:0px!important">{{ pageTitle }}
      </x-header>
    </div>
    <div class="content">
      <template v-for="(i,k) in userData">
        <div :key="k">
          <!-- 医院 -->
          <div v-if="i.widgetType === 'SingleLineText' && i.exactType === 'hospital'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div>
                <hospital :field='i'></hospital>
              </div>
            </div>
          </div>
          <!-- 头像 -->
          <div v-if="i.widgetType === 'Avatar' && i.exactType === 'Avatar'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div class="upAvatar">
                <headPortrait :field='i'/>
              </div>
            </div>
          </div>

          <div v-if="i.widgetType === 'FullName'||(i.widgetType === 'SingleLineText' && !i.exactType)">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div class="inputContent">
                <input type="text" :maxlength="i.maxEnterNumber?Number(i.maxEnterNumber):400"
                       :placeholder="i.placeholder" v-model="i.values[0].attrValue">
                <span>{{ i.rightUnit }}</span>
              </div>
            </div>
          </div>
          <div v-if="i.widgetType === 'Number'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>

            </div>
            <div class="item">
              <div class="inputContent">

                <input type="number" @blur="doBlur(i)" :placeholder="i.placeholder" v-model="inputVal">
                <span>{{ i.rightUnit }}</span>

              </div>
              <div v-if="max" class="max">超出最大值</div>
              <div v-if="min" class="max">超出最小值</div>
            </div>
          </div>
          <div v-if="i.widgetType === 'SingleLineText' && i.exactType === 'Mobile'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div class="inputContent">
                <input type="number" :placeholder="i.placeholder" v-model="inputVal">
                <span>{{ i.rightUnit }}</span>
              </div>
            </div>
          </div>
          <div v-if="i.widgetType === 'MultiLineText'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div class="textareaContent">
                <x-textarea class="inner" v-model="textareaVal" :max="i.maxEnterNumber" :autosize="true"
                            :placeholder="i.placeholder"></x-textarea>
              </div>
            </div>
          </div>
          <div
            v-if="(i.widgetType === 'Radio' && !i.exactType)||(i.widgetType === 'DropdownSelect')||
              (i.widgetType === 'MultiLevelDropdownSelect') ||(i.widgetType === 'Radio' && i.exactType === 'FollowUpStage')">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div class="radioItem">
                <div class="allselect" v-for="(z,y) in i.options" :key="y+'radio'">
                  <p :class="checkVal(i,z)?'active':''" @click="selectBtn(z,y+'radio')">
                    {{ z.attrValue }}

                  </p>
                  <div v-if="checkVal(i,z)&&z.attrValue!=='其他'&&i.itemAfterHasEnter===1" class="CheckBoxInput">
                    <input type="text" v-model="i.values[0].desc" @blur="radioboxSelectinput(i,z,y)"
                           :placeholder="i.itemAfterHasEnterRequired==1?'请输入 (必填)':'请输入'">
                  </div>
                </div>
              </div>
              <div class="othertextArea" v-if="hasother(i)">
                <x-textarea class="inner" v-model="i.otherValue" :max="i.otherValueSize" :autosize="true"
                            :placeholder="i.otherErrorMessage"></x-textarea>
              </div>
            </div>
          </div>
          <div v-if="i.widgetType === 'CheckBox'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div class="checkItem" v-for="(z,y) in i.options" :key="y+'radio'">

                <p :class="checkValT(i,z)?'active':''" @click="checkboxSelectbtn(i,z)">

                  {{ z.attrValue }}

                </p>
                <div v-if="checkValT(i,z)&&z.attrValue!=='其他' &&i.itemAfterHasEnter===1" class="CheckBoxInput">
                  <input type="text" v-model="z.valuesRemark" @blur="checkboxSelectinput(i,z,y)"
                         :placeholder="i.itemAfterHasEnterRequired==1?'请输入 (必填)':'请输入'">
                </div>

              </div>
              <div class="othertextArea" v-if="hasother(i)">
                <x-textarea class="inner" v-model="i.otherValue" :max="i.otherValueSize" :autosize="true"
                            :placeholder="i.otherErrorMessage"></x-textarea>
              </div>

            </div>
          </div>

          <div v-if="i.widgetType === 'Address'">

            <div class="title">
              <h2>{{ i.label }}</h2>
            </div>
            <van-popup :style="{ height: '40%' }" v-model="shows" position="bottom">
              <van-picker @cancel="shows=false" @confirm="updata" ref="addressPicker" type="address" show-toolbar
                          visible-item-count="7" value-key="name" :columns="addressData"/>
            </van-popup>
            <div class="item">
              <div class="inputContent" @click="shows=true" style="position: relative;">
                <p
                  style="color:#666!important;border:none;width:80%;display:inline-block;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;line-height:27px;vertical-align:bottom!important">
                  {{ showaddressVal ? showaddressVal : i.placeholder }}</p>
                <x-icon type="ios-arrow-forward" size="22"></x-icon>
              </div>
              <div v-if="i.hasAddressDetail=== 1">
                <div style="width:75vw;height:10px;border-bottom:1px solid #E5E5E5;margin:60px auto"></div>
                <div class="title">
                  <h2>{{ i.labelDesc }}</h2>
                </div>
                <div>
                  <x-textarea style="width:80%;margin:0px auto;border:1px solid #E5E5E5;border-radius:8px;"
                              v-model="i.value" :autosize="true" placeholder="请输入..."></x-textarea>
                </div>
              </div>

            </div>
          </div>
          <div v-if="i.widgetType === 'SingleLineText' && i.exactType === 'Height'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item" key="height111">
              <div class="SingleLinecontent">
                <div class="SingleLineItem" ref="scrollerEvent">
                  <datetime-view v-model="heightVal" hour-row="{value}cm" :hour-list="heightData" ref="datetime1"
                                 format="HH"></datetime-view>
                </div>
              </div>
            </div>
          </div>
          <div v-if="i.widgetType === 'SingleLineText' && i.exactType === 'Weight'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item" key="Weight111">
              <div class="SingleLinecontent">
                <div class="SingleLineItem" ref="scrollerEventT">
                  <datetime-view v-model="weightVal" hour-row="{value}kg" :hour-list="weightData" ref="datetime2"
                                 format="HH"></datetime-view>
                </div>
              </div>
            </div>
          </div>
          <div v-if="i.widgetType === 'Radio' && i.exactType === 'Gender'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div class="genderinner">
                <div :class="genderIndexs===0?'man':''" @click="selectedGender(i,0)">
                  <img :src="genderIndexs===0?manImg:manImgT" alt="" align="middle">
                </div>
                <div :class="genderIndexs===1?'weman':''" @click="selectedGender(i,1)">
                  <img :src="genderIndexs===1?wemanImg:wemanImgT" alt="" align="middle">
                </div>
              </div>
              <div class="genderlabel">

                <div v-for="(z,index) in i.options" :key="index">{{ z.attrValue }}</div>
              </div>
            </div>
          </div>
          <div v-if="i.widgetType === 'Date' " :key="k + 'z'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div class="birthdayInner">
                <datetime-view :key="datematterFix(i)" v-model="birthdayVal" :default-selected-value="datematterFix(i)"
                               :start-date="i.defineChooseDate&&i.defineChooseDate===1 ?
                                    (i.defineChooseDateType === 2 ? turnDate() : '1900/01/01') : (i.minValue ? i.minValue.replace(/-/g, '/') : '1900/01/01')"
                               :end-date="i.defineChooseDate&&i.defineChooseDate===1 ? (i.defineChooseDateType === 1 ? turnDate():'2121/01/01') : '2121/01/01'"
                               year-row="{value}年" month-row="{value}月" day-row="{value}日" ref="datetime"
                               format="YYYY-MM-DD"></datetime-view>
              </div>
            </div>
          </div>
          <div v-if="i.widgetType === 'Time'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div class="birthdayInner">
                <datetime-view v-model="timeVal" default-selected-value="12/00" hour-row="{value}时"
                               minute-row="{value}分" ref="datetime" format="HH:mm"></datetime-view>
              </div>
            </div>
          </div>
          <div v-if="i.widgetType === 'MultiImageUpload'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item" style="padding-bottom:20px">
              <div class="uploadImg">
                <upLoaderimg :filesList="i.values" :urlbtn="seturl" :info='i'/>
              </div>
            </div>
          </div>
          <div v-if="i.widgetType === 'SingleLineText'&& i.exactType === 'BMI'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div class="bmiContent">
                <p class="pointNum">{{ bmiVal }}</p>
                <div class="linecontent">
                  <div class="lineInner"></div>
                  <div class="point" :style="{left:bmiset}"></div>
                  <span class="dot-txt1">偏轻</span>
                  <span class="dot-txt2">健康</span>
                  <span class="dot-txt3">偏重</span>
                  <span class="dot-txt4">肥胖</span>
                  <span class="dot-txt5">非常肥胖</span>
                  <span class="dot1">18.5</span>
                  <span class="dot2">24</span>
                  <span class="dot3">28</span>
                  <span class="dot4">32</span>
                </div>
              </div>
            </div>
          </div>
          <div v-if="i.widgetType === 'SingleLineText'&& i.exactType === 'SCR'">
            <div class="title">
              <h2>{{ i.label }}</h2>
              <p>{{ i.labelDesc }}</p>
            </div>
            <div class="item">
              <div class="item">
                <div class="inputContent">
                  <input :placeholder="i.placeholder" v-model="inputscrVal" type="number">
                  <span>{{ i.rightUnit }}</span>
                </div>
              </div>
              <div class="bmiContent">
                <p class="pointNumT">评测结果</p>
                <div class="docsOne">
                  <span class="docsName">GFR<span style="margin-left: 53px"
                                                  class="docsdashed">--------------</span></span>
                  <span class="docsName">{{ gfrVal }}</span>
                </div>
                <div class="docsOne">
                  <span class="docsName">CCR<span style="margin-left: 53px"
                                                  class="docsdashed">--------------</span></span>
                  <span class="docsName">{{ ccr }}</span>
                </div>
                <div class="docsOne">
                  <span class="docsName">肾病分期 <span class="docsdashed">--------------</span></span>
                  <span class="docsName">{{ moregfr }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-for="ques in i.values">
            <levelTwo v-if="ques.questions" :questionsList="ques.questions" :urlbtn="levelBtn"/>
          </div>
          <!-- 这里只处理子选项 -->
          <div v-if="levelCan">
            <levelTwo :questionsList="levelObj" :urlbtn="levelBtn"/>
          </div>
        </div>
      </template>
    </div>
    <div :style="{display:'flex','justify-content':pageNum < 1?'center':'space-between'}" class="footer">
      <span v-if="showBack()" class="backquestion" @click="backBtn()">上一步</span>
      <div
        class="nextquestion"
        :style="{background:getNextColor(mydata[pageNum])}"
        @click="nextBtn(mydata[pageNum])">
        {{ questionNumber - 1 === pageNum ? status == '1' ? '完成注册' : '提交' : '下一步' }}
      </div>
    </div>
  </section>
</template>
<script>
import Vue from 'vue'
import hospital from '../../components/arrt/questionsHospital/hospital.vue'
import upLoaderimg from './upload'
import headPortrait from '@/components/arrt/headPortrait/headPortrait'
import upAvatar from './upavatar'
import levelTwo from './levelTwo'
import Api from '@/api/Content.js'
import wx from 'weixin-js-sdk'
import {Box, DatetimeView, Scroller, Swiper, SwiperItem, XProgress, XTextarea,} from 'vux'
import {Button, Cell, Picker, Popup, Toast} from 'vant'

Vue.use(Picker)
Vue.use(Toast)
Vue.use(Button)
Vue.use(Cell)
Vue.use(Popup)

export default {
  components: {
    headPortrait,
    XProgress,
    Box,
    XTextarea,
    Scroller,
    Swiper,
    SwiperItem,
    DatetimeView,
    upLoaderimg,
    upAvatar,
    levelTwo,
    hospital
  },
  data() {
    return {
      patientBaseInfo: '', // 患者基本信息
      pageTitle: '',
      SingleLineTextInput: '',
      pageNum: 0,
      selectIndex: '',
      mydata: [],
      userData: [],
      status: 1,
      heightData: [],
      weightData: [],
      swiperItemIndex: 0,
      manImg: require('@/assets/my/man.png'),
      wemanImg: require('@/assets/my/weman.png'),
      manImgT: require('@/assets/my/manT.png'),
      wemanImgT: require('@/assets/my/wemanT.png'),
      genderIndex: '',
      birthdayData: {year: [], month: [], day: []},
      format: 'YYYY-MM-DD',
      //公用的赋值data
      inputVal: '',
      inputscrVal: '',
      textareaVal: '',
      birthdayVal: '',
      timeVal: '',
      bmiVal: '',
      bmiset: '',
      gfrVal: '',
      moregfr: '',
      ccr: '',
      weightVal: '',
      heightVal: '',
      allInfo: {},
      shows: false,
      addressVal: [],
      addressData: [],
      showaddressVal: '请选择',
      //   addressCodeVal:[],
      levelCan: false,
      levelObj: [],
      showAddress: false,
      validNum: 0,
      Canparent: false,
      max: false, //数字显示超出最大值
      min: false, //数字显示小最小值
      nextpage: false,
      chooseinput: false,
      userAge: undefined,
      userWeight: undefined,
      userGender: undefined,
      ccrField: undefined,
      genderIndexs: undefined,
      formId: '',
      isGroup: 0,
      localPageTitleName: '',
      questionNumber: 0,
      formList: 0,
      showNextBtn: false,
    }
  },
  watch: {
    //赋值给到所有的input框的值
    inputVal: {
      handler(newVal) {
        if (this.mydata[this.pageNum].widgetType === 'Number' && this.mydata[this.pageNum].maxValue && newVal) {
          if (newVal.length > this.mydata[this.pageNum].maxValue) {
            this.mydata[this.pageNum].values = [{attrValue: newVal.substring(0, this.mydata[this.pageNum].maxValue)}]
            this.inputVal = this.mydata[this.pageNum].values[0].attrValue
          } else {
            this.mydata[this.pageNum].values = [{attrValue: newVal}]
          }
        } else if (
          this.mydata[this.pageNum].widgetType === 'SingleLineText' &&
          this.mydata[this.pageNum].exactType === 'Mobile' &&
          this.mydata[this.pageNum].maxEnterNumber &&
          newVal
        ) {
          if (newVal.length > this.mydata[this.pageNum].maxEnterNumber) {
            this.mydata[this.pageNum].values = [
              {attrValue: newVal.substring(0, this.mydata[this.pageNum].maxEnterNumber)}
            ]
            this.inputVal = this.mydata[this.pageNum].values[0].attrValue
          } else {
            this.mydata[this.pageNum].values = [{attrValue: newVal}]
          }
        } else {
          this.mydata[this.pageNum].values = [{attrValue: newVal}]
        }
        this.setmyotherVal()
      },
      deep: true
    },
    inputscrVal: {
      handler(newVal) {
        this.mydata[this.pageNum].values = [{attrValue: newVal}]
        this.setmyotherVal()
        if (!newVal) {
          this.gfrVal = ''
          this.moregfr = ''
          this.ccr = ''
        }
        this.calculateCcr()
      },
      deep: true
    },
    textareaVal: {
      handler(newVal) {
        this.mydata[this.pageNum].values = [{attrValue: newVal}]
      },
      deep: true
    },
    birthdayVal: {
      handler(newVal) {
        this.mydata[this.pageNum].values = [{attrValue: newVal, attrText: newVal}]
        this.setmyotherVal()
      },
      deep: true
    },
    timeVal: {
      handler(newVal) {
        this.mydata[this.pageNum].values = [{attrValue: newVal}]
        this.setmyotherVal()
      },
      deep: true
    },
    weightVal: {
      handler(newVal) {
        this.mydata[this.pageNum].values = [{attrValue: newVal}]
        this.setmyotherVal()
      },
      deep: true
    },
    heightVal: {
      handler(newVal) {
        this.mydata[this.pageNum].values = [{attrValue: newVal}]
        this.setmyotherVal()
      },
      deep: true
    },
    pageNum: {
      handler(newVal, oldVal) {
        if (newVal < this.mydata.length) {
          let obj = this.mydata[newVal]
          this.levelCan = false
          if (obj.widgetType) {
            if (
              obj.widgetType === 'FullName' ||
              obj.widgetType === 'Number' ||
              (obj.widgetType === 'SingleLineText' && !obj.exactType)
            ) {
              this.inputVal =
                obj.values && obj.values.length > 0 && obj.values[0].attrValue ? obj.values[0].attrValue : ''
            }
            if (obj.widgetType === 'SingleLineText' && obj.exactType === 'SCR') {
              this.inputscrVal = obj.values && obj.values.length > 0 ? obj.values[0].attrValue : ''
            }

            if (obj.widgetType === 'Radio' && obj.exactType === 'Gender') {
              this.genderIndexs =
                obj.values && obj.values.length > 0
                  ? obj.values[0].valueText
                    ? obj.values[0].valueText === '男'
                      ? 0
                      : 1
                    : ''
                  : ''
            }
            if (obj.widgetType === 'SingleLineText' && obj.exactType === 'Mobile') {
              this.inputVal = obj.values && obj.values.length > 0 ? obj.values[0].attrValue : ''
            }
            if (obj.widgetType === 'MultiLineText') {
              this.textareaVal = obj.values && obj.values.length >= 0 ? obj.values[0].attrValue : ''
            }
            if (obj.widgetType === 'SingleLineText' && obj.exactType === 'Weight') {
              this.weightVal =
                obj.values && obj.values.length >= 0 && obj.values[0].attrValue ? obj.values[0].attrValue : '60'
            }
            if (obj.widgetType === 'SingleLineText' && obj.exactType === 'Height') {
              this.heightVal =
                obj.values && obj.values.length >= 0 && obj.values[0].attrValue ? obj.values[0].attrValue : '170'
            }
            if (obj.widgetType === 'Address') {
              this.addressVal =
                obj.values && obj.values.length >= 0 && obj.values[0].attrValue ? obj.values[0].attrValue : []
              this.addressCodeVal =
                obj.values && obj.values.length >= 0 && obj.values[0].CodeVal ? obj.values[0].CodeVal : []
              let mydata = ''
              if (this.addressVal && this.addressVal.length > 0) {
                this.addressVal.forEach(element => {
                  mydata += '  ' + element
                })
                this.showaddressVal = mydata
              } else {
                this.showaddressVal = ''
              }
            }

            if (obj.widgetType === 'Date') {
              obj.values && obj.values.length > 0 && obj.values[0].attrValue ? obj.values[0].attrValue : ''
              if (obj.values && obj.values.length > 0 && obj.values[0].attrValue) {
                this.birthdayVal = obj.values[0].attrValue
              }
              this.setmyotherVal()
            }
            if (obj.widgetType === 'Time') {
              this.timeVal = obj.values && obj.values.length > 0 ? obj.values[0].attrValue : '12:00:00'
            }
            if (obj.widgetType === 'SingleLineText' && obj.exactType === 'Height') {
              this.$nextTick(() => {
                if (
                  this.mydata[newVal].values &&
                  this.mydata[newVal].values.length > 0 &&
                  this.mydata[newVal].values[0].attrValue
                ) {
                  if (this.$refs.scrollerEvent && this.$refs.scrollerEvent.length > 0) {
                    this.$refs.scrollerEvent[0].scrollTop = this.mydata[newVal].values[0].attrValue * 50 - 100
                  }
                }
              })
            }
            if (obj.widgetType === 'SingleLineText' && obj.exactType === 'Weight') {
              this.$nextTick(() => {
                if (
                  this.mydata[newVal].values &&
                  this.mydata[newVal].values.length > 0 &&
                  this.mydata[newVal].values[0].attrValue
                ) {
                  if (this.$refs.scrollerEventT && this.$refs.scrollerEventT.length > 0) {
                    this.$refs.scrollerEventT[0].scrollTop = this.mydata[newVal].values[0].attrValue * 50 - 100
                  }
                }
              })
            }
          }
        }
      },
      deep: true
    }
  },
  created() {
    fetch('https://caring.obs.cn-north-4.myhuaweicloud.com/public_js/address.js')
      .then(response => {
        return response.json();
      })
      .then(data => {
        Vue.set(this, 'addressData', data);
      })
    this.getInfo()
  },
  mounted() {
    if (this.$route.query.isGroup) {
      this.isGroup = Number(this.$route.query.isGroup)
      console.log('question mounted', this.isGroup)
    }
    if (this.$route.query && this.$route.query.status) {
      this.status = Number(this.$route.query.status)
      if (this.status === 1) {
        this.getbaseInfo()
      } else if (this.status === 2) {
        this.gethealthInfo()
      } else if (this.status === 4) {
        this.healthCalendar()
      } else if (this.status === 5) {
        this.testNumber()
      } else if (this.status === 6) {
        this.customfollow(this.$route.query.planId, this.$route.query.messageId)
      }
    }
  },
  methods: {
    showBack() {
      return this.pageNum >= 1
    },
    getNextColor(i) {
      console.log(i)
      if (i !== undefined) {
        if (i && i.required && i.widgetType) {
          // 单行文本, 多行文本,数字,头像
          if (i.widgetType === 'SingleLineText' && i.exactType !== 'hospital'
            || i.widgetType === 'MultiLineText' || i.widgetType === 'Number' || i.widgetType === 'FullName' || i.widgetType === 'Avatar') {
            if (!i.values[0].attrValue || i.values[0].attrValue === '') {
              return '#E0E0E0'
            } else {
              if (i.widgetType === 'Number') {
                console.log('我是数字', Number(i.values[0].attrValue), Number(i.maxValue), Number(i.values[0].attrValue) <= Number(i.maxValue))
                if (Number(i.values[0].attrValue) <= Number(i.maxValue) && Number(i.values[0].attrValue) >= Number(i.minValue)) {
                  this.max = false
                  this.min = false
                  return '#337EFF'
                } else {
                  return '#E0E0E0'
                }
              }
              if (i.exactType === 'Mobile') {
                if (i.values[0].attrValue.length < 11) {
                  return '#E0E0E0'
                }
              }
              return '#337EFF'
            }
          }
          // 医院
          if (i.exactType === 'hospital') {
            if (i.values.length < 1) {
              return '#E0E0E0'
            }
          }
          // 单选
          if (i.widgetType === 'Radio') {
            // 没选的话
            if (!i.values[0].valueText) {
              console.log('我多选')
              return '#E0E0E0'
            }
            // 需要填写选项备注
            if (i.itemAfterHasEnter === 1 &&
              i.values[0].valueText !== '其他' &&
              i.itemAfterHasEnterRequired === 1 &&
              (!i.values[0].desc || i.values[0].desc === '')) {
              return '#E0E0E0'
            }
            // 其他输入框必填
            if (i.values[0].valueText === '其他' && i.otherEnterRequired === 1) {
              if (!i.otherValue) {
                return '#E0E0E0'
              }
            }
          }
          // 多选
          if (i.widgetType === 'CheckBox') {
            if (!i.values[0].valueText) {
              return '#E0E0E0'
            }
            if (i.values.length > 0 && i.itemAfterHasEnterRequired == 1) {
              for (let index = 0; index < i.values.length; index++) {
                const item = i.values[index]
                const desc = i.options.find(val => val.id === item.attrValue)
                // 没填备注
                if (desc.attrValue !== '其他' && !desc.valuesRemark) {
                  console.log(132)
                  return '#E0E0E0'
                }
                if (desc.attrValue === '其他' && i.otherEnterRequired && !i.otherValue) {
                  // 没填其他备注
                  return '#E0E0E0'
                }
              }
            }
          }
          // 下拉
          if (i.widgetType === 'DropdownSelect') {
            // 没选
            if (!i.values[0].valueText) {
              return '#E0E0E0'
            }
            if (i.itemAfterHasEnterRequired === 1 && !i.values[0].desc && i.values[0].valueText !== '其他') {
              return '#E0E0E0'
            }
            // 选其他了但是没填输入框
            if (i.otherEnterRequired === 1 && !i.otherValue && i.values[0].valueText && i.values[0].valueText === '其他') {
              return '#E0E0E0'
            }
          }
          // 图片上传
          if (i.widgetType === 'MultiImageUpload') {
            if (i.values.length === 0) {
              return '#E0E0E0'
            }
          }
          // 地址
          if (i.widgetType === 'Address') {
            if (!i.values[0].attrValue) {
              return '#E0E0E0'
            }
            if (i.hasAddressDetail === 1 && i.hasAddressDetailRequired === 1) {
              if (!i.value || i.value === '') {
                // 输入框没写值
                return '#E0E0E0'
              }
            }
          }
        } else {
          if (i.widgetType === 'Number' && i.values[0].attrValue) {
            if (!i.maxValue && !i.minValue) {
              return '#337EFF'
            }
            if (i.maxValue && Number(i.values[0].attrValue) < Number(i.maxValue)) {
              return '#337EFF'
            } else {
              return '#E0E0E0'
            }
            if (i.minValue && Number(i.values[0].attrValue) > Number(i.minValue)) {
              return '#337EFF'
            } else {
              return '#E0E0E0'
            }
            return '#337EFF'
          }
          return '#337EFF'
        }
      }
      console.log('都通过了')
      return '#337EFF'

    },
    updata() {
      // console.log(123);
      this.shows = false
      this.$refs.addressPicker[0].confirm()
      let values = this.$refs.addressPicker[0].getValues()
      // console.log(values);
      let attrValue = []
      for (let i = 0; i < values.length; i++) {
        attrValue.push(values[i].name)
      }
      let mydata = ''
      if (attrValue.length > 0) {
        attrValue.forEach(element => {
          mydata += '  ' + element
        })
        this.showaddressVal = mydata
        this.mydata[this.pageNum].values = [{attrValue: attrValue}]
      }
    },
    onHide(a, b, c) {
      let names = ''
      let names1 = []
      for (let i = 0; i < a.length; i++) {
        b.forEach(item => {
          if (a[i] === item.value) {
            names += `${item.name}${i < 2 ? ' ' : ''}`
            names1.push(item.name)
          }
        })
        this.addressVal = names1
        console.log(this.addressVal)

        this.showaddressVal = names
        this.mydata[this.pageNum].values = [{attrValue: names1}]
      }
    },
    // 年龄
    jsGetAge(strBirthday, split) {
      // 传入形式yyyy-MM-dd
      // strBirthday = util.formatTime(strBirthday);转换成yyyy-MM-dd形式
      var returnAge
      var strBirthdayArr = strBirthday.split(split)
      var birthYear = strBirthdayArr[0]
      var birthMonth = strBirthdayArr[1]
      var birthDay = strBirthdayArr[2]
      var d = new Date()
      var nowYear = d.getFullYear()
      var nowMonth = d.getMonth() + 1
      var nowDay = d.getDate()
      if (nowYear == birthYear) {
        returnAge = 0 // 同年 则为0岁
      } else {
        var ageDiff = nowYear - birthYear // 年之差
        if (ageDiff > 0) {
          if (nowMonth == birthMonth) {
            var dayDiff = nowDay - birthDay // 日之差
            if (dayDiff < 0) {
              returnAge = ageDiff - 1
            } else {
              returnAge = ageDiff
            }
          } else {
            var monthDiff = nowMonth - birthMonth // 月之差
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
      this.userAge = returnAge
    },
    calculateCcr() {
      let sex = undefined
      let age = undefined
      let weight = undefined
      this.mydata.forEach(z => {
        if (
          z.widgetType === 'SingleLineText' &&
          z.exactType === 'Weight' &&
          z.values &&
          z.values.length > 0 &&
          z.values[0].attrValue
        ) {
          weight = z.values[0].attrValue
        }
        if (
          z.widgetType === 'Radio' &&
          z.exactType === 'Gender' &&
          z.values &&
          z.values.length > 0 &&
          z.values[0].attrValue
        ) {
          sex = z.values[0].valueText === '男' ? 0 : z.values[0].valueText === '女' ? 1 : undefined
        }
        if (
          z.widgetType === 'Date' &&
          z.exactType === 'Birthday' &&
          z.values &&
          z.values.length > 0 &&
          z.values[0].attrValue
        ) {
          age = that.jsGetAge(z.values[0].attrValue)
        }
      })
      if (!weight) {
        weight = this.patientBaseInfo.weight
      }
      if (sex === undefined) {
        sex = this.patientBaseInfo.sex
      }
      if (!age) {
        if (this.patientBaseInfo.birthday) {
          let userAge = this.patientBaseInfo.birthday
          if (userAge.indexOf('-') > -1) {
            this.jsGetAge(userAge, '-')
          } else if (userAge.indexOf('/') > -1) {
            this.jsGetAge(userAge, '/')
          }
          age = this.userAge
        }
      }
      let index = undefined
      const that = this
      that.allInfo.fieldList.forEach((ele, i) => {
        if (ele.exactType === that.Constants.FieldExactType.CCR) {
          index = i
        }
      })
      console.log(index)
      if (index === undefined) {
        return
      }
      if (sex === undefined) {
        return
      }
      let scr = this.inputscrVal

      if (!scr || scr === 0) {
        return
      }
      let temNum = ((140 - age) * weight) / (0.818 * scr)

      if (sex === 1) {
        temNum = temNum * 0.85
      }
      const num = temNum ? (temNum == Infinity ? 0 : temNum.toFixed(2)) : 0
      that.ccr = num
      console.log(that.ccr)
      that.allInfo.fieldList[index].values[0].attrValue = this.ccr
    },
    // 获取local里的性别 年龄体重
    getUserInof() {
      const myallInfo = this.patientBaseInfo
      if (myallInfo.weight) {
        this.userWeight = myallInfo.weight
      }
      if (myallInfo.sex !== null) {
        this.userGender = myallInfo.sex
      }
      if (myallInfo.birthday) {
        this.userAge = myallInfo.birthday
        if (this.userAge.indexOf('-') > -1) {
          this.jsGetAge(this.userAge, '-')
        } else if (this.userAge.indexOf('/') > -1) {
          this.jsGetAge(this.userAge, '/')
        }
      }
    },
    //----------------------------------赋值函数----------------------------------------
    setmyotherVal() {
      let that = this
      let myheight = undefined
      let myweight = undefined
      let mygender = undefined
      let myscr = undefined
      let myage = undefined
      this.mydata.forEach(z => {
        if (
          z.widgetType === 'SingleLineText' &&
          z.exactType === 'Height' &&
          z.values &&
          z.values.length > 0 &&
          z.values[0].attrValue
        ) {
          myheight = z.values[0].attrValue
        }
        if (
          z.widgetType === 'SingleLineText' &&
          z.exactType === 'Weight' &&
          z.values &&
          z.values.length > 0 &&
          z.values[0].attrValue
        ) {
          myweight = z.values[0].attrValue
        }
        if (
          z.widgetType === 'Radio' &&
          z.exactType === 'Gender' &&
          z.values &&
          z.values.length > 0 &&
          z.values[0].attrValue
        ) {
          mygender = z.values[0].valueText === '男' ? 1 : z.values[0].valueText === '女' ? 2 : 3
        }
        if (
          z.widgetType === 'SingleLineText' &&
          z.exactType === 'SCR' &&
          z.values &&
          z.values.length > 0 &&
          z.values[0].attrValue
        ) {
          myscr = z.values[0].attrValue
        }
        if (
          z.widgetType === 'Date' &&
          z.exactType === 'Birthday' &&
          z.values &&
          z.values.length > 0 &&
          z.values[0].attrValue
        ) {
          myage = that.jsGetAge(z.values[0].attrValue)
        }
      })

      if (!myweight) {
        myweight = this.patientBaseInfo.weight
      }
      if (!myheight) {
        myheight = this.patientBaseInfo.height
      }
      if (!mygender) {
        mygender = this.patientBaseInfo.sex === 0 ? 1 : this.patientBaseInfo.sex === 1 ? 2 : 3
      }
      if (!myage) {
        if (this.patientBaseInfo.birthday) {
          let userAge = this.patientBaseInfo.birthday
          if (userAge.indexOf('-') > -1) {
            this.jsGetAge(userAge, '-')
          } else if (userAge.indexOf('/') > -1) {
            this.jsGetAge(userAge, '/')
          }
          myage = this.userAge
        }
      }
      //   BMI
      let setIndex = this.allInfo.fieldList.findIndex(res => res.widgetType === 'SingleLineText' && res.exactType === 'BMI')
      if (myweight && myheight && setIndex && setIndex >= 0) {
        let myresult = (myweight * 10000) / (myheight * myheight)
        this.bmiVal = myresult.toFixed(2)
        this.allInfo.fieldList[setIndex].values[0].attrValue = myresult.toFixed(2)
        this.bmiset = ((myresult - 14.5) / 21.5) * 100 - 4 >= 96 ? '96%' : ((myresult - 14.5) / 21.5) * 100 - 4 + '%'
      }

      let setgfrIndex = this.allInfo.fieldList.findIndex(res => res.widgetType === 'SingleLineText' && res.exactType === 'GFR')
      let setmoregfrIndex = this.allInfo.fieldList.findIndex(
        res => res.widgetType === 'SingleLineText' && res.exactType === 'CourseOfDisease'
      )
      let gfrVal = 0
      let scrIndex = this.allInfo.fieldList.find(res => res.widgetType === 'SingleLineText' && res.exactType === 'SCR')
      if (scrIndex !== undefined) {
        if (mygender === 1 && myage) {
          gfrVal = 186 * Math.pow(myscr / 88.4, -1.154) * Math.pow(myage, -0.203)
        } else if (mygender === 2 && myage) {
          gfrVal = 186 * Math.pow(myscr / 88.4, -1.154) * Math.pow(myage, -0.203) * 0.742
        }
        if (gfrVal >= 90 && gfrVal !== Infinity) {
          this.moregfr = 'CKD 1期'
        } else if (gfrVal >= 60 && gfrVal < 90) {
          this.moregfr = 'CKD 2期'
        } else if (gfrVal >= 30 && gfrVal < 60) {
          this.moregfr = 'CKD 3期'
        } else if (gfrVal >= 15 && gfrVal < 30) {
          this.moregfr = 'CKD 4期'
        } else if (gfrVal < 15 && gfrVal > 0) {
          this.moregfr = 'CKD 5期'
        } else if (gfrVal <= 0) {
          this.moregfr = ''
        }
        if (myscr && Number(myscr) > 0 && setgfrIndex >= 0 && setmoregfrIndex >= 0) {
          this.allInfo.fieldList[setgfrIndex].values[0].attrValue = gfrVal.toFixed(2)
          this.gfrVal = gfrVal.toFixed(2)
          this.allInfo.fieldList[setmoregfrIndex].values[0].attrValue = this.moregfr
        } else if (myscr && Number(myscr) <= 0 && setgfrIndex >= 0 && setmoregfrIndex >= 0) {
          this.allInfo.fieldList[setgfrIndex].values[0].attrValue = ''
          this.gfrVal = ''
          this.moregfr = ''
        }
      } else if (myscr && myage && setgfrIndex >= 0 && setmoregfrIndex >= 0 && mygender) {
        if (mygender === 1) {
          gfrVal = 186 * Math.pow(myscr / 88.4, -1.154) * Math.pow(myage, -0.203)
        } else if (mygender === 2) {
          gfrVal = 186 * Math.pow(myscr / 88.4, -1.154) * Math.pow(myage, -0.203) * 0.742
        }
        if (gfrVal >= 90 && gfrVal !== Infinity) {
          this.moregfr = 'CKD 1期'
        } else if (gfrVal >= 60 && gfrVal < 90) {
          this.moregfr = 'CKD 2期'
        } else if (gfrVal >= 30 && gfrVal < 60) {
          this.moregfr = 'CKD 3期'
        } else if (gfrVal >= 15 && gfrVal < 30) {
          this.moregfr = 'CKD 4期'
        } else if (gfrVal < 15 && gfrVal > 0) {
          this.moregfr = 'CKD 5期'
        } else if (gfrVal <= 0) {
          this.moregfr = ''
        }
        if (myscr && Number(myscr) > 0) {
          this.allInfo.fieldList[setgfrIndex].values[0].attrValue = gfrVal.toFixed(1)
          this.gfrVal = gfrVal.toFixed(1)
          this.allInfo.fieldList[setmoregfrIndex].values[0].attrValue = this.moregfr
        } else {
          this.allInfo.fieldList[setgfrIndex].values[0].attrValue = ''
          this.gfrVal = ''
          this.moregfr = ''
        }
      }
    },

    /**
     * 在经过检验后。提交数据到后端接口。
     */
    submitField(fillInIndex) {
      // 入组时 基本信息的提交。
      // 使用 myData 和 allInfo.fieldList 。合并出 提交使用的 fieldList
      const fieldList = []
      for (let i = 0; i < this.allInfo.fieldList.length; i++) {
        const fieldId = this.allInfo.fieldList[i].id
        const index = this.mydata.findIndex(item => item.id === fieldId)
        if (index > -1) {
          const field = this.mydata[index]
          fieldList.push(field)
        } else {
          fieldList.push(this.allInfo.fieldList[i])
        }
      }
      let params = {
        businessId: this.allInfo.businessId,
        category: this.allInfo.category,
        jsonContent: JSON.stringify(fieldList),
        messageId: this.allInfo.messageId,
        name: this.allInfo.name,
        oneQuestionPage: this.allInfo.oneQuestionPage,
        showTrend: this.allInfo.showTrend,
        userId: localStorage.getItem('userId'),
        formId: this.allInfo.formId,
      }
      if (this.allInfo.id) {
        params.id = this.allInfo.id
      }
      if (this.status === 1 || this.status === 2) {
        if (this.isGroup === 1) {
          params.fillInIndex = fillInIndex
          Api.saveFormResultStage(params).then(res => {
            this.allInfo.id = res.data.data.id
            if (fillInIndex === -1) {
              if (this.status === 1) {
                localStorage.setItem('editquestion', true)
                this.$router.replace({
                  path: '/questionnaire/loginSuccessful'
                })
              } else {
                this.$router.replace({
                  path: '/home'
                })
              }
            }
          })
          // 疾病信息 非入组 的提交。
        } else if (this.status === 2) {
          if (this.allInfo.id) {
            params.formId = this.allInfo.formId
            params.id = this.allInfo.id
            Api.gethealthformSubPut(params).then(res => {
              if (res.data.code === 0) {
                this.$vux.toast.text('提交成功', 'center')
                this.$router.replace({
                  path: '/home',
                })
              }
            })
          } else {
            params.formId = this.allInfo.formId
            Api.gethealthformSub(params).then(res => {
              if (res.data.code === 0) {
                this.$vux.toast.text('提交成功', 'center')
                this.$router.replace({
                  path: '/home',
                })
              }
            })
          }
        }
      } else if (this.status === 4 || this.status === 6 || this.status === 5) {
        // 判断是否是 健康日志 或者 检验数据 或者 自定义计划计划的表单
        // 健康日志
        if (this.status === 4) {
          Api.gethealthformSub(params).then(res => {
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功', 'center')
              this.$router.replace('/healthCalendar/index')
            }
          })
        }
        // 检验数据
        if (this.status === 5) {
          Api.gethealthformSub(params).then(res => {
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功', 'center')
              this.$router.replace('/testNumber/index')
            }
          })
        }
        // 自定义护理计划的表单
        if (this.status === 6) {
          Api.gethealthformSub(params).then(res => {
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功', 'center')
              this.$router.replace(`/custom/follow/${this.$route.query.planId}`)
            }
          })
        }
      }
    },

    //   下一页
    nextBtn(i) {
      for (let z = 0; z < this.mydata.length; z++) {
        if (this.mydata[z].widgetType === 'CheckBox') {
          if (this.mydata[z].values && this.mydata[z].values.length > 0) {
            const values = this.mydata[z].values
            for (let i = 0; i < values.length; i++) {
              for (let j = 0; j < this.mydata[z].options.length; j++) {
                if (this.mydata[z].options[j].select) {
                  if (this.mydata[z].options[j].id === values[i].attrValue) {
                    this.mydata[z].options[j].valuesRemark = values[i].desc
                    //
                  }
                }
              }
            }
          }
        }
      }

      // 验证表单是否满足限制
      if (this.getNextColor(i) === '#E0E0E0') {
        // 单行文本,多行文本,图片上传,姓名
        if (i.widgetType === 'SingleLineText' || i.widgetType === 'MultiLineText' || i.widgetType === 'MultiImageUpload' || i.widgetType === 'FullName' || i.widgetType === 'Avatar') {
          if (i.widgetType === 'MultiImageUpload') {
            this.$vux.toast.text('请上传图片', 'center')
            return
          }
          if (i.widgetType === 'Avatar') {
            this.$vux.toast.text('请上传您的头像', 'center')
            return
          }
          if (i.widgetType === 'FullName') {
            this.$vux.toast.text('请输入您的姓名', 'center')
            return
          }
          // 手机号组件
          if (i.exactType === 'Mobile') {
            if (i.values[0].attrValue.length < 11 && i.values[0].attrValue.length > 0) {
              this.$vux.toast.text('请填写正确手机号', 'center')
              return;
            }
          }
          if (i.exactType === 'hospital') {
            this.$vux.toast.text('请选择医院', 'center')
            return;
          }
          this.$vux.toast.text('请填写输入框', 'center')
          return
        }
        // 数字
        if (i.widgetType === 'Number') {
          if (!i.values[0].attrValue || i.values[0].attrValue === '') {
            this.$vux.toast.text('请填写输入框', 'center')
            return
          }
          if (this.max || this.min) {
            this.$vux.toast.text('超出范围请重新输入', 'center')
            return;
          }
        }
        // 单选
        if (i.widgetType === 'Radio') {
          if (i.exactType === 'Gender') {
            if (!i.values[0].valueText) {
              this.$vux.toast.text('请选择您的性别', 'center')
              return
            }

          } else if (!i.values[0].valueText) {
            this.$vux.toast.text('请选择一个选项', 'center')
            return
          }
          if (
            i.values[0].valueText !== '其他' &&
            i.itemAfterHasEnter === 1 &&
            i.itemAfterHasEnterRequired === 1 &&
            (!i.values[0].desc || i.values[0].desc === '')
            || i.values[0].valueText === '其他' && i.otherEnterRequired === 1 && !i.otherValue
          ) {
            this.$vux.toast.text('请填写输入框', 'center')
            return
          }
        }
        // 多选
        if (i.widgetType === 'CheckBox') {
          if (i.values.length > 0 && !i.values[0].valueText) {
            i.values.splice(0, 1)
          }
          if (i.values.length > 0 && i.itemAfterHasEnterRequired == 1) {
            for (let index = 0; index < i.values.length; index++) {
              if (i.values[index].valueText !== '其他' && (!i.values[index].desc || i.values[index].desc === '')) {
                this.$vux.toast.text('请填写输入框', 'center')
                return
              }
            }
            if (i.otherEnterRequired === 1 && !i.otherValue) {
              this.$vux.toast.text('请填写输入框', 'center')
              return
            }
          }
        }
        // 下拉
        if (i.widgetType === 'DropdownSelect') {
          if (!i.values[0].valueText) {
            this.$vux.toast.text('请选择一个选项', 'center')
            return;
          }
          // 选其他了但是没填输入框
          if (i.otherEnterRequired && !i.otherValue && i.values[0].valueText && i.values[0].valueText === '其他') {
            this.$vux.toast.text('请填写输入框', 'center')
            return;
          }
          if (i.values[0].valueText !== '其他' && i.itemAfterHasEnterRequired === 1 && (!i.values[0].desc || i.values[0].desc === '')) {
            this.$vux.toast.text('请填写输入框', 'center')
            return;
          }
        }
        // 地址组件限制
        if (i.widgetType === 'Address') {
          if (!i.values[0].attrValue) {
            this.$vux.toast.text('请选择地址', 'center')
            return;
          }
          if (i.hasAddressDetail === 1 && i.hasAddressDetailRequired === 1) {
            if (!i.value || i.value === '') {
              // 输入框填写了值
              this.$vux.toast.text('请输入详细地址', 'center')
              return;
            }
          }
        }
      }
      // 时间组件直接赋值
      if (i.widgetType === 'Time') {
        i.values = [{attrValue: this.timeVal === '' ? '12:00' : this.timeVal}]
      }
      // 手机号组件限制
      if (i.widgetType === 'SingleLineText' && i.exactType === 'Mobile') {
        console.log(i)
        if (i.required == true && (i.values[0].attrValue == '' || i.values[0].attrValue == undefined)) {
          this.$vux.toast.text('请填写输入框', 'center')
          return
        } else if (
          i.values &&
          i.values.length > 0 &&
          i.values[0].attrValue !== undefined &&
          i.values[0].attrValue.length < 11
        ) {
          this.$vux.toast.text('请输入正确手机号', 'center')
          return
        }
      }
      // 姓名限制
      if (i.widgetType == 'FullName') {
        if (i.required == true && (i.values[0].attrValue == '' || !i.values[0].attrValue)) {
          this.$vux.toast.text('请输入您的姓名', 'center')
          return
        }
      }
      // 性别限制
      if (i.exactType == 'Gender' && i.required == true) {
        if (!i.values[0].valueText) {
          this.$vux.toast.text('请选择您的性别', 'center')
          return
        }
      }
      //如果是 随访阶段组件，组件下包含日期组件
      if (i.widgetType === 'Radio' && i.exactType === 'FollowUpStage') {
        if (i.values && i.values.length > 0) {
          let values = i.values
          for (let j = 0; j < values.length; j++) {
            let questions = values[j].questions
            if (questions) {
              questions.forEach((item, index) => {
                if (item.widgetType === "Date" && item.values && item.values.length > 0 && !item.values[0].attrValue) {
                  i.values[j].questions[index].values[0] = {
                    attrText: moment().format("YYYY/MM/DD"),
                    attrValue: moment().format("YYYY/MM/DD")
                  }
                }
              })
            }
          }
        }
      }
      if (i.widgetType === "Date" && i.values && i.values.length > 0 && !i.values[0].attrValue) {
        // 如果日期没有值， 则默认赋值当前时间
        i.values[0] = {
          attrText: moment().format("YYYY/MM/DD"),
          attrValue: moment().format("YYYY/MM/DD")
        }
      }
      if ((i && i.required && i.values && i.values.length > 0) || (i && !i.required)) {
        if (this.pageNum < this.questionNumber - 1) {
          this.pageNum++
          if (this.isGroup === 1) {
            this.submitField(this.pageNum)
            if (this.status === 1) {
              this.pageTitle = this.pageTitle = `${this.$getDictItem('patient')}注册  ${this.pageNum + 1}/${this.questionNumber}`
            } else {
              this.pageTitle = `${this.localPageTitleName} ${this.pageNum + 1}/${this.questionNumber}`
            }
          }
          this.userData.splice(0, 1)
          this.jupnextQuestion()
        } else {
          if (this.isGroup === 1) {
            this.submitField(-1)
          } else {
            this.submitField(-1)
          }
        }
      } else {
        this.$vux.toast.text('此选项不能为空', 'center')
      }
    },
    // 数字组件失去焦点
    doBlur(val) {
      if (val.values[0].attrValue === '') {
        this.max = false
        this.min = false
        return
      }
      if (val.maxValue && parseInt(val.values[0].attrValue) > parseInt(val.maxValue)) {
        this.$vux.toast.text('超出最大值', 'center')

        this.max = true
        this.min = false
      }
      if (val.minValue && parseInt(val.values[0].attrValue) < parseInt(val.minValue)) {
        this.$vux.toast.text('超出最小值', 'center')

        this.min = true
        this.max = false
      }
      if (
        val.maxValue &&
        parseInt(val.values[0].attrValue) <= parseInt(val.maxValue) &&
        val.minValue &&
        parseInt(val.values[0].attrValue) >= parseInt(val.minValue)
      ) {
        this.max = false
        this.min = false
      }

    },
    datematterFix(i) {
      var nowDate = new Date()
      var year = nowDate.getFullYear()
      var month = nowDate.getMonth() + 1 < 10 ? '0' + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1
      var day = nowDate.getDate() < 10 ? '0' + nowDate.getDate() : nowDate.getDate()
      let result = year + '/' + month + '/' + day
      //   如果是第一次并且有默认时间
      if (!i.values[0].attrValue && i.defaultValue) {
        i.values = [
          {
            attrValue: i.defaultValue,
            attrText: i.defaultValue
          }
        ]
        return i.defaultValue
      }
      //   如果有之前数据的值
      if (i.values[0].attrValue) {
        result = i.values[0].attrValue
        return result
      }
      // 如果没有默认值 并且 第一次
      if (!i.values[0].attrValue && !i.defaultValue) {
        i.values = [
          {
            attrValue: result,
            attrText: result
          }
        ]
        return result
      }
    },
    ifios() {
      let result = false
      var browser = {
        versions: (function () {
          var u = navigator.userAgent
          return {
            trident: u.indexOf('Trident') > -1, //IE内核
            presto: u.indexOf('Presto') > -1, //opera内核
            webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
            gecko: u.indexOf('Firefox') > -1, //火狐内核Gecko
            mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios
            android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android
            iPhone: u.indexOf('iPhone') > -1, //iPhone
            iPad: u.indexOf('iPad') > -1, //iPad
            webApp: u.indexOf('Safari') > -1 //Safari
          }
        })()
      }
      var isPhone =
        browser.versions.mobile ||
        browser.versions.ios ||
        browser.versions.android ||
        browser.versions.iPhone ||
        browser.versions.iPad

      if (isPhone && browser.versions.ios) {
        return (result = true)
      } else if ((isPhone && browser.versions.android) || !isPhone) {
        return (result = false)
      }
      return result
    },
    backParent() {
      if (this.status === 1) {
        const enableIntro = localStorage.getItem('enableIntro')
        if (enableIntro !== undefined && enableIntro === '0') {
          this.$router.replace('/')
        } else {
          wx.closeWindow()
        }
      } else if (this.status === 2) {
        // 从基本信息页，跳转到欢迎页
        localStorage.setItem('editquestion', true)
        this.$router.replace({
          path: '/questionnaire/loginSuccessful'
        })
      } else if (this.status === 4 || this.status === 5 || this.status === 6) {
        if (this.status === 4) {
          this.$router.replace('/healthCalendar/index')
          return
        }
        if (this.status === 5) {
          this.$router.replace('/testNumber/index')
          return
        }
        if (this.status === 6) {
          this.$router.replace('/custom/follow/' + this.$route.query.planId)
          return
        }
      }
    },
    turnDate(i, k) {
      let result = ''
      if (i) {
        var d = ''
        if (this.ifios()) {
          d = new Date(this.rTime(i.replace(/-/g, '/')))
        } else {
          d = new Date(i)
        }
        let thisMonth = d.getMonth() + 1
        let thisDay = d.getDate()
        if (Number(thisMonth) < 10) {
          thisMonth = '0' + thisMonth
        }
        if (Number(thisDay) < 10) {
          thisDay = '0' + thisDay
        }
        //   result = d.getFullYear() + '/' + thisMonth + '/' + thisDay
        //
        //
        result = d.getFullYear() + '/' + thisMonth + '/' + thisDay
      } else {
        if (k) {
          var d = new Date('1945/01/01')
          let thisMonth = d.getMonth() + 1
          let thisDay = d.getDate()
          if (Number(thisMonth) < 10) {
            thisMonth = '0' + thisMonth
          }
          if (Number(thisDay) < 10) {
            thisDay = '0' + thisDay
          }
          result = d.getFullYear() + '/' + thisMonth + '/' + thisDay
        } else {
          var d = new Date()
          let thisMonth = d.getMonth() + 1
          let thisDay = d.getDate()
          if (Number(thisMonth) < 10) {
            thisMonth = '0' + thisMonth
          }
          if (Number(thisDay) < 10) {
            thisDay = '0' + thisDay
          }
          result = d.getFullYear() + '/' + thisMonth + '/' + thisDay
        }
      }
      return result
    },
    rTime(date) {
      var json_date = new Date(date).toJSON()
      return new Date(new Date(json_date) + 8 * 3600 * 1000)
        .toISOString()
        .replace(/T/g, ' ')
        .replace(/\.[\d]{3}Z/, '')
    },
    rulesSet(i) {
      var reg = /(\d{4})\-(\d{2})\-(\d{2})/
      var date = i.replace(reg, '$1年$2月$3日')
      date = date.substring(0, 4)
      return Number(date)
    },
    levelBtn(i) {
      if (this.mydata[this.pageNum].values[0].children) {
        this.mydata[this.pageNum].values[0].children = i
      }
    },
    onShadowChange(ids, names) {
      //
      if (this.showAddress) {
        let mydata = ''
        names.forEach(element => {
          mydata += '  ' + element
        })
        this.showaddressVal = mydata
        this.addressVal = names
        this.mydata[this.pageNum].values = [{attrValue: names}]
      }
    },
    gethealthInfo() {
      Toast.loading('加载中');
      const params = {
        formEnum: 'HEALTH_RECORD',
        patientId: localStorage.getItem('userId'),
        completeEnterGroup: 0
      }
      Api.gethealthform(params).then(res => {
        if (res.data.code === 0) {
          if (res.data.data.name) {
            this.localPageTitleName = res.data.data.name
          } else  {
            this.localPageTitleName = localStorage.getItem('pageTitle')
          }

          this.allInfo = res.data.data
          this.pageNum = res.data.data.fillInIndex && res.data.data.fillInIndex > 0 ? res.data.data.fillInIndex : 0
          res.data.data.fieldList.forEach(item => {
            if (item.widgetType !== 'Desc' && item.widgetType !== 'Page' && item.widgetType !== 'SplitLine' && item.exactType !== 'GFR' && item.exactType !== 'CourseOfDisease' && item.exactType !== 'CCR' && item.exactType !== 'MonitoringEvents') {
              this.mydata.push(item)
            }
          })
          this.questionNumber = this.mydata.length
          if (this.isGroup === 1) {
            this.pageTitle = `${this.localPageTitleName} ${this.pageNum + 1}/${this.questionNumber}`
          } else {
            this.pageTitle = this.localPageTitleName
          }
          setTimeout(() => {
            Toast.clear()
          }, 300)
          this.jupnextQuestion()
          for (let i = 0; i < 300; i++) {
            this.heightData.push(i)
            this.weightData.push(i)
          }
          this.setmyotherVal()
        }
      })
    },
    getbaseInfo() {
      Toast.loading('加载中');
      const params = {
        formEnum: 'BASE_INFO',
        patientId: localStorage.getItem('userId'),
        completeEnterGroup: 0
      }
      Api.gethealthform(params).then(res => {
        if (res.data.code === 0) {
          console.log('=================>', res.data.data)
          this.pageNum = res.data.data.fillInIndex && res.data.data.fillInIndex > 0 ? res.data.data.fillInIndex : 0
          this.allInfo = res.data.data
          res.data.data.fieldList.forEach(item => {
            if (item.widgetType !== 'Desc' && item.widgetType !== 'Page' && item.widgetType !== 'SplitLine' && item.exactType !== 'GFR' && item.exactType !== 'CourseOfDisease' && item.exactType !== 'CCR' && item.exactType !== 'MonitoringEvents') {
              this.mydata.push(item)
            }
          })
          this.questionNumber = this.mydata.length
          if (this.isGroup === 1) {
            this.pageTitle = `${this.$getDictItem('patient')}注册  ${this.pageNum + 1}/${this.questionNumber}`
          } else {
            this.pageTitle = res.data.data.name
          }
          setTimeout(() => {
            Toast.clear()
          }, 300)
          this.jupnextQuestion()
          for (let i = 0; i < 300; i++) {
            this.heightData.push(i)
            this.weightData.push(i)
          }
          this.setmyotherVal()
        }
      })
    },
    healthCalendar() {
      const that = this
      const planType = 5
      this.pageTitle = localStorage.getItem('pageTitle')
      Api.formOrFormResult(null, planType, this.$route.query.messageId).then(res => {
        if (res.data.code === 0) {
          that.allInfo = res.data.data
          that.allInfo.fieldList = JSON.parse(that.allInfo.jsonContent)
          that.allInfo.fieldList.forEach(item => {
            if (item.widgetType !== 'Desc' && item.widgetType !== 'Page' && item.widgetType !== 'SplitLine' && item.exactType !== 'GFR' && item.exactType !== 'CourseOfDisease' && item.exactType !== 'CCR' && item.exactType !== 'MonitoringEvents') {
              this.mydata.push(item)
            }
          })
          this.questionNumber = this.mydata.length
          for (let i = 0; i < 300; i++) {
            that.heightData.push(i)
            that.weightData.push(i)
          }
          this.jupnextQuestion()
          that.setmyotherVal()
        }
      })
    },
    testNumber() {
      const that = this
      const planType = 3
      this.pageTitle = localStorage.getItem('pageTitle')
      Api.formOrFormResult(null, planType, this.$route.query.messageId).then(res => {
        console.log(res.data.data)
        that.allInfo = res.data.data
        that.allInfo.fieldList = JSON.parse(that.allInfo.jsonContent)
        that.allInfo.fieldList.forEach(item => {
          if (item.widgetType !== 'Desc' && item.widgetType !== 'Page' && item.widgetType !== 'SplitLine' && item.exactType !== 'GFR' && item.exactType !== 'CourseOfDisease' && item.exactType !== 'CCR' && item.exactType !== 'MonitoringEvents') {
            this.mydata.push(item)
          }
        })
        this.questionNumber = this.mydata.length
        for (let i = 0; i < 300; i++) {
          that.heightData.push(i)
          that.weightData.push(i)
        }
        this.jupnextQuestion()
        that.setmyotherVal()
      })
    },
    customfollow(i, k) {
      const that = this
      this.pageTitle = localStorage.getItem('pageTitle')
      Api.getCustomPlanFormResult(i, k).then(res => {
        if (res.data.code === 0 && res.data.data) {
          const form = res.data.data
          that.allInfo = form
          that.allInfo.fieldList = JSON.parse(that.allInfo.jsonContent)
          that.allInfo.fieldList.forEach(item => {
            if (item.widgetType !== 'Desc' && item.widgetType !== 'Page' && item.widgetType !== 'SplitLine' && item.exactType !== 'GFR' && item.exactType !== 'CourseOfDisease' && item.exactType !== 'CCR' && item.exactType !== 'MonitoringEvents') {
              this.mydata.push(item)
            }
          })
          this.formId = res.data.data.formId
          this.questionNumber = this.mydata.length
          for (let i = 0; i < 300; i++) {
            that.heightData.push(i)
            that.weightData.push(i)
          }
          this.jupnextQuestion()
          that.setmyotherVal()
        }
      })
    },
    seturl(k) {
      this.mydata[this.pageNum].values = k
    },
    jupnextQuestion() {
      if (this.pageNum === 0) {
        this.Canparent = true
      } else {
        this.Canparent = false
      }
      let obj = this.mydata[this.pageNum]
      if (
        (obj.widgetType &&
          (obj.widgetType === 'FullName' ||
            obj.widgetType === 'Number' ||
            (obj.widgetType === 'SingleLineText' && !obj.exactType) ||
            obj.widgetType === 'MultiLineText' ||
            (obj.widgetType === 'Radio' && !obj.exactType) ||
            obj.widgetType === 'DropdownSelect' ||
            obj.widgetType === 'MultiLevelDropdownSelect' ||
            obj.widgetType === 'CheckBox' ||
            (obj.widgetType === 'SingleLineText' && obj.exactType === 'Height') ||
            (obj.widgetType === 'SingleLineText' && obj.exactType === 'Weight') ||
            obj.widgetType === 'Address' ||
            (obj.widgetType === 'Radio' && obj.exactType === 'Gender') ||
            (obj.widgetType === 'Radio' && obj.exactType === 'FollowUpStage') ||
            obj.widgetType === 'Date' ||
            obj.widgetType === 'Time' ||
            obj.widgetType === 'MultiImageUpload' ||
            (obj.widgetType === 'SingleLineText' && obj.exactType === 'BMI') ||
            (obj.widgetType === 'SingleLineText' && obj.exactType === 'SCR') ||
            obj.widgetType === 'Avatar' ||
            obj.widgetType === 'Avatar' ||
            (obj.widgetType === 'SingleLineText' && obj.exactType === 'Mobile'))) ||
        (obj.widgetType === 'SingleLineText' && obj.exactType === 'hospital')) {
        this.userData.push(this.mydata[this.pageNum])
        this.checkbacklevel() //检查当前表单之前是否存在无法显示的表单
        this.$forceUpdate()
      } else {
        this.pageNum++
        if (this.pageNum < this.mydata.length) {
          this.jupnextQuestion()
        } else {
          this.submitField(-1)
        }
      }
    },
    jupbackQuestion() {
      let obj = this.mydata[this.pageNum]
      if (
        obj.widgetType &&
        (obj.widgetType === 'FullName' ||
          obj.widgetType === 'Number' ||
          (obj.widgetType === 'SingleLineText' && !obj.exactType) ||
          obj.widgetType === 'MultiLineText' ||
          (obj.widgetType === 'Radio' && !obj.exactType) ||
          obj.widgetType === 'DropdownSelect' ||
          obj.widgetType === 'MultiLevelDropdownSelect' ||
          obj.widgetType === 'CheckBox' ||
          (obj.widgetType === 'SingleLineText' && obj.exactType === 'Height') ||
          (obj.widgetType === 'SingleLineText' && obj.exactType === 'hospital') ||
          (obj.widgetType === 'SingleLineText' && obj.exactType === 'Weight') ||
          obj.widgetType === 'Address' ||
          (obj.widgetType === 'Radio' && obj.exactType === 'Gender') ||
          (obj.widgetType === 'Radio' && obj.exactType === 'FollowUpStage') ||
          obj.widgetType === 'Date' ||
          obj.widgetType === 'Time' ||
          obj.widgetType === 'MultiImageUpload' ||
          (obj.widgetType === 'SingleLineText' && obj.exactType === 'BMI') ||
          (obj.widgetType === 'SingleLineText' && obj.exactType === 'SCR') ||
          obj.widgetType === 'Avatar' ||
          obj.widgetType === 'Avatar' ||
          (obj.widgetType === 'SingleLineText' && obj.exactType === 'Mobile'))
      ) {
        this.userData.push(this.mydata[this.pageNum])
        this.validNum = this.pageNum
        if (this.pageNum === 0) {
          this.Canparent = true
        }
        this.checkbacklevel()
        this.$forceUpdate()
      } else {
        this.pageNum--
        if (this.pageNum >= 0) {
          this.jupbackQuestion()
        } else {
          this.Canparent = true
          this.pageNum = this.validNum
        }
      }
    },
    checkbacklevel() {
      let nowNum = false
      if (this.pageNum <= 0) {
        return
      }
      let myobj = this.mydata[this.pageNum - 1] ? this.mydata[this.pageNum - 1] : {}
      if (
        myobj.widgetType &&
        (myobj.widgetType === 'FullName' ||
          myobj.widgetType === 'Number' ||
          (myobj.widgetType === 'SingleLineText' && !myobj.exactType) ||
          myobj.widgetType === 'MultiLineText' ||
          (myobj.widgetType === 'Radio' && !myobj.exactType) ||
          myobj.widgetType === 'DropdownSelect' ||
          myobj.widgetType === 'MultiLevelDropdownSelect' ||
          myobj.widgetType === 'CheckBox' ||
          (myobj.widgetType === 'SingleLineText' && myobj.exactType === 'Height') ||
          (myobj.widgetType === 'SingleLineText' && myobj.exactType === 'hospital') ||
          (myobj.widgetType === 'SingleLineText' && myobj.exactType === 'Weight') ||
          myobj.widgetType === 'Address' ||
          (myobj.widgetType === 'Radio' && myobj.exactType === 'Gender') ||
          myobj.widgetType === 'Date' ||
          myobj.widgetType === 'Time' ||
          myobj.widgetType === 'MultiImageUpload' ||
          (myobj.widgetType === 'SingleLineText' && myobj.exactType === 'BMI') ||
          (myobj.widgetType === 'SingleLineText' && myobj.exactType === 'SCR') ||
          myobj.widgetType === 'Avatar' ||
          myobj.widgetType === 'Avatar' ||
          (myobj.widgetType === 'SingleLineText' && myobj.exactType === 'Mobile'))
      ) {
        nowNum = true
      }
      let myobjT = this.mydata[this.pageNum - 2] ? this.mydata[this.pageNum - 2] : {}
      if (
        myobjT.widgetType &&
        (myobjT.widgetType === 'FullName' ||
          myobjT.widgetType === 'Number' ||
          (myobjT.widgetType === 'SingleLineText' && !myobjT.exactType) ||
          myobjT.widgetType === 'MultiLineText' ||
          (myobjT.widgetType === 'Radio' && !myobjT.exactType) ||
          myobjT.widgetType === 'DropdownSelect' ||
          myobjT.widgetType === 'MultiLevelDropdownSelect' ||
          myobjT.widgetType === 'CheckBox' ||
          (myobjT.widgetType === 'SingleLineText' && myobjT.exactType === 'Height') ||
          (myobj.widgetType === 'SingleLineText' && myobj.exactType === 'hospital') ||
          (myobjT.widgetType === 'SingleLineText' && myobjT.exactType === 'Weight') ||
          myobjT.widgetType === 'Address' ||
          (myobjT.widgetType === 'Radio' && myobjT.exactType === 'Gender') ||
          myobjT.widgetType === 'Date' ||
          myobjT.widgetType === 'Time' ||
          myobjT.widgetType === 'MultiImageUpload' ||
          (myobjT.widgetType === 'SingleLineText' && myobjT.exactType === 'BMI') ||
          (myobjT.widgetType === 'SingleLineText' && myobjT.exactType === 'SCR') ||
          myobjT.widgetType === 'Avatar' ||
          myobjT.widgetType === 'Avatar' ||
          (myobjT.widgetType === 'SingleLineText' && myobjT.exactType === 'Mobile'))
      ) {
        nowNum = true
      }
      let myobjTs = this.mydata[this.pageNum - 3] ? this.mydata[this.pageNum - 3] : {}
      if (
        myobjTs.widgetType &&
        (myobjTs.widgetType === 'FullName' ||
          myobjTs.widgetType === 'Number' ||
          (myobjTs.widgetType === 'SingleLineText' && !myobjTs.exactType) ||
          myobjTs.widgetType === 'MultiLineText' ||
          (myobjTs.widgetType === 'Radio' && !myobjTs.exactType) ||
          myobjTs.widgetType === 'DropdownSelect' ||
          myobjTs.widgetType === 'MultiLevelDropdownSelect' ||
          myobjTs.widgetType === 'CheckBox' ||
          (myobjTs.widgetType === 'SingleLineText' && myobjTs.exactType === 'Height') ||
          (myobj.widgetType === 'SingleLineText' && myobj.exactType === 'hospital') ||
          (myobjTs.widgetType === 'SingleLineText' && myobjTs.exactType === 'Weight') ||
          myobjTs.widgetType === 'Address' ||
          (myobjTs.widgetType === 'Radio' && myobjTs.exactType === 'Gender') ||
          myobjTs.widgetType === 'Date' ||
          myobjTs.widgetType === 'Time' ||
          myobjTs.widgetType === 'MultiImageUpload' ||
          (myobjTs.widgetType === 'SingleLineText' && myobjTs.exactType === 'BMI') ||
          (myobjTs.widgetType === 'SingleLineText' && myobjTs.exactType === 'SCR') ||
          myobjTs.widgetType === 'Avatar' ||
          myobjTs.widgetType === 'Avatar' ||
          (myobjTs.widgetType === 'SingleLineText' && myobjTs.exactType === 'Mobile'))
      ) {
        nowNum = true
      }
      if (!nowNum) {
        if (this.pageNum === 1) {
          this.Canparent = true
        } else {
          this.Canparent = false
        }
      }
    },
    getInfo() {
      const params = {
        id: localStorage.getItem('userId')
      }
      Api.getContent(params).then(res => {
        if (res.data.code === 0) {
          if (res.data.data) {
            this.patientBaseInfo = res.data.data
            this.getUserInof()
            localStorage.setItem('myallInfo', JSON.stringify(res.data.data))
          }
        }
      })
    },

    backBtn() {
      this.userData.splice(0, 1)
      console.log(this.pageNum)
      this.pageNum--
      console.log(this.pageNum)
      if (this.isGroup === 1) {
        if (this.status === 1) {
          this.pageTitle = this.pageTitle = `${this.$getDictItem('patient')}注册  ${this.pageNum + 1}/${this.questionNumber}`
        } else {
          this.pageTitle = `${this.localPageTitleName} ${this.pageNum + 1}/${this.questionNumber}`
        }
      }
      this.jupbackQuestion()
    },
    checkboxSelectinput(z, i, y) {
      // i 里有desc
      // z 是当前数据
      // y是下标
      if (z.values.lenght > 0 && !z.values[0].valueText) {
        z.values = []
      }
      for (let j = 0; j < z.values.length; j++) {
        if (z.values[j].attrValue === i.id) {
          z.values[j].desc = i.valuesRemark
        }
      }
    },
    // 单选
    radioboxSelectinput(z, i) {
      if (z.values[0].desc === '' && z.itemAfterHasEnter === 1 && z.itemAfterHasEnterRequired === 1) {
        this.$vux.toast.text('请填写输入框', 'center')
      }
    },
    checkboxSelectbtn(z, i) {
      if (this.mydata[this.pageNum].values && this.mydata[this.pageNum].values[0] && !this.mydata[this.pageNum].values[0].attrValue) {
        this.mydata[this.pageNum].values.splice(0, 1)
      }
      let otherIndex = this.mydata[this.pageNum].values.findIndex(res => res.attrValue === i.id)
      if (otherIndex >= 0) {
        this.mydata[this.pageNum].values.splice(otherIndex, 1)
        if (this.mydata[this.pageNum].values.length === 0) {
          this.mydata[this.pageNum].values = [{}]
        }
      } else {
        if (i.questions && i.questions.length > 0) {
          this.mydata[this.pageNum].values.push({attrValue: i.id, valueText: i.attrValue, questions: i.questions})
        } else {
          this.mydata[this.pageNum].values.push({attrValue: i.id, valueText: i.attrValue})
        }
      }
    },
    //-----------------------------------------------------------------------
    avaterBtn(z) {
      this.mydata[this.pageNum].values = z
    },
    //   ------------------------------------选择题-------------------------------------------
    //单选多选，选择题的选中的样式
    checkVal(i, z) {
      let checkedindex = i.values.findIndex(res => res.attrValue === z.id)
      if (checkedindex >= 0) {
        if (i.values[checkedindex].children && i.values[checkedindex].children.length > 0) {
          this.levelCan = true
          this.levelObj = i.values[checkedindex].children
        } else {
          this.levelCan = false
          this.levelObj = []
        }
        return true
      } else {
        return false
      }
    },
    checkValT(i, z) {
      //
      let checkedindex = i.values.findIndex(res => res.attrValue === z.id)
      if (checkedindex >= 0) {
        return true
      } else {
        return
      }
    },
    //选择题的选定值
    selectBtn(i, k) {
      console.log(this.mydata[this.pageNum].values[0].attrValue === i.id)
      if (this.mydata[this.pageNum].values && this.mydata[this.pageNum].values[0] && this.mydata[this.pageNum].values[0].attrValue === i.id) {
        this.mydata[this.pageNum].values = [{attrValue: '', valueText: ''}]
        return
      }
      this.mydata[this.pageNum].values = [{attrValue: i.id, valueText: i.attrValue, questions: i.questions}]
      console.log(i, this.mydata[this.pageNum].values)

      if (i.children && i.children.length > 0) {
        let myobj = {options: [], values: [{attrValue: ''}], widgetType: 'Radio'}
        this.levelCan = true
        i.children.forEach(element => {
          myobj.options.push(element)
        })
        this.levelObj = [myobj]
        this.mydata[this.pageNum].values = [{attrValue: i.id, valueText: i.attrValue, children: [myobj]}]
      }
    },
    hasother(i) {
      if (i.values.findIndex(res => res.valueText === '其他') >= 0) {
        return true
      } else {
        return false
      }
    },
    otherVal(i) {
      if (i.values.findIndex(res => res.questions) >= 0) {
        return true
      } else {
        return false
      }
    },
    //---------------------------------身高体重的拖拽选择----------------------------------------
    SingleLineBtn(i, o) {
      this.mydata[this.pageNum].values = [{attrValue: o.val}]
      this.setmyotherVal()
    },

    //---------------------------------------性别---------------------------------
    selectedGender(z, k) {
      this.mydata[this.pageNum].values = [{valueText: z.options[k].attrValue, attrValue: z.options[k].id}]
      this.genderIndexs = k
      this.setmyotherVal()
    },
    jsGetAge(strBirthday) {
      var returnAge
      var strBirthdayArr = strBirthday.split('-')
      var birthYear = strBirthdayArr[0]
      var birthMonth = strBirthdayArr[1]
      var birthDay = strBirthdayArr[2]

      var d = new Date()
      var nowYear = d.getFullYear()
      var nowMonth = d.getMonth() + 1
      var nowDay = d.getDate()

      if (nowYear == birthYear) {
        returnAge = 0 //同年 则为0岁
      } else {
        var ageDiff = nowYear - birthYear //年之差
        if (ageDiff > 0) {
          if (nowMonth == birthMonth) {
            var dayDiff = nowDay - birthDay //日之差
            if (dayDiff < 0) {
              returnAge = ageDiff - 1
            } else {
              returnAge = ageDiff
            }
          } else {
            var monthDiff = nowMonth - birthMonth //月之差
            if (monthDiff < 0) {
              returnAge = ageDiff - 1
            } else {
              returnAge = ageDiff
            }
          }
        } else {
          returnAge = -1 //返回-1 表示出生日期输入错误 晚于今天
        }
      }
      this.userAge = returnAge
      return returnAge //返回周岁年龄
    }
  },

}
</script>
<style lang="less" scoped>
/deep/ .textareaContent {
  .weui-cell {
    padding: 0 !important;

    .weui-textarea {
      background: #FAFAFA !important;
    }
  }
}

/deep/ .van-picker__toolbar {
  background: #fbf9fe;
  border-bottom: 1px solid #eae9ec;

  .van-picker__confirm {
    color: #00d97e;
  }
}

/deep/ .van-popup::-webkit-scrollbar {
  width: 0 !important;
}

/deep/ .van-hairline-unset--top-bottom {
  background: rgba(238, 238, 238, 0.39);
  left: 0;
  width: 100%;
}

// 多选输入框样式
.CheckBoxInput {
  width: 100%;
  margin: 20px auto;
  text-align: center;

  input {
    width: 70%;
    height: 50px;
    border: none;
    border-bottom: 1px solid #d6d6d6;
  }

  input::-webkit-input-placeholder {
    text-align: center;
    color: #b8b8b8;
    font-size: 16px;
  }
}

.max {
  font-size: 13px;
  color: #ff0008;
  margin-left: 24px;
}

.nextBtn1 {
  pointer-events: none;
  background: rgba(102, 114, 140, 0.1) !important;
}

.allcontent {
  //width: 100vw;
  height: 94.8vh;
  position: relative;
  overflow: hidden;
  padding: 17px;
  background: linear-gradient(180deg, #EEF4FF 0%, #FFFFFF 100%);

  .topContent {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    z-index: 99999;
  }

  .content {
    margin-top: 13vw;
    padding-top: 43px;
    overflow-y: auto;
    border-radius: 13px;
    box-shadow: 0px 1px 7px 1px rgba(51, 126, 255, 0.08);
    height: 70vh;
    background: #FFFFFF;

    .title {
      text-align: center;
      margin-bottom: 40px;

      h2 {
        font-size: 16px;
        line-height: 30px;
        color: #333333;
      }

      p {
        font-size: 14px;
        line-height: 28px;
        color: #999999;
      }
    }

    .item {
      .upAvatar {
        width: 30vw;
        height: 30vw;
        margin: 20px auto;
        // overflow: hidden;
      }

      .bmiContent {
        width: 80vw;
        min-height: 190px;
        margin: 0px auto;
        border: 1px solid #ffbe8b;
        background: rgba(255, 190, 139, 0.1);
        border-radius: 6px;

        .docsOne {
          width: 80%;
          margin: 20px auto;
          display: flex;
          justify-content: space-between;

          .docsName {
            color: #666;
            font-size: 14px;
            vertical-align: middle;
          }

          .docsdashed {
            color: #b8b8b8;
            font-weight: bold;
            vertical-align: middle;
            margin-left: 20px;
            //   font-size: 14px;
          }
        }

        .pointNum {
          font-size: 26px;
          color: #66728c;
          font-weight: bold;
          text-align: center;
          margin: 40px 0px 60px;
        }

        .pointNumT {
          font-size: 26px;
          color: #66728c;
          font-weight: bold;
          text-align: center;
          margin: 40px 0px;
        }

        .linecontent {
          width: 70vw;
          margin: 10px auto;
          position: relative;

          .lineInner {
            width: 70vw;
            height: 8px;
            background-image: linear-gradient(to right,
            #00d97e,
            #00d97e 18.6%,
            #1fc8c8 18.6%,
            #1fc8c8 44.18%,
            #5695ff 44.18%,
            #5695ff 62.78%,
            #fea900 62.78%,
            #fea900 81.38%,
            #ff6f00 81.38%,
            #ff6f00 100%);
            // background: linear-gradient(to right, #00D97E 18.6%, #1FC8C8 25.58%,#5695FF 18.6%,#FEA900 18.6%,#FF6F00 18.6%); /* Safari 5.1 - 6.0 */
            // background: -o-linear-gradient(90deg, #00D97E 18.6%, #1FC8C8 25.58%,#5695FF 18.6%,#FEA900 18.6%,#FF6F00 18.6%); /* Opera 11.1 - 12.0 */
            // background: -moz-linear-gradient(90deg, #00D97E 18.6%, #1FC8C8 25.58%,#5695FF 18.6%,#FEA900 18.6%,#FF6F00 18.6%); /* Firefox 3.6 - 15 */
            // background: linear-gradient(to 90deg, #00D97E 18.6%, #1FC8C8 25.58%,#5695FF 18.6%,#FEA900 18.6%,#FF6F00 18.6%); /* 标准的语法 */
            border-radius: 20px;
            margin: 10px auto;
          }

          span {
            font-size: 12px;
            position: absolute;
            color: #999;
          }

          .point {
            position: absolute;
            width: 10px;
            height: 10px;
            border-radius: 50%;
            transform-origin: 50% 50%;
            border: 2px solid #66728c;
            background: #fff;
            top: -3px;
          }

          .dot-txt1 {
            font-size: 13px;
            color: #666;
            bottom: 15px;
            left: 4%;
          }

          .dot-txt2 {
            font-size: 13px;
            color: #666;
            bottom: 15px;
            left: 26%;
          }

          .dot-txt3 {
            font-size: 13px;
            color: #666;
            bottom: 15px;
            left: 48%;
          }

          .dot-txt4 {
            font-size: 13px;
            color: #666;
            bottom: 15px;
            left: 65%;
          }

          .dot-txt5 {
            font-size: 13px;
            color: #666;
            bottom: 15px;
            left: 78%;
          }

          .dot1 {
            bottom: -24px;
            left: 14.6%;
          }

          .dot2 {
            bottom: -24px;
            left: 41.18%;
          }

          .dot3 {
            bottom: -24px;
            left: 59.78%;
          }

          .dot4 {
            bottom: -24px;
            left: 78.38%;
          }
        }
      }

      .uploadImg {
        width: 70vw;
        margin: 0 auto;
      }

      .birthdayInner {
        width: 70vw;
        margin: 0 auto;
      }

      .genderinner {
        width: 60vw;
        display: flex;
        justify-content: space-between;
        margin: 0 auto;

        div {
          width: 22vw;
          height: 22vw;
          background: #fafafa;
          border: 1px solid rgba(214, 214, 214, 0.6);
          border-radius: 50%;
          display: table-cell;
          vertical-align: middle;
          text-align: center;
          line-height: 18vw;

          img {
            width: 13vw;
            display: inline-block;
          }
        }

        .man {
          background: #70ddc6;
          border: none;
        }

        .weman {
          background: #ffacac;
          border: none;
        }
      }

      .genderlabel {
        width: 60vw;
        display: flex;
        justify-content: space-between;
        margin: 10px auto;

        div {
          width: 22vw;
          height: 22vw;
          text-align: center;
        }
      }

      .inputContent {
        width: 80%;
        border-radius: 60px;
        border: 1px solid #d6d6d6;
        padding: 5px 10px;
        background: #f5f5f5;
        margin: 10px auto 20px;
        text-align: center;

        input {
          border: none;
          text-align: center;
          background: #f5f5f5;
          width: 75%;
          color: #666666;
        }

        p {
          border: none;
          text-align: center;
          background: #f5f5f5;
          width: 75%;
          color: #666666;
        }

        input::-webkit-input-placeholder {
          color: #b8b8b8;
        }

        span {
          color: #666666;
        }

        .vux-x-icon {
          fill: #b8b8b8;
          vertical-align: bottom;
        }
      }

      .textareaContent {
        background: #fafafa;
        border-radius: 10px;
        border: 1px solid #d6d6d6;
        padding: 10px;
        margin: 10px auto;
        width: 80%;

        textarea {
          background: #fafafa;
          border: none;
          width: 100%;
          resize: none;
        }
      }

      .radioItem {
        .allselect {
          p {
            border: 1px solid #cccccc;
            border-radius: 30px;
            width: 70%;
            color: #666666;
            margin: 20px auto;
            text-align: center;
            padding: 8px 3px;
          }

          .active {
            color: #337EFF;
            padding: 7px 0px;
            font-weight: bold;
            border: 2px solid #337EFF;
          }
        }

        .twoselect {
          p {
            border: 1px solid #cccccc;
            border-radius: 30px;
            color: #666666;
            margin: 20px auto;
            text-align: center;
            padding: 1px 30px;
          }

          .active {
            color: #66728c;
            padding: 0px 30px;
            font-weight: bold;
            border: 2px solid #66728c;
          }
        }
      }

      .otherquestions {
        width: 70%;
        margin: 30px auto;
        padding-top: 30px;
        border-top: 1px solid #e5e5e5;

        .title {
          text-align: center;
          margin-bottom: 40px;

          h2 {
            font-size: 16px;
            line-height: 30px;
            color: #333333;
          }

          p {
            font-size: 14px;
            line-height: 28px;
            color: #999999;
          }
        }
      }

      .checkItem {
        p {
          border: 1px solid #cccccc;
          border-radius: 6px;
          width: 70%;
          color: #666666;
          margin: 20px auto;
          text-align: center;
          padding: 8px 5px;
        }

        .active {
          color: #337EFF;
          padding: 7px 4px;
          font-weight: bold;
          border: 2px solid #337EFF;
        }
      }

      .othertextArea {
        width: 70%;
        margin: 30px 15% 20px;
        border: 1px solid #ccc;
        border-radius: 5px;

        .inner {
          padding: 10px !important;
          font-size: 12px;
        }
      }

      .SingleLinecontent {
        width: 30%;
        height: 40vh;
        margin: 20px auto;
        padding: 40px 0px;
        background: #fafafa;
        border: 1px solid #d6d6d6;
        overflow: hidden;
        border-top-right-radius: 60px;
        border-top-left-radius: 60px;
        border-bottom-right-radius: 60px;
        border-bottom-left-radius: 60px;

        div {
          width: 100%;
          height: 40vh;
          overflow: hidden;
          overflow-y: scroll;

          a {
            width: 80%;
            display: block;
            text-align: center;
            line-height: 40px;
            margin: 10px auto;
            border-radius: 40px;
          }

          .SingleLineItem {
            background: #66728c;
            color: #fff;
          }
        }
      }

      .nextBtn {
        margin: 60px auto;
        margin-top: 60px;
        background: rgba(214, 214, 214, 1);
        border: 1px solid rgba(102, 114, 140, 0.08);
        width: 60px;
        height: 60px;
        border-radius: 50%;
        text-align: center;
        line-height: 60px;

        .vux-x-icon {
          fill: #fff;
        }
      }
    }
  }

  .footer {
    padding: 17px 0;
    width: 100vw;
    display: flex;
    justify-content: space-between;

    .backquestion {
      margin: 10px 0px;
      width: 20%;
      padding: 10px 3%;
      border: 1px solid #337EFF;
      font-size: 17px;
      color: #337EFF;
      border-radius: 42px;
      text-align: center;
    }

    .nextquestion {
      margin: 10px 0px;
      width: 50%;
      padding: 10px 3%;
      background: #e0e0e0;
      font-size: 17px;
      color: #fff;
      border-radius: 42px;
      text-align: center;
      margin-right: 9%;
    }
  }
}

.van-button {
  background-color: rgb(102, 114, 139);
  border: none;
  width: 13rem /* 208/16;; */;
  height: 2.8125rem /* 45/16; */;
  margin: 0 auto;
  text-align: center;
  display: inherit;

  .van-button__text {
    color: #fff !important;
  }
}

</style>
