<template>
	<div>
   	<select class="weui-input" v-model="value" v-on:input="$emit('input', $event.target.value)"
   		 v-ab-permission="AbPermission" >
   		 <option value="">请选择</option>
   		<option v-for="dict in dataList" :value="dict.key">{{dict.name}}</option>
   	</select>
   	</div>
</template>

<script>
import Vue from 'vue'
import bpmService from '@/service/bpm/bpmService'


export default {
  props: ['dictKey','value','AbPermission'],
  methods: {
    
  },
  data :function () {
	    return {
	      dataList:[],
	      currentValue:""
	    }
	},
  	created : function(){
		var vm = this;
  	 	if(!vm.dictKey)return;
  	  	
  		var defer = Vue.baseService.get(Vue.__ctx+"/sys/dataDict/getDictData?dictKey="+this.dictKey);
  		Vue.tools.getResultData(defer,function(data){
	 		vm.dataList = data;
		},"alert");
 	}
  
}
</script>
