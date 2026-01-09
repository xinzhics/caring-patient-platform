<template>
  <section class="allcont">
    <x-header :left-options="{ backText: '' }">个人资料</x-header>
    <div>
      <group>
        <cell title="头像" is-link>

          <span style="padding: 0px 10px">
            <van-uploader v-if="baseInfo.avatar" :accept="'image/*'" :before-read="beforeRead">
              <img :src="baseInfo.avatar" alt="等待传图" class="imgPreview"
                   style="width: 2.4rem; height: 2.4rem; border-radius: 50%"/>
            </van-uploader>
            <span v-else>-</span>
          </span>
        </cell>
        <van-cell title="姓名" is-link @click="show2 = true;nameVal = baseInfo.name;">
          <span style="padding: 0px 10px">{{ baseInfo.name || "-" }}</span>
        </van-cell>
      </group>
      <group>
        <van-cell title="医院" is-link @click="show3 = true; hospitalVal = baseInfo.hospitalName">
          <span style="padding: 0px 10px">{{ baseInfo.hospitalName || "-" }}</span>
        </van-cell>
        <van-popup v-model="show3" position="bottom" :style="{ height: '100%' }">
          <section style="background: #fafafa">
            <van-row align="center" style="height: 50px" class="headTitle">
              <van-col offset="1" span="9">
                <van-icon name="arrow-left" @click="hide"/>
              </van-col>
              <van-col span="12">选择医院</van-col>
            </van-row>
            <van-search v-model="hospitalName" placeholder="搜索" @input="onInput" @blur="onBlur" @focus="onFocus"
                        @search="onSearch" show-action>
              <div slot="action" @click="onSearch">
                <van-button type="info" @click="onSearch">搜索</van-button>
              </div>
            </van-search>
            <!-- 三个选项 -->
            <van-row type="flex" justify="space-between" align="center" style="height: 48px" v-show="type !== 4">
              <van-col offset="2" @click="chooseOptions(1)" span="8" :style="{ color: type >= 1 ? '#337EFF' : '' }">省份
                <van-icon name="arrow-down" :style="{ color: type >= 1 ? '#337EFF' : '' }"/>
              </van-col>
              <van-col span="8" @click="chooseOptions(2)" :style="{ color: type >= 2 ? '#337EFF' : '' }">城市
                <van-icon name="arrow-down" :style="{ color: type >= 2 ? '#337EFF' : '' }"/>
              </van-col>
              <van-col span="6" @click="chooseOptions(3)" :style="{ color: type === 3 ? '#337EFF' : '' }">医院
                <van-icon name="arrow-down" :style="{ color: type === 3 ? '#337EFF' : '' }"/>
              </van-col>
            </van-row>
            <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
              <van-list v-if="focus" v-model="loading" :finished="finished" :finished-text="focus?'没有更多了':''"
                        @load="onLoad">
                <div class="showData" v-for="(i, index) in list" :key="index">
                  <div class="options" @click="chooseOption(i)">
                    {{ getItem(i) }}
                  </div>
                </div>
              </van-list>
            </van-pull-refresh>
          </section>
        </van-popup>
        <van-cell title="科室" is-link @click="show4 = true; sectionOfficeVal = baseInfo.deptartmentName">
          <span style="padding: 0px 10px">{{ baseInfo.deptartmentName || "-" }}</span>
        </van-cell>
        <van-cell title="职称" is-link @click="show5 = true; justifyVal = baseInfo.title">
          <span style="padding: 0px 10px">{{ baseInfo.title || "-" }}</span>
        </van-cell>
        <van-cell title="专业特长" is-link @click="show7 = true; specialties = baseInfo.extraInfo.Specialties">
          <div slot="default" style="padding: 0px 10px">
            {{ baseInfo.extraInfo.Specialties || "-" }}
          </div>
        </van-cell>
        <van-cell title="详细介绍" is-link @click="show8 = true;introduction = baseInfo.extraInfo.Introduction">
          <span slot="default" style="padding: 0px 10px">{{ baseInfo.extraInfo.Introduction || "-" }}</span>
        </van-cell>
      </group>
      <group>
        <van-cell title="手机号">
          <span style="padding: 0px 10px">{{ baseInfo.mobile || "-" }}</span>
        </van-cell>
      </group>

      <van-cell title="密码设置" is-link @click="toPassword()">
      </van-cell>
      <van-cell v-if="officialAccountType === 'PERSONAL_SERVICE_NUMBER'" title="切换身份" is-link @click="selectRole()">
      </van-cell>
    </div>
    <div style="padding-bottom: 40px">
      <x-button style="
        width: 60vw;
        background: rgb(102, 114, 139);
        color: #fff;
        margin-top: 40px;
      " @click.native="show6 = !show6">
        退出登录
      </x-button>
    </div>

    <van-popup
      :lock-scroll="false"
      duration="0"
      v-model="showCropper"
      position="top"
      :style="{ height: '100%' }"
    >
      <div class="cropper-container">
        <vueCropper
          style="background-image:none;background: #000"
          ref="cropper"
          :img="option.img"
          :outputSize="option.size"
          :outputType="option.outputType"
          :info="true"
          :full="option.full"
          :canMove="option.canMove"
          :canMoveBox="option.canMoveBox"
          :original="option.original"
          :autoCrop="option.autoCrop"
          :fixed="option.fixed"
          :centerBox="option.centerBox"
          :infoTrue="option.infoTrue"
          :fixedBox="option.fixedBox"
          :auto-crop-width="option.autoCropWidth"
          :auto-crop-height="option.autoCropHeight"
          @realTime="realTime"
        ></vueCropper>
        <van-nav-bar
          left-text="取消"
          right-text="完成"
          @click-left="onClickLeft"
          @click-right="getCropBlob"
        />
      </div>
    </van-popup>

    <!-- 姓名弹窗 -->
    <van-dialog v-model="show2" class="mydialog" :show-confirm-button="false">
      <div style="margin: 20px 10% 10px; width: 80%; text-align: left">
        <input style="
            border: none;
            border-bottom: 1px solid #b8b8b8;
            width: 80%;
            color: #333;
          " v-model="nameVal"/><span :style="{
            color: nameVal && nameVal.length > 10 ? '#f00' : '#999',
            fontSize: '14px',
          }">{{ nameVal ? nameVal.length : 0 }}/10</span>
        <p :style="{
            color: nameVal && nameVal.length > 10 ? '#f00' : '#999',
            fontSize: '14px',
            lineHeight: '26px',
          }">
          姓名不得超过10个字
        </p>
        <x-button style="
            margin-top: 10px;
            width: 40%;
            font-size: 14px;
            background: #3f86ff;
            color: #fff;
          " @click.native="sureName">确定
        </x-button>
      </div>
    </van-dialog>
    <!-- 科室弹窗 -->
    <van-dialog v-model="show4" class="mydialog" :show-confirm-button="false">
      <div style="margin: 20px 10% 10px; width: 80%; text-align: left">
        <input style="
            border: none;
            border-bottom: 1px solid #b8b8b8;
            width: 80%;
            color: #333;
          " v-model="sectionOfficeVal"/><span :style="{
            color:
              sectionOfficeVal && sectionOfficeVal.length > 20
                ? '#f00'
                : '#999',
            fontSize: '14px',
          }">{{ sectionOfficeVal ? sectionOfficeVal.length : 0 }}/20</span>
        <p :style="{
            color:
              sectionOfficeVal && sectionOfficeVal.length > 20
                ? '#f00'
                : '#999',
            fontSize: '14px',
            lineHeight: '26px',
          }">
          科室不得超过20个字
        </p>
        <x-button style="
            margin-top: 10px;
            width: 40%;
            font-size: 14px;
            background: #3f86ff;
            color: #fff;
          " @click.native="suresectionOffice">确定
        </x-button>
      </div>
    </van-dialog>
    <!-- 职称弹窗 -->
    <van-dialog v-model="show5" class="mydialog" :show-confirm-button="false">
      <div style="margin: 20px 10% 10px; width: 80%; text-align: left">
        <input style="
            border: none;
            border-bottom: 1px solid #b8b8b8;
            width: 80%;
            color: #333;
          " v-model="justifyVal"/><span :style="{
            color: justifyVal && justifyVal.length > 20 ? '#f00' : '#999',
            fontSize: '14px',
          }">{{ justifyVal ? justifyVal.length : 0 }}/20</span>
        <p :style="{
            color: justifyVal && justifyVal.length > 20 ? '#f00' : '#999',
            fontSize: '14px',
            lineHeight: '26px',
          }">
          职称不得超过20个字
        </p>
        <x-button style="
            margin-top: 10px;
            width: 40%;
            font-size: 14px;
            background: #3f86ff;
            color: #fff;
          " @click.native="surejustify">确定
        </x-button>
      </div>
    </van-dialog>
    <!-- 专业特长 -->
    <van-dialog v-model="show7" class="mydialog" :show-confirm-button="false">
      <div style="margin: 20px 5% 10px; text-align: left">
        <x-textarea :max="500" style=" border: 1px solid #f5f5f5" v-model="specialties"></x-textarea>
        <x-button style="
            margin-top: 10px;
            width: 40%;
            font-size: 14px;
            background: #3f86ff;
            color: #fff;"
                  @click.native="surespecialties">确定
        </x-button>
      </div>
    </van-dialog>
    <!-- 详细介绍 -->
    <van-dialog v-model="show8" class="mydialog" :show-confirm-button="false">
      <div style="margin: 20px 5% 10px; text-align: left">
        <x-textarea :max="500" style="border: 1px solid #f5f5f5" v-model="introduction"></x-textarea>
        <x-button style="
            margin-top: 10px;
            width: 40%;
            font-size: 14px;
            background: #3f86ff;
            color: #fff;
          " @click.native="sureintroduction">确定
        </x-button>
      </div>
    </van-dialog>
    <van-dialog v-model="show6" class="mydialog" :show-confirm-button="false">
      <div style="margin: 20px 10% 10px; width: 80%; text-align: left">
        <p>确定退出？</p>
        <div style="display: flex; justify-content: space-between">
          <x-button style="
              width: 20vw;
              color: #333;
              margin-top: 20px;
              border: 1px solid #eee;
            " type="default" mini @click.native="cenlcesubmit">取消
          </x-button>
          <x-button style="
              width: 20vw;
              background: rgb(102, 114, 139);
              color: #fff;
              margin-top: 20px;
            " mini @click.native="submit">确定
          </x-button>
        </div>
      </div>
    </van-dialog>
  </section>
</template>
<script>
import Api from '@/api/doctor.js'
import ApiT from '@/api/Content.js'

import VueCropper from "vue-cropper/src/vue-cropper.vue"
import "vue-cropper/next/dist/index.css"
import {XTextarea} from 'vux'
import Vue from 'vue'
import {Search, DropdownMenu, DropdownItem, Button, Overlay, Popup, NavBar, Dialog, Cell} from 'vant'

Vue.use(Button)
Vue.use(Search)
Vue.use(DropdownMenu)
Vue.use(DropdownItem)
Vue.use(Overlay);
Vue.use(Popup);
Vue.use(NavBar);
Vue.use(Cell);
Vue.use(Dialog)
export default {
  name: 'infoDetail',
  components: {
    XTextarea, VueCropper
  },
  data() {
    return {
      officialAccountType: localStorage.getItem('officialAccountType'),
      headerImg: 'https://caing-test.obs.cn-north-4.myhuaweicloud.com/23a176879c0c5675556687e5718cc82.png',
      show2: false,
      name: '',
      nameVal: '',
      show3: false,
      hospitalVal: '',
      show4: false,
      sectionOffice: '',
      sectionOfficeVal: '',
      show5: false,
      justifyVal: '',
      show6: false,
      telephoneVal: '',
      baseInfo: {
        extraInfo: {
          Specialties: '',
          Introduction: ''
        }
      },
      specialties: '',
      introduction: '',
      show7: false,
      show8: false,

      // 医院数据
      list: [],
      loading: false,
      finished: false,
      refreshing: false,
      provinceId: '',
      cityId: '',
      // 请求参数
      params: {
        order: 'ascending',
        current: 1,
        model: {},
        size: 100
      },
      type: 1,
      beforeSearchType: 1,
      hospitalName: '',
      focus: true,
      issousuo: 0,
      doctorOpenId: '',
      //  裁剪图片数据
      imageFileName: '',
      showCropper: false, // 裁剪的弹窗
      option: {
        img: '', // 裁剪图片的地址
        info: true, // 裁剪框的大小信息
        outputSize: 0.8, // 裁剪生成图片的质量
        outputType: 'jpeg', // 裁剪生成图片的格式
        canScale: true, // 图片是否允许滚轮缩放
        autoCrop: true, // 是否默认生成截图框
        autoCropWidth: 200, // 默认生成截图框宽度
        autoCropHeight: 200, // 默认生成截图框高度
        fixedBox: false, // 固定截图框大小 不允许改变
        fixed: true, // 是否开启截图框宽高固定比例
        //fixedNumber: [2.35, 1], // 截图框的宽高比例
        full: true, // 是否输出原图比例的截图
        canMoveBox: true, // 截图框能否拖动
        original: false, // 上传图片按照原始比例渲染
        centerBox: false, // 截图框是否被限制在图片里面
        infoTrue: true // true 为展示真实输出图片宽高 false 展示看到的截图框宽高
      },
      loadingShow: false, // 是否展示loading
    }
  },
  mounted() {
    if (!localStorage.getItem('doctortoken') || !localStorage.getItem('caring_doctor_id') || !localStorage.getItem('headerTenant')) {
      this.$router.replace('/')
    }
    this.getInfo()
  },
  created() {
    this.pageProvince()
  },
  methods: {
    // 跳转到
    toPassword() {
      this.$router.push({
        path: '/doctorInfoDetail/updatePsd'
      })
    },
    //裁剪图片
    realTime(data) {
      this.$refs.cropper.getCropData(data => {
        this.imageBase64 = 'data:image/jpeg;base64' + data
      })
    },
    beforeRead(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      if (!isJPG) {
        this.$vux.toast.text('上传图片只能为jpg或者png', 'center')
        return
      }
      this.imageFileName = file.name
      this.imageToBase64(file)
      this.showCropper = true
    },
    selectRole() {
      let url = window.location.href.substring(0, window.location.href.indexOf('/'));
      window.location.href = url + '/wx/select/role?reSelectRole=true'
    },
    imageToBase64(file) {
      let reader = new FileReader()
      let that = this
      reader.readAsDataURL(file)
      reader.onload = () => {
        that.option.img = reader.result
        console.log(that.option.img)
      }

    },
    getCropBlob() {
      // let formData = new FormData()
      this.loadingShow = true
      this.$refs.cropper.getCropBlob((data) => {
        console.log(data)
        var formData = new FormData()
        formData.append('file', data, this.imageFileName)
        formData.append('folderId', 1308295372556206080)
        ApiT.updateImg(formData).then(res => {
          if (res.data.code === 0) {
            this.baseInfo.avatar = res.data.data.url
            this.$vux.toast.text('上传成功', 'center')
            this.showCropper = false
            this.putContent()
          } else {
            this.$vux.toast.text('文件上传失败', 'center')
          }
        })
        this.$emit('update:showAta', false)
      })
    },
    onClickLeft() {
      this.loadingShow = false
      this.showCropper = false
      this.$parent.showPhotoUploader = false
    },
    // 医院方法
    onRefresh() {
      // 清空列表数据
      this.finished = false
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true
      this.onLoad()
    },
    onLoad() {
      setTimeout(() => {
        if (this.refreshing) {
          this.list = []
          this.params.current = 1
          this.refreshing = false
        }
        if (this.type === 1) {
          this.pageProvince()
        } else if (this.type === 2) {
          this.pageCity()
        } else if (this.type === 3 || this.type === 4) {
          this.hospitalPage()
        }
      }, 500)
    },
    // 点省
    chooseOptions(count) {
      if (count === 1 && this.type !== 1) {
        this.list = []
        this.type = 1
        this.pageProvince()
      }
      if (count === 2 && this.type !== 2 && this.provinceId) {
        this.list = []
        this.type = 2
        this.params.current = 1
        this.pageCity()
      }
      if (count === 3 && this.type !== 3 && this.cityId) {
        this.list = []
        this.type = 3
        this.params.current = 1
        this.hospitalPage()
      }
    },
    // 点省选项
    chooseOption(val) {
      this.finished = false
      if (this.type == 1) {
        // this.params.model.provinceId = val.id
        this.provinceId = val.id
        this.params.current = 1
        this.type = 2
        this.list = []
        this.finished = false
        this.pageCity()
      } else if (this.type === 2) {
        this.cityId = val.id
        this.params.current = 1
        this.type = 3
        this.list = []
        this.finished = false

        this.hospitalPage()
      } else {
        console.log(val);
        this.chooseHospitalOption2(val)
        this.surehospital()
      }
    },
    chooseHospitalOption2(val) {
      let arr = {
        valueText: val.hospitalName,
        attrValue: val.id
      }
      console.log(val.hospitalName);
      this.hospitalVal = val.hospitalName
      this.hide()
    },
    hide() {
      this.hospitalName = ''
      this.list = []
      this.type = 1
      this.cityId = ''
      this.provinceId = ''
      this.pageProvince()
      this.show3 = false
    },
    // 获取焦点
    onFocus() {
      this.focus = false
      document.body.style.overflow = 'hidden'
    },

    onBlur() {
      this.focus = true
      document.body.style.overflow = 'visible'
    },
    // 点搜索
    onSearch() {
      if (this.issousuo === 2) {
        this.type = 4;
        this.refreshing = true
        this.finished = false;
        this.issousuo = 1
      } else {
        this.issousuo = 3
      }
      console.log(this.issousuo);
    },
    onInput(val) {
      if (val !== '') {
        this.list = []
        this.issousuo = 2
      }
      if (val === "") {
        this.type = this.beforeSearchType
        this.refreshing = true
        this.finished = false;
        this.onLoad()
      }
    },
    // 渲染数据
    getItem(item) {
      if (this.type === 1) {
        return item.province
      } else if (this.type === 2) {
        return item.city
      } else {
        return item.hospitalName
      }
    },

    // 分页查询省
    pageProvince() {
      this.params.current = 1
      Api.listByIds(this.params).then(res => {
        if (res.data && res.data.code === 0) {
          if (this.params.current === 1) {
            this.list = res.data.data.records
          } else {
            this.list.push(...res.data.data.records)
          }

          this.loading = false
          if (res.data.data.records && res.data.data.records.length < 100) {
            this.finished = true
          }
          this.params.current++
        }
      })
    },
    // 分页查询市
    pageCity() {
      this.params.model.provinceId = this.provinceId
      Api.listByCity(this.params).then(res => {
        if (res.data && res.data.code === 0) {
          if (res.data && res.data.code === 0) {
            if (this.params.current === 1) {
              this.list = res.data.data.records
            } else {
              this.list.push(...res.data.data.records)
            }

            this.params.current++
            this.loading = false
            if (res.data.data.records && res.data.data.records.length < 100) {
              this.finished = true
            }
          }
        }
      })
    },
    // 查医院
    hospitalPage() {
      this.params.model = {
        cityId: this.cityId,
        hospitalName: this.hospitalName,
        provinceId: this.provinceId
      }

      Api.hospitalPage(this.params).then(res => {
        if (res.data && res.data.code === 0) {
          if (res.data && res.data.code === 0) {
            if (this.params.current === 1) {
              this.list = res.data.data.records
            } else {
              this.list.push(...res.data.data.records)
            }
            this.params.current++
            this.loading = false
            if (res.data.data.records && res.data.data.records.length < 100) {
              this.finished = true
            }
          }
        }
      })
    },

    showPopup() {
      this.show3 = true
    },

    getInfo() {
      const params = {
        id: localStorage.getItem('caring_doctor_id')
      }
      if (!params.id) {
        return
      }
      Api.getContent(params).then(res => {
        if (res.data.code === 0) {
          if (res.data.data.extraInfo) {
            res.data.data.extraInfo = JSON.parse(res.data.data.extraInfo)
          } else {
            res.data.data.extraInfo = {Specialties: '', Introduction: ''}
          }
          this.baseInfo = res.data.data
          if (!res.data.data.avatar) {
            this.baseInfo.avatar = this.headerImg
          }
          this.doctorOpenId = this.baseInfo.openId
        }
      })
    },
    sureName() {
      if (this.nameVal && this.nameVal.length > 10) {
        return
      } else {
        this.show2 = false
        this.baseInfo.name = this.nameVal
        this.putContent()
      }
    },
    surehospital() {
      this.show3 = false
      this.baseInfo.hospitalName = this.hospitalVal
      console.log(this.baseInfo, "===========================>");
      this.putContent()

    },
    suresectionOffice() {
      if (this.sectionOfficeVal && this.sectionOfficeVal.length > 20) {
        return
      } else {
        this.show4 = false
        this.baseInfo.deptartmentName = this.sectionOfficeVal
        this.putContent()
      }
    },
    surejustify() {
      if (this.justifyVal && this.justifyVal.length > 20) {
        return
      } else {
        this.show5 = false
        this.baseInfo.title = this.justifyVal
        this.putContent()
      }
    },
    surespecialties() {
      this.show7 = false
      this.baseInfo.extraInfo.Specialties = this.specialties
      this.putContent()
    },
    sureintroduction() {
      this.show8 = false
      this.baseInfo.extraInfo.Introduction = this.introduction
      this.putContent()
    },
    putContent() {
      const params = {
        id: this.baseInfo.id,
        name: this.baseInfo.name,
        hospitalName: this.baseInfo.hospitalName,
        title: this.baseInfo.title,
        deptartmentName: this.baseInfo.deptartmentName,
        mobile: this.baseInfo.mobile,
        avatar: this.baseInfo.avatar,
        extraInfo: JSON.stringify(this.baseInfo.extraInfo)
      }
      console.log();
      Api.putContent(params).then(res => {
        if (res.data.code === 0) {
          console.log(res, "------------------------------>>>>>>");
          this.$vux.toast.text('修改成功', 'center')
        }
      })
    },
    submit() {
      ApiT.getExit().then(res => {
        if (res.data.code === 0) {
          localStorage.removeItem('caring_doctor_id')
          localStorage.removeItem('doctortoken')
          // 退出登录是。openId是存在的。就传到登录页避免再次找后端要openId
          this.$router.replace({
            path: '/',
            query: {
              "openId": this.doctorOpenId
            }
          })
        } else {
          this.$vux.toast.text('退出登录失败', 'center')
        }
      })
    },
    cenlcesubmit() {
      this.show6 = false
    }
  }
}
</script>
<style lang="less" scoped>
.cropper-container {
  width: auto;
  height: 93vh;

  .van-nav-bar {
    height: 7vh;
    background-color: rgba(0, 0, 0, 1);

    :global(.van-nav-bar__text) {
      color: #fff;
    }

    :global(.van-nav-bar__text:nth-child(2)) {
      color: #000;
      font-weight: 500;
    }
  }
}

.allcont {
  width: 100vw;
  height: 100vh;
  background: #fafafa;
}

// .mydialog {padding: 20px 0px;}

// 医院弹窗样式
/deep/ .van-search {
  background: #f0f0f0;

  .van-search__content {
    background: #fff;
    height: 46px;
    border-radius: 4px;
    padding: 0;

    .van-cell {
      padding: 0 !important;
      line-height: 46px;
      margin: 0 !important;

      .van-field__left-icon {
        margin-left: 13px;
        margin-right: 6px;

        i {
          font-size: 15px;
          color: #000;
          margin-top: 0 !important;
        }
      }

      .van-cell__value {
        padding: 0 !important;

        .van-field__body {
          // line-height: 35px;  //搜索字体
          ::-webkit-input-placeholder {
            /*Webkit browsers*/
            color: rgba(0, 0, 0, 0.25);
            font-size: 15px;
          }
        }
      }
    }
  }
}

/deep/ .weui-cells {
  .weui-cell__ft {
    max-width: 250px;

    span {
      padding: 0 !important;
    }
  }
}

/deep/ .van-icon {
  display: flex !important;
  align-items: center;
}

.select {
  width: 100%;
  background-color: #fff;
  height: 48px;
}

.showData {
  width: 100%;
  background-color: #fff;

  .options {
    padding-left: 13px;
    border-bottom: 1px solid #ececec;
    min-height: 48px;
    line-height: 48px;
    color: #666666;
    font-size: 16px;
    font-family: Source Han Sans CN;
  }
}

// /deep/.van-col{
//   height: 50px;
// }
/deep/ .headTitle {
  .van-col {
    height: 50px;
    line-height: 50px;
  }
}

.searchList {
  background-color: #fff;
  padding-left: 13px;
  color: #666;
  min-height: 48px;
  border-bottom: 1px solid #ececec;
  line-height: 48px;
}

/deep/ .van-icon-clear {
  margin-top: 0 !important;
}
</style>
