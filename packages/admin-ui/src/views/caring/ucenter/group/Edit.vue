<template>
  <el-dialog :close-on-click-modal="false" :close-on-press-escape="true" :title="title" :type="type"
             :visible.sync="isVisible" :width="width" top="50px" v-el-drag-dialog>
    <el-form :model="group" :rules="rules" label-position="right" label-width="100px" ref="form">
      <el-form-item :label="$t('table.group.nurseId')" prop="nurseId">
        <el-input type="" v-model="group.nurseId" placeholder="服务专员ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.group.nurseName')" prop="nurseName">
        <el-input type="" v-model="group.nurseName" placeholder="服务专员名字"/>
      </el-form-item>
      <el-form-item :label="$t('table.group.icon')" prop="icon">
        <el-input type="" v-model="group.icon" placeholder="图标"/>
      </el-form-item>
      <el-form-item :label="$t('table.group.name')" prop="name">
        <el-input type="" v-model="group.name" placeholder="小组名字"/>
      </el-form-item>
      <el-form-item :label="$t('table.group.order')" prop="order">
        <el-input type="" v-model="group.order" placeholder="排序"/>
      </el-form-item>
      <el-form-item :label="$t('table.group.organId')" prop="organId">
        <el-input type="" v-model="group.organId" placeholder="机构ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.group.organName')" prop="organName">
        <el-input type="" v-model="group.organName" placeholder="机构名字"/>
      </el-form-item>
      <el-form-item :label="$t('table.group.contactName')" prop="contactName">
        <el-input type="" v-model="group.contactName" placeholder="联系人"/>
      </el-form-item>
      <el-form-item :label="$t('table.group.contactMobile')" prop="contactMobile">
        <el-input type="" v-model="group.contactMobile" placeholder="联系人电话"/>
      </el-form-item>
      <el-form-item :label="$t('table.group.address')" prop="address">
        <el-input type="" v-model="group.address" placeholder="地址"/>
      </el-form-item>
      <el-form-item :label="$t('table.group.remarks')" prop="remarks">
        <el-input type="" v-model="group.remarks" placeholder="备注"/>
      </el-form-item>
      <el-form-item :label="$t('table.group.classCode')" prop="classCode">
        <el-input type="" v-model="group.classCode" placeholder="机构CLASSCODE"/>
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
import groupApi from "@/api/Group.js";

export default {
  name: "GroupEdit",
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
      group: this.initGroup(),
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
    initGroup() {
      return {
        id: "",
        nurseId: null,
        nurseName: '',
        icon: '',
        name: '',
        order: null,
        organId: null,
        organName: '',
        contactName: '',
        contactMobile: '',
        address: '',
        remarks: '',
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
    setGroup(val = {}) {
      const vm = this;

      vm.dicts = val['dicts'];
      vm.enums = val['enums'];
      if (val['row']) {
        vm.group = { ...val['row'] };

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
      this.group = this.initGroup();
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
      groupApi.save(this.group).then(response => {
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
      groupApi.update(this.group).then(response => {
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
