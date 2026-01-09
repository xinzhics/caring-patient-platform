import Vue from 'vue'
import {baseService,arrayTools} from '@/service/common/baseService'
import tools from '@/service/common/tools'
import formService from '@/service/form/formService'
import opinionDialog from '@/components/bpm/opinionDialog'
import taskOpinionHistory from '@/components/bpm/opinionHistoryDialog.vue'
import instanceImageDialog from '@/components/bpm/instanceImageDialog.vue'
Vue.component('opinionDialog', opinionDialog)
Vue.component('opinion-history-dialog', taskOpinionHistory)
Vue.component('instance-image-dialog', instanceImageDialog)

var BpmService = {};
/** taskId,instanceId,defId,bpmTask,form,buttons**/
BpmService.data ;
//初始化动作
BpmService.init = function(params,fn){
	this.data = params;
	var dataUrl = Vue.__ctx + "/bpm/task/getTaskData";// taskId=xxx
	if(!params.taskId){
		dataUrl =  Vue.__ctx +"/bpm/instance/getInstanceData";//?defId="+defId+"&instanceId="+instanceId+"&readonly="+param.readonly;
	}
	
	var defer = baseService.postForm(dataUrl,params);
	Vue.tools.getResultData(defer,function(data){
		if(fn)fn(data);
		Vue.tools.extend(data,BpmService.data);
	},"alert",function(){
		Vue.$router ? this.$router.back() : window.history.back();
	});
	
};

//获取表单的数据
BpmService.getFormData = function(custFormContext,button){
	var validateForm = "start,agree,oppose".indexOf(button.alias)!=-1;
		
	var frmType = this.data.form.type;
	if(frmType=='INNER'){
		var errorMsg = formService.getValidateMsg(custFormContext);
		if(errorMsg && validateForm){
			Vue.$vux.alert.show({  title: '表单校验提示',  content: errorMsg})
			return false; 
		}
		return custFormContext.data;
	}else{
		alert("TODO 移动端URL表单")
	}
};

BpmService.getSubmitFlowParam = function(){
	if(!this.data){
		alert("error");
		return;
	}
	
	if(this.data.task) {
		return {"taskId":this.data.taskId,instanceId:this.data.task.instId};
	}
	
	return {
			defId: this.data.defId,
			instanceId: this.data.instanceId
		}
}
/**
 * 流程按钮解析。
 * 关于按钮样式，对话框宽高属性，不做可配置行，因为前段无法统一，
 * 但是、请求资源路径要求一致，比如/bpm/task/   /bpm/instance.
 * 前缀自己前段控制
 */
Vue.component('bpmButtons',{
	 props: ['buttons'],
	 data:function(){
		 return { }
	 },
	 methods:{
		 buttonClick : function(button){
				this.$vux.loading.show({ text: 'Loading' })
					
				//执行前置js
				if(!this.execuFn(button.beforeFn)){
					 return;
				 }
				//获取流程数据
				var custFormContext = getCustFormComponent(this.$parent);
				
				//如果自定义表单定义了 custValid表单组件的function 则执行返回 false 则不提交
				if(custFormContext.custValid){
					if(custFormContext.custValid()===false){
						this.$vux.loading.hide();
						return;
					}
				}
				
				var busData = BpmService.getFormData(custFormContext,button);
				if(busData === false){//获取数据失败
					this.$vux.loading.hide();
					return;
				}
				var scope = this;
				var flowData = BpmService.getSubmitFlowParam();
				flowData.data = busData;
				flowData.action =  button.alias;
			   
				//获取更多完成动作配置
				if( button.configPage){
					this.$vux.loading.hide();
					var componentKey = "opinion-dialog";
					if( "taskOpinion" === button.alias){ componentKey = "opinion-history-dialog" }
					if( "flowImage" === button.alias){ componentKey = "instance-image-dialog" }
					
					var formContext = this.$parent;
					if(formContext.dynamicComponent.key === componentKey && componentKey != "opinion-dialog" ){
						formContext.dynamicComponent.show = true;
						return;
					}
					
					formContext.dynamicComponent = {
						show:true,
						key :componentKey,
						flowParam:flowData,
						callback:function(data){
							scope.postAction(data);
						}
					};
				}else{
					scope.postAction(flowData);
				}
			},
			postAction : function(flowData){
				this.$vux.loading.show({ text: 'Loading' })
				// 执行动作
				var url =  Vue.__ctx + (flowData.taskId ? "/bpm/task/doAction":"/bpm/instance/doAction");
				var defer = Vue.baseService.post(url,flowData);
				Vue.tools.getResultMsg(defer,function(data){
					Vue.$vux.loading.hide();
					Vue.$router ? this.$router.back() : window.history.back();
				},function(){
					Vue.$vux.loading.hide();
				});
				
				
			},
			execuFn : function(fnStr){
				if(!fnStr)return true;
				
				var script = "var tempFunction = function(scope){ "+fnStr+"};"
				var result =  eval(script+"tempFunction(scope);");
				if(result===false) return false;
				return true;
			},
			getButtonCss:function(alias){
				var  buttonsCss = {
					"start":"icon-success-fa fa fa-send",
					"draft":"icon-primary-fa fa fa-clipboard",
					"save":"icon-primary-fa fa fa-clipboard",
					"agree":"icon-success-fa fa fa-check-square-o",
					"oppose":"icon-primary-fa  fa-close",
					"reject":"icon-danger-fa fa fa-lastfm",
					"reject2Start":"icon-danger-fa  fa fa-lastfm",
					"lock":"icon-primary-fa fa fa-lock",
					"unlock":"icon-primary-fa fa fa-unlock",
					"taskOpinion":"icon-primary-fa fa fa-navicon",
					"manualEnd":"icon-primary-fa fa fa-ioxhost",
					"flowImage":"icon-primary-fa fa fa-image",
				}
				if(buttonsCss[alias])return  buttonsCss[alias];
				return "";
			}
	 },                                                                                                     
	 template:'<tabbar style="position:fixed">                                                                                     						\
		 			<tabbar-item  v-for="btn in buttons" v-if="\'print,\'.indexOf(btn.alias)==-1">                                            \
						 <span slot="icon" :class="getButtonCss(btn.alias)" v-on:click="buttonClick(btn)"></span>													\
					     <span slot="label" v-on:click="buttonClick(btn)">{{btn.name}}</span>																		\
			 		</tabbar-item>                                                                          							\
	 		  </tabbar>',                                                                                   
	
});

function getCustFormComponent(pageComponent){
	for(var i=0,c;c=pageComponent.$children[i++];){
		if(c.$options._componentTag==="ab-custom-form"){
			return c;
		}
	}
	// 不向下递归
	console.error("页面中找不到 cust-form 的组件！！！");
	return null;
}


export default  BpmService


