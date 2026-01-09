<template>
  <div class="box1">
    <div class="title">

      <span class="username">{{ field.label }}</span>
      <span class="mustchoose" v-if="field.required">*</span>
    </div>
    <div class="depict">{{ field.labelDesc }}</div>
    <!-- scr -->
    <div class="userinfo">
      <van-field v-model="field.values[0].attrValue" type="number" :ref="field.id" :class="[show===true?'cell2':'']"
                 :disabled="field.isUpdatable !== undefined && field.isUpdatable === 0"
                 :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}"
                 :min="0" :max="300" :placeholder="field.placeholder" @input="change" @blur="fieldInput"/>
      <div class="unit">
        {{ field.rightUnit }}
      </div>
    </div>
    <!-- 子输入框   width: 6.75rem /* 108/16 */;-->
    <div class="fansile">

      <div class="scrchild" :style="{'margin-right':margin}" v-if="gfrField !== undefined">

        <div class="title ">
          <span class="username titlename">{{ gfrField.label }}</span>
        </div>
        <van-field disabled class="cell3" :placeholder="gfrField.placeholder"
                   :value='gfrField.values[0].attrValue==""?"-":gfrField.values[0].attrValue'/>

      </div>
      <div class="scrchild" :style="{'margin-right':margin}" v-if="ccrField !== undefined">

        <div class="title ">
          <span class="username titlename">{{ ccrField.label }}</span>
        </div>
        <!--   -->
        <van-field disabled class="cell3" :placeholder="ccrField.placeholder"
                   :value='ccrField.values[0].attrValue==""?"-":ccrField.values[0].attrValue'/>

      </div>
      <div class="scrchild" v-if="courseOfDiseaseField !== undefined">

        <div class="title ">
          <span class="username titlename">{{ courseOfDiseaseField.label }}</span>
        </div>
        <van-field disabled class="cell3" :placeholder="courseOfDiseaseField.placeholder"
                   :value='courseOfDiseaseField.values[0].attrValue'/>
      </div>

    </div>

  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped src="./index.less">
</style>
