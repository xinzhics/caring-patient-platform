import Vue from 'vue'
import Axios from 'axios'
import Qs from 'qs'
//配置后端 ctx 路径
var __ctx = "http://test.agilebpm.cn/agile-bpm-platform";
//var __ctx = "/agile-bpm-platform";

var axios = Axios.create({
　baseURL: '',//配置请求地址默认则直接请求 与前端地址相同的 domain
　timeout: '18000',  //请求超时时间
 //header传值，例如：Authorization
　headers: { }
})
axios.defaults.withCredentials = true;

axios.interceptors.request.use(function(request){
	request.credentials = true;
	return request;
})

// 设置通用的拦截器
axios.interceptors.response.use(function(response){
	if(response.data && !response.data.isOk && response.data.code==="401" ){
		Vue.toLogin();
	  }
	  if(response.data && !response.data.isOk && response.data.code==="403" ){
		  alert("访问受限! "+data.msg);
	  }
    return response;
},function(error){
	//通用异常
    return Promise.reject(error);
});


// baseService 基础服务
 var BaseService = {
	  get : function (url) {
	  	
	  	 return new Promise((resolve, reject) => {
		    axios.get(url)
		    	.then(response => {
		    		resolve(response.data);
		    	})
		    	.catch(error => {
				    console.log("请求失败：",error);
		    		reject(error)
		    	})
		  })
	  },
	  post : function(url,data){
	  	if(!data) data={};
	  	
	  	
	  	 return new Promise((resolve, reject) => {
		    axios.post(url,data)
		    	.then(response => {
		    		resolve(response.data) ;
		    	})
		    	.catch(error => {
				    console.log("请求失败：",error);
		    	reject(error)
		    	})
		  })
	  	
	  },
	  // form-data格式化
	  postForm : function(url,data){
	  	if(!data) data={};
	  	
	  	return new Promise((resolve, reject) => {
		    axios.post(url, data,{transformRequest:this.jsonToFormData,headers:{"Content-Type":"application/x-www-form-urlencoded; charset=utf-8"} })
		    	.then(response => {
		    		resolve(response.data) ;
		    	})
		    	.catch(error => {
				    console.log("请求失败：",error);
		    	reject(error)
		    	})
		  })
	  }
	  
	  ,jsonToFormData:function(data){
	  	data = Qs.stringify(data,{arrayFormat: 'brackets'});// qs.stringify(params)
			return data;
	  }
 }
 
 // 数组服务
  var ArrayToolService = {
		//上移按钮
    	up:function(idx,list){
    		if(idx<1){
    			return;
    		}
    		var t=list[idx-1];
    		list[idx-1]=list[idx];
    		list[idx]=t;
    	},
    	//下移按钮
    	down:function(idx,list){
    		if(idx>=list.length-1){
    			return;
    		}
    		var t=list[idx+1];
    		list[idx+1]=list[idx];
    		list[idx]=t;
    	},
    	resumeSn:function(list){
    		for(var k = 0 ; k < list.length ; k++){
    			list[k].sn = k;
			}
    		return list;
    	},
    	/**
    	 * idx 原位置
    	 * num 目标位置
    	 * list 数组
    	 */
    	moveToNum:function(idx,target,list){
    		if(target==-1){
    			target = 0;
    		}else if(idx >= target){
    			target = target+1;
    		}
    		var t= list.splice(idx,1);
    		list.insert(target,t[0]);
    		this.resumeSn(list);
    	},
    	//默认ngModel turnToIndex
    	turnTo:function(rowScope,list){
    		var toIndex =rowScope.turnToIndex - 1;
    		if(!rowScope.turnToIndex || toIndex<0 || toIndex>=list.length) return; 
    		
    		var index = rowScope.$index;
    		if(toIndex == index) return;
    		
    		var row= list.splice(index,1);
    		list.insert(toIndex,row[0]);
    		
    		rowScope.turnToIndex= "";
    	},
    	//删除按钮
    	del:function(idx,list){
    		list.splice(idx,1);
    	},
    	//找到指定元素的未知
    	idxOf:function(val,list){
    		for (var i = 0; i < list.length; i++) {  
    	        if (list[i] == val) return i;  
    	    }  
    	    return -1; 
    	},
    	//删除指定元素
    	remove:function(val,list){
    		var idx = this.idxOf(val,list);  
    	    if (idx > -1) {  
    	    	list.splice(idx, 1);  
    	    }  
    	},
    	//置顶
    	top:function(idx,list){
    		if(idx>=list.length || idx<1){
    	           return;
    		}
    		//逐个交换
            for(var i=0;i<idx;i++){
	            var temp=list[i];
	            list[i]=list[idx];
	            list[idx]=temp;
            }
    	},
    	//置底
    	bottom:function(idx,list){
    		if(idx>=list.length-1 || idx<0){
                return;
            }
            //逐个交换
            for(var i=list.length-1;i>idx;i--){
                var temp=list[i];
                list[i]=list[idx];
                list[idx]=temp;
            }
    	}
    }; 

export default {
	// 全局安装时候
  install (Vue) {
    Vue.baseService = BaseService;
    Vue.arrayTools = ArrayToolService;
    Vue.__ctx = __ctx;
  },
  baseService: BaseService,
  arrayTools: ArrayToolService
}

export const baseService = BaseService;
export const arrayTools = ArrayToolService