<template>
  <div>
    <div class="single-choice" v-if="field">
      <div class="box1">
        <div class="title">
          <div class="username">{{ field.label }}</div>
          <div class="mustchoose" v-if="field.required">*</div>
        </div>
        <div class="depict">{{ field.labelDesc }}</div>
        <div :class="showbox ? 'showbox' : 'noshowbox'">
          <van-cell is-link class="firstcell" ref="color" :class="checkFail ? ' cell2' : ''"
                    @click="showPopup"
                    :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}">

            <div :style="{color:field.values[0].valueText?'#333333':'#b8b8b8 !important'}" style="display: flex; align-items: center">
              {{
                field.values[0].valueText ? field.values[0].valueText === '其他' && field.otherLabelRemark ? field.otherLabelRemark : field.values[0].valueText : field.placeholder ? field.placeholder : '请选择'
              }}
            </div>
            <template #label>
              <div>

                {{ getLabel() }}
              </div>
            </template>
          </van-cell>
        </div>

        <!--        <van-cell v-else class="firstcell" title="请选择1" ref="color" is-link @click="showPopup"></van-cell>-->
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
                </div>
                <div class="hook" v-show="selectOption.id === info.id ">
                  <van-icon name="success"/>
                </div>
              </div>
              <!-- 选项后面的输入框 -->

              <van-field
                v-if="field.itemAfterHasEnter === 1 && selectOption.attrValue && selectOption.attrValue !== '其他' && info.id === selectOption.id "
                v-model='selectOption.valuesRemark' rows="1" :error="selectOption.error"
                @input="(val) => fieldInput(val, info)" autosize type="textarea" show-word-limit
                :maxlength='field.itemAfterHasEnterSize ? field.itemAfterHasEnterSize : 120'
                :placeholder="field.itemAfterHasEnterRequired === 1? '请输入(必填)' : '请输入(选填)'">
              </van-field>
              <!-- 选中其他选项的输入框 -->
              <van-field :style="{'border':field.modifyTags ? '1px dashed #337EFF':''}"
                         v-if="info.id === selectOption.id && field.hasOtherOption === 1 && selectOption.attrValue === '其他'"
                         v-model="field.otherValue" show-word-limit :maxlength="field.otherValueSize"
                         :error="field.error"
                         @input="(val) => fieldInputOther(val, info)" autosize rows="1" type="textarea"
                         :placeholder="field.otherEnterRequired === 1? '请输入(必填)' : '请输入(选填)'"/>
            </div>
          </div>
          <div class="btn1">
            <van-button round type="info" :disabled="checkRadioDisableSubmit()" style="margin-top:2rem" @click="updata">
              确定
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
