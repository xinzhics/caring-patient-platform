<template>
	<div class="ztree"></div>
</template>

<script>
import Vue from 'vue';
import '@/assets/ztree/css/zTreeStyle.css';
import "@/assets/ztree/jquery.min.js";
import "@/assets/ztree/jquery.ztree.all.min.js";

export default {
	props:["dialog"],//dialog:树对话框
  	data : function() {
	    return {
	    	treeObj:null
	    }
  	},
  	mounted : function(){
  		this.loadTree();
  	},
  	methods:{
  		loadTree:function(){
  			var that = this;
  			
  			var setting = {
				data: {
					simpleData: {
						enable: true,
						idKey: this.dialog.treeConfig.id,
						pIdKey: this.dialog.treeConfig.pid
					},
					key:{
						name:this.dialog.treeConfig.showColumn,
						title:this.dialog.treeConfig.showColumn
					}
				}
			};
			
  			if(this.dialog.multiple){
  				setting.check = {
					enable: true,
					chkboxType: {"Y": "", "N": ""}	
  				}
  				//设置点击就check的事件
  				setting.callback = {
  					onClick: function(event, treeId, treeNode){
  						that.treeObj.checkNode(treeNode,!treeNode.checked,false);
  					}
  				}
  			}
  			
  			var url = Vue.__ctx +"/form/formCustDialog/treeData_"+this.dialog.key;
  	    	var post = Vue.baseService.postForm(url);
  	    	post.then(function(data){
  	    		$(that.$el).attr("id",new Date().getTime());//设置个随机id
  	    		that.treeObj = $.fn.zTree.init($(that.$el), setting, data);
  	    		that.treeObj.expandAll(true);
  	    	});
  		},
	  	//获取选中的结果
		getData : function(){
			var data = [];
			var that = this;
			var rows = [];
			
			if (this.dialog.multiple) {
				rows = this.treeObj.getCheckedNodes();
			} else {
				rows = this.treeObj.getSelectedNodes();
			}
			
			rows.forEach(function(row){
				var json = {};
				that.dialog.returnFields.forEach(function(field){
					json[field.returnName] = row[field.columnName];
				});
				data.push(json);
			});
			console.info(data);
			return data;
		}
	},
	watch : {
		//监听dialog对象
		dialog : function(newVal,oldVal){
			if(!newVal||newVal===oldVal){
				return;
			}
			this.loadTree();
		}
	}
}
</script>

<style>
body {
	font-family: Helvetica, sans-serif;
}
</style>
