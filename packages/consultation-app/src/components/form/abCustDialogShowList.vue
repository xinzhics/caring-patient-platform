<template>
<div>
	<ab-scroller :url="url" :list="rows" :pageSize="dialog.pageSize" :conditions="conditions">
		<x-table :cell-bordered="true"  style="background-color:#fff;">
		<thead style="background-color: #F7F7F7">
			<tr>
				<th></th>
				<th v-for="field in dialog.displayFields">
					{{field.showName}}
				</th>
			</tr>
		</thead>
		<tbody>
			<tr v-for="row in rows" v-on:click="check(row)">
				<td><input type="checkbox" v-model="row.checked"> </td>
				<td v-for="field in dialog.displayFields">{{row[field.columnName]?row[field.columnName]:"-"}}</td>
			</tr>
		</tbody>
		</x-table>
	</ab-scroller>
</div>
</template>

<script>
import Vue from "vue";
import { XTable } from 'vux';
Vue.component('x-table', XTable);
import { LoadMore } from 'vux';
Vue.component('loadMore', LoadMore);
import { XButton } from 'vux';
Vue.component('x-button', XButton);

export default {
	name:"abCustDialogShowList",
 	props:["dialog","initData"],//dialog:列表对话框
	data: function () {
	  return {
		  listData:[],//列表数据
		  url:"",//获取数据的url地址
		  rows : [],
		  conditions:[]
	  }
	},
	created : function(){
		if(this.dialog){
			this.url = Vue.__ctx +"/form/formCustDialog/listData_"+this.dialog.key;
		}
		//拼装查询条件
		var that = this;
		this.dialog.conditionFields.forEach(function(field){
			var json = {};
			json.key = that.getId(field);
			json.value = field.showName;
			that.conditions.push(json);
		});
	},
	methods: {
		check : function(row){
			//单选
			if(!this.dialog.multiple){
				this.rows.forEach(function(item){
					Vue.set(item, "checked", false);
				});
			}
			Vue.set(row, "checked", !row.checked);
		},
		rowStyle : function(row){
			if(row.checked){
				return {
					backgroundColor:"#F7F7F7"
				}
			}
			return {};
		},
		//获取选中的结果
		getData : function(){
			var data = [];
			var that = this;
			this.rows.forEach(function(row){
				if(!row.checked){
					return;
				}
				var json = {};
				that.dialog.returnFields.forEach(function(field){
					json[field.returnName] = row[field.columnName];
				});
				data.push(json);
			});
			console.info(data);
			return data;
		},
		/**
		 * 判断两个json是否相等 ps：只比较只b中存在的字段
		 */
		jsonEqual : function(a, b) {
			if (a === b) {
				return true;
			}
			var allEq = true;//全相等
			var hasOneEq = false;// 有一个字段相等
			for ( var key in b) {
				if (a[key] && b[key] && b[key] !== a[key]) {
					allEq = false;
					break;
				}
				if (a[key] && b[key] && b[key] === a[key]) {
					hasOneEq = true;
				}
			}
			return allEq&&hasOneEq;
		},
		//获取查询字段的id
		getId : function(field) {
			var id = field.columnName + "^";
			if (field.dbType === "varchar") {
				id += "V";
			}
			if (field.dbType === "number") {
				id += "N";
			}
			if (field.dbType === "date") {
				id += "D";
			}
			id += field.condition;
			return id;
		}
	},
	watch : {
		//监听dialog对象
		dialog : function(newVal,oldVal){
			if(!newVal||newVal===oldVal){
				return;
			}
			this.url = Vue.__ctx +"/form/formCustDialog/listData_"+newVal.key
		},
		rows : function(newVal,oldVal){
			console.info(Vue.arrayTools);
			if(!this.initData){
				return;
			}
			var that = this;
			this.rows.forEach(function(row){
				that.initData.forEach(function(item){
					if(item.inited){
						return;
					}
					
					//处理返回字段的返回名映射为字段名
					var itemTemp = {};
					
					for(var key in item){
						var val = item[key];
						var isMatch = false;
						that.dialog.returnFields.forEach(function(field){
							if(field.returnName===key){
								itemTemp[field.columnName] = val;
								isMatch = true;
							}
						});
						if(!isMatch){
							itemTemp[key] = val;
						}
					}
					
					if (that.jsonEqual(row, itemTemp)) {
						Vue.set(row, "checked", true);
						item.inited = true;//标记已经初始化过
					}
				});
			});
		}
	}
}

</script>
