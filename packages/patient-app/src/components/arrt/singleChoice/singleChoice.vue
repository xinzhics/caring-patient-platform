<template>
  <!-- 单选组件 -->
  <div>
    <div class="single-choice" v-if="field">
      <div class="box1">
        <div class="title">
          <div class="username">{{ field.label }}</div>
          <div class="mustchoose" v-if="field.required">*</div>
        </div>
        <div class="depict">{{ field.labelDesc }}</div>
        <div :class="showbox ? 'showbox' : 'noshowbox'">
          <div style="padding: 0 0px 0 13px" @click="showPopup">
            <div
              :class="checkFail ? ' cell2' : 'cell2 cell3'"
            >
              <div :class="field.modifyTags?'blue-border':''"
                   style="display: flex;justify-content: space-between;align-items: center;padding: 10px 5px 12px 15px;background: #FAFAFA;
                   font-size: 15px;border: 1px solid #CED1D9;border-radius: 8px">
                <div>
                  <div :style="{color:field.values[0].valueText?'#333333':'#b8b8b8 !important'}">
                    {{
                      field.values[0].valueText ? field.values[0].valueText === '其他' && field.otherLabelRemark ? field.otherLabelRemark : field.values[0].valueText : field.placeholder ? field.placeholder : '请选择'
                    }}
                    <span style="color: rgb(240, 160, 48) !important; font-size: 14px"
                          v-if="field.exactType === 'SCORING_SINGLE_CHOICE' && field.values.length > 0 && field.values[0].score != undefined && !showScore(field.values[0].attrValue)">({{ field.values[0].score }}分)</span>
                  </div>
                  <div v-if="getLabel()" style="color: #969799 ;margin-top: 5px;word-break:break-all">
                    {{ getLabel() }}
                  </div>
                </div>
                <div style="color: #969DB2 !important">
                  <van-icon style="color: #969DB2 !important;" name="arrow"/>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 弹出层 -->
        <van-popup v-model="show" closeable position="bottom">
          <div class="choose">
            <div class="title" style="text-align: center !important">
              <div>
                请选择
              </div>
            </div>
            <div v-for='(info,index) in field.options' :key="index">
              <div class="hook1" @click="chooseOption(info,index)">
                <div class="options" :class="[selectOption.id === info.id ? 'options2' : '']">
                  {{ info.attrValue === '其他' && field.otherLabelRemark ? field.otherLabelRemark : info.attrValue }}
                  <span style="color: rgb(240, 160, 48); font-size: 14px"
                        v-if="field.exactType === 'SCORING_SINGLE_CHOICE' && info.score != undefined && !info.scoreHide">({{ info.score }}分)</span>
                </div>
                <div class="hook" v-show="selectOption.id === info.id ">
                  <van-icon name="success"/>
                </div>
              </div>
              <!-- 选项后面的输入框 -->

              <van-field
                v-if="field.itemAfterHasEnter === 1 && selectOption.attrValue && selectOption.attrValue !== '其他' && info.id === selectOption.id "
                v-model='selectOption.valuesRemark'
                rows="1"
                :error="selectOption.error"
                @input="(val) => fieldInput(val, info)"
                autosize type="textarea" show-word-limit
                :maxlength='field.itemAfterHasEnterSize ? field.itemAfterHasEnterSize : 120'
                :placeholder="field.itemAfterHasEnterRequired === 1? '请输入(必填)' : '请输入(选填)'">
              </van-field>
              <!-- 选中其他选项的输入框 -->
              <van-field
                v-if="info.id === selectOption.id && field.hasOtherOption === 1 && selectOption.attrValue === '其他'"
                v-model="field.otherValue"
                show-word-limit
                :maxlength="field.otherValueSize"
                :error="field.error"
                @input="(val) => fieldInputOther(val, info)"
                autosize
                rows="1"
                autosize type="textarea" :placeholder="field.otherEnterRequired === 1? '请输入(必填)' : '请输入(选填)'"/>
            </div>
          </div>
          <div class="btn1">
            <van-button round type="info" :disabled="checkRadioDisableSubmit()" style="margin-top:2rem"
                        @click="updata">确定
            </van-button>
          </div>
        </van-popup>
      </div>
    </div>
    <div v-for='(value,k) in field.values' :key="field.id + 'question' +  k">
      <editorChild :questions='value.questions' ref='indexItem'></editorChild>
    </div>
  </div>

</template>
<script src="./index.js">
</script>
<style lang="less" scoped src="./singleChoice.less">
</style>
