<template>
  <div class="box1">
    <div class="title">
      <span class="username">{{field.label}}</span>
      <span class="mustchoose" v-if="field.required===true">*</span>
    </div>
    <div class="depict">{{field.labelDesc}}</div>
    <div>
      <!-- 必选 -->
      <div>
        <van-cell :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}" is-link
          :value="field.values[0].attrValue ? field.values[0].attrValue + ' cm' : field.placeholder" :ref="field.id"
          :class="[field.required && !field.values[0].attrValue && btn === false ?'cell2':'']" @click="showPopup" />
      </div>
      <!-- 非必选 -->
      <van-popup @closed="close" v-model="show" position="bottom" closeable>
        <div class="title1">请选择</div>

        <!-- <van-datetime-picker :show-toolbar="false" visible-item-count="3" v-model="currentTime" type="time" title="选择时间" @click="showbox" /> -->
        <van-picker ref="value" :show-toolbar="false" title="标题" visible-item-count="3" :columns="columns"
          :default-index="defaultIndex" />
        <div class="submit">
          <van-button round type="info" @click="updata">确定</van-button>
        </div>

      </van-popup>
    </div>
  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped src="./index.less">
</style>
