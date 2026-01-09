import Vue from 'vue'
import {baseService,arrayTools} from '@/service/common/baseService'
import tools from '@/service/common/tools'
import formValidator from '@/service/form/formValidator'


import abCheckbox from '@/components/form/abCheckbox.vue'
import abDict from '@/components/form/abDict.vue'
Vue.component('abCheckbox', abCheckbox);
Vue.component('abDict', abDict);
import AbUpload from '@/components/form/abUpload.vue';
Vue.component('abUpload', AbUpload);
import { Datetime } from 'vux'
Vue.component('datetime', Datetime)

var FormService = {};
/**
 * 混入html中的组件定义的参数变量
 */
FormService.initCustFormFu = function(data,Vue){
	
	var html = data.form.formHtml;
	window.custFormComponentMixin = {};
	var custComponentData = data;
		
	// 处理 custFormComponentMixin
	if(html.indexOf("<script>") != -1){
		var reg=/<script[^>]*?>[\s\S]*?<\/script>/i;
		var patten = reg.exec(html)	
		var script = patten[0];
		
		var len1=script.indexOf(">")
		var len2=script.lastIndexOf("<")
		var custScriptStr = script.substring(len1+1,len2);
		html = html.replace(reg,"");
		
		if(custScriptStr){
			try{
				// TODO严格模式下问题
				eval(custScriptStr);// eslint-disable-line
			}catch(e){
				console.error("解析表单脚本异常",custScriptStr,e);
			}
		}
		
		// 用于第三层子表数据暂存 _temp.xxx = [] //则该子表的数据暂存
		custComponentData.subTempData = {};
		// 三层子表对话框的展示 _subTable.xxx = true;//则弹框中
		custComponentData.subTableDialog={};
	}
	
Vue.component('ab-custom-form',{
    	name:"customForm",
		mixins: [custFormComponentMixin],
		template: html,
		data:function(){
			return custComponentData;
		},
		methods:{
			// 展示子表 TODO 改成组件 而不是subTempadata，subTableDialog这种，并添加确认框
			showSubTable: function(dataScope,tableName){
				if(!dataScope[tableName+"List"]){
					this.$set(dataScope,tableName+"List",[]);
				}
				
				this.$set(this.subTempData,tableName+"List",dataScope[tableName+"List"]);
				this.$set(this.subTableDialog,tableName,true);
			}
		}
	});
};




/**
 * <pre>
 * 表单增加子表指令
 * abSubAdd: 为一个list push 一个对象
 * Arr{0] = list
 * Arr[1] = object
 * 会将 object extends 然后push 到 list 中
 * </pre>
 */
Vue.directive('SubAdd', {
	  bind: function (el,binding,vnode) {
		  initEmptySub(binding,vnode);
		  
		  addEvent(el, 'click', function(e) {
			  if(binding.value && binding.value.length == 2){
				  binding.value[0].push(Vue.tools.extend( binding.value[1]));
			  }
	      });
	  }
	})
	
Vue.directive('SubDel', {
	  bind: function (el,binding) {
		  addEvent(el, 'click', function(e) {
			  if(binding.value && binding.value.length == 2){
				  arrayTools.del(binding.value[1], binding.value[0]);
			  }
	      });
	  }
	})
	
function initEmptySub(binding,vnode){
	if(!binding.value && binding.value.length !== 2){
		return ;
	}
	if(binding.value[0]) return ;
	//[data.applyOrderInfo.customerRelList,initData.applyOrderInfo.customerRel]
	//取出data.applyOrderInfo.customerRelList
	var dataPath = binding.expression.split(",")[0].substring(1);
	var index = dataPath.lastIndexOf(".");
	var tableKey = dataPath.substring(index+1);
	var prePath = dataPath.substring(0,index);
	
	binding.value[0] = [];
	eval("vnode.context.$set(vnode.context."+prePath+",\""+tableKey+"\",binding.value[0])");
	return ;
	
	var scope = vnode.context;
	
	for (var i = 0; i < array.length; i++) {
		if(i==array.lenght)
		scope = scope[array[i]];
	}
	
	for(var i=0,k;k=array[i++];){
		scope = scope[k];
	}
	return value;
	
}


/**
 * 校验,依赖 v-model 或者 组件的 input 事件
 * 
 */
Vue.directive("AbValidate",{
	bind:function(el,binding,vnode){
		 formValidator.doValidate(el,binding,vnode);
		 
		 var validateHandler = function(e){
			 if(e.target && e.target !== el)return;
			 
			 var value = e.target? e.target.value : e;
			 formValidator.doValidate(el,binding,vnode,value);
		 }
		 
		 if(vnode.componentInstance){
			 vnode.componentInstance.$on("input",function(value){
				 validateHandler(value);
			 })
		 }else{
			 addEvent(el, 'input', validateHandler);
		 }
		 
	},
	update :function(el, binding, vnode,oldVnode) {
		// TODO 先这么搞、等后面找到取值的合理方法、再利用事件或者属性拦截器来解决    model值改变无法通知到校验器的问题
		// 这样做会导致所有控件全部执行一次校验尝试
		if(el.value === undefined )return;
		
		var oldValue = el.dataset._oldValue;
		if(oldValue !== undefined && oldValue !== el.value){
			 formValidator.doValidate(el,binding,vnode,el.value);
		}
		el.dataset._oldValue = el.value;
	}
})

/**
 * 获取表单的校验情况
 * 自定义表单为<custForm> 组件。当前componentScope 为页面的scope
 * 所以要先找到 custForm 组件的作用域
 * return 
 */
FormService.getValidateMsg = function(componentScope){
	var errorStr ="";
	if(!componentScope.$validity){
		 return errorStr;
	}
	
	for(var key in componentScope.$validity){
		if(errorStr){errorStr = errorStr + "<br/>"}
		errorStr = errorStr +"【"+ key +"】"+ ":" + componentScope.$validity[key];
	}
	
	if(errorStr){
		errorStr = "<div style='text-align:left'>"+errorStr+"</div>"
	}
	
	return errorStr;
}


/**
 *  基础权限，对控件做只读、必填、无权限, @v-model 必须
 *  v-ab-permission="permission.kjcs.cskj.xb" v-model=""
 *  
 *  判断是否有展示权限，无则移除dom
 *  v-ab-permission:show="permission.kjcs.cskj.xb" 
 *  
 *   *  判断是否有编辑权限，无则移除dom
 *  v-ab-permission:edit="permission.kjcs.cskj.xb" 
 */
Vue.directive("AbPermission",{
	inserted:function(el,binding,vnode){
		handleElementPermission(el,binding,vnode);
	},
	update:function(el,binding,vnode){
		handleElementPermission(el,binding,vnode);
	}
})

/**
 * 处理控件权限
 * 
 * @param el
 * @param binding
 * @param vnode
 * @returns
 */
function handleElementPermission(el,binding,vnode){
	if(!binding.value)return;
	// 只成功处理一次
	if(el.dataset.hasInited)return ;
	
	if(binding.arg){
		handleSpecialPermision(el,binding,vnode);
		return;
	}
	
	// 处理必填权限
	if(binding.value=="b"){
		vnode.elm.required =true;
	}
	else if(binding.value === "w"){
		
	}
	// 
	else if(binding.value === "r"){
		vnode.elm.readOnly = true;
		vnode.elm.disabled = true;
	}
	else if(binding.value === "n"){
		vnode.elm.remove();
	}
	
	el.dataset.hasInited = true;
}

function handleSpecialPermision(el,binding,vnode){
	
	//无展示权限则移除掉标签
	if(binding.arg ==="show" && binding.value=="n"){
		vnode.elm.remove();
	}

	//无编辑权限则移除掉控件
	if(binding.arg ==="edit" && binding.value!=="w" && binding.value!=="b"){
		vnode.elm.remove();
	}
	
	el.dataset.hasInited = true;
}


	
function addEvent(element, event, listener) {
    if (element.addEventListener) {
      element.addEventListener(event, listener, false);
    } else if (element.attachEvent) {
      element.attachEvent('on' + event, listener);
    } else {
      element['on' + event] = listener;
    }
  }
export default  FormService


