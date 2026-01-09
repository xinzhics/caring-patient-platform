<template>
  <!-- 多选组件 -->
  <div>
    <div class="main">
      <div class="title">
        <span class="name">{{ field.label }}</span>
        <span class="mustchoose" v-if="field.required">*</span>
        <div class="describe">{{ field.labelDesc }}</div>
      </div>
      <van-cell v-if="noValues"
                :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}"
                title="请选择" :ref="field.id" is-link @click="showPopup"></van-cell>
      <van-cell is-link :ref="field.id" @click="showPopup" v-if="!noValues"
                :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}">
        <!-- 一般选项 -->
        <div class="title3" v-for='(info,index) in field.values' v-if="info.attrValue" :key="index">
          <div class="box" v-if="info.valueText !== ''">
            <div class="yuan"></div>
            <div class="place">{{ info.valueText === '其他' && field.otherLabelRemark ? field.otherLabelRemark : info.valueText }}</div>
          </div>
          <div class="miaoshu" v-if="info.desc && info.valueText !== '其他'">
            {{ info.desc }}
          </div>
          <div class="miaoshu" v-if="info.valueText === '其他'">
            {{ field.otherValue }}
          </div>
          <div class="line2" v-if="field.values.length>1 && index < field.values.length-1"></div>
        </div>
      </van-cell>

      <van-popup v-model="show" closeable close-icon-position="top-right" position="bottom">
        <div class="title2">
          请选择
        </div>
        <div class="choose" v-for='(info,index) in field.options' :key="index">
          <div class="text2" :class="[info.select ? 'text3' : '']" @click="choosebox(info,index)">
            {{ info.attrValue === '其他' && field.otherLabelRemark ? field.otherLabelRemark : info.attrValue }}
          </div>
          <div class="hook" v-show="info.select">
            <van-icon name="success"/>
          </div>
          <div class="input1" v-if="info.select">

            <van-field v-if="field.itemAfterHasEnter === 1 && info.attrValue !== '其他'" class="cell2"
                       :error="info.error"
                       rows="1" autosize type="textarea" v-model="info.valuesRemark"
                       @input="(val) => fieldInput(val, info)"
                       :placeholder="field.itemAfterHasEnterRequired === 1? '请输入(必填)' : '请输入(选填)'"
                       show-word-limit
                       :maxlength="field.itemAfterHasEnterSize"/>

            <!-- 其他输入框 -->
            <van-field v-if="field.hasOtherOption === 1 && info.attrValue === '其他'" class="cell2" rows="1"
                       :error="field.error" @input="(val) => fieldInputOther(val, info)" autosize type="textarea"
                       v-model="field.otherValue"
                       :placeholder="field.otherEnterRequired === 1? '请输入(必填)' : '请输入(选填)'"
                       show-word-limit :maxlength="field.otherValueSize"/>

          </div>
        </div>
        <div class="submit">
          <van-button round type="info" :disabled="checkCheckBoxDisableSubmit()" @click="updata">确定</van-button>
        </div>
      </van-popup>
    </div>

    <div v-if="field.values && field.values.length > 0" v-for='(value,k) in field.values'
         :key="field.id + 'question' +  k">
      <editorChild :questions='value.questions' ref='indexItem'></editorChild>
    </div>
  </div>

</template>
<script src="./index.js">
</script>
<style lang="less" scoped src="./multipleChoice.less">
</style>
