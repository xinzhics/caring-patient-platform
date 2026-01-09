<template>

</template>

<script>
import Vue from 'vue'
export default {
  props:[],
  data :function () {
	    return {
	    	user:{account:"",password:""},
	    	show : false
	    }
   },
   methods: {
	    ok:function(){
	    	if(!this.user.account || !this.user.password){
	    		Vue.tools.toast("请完善登录信息","warn");
	    		return ;
	    	}
	    	var post = Vue.baseService.postForm(Vue.__ctx+"/org/login/valid",this.user);
	    	var vm = this;
	 		Vue.tools.getResultData(post,function(){
	 			vm.show = false;
	 			Vue.tools.toast("身份认证成功","success");
	 		},"toast")
	    },
		cancel:function(){
			this.show = false;
			this.$emit('cancel',this.flowParam);
		}
   },
  created :function(){
	  
  }
}


function createDialogVM(Vue){
	if (typeof document === 'undefined') {
	    console.error('[VUX] Alert plugin cannot be used in ssr.')
	    return
	  }
	
	
	
	
}

</script>
