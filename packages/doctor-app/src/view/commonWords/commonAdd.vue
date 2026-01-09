<template>
  <section class="allContent">
    <nav-bar :pageTitle="pageTitle" @clickBack="back()" :back-function="true" :showRightIcon="commonId ? true : false" :rightIcon="require('@/assets/my/delete.png')" @toHistoryPage="deleteCommon"></nav-bar>
    <div style="width: 100%; overflow-y: scroll">
      <div style="background-color: white; padding: 10px 9px 0 10px">
        <div class="add-common-import-div" @click="templateJump()">
          <img :src="require('@/assets/common/fuzhimoban.png')" style="margin-right: 8px">
          <div>从常用语模版内导入</div>
          <div style="margin-left: 8px"><img :src="require('@/assets/common/right_double_arrow.png')"></div>
        </div>
      </div>
      <div style="background-color: white; padding-top: 29px; padding-left: 12px; padding-right: 12px;">
        <div class="add-common-first-title" style="margin-bottom: 12px">
          标题：
        </div>
        <div>
          <van-field
            class="add-common-type-field"
            v-model="form.title"
            maxlength="20"
            placeholder="请输入常用语的标题"
          >
            <template slot="left-icon">
              <div style="display: flex; align-items: center; height: 100%">
                <img :src="require('@/assets/common/edit_black.png')" style="width: 18px; height: 18px;">
              </div>
            </template>
            <template slot="right-icon">
              <div style="display: flex; align-items: center; margin-right: 5px">
                <div style="color: #999; font-size: 14px;">{{form.title ? form.title.length : 0}}</div>
                <div style="color: #999; font-size: 14px;">/</div>
                <div style="color: #999; font-size: 14px;">20</div>
              </div>
            </template>
          </van-field>
        </div>
        <div class="add-common-first-title" style="margin-top: 24px;">
          分类：
        </div>
      </div>
      <div style="background-color: white; padding-bottom: 10px; padding-top: 10px;">
        <div style="display: flex; flex-wrap: wrap; max-height: 100px; overflow-y: auto">
          <div @click="selectType(type)" class="add-common-type" :style="{backgroundColor: type.id === form.typeId ? 'var(--caring-common-title-icon-bg)' : 'var(--caring-common-type-no-select-bg)',
          color: type.id === form.typeId ? 'var(--caring-first-title-white)' : 'var(--caring-common-no-select)' }"
               v-for="(type, index) in typeList" :key="index">{{type.title}}</div>
        </div>
        <div @click="selectType({id: undefined, title: '', formTemplate: 2})" class="add-common-type" style="color: #999999;border: 1px dashed #999999; width: 88px;">
          <van-icon name="plus" color="#999999" size="13" /> 添加分类
        </div>
        <div style="padding-top: 15px; padding-left: 12px; padding-right: 12px;" v-if="form.typeForm === 2">
          <van-field
            class="add-common-type-field"
            v-model="form.typeName"
            maxlength="10"
            placeholder="请输入分类信息"
          >
            <template slot="left-icon">
              <div style="display: flex; align-items: center; height: 100%">
                <img :src="require('@/assets/common/edit_black.png')" style="width: 18px; height: 18px;">
              </div>
            </template>
            <template slot="right-icon">
              <div style="display: flex; align-items: center; margin-right: 5px">
                <div style="color: #999; font-size: 14px;">{{form.typeName ? form.typeName.length : 0}}</div>
                <div style="color: #999; font-size: 14px;">/</div>
                <div style="color: #999; font-size: 14px;">10</div>
              </div>
            </template>
          </van-field>
        </div>
      </div>
      <!-- 内容编辑 -->
      <div style="font-size: 14px; color: #666666; margin: 10px 0 10px 16px">
        内容编辑
      </div>
      <div style="background: white;">
        <van-field
          v-model="form.content"
          @input="contentInput"
          rows="8"
          type="textarea"
          placeholder="请输入自定义的常用语"
          maxlength="800"
        >
          <template slot="left-icon">
            <img v-show="!form.content" :src="require('@/assets/common/edit_black.png')">
          </template>
        </van-field>
        <div style="padding: 7px 10px; display: flex; justify-content: space-between; align-items: center">
          <div style="display: flex; align-items: center" >
          </div>
          <div style="display: flex; align-items: center; margin-right: 5px">
            <div style="color: #999; font-size: 14px;">{{form.content ? form.content.length : 0}}</div>
            <div style="color: #666; font-size: 14px;">/</div>
            <div style="color: #999; font-size: 14px;">800</div>
          </div>
        </div>
      </div>
      <div style="margin-top: 30px; margin-bottom: 20px; width: 100%;" >
        <van-button type="info"
                    round
                    @click="addCommon"
                    :disabled="form.content.length === 0 || (!form.title || form.title.length === 0) || (form.typeForm === 2 && form.typeName === ''  ) "
                    :style="{backgroundColor: form.content.length === 0 || (!form.title || form.title.length === 0) || (form.typeForm === 2 && form.typeName === ''  ) ? 'var(--caring-common-button-disable-bg)' : 'var(--caring-common-button-default-bg)'}"
                    style="display: block; width: 95%; height: 42px; margin: 0 auto; border: 0px"
        >
          {{submitButton}}
        </van-button>
      </div>
      <!--  删除  -->
      <van-dialog v-model="isDeleteDialog" show-cancel-button confirmButtonColor="#2F8DEB"
                  @confirm="confirmDelete()"
                  @cancel="() => {isDeleteDialog = false}"
                  confirmButtonText="确定">
        <div style="display: flex; flex-direction: column; align-items: center; padding: 20px 30px">
          <img :src="require('@/assets/my/careful.png')" style="margin-left: 5px" width="60" height="60">
          <div
            style="font-size: 18px; color: #333333; margin-top: 12px; margin-bottom: 5px; margin-left: 5px">
            确定删除吗？
          </div>
          <div style="color: #333; font-size: 18px; text-align: center">
            删除后内容不可恢复！
          </div>
        </div>
      </van-dialog>
    </div>
  </section>
</template>

<script>
  import Vue from 'vue';
  import {Field, Button} from 'vant';
  import Api from '@/api/doctor.js'
  import CommonApi from '@/api/common.js'
  import navBar from "@/components/headers/navBar";
  Vue.use(Button)
  Vue.use(Field)
  export default {
    name: "commonAdd",
    components: {
      navBar
    },
    data() {
      return {
        addTypeStatus: false,
        commonId: undefined,
        pageTitle: '添加常用语',
        submitButton: '确认添加',
        isDeleteDialog: false,
        disableSubmit: true,
        typeList: [],
        doctorId: localStorage.getItem('caring_doctor_id'),
        userType: 'doctor',
        form: {
          typeId: undefined, // 选择的分类ID。 自定义时 不需要
          typeName: '', // 分类的名称，
          typeForm: undefined, // 分类的来源，0 自己的分类，1 模版分类， 2 自定义分类名称
          title: '',  // 常用语的标题
          content: '', // 常用语的内容
          userType: 'doctor'  // 用户的角色
        },
        message: '',
      }
    },
    created() {
      this.queryTypeList()
      if ( this.$route.query.id) {
        this.commonId = this.$route.query.id
        this.pageTitle = '编辑常用语'
        this.submitButton = '确认提交'
        this.getDoctorCommon()
      }
      if (this.$route.query.templateContentId) {
        this.getTemplateDetail(this.$route.query.templateContentId)
      }
    },
    methods: {
      contentInput() {
        if (this.form.content.length > 0) {
          this.disableSubmit = false
        } else {
          this.disableSubmit = true
        }
      },
      getDoctorCommon() {
        CommonApi.getDoctorCommon(this.commonId).then(res => {
          if (res.data.code === 0) {
            this.form = res.data.data
            this.form.typeForm = 0
            this.contentInput()
          }
        })
      },
      // 选择分类
      selectType(type) {
        this.form.typeId = type.id
        this.form.typeName = type.title
        this.form.typeForm = type.formTemplate
      },

      // 选择了一个常用语模版，直接查询模版信息并覆盖内容
      getTemplateDetail(templateContentId) {
        CommonApi.getComMsgTemplate(templateContentId).then(res => {
          console.log('getComMsgTemplate', res.data.data)
          if (res.data.code === 0) {
            const data = res.data.data
            this.form.typeForm = 1
            this.form.typeId = data.templateTypeId
            this.form.title = data.templateTitle
            this.form.content = data.templateContent
            this.form.accountId = this.doctorId
            this.form.userType = 'doctor'
            this.contentInput()
           // this.form.sourceTemplateId = templateContentId 如果添加方式选择的常用语模版需要记录。就放开
          }
        })
      },

      // 查询分类列表
      queryTypeList() {
        CommonApi.commonTypeAll(this.doctorId, this.userType).then(res => {
          if (res.data.code === 0) {
            this.typeList = res.data.data
          }
        })
      },
      // 跳转到模板
      templateJump() {
        this.$router.replace({
          path: '/common/commonTemplate',
          query: {
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            message: this.$route.query.message,
            isCallBack: true,
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
      },
      // 删除这个常用语
      deleteCommon() {
        if (this.id === '') {
          this.$vux.toast.text("未查询到该常用语", 'center');
          return
        }
        this.isDeleteDialog = true
      },
      //后退
      back() {
        this.$router.replace({
          path: '/common/commonList',
          query: {
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            message: this.$route.query.message,
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
      },
      // 删除
      confirmDelete() {
        Api.deleteCommon(this.commonId)
          .then(res => {
            this.$vux.toast.text("删除成功", 'center');
            this.$router.replace({
              path: '/common/commonList',
              query: {
                imAccount: this.$route.query.imAccount,
                imPatientId: this.$route.query.imPatientId,
                message: this.$route.query.message,
                formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
              }
            })
          })
      },
      addCommon() {
        if (this.disableSubmit) {
          this.$vux.toast.text("请输入常用语", 'center');
          return;
        }
        this.form.accountId = this.doctorId; // 用户ID， 医生iD
        CommonApi.saveOrUpdateCommonMsgAndType(this.form)
          .then((res) => {
            this.$vux.toast.text("添加成功", 'center');
            this.$router.replace({
              path: '/common/commonList',
              query: {
                imAccount: this.$route.query.imAccount,
                imPatientId: this.$route.query.imPatientId,
                message: this.$route.query.message,
                formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
              }
            })
          })
      },
    },
  }
</script>

<style lang='less' scoped  src="./commonClass.less">
</style>
<style scoped lang='less'>

  .allContent {
    width: 100vw;
    height: 100vh;
    position: relative;
    background-color: #f5f5f5;
  }

  /deep/ .vux-header {
    margin-bottom: 0px;
    height: 50px;
    position: fixed;
    width: 100%;
    z-index: 999;
    top: 0;
    left: 0;
  }


</style>
