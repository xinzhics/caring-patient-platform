<template>
  <div>
    <div v-if="keyWord">
      <span>
        根据关键词 "<span class="div_bold">{{keyWord}}</span>" 整理的内容如下:
      </span>
    </div>
    <div style="margin-top: 10px" v-if="cmsEnglishTitle">
      <span class="div_bold">英文标题：</span>
      <span>
        {{cmsEnglishTitle}}
      </span>
    </div>
    <div style="margin-top: 10px" v-if="cmsTitle">
      <span class="div_bold">中文标题：</span>
      <span>
        {{cmsTitle}}
      </span>
    </div>
    <div style="margin-top: 10px" v-if="cmsSummary">
      <span class="div_bold">概要：</span>
      <span>
        {{cmsSummary}}
      </span>
    </div>
    <div style="margin-top: 10px" v-if="cmsLink">
      <span class="div_bold">原文地址：</span>
      <span style="color: #547EFF">
        <a :href="cmsLink">{{cmsLink}}</a>
      </span>
    </div>
  </div>
</template>

<script>
export default {
  name: "systemArticle",
  props: {
    content: {
      type: String,
      default: function () {
        return {}; // 使用工厂函数返回一个空对象作为默认值
      }
    },
  },
  data() {
    return {
      keyWord: '',
      cmsEnglishTitle: '',
      cmsTitle: '',
      cmsSummary: '',
      cmsLink: '',
    }
  },
  mounted() {
    let data = JSON.parse(this.content)
    this.keyWord = data.keyWord.join(', ');
    this.cmsEnglishTitle = data.cmsEnglishTitle
    this.cmsTitle = data.cmsTitle
    this.cmsSummary = data.cmsSummary
    this.cmsLink = data.cmsLink
  }
}
</script>

<style lang="less" scoped>

.div_bold {
  font-weight: bolder;
}

</style>
