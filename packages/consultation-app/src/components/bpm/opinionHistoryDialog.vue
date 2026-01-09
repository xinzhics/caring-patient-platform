<template>
<div>
<popup  v-model="show" position="bottom" max-height="80%" @on-hide="ok()">
	<div class="weui-dialog__hd showContent">
        <strong class="weui-dialog__title">审批历史</strong>
     </div>
     <div class="weui-dialog__bd">
		 <timeline>
			<timeline-item v-for="(opinion,index) of opinionList"  :key="index">
				<h4 :class="!opinion.approveTime ?'recent':''">【{{opinion.taskName}}】{{opinion.approverName}}（{{getByStatus(opinion.status)}}）</h4>
				<p v-if="opinion.approveTime">  {{opinion.approveTime}}</p>
				<p v-if="opinion.approveTime"> 意见：{{opinion.opinion}}</p>
			</timeline-item>
		</timeline>
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
	    	opinionList:[],
	    	show:false
	    }
   },
   methods: {
	    ok:function(){
	    	this.show = false;
	    	this.$emit('cancel',null);
	    },
	    getByStatus : function(status){
			if(status == 'create')return "待审批";
			if(status == 'start')return "发起流程";
			if(status == 'against')return "反对";
			if(status == 'reject')return "驳回";
			if(status == 'manualEnd')return "人工终止";
			if(status == 'end')return "终止";
			if(status == 'agree')return "同意";
			return status;
		}
   },
  created :function(){
	  var defer = Vue.baseService.postForm(Vue.__ctx+"/bpm/instance/getInstanceOpinion",{instId:this.passConf.instanceId});		
	  var vm = this;
	  Vue.tools.getResultData(defer,function(data){
		  vm.opinionList = data;
		  vm.opinionList.reverse();
		})
	this.show =this.showDialog;
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
