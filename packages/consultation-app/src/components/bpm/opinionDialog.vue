<template>
<div>
<x-dialog v-model="show" style="width:100%">
	<div class="weui-dialog__hd showContent">
        <strong class="weui-dialog__title">备注意见</strong>
     </div>
     <div class="weui-dialog__bd">
		 <textarea  style="width: 261px; height: 130px;" v-model="flowParam.opinion" class="weui-input"  ab-validate="{required:true}"></textarea>         
     </div>
      <div class="weui-dialog__ft">
        <a class="weui-dialog__btn weui-dialog__btn_default" v-on:click="ok()">确定</a>
        <a class="weui-dialog__btn weui-dialog__btn_default"  v-on:click="cancel()">取消</a>
      </div>
  </x-dialog>
 </div>
</template>

<script>
import Vue from 'vue'
export default {
  props:['passConf','showDialog'],
  data :function () {
	    return {
	    	flowParam:this.passConf,
	    	show:false
	    }
   },
   methods: {
	    ok:function(){
	    	if(!this.flowParam.opinion){
	    		Vue.tools.toast("请完善意见","warn");
	    		return ;
	    	}
	    	
	    	this.show = false;
	    	this.$emit('callback',this.flowParam);
	    },
	    setOpinion : function(str){
			this.flowParam.opinion =  str;
		},
		cancel:function(){
			this.show = false;
			this.$emit('cancel',this.flowParam);
		}
   },
   watch:{
	   	showDialog:function(value,oldValue){
	   		this.show = value;
	    }
   },
  created :function(){
  	 this.usefulOpinion = [ "同意", "请注意，情况复杂！", "情况紧急，请尽快处理！", 	"驳回"  ];
  	 this.show = this.showDialog;
  }
}


</script>
