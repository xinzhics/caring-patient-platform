<template>
    <div class="lano-uplodaer-container" :class='className' style="background: #FFFFFF">
        <!--上传如果是头像 使用头像布局-->
        <div v-if="type === 'avatar'" style="position: relative;">
            <div style="display:flex;justify-content:center;">
                <div class="lano-upload-wrap">
                    <div class="lano-upload-preview" v-for="(file,ind) in fileList" :key="ind">
                        <van-image
                                :src="file[keyName] "
                                :alt="label"
                                @click="showImage()">

                            <template v-slot:error>
                                <i class="iconfont iconRemarks"></i>
                            </template>
                        </van-image>
                        <span class="lano-uploader-del" @click.stop="delFile(ind, file)">
                                     <icon type="clear"></icon>
                                </span>
                    </div>
                    <van-uploader
                            :accept="accept"
                            :after-read="onUpload"
                            :multiple="false"
                            v-if="fileList.length<(max?Number(max):1)"
                            :before-read="beforeReadFun"
                            :max-count="max?Number(max):1"
                    />
                </div>
            </div>

            <div style="display:flex;justify-content:center;" v-if="setTip">
                <span v-if="errorMessage" style="color: red; font-size: 14px; margin-top: 5px; text-align: center">{{ errorMessage }}</span>
                <span v-else style="color: #333333; font-size: 14px; margin-top: 5px; text-align: center">请上传头像</span>
            </div>
        </div>

        <!--上传图片-->
        <div v-else-if="label" class="lano-uploader-label" :class="{'required':required}">
            <van-row>
                <van-col span="7">
                    <span style="font-size: 14px; color: #333333" v-text="label"></span>
                </van-col>
                <van-col span="17">
                    <div class="lano-upload-wrap" style="float: right">
                        <div class="lano-upload-wrap" style="float: right">
                            <div class="lano-upload-preview" v-for="(file,ind) in fileList" :key="ind">
                                <van-image
                                        :src="file[keyName]"
                                        :alt="label"
                                        @click="showImage()">

                                    <template v-slot:error>
                                        <i class="iconfont iconRemarks"></i>
                                    </template>
                                </van-image>
                                <span class="lano-uploader-del" @click.stop="delFile(ind, file)">
                                    <icon type="clear"></icon>
                                </span>
                            </div>
                            <van-uploader
                                    :accept="accept"
                                    :after-read="onUpload"
                                    :multiple="false"
                                    v-if="fileList.length<(max?Number(max):1)"
                                    :before-read="beforeReadFun"
                                    :max-count="max?Number(max):1"
                            />
                        </div>
                    </div>
                </van-col>
            </van-row>
            <p
                class="error-msg"
                v-text="'请选择'+label||'文件'"
                v-if="errorMessage && required"
            ></p>
        </div>
        <x-dialog v-model="showDialog" class="dialog-demo">
            <p style="margin-top:20px">是否确定删除此头像！</p>
            <div style="display: flex;justify-content: space-between;margin:20px 0px">
                <x-button mini type="warn" action-type="reset" style="height:30px;padding:0px 30px" @click.native="detelBtn">确定</x-button>
               <x-button mini type="primary" action-type="button" style="height:30px;margin-top:0px;padding:0px 30px" @click.native="cancelBtn">取消</x-button>
            </div>
      </x-dialog>
    </div>
</template>

<script>
    import {ImagePreview,Row,Col,Cell,Uploader,Loading } from "vant";
    import { Image as VanImage } from 'vant';
    import { Icon,XDialog,XButton} from 'vux';
    import Api from '@/api/Content.js'
    export default {
        components:{
            [Row.name]:Row,
            [Col.name]:Col,
            [Cell.name]:Cell,
            [Uploader.name]:Uploader,
            [VanImage.name]:VanImage,
            [ImagePreview.name]:ImagePreview,
            [Loading.name]:Loading,
            Icon,
            XDialog,
            XButton
        },
        name: "lano-uploader",
        props: {
            label: {},
            className: {},
            accept: {
                default: "*"
            },
            value: {
                type: Array
            }, // 绑定数据
            errorMessageAlign: {
                type: String
            },
            errorMessage: {
                type: String
            },
            required: {
                type: Boolean
            },
            max: {
                default: 1
            }, // 文件数量上限
            min: {
                type: String,
                default: undefined
            }, // 文件数量下限
            keyName: {
                default: "url"
            }, // url绑定的属性名，默认url
            beforeRead: {}, // 上传前的事件
            type: {
                type: String
            }
        },
        data() {
            return {
                fileList:
                    this.value && this.value[0] && this.value[0].attrValue && JSON.stringify(this.value[0]) != "{}"
                        ? this.value
                        : [],
                setTip:true,
                showDialog:false,
                selectInd:''
            };
        },
        methods: {
            beforeReadFun() {
                this.beforeRead && this.beforeRead();
                return true;
            },
            onUpload(files) {
            // console.log(files)
            if (files) {
              var formData = new FormData()
              formData.append('file', files.file)
              formData.append('folderId', 1308295372556206080)

              if (this.type === 'img') {
                const isJPG = (files.file.type === 'image/jpeg' || files.file.type === 'image/png')
                if (!isJPG) {
                  this.$vux.toast.text("上传图片只能为jpg或者png",'center');
                  return
                }
              }
              const isLt2M = files.file.size / 1024 / 1024 < 2
              if (!isLt2M) {
                this.$vux.toast.text("上传图片大小不能超过 2MB",'center');
                return
              }
              Api.updateImg(formData).then((res) => {
                if (res.data.code === 0) {
                  this.$vux.toast.text("上传成功",'center');
                  let obj = {};
                  obj[this.keyName] = res.data.data.url;
                  let tempList = JSON.parse(
                    JSON.stringify(this.fileList)
                  );
                  tempList.push(obj);
                  this.$set(this, "fileList", tempList);
                  this.setTip=false
                } else {
                  this.$vux.toast.text("文件上传失败",'center');

                }
              })
            }
          },
            showImage() {
                if (this.fileList[0].attrValue) {
                    ImagePreview({images: [this.fileList[0].attrValue], closeable: true, });
                }
            },
            // 请求服务器删除文件，和 属性值
            deleteFile(fileId) {
                Service.get("/pds/wx/patient/deleteAttrValue/" + fileId);
            },
            delFile(ind, file) {
                this.showDialog = true
                this.selectInd = ind
                // this.$dialog
                //     .confirm({
                //         message: "确定要删除此文件吗"
                //     })
                //     .then(() => {
                //         /*if (file && file.id) {
                //                 this.deleteFile(file.id);
                //             }*/
                //         this.fileList.splice(ind, 1);
                //     })
                //     .catch(() => {
                //     });
            },
            isImg(url) {
                if (url) {
                    let imgType = "jpg/jpeg/png/gif/svg/ico/webp/bmp";
                    let ind = url.lastIndexOf(".");
                    let suf = url.substring(ind + 1, url.length);
                    if (imgType.findIndex(suf) > -1) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            },
            detelBtn(){
                this.fileList.splice(this.selectInd, 1);
                this.showDialog = false
            },
            cancelBtn(){
                this.showDialog = false
            }
        },
        mouted() {
            // console.log(this);
        },
        watch: {
            fileList(newVal) {
                this.$emit("input", newVal);
            }
        }
    };
</script>
<style lang="less" scoped>
    .lano-uplodaer-container {
        padding: 10px 10px 10px 15px;
    }

    .required::before {
        position: absolute;
        left: 7px;
        color: #f44;
        font-size: 14px;
        content: "*";
    }

    .lano-uploader-label {
        color: #323233;
        // padding: 10px 0;
    }

    .lano-upload-wrap {
        display: flex;
        flex-wrap: wrap;
        position: relative;
    }

    .child {
        margin: auto;
        position: absolute;
        top: 0;
        left: 0;
        bottom: 0;
        right: 0;
    }

    .lano-upload-preview {
        display: inline-block;
        position: relative;
        margin: 0 8px 8px 0;

        /deep/ .van-image__img {
            width: 50px;
            height: 50px;
        }

        /deep/ .van-image {
            width: 50px;
            height: 50px;
        }

        /deep/ .van-image__error {
            .iconfont {
                font-size: 50px;
            }
        }

        .upload-img {
            width: 50px;
            height: 50px;
            line-height: 50px;
            text-align: center;
            display: block;
        }

        .iconRemarks {
            font-size: 50px;
        }

        .lano-uploader-del {
            position: absolute;
            bottom: 0;
            right: -5px;
            // background: #999;
            padding: 2px;

            .iconfont {
                color: #eee;
                font-size: 16px;
            }
        }
    }

    /deep/ .van-uploader {
        width: auto;
    }

    /deep/ .van-uploader__upload {
        width: 50px;
        height: 50px;
    }

    .error-msg {
        color: #f44;
        font-size: 12px;
        text-align: right;
        margin-right: 15px;
    }
</style>
