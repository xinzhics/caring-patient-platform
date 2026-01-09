<template>
  <div class="box1">
    <div class="title">
      <span class="username">{{field.label}}</span>
      <span class="mustchoose" v-if="field.required===true">*</span>
    </div>
    <div class="depict">{{field.labelDesc}}</div>
    <div>
      <van-cell :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}" :ref='field.id' :class="[showred?'picc2':'']" is-link :value="values ? values : field.placeholder" @click="showPopup" />
    </div>
    <van-popup v-model="show" position="bottom" closeable>
      <div class="title1">请选择</div>
      <van-picker ref="addressPicker" type="address" :show-toolbar="false" visible-item-count="3" value-key="name" :columns="columns" />
      <div class="submit">
        <van-button round type="info" @click="updata">确定</van-button>
      </div>
    </van-popup>
    <div class="box2" v-if="field.hasAddressDetail === 1">
      <van-field v-model="field.value"
                 rows="2"
                 @click-input="isCanClikc" :readonly="noClikc"
                 @input="addressDetail"
                 :class="[addressDetailsFail?'picc2':'']"
                 type="textarea"
                 :maxlength="field.addressDetailSize"
                 placeholder="请输入详细地址" show-word-limit />
    </div>
  </div>

</template>

<script src="./index.js">
</script>

<style lang='less' scoped  src="./index.less">
</style>
