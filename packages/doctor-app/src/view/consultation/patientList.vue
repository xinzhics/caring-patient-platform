<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">{{ "选择患者" }}
    </x-header>
    <div style="margin-top: 50px; background: white">
      <cell v-for="(i,k) in list" :key="k+'z'" is-link @click.native="itemClick(i)">
        <div slot="title" style="display: flex; align-items: center">
          <img :src="i.avatar ? i.avatar : require('@/assets/my/touxiang.png')" alt=""
               style="width:2.2rem;height:2.2rem;vertical-align: middle; border-radius: 2.2rem; margin-right: 5px">
          {{ i.doctorRemark ? i.name+'('+i. doctorRemark +')': i.name }}
        </div>
      </cell>
    </div>
  </section>
</template>

<script>
import Api from '@/api/doctor.js'

export default {
  name: "patientList",
  data() {
    return {
      list: [],
      params: {
        model: {
          doctorId: localStorage.getItem('caring_doctor_id'),
          status: 1,
        },
        current: 1,
        size: 50,
      }
    }
  },
  created() {
    this.getInfo()
  },
  methods: {
    getInfo() {
      Api.postquery(this.params).then((res) => {
        if (res.data.code === 0) {
          this.list = res.data.data.records
        }
      })
    },
    itemClick(item) {
      Vue.$store.commit('addData', {
        patientId: item.id,
        patientName: item.doctorRemark ?item.name+'('+item.doctorRemark+')' : item.name,
        groupName: item.name + '患者的讨论小组',
        groupDesc: this.$store.state.consultation.addData.groupDesc,
        memberAvatar: item.avatar
      })
      this.$router.back()
    }
  }
}
</script>

<style lang="less" scoped>

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;
}

/deep/ .vux-header {
  margin-bottom: 0px;
  height: 50px;
  position: fixed;
  width: 100%;
  z-index: 999;
  top: 0;
  left: 0;
}
</style>
