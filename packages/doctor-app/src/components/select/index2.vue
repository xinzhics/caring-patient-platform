<template>
    <div>
        <van-field v-model="showText" :placeholder="placeholder||'请选择'" readonly @click="show = !show"/>
        <van-popup v-model="show" position="bottom" @closed="close">
            <van-picker
                    :columns="options"
                    show-toolbar
                    :title="$attrs.label"
                    :default-index="sele"
                    @confirm="onConfirm"
                    @cancel="show = !show"
                    @change="onChange"
            />
        </van-popup>
    </div>
</template>

<script>
    import {parseTime} from "@/components/utils/index.js";

    export default {
        props: {
            placeholder: {
                type: String
            },
            columns: {},
            label: {},
            selVal: {},
            value: {},
            type: {},
            required: {},
            selInd: {
                type: Number
            },
            data:{},
            attr:'',
            fypc:{
                type: String
            },
            fyts:{
                type: String
            },
            fyzq:{
                type: String
            },
        },
        data() {
            return {
                show: false,
                sourceData: {},
                selText: "",
                resultData: '',
                selectIndex:'',
                number:0,
                obj:{},//本页面使用的对象
            };
        },
        methods: {
            validate() {
                let valRes = !(this.required && this.showText);
                return valRes;
            },
            onConfirm(value, ind) {
                Vue.set(this,'number',1);
                Vue.set(this,'selectIndex',ind);
                Vue.set(this,'selInd',ind);
                if(this.fypc){
                    this.data[this.attr] = ind+1;
                }else if(this.fyts){
                    Vue.set(this.data,this.attr,ind+1);
                }else {
                    Vue.set(this.data,this.attr,ind);
                }
                this.show = !this.show;
            },
            onChange(picker) {
                this.selectIndex = picker.getIndexes()[0];
            },
            initOptions(){
                let columns = this.columns;
                let arr = [];
                for (let i = 0; i < columns.length; i++) {
                    const column = columns[i];
                    arr.push(column.label);
                }
                return arr;
            },
            initPage(){
                let s = JSON.stringify(this.data);
                this.obj = JSON.parse(s);
            },
            close(){
                Vue.set(this,'number',0);
            },
            isRealNum (val) {
                // 先判定是否为number
                if (typeof val !== 'number') {
                    return false;
                }
                if (!isNaN(val)) {
                    return true;
                } else {
                    return false;
                }
            },

        },
        mounted() {
            this.initPage();
        },
        computed: {
            showText: {
                set(value) {
                    this.selText = value;
                },
                get() {
                    var text = '';
                    let column = this.columns;
                    if(this.number == 0){
                        if(this.isRealNum (this.selInd) && this.selInd>=0){
                            if(this.fyzq && this.selInd>1){
                                text = column[1].label;
                            }else if(this.fyts){
                                text = column[this.selInd<0?0:this.selInd].label;
                            }else {
                                text = column[this.selInd].label;
                            }
                        }
                    }else {
                        if(this.isRealNum (this.selectIndex)){
                            text = column[this.selectIndex].label;
                        }
                    }
                    return text;
                }
            },
            options(){
                return this.initOptions();
            },
            sele(){
                return this.selInd;
            }
        }
    };
</script>
<style scoped lang="less">
    .van-field {
        padding: 0;
        padding-right: 20px;
        text-align: right;
    }

    /deep/ .van-field__control {
        text-align: right !important;
    }
</style>
