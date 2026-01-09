<template>
  <div class="time">
    <div class="box1">
      <div class="title">
        <span class="username">{{field.label}}</span>
        <span class="mustchoose" v-if="field.required">*</span>
      </div>
      <div class="depict">{{field.labelDesc}}</div>
      <div>
        <!-- 必选 -->
        <van-cell is-link :ref="field.id"
       :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}"
                  :class="[field.required && !field.values[0].attrValue && btn===false ?'cell2':'']"
                  :value="field.values[0].attrValue ? field.values[0].attrValue : field.placeholder" @click="showPopup" />
        <van-popup @closed="close" v-model="show" position="bottom" closeable>
          <div class="title1">请选择</div>
          <van-datetime-picker
            v-model="currentTime"
            :show-toolbar="false"
            type="time"
             visible-item-count="3"
          />
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
