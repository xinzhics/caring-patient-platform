<template>
  <el-dialog :close-on-click-modal="false" :close-on-press-escape="true" :title="title" :type="type"
             :visible.sync="isVisible" :width="width" top="50px" v-el-drag-dialog>
    <el-form :model="channel" :rules="rules" label-position="right" label-width="100px" ref="form">
      <el-form-item :label="$t('table.channel.channelName')" prop="channelName">
        <el-input type="" v-model="channel.channelName" placeholder="请填写栏目名称"/>
        
      </el-form-item>
      <el-form-item :label="$t('table.channel.sort')" prop="sort">
        <el-input type="" v-model="channel.sort" placeholder="数字越大越靠前"/>
      </el-form-item>
      <!-- <el-form-item :label="$t('table.channel.channelType')" prop="channelType"> -->
        <!-- <el-input type="" v-model="channel.channelType" placeholder="频道类型"/> -->
        <!-- <el-select style="width:100%"  placeholder="频道类型" v-model="channel.channelType" value>
          <el-option :key="index" :label="item.name" :value="item.val" v-for="(item, key, index) in [{name:'文章',val:'Article'},{name:'轮播图',val:'Banner'}]" />
        </el-select>
      </el-form-item> -->
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
import channelApi from "@/api/Channel.js";

export default {
  name: "ChannelEdit",
  directives: { elDragDialog },
  components: {  },
  props: {
    doctorOwner:{
      type: Boolean,
      default: false
    },
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
      channel: this.initChannel(),
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
    initChannel() {
      return {
        id: "",
        channelName: '',
        sort: null,
        channelType: '',
        ownerType: 'TENANT',
        doctorOwner:this.doctorOwner
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
    setChannel(val = {}) {
      const vm = this;

      vm.dicts = val['dicts'];
      vm.enums = val['enums'];
      if (val['row']) {
        vm.channel = { ...val['row'] };

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
      this.channel = this.initChannel();
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
      if(!this.channel.channelName){
          vm.$message({
            message: '请填写完整',
            type: "error"
          });
          return
      }
      this.channel.doctorOwner = this.doctorOwner
      channelApi.save(this.channel).then(response => {
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
      if(!this.channel.channelName){
          vm.$message({
            message: '请填写完整',
            type: "error"
          });
          return
      }
      this.channel.doctorOwner = this.doctorOwner
      channelApi.update(this.channel).then(response => {
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
