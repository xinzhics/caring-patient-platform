<template>
    <section class="contentavater">
        <div class="avaterCont" >
            <img v-if="!loadingImgCan&&filesList[0].attrValue" :class="mytype==='1'?'othershow':'show'" style="border-radius: 50%" width="100%" :src="filesList[0].attrValue" alt="">
            <img v-if="!loadingImgCan&&!filesList[0].attrValue" :class="mytype==='1'?'othershow':'show'" style="border-radius: 50%" width="100%" :src="loaderImg" alt="">
            <img v-if="loadingImgCan" :class="mytype==='1'?'othershow':'show'" style="border-radius: 50%;background:#f5f5f5" width="100%" :src="loadingImg" alt="">
            <div class="icon" :style="{width:mytype==='1'?'30px':'8vw',height:mytype==='1'?'30px':'8vw'}">
                <img :src="icon" alt="">
            </div>
            <input class="iconup" :type="typeBtn" @change="uploaderfile($event)" accept="image/*"/>
        </div>
        <p class="tip">{{objname?objname:'点击上传'}}</p>
    </section>
</template>
<script>
import Api from '@/api/Content.js'
export default {
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
        mytype: {
            type: String,
            default() {
                return ''
            }
        },
        objname:{
            type: String,
            default() {
                return ''
            }
        },
        myindex:{
            type: Number,
            default() {
                return 0
            }
        },
    },
    data(){
        return{
            showOne:false,
            upimg:false,
            loaderImg:require('@/assets/my/doctor_avatar.png'),
            upmyImg:'',
            icon:require('@/assets/my/canam.png'),
            resultList:[],
            loadingImg:require('@/assets/my/loadingImg1.gif'),
            loadingImgCan:false,
            typeBtn:'file'
        }
    },
    methods:{
        uploaderfile(i){

            console.log(i)
                    let files = i.target.files[0]
                    var formData = new FormData()
                    if(files&&formData){
                        this.loadingImgCan = true
                        this.typeBtn = 'text'
                    }
                    formData.append('file', files)
                    formData.append('folderId', 1308295372556206080)
                    const isJPG = (files.type === 'image/jpeg' || files.type === 'image/png')
                    if (!isJPG) {
                        this.$vux.toast.text("上传图片只能为jpg或者png!",'center');
                        this.loadingImgCan = false
                        return
                    }

                    Api.updateImg(formData).then((res) => {
                        if (res.data.code === 0) {
                            this.$vux.toast.text("上传成功",'bottom');
                            this.loadingImgCan = false
                            this.typeBtn = 'file'
                            this.resultList=[{attrValue:res.data.data.url}]
                            if(this.mytype==='1'){
                                this.urlbtn({index:this.myindex,obj:this.resultList})
                            }else{
                                this.urlbtn(this.resultList)
                            }

                        } else {
                            this.loadingImgCan = false
                            this.$vux.toast.text("文件上传失败",'bottom');
                            this.typeBtn = 'file'
                        }
                    })
        }
    }
}
</script>
<style lang="less" scoped>
.contentavater{
    .tip{
        text-align: center;
        color: #666666;
    }
    .avaterCont{
        position:relative;
        .show{
            width: 30vw;
            height: 30vw;
            // border-radius: 50%;
        }
        .othershow{
            width: 80px;
            height: 80px;
        }
        .icon{

            position: absolute;
            right:0;
            bottom:5px;
            background:#fff;
            border-radius:50%;
            border:1px solid #B8B8B8;
            text-align: center;
            img{
                width:4vw;
                margin: 2vw;
                // height: 7vw;
            }
        }
        .iconup{
            width:12vw;
            height:12vw;
            position: absolute;
            right:0;
            bottom:5px;
            z-index:999;
            opacity: 0;
        }
    }
}
</style>
