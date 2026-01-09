<template>
  <div>
    <div class="main">
      <div class="title">
        <span class="name">{{field.label}}</span>
        <span class="mustchoose" v-if="field.required===true">*</span>
        <div class="describe">{{field.labelDesc}}</div>
      </div>
      <div>
        <van-cell class="cell" is-link :value="
            field.values && field.values.length > 0
              ? field.values[0].valueText
              : field.placeholder?field.placeholder:'请选择医院'
          " :class="[field.values &&field.values.length>0?'cellColor':'nodatacolor']" :style="{'border':field.modifyTags ? '1px dashed #337EFF':'1px solid #D6D6D6;','background':field.modifyTags?'rgba(51, 126, 255, 0.05)':''}" is-link @click="showPopup" />
        <div>
          <van-popup v-model="show" position="bottom" :style="{height: '100%' }">
            <hospital v-if="show" :scrollTop='scrollTop' @hideList="hide" :field='field' />
          </van-popup>
        </div>

      </div>
    </div>
  </div>

</template>

<script src="./index.js">
</script>

<style lang='less' scoped  src="./index.less">
</style>