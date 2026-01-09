var Tools  = {};

Tools.extend = function(parent, child){
	 var child = child || {};  
     for(var prop in parent) {  
         child[prop] = parent[prop];  
     }  
     return child;  
}

Tools.getResultData = function(defer,fn,msgType,errorFn){
	defer.then(function(result){
		Tools.getResult(result,fn,msgType,null,errorFn);
		},function(status){
			if(status ==!0){
				alert("加载失败！"+status);
			}
			
		}
	);
}

Tools.getResultMsg  = function(defer,fn,errorFn){
	defer.then(function(result){
		Tools.getResult(result,fn,"alert","alert",errorFn);
		},function(status){
			if(errorFn){
				errorFn(status);
			}
			alert("加载失败！"+status,2);
		}
	);
}

Tools.getResult = function(result,fn,errMsgType,sucMsgType,errorFn){
	if(typeof(result) !== "object"){
		if(!result.startWith("{") && !result.startWith("[")){
			result = {"isOk":false,msg:"服务器反馈数据格式存在异常，无法解析反馈结果！","cause":result}
		}else{
			var result	= eval('(' + result + ')');
		}
	}
	 //失败的提示
	if(!result.isOk){
		if(!errMsgType || errMsgType ==='toast'){
			Tools.toast(result.msg,"warn");
			if(errorFn){errorFn(result)};
		}else if (errMsgType === 'alert'){
			Tools.alert(result.msg,"错误信息",errorFn);
		}
		console.error(result);
		return;
	}else{
		if(!sucMsgType){//不需要任何显示
			if(fn){
				fn(result.data);
			}
		}else if(sucMsgType ==='toast'){
			Tools.toast(result.msg);
			if(fn){
				fn(result.data);
			}
		}else if (errMsgType === 'alert'){
			Tools.alert(result.msg,function(){
				if(fn){
					fn(result.data);
				}
			});
		}
	}
}


/***
 * 将格式化数字转换成number
 */
Tools.toNumber = function(x){ 
	if(x === null || x === undefined ||  x === '') return '';
	if(typeof x == "string"){
		x = x.replace(/,/g, "");
	}
	var match = x.toString().match(/([$|￥])\d+\.?\d*/);
	if(match){
		x = x.replace(match[1],'');
	}
	var val =Number(x);
	if(Number.isNaN(val))return x;
	return val;
}

/**
 * 通过json的路径获取值。严格模式下无法使用eval
 */
Tools.getJsonValue = function xxx(json, path){
	if(!json) return ;
	try{
		var array = path.split(".");
		var value = json ;
		for(var i=0,k;k=array[i++];){
			value = value[k];
		}
		return value;
	}catch(e){
		console.error("通过path 获取json的value 失败 ",e,json,path);
	}
	
}

/**
 * 移动端没有那么多样式
 */
Tools.alert = function(content,title,fn){
	if(typeof title === "function"){
		fn = title;
		title="";
	}
	
	if(!title)title="提示";
	if(!installVue){alert}
	installVue.$vux.alert.show({
		  title: title,
		  content: content,
		  onHide () {
			 if(fn)fn();
		  }
	})
}

Tools.toast = function(content,type){
	if(!type) type = "text";
	
	
	installVue.$vux.toast.show({
		 text: content,
		 type:type
	})
}

/**
 * 日期格式化。
 * 日期格式：
 * yyyy，yy 年份
 * MM 大写表示月份
 * dd 表示日期
 * HH 表示小时
 * mm 表示分钟
 * ss 表示秒
 * q  表示季度
 * 实例如下：
 * var now = new Date(); 
 * var nowStr = now.format("yyyy-MM-dd HH:mm:ss"); 
 */
Date.prototype.format = function(format){ 
	var o = { 
		"M+" : this.getMonth()+1, //month 
		"d+" : this.getDate(), //day 
		"H+" : this.getHours(), //hour 
		"m+" : this.getMinutes(), //minute 
		"s+" : this.getSeconds(), //second 
		"q+" : Math.floor((this.getMonth()+3)/3), //quarter 
		"S" : this.getMilliseconds() //millisecond 
	} 
	
	if(/(y+)/.test(format)) { 
		format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	} 

	for(var k in o) { 
		if(new RegExp("("+ k +")").test(format)) { 
			format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
		} 
	} 
	return format; 
} 


/**
 * 求当前时间到指定时间的差（当前时间是起点）
 * type:date 天数;month:月数;year:年数;hour：小时
 */
Date.prototype.between = function(date,type) {
	if(!type){
		type = "date";
	}
	if(date.getTime()<=this.getTime()){
		return 0;
	}
	if(type=="hour"){
		var dateSpan = date.getTime() - this.getTime();
		return Math.floor(dateSpan / (3600 * 1000));
	}
	if(type=="date"){
		var dateSpan = date.getTime() - this.getTime();
		return Math.floor(dateSpan / (24 * 3600 * 1000));
	}
	if(type=="month"){
		return (date.getFullYear()-this.getFullYear()) * 12 + (date.getMonth()-this.getMonth());
	}
	if(type=="year"){
		return date.getFullYear()-this.getFullYear();
	}
	
};

/**
 * 字符串根据某个格式转化成日期
 * yyyy-MM-dd HH:mm:ss
 */
String.prototype.toDate = function(format) {
	if(!format){
		format = "yyyy-MM-dd";
	}
	var year = String_toDate(this,format,"yyyy")
	var month = String_toDate(this,format,"MM");
	var day = String_toDate(this,format,"dd");
	var hour = String_toDate(this,format,"HH");
	var minth = String_toDate(this,format,"mm");
	var second = String_toDate(this,format,"ss");
	var date = new Date(0);
	date.setFullYear(year||1);
	date.setMonth(parseInt(month||1)-1);
	date.setDate(day||1);
	date.setHours(hour||0);
	date.setMinutes(minth||0);
	date.setSeconds(second||0);
	return date;
};
function String_toDate(str,format,a){
	if(format.indexOf(a)==-1){
		return null;
	}
	return str.substr(format.indexOf(a),a.length);
}

var installVue = null;
export default {
	// 全局安装时候
  install(Vue) {
    Vue.tools = Tools;
    installVue = Vue;
  },
  tools : Tools
}

export const tools = Tools;
