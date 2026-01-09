<template>
    <section>
        <x-header :left-options="{backText: ''}"  style="margin-bottom:0px!important">{{myTitle}}</x-header>
        <div>

             <group label-width="8em"  label-align="left">
                <template v-for="(i,k) in allFields">
                    <div v-if="i.widgetType==='Desc'" :key="k" style="width:100%;padding:10px 15px">
                        <p style="font-size:16px;font-weight:bolder;line-height:30px">{{i.label}}</p>
                        <p style="font-size:13px;color:#999">{{i.labelDesc}}</p>
                    </div>
                    <div  v-else-if="(i.widgetType === 'Avatar'&&i.exactType === 'Avatar')" :key="k" style="width:80px;margin:0 auto">
                        <upAvatar mytype="1" :filesList="i.values" :urlbtn="avaterBtn" :myindex="k" :objname="i.label"/>
                    </div>
                    <x-input :key="k" v-else-if="i.widgetType === 'FullName' ||i.widgetType === 'Number'||(i.widgetType === 'SingleLineText' && !i.exactType)||(i.widgetType === 'SingleLineText' && i.exactType === 'Mobile')" :value="inputValcheck(i)" :title="i.label" placeholder-align="right" text-align="right" :placeholder="i.placeholder" @on-change="changeValue($event,k)">
                        <span slot="right">{{i.rightUnit}}</span>
                    </x-input>
                    <x-input @on-change="changeValue($event,k)" :key="k" v-else-if="i.widgetType === 'SingleLineText'&&i.exactType === 'Height'" :value="inputValcheck(i)" :title="i.label" placeholder-align="right" text-align="right" :placeholder="i.placeholder">
                        <span slot="right">{{i.rightUnit}}</span>
                    </x-input>
                    <!-- <x-input  :key="k" v-else-if="i.widgetType === 'SingleLineText'&&i.exactType === 'BMI'" readonly :value="bmiValue" :title="i.label" placeholder-align="right" text-align="right" >
                        <span slot="right">{{i.rightUnit}}</span>
                    </x-input> -->
                    <cell :key="k" v-else-if="i.widgetType === 'SingleLineText'&&i.exactType === 'BMI'"  :title="i.label" >{{bmiValue}}</cell>
                     <x-input @on-change="changeValue($event,k)" :key="k" v-else-if="i.widgetType === 'SingleLineText'&&i.exactType === 'Weight'" :value="inputValcheck(i)" :title="i.label" placeholder-align="right" text-align="right" :placeholder="i.placeholder">
                        <span slot="right">{{i.rightUnit}}</span>
                    </x-input>
                    <x-input @on-change="changeValue($event,k)" :key="k" v-else-if="i.widgetType === 'SingleLineText'&&i.exactType === 'SCR'" :value="inputValcheck(i)" :title="i.label" placeholder-align="right" text-align="right" :placeholder="i.placeholder">
                        <span slot="right">{{i.rightUnit}}</span>
                    </x-input>
                    <!-- <x-input @on-change="changeValue($event,k)" :key="k" v-else-if="i.widgetType === 'SingleLineText'&&i.exactType === 'GFR'" :value="inputValcheck(i)" :title="i.label" placeholder-align="right" text-align="right" :placeholder="i.placeholder">
                        <span slot="right">{{i.rightUnit}}</span>
                    </x-input> -->
                    <cell :key="k" v-else-if="i.widgetType === 'SingleLineText'&&i.exactType === 'GFR'"  :title="i.label" >{{gfrVal}}</cell>
                    <cell :key="k" v-else-if="i.widgetType === 'SingleLineText'&&i.exactType === 'CourseOfDisease'"  :title="i.label" >{{moregfr}}</cell>
                    <x-input @on-change="changeValue($event,k)" :key="k" v-else-if="i.widgetType === 'SingleLineText'&&i.exactType === 'CCR'" readonly :value="inputValcheck(i)" :title="i.label" placeholder-align="right" text-align="right" :placeholder="i.placeholder">
                        <span slot="right">{{i.rightUnit}}</span>
                    </x-input>
                    <!-- <x-input @on-change="changeValue($event,k)" :key="k" v-else-if="i.widgetType === 'SingleLineText'&&i.exactType === 'CourseOfDisease'" readonly :value="inputValcheck(i)" :title="i.label" placeholder-align="right" text-align="right" :placeholder="i.placeholder">
                        <span slot="right">{{i.rightUnit}}</span>
                    </x-input> -->
<!--                    <x-address :key="k" v-else-if="i.widgetType === 'Address'" raw-value  @on-shadow-change="onShadowChange" :title="i.label" :value="addressValcheck(i)" :placeholder="i.placeholder" :list="addressData" @on-hide="addressHide(k)"></x-address>-->
                    <x-textarea @on-change="changeValue($event,k)" :key="k" v-else-if="i.widgetType === 'MultiLineText'" :title="i.label" :value="textareaValcheck(i)"   name="description" placeholder-align="right" :placeholder="i.placeholder"></x-textarea>
                    <div :key="k" v-else-if="i.widgetType === 'Radio'||i.widgetType === 'DropdownSelect'">
                        <cell :title="i.label" is-link :placeholder="i.placeholder" :border-intent="false" :arrow-direction="canWatch? 'up' : 'down'"  @click.native="canWatch = true"></cell>
                        <radio class="slide" :class="canWatch?'animate':''" @on-change="changeValue($event,k,true)" :options="setOptions(i)" :value="radioValcheck(i)" ></radio>
                    </div>
                    <div :key="k" v-else-if="i.widgetType === 'MultiLevelDropdownSelect'">
                        <cell :title="i.label" is-link :placeholder="i.placeholder" :border-intent="false" :arrow-direction="canWatch? 'up' : 'down'" @click.native="canWatch = true"></cell>
                        <radio class="slide" :class="canWatch?'animate':''" @on-change="changeValue($event,k,true)" :options="setOptions(i)" :value="radioValcheck(i)" ></radio>
                    </div>
                    <div :key="k" v-else-if="i.widgetType === 'CheckBox'">
                        <cell :title="i.label" is-link :placeholder="i.placeholder" :border-intent="false" :arrow-direction="canWatch? 'up' : 'down'" @click.native="canWatch = true"></cell>
                        <checklist  class="slide" :class="canWatch?'animate':''" @on-change="checkboxValue($event,k)" :options="setOptions(i)" :value="checklistValcheck(i)" ></checklist >
                    </div>

                    <cell v-else-if="(i.widgetType==='MultiImageUpload' && !i.exactType)" :key="k" :placeholder="i.placeholder" :title="i.label" >
                        <!-- <img :src="inputValcheck(i)?inputValcheck(i):imgIcon" alt="" style="width:30px;height:30px;"> -->
                        <upImg mytype="1" :filesList="i.values" :urlbtn="avaterBtn" :myindex="k" :objname="i.label"/>
                    </cell>
                    <datetime @on-change="changeValue($event,k)" text-align="right" :key="k" format="HH:mm" placeholder-align="right" v-else-if="i.widgetType==='Time'" :placeholder="i.placeholder" :value="inputValcheck(i)" :title="i.label"></datetime>
                    <datetime @on-change="changeValue($event,k)" text-align="right" :key="k" format="YYYY/MM/DD" placeholder-align="right" v-else-if="i.widgetType==='Date'" :placeholder="i.placeholder" :value="inputValcheck(i)" :title="i.label"></datetime>
                </template>
             </group>
             <x-button type="primary" @click.native="mysubmit()">提交</x-button>
        </div>
    </section>
</template>
<script>
import upAvatar from '@/view/questionnaire/upavatar'
import upImg from './upavatar'
import { XProgress, Box, XTextarea,XButton, Scroller,Swiper, SwiperItem,Datetime, DatetimeView, Popup, Radio,Checklist} from 'vux'
export default {
    components: {
        XProgress,
        Box,
        XTextarea,
        Scroller,
        Swiper,
        SwiperItem,
        DatetimeView,
        Popup,
        Radio,
        Checklist,
        Datetime,
        XButton,
        upAvatar,
        upImg
    },
    props: {
        myTitle: {
            type: String
        },
        allFields: {
            type: Array
        },
        submit: {
            type: Function
        }
    },
    data(){
        return{
            inputValue:'',
            addressVal:[],
            // addressData:ChinaAddressV4Data,
            obj:{},
            canWatch:true,
            imgIcon:require('@/assets/my/imgIcon.png'),
            bmiValue:'',
            gfrVal:'',
            moregfr:''
        }
    },
    // watch: {
    //     //赋值给到所有的input框的值
    //     inputValue: {
    //         handler(newVal) {
    //             // console.log(newVal)
    //         },
    //         deep: true

    //     }
    // },
    mounted(){

    },
    methods:{
        inputValcheck(z){//input框的输入判断
            // console.log(i)
            let result = ''
            if(z.values&&z.values.length>0){
                result = z.values[0].attrValue
            }


            return result
        },
        addressValcheck(i){//地址输入判断
            let result = []
            if(i.values&&i.values.length>0){
                result = i.values[0].attrValue
            }
            return result
        },
        checkresult(){
            let myheight = ''
            let myweight = ''
            let mygender = ''
            let myscr = ''
            let myage = ''
            let myresult = ''
            let this_ = this
            this.allFields.forEach(z => {
                if(z.widgetType === 'SingleLineText' && z.exactType === 'Height' && z.values && z.values.length > 0 && z.values[0].attrValue){
                    myheight = z.values[0].attrValue
                }
                if(z.widgetType === 'SingleLineText' && z.exactType === 'Weight' && z.values && z.values.length > 0 && z.values[0].attrValue){
                    myweight = z.values[0].attrValue
                }
                if(z.widgetType === 'Radio' && z.exactType === 'Gender' && z.values && z.values.length > 0 && z.values[0].attrValue){
                    mygender = z.values[0].valueText==='男'? 1 : (z.values[0].valueText==='女'?2:3)
                }
                if(z.widgetType === 'SingleLineText'&& z.exactType === 'SCR' && z.values && z.values.length > 0 && z.values[0].attrValue){
                    myscr = z.values[0].attrValue
                }
                if(z.widgetType === 'Date'&& z.exactType === 'Birthday' && z.values && z.values.length > 0 && z.values[0].attrValue){
                    myage = this_.jsGetAge(z.values[0].attrValue)
                }
            });
            let setIndex = this.allFields.findIndex(res=>res.widgetType === 'SingleLineText'&& res.exactType === 'BMI')
            if(myweight&&myheight&&setIndex&&setIndex>=0){
                myresult = (myweight*10000/(myheight*myheight)).toFixed(1)
                this.$set(this.allFields[setIndex],'values',[{attrValue:myresult}])
            }else{
                myresult = ''
            }
            this.bmiValue = myresult

            let setgfrIndex = this.allFields.findIndex(res=>res.widgetType === 'SingleLineText'&& res.exactType === 'GFR')
            let setmoregfrIndex = this.allFields.findIndex(res=>res.widgetType === 'SingleLineText'&& res.exactType === 'CourseOfDisease')


            let gfrVal = 0
            myage = myage?myage:0
            mygender = mygender===1?1:(mygender===2?2:3)
            if(mygender===1&&myage){
                gfrVal = 186*Math.pow(myscr/88.4,-1.154)*Math.pow(myage,-0.203)
            }else if(mygender===2&&myage){
                gfrVal = 186*Math.pow(myscr/88.4,-1.154)*Math.pow(myage,-0.203)*0.742
            }
            if(gfrVal>=90){
                this.moregfr = 'CKD 1期'
            }else if(gfrVal>=60 && gfrVal<90){
                this.moregfr = 'CKD 2期'
            }else if(gfrVal>=30 && gfrVal<60){
                this.moregfr = 'CKD 3期'
            }else if(gfrVal>=15 && gfrVal<30){
                this.moregfr = 'CKD 4期'
            }else if(gfrVal<15&&gfrVal>0){
                this.moregfr = 'CKD 5期'
            }else if(gfrVal<=0){
                this.moregfr = ''
            }
            if(myscr&&Number(myscr)>0){
               this.gfrVal = gfrVal.toFixed(1)
               if(setgfrIndex>0){
                   this.$set(this.allFields[setgfrIndex],'values',[{attrValue:this.gfrVal}])
               }
               if(setmoregfrIndex>0){
                   this.$set(this.allFields[setmoregfrIndex],'values',[{attrValue:this.moregfr}])
               }
            }else{
                this.gfrVal = ''
                this.moregfr = ''
            }

        },
        checkboxValue(j,k){
            console.log(j,k)
            let this_ = this
            let result = []
            j.forEach(element => {
                if(element){
                    let myindex = this_.allFields[k].options.findIndex(res=>res.id===element)
                    result.push({attrValue:element,valueText:this.allFields[k].options[myindex].attrValue})
                }

            });
            // let myindex = this.allFields[k].options.findIndex(res=>res.id===j)
            // // if(myindex>=0){
                this.$set(this.allFields[k],'values',result)
            // }

        },
        textareaValcheck(i){//多行学文字显示
            let result = ''
            if(i.values&&i.values.length>0){
                result = i.values[0].attrValue
            }
            return result
        },
        radioValcheck(i){//单选的值处理
            let result = ''
            if(i.values&&i.values.length>0){
                result = i.values[0].attrValue
            }
            return result
        },
        checklistValcheck(i){
            let result = []
            if(i.values&&i.values.length>0){
                // result = i.values
                i.values.forEach(element => {
                    result.push(element.attrValue)
                });
            }
            return result
        },
        setOptions(i){
            // console.log(i.options)
            let result = []
            if(i.options&&i.options.length>0){
                i.options.forEach(element => {
                    let thisObj = {}
                    thisObj.key = element.id
                    thisObj.value = element.attrValue
                    result.push(thisObj)
                });
                // result = i.values[0].attrValue
            }
            return result
        },
        onfocus(i){
            let obj = i
            if(obj.widgetType === 'FullName' ||obj.widgetType === 'Number'||(obj.widgetType === 'SingleLineText' && !obj.exactType)){
                this.inputValue = obj.values&&obj.values.length>0&&obj.values[0].attrValue?obj.values[0].attrValue:''
            }
        },
        avaterBtn(j){
            this.$set(this.allFields[j.index],'values',j.obj)
        },
        onshow(i,k){
            this.obj = i
        },

        changeValue(j,k,h){
            // console.log(j)
            if(h){
                let myindex = this.allFields[k].options.findIndex(res=>res.id===j)
                if(myindex>=0){
                    this.$set(this.allFields[k],'values',[{attrValue:j,valueText:this.allFields[k].options[myindex].attrValue}])
                }
            }else{
                this.$set(this.allFields[k],'values',[{attrValue:j}])
            }
            let z = this.allFields[k]
            if(z.widgetType === 'SingleLineText' && z.exactType === 'Height' && z.values && z.values.length > 0 && z.values[0].attrValue){
                this.checkresult()
            }
            if(z.widgetType === 'SingleLineText' && z.exactType === 'Weight' && z.values && z.values.length > 0 && z.values[0].attrValue){
                this.checkresult()
            }
            if(z.widgetType === 'Radio' && z.exactType === 'Gender' && z.values && z.values.length > 0 && z.values[0].attrValue){
                this.checkresult()
            }
            if(z.widgetType === 'SingleLineText'&& z.exactType === 'SCR' && z.values && z.values.length > 0 && z.values[0].attrValue){
                this.checkresult()
            }
            if(z.widgetType === 'Date'&& z.exactType === 'Birthday' && z.values && z.values.length > 0 && z.values[0].attrValue){
                this.checkresult()
            }

        },
        addressHide(k){
            this.$set(this.allFields[k],'values',[{attrValue:this.addressVal}])
        },
        onShadowChange(ids,name){
            this.addressVal = name
        },
        mysubmit(){
            // console.log(this.allFields)
            this.submit(this.allFields);
        },
        jsGetAge(strBirthday){
            var returnAge;
            var strBirthdayArr=strBirthday.split("/");
            var birthYear = strBirthdayArr[0];
            var birthMonth = strBirthdayArr[1];
            var birthDay = strBirthdayArr[2];

            var d = new Date();
            var nowYear = d.getFullYear();
            var nowMonth = d.getMonth() + 1;
            var nowDay = d.getDate();

            if(nowYear == birthYear){
                returnAge = 0;//同年 则为0岁
            }
            else{
                var ageDiff = nowYear - birthYear ; //年之差
                if(ageDiff > 0){
                    if(nowMonth == birthMonth) {
                        var dayDiff = nowDay - birthDay;//日之差
                        if(dayDiff < 0)
                        {
                            returnAge = ageDiff - 1;
                        }
                        else
                        {
                            returnAge = ageDiff ;
                        }
                    }
                    else
                    {
                        var monthDiff = nowMonth - birthMonth;//月之差
                        if(monthDiff < 0)
                        {
                            returnAge = ageDiff - 1;
                        }
                        else
                        {
                            returnAge = ageDiff ;
                        }
                    }
                }
                else
                {
                    returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
                }
            }

            return returnAge;//返回周岁年龄

        }
    }
}
</script>
<style scoped>
.weui-cell__ft{
    text-align: right;
}
.sub-item {
  color: #888;
}
.slide {
  padding: 0 20px;
  overflow: hidden;
  max-height: 0;
  transition: max-height .5s cubic-bezier(0, 1, 0, 1) -.1s;
}
.animate {
  max-height: 9999px;
  transition-timing-function: cubic-bezier(0.5, 0, 1, 0);
  transition-delay: 0s;
}
</style>
