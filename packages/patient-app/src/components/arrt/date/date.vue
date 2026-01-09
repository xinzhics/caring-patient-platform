<template>
  <div class="date">
    <div class="box1">
      <div class="title">
        <span class="username">{{field.label}}</span>
        <span class="mustchoose" v-if="field.required===true">*</span>
      </div>
      <div class="depict">{{field.labelDesc}}</div>
      <div>
        <div>
          <van-cell is-link :value="field.values[0].attrValue ? field.values[0].attrValue : field.placeholder" :ref="field.id"
                    :class="[field.required && !field.values[0].attrValue && btn===false ?'cell2':'']" @click="showPopup" 
                   :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}" 
                    />
        </div>
        <van-popup @closed="close" v-model="show" position="bottom" closeable>
          <div class="title1">请选择</div>

          <van-datetime-picker ref="value" :show-toolbar="false"
                               :min-date="minDate" :max-date="maxDate" v-model="currentDate"
                               type="date" visible-item-count="3" :columns-order="['year', 'month', 'day']"
                               :formatter="formatter" />
          <div class="submit">
            <van-button round type="info" @click="updata">确定</van-button>
          </div>
        </van-popup>
      </div>
    </div>
  </div>

</template>

<script src="./index.js">
</script>

<style lang='less' scoped  src="./index.less">
</style>
