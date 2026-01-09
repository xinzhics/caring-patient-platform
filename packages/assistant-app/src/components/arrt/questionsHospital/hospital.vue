<template>
  <div>
    <div class="main">
      <div>
        <van-cell
          class="cell"
          is-link
          :value="
            field.values && field.values.length > 0
              ? field.values[0].valueText
              : field.placeholder?field.placeholder:'请选择医院'
          "
          :class="[
            field.values && field.values.length > 0
              ? 'cellColor'
              : 'nodatacolor',
          ]"
          :style="{
            border: field.modifyTags
              ? '1px dashed #337EFF'
              : '1px solid #D6D6D6;',
            background: field.modifyTags ? 'rgba(51, 126, 255, 0.05)' : '',
          }"
          @click="showPopup"
        />
        <van-popup v-model="show" position="bottom" :style="{ height: '100%' }">
          <hospital v-if="show" @hideList="hide" :field="field" />
        </van-popup>
      </div>
    </div>
  </div>
</template>

<script src="./index.js">
</script>

<style lang='less' scoped  src="./index.less">
</style>
