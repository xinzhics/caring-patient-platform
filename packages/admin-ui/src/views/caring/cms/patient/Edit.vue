<template>
  <el-dialog :close-on-click-modal="false" :close-on-press-escape="true" :title="title" :type="type"
             :visible.sync="isVisible" :width="width" top="50px" v-el-drag-dialog>
    <el-form :model="patient" :rules="rules" label-position="right" label-width="100px" ref="form">
      <el-form-item :label="$t('table.patient.serviceAdvisorId')" prop="serviceAdvisorId">
        <el-input type="" v-model="patient.serviceAdvisorId" placeholder="服务专员ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.serviceAdvisorName')" prop="serviceAdvisorName">
        <el-input type="" v-model="patient.serviceAdvisorName" placeholder="服务专员名称"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.nickName')" prop="nickName">
        <el-input type="" v-model="patient.nickName" placeholder="微信昵称"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.mobile')" prop="mobile">
        <el-input type="" v-model="patient.mobile" placeholder="手机号码"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.sex')" prop="sex">
        <el-radio-group v-model="patient.sex" size="medium">
          <el-radio-button :label="true">{{ $t("common.yes") }}</el-radio-button>
          <el-radio-button :label="false">{{ $t("common.no") }}</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="$t('table.patient.name')" prop="name">
        <el-input type="" v-model="patient.name" placeholder="真实姓名"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.birthday')" prop="birthday">
        <el-input type="" v-model="patient.birthday" placeholder="出生年月"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.groupId')" prop="groupId">
        <el-input type="" v-model="patient.groupId" placeholder="小组ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.groupName')" prop="groupName">
        <el-input type="" v-model="patient.groupName" placeholder="小组名"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.doctorId')" prop="doctorId">
        <el-input type="" v-model="patient.doctorId" placeholder="医生ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.doctorName')" prop="doctorName">
        <el-input type="" v-model="patient.doctorName" placeholder="医生名称"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.status')" prop="status">
        <el-input type="" v-model="patient.status" placeholder="会员状态（0:关注  1：正常  2：取关+已填 3：取关+未填)"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.diagnosisId')" prop="diagnosisId">
        <el-input type="" v-model="patient.diagnosisId" placeholder="诊断类型ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.diagnosisName')" prop="diagnosisName">
        <el-input type="" v-model="patient.diagnosisName" placeholder="诊断类型名称"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.wxAppId')" prop="wxAppId">
        <el-input type="" v-model="patient.wxAppId" placeholder="所属公众号ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.openId')" prop="openId">
        <el-input type="" v-model="patient.openId" placeholder="微信openid"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.avatar')" prop="avatar">
        <el-input type="" v-model="patient.avatar" placeholder="头像"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.imAccount')" prop="imAccount">
        <el-input type="" v-model="patient.imAccount" placeholder="环信账号"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.remark')" prop="remark">
        <el-input type="" v-model="patient.remark" placeholder=""/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.isCompleteEnterGroup')" prop="isCompleteEnterGroup">
        <el-input type="" v-model="patient.isCompleteEnterGroup" placeholder="是否完成入组信息（0：否  1：是）"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.examineCount')" prop="examineCount">
        <el-input type="" v-model="patient.examineCount" placeholder="随访计划完成次数"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.ckd')" prop="ckd">
        <el-input type="" v-model="patient.ckd" placeholder="病分期"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.nursingTime')" prop="nursingTime">
        <el-date-picker
          v-model="patient.nursingTime"
          placeholder="随访计划开始时间"
          :start-placeholder="$t('table.patient.nursingTime')"
          value-format="yyyy-MM-dd HH:mm:ss"
          format="yyyy-MM-dd HH:mm:ss"
          class="filter-item date-range-item"
          type="datetime"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.hospitalName')" prop="hospitalName">
        <el-input type="" v-model="patient.hospitalName" placeholder="医院名称"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.certificateNo')" prop="certificateNo">
        <el-input type="" v-model="patient.certificateNo" placeholder="身份证号"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.code')" prop="code">
        <el-input type="" v-model="patient.code" placeholder="会员标识码"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.agreeAgreement')" prop="agreeAgreement">
        <el-input type="" v-model="patient.agreeAgreement" placeholder="是否同意 入组协议 （0； 同意 1 ）"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.grandTotalCheck')" prop="grandTotalCheck">
        <el-input type="" v-model="patient.grandTotalCheck" placeholder="用户累计打卡天数"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.successiveCheck')" prop="successiveCheck">
        <el-input type="" v-model="patient.successiveCheck" placeholder="用户连续打卡天数"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.height')" prop="height">
        <el-input type="" v-model="patient.height" placeholder="身高"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.weight')" prop="weight">
        <el-input type="" v-model="patient.weight" placeholder="体重"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.organId')" prop="organId">
        <el-input type="" v-model="patient.organId" placeholder="所属机构ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.organCode')" prop="organCode">
        <el-input type="" v-model="patient.organCode" placeholder="所属机构代码"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.organName')" prop="organName">
        <el-input type="" v-model="patient.organName" placeholder="所属机构名称"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.lastLoginIp')" prop="lastLoginIp">
        <el-input type="" v-model="patient.lastLoginIp" placeholder="最后登录IP"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.lastLoginTime')" prop="lastLoginTime">
        <el-input type="" v-model="patient.lastLoginTime" placeholder="最后登录时间，时间戳类型"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.totalLoginTimes')" prop="totalLoginTimes">
        <el-input type="" v-model="patient.totalLoginTimes" placeholder="登录次数"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.extraInfo')" prop="extraInfo">
        <el-input type="" v-model="patient.extraInfo" placeholder="用户额外信息"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.completeEnterGroupTime')" prop="completeEnterGroupTime">
        <el-date-picker
          v-model="patient.completeEnterGroupTime"
          placeholder="入组时间"
          :start-placeholder="$t('table.patient.completeEnterGroupTime')"
          value-format="yyyy-MM-dd HH:mm:ss"
          format="yyyy-MM-dd HH:mm:ss"
          class="filter-item date-range-item"
          type="datetime"/>
      </el-form-item>
      <el-form-item :label="$t('table.patient.classCode')" prop="classCode">
        <el-input type="" v-model="patient.classCode" placeholder="权限代码"/>
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
import patientApi from "@/api/Patient.js";

export default {
  name: "PatientEdit",
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
      patient: this.initPatient(),
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
    initPatient() {
      return {
        id: "",
        serviceAdvisorId: null,
        serviceAdvisorName: '',
        nickName: '',
        mobile: '',
        sex: true,
        name: '',
        birthday: '',
        groupId: null,
        groupName: '',
        doctorId: null,
        doctorName: '',
        status: null,
        diagnosisId: null,
        diagnosisName: '',
        wxAppId: '',
        openId: '',
        avatar: '',
        imAccount: '',
        remark: '',
        isCompleteEnterGroup: null,
        examineCount: null,
        ckd: null,
        nursingTime: null,
        hospitalName: '',
        certificateNo: '',
        code: '',
        agreeAgreement: null,
        grandTotalCheck: null,
        successiveCheck: null,
        height: null,
        weight: null,
        organId: '',
        organCode: '',
        organName: '',
        lastLoginIp: '',
        lastLoginTime: null,
        totalLoginTimes: null,
        extraInfo: '',
        completeEnterGroupTime: null,
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
    setPatient(val = {}) {
      const vm = this;

      vm.dicts = val['dicts'];
      vm.enums = val['enums'];
      if (val['row']) {
        vm.patient = { ...val['row'] };

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
      this.patient = this.initPatient();
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
      patientApi.save(this.patient).then(response => {
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
      patientApi.update(this.patient).then(response => {
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
