<template>
  <div class="single-line-mobile">
    <div class="box1">
      <div class="title">

        <span class="username">{{ field.label }}</span>
        <span class="mustchoose" v-if="field.required">*</span>
      </div>
      <div class="depict">{{ field.labelDesc }}</div>
      <!-- 必填单行文本 -->
      <div class="userinfo" style="display:flex">
        <van-field :ref="field.id"
                   @click-input="isCanClick"
                   :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}"
                   :disabled="!field.isUpdatable || field.isUpdatable !== 1"
                   :class="[show===true?'cell2':'']" @input="change" @blur="fieldInput" show-word-limit
                   v-model="field.values[0].attrValue" :maxlength="field.maxEnterNumber"
                   :placeholder="field.placeholder"
                   type="digit"/>
        <div @click="showDlog=true"
             v-if="field.values[0].attrValue&&field.values[0].attrValue!==''&&field.values[0].attrValue.length==11"
             style="width: 83px;height:46px;background:#22CC5E;border-radius: 6px;margin-right: 15px;font-size: 14px;color:
             #fff;text-align: center;line-height: 46px;position: relative">
          <img src="../images/phone.png" style="width: 16px;position: absolute;top: 50%;transform: translateY(-50%);left: 8px" alt="">
          <span style="margin-left: 20px;color: #fff">呼叫</span>
        </div>
      </div>

    </div>
    <van-popup closeable style="width: 292px" v-model="showDlog">
      <img style="width: 100%" src="../../../images/call-phone.png"/>
      <!--      <div @click="closeDlog" style="position: absolute ;top:13px;right: 13px;color: #A9B2C3 !important;"><van-icon  name="cross" /></div>-->

      <div style="font-size: 13px;color: #999999;text-align: center">使用普通电话拨打</div>
      <div style="text-align: center;font-size: 25px;color: #333333;">{{ field.values[0].attrValue }}</div>
      <a @click="showDlogs" :href="'tel:' + field.values[0].attrValue"
         style="display: block;width: 208px;height: 46px;background: #337EFF;border-radius: 42px;text-align: center;line-height: 46px;margin: 35px auto;color: #fff">确定呼叫</a>
    </van-popup>
  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped src="./caringMobile.less">
@font-face {
  font-family: 'my-icon';
  src: url('../images/phone.png') format('truetype');
}

.my-icon {
  font-family: 'my-icon';
}

.my-icon-extra::before {
  content: '\e626';
}
</style>
