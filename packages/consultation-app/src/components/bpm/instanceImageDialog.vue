<template>
<div>
<popup  v-model="show" position="bottom" max-height="80%" @on-hide="ok()" should-scroll-top-on-show >
	<div class="weui-dialog__hd showContent">
        <strong class="weui-dialog__title">流程图</strong>
     </div>
     <div class="weui-dialog__bd" style="min-height: 50%">
		<img :src="__ctx+'/bpm/instance/flowImage?instId='+passConf.instanceId+'&defId='+passConf.defId"/>  
     </div>
      <div class="weui-dialog__ft">
        <a class="weui-dialog__btn weui-dialog__btn_default" style="text-align: center;" v-on:click="ok()">关闭</a>
      </div>
  </popup>
 </div>
</template>

<script>
import Vue from 'vue'
import { Timeline ,TimelineItem} from 'vux'

Vue.component('timeline', Timeline)

export default {
  props:['passConf','showDialog'],
  data :function () {
	    return {
	    	show:false,
	    	__ctx:""
	    }
   },
   methods: {
	    ok:function(){
	    	this.show = false;
	    	this.$emit('cancel',null);
	    },
	    getByStatus : function(status){
			 
		}
   },
  created :function(){
	this.show =this.showDialog;
	this.__ctx = Vue.__ctx;
  },
  components: {
	   Timeline,TimelineItem
  }
}

</script>

<style>
.recent {
		color: rgb(4, 190, 2)
	}
	p {
		color: #888;
		font-size: 0.8rem;
	}
	h4 {
		color: #666;
		font-weight: normal;
	}

</style>
