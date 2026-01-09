<template>
  <div id="app">
     <router-view/>
  </div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'
import Api from '@/api/Content.js'
import ApiT from '@/api/doctor.js'
import {Base64} from 'js-base64'
import WebIM from "./webIM/WebIM";


export default {
  name: 'App',
  data:function(){
	  return {
	  }
  },
  created:function(){

  },
  mounted() {
    window.localStorage.setItem('scanCodeUrl', location.href)
  },
  methods:{

  }
}
</script>

<style lang="less">
@import '~vux/src/styles/reset.less';
// #app{
  // width:100vw;
  // height: 100vh;
  // background-color: #fafafa;

// }
#app{
  // width: 100vw;
  // height: 100vh;
  // overflow: hidden;
  // overflow-y: scroll;
  background: #f5f5f5;
}

.weui-toast__content{
  color: #fff!important;
}
body {
  background-color: #fbf9fe;
}
.main{
  background-color:#f5f5f5;
}
.vux-header{
    border-bottom:1px solid rgba(102,102,102,0.1);
    margin-bottom:10px;
}
.van-cell{
  padding: 17px 16px;
}
.van-cell__title,.vux-label,.weui-label{
  color: #666;
  font-size: 14px!important;

}
.van-cell__value,.weui-cell__primary{
  color: #999;
}
.weui-cells{
  margin-top:10px!important;
  font-size: 14px!important;
}
.weui-cell{
  padding: 17px 16px!important;
}
.weui-btn{
  border-radius: 30px!important;

}

.vux-label,.weui-label{
  font-size: 14px!important;
}
.weui-switch:after{
  left: 6px!important;
}
.weui-switch:checked{

  border-color: #5294F7!important;
 background-color: #5294F7!important;
}
.weui-grid__label{
  color: #666!important;
}
.van-field__label{
  width: 9.2em;
}
.classTextarea>.van-cell>.van-cell__value{
  border:1px solid rgba(102,102,102,0.1);
  padding: 5px;
  text-align: left;
}
.textareaCont::before{
  border-top: none!important;
  // border: 1px solid rgba(102,102,102,0.1);
}
.van-button--primary{
  background-color:rgba(102,102,102,0.85);
  border: 1px solid rgba(102,102,102,0.85);
}
.vux-number-selector-sub{
  border-radius: 20px 0 0 20px!important;
}
.vux-number-selector-plus{
  border-radius: 0 20px 20px 0!important;
}
.vux-number-input,.vux-popup-picker-value{
  color: #999!important;
}


.btn{
  background-color:rgba(102,102,102,0.85)!important;
  border: 1px solid rgba(102,102,102,0.85)!important;
}


</style>
<style>
.weui-btn_primary{
  background-color: #3F86FF;
}
.checkBox input[type=checkbox]:checked{
  background-color: #999!important;
}
.vue-directive-image-previewer-body img{
    cursor: pointer;
    top: 20%!important;
    left: 10%!important;
    width: 80%!important;
    height: auto!important;
    transition: all 0ms ease 0ms;
}
.setBnts{
  border: none!important;
  border:1px solid rgba(0, 0, 0, 0.2)!important
  /* // border: 1px solid rgba(102,102,102,0.1); */
}
.weui-btn:after{
  border: none!important;
}
.mypicker>.weui-cell{
     padding:0px!important
  }
</style>
