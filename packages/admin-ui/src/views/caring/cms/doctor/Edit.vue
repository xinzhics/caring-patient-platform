<template>
  <el-dialog :close-on-click-modal="false" :close-on-press-escape="true" :title="title" :type="type"
             :visible.sync="isVisible" :width="width" top="50px" v-el-drag-dialog>
    <el-form :model="doctor" :rules="rules" label-position="right" label-width="100px" ref="form">
      <el-form-item :label="$t('table.doctor.name')" prop="name">
        <el-input type="" v-model="doctor.name" placeholder="医生名字"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.groupId')" prop="groupId">
        <el-input type="" v-model="doctor.groupId" placeholder="小组ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.remarks')" prop="remarks">
        <el-input type="" v-model="doctor.remarks" placeholder="备注"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.groupName')" prop="groupName">
        <el-input type="" v-model="doctor.groupName" placeholder="小组名字"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.nursingId')" prop="nursingId">
        <el-input type="" v-model="doctor.nursingId" placeholder="服务专员ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.nursingName')" prop="nursingName">
        <el-input type="" v-model="doctor.nursingName" placeholder="服务专员"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.organId')" prop="organId">
        <el-input type="" v-model="doctor.organId" placeholder="机构ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.province')" prop="province">
        <el-input type="" v-model="doctor.province" placeholder="省"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.city')" prop="city">
        <el-input type="" v-model="doctor.city" placeholder="市"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.hospitalId')" prop="hospitalId">
        <el-input type="" v-model="doctor.hospitalId" placeholder="医院"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.departmentId')" prop="departmentId">
        <el-input type="" v-model="doctor.departmentId" placeholder="科室"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.title')" prop="title">
        <el-input type="" v-model="doctor.title" placeholder="职称"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.mobile')" prop="mobile">
        <el-input type="" v-model="doctor.mobile" placeholder="手机号码"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.avatar')" prop="avatar">
        <el-input type="" v-model="doctor.avatar" placeholder="头像"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.nickName')" prop="nickName">
        <el-input type="" v-model="doctor.nickName" placeholder="昵称"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.idCard')" prop="idCard">
        <el-input type="" v-model="doctor.idCard" placeholder="身份证号"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.idCardImgF')" prop="idCardImgF">
        <el-input type="" v-model="doctor.idCardImgF" placeholder="身份证正面照"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.idCardImgB')" prop="idCardImgB">
        <el-input type="" v-model="doctor.idCardImgB" placeholder="身份证背面照"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.wxAppId')" prop="wxAppId">
        <el-input type="" v-model="doctor.wxAppId" placeholder="所属公众号ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.openId')" prop="openId">
        <el-input type="" v-model="doctor.openId" placeholder="微信openid"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.imAccount')" prop="imAccount">
        <el-input type="" v-model="doctor.imAccount" placeholder="环信账号"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.hospitalName')" prop="hospitalName">
        <el-input type="" v-model="doctor.hospitalName" placeholder="医生姓名"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.qrCode')" prop="qrCode">
        <el-input type="" v-model="doctor.qrCode" placeholder="患者关注码"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.businessCardQrCode')" prop="businessCardQrCode">
        <el-input type="" v-model="doctor.businessCardQrCode" placeholder="医生名片 跟患者关住码一样的"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.deptartmentName')" prop="deptartmentName">
        <el-input type="" v-model="doctor.deptartmentName" placeholder="科室名称"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.organCode')" prop="organCode">
        <el-input type="" v-model="doctor.organCode" placeholder="所属机构代码"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.organName')" prop="organName">
        <el-input type="" v-model="doctor.organName" placeholder="所属机构名称"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.birthday')" prop="birthday">
        <el-input type="" v-model="doctor.birthday" placeholder="出生年月"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.lastLoginIp')" prop="lastLoginIp">
        <el-input type="" v-model="doctor.lastLoginIp" placeholder="最后登录IP"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.totalLoginTimes')" prop="totalLoginTimes">
        <el-input type="" v-model="doctor.totalLoginTimes" placeholder="登录次数"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.sex')" prop="sex">
        <el-input type="" v-model="doctor.sex" placeholder="性别 0:男 1：女"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.lastLoginTime')" prop="lastLoginTime">
        <el-input type="" v-model="doctor.lastLoginTime" placeholder="最后登录时间，时间戳类型"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.extraInfo')" prop="extraInfo">
        <el-input type="" v-model="doctor.extraInfo" placeholder="扩展内容"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.loginName')" prop="loginName">
        <el-input type="" v-model="doctor.loginName" placeholder="默认医生的登录名"/>
      </el-form-item>
      <el-form-item :label="$t('table.doctor.classCode')" prop="classCode">
        <el-input type="" v-model="doctor.classCode" placeholder="权限代码"/>
      </el-form-item>
    </el-form>
    <div class="dialog-footer" slot="footer">
      <el-button @click="isVisible = false" plain type="warning">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button @click="submitForm" :disabled="confirmDisabled" plain type="primary">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import elDragDialog from '@/directive/el-drag-dialog'
import doctorApi from "@/api/Doctor.js";

export default {
  name: "DoctorEdit",
  directives: { elDragDialog },
  components: {  },
  props: {
    dialogVisible: {
      type: Boolean,
      default: false
    },
    type: {
      type: String,
      default: "add"
    }
  },
  data() {
    return {
      confirmDisabled: false,
      doctor: this.initDoctor(),
      screenWidth: 0,
      width: this.initWidth(),
      rules: {

      },
      // 枚举
      enums: {
      },
      // 字典
      dicts: {
      }
    };
  },
  computed: {
    isVisible: {
      get() {
        return this.dialogVisible;
      },
      set() {
        this.close();
        this.reset();
      }
    },
    title() {
      return this.$t("common." + this.type);
    }
  },
  watch: {},
  mounted() {
    window.onresize = () => {
      return (() => {
        this.width = this.initWidth();
      })();
    };
  },
  methods: {
    initDoctor() {
      return {
        id: "",
        name: '',
        groupId: null,
        remarks: '',
        groupName: '',
        nursingId: null,
        nursingName: '',
        organId: null,
        province: '',
        city: '',
        hospitalId: null,
        departmentId: '',
        title: '',
        mobile: '',
        avatar: '',
        nickName: '',
        idCard: '',
        idCardImgF: '',
        idCardImgB: '',
        wxAppId: '',
        openId: '',
        imAccount: '',
        hospitalName: '',
        qrCode: '',
        businessCardQrCode: '',
        deptartmentName: '',
        organCode: '',
        organName: '',
        birthday: '',
        lastLoginIp: '',
        totalLoginTimes: null,
        sex: null,
        lastLoginTime: null,
        extraInfo: '',
        loginName: '',
        classCode: '',
      };
    },
    initWidth() {
      this.screenWidth = document.body.clientWidth;
      if (this.screenWidth < 991) {
        return "90%";
      } else if (this.screenWidth < 1400) {
        return "45%";
      } else {
        return "800px";
      }
    },
    setDoctor(val = {}) {
      const vm = this;

      vm.dicts = val['dicts'];
      vm.enums = val['enums'];
      if (val['row']) {
        vm.doctor = { ...val['row'] };

      }
    },
    close() {
      this.$emit("close");
    },
    reset() {
      // 先清除校验，再清除表单，不然有奇怪的bug
      this.$refs.form.clearValidate();
      this.$refs.form.resetFields();
      this.confirmDisabled = false;
      this.doctor = this.initDoctor();
    },
    submitForm() {
      const vm = this;
      this.$refs.form.validate(valid => {
        if (valid) {
          vm.editSubmit();
        } else {
          return false;
        }
      });
    },
    editSubmit() {
      const vm = this;
      if (vm.type === "edit") {
          vm.update();
      } else {
          vm.save();
      }
    },
    save() {
      const vm = this;
      vm.confirmDisabled = true;
      doctorApi.save(this.doctor).then(response => {
        const res = response.data;
        if (res.isSuccess) {
          vm.isVisible = false;
          vm.$message({
            message: vm.$t("tips.createSuccess"),
            type: "success"
          });
          vm.$emit("success");
        }
      }).finally(()=> vm.confirmDisabled = false);
    },
    update() {
      const vm = this;
      vm.confirmDisabled = true;
      doctorApi.update(this.doctor).then(response => {
        const res = response.data;
        if (res.isSuccess) {
          vm.isVisible = false;
          vm.$message({
            message: this.$t("tips.updateSuccess"),
            type: "success"
          });
          vm.$emit("success");
        }
      }).finally(()=> vm.confirmDisabled = false);
    }
  }
};
</script>
<style lang="scss" scoped></style>
