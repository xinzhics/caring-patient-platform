<template>
  <div class="main">
    <div class="title">
      <span class="name">{{ field.label }}</span>
      <span class="mustchoose" v-if="field.required">*</span>
      <div class="describe">{{ field.labelDesc }}</div>
    </div>
    <div class="dropdown">
      <van-cell @click="showbox" is-link arrow-direction="down"
                ref="color"
                :class="checkFail ? 'cell2' : ''"
                :value="field.values && field.values.length > 0 && field.values[0].attrValue ? (field.values[0].valueText === '其他' && field.otherLabelRemark ? field.otherLabelRemark : field.values[0].valueText)  : '请选择'"
                :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}"
      />
    </div>
    <div :class="show === false ? 'showbox' : 'show'">
      <div class="show1"
           :style="{'border-bottom':index>=0 && index < field.options.length-1 ? '1px solid #D6D6D6 ':''}"
           @click="chooseBox(info, index)" v-for="(info,index) in field.options " :key="index">
        <div class="choose"
             style="width:92%"
             :class="optionSelected(info, index) ? 'choose2':''">
          {{ info.attrValue === '其他' && field.otherLabelRemark ? field.otherLabelRemark : info.attrValue }}
        </div>
        <div class="icon" v-if="optionSelected(info, index)">
          <van-icon name="success" color="#6F7D97"/>
        </div>
      </div>
    </div>
    <div class="userinfo">
      <van-field v-if="field.values[0].valueText === '其他' && field.hasOtherOption === 1 "
                 v-model="field.otherValue"
                 show-word-limit
                 :class="[otherValueFail === true ? 'cell2 cell3' : 'cell3']"
                 :rules="[{ required: field.otherEnterRequired === 1 }]"
                 :maxlength="field.otherValueSize"
                 :placeholder="field.otherEnterRequired === 1 ? '请输入(必填)' : '请输入(选填)'" type="textarea"/>
    </div>
  </div>
</template>

<script src="./index.js">
</script>
<style lang="less" scoped src="./index.less">
</style>
