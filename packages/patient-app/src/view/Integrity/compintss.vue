<template>
  <div>
        <div class="title">
      <div class="icon"></div>
      <div class="titleName">{{titles}}</div>
    </div>
    <div  v-for='(info,index) in currentFields' :key="index">
      <!-- 姓名 -->
      <div class="itme-box" v-if="info.formField.widgetType==='FullName'">
        <div v-if="info.ce" class="title">
          
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <singleLine :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 手机号 -->
      <div class="itme-box"  v-if="info.formField.widgetType==='SingleLineText'&&info.formField.exactType==='Mobile'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <caringMobile :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 下拉 -->
      <div class="itme-box" v-if="info.formField.widgetType==='DropdownSelect'&&info.formField.exactType==='Diagnose'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <dropdown :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 数字 -->
      <div class="itme-box" v-if="info.formField.widgetType==='Number'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <number :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>
      </div>
      <!-- 日期 -->
      <div class="itme-box" v-if="info.formField.widgetType==='Date'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <date1 :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 单选 -->
      <div class="itme-box" v-if="info.formField.widgetType==='Radio'&&info.formField.exactType!=='Gender'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <singleChoice :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 单行文本 -->
      <div class="itme-box" v-if="info.formField.widgetType==='SingleLineText'&&info.formField.exactType!=='Mobile'&&info.formField.exactType!=='Height'&&info.formField.exactType!=='Weight'&&info.formField.exactType!=='hospital'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <singleLine :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 多选 -->
      <div class="itme-box" v-if="info.formField.widgetType==='CheckBox'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <multipleChoice :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div  class="lines" v-if="index!=currentFields.length-1"></div>

      </div>
      <!-- 图片上传 -->
      <div class="itme-box" v-if="info.formField.widgetType==='MultiImageUpload'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <upphoto :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 头像 -->
      <div class="itme-box" v-if="info.formField.widgetType==='Avatar'&&info.formField.exactType==='Avatar'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <headPortrait :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 性别 -->
      <div class="itme-box" v-if="info.formField.widgetType==='Radio'&&info.formField.exactType==='Gender'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <gender :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 地址 -->
      <div class="itme-box" v-if="info.formField.widgetType==='Address'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <addresss :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 身高 -->
      <div class="itme-box" v-if="info.formField.widgetType==='SingleLineText'&&info.formField.exactType==='Height'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <height :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 体重 -->
      <div class="itme-box" v-if="info.formField.widgetType==='SingleLineText'&&info.formField.exactType==='Weight'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <weight :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 医院 -->
      <div class="itme-box" v-if="info.formField.widgetType==='SingleLineText'&&info.formField.exactType==='hospital'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <hospital :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 多行文字 -->
      <div class="itme-box" v-if="info.formField.widgetType==='MultiLineText'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <textArea :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 时间 -->
      <div class="itme-box" v-if="info.formField.widgetType==='Time'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <time1 :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
      <!-- 下拉框 -->
      <div class="itme-box" v-if="info.formField.widgetType==='DropdownSelect'&&info.formField.exactType!=='Diagnose'">
        <div v-if="info.ce" class="title">
          <div class="icon"></div>
          <div class="titleName">{{info.planName?info.planName:info.businessType}}</div>
        </div>
        <dropdown :field='info.formField' :style="{'pointer-events': info.fieldWrite==1?'none':''}" />
        <div class="lines" v-if="index!=currentFields.length-1"></div>
        <div v-if="info.fieldWrite!==1" class="tishi">待完善</div>

      </div>
    </div>
  </div>
</template>

<script>
import selectPicker from '@/components/select/index'
import attrDesc from '@/components/arrt/descindex'
import splitLine from '@/components/arrt/splitLineindex'
import hospital from '@/components/arrt/hospital/hospital'
import scr from '@/components/arrt/scr/scr'
import height from '@/components/arrt/height/height'
import weight from '@/components/arrt/weight/weight'
import Bmi from '@/components/arrt/bmi/bmi'
import headPortrait from '@/components/arrt/headPortrait/headPortrait'
import addresss from '@/components/arrt/address/address'
export default {
    components: {
    selectPicker,
    attrDesc,
    splitLine,
    hospital,
    scr,
    height,
    weight,
    Bmi,
    headPortrait,
    addresss,
    singleLine: () => import('@/components/arrt/Singleline/field-Singleline'),
    caringMobile: () => import('@/components/arrt/mobile/caringMobile'),
    textArea: () => import('@/components/arrt/textArea/textarea'),
    singleChoice: () => import('@/components/arrt/singleChoice/singleChoice'),
    multipleChoice: () => import('@/components/arrt/multipleChoice/multipleChoice'),
    number: () => import('@/components/arrt/number/number'),
    upphoto: () => import('@/components/arrt/hisPicture/hisPicture'),
    dropdown: () => import('@/components/arrt/dropdown/dropdown'),
    gender: () => import('@/components/arrt/gender/gender'),
    time1: () => import('@/components/arrt/time/time'),
    date1: () => import('@/components/arrt/date/date'),
    caringLine: () => import('@/components/arrt/line')
  },
  data(){
    return{
      titles:'',
      formData:[]
    }
  },
  beforeMount(){
    setTimeout(() => {
      console.log("=====================================",this.currentFields);
      this.titles=this.title
      this.formData=this.currentFields
    }, 1000);
  },
  props: {
    currentFields: {
      type: Array,
    },
    title: {
      type: String,
      default: ''
    }
  },
}
</script>

<style lang="less" scoped>
.itme-box{
  position: relative;
.tishi{
  font-size: 12px;
  text-align: center;
  width: 63px;
height: 24px;
color: #fff;
line-height: 24px;
background: #FF7777;
opacity: 1;
border-radius: 20px;
position: absolute;
right: 16px;
top: 22px;
}
}

.lines {
  height: 1px;
  background: #d6d6d6;
  margin-left: 16px;
  margin-right: 16px;
}
.title {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #d6d6d6;
  padding-bottom: 11.5px;
  margin-top: 29px;
  margin-left: 16px;
  margin-right: 16px;
  .icon {
    width: 3px;
    height: 12px;
    background: #337eff;
    border-radius: 4px 4px 4px 4px;
    margin-top: 7px;
  }
  .titleName {
    font-size: 16px;
    color: #337eff;
    margin-left: 8px;
    // font-weight: 700;
  }
}
</style>