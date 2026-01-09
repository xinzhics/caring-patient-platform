<template>
  <div class="box1">
    <div style="width: 100%; position: relative; display: flex;">
      <van-col span="24" class="attr-desc">
        <div
          style="display:flex; padding: 13px;"
          :class="field.titleTextAlign === 'right' ? 'align-right' : field.titleTextAlign === 'left' ? 'align-left' : 'align-center'">
          <div v-if="field.contentTextAlign==='left'"
               style="width: 3px !important;height: 15px;background: rgb(50, 126, 251);opacity: 1;border-radius: 4px; color:rgb(50, 126, 251);"
               class="box2"></div>
          <div style="font-weight: 500;color: #327EFB; font-size: 16px; " class="box3">
            {{ field.label }}
          </div>
        </div>
        <div
          :class="[field.dividerStyle === 'hairline' ? 'link-solid' : 'link-dashed', field.dividerWidth === 100 ? 'link-kuan100' : 'link-kuan80']">
        </div>
        <div v-if="field.labelDesc"
          style="padding: 13px">
          <p class="my-form-label-desc"
             :class="field.contentTextAlign === 'right' ? 'align-right' : field.contentTextAlign === 'left' ? 'align-left' : 'align-center'">
            {{ field.labelDesc }}
          </p>
        </div>
      </van-col>
    </div>
  </div>

</template>
<script>
import Vue from 'vue'
import {
  Col
} from 'vant'

Vue.use(Col)
export default {
  components: {
    [Col.name]: Col
  },
  props: {
    field: {
      type: Object
    }
  },
}

</script>
<style lang="less" scoped>
.box1 {
  width: 100%;
  background-color: #fff;
  line-height: 2rem;
}

.attr-desc {
  h3 {
    padding: 0;
    margin: 0;
  }

  .align-right {
    display: flex;
    justify-content: right;
    align-items: center;
    min-height: 16px;
    line-height: 16px;
    padding-top: 25px;
  }

  .align-left {
    display: flex;
    align-items: center;
    min-height: 16px;
    line-height: 16px;
    padding-top: 25px;

  }

  .align-center {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 16px;
    line-height: 16px;
    padding-top: 25px;
  }

  .box2 {
    margin-right: 8px;
  }

  .box3 {
    font-weight: 500;
  }

  .link-solid {
    height: 0.5px;
    border-top: solid #dcdfe6 0.5px;
  }

  .link-kuan100 {
    width: 100%;
  }

  .link-kuan80 {
    margin-left: 13px;
    margin-right: 13px;
  }

  .link-dashed {
    height: 1px;
    border-top: dashed #dcdfe6 1.5px;
  }

  .my-form-label-desc {
    padding: 0;
    margin: 0;
    font-size: 12px;
    color: #999999;
  }
}

</style>
