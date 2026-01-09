<template>
    <section>

        <div class="input_img_father_div">
         <div v-if="filesList.length===0">
             <img class="upload" :src="loaderImg" style="width: 100%; vertical-align: middle;" @click="upBtn"/>
         </div>
            <div class="imgInner" v-for="(i,index) in filesList" :key="index" style="margin:0px 0px 20px" >

                <div v-if="!loadingImgCan&&i&&i.attrValue">
                    <x-icon type="ios-close" size="20" @click="deleteImg(i,index)"></x-icon>
                    <img class="upload" :src="i&&i.attrValue?i.attrValue:loaderImg" style="width: 100%; vertical-align: middle;" @click="showbig(i,index)"/>
                </div>
                <img v-if="!loadingImgCan&&i&&!i.attrValue" class="upload" :src="loaderImg" style="width: 100%; vertical-align: middle;" @click="upBtn"/>



                <div  v-if="loadingImgCan&&typeUpload===1" style="text-align:center;background:#f5f5f5">
                    <img class="upload" :src="loadingImg" style="width: 30vw;height:30vw; vertical-align: middle;text-align:center"/>
                </div>

            </div>

        </div>
        <div v-if="filesList&&filesList.length>=1&&filesList[0]&&filesList[0].attrValue&&info.uploadMany!==2&&filesList.length<info.uploadNumber" class="moreup" >
              <x-icon v-if="!loadingImgCan1" type="ios-plus-outline" size="20"></x-icon>
              <img v-if="loadingImgCan1&&typeUpload===2" :src="loadingImg1" style="vertical-align: middle;width: 40px;" />
              <span>再上传一张</span>
              <input style="position: absolute;top:20px;left:0;height:40px;width:100%;z-index:999;opacity: 0;" :type="typeBtn" @change="uploaderfile($event,2)" accept="image/jpg, image/jpeg, image/png,image/webp"/>
        </div>
        <x-dialog v-model="showOne" class="dialog-demo" hide-on-blur :dialog-style="{borderRadius:'10px',width:'70%'}">
             <p style="text-align:center;padding:10px">上传示例</p>
            <div style="padding:10px">
                <img :src="warn" alt="" width="100%">
            </div>
            <div style="position:relative" >
                <input style="position: absolute;top:20px;right:15vw;height:40px;width:40vw;z-index:999;opacity: 0; background: " ref="inputImg" type="file" @change="uploaderfile($event,1)" accept="image/jpg, image/jpeg, image/png"/>
            </div>
              <x-button style="color:#fff;background:#66728C;width:40vw;margin:20px auto" >我知道了</x-button>
        </x-dialog>
        <!-- <div>
            <previewer :list="listDatas" ref="previewer" :options="options"></previewer>
        </div> -->
    </section>
</template>
<script>
import Vue from 'vue';
import { Toast } from 'vant';

Vue.use(Toast);
 import Api from '@/api/Content.js'
 import { Previewer} from 'vux'
export default {
    components: {
        Previewer
    },
    props: {
            urlbtn: {
                type: Function
            },
            filesList: {
                type: Array,
                default() {
                    return []
                }
            },
            info: {
                type: Object,
                default() {
                    return {}
                }
            },
    },
    data(){
        return{
            showOne:false,
            upimg:false,
            loaderImg:require('@/assets/my/bj.png'),
            loadingImg:require('@/assets/my/loadingImg1.gif'),
            loadingImg1:require('@/assets/my/loadingImg.gif'),
            upmyImg:'',
            warn:require('@/assets/my/exp.png'),
            setIndex:-1,
            imgWidth: '375px',
            listDatas: [],
            options: { //需正确配置
                getThumbBoundsFn (index) {
                    let thumbnail = document.querySelectorAll('.previewer-demo-img')[index];
                    let pageYScroll = window.pageYOffset || document.documentElement.scrollTop;
                    let rect = thumbnail.getBoundingClientRect()
                    return {x: rect.left, y: rect.top + pageYScroll, w: rect.width}
                }
            },
            setObj:{},
            loadingImgCan:false,
            typeUpload:2,
            loadingImgCan1:false,
            typeBtn:'file'

        }
    },
    mounted(){
      this.imgWidth = parseInt((Number(window.screen.width) - 45) / 3) + 'px';
      console.log('info',this.filesList,this.info)
    },
    methods:{
        showbig(i,index){
            // this.setObj = i
            // this.listDatas = [{
            //     msrc: this.setObj.attrValue,
            //     src: this.setObj.attrValue,
            //     w: 900,
            //     h: 1200
            // }]
            // this.$refs.previewer.show(index)
        },
        loaGongkeSearck() { // 获取图片路径，通过base64转换显示
        //     this.listDatas.push({
        //         msrc: this.setObj.attrValue,
        //         src: this.setObj.attrValue,
        //         w: 900,
        //         h: 1200
        //     })
        },
        upBtn(i,index){
            if(index>=0){
                this.setIndex = index
            }
            this.showOne = true
        },
        uploaderfile(i,z){
                    this.typeUpload = z
                    this.showOne = false
                    let files = i.target.files[0]
                    var formData = new FormData()
                    formData.append('file', files)
                    formData.append('folderId', 1308295372556206080)
                    if(files&&formData){
                        this.typeBtn = 'text'
                        if(z===1){
                            this.loadingImgCan = true
                        }else{
                            this.loadingImgCan = false
                        }
                        this.loadingImgCan1 = true
                    }
                    // if (this.type === 'img') {
                    const isJPG = (files.type === 'image/jpeg' || files.type === 'image/png'|| files.type === 'image/jpg'|| files.type === 'image/webp')
                    if (!isJPG) {
                        this.$vux.toast.text("上传图片格式只能为jpg/png/jpeg/webp!",'center');
                        this.loadingImgCan = false
                        this.loadingImgCan1 = false
                        this.$refs.inputImg.value = null;
                        return
                    }
                    // }
                    // const isLt2M = files.size / 1024 / 1024 < 5
                    // if (!isLt2M) {
                    //     this.$vux.toast.text("上传图片大小不能超过 5MB!",'center');
                    //     this.loadingImgCan = false
                    //     this.loadingImgCan1 = false
                    //     return
                    // }
          Toast.loading('上传中');
          Api.updateImg(formData).then((res) => {
                        if (res.data.code === 0) {
                          Toast.clear();
                            this.$vux.toast.text("上传成功",'center');
                             this.loadingImgCan = false
                             this.loadingImgCan1 = false
                            // let obj = {};
                            // obj[this.keyName] = res.data.data.url;
                            // let tempList = JSON.parse(
                            //     JSON.stringify(this.fileList)
                            // );
                            // tempList.push(obj);
                            this.upimg = true
                            this.typeBtn = 'file'
                            if(this.setIndex>0){
                                this.filesList.splice(this.setIndex,1,{attrValue:res.data.data.url})
                            }else{
                                if(this.filesList&&this.filesList[0]&&!this.filesList[0].attrValue){
                                    this.filesList.splice(0,1,{attrValue:res.data.data.url})
                                }else{
                                    this.filesList.push({attrValue:res.data.data.url})
                                }
                            }
                            this.urlbtn(this.filesList)
                        } else {
                            this.$vux.toast.text("文件上传失败",'center');
                            this.loadingImgCan = false
                            this.loadingImgCan1 = false
                            this.typeBtn = 'file'
                        }
                    })
        },
        deleteImg(i,k){
            this.filesList.splice(k,1)
            if(this.filesList&&this.filesList.length===0){
                this.urlbtn([{attrValue:''}])
            }else{
                this.urlbtn(this.filesList)
            }
        }
    }
}
</script>
<style lang="less" scoped>
.input_img_father_div {
    position: relative;
    width: 70vw;
    vertical-align: middle;
    /* background-color: #c6c6c6; */
    align-items: center;
    .imgInner{
        position: relative;
        .vux-x-icon {
            position: absolute;
            right: -10px;
            top: -10px;
            fill: #FF5C5C;
        }
    }
    .upload_input {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
        opacity: 0;
        width: 100%;
    }
    .input_img_btn_div {
        text-align: center;
        display: flex;
        flex-direction: column; /*元素的排列方向为垂直*/
        justify-content: center; /*水平居中对齐*/
        align-items: center; /*垂直居中对齐*/
        .upload{
            width: 100%;
        }
    }
}
.moreup{
    width: 70vw;
    height: 40px;
    border-radius: 4px;
    background: #F7F7F7;
    border: 1px solid #D6D6D6;
    margin-top: 20px;
    line-height: 40px;
    text-align: center;
    font-size: 15px;
    position: relative;
    span{
        vertical-align: middle;
    }
    .vux-x-icon {
        vertical-align: middle;
        fill: #66728C;
    }
}


</style>
