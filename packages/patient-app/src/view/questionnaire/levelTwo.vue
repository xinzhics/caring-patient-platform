<template>
  <section class="contentlevel">
    <div v-for=" (i,z) in questionsList" :key="z" class="contentIn">
      <div v-if="i.widgetType === 'SingleLineText'||i.widgetType === 'Number'" class="contentInner">
        <div class="title">
          <h2>{{ i.label }}</h2>
          <p>{{ i.labelDesc }}</p>
        </div>
        <div class="item">
          <div class="inputContent">
            <input :type="i.widgetType === 'Number'?'number':'text'" :placeholder="i.placeholder" @blur="onblurBtn"
                   v-model="i.values[0].attrValue">
            <span>{{ i.rightUnit }}</span>
          </div>
        </div>
      </div>
      <div v-if="i.widgetType === 'MultiLineText'" class="contentInner">
        <div class="title">
          <h2>{{ i.label }}</h2>
          <p>{{ i.labelDesc }}</p>
        </div>
        <div class="item">
          <div class="textareaContent">
            <textarea :placeholder="i.placeholder" v-model="i.values[0].attrValue" rows="3" cols="20"></textarea>
          </div>
        </div>
      </div>
      <div v-if="(i.widgetType === 'Radio')||(i.widgetType === 'DropdownSelect')" class="contentInner">
        <div class="title">
          <h2>{{ i.label }}</h2>
          <p>{{ i.labelDesc }}</p>
        </div>
        <div class="item">
          <div class="radioItem">
            <div class="allselect">
              <p :class="checkVal(i,x)?'active':''" v-for="(x,y) in i.options" :key="y+'radio'"
                 @click="selectBtn(i,x,y+'radio')">
                {{ x.attrValue }}
              </p>
            </div>
          </div>
        </div>
      </div>
      <div v-if="i.widgetType === 'CheckBox'" class="contentInner">
        <div class="title">
          <h2>{{ i.label }}</h2>
          <p>{{ i.labelDesc }}</p>
        </div>
        <div class="item">
          <div class="checkItem" v-if="i.options">
            <p :class="checkVal(i,x)?'active':''" v-for="(x,y) in i.options"
               :key="y+'check'+ Math.round(Math.random()*10000+1110)" @click="checkboxSelectbtn(i,x,z)">
              {{ x.attrValue }}
            </p>
          </div>
        </div>
      </div>
      <div v-if="i.widgetType === 'MultiImageUpload'" class="contentInner">
        <div class="title">
          <h2>{{ i.label }}</h2>
          <p>{{ i.labelDesc }}</p>
        </div>
        <div class="item">
          <div class="uploadImg" @click="onfocusUpload(i,z)">
            <upLoaderimg :filesList="i.values" :info="i" :urlbtn="seturl"/>
          </div>
        </div>
      </div>
      <div v-if="i.widgetType === 'Date' " class="contentInner">
        <div class="title">
          <h2>{{ i.label }}</h2>
          <p>{{ i.labelDesc }}</p>
        </div>
        <div class="item">
          <div class="birthdayInner">
            <datetime-view
              :key="datematterFix(i)"
              :default-selected-value="datematterFix(i)"
              :render="setDateval(i,z)"
              v-model="i.birthdayVal"
              @on-change="(val) => birthdayValChange(val)"
              :start-date="i.defineChooseDate&&i.defineChooseDate==1?(i.defineChooseDateType==2?turnDate():'1900/01/01'):(i.minValue?i.minValue.replace(/-/g, '/'):'1900/01/01')"
              :end-date="i.defineChooseDate&&i.defineChooseDate==1?(i.defineChooseDateType==1?turnDate():'2121/01/01'):'2121/01/01'"
              year-row="{value}年" month-row="{value}月" day-row="{value}日" ref="datetime"
              format="YYYY/MM/DD"></datetime-view>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
<script>
import upLoaderimg from './upload'
import {DatetimeView} from 'vux'

export default {
  components: {
    upLoaderimg,
    DatetimeView
  },
  props: {
    urlbtn: {
      type: Function
    },
    questionsList: {
      type: Array,
      default() {
        return []
      }
    }
  },
  data() {
    return {
      inputVal: '',
      questionIndex: 0,
      questionObj: {},
      birthdayVal: ''
    }
  },
  created() {
    console.log('levelTwo', this.questionsList[0].values)
    if (this.questionsList && this.questionsList.length > 0) {
      for (let i = 0; i < this.questionsList.length; i++) {
        if (this.questionsList[i].widgetType === 'Date') {
          if (this.questionsList[i].values && this.questionsList[i].values.length > 0 && this.questionsList[i].values[0].attrValue) {
            this.questionsList[i].birthdayVal = this.questionsList[i].values[0].attrValue
          } else {
            this.questionsList[i].birthdayVal = this.questionsList[i].defaultValue
          }
        }
        if (this.questionsList[i].widgetType === 'SingleLineText' || this.questionsList[i].widgetType === 'Number' || this.questionsList[i].widgetType === 'MultiLineText') {
          if (!this.questionsList[i].values) {
            this.questionsList[i].values = [{attrValue: ''}]
          } else if (this.questionsList[i].values && this.questionsList[i].values.length > 0 && this.questionsList[i].values[0].attrValue === undefined) {
            this.questionsList[i].values[0].attrValue = ''
          }
        }
      }
    }
  },
  mounted() {

  },
  methods: {
    birthdayValChange(val) {
      for (let i = 0; i < this.questionsList.length; i++) {
        if (this.questionsList[i].widgetType === 'Date') {
          if (this.questionsList[i].birthdayVal) {
            this.questionsList[i].values = [{attrValue: this.questionsList[i].birthdayVal}]
          }
        }
      }
    },
    setDateval(i, z) {
      console.log('setDateval', i, z)
      this.questionsList[z].values = [{attrValue: i.birthdayVal}]
    },
    datematterFix(i) {
      let result = ''
      console.log('datematterFix', i)
      if (i.defaultValue) {
        result = i.defaultValue.replace().replace(/-/g, '/')
      }
      return result
    },
    rulesSet(i) {
      var reg = /(\d{4})\-(\d{2})\-(\d{2})/;
      var date = i.replace(reg, "$1年$2月$3日");
      date = date.substring(0, 3);
      return Number(date)
    },
    onblurBtn() {
      // this.questionIndex = index
      // this.questionObj = i
      this.urlbtn(this.questionsList)
    },
    setVal(i) {
      return true
    },
    checkVal(i, z) {
      let checkedindex = i.values.findIndex(res => res.attrValue === z.id)
      if (checkedindex >= 0) {
        return true
      } else {
        return false
      }
    },
    //选择题的选定值
    selectBtn(i, x) {
      i.values = [{attrValue: x.id, valueText: x.attrValue}]
      if (x.questions && x.questions.length > 0) {
        i.values = [{attrValue: x.id, valueText: x.attrValue, questions: x.questions}]
      }
      if (x.children && x.children.length > 0) {
        i.values = [{attrValue: x.id, valueText: x.attrValue, children: x.children}]
      }
      this.urlbtn(this.questionsList)
    },
    checkboxSelectbtn(i, x, z) {
      var obj = this.questionsList
      console.log(obj[z].id);
      obj[0].checkBoxValues = []
      if (obj[z].values && obj[z].values[0] && !obj[z].values[0].attrValue) {
        obj[z].values.splice(0, 1)
      }
      console.log(obj[z].values)
      let otherIndex = obj[z].values.findIndex(res => res.attrValue === x.id)
      if (otherIndex >= 0) {
        obj[z].values.splice(otherIndex, 1)
      } else {
        obj[z].values.push({attrValue: x.id, valueText: x.attrValue})
      }
      this.urlbtn(obj)
    },
    onfocusUpload(i, z) {
      this.questionObj = i
      this.questionIndex = z
    },
    seturl(k) {
      this.questionObj.values = k
      this.questionsList[this.questionIndex] = this.questionObj
      this.urlbtn(this.questionsList)
    },
    turnDate(i, k) {
      let result = ''
      if (i) {
        var d = ''
        if (this.ifios()) {
          d = new Date(this.rTime(i.replace(/-/g, '/')));
        } else {
          d = new Date(i);
        }
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
        if (k) {
          var d = new Date('1945/01/01');
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
          var d = new Date();
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
  }
}
</script>
<style lang="less" scoped>
.contentlevel {
  width: 100%;
  margin-top: 60px !important;
  .contentIn {
    .contentInner {
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
        .inputContent {
          width: 80%;
          border-radius: 60px;
          border: 1px solid #D6D6D6;
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

          input::-webkit-input-placeholder {
            color: #B8B8B8;
          }

          span {
            color: #666666;
          }

          .vux-x-icon {
            fill: #B8B8B8;
            vertical-align: bottom;
          }
        }

        .textareaContent {
          background: #FAFAFA;
          border-radius: 10px;
          border: 1px solid #D6D6D6;
          padding: 10px;
          margin: 10px auto;
          width: 80%;

          textarea {
            background: #FAFAFA;
            border: none;
            width: 100%;
            resize: none
          }
        }

        .radioItem {
          .allselect {
            p {
              border: 1px solid #CCCCCC;
              border-radius: 30px;
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
        }

        .checkItem {
          p {
            border: 1px solid #CCCCCC;
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

        .uploadImg {
          width: 70vw;
          margin: 0 auto;
        }

        .birthdayInner {
          width: 70vw;
          margin: 0 auto;
        }
      }

    }
  }
}
</style>
