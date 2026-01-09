<template>
  <el-dialog :close-on-click-modal="false" :close-on-press-escape="true" :title="title" :type="type"
             :visible.sync="isVisible" :width="width" top="50px" v-el-drag-dialog>
    <el-form :model="nursingStaff" :rules="rules" label-position="right" label-width="100px" ref="form">
      <el-form-item :label="$t('table.nursingStaff.loginName')" prop="loginName">
        <el-input type="" v-model="nursingStaff.loginName" placeholder="用户名"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.password')" prop="password">
        <el-input type="" v-model="nursingStaff.password" placeholder="密码"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.name')" prop="name">
        <el-input type="" v-model="nursingStaff.name" placeholder="姓名"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.avatar')" prop="avatar">
        <el-input type="" v-model="nursingStaff.avatar" placeholder="头像url"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.mobile')" prop="mobile">
        <el-input type="" v-model="nursingStaff.mobile" placeholder="手机号码"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.passwordStrongLevel')" prop="passwordStrongLevel">
        <el-input type="" v-model="nursingStaff.passwordStrongLevel" placeholder="用户密码强度"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.imAccount')" prop="imAccount">
        <el-input type="" v-model="nursingStaff.imAccount" placeholder="IM的账号"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.gesturePwd')" prop="gesturePwd">
        <el-input type="" v-model="nursingStaff.gesturePwd" placeholder="手势密码"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.classCode')" prop="classCode">
        <el-input type="" v-model="nursingStaff.classCode" placeholder=" 权限代码 "/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.certificate')" prop="certificate">
        <el-input type="" v-model="nursingStaff.certificate" placeholder="身份证号"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.totalLoginTimes')" prop="totalLoginTimes">
        <el-input type="" v-model="nursingStaff.totalLoginTimes" placeholder="登录次数"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.organId')" prop="organId">
        <el-input type="" v-model="nursingStaff.organId" placeholder="所属单位ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.organName')" prop="organName">
        <el-input type="" v-model="nursingStaff.organName" placeholder=""/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.organCode')" prop="organCode">
        <el-input type="" v-model="nursingStaff.organCode" placeholder=" 所属机构代码"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.sex')" prop="sex">
        <el-input type="" v-model="nursingStaff.sex" placeholder="性别 0:男 1：女"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.birthday')" prop="birthday">
        <el-input type="" v-model="nursingStaff.birthday" placeholder="出生年月"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.extraInfo')" prop="extraInfo">
        <el-input type="" v-model="nursingStaff.extraInfo" placeholder="额外信息"/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.lastLoginIp')" prop="lastLoginIp">
        <el-input type="" v-model="nursingStaff.lastLoginIp" placeholder=""/>
      </el-form-item>
      <el-form-item :label="$t('table.nursingStaff.lastLoginTime')" prop="lastLoginTime">
        <el-input type="" v-model="nursingStaff.lastLoginTime" placeholder="创建时间"/>
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
import nursingStaffApi from "@/api/NursingStaff.js";

export default {
  name: "NursingStaffEdit",
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
      nursingStaff: this.initNursingStaff(),
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
    initNursingStaff() {
      return {
        id: "",
        loginName: '',
        password: '',
        name: '',
        avatar: '',
        mobile: '',
        passwordStrongLevel: '',
        imAccount: '',
        gesturePwd: '',
        classCode: '',
        certificate: '',
        totalLoginTimes: null,
        organId: null,
        organName: '',
        organCode: '',
        sex: null,
        birthday: '',
        extraInfo: '',
        lastLoginIp: '',
        lastLoginTime: null,
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
    setNursingStaff(val = {}) {
      const vm = this;

      vm.dicts = val['dicts'];
      vm.enums = val['enums'];
      if (val['row']) {
        vm.nursingStaff = { ...val['row'] };

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
      this.nursingStaff = this.initNursingStaff();
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
      nursingStaffApi.save(this.nursingStaff).then(response => {
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
      nursingStaffApi.update(this.nursingStaff).then(response => {
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
