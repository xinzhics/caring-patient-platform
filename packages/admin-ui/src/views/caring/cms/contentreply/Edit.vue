<template>
  <el-dialog :close-on-click-modal="false" :close-on-press-escape="true" :title="title" :type="type"
             :visible.sync="isVisible" :width="width" top="50px" v-el-drag-dialog>
    <el-form :model="contentReply" :rules="rules" label-position="right" label-width="100px" ref="form">
      <el-form-item :label="$t('table.contentReply.cParentCommentId')" prop="cParentCommentId">
        <el-input type="" v-model="contentReply.cParentCommentId" placeholder="父ID"/>
      </el-form-item>
      <el-form-item :label="$t('table.contentReply.cReplierId')" prop="cReplierId">
        <el-input type="" v-model="contentReply.cReplierId" placeholder="留言人"/>
      </el-form-item>
      <el-form-item :label="$t('table.contentReply.cContentId')" prop="cContentId">
        <el-input type="" v-model="contentReply.cContentId" placeholder="文章Id"/>
      </el-form-item>
      <el-form-item :label="$t('table.contentReply.content')" prop="content">
        <el-input type="" v-model="contentReply.content" placeholder="留言内容"/>
      </el-form-item>
      <el-form-item :label="$t('table.contentReply.nAuditStatus')" prop="nAuditStatus">
        <el-input type="" v-model="contentReply.nAuditStatus" placeholder="审核状态"/>
      </el-form-item>
      <el-form-item :label="$t('table.contentReply.cReplierWxName')" prop="cReplierWxName">
        <el-input type="" v-model="contentReply.cReplierWxName" placeholder="微信昵称"/>
      </el-form-item>
      <el-form-item :label="$t('table.contentReply.cReplierAvatar')" prop="cReplierAvatar">
        <el-input type="" v-model="contentReply.cReplierAvatar" placeholder="头像"/>
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
import contentReplyApi from "@/api/ContentReply.js";

export default {
  name: "ContentReplyEdit",
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
      contentReply: this.initContentReply(),
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
    initContentReply() {
      return {
        id: "",
        cParentCommentId: null,
        cReplierId: null,
        cContentId: null,
        content: '',
        nAuditStatus: null,
        cReplierWxName: '',
        cReplierAvatar: '',
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
    setContentReply(val = {}) {
      const vm = this;

      vm.dicts = val['dicts'];
      vm.enums = val['enums'];
      if (val['row']) {
        vm.contentReply = { ...val['row'] };

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
      this.contentReply = this.initContentReply();
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
      contentReplyApi.save(this.contentReply).then(response => {
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
      contentReplyApi.update(this.contentReply).then(response => {
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
