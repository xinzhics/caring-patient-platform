<template>
  <div style="width: 100%; position: relative; display: flex;">
    <van-col span="24" class="attr-desc">
      <div style="width: 100%; margin-top: 10px; margin-bottom: 10px;"
           :class="field.titleTextAlign === 'right' ? 'align-right' : field.titleTextAlign === 'left' ? 'align-left' : 'align-center'">
        <span style="color: #333333; font-size: 16px; ">{{ field.label }}</span>
      </div>
      <div
           :class="[field.dividerStyle === 'hairline' ? 'link-solid' : 'link-dashed', field.dividerWidth === 100 ? 'link-kuan100' : 'link-kuan80']"></div>
      <div style="width: 100%; margin-top: 10px; margin-bottom: 10px;">
        <p class="my-form-label-desc"
           :class="field.contentTextAlign === 'right' ? 'align-right' : field.contentTextAlign === 'left' ? 'align-left' : 'align-center'">
          {{ field.labelDesc }}
        </p>
      </div>
    </van-col>
  </div>
</template>
<script>
import {Col} from "vant";
export default {
  components:{
        [Col.name]:Col,
  },
  props: {
    field: {
      type: Object
    }
  },
  data: function () {
    return {
      // field: {
      //     contentTextAlign: "center",
      //     dividerStyle: "hairline",
      //     dividerWidth: 100,
      //     id: "40288105739e491101739e51de640074",
      //     label: "这里是疾病信息",
      //     labelDesc: "填写疾病信息有助于医生提前了解您的病情。",
      //     titleTextAlign: "center",
      //     values: [],
      //     widgetType: "Desc"
      // }
    };
  }
};
</script>
<style lang="less" scoped>
.attr-desc {
  h3 {
    padding: 0;
    margin: 0;
  }

  .align-right {
    text-align: right;
  }

  .align-left {
    text-align: left;
  }

  .align-center {
    text-align: center;
  }

  .link-solid {
    height: 0.5px;
    border-top: solid #DCDFE6 0.5px;
  }

  .link-kuan100 {
    width: 100%;
  }

  .link-kuan80 {
    margin-left: 20px;
    margin-right: 20px;
  }

  .link-dashed {
    height: 1px;
    border-top: dashed #DCDFE6 1.5px;
  }

  .my-form-label-desc {
    padding: 0;
    margin: 0;
    font-size: 12px;
    color: #999999;
  }
}
</style>
