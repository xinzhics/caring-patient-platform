
<template>
<div>
	<div>
		<group v-if="conditions&&conditions.length>0">
			<flexbox :gutter="0">
		      	<flexbox-item span="120">
					<selector v-model="condition" :options="conditions" placeholder="条件"></selector>
				</flexbox-item>
		      	<flexbox-item>
					<x-input v-model="findInput" placeholder="请输入" @on-enter="find"></x-input>
		      	</flexbox-item>
	    	</flexbox>
	    </group>
	</div>
	<div>
		<scroller :on-refresh="refresh" :on-infinite="infinite" :height="heightTemp" ref="listScroller" :style="scrollerStyle">
			<div>
				<slot :rows="rows"></slot>
			</div>
		</scroller>
	</div>
</div>
</template>

<script>
import Vue from 'vue';
import VueScroller from 'vue-scroller';
Vue.use(VueScroller);
import { XInput } from 'vux';
Vue.component('x-input', XInput);
import { Group } from 'vux';
Vue.component('group', Group);
import { Flexbox, FlexboxItem } from 'vux';
Vue.component('flexbox', Flexbox);
Vue.component('flexbox-item', FlexboxItem);
import { Selector } from 'vux';
Vue.component('selector', Selector);

/**
 * ab项目基于vue-scroller的上拉刷新，和下拉加载更多组件
 * 注意组件的作用域中的rows是数组列表
 * props请看代码中的描述
 * slot-scope在卡槽中直接使用组件中的作用域！
 * eg:
 * <ab-scroller url="/bpm/my/todoTaskList">
 *     <div slot-scope="slotScope">
 *        <cell v-for="task in slotScope.rows" :title="task.name" after-title="超管" :link="{path:'/bpm/complateTask',query:{taskId: task.id}}" :inline-desc="task.subject"></cell>
 *     </div>
 * </ab-scroller>
 * 
 */
export default {
	//url:数组请求url;pageSize:分页大小;scorller的高度 默认80%;list的数组赋值;
	//marginTop:由于scroller组件的bug，其无法计算出离顶部的距离，目前组件会默认进行x-header的50px
 	//conditions:条件字段 eg:[{key:"name_^VLK",value:"名字"},{key:"subject_^VLK",value:"标题"}]
	props:["url","pageSize","height","list","marginTop","conditions"],
	data: function () {
	  return {
		rows :[],//列表数据
	  	pageData :{},//当前页数据
	  	heightTemp:"80%",
	  	condition:"",
	  	findInput:"",
	  	scrollerStyle:{}
	  }
	},
	created : function(){
		//设置高度
		if(this.height){
			this.heightTemp = height;
		}
		
		//设置列表
		if(this.list){
			this.rows=this.list;
		}
		
		//初始化查询列表
		this.search(0,this.pageSize);
		
		//计算距离顶部的高度
		var marginTop = this.marginTop?parseInt(this.marginTop):50;
		if(this.conditions&&this.conditions.length>0){
			this.condition = this.conditions[0].key;//设置默认查询条件
			marginTop+=60;
		}
		this.scrollerStyle["margin-top"] = marginTop + "px";
	},
	methods: {
		//上拉刷新
		refresh(done) {
			console.info("refresh");
			this.rows.splice(0);//置空数组
			this.search(0,this.pageSize,function(){
				done();
			});
		},
		//下拉加载
	    infinite(done) {
			console.info("infinite");
			if(this.pageData.pageResult){//数据加载好了
				if(!this.pageData.pageResult.hasNextPage){//无下一页
					this.$refs.listScroller.finishInfinite(true);//通知组件到尾部了
					return;
				}
				if(this.pageData.pageResult.hasNextPage){//有下一页，加载下一页数据
					this.search(this.rows.length,this.pageSize,function(){
						done();
					});
					return;
				}
			}
	      	done();
	    },
	    //获取页面数据
	    search(offset,limit,callback){
	    	var that = this;
	    	var url = this.url;
	    	if(!url){//为空时不处理
	    		return;
	    	}
	    	if(url.indexOf("/")===0){
	    		url = Vue.__ctx + url;
	    	}
	    	
	    	var param = {
				offset:offset,
				limit:limit?limit:10
			};
	    	if(this.findInput&&this.condition&&this.conditions.length>0){
	    		param[this.condition] = this.findInput;
	    	}
			var post = Vue.baseService.postForm(url,param);
			post.then(function(data){
				if(data.isOk === false){
					Vue.tools.toast(data.msg);
					console.error(data);
					return;
				}
				
				that.pageData = data;
				data.rows.forEach(function(item,index){
					that.rows.push(item);
				});
				if(callback){
					callback(data);
				}
			});
	    },
	    find : function(){
	    	this.rows.splice(0);//置空数组
			this.search(0,this.pageSize);
	    }
	},
	watch : {
		//监听url，当url不为空时则查一次
		url : function(newVal,oldVal){
			if(!newVal||newVal===oldVal){
				return;
			}
			this.search(0,this.pageSize);
		}
	}
	
}

</script>

<style>
.card-padding {
	padding: 15px;
}
</style>
