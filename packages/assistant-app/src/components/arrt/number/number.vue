<template>
  <div class="box1">
    <div class="title">
      <span class="username">{{field.label}}</span>
      <span class="mustchoose" v-if="field.required">*</span>
      <span class="maxnum" style="color:red;font-size:.8rem" v-if="maxValue">超出最大值</span>
      <span class="minnum" style="color:red;font-size:.8rem" v-if="minValue">超出最小值</span>
    </div>
    <div class="depict">{{field.labelDesc}}</div>
    <div class="a">
      <van-field ref="color" @blur="updata" @input="input" :type="field.canDecimal===1?'number':'digit'"
         :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}"
        :disabled="!field.isUpdatable || field.isUpdatable !== 1"
        :class="[show===true?'cell3':'']" v-model="field.values[0].attrValue" :placeholder="field.placeholder" />
         <div class="unit" v-if="field.rightUnit">
      {{field.rightUnit}}
    </div>
    </div>

     <div class="remind" v-if="field.dataFeedBacks&&message!==''">
        <div style="margin-left:16px;margin-right:13px;margin-top:15px;width:17px;height:17px"><img src="@/assets/my/remind.png" style="height:100%"></div>
        <div style="margin-top:15px;margin-bottom:15px;margin-right:4px">{{message}}</div>
      </div>
  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped src="./index.less">
</style>
