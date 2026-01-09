<template>
    <section>
        <x-header :left-options="{backText: ''}"  style="margin-bottom:0px!important">{{myTitle}}<a slot="right" class="fa fa-exist"><img style="width: 1.2rem;" :src="editorImg" alt="" @click="myType==='1'?$router.push('/baseinfo/editor'):$router.push('/health/editor')"></a></x-header>
         <!-- @click="$router.push('/baseinfo/editor')" -->
        <div>
            <group label-width="8em"  label-align="left">
                <template v-for="(i,k) in allFields" >
                    <div v-if="i.widgetType==='Avatar' && i.exactType==='Avatar'" :key="k" style="border-top: 1px solid rgba(217,217,217,0.5);width:100%;text-align:center;padding:20px 0px">
                        <img :src="valueSet(i)" alt="" style="width:60px;border-radius:50%">
                    </div>
                    <div v-else-if="i.widgetType==='Desc'" :key="k" style="width:100%;padding:10px 15px">
                        <p style="font-size:16px;font-weight:bolder;line-height:30px">{{i.label}}</p>
                        <p style="font-size:13px;color:#999">{{i.labelDesc}}</p>
                    </div>
                    <cell v-else-if="i.widgetType==='MultiImageUpload' && !i.exactType" :key="k" :title="i.label" >
                        <img :src="valueSet(i)==='-'?imgIcon:valueSet(i)" alt="" style="width:30px;">
                    </cell>
                    <cell v-else :key="k" :title="i.label" :value="valueSet(i)" ></cell>
                </template>
                
            </group>
        </div>
    </section>
</template>
<script>
export default {
    props: {
        myTitle: {
            type: String
        },
        myType: {
            type: String
        },
        allFields: {
            type: Array
        },
    },
    data() {
        return {
            imgIcon:require('@/assets/my/imgIcon.png'),
            editorImg:require('@/assets/my/editor.png'),
        }
    },
    mounted(){
        // console.log(this.allFields)
    },
    methods:{
        valueSet(i) {
            let obj = i
            let val = ''
            // console.log(i)
            if(obj.widgetType&&((obj.widgetType === 'FullName' ||obj.widgetType === 'Number'||(obj.widgetType === 'SingleLineText' && !obj.exactType))||(obj.widgetType === 'MultiLineText')||(obj.widgetType === 'Radio' && !obj.exactType)||(obj.widgetType === 'DropdownSelect')||(obj.widgetType === 'MultiLevelDropdownSelect')
            ||obj.widgetType === 'CheckBox'||(obj.widgetType === 'SingleLineText' && obj.exactType === 'Height')||(obj.widgetType === 'SingleLineText' && obj.exactType === 'Weight')||(obj.widgetType === 'Address')
            ||(obj.widgetType === 'Radio' && obj.exactType === 'Gender')||(obj.widgetType === 'Date')||(obj.widgetType === 'Time')
            ||(obj.widgetType === 'MultiImageUpload')||(obj.widgetType === 'SingleLineText'&& obj.exactType === 'BMI')||(obj.widgetType === 'SingleLineText'&& obj.exactType === 'SCR')
            ||(obj.widgetType === 'Avatar' ||obj.widgetType === 'Avatar')||(obj.widgetType === 'SingleLineText'&&obj.exactType === 'Mobile') || (obj.widgetType === 'SingleLineText'&&obj.exactType === 'GFR')||(obj.widgetType === 'SingleLineText'&&obj.exactType === 'CCR')||(obj.widgetType === 'SingleLineText'&&obj.exactType === 'CourseOfDisease'))){
                if(obj.values.length>1){
                    obj.values.forEach(element => {
                        if(element.attrValue){
                            if(obj.rightUnit){
                                val += element.attrValue + obj.rightUnit
                            }else{
                                if(i.widgetType==="CheckBox" ||i.widgetType==="Radio"){
                                    val += element.valueText+'、'
                                }else{
                                    val += element.attrValue 
                                }
                            }
                        }
                    });
                }else{
                    if(obj.values[0].attrValue){
                        if(obj.rightUnit){
                            val = obj.values[0].attrValue + obj.rightUnit
                        }else{
                            if(obj.widgetType==="CheckBox"||obj.widgetType==="Radio"||obj.widgetType==="DropdownSelect"||obj.widgetType==="MultiLevelDropdownSelect"){
                                val = obj.values[0].valueText
                            }else if(obj.widgetType==="Address"){
                                obj.values[0].attrValue.forEach(j => {
                                    val += j+'、'
                                })

                            }else{
                                val = obj.values[0].attrValue
                            }
                        }
                    }else{
                        if(obj.rightUnit){
                            val = '-'+ obj.rightUnit
                        }else{
                            val = '-'
                        }
                    }
                }
            }
            return val
        },
        headerBtn(){

        }
    }
}
</script>