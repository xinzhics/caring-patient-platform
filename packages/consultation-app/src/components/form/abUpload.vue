<template>
<div>
	<!-- 按钮 -->
	<span v-on:click="showDialog" v-if="permission=='w'">
		<slot></slot>
	</span>
	<a v-if="permission=='w'||permission=='r'" style="margin-left: 5px;" v-for="file in files" :href="file.href" class="weui-btn weui-btn_mini weui-btn_primary fa fa-download">
		{{file.name}}
	</a>
	<!-- 附件对话框的内容 -->
	<popup v-model="show" height="100%">
		<x-header :left-options="{showBack: false}">附件上传</x-header>
		<x-table :cell-bordered="true"  style="background-color:#fff;">
			<thead style="background-color: #F7F7F7">
				<tr>
					<th>文件名</th>
					<th>状态</th>
					<th>预览</th>
					<th>
						<x-button type="primary" mini @click.native="click">添加</x-button>
					</th>
				</tr>
			</thead>
			<tbody>
				<tr v-for="file in files">
					<th>{{file.name}}</th>
					<td>{{file.statusText}}</td>
					<td>
						<img style="width: 100px; height: 100px" v-if="file.src" :src="file.src" />
						<span v-if="!file.src">无法预览</span>
					</td>
					<td>
						<x-button type="primary" mini @click.native="download(file)">下载</x-button>
						<x-button type="primary" mini @click.native="open(file)">打开</x-button>
						<x-button type="warn" mini @click.native="del(file)">删除</x-button>
					</td>
				</tr>
			</tbody>
		</x-table>
		
		<group :style="btnStyle">
			<flexbox :gutter="0">
		      	<flexbox-item>
					<x-button @click.native="ok">确定</x-button>
				</flexbox-item>
		      	<flexbox-item>
					<x-button @click.native="show=false">取消</x-button>
		      	</flexbox-item>
	    	</flexbox>
	    </group>
	</popup>
	
	<!-- 图片预览框 -->
	<popup v-model="showImg" height="100%" style="margin-top:5%; text-align: center;">
		<x-button type="primary" @click.native="showImg=false">关闭</x-button>
		<span style="font-size:20px;" v-show="spanShow=='loading'">加载中...</span>
		<span style="font-size:20px;" v-show="spanShow=='error'">加载失败</span>
		<x-img :src="imgSrc" error-class="ximg-error" @on-success="imgSuc" @on-error="imgErr"></x-img>
	</popup>
	
	<!-- 上传附件组件 -->
	<file-upload ref="upload" :input-id="fileUploadId" 
		v-model="files" :custom-action="customAction" :multiple="true" 
		@input-file="inputFile" @input-filter="inputFilter">
	</file-upload>
	
</div>
</template>

<script>

import Vue from 'vue';
const VueUploadComponent = require('vue-upload-component');
Vue.component('file-upload', VueUploadComponent);
import { Popup } from 'vux';
Vue.component('popup', Popup);
import { XButton } from 'vux';
Vue.component('x-button', XButton);
import { Group } from 'vux';
Vue.component('group', Group);
import { Flexbox, FlexboxItem } from 'vux';
Vue.component('flexbox', Flexbox);
Vue.component('flexbox-item', FlexboxItem);
import { XImg } from 'vux';
Vue.component('x-img', XImg);
/**
 * <ab-upload v-model="files" :permission="xxx">
 * 	  <a href="javascript:;" class="weui-btn weui-btn_mini weui-btn_primary fa fa-upload">上传</a>
 * </ab-upload>
 */
export default {
	props:["value","permission"],
	data: function () {
	  return {
		  show:false,
		  showImg:false,
		  files:[],
		  fileUploadId:"",
		  url:Vue.__ctx +"/sys/sysFile/upload",
		  btnStyle:{
			  "margin-top":"200px"
		  },
		  imgSrc:"",
		  spanShow:""
	  }
	},
	mounted : function(){
		this.fileUploadId = "fileId"+Math.random();//随机id
		this.showDialog();
		this.show=false;
	},
	methods: {
		//显示上传对话框
		showDialog : function(){
			//初始化value值
			if(this.value){
				this.files = JSON.parse(this.value);
			}
			this.files.forEach(function(file){
				file.success = true;
				file.statusText = "已上传";
				file.href = Vue.__ctx + "/sys/sysFile/download?fileId=" + file.id;
				if (/\.(jpeg|jpe|jpg|gif|png|webp)$/i.test(file.name)) {
					file.src = file.href;
				}
			});
			this.show=true;
		},
		//点击触发选择上传
		click : function(){
			document.getElementById(this.fileUploadId).click();
		},
		ok : function(){
			this.show = false;
			
			var list = [];
			this.files.forEach(function(file){
				if(!file.success){
					return;
				}
				list.push({
					id : file.id,
					name : file.name
				});
			});
			
			this.$emit('input', JSON.stringify(list));
		},
		//添加，更新，移除 后
		inputFile : function(newFile, oldFile) {
			// 自动上传
	      	if (Boolean(newFile) !== Boolean(oldFile) || oldFile.error !== newFile.error) {
	        	if (!this.$refs.upload.active) {
	          		this.$refs.upload.active = true
	        	}
	      	}
      	 	// 更新文件
	      	if (newFile && oldFile) {
	      		// 开始上传
            	if (newFile.active !== oldFile.active) {
            		newFile.statusText = "开始上传";
	            }

	            // 上传进度
	            if (newFile.progress !== oldFile.progress) {
	            	newFile.statusText = "上传中..." + newFile.progress + "%";
	            }

	            // 上传错误
	            if (newFile.error !== oldFile.error) {
	            	newFile.statusText = "上传错误";
	            }

	            // 上传成功
	            if (newFile.success !== oldFile.success) {
	            	var result = JSON.parse(newFile.response);
	            	newFile.statusText = result.msg;
            		if(result.isOk){
            			Vue.tools.toast(result.msg);
            			newFile.id = result.data;
            			newFile.href = Vue.__ctx + "/sys/sysFile/download?fileId=" + result.data;
            		}else{
            			Vue.tools.toast(result.msg,"warn");
            		}
	            }
	      	}
      	 	
	     	// 删除文件
	      	if (!newFile && oldFile) {
          	}
		},
		inputFilter : function(newFile, oldFile, prevent) {
			// 添加文件
			if (newFile && !oldFile) {
		        if (!/\.(jpeg|jpe|jpg|gif|png|webp)$/i.test(newFile.name)) {
		          return;
		        }

		        // 创建 `src` 字段 用于缩略图预览
		        newFile.src = ''
		        let URL = window.URL || window.webkitURL
		        if (URL && URL.createObjectURL) {
		        	newFile.src = URL.createObjectURL(newFile.file);
		        }
	      	}
		},
		customAction : function(file, component) {
			file.postAction = this.url;
			component.uploadHtml5 = function(file){
				var form = new window.FormData();
		        form.append(this.name, file.file, file.file.filename || file.name);
		        var xhr = new XMLHttpRequest();
		        xhr.withCredentials = true;
		        xhr.open('POST', file.postAction);
		        return this.uploadXhr(xhr, file, form);
			};
			return component.uploadHtml5(file);
		},
		open : function(file){
			if (/\.(jpeg|jpe|jpg|gif|png|webp)$/i.test(file.name)) {
				this.spanShow = "loading";
				this.imgSrc = file.href;
				this.showImg = true;
			} else {
            	Vue.tools.toast("目前只支持打开图片，请下载后查看");
			}
		},
		del : function(file) {
			var idx = this.files.indexOf(file);
			var that = this;
			if(!file.success){
				that.files.splice(idx,1);
				that.$refs.upload.remove(file);
				return;
			}
        	var url = Vue.__ctx + "/sys/sysFile/del";
        	var post = Vue.baseService.postForm(url,{
        		fileId : file.id
    		});
        	Vue.tools.getResultData(post,function(){
        		Vue.tools.toast("删除成功");
        		that.$refs.upload.remove(file);
        		that.files.splice(idx,1);
        	});
		},
		imgSuc : function(src, ele) {
			this.spanShow = "";
	    },
	    imgErr : function(src, ele, msg) {
	    	this.spanShow = "error";
	    },
	    download : function(file) {
	    	window.open(file.href);
	    }
	}
}
</script>
