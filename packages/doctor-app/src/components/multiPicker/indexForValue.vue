<template>
    <div>
        <van-cell :required="!!required" :title="title" is-link>
            <template slot="default">
                <van-field v-model="showText" :placeholder="placeholder||'请选择'" readonly @click="show = !show" />
            </template>
        </van-cell>
        <van-popup v-model="show" position="bottom">
            <van-picker show-toolbar ref="multiPicker" :columns="initColumns" @confirm="onConfirm" @cancel="show = !show" />
        </van-popup>
    </div>
</template>
<script>
    import {
        isNotEmptyArr
    } from "@/components/utils";
    export default {
        name: "multi-picker",
        props: {
            title: {
                type: String
            },
            placeholder: {},
            columns: {
                type: Array
            },
            required: {},
            data: {},
            attr: ""
        },
        data() {
            return {
                selArr: [],
                show: false,
                isInit: false,
                deep: 1,
                showColumns: [],
                refs: "multiPicker",
                selectId: '',
                flag: true
            };
        },
        methods: {
            validate() {
                let valRes = !(this.required && isNotEmptyArr(value));
                return valRes;
            },
            onConfirm() {
                let values = this.$refs.multiPicker.getValues();
                let v = "";
                for (let i = 0; i < values.length; i++) {
                    const value = values[i];
                    v += value;
                }
                this.$emit("input", v);
                this.show = false;
            },
            initPickColumns() {
                let self = this;
                let options = self.columns;
                let split = this.showText.split(".");
                if (options && options.length > 0) {
                    for (let i = 0; i < options.length; i++) {
                        const ele = options[i];
                        if (ele && ele.values && ele.values.length > 0) {
                            for (let j = 0; j < ele.values.length; j++) {
                                const element = ele.values[j];
                                if (i == 0 && parseInt(split[i]) == parseInt(element)) {
                                    Vue.set(ele, 'defaultIndex', j)
                                    break;
                                }
                                if (i != 0 && parseFloat('.' + split[i]) == parseFloat(element)) {
                                    Vue.set(ele, 'defaultIndex', j)
                                    break;
                                }
                            }
                        }
                    }
                }
                return options;
            },
        },
        computed: {
            showText() {
                if (this.value && this.value == "0") {
                    return "";
                } else {
                    return this.value + '';
                }
            },
            value() {
                let datum = this.data[this.attr];
                if (datum) {
                    return datum;
                } else {
                    return '';
                }
            },
            initColumns() {
                return this.initPickColumns();
            }
        },
        mounted: function() {}
    };
</script>
<style lang="less" scoped>
    /deep/.van-field__control {
        text-align: right !important;
    }
</style>