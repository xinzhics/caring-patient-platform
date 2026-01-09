<template>
<div>
	
 	<span v-bind:class="btnstyle" v-on:click="show=true"><slot></slot></span>
	<popup v-model="show" height="100%">
		<x-header :left-options="{showBack: false}">{{dialog.name}}</x-header>
		<div>
			<ab-cust-dialog-show-list v-if="dialog.style=='list'" :dialog="dialog" :initData="initData"> </ab-cust-dialog-show-list>
			<ab-cust-dialog-show-tree v-if="dialog.style=='tree'" :dialog="dialog" :initData="initData"> </ab-cust-dialog-show-tree>
		</div>
		<group :style="btnStyle">
			<flexbox :gutter="0">
		      	<flexbox-item>
					<x-button @click.native="getData()">确定</x-button>
				</flexbox-item>
		      	<flexbox-item>
					<x-button @click.native="show=false">取消</x-button>
		      	</flexbox-item>
	    	</flexbox>
	    </group>
	</popup>
</div>
</template>

<script>

import Vue from 'vue';
//注册AbCustDialogShowList
import AbCustDialogShowList from '@/components/form/abCustDialogShowList.vue';
Vue.component('abCustDialogShowList', AbCustDialogShowList);
//注册AbCustDialogShowTree
import AbCustDialogShowTree from '@/components/form/abCustDialogShowTree.vue';
Vue.component('abCustDialogShowTree', AbCustDialogShowTree);
import { Popup } from 'vux';
Vue.component('popup', Popup);
import { XButton } from 'vux';
Vue.component('x-button', XButton);
import { Group } from 'vux';
Vue.component('group', Group);
import { Flexbox, FlexboxItem } from 'vux';
Vue.component('flexbox', Flexbox);
Vue.component('flexbox-item', FlexboxItem);

/**
 * 自定义对话框，使用实例如下：
 * <ab-cust-dialog dialogKey="userSelector" :dialogSetting="dialogSetting" value-name="name" value-mobile="mobile"></ab-cust-dialog>
 * props:["dialogKey","dialogSetting","model"],//dialogKey:列表对话框的key;dialogSetting:对话框的配置修改;model赋值对象
 * <ab-cust-dialog dialogKey="orgSelector" :dialogSetting="dialogSetting" :model="obj" value-name="name" value-mobile="mobile"></ab-cust-dialog>
 * model如果是object，那么会根据value-xxx字段赋值这个object对象
 * model如果是list数组，那么会根据value-xxx组装成的json,push到model中。。model.push(json) 注意由于数组只能是push不会减少数组内容，所以数组类型下不支持初始化选项的行为
 */
export default {
	props:["dialogKey","dialogSetting","model"],//dialogKey:列表对话框的key;dialogSetting:对话框的配置修改
	data: function () {
	  return {
		  show:false,
		  btnstyle:"weui-btn weui-btn_mini weui-btn_primary fa fa-search",
		  btnStyle:{
			  "margin-top":window.innerHeight*0.80+"px"//这是List的高度
		  },
		  dialog:{},
		  initData:[],
		  valueMap:{}
	  }
	},
	created : function(element){
		var that = this;
		//获取对话框数据
    	var url = Vue.__ctx + "/form/formCustDialog/getObject";
    	var post = Vue.baseService.postForm(url,{
    		key : this.dialogKey
		});
    	Vue.tools.getResultData(post,function(data){
    		that.dialog = data;
    		if(that.dialogSetting){
    			Object.assign(that.dialog,that.dialogSetting);
    		}
    		//根据不同类型，按钮计算地址是不一样的。怪scroller..
    		if(data.style=='tree'){
    			that.btnStyle["margin-top"] = window.innerHeight*0.4+"px";
    		}
    	});
    	
    	//映射关系
		for(var key in this.$attrs){
			var val = this.$attrs[key];
			// 找到value开头的赋值配置
			if (key.indexOf("value-") !== 0) {
				continue;
			}
			var name = key.replace("value-", "");
			this.valueMap[name] = val;
		};
		
		this.initData = this.getInitData();
    	
	},
	methods: {
		getData:function(){
			var that = this;
			var popup = null;//获取到popup这层组件
			this.$children.forEach(function(item){
				if(item.$vnode.tag.indexOf("popup")>-1){
					popup = item;
				}
			});
			var dialog = null;//获取组件对象
			popup.$children.forEach(function(item){
				if(that.dialog.style==="list"){
					if(item.$vnode.tag.indexOf("abCustDialogShowList")>-1){
						dialog = item;
					}	
				}
				if(that.dialog.style==="tree"){
					if(item.$vnode.tag.indexOf("abCustDialogShowTree")>-1){
						dialog = item;
					}	
				}
			});
			var data = dialog.getData();//调用组件函数
			this.handleData(data);
			this.show = false;//关闭popup
		},
		//处理返回数据
		handleData:function(data){
			var valueMap = this.valueMap;
			if(Array.isArray(this.model)){//model是列表
				var list = this.model;
				
				data.forEach(function(item){
					if(JSON.stringify(valueMap)==="{}"){//无映射关系则把数据全返回
						list.push(item);
						return;
					}
					
					var json = {};
					//处理映射关系
					for(var key in valueMap){
						var val = valueMap[key];
						//如果val是a.b这样的，则需要对json.a初始化，不然直接操作json.a.b会报错
						var strs = val.split(".");
						var exp = "json";
						for (var i=0;i<strs.length-1;i++){
							exp = exp + "."+strs[i];
							if(eval("!"+exp)){//为空则初始化
								eval(exp+" = {};");
							}
						}
						eval("json."+val+" = item[key];");
					}
					list.push(json);
				});
			}else{//组件是对象
				for(var key in valueMap){
					var val = valueMap[key];
					var list = [];
					data.forEach(function(item) {
						list.push(item[key]);
					});
					this.$set(this.model,val,list.join(','));
				}
			}
		},
		getInitData:function(){
			if(Array.isArray(this.model)){//数组不需要初始化，不会对数组进行删除行为
				return [];
			}
			
			var initData = [];
			var json = null;
			for(var key in this.valueMap){
				var data = this.model[key];
				if(!data){
					continue;
				}
				if(!json){
					json = {};
				}
				eval("json[key]=data");
			}
			
			if(!json){
				return initData;
			}
			
			//切割json中的,当作多选
			for(var key in json){
				var val = json[key];
				var list = val.split(",");
				var index = 0;
				
				list.forEach(function(item) {
					if(!initData[index]){
						initData[index] = {};
					}
					initData[index][key] = item;
					index++;
				});
			}
			
			return initData;
		}
	},
	mounted:function(){
		var $vm = this;
		$vm.$el.removeAttribute("class")
	}
}
</script>
