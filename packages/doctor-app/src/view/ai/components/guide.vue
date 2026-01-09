<template>
  <div>
    <div>
      <div>您好，我是您的学术助理敏智。</div>
      <div>我是基于chatgtp4基础开发的过敏专科AI学术助理。</div>
      <div>我可以帮助您快速查找、搜集和整理过敏专科的医学文献资料。</div>
      <div>如果您需要，我也可以帮您阅读最新的医学资讯并帮您生成摘要，帮助您提高临床科研方面的学习和工作效率。</div>
      <div>将来我将会不断学习新的技能，为您提供更多帮助，并且根据您的专业领域，我为您整理了以下几个关键词，点击下方关键词即可订阅：</div>
    </div>
    <div class="content">
      <div v-for="(item, index) in list" :key="index" @click="selectItem(item)"
           :class="isSelect(item) ? 'select' : 'item'">
        <div>{{ item.keyWord }}</div>
      </div>
    </div>
    <div style="display: flex; justify-content: right" v-if="!isDoctorKey">
      <van-button type="info" size="small" class="commit-btn" @click="submit()">确定订阅</van-button>
    </div>
  </div>
</template>

<script>

import doctorApi from '@/api/doctor.js'

export default {
  name: "guide",
  props: {
    keyList: {
      type: Array,
      default: []
    },
    isDoctorKey: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      list: [],
      selectList: [],
    }
  },
  mounted() {
    this.list = this.keyList
    this.list.forEach(item => {
      if (item.subscribe) {
        this.selectList.push(item)
      }
    })
  },
  methods: {
    selectItem(sItem) {
      if (this.selectList.length === 0) {
        this.selectList.push(sItem)
      } else {
        let idExists = this.selectList.some(item => item.id === sItem.id);
        if (idExists) {
          this.selectList = this.selectList.filter(item => item.id !== sItem.id);
        } else {
          this.selectList.push(sItem)
        }
      }
    },
    isSelect(sItem) {
      let idExists = this.selectList.some(item => item.id === sItem.id);
      return idExists
    },
    submit() {
      let keyWord = this.selectList.map(item => item.keyWord).join('、');
      let keyWordId = []
      this.selectList.map(item => keyWordId.push(item.id));
      if (keyWord) {
        doctorApi.subscribeKeyword(keyWordId)
          .then(res => {
            doctorApi.getDoctorSubscribeReply({
              keyWord: keyWord,
              doctorId: localStorage.getItem('caring_doctor_id'),
              imAccount: localStorage.getItem('userImAccount'),
            })
              .then(res => {
                Vue.$store.commit('updateAiMessage', {
                  message: {
                    ext: {
                      content: keyWord,
                      createTime: moment().format('yyyy-MM-DD HH:mm:ss'),
                      senderRoleType: 'doctor',
                    }
                  }
                })
              })
          })
      } else {
        this.$vux.toast.text("请选择关键字", 'center');
      }
    }
  }
}
</script>

<style lang="less" scoped>

.content {
  padding: 16px 0px 5px 10px;
  display: flex;
  flex-flow: row wrap;
  justify-content: left;
  align-items: flex-start;
  background: #FFFFFF;
  overflow-y: scroll;
  scrollbar-width: none;

  .item {
    max-width: 80%;
    margin: 0 10px 15px 0;
    text-align: center;
    font-size: 14px;
    color: #547EFF;
    border-radius: 20px;
    white-space: pre-line;
    padding: 2px 10px;
    border: 1px solid #547EFF;
  }

  .select {
    max-width: 80%;
    margin: 0 10px 15px 0;
    font-size: 14px;
    text-align: center;
    color: #999;
    background: #EDEDED;
    border-radius: 20px;
    white-space: pre-line;
    padding: 2px 10px;
    border: 1px solid #EDEDED;
  }
}

.commit-btn {
  border-radius: 3px;
  margin-bottom: 10px;
  width: 100px;
  height: 35px;
  margin-right: 5px
}

</style>
