<template>
  <el-dialog :close-on-click-modal="false" :close-on-press-escape="true" :title="title" :type="type"
             :visible.sync="isVisible" :width="width" top="50px" >
    <el-form :model="channelContent" :rules="rules" label-position="right" label-width="100px" ref="form" size="mini">
       <el-form-item :label="$t('table.channelContent.title')" prop="title">
        <el-input type="" v-model="channelContent.title" placeholder="请输入文章标题"/>
      </el-form-item>
      <el-form-item :label="$t('table.channelContent.channelId')" prop="channelId">
        <!-- <el-input type="" v-model="channelContent.channelId" placeholder="栏目id"/> -->
        <el-select style="width:100%"  v-model="channelContent.channelId" value>
          <el-option :key="index" :label="item.channelName" :value="item.id" v-for="(item, key, index) in channelData" />
        </el-select>
      </el-form-item>
     
      <el-form-item :label="$t('table.channelContent.icon')" prop="icon">
        <!-- <el-input type="" v-model="channelContent.icon" placeholder="主缩略图"/> -->
        <uploadImg :img-url="channelContent.icon" @uploadBtn="uploadBtn" />
      </el-form-item>
      <el-form-item :label="$t('table.channelContent.content')" prop="content">
        <!-- <el-input type="" v-model="channelContent.content" placeholder="内容"/> -->
        <Tinymce ref="content" v-model="channelContent.content" width="100%" :height="300" />
      </el-form-item>
      <el-form-item :label="$t('table.channelContent.link')" prop="link">
        <el-input type="" v-model="channelContent.link" placeholder="跳转地址，如(www.baidu.com)"/>
      </el-form-item>
      <el-form-item :label="$t('table.channelContent.sort')" prop="sort">
        <el-input type="" v-model="channelContent.sort" placeholder="数字越大越靠前"/>
      </el-form-item>
      <el-form-item :label="$t('table.channelContent.isTop')" prop="isTop">
        <!-- <el-input type="" v-model="channelContent.isTop" placeholder="置顶（0, 1）"/> -->
        <el-radio-group v-model="channelContent.isTop" size="mini">
          <el-radio-button  :key="index" :label="item.val" :value="item.val" v-for="(item, key, index) in [{name:'是',val:1},{name:'否',val:0}]">{{ item.name }}</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="$t('table.channelContent.hitCount')" prop="hitCount">
        <el-input type="" v-model="channelContent.hitCount" placeholder="点击量"/>
      </el-form-item>
      <el-form-item :label="$t('table.channelContent.summary')" prop="summary">
        <el-input type="" v-model="channelContent.summary" placeholder="请输入文章摘要"/>
      </el-form-item>
      <!-- <el-form-item :label="$t('table.channelContent.canComment')" prop="canComment"> -->
        <!-- <el-input type="" v-model="channelContent.canComment" placeholder="是否允许评论，1允许，0不允许。"/> -->
        <!-- <el-radio-group v-model="channelContent.canComment" size="mini">
          <el-radio-button  :key="index" :label="item.val" :value="item.val" v-for="(item, key, index) in [{name:'是',val:1},{name:'否',val:0}]">{{ item.name }}</el-radio-button>
        </el-radio-group>
      </el-form-item> -->
      <!-- <el-form-item :label="$t('table.channelContent.messageNum')" prop="messageNum">
        <el-input type="" v-model="channelContent.messageNum" placeholder="留言数量"/>
      </el-form-item> -->
      <el-form-item :label="$t('table.channelContent.channelType')" prop="channelType">
        <!-- <el-input type="" v-model="channelContent.channelType" placeholder="所属栏目类型，与栏目表保持一致"/> -->
        <el-select style="width:100%"  placeholder="请选择" v-model="channelContent.channelType" value>
          <el-option :key="index" :label="item.name" :value="item.val" v-for="(item, key, index) in [{name:'文章',val:'Article'},{name:'轮播图',val:'Banner'}]" />
        </el-select>
      </el-form-item>
    </el-form>
    <div class="dialog-footer" slot="footer">
      <el-button size="mini" @click="isVisible = false" plain type="warning">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button size="mini" @click="submitForm" :disabled="confirmDisabled" plain type="primary">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </el-dialog>
</template>
<script>
import elDragDialog from '@/directive/el-drag-dialog'
import channelContentApi from "@/api/ChannelContent.js";
import Tinymce from '@/components/Tinymce'
import uploadImg from '@/components/updataImg/uploadImg'

export default {
  name: "ChannelContentEdit",
  directives: { elDragDialog },
  components: { Tinymce,uploadImg },
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
    },
    enums:{
      type: Object,
      default: {}
    },
    dicts:{
      type: Object,
      default: {}
    },
    channelContent:{
      type: Object,
      default: {
        id: "",
        channelId: null,
        title: '',
        icon: '',
        content: '',
        link: '',
        sort: null,
        isTop: null,
        hitCount: null,
        summary: '',
        canComment: null,
        messageNum: null,
        ownerType: 'TENANT',
        channelType: '',
      }
    }
  },
  data() {
    return {
      confirmDisabled: false,
      // channelContent: this.initChannelContent(),
      screenWidth: 0,
      width: this.initWidth(),
      rules: {

      },
      // // 枚举
      // enums: {
      // },
      // // 字典
      // dicts: {
      // },
      channelData:[]
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
    this.getchannelData()
  },
  methods: {
    uploadBtn(i){
      // console.log(i)
      this.$emit('uploadBtnT',i)
    },
    getchannelData(){
      const params={
        "ownerType": "TENANT",
        "doctorOwner":this.doctorOwner
      }
      channelContentApi.getTypeid(params).then(response => {
        const res = response.data;
        if (res.isSuccess) {
         this.channelData = res.data
        }
      }).finally();
    },
    initChannelContent() {
      return {
        id: "",
        channelId: null,
        title: '',
        icon: '',
        content: '',
        link: '',
        sort: null,
        isTop: null,
        hitCount: null,
        summary: '',
        canComment: null,
        messageNum: null,
        ownerType: 'TENANT',
        channelType: '',
        doctorOwner:this.doctorOwner
      };
    },
    initWidth() {
      this.screenWidth = document.body.clientWidth;
      if (this.screenWidth < 991) {
        return "90%";
      } else if (this.screenWidth < 1400&&this.screenWidth>991) {
        return "60%";
      } else {
        return "800px";
      }
    },
    // setChannelContent(val = {}) {
    //   const vm = this;

    //   vm.dicts = val['dicts'];
    //   vm.enums = val['enums'];
    //   // console.log(val['row'])
    //   if (val['row']) {
    //     vm.channelContent = { ...val['row'] };
    //   }
    //   // else{
    //   //   vm.channelContent = this.initChannelContent()
    //   // }
    // },
    close() {
      this.$emit("close");
    },
    reset() {
      // 先清除校验，再清除表单，不然有奇怪的bug
      this.$refs.form.clearValidate();
      this.$refs.form.resetFields();
      this.confirmDisabled = false;
      // this.channelContent = this.initChannelContent();
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
      if(!this.channelContent.content||!this.channelContent.title||!this.channelContent.channelId||!this.channelContent.channelType){
          
          vm.$message({
            message: '请填写完整',
            type: "error"
          });
           vm.confirmDisabled = false;
          return
      }
      this.channelContent.doctorOwner = this.doctorOwner
      channelContentApi.save(this.channelContent).then(response => {
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
      if(!this.channelContent.content||!this.channelContent.title||!this.channelContent.channelId||!this.channelContent.channelType){
          vm.$message({
            message: '请填写完整',
            type: "error"
          });
          vm.confirmDisabled = false;
          return
      }
      this.channelContent.doctorOwner = this.doctorOwner
      channelContentApi.update(this.channelContent).then(response => {
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
