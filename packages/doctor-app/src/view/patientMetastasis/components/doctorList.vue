<template>
  <section>
    <div>
      <x-header :left-options="{backText: '',preventGoBack:true}" @on-click-back="itemClick({})"
        style="margin-bottom:0px!important;">
        选择接收{{name}}（{{ alldata ? alldata.length : 0 }}）
      </x-header>
    </div>
    <div>
      <van-search
        v-model="doctorName"
        show-action
        :clearable="false"
        placeholder="搜索"
        @search="onSearch"
      >
        <template #action>
          <div @click="onSearch">搜索</div>
        </template>
      </van-search>
      <group class="myheader">
        <cell v-for="(i,k) in alldata" :key="k" @click.native="itemClick(i)">
          <div slot="title" style="display:flex;">
            <img :src="i.avatar" alt="" style="width:2.2rem;height:2.2rem;vertical-align: middle; border-radius: 50%; margin-right:0.5rem">
            <div >
              <div :class="!i.title?'ones':''">{{ i.name }}</div>
              <p>{{i.title}}</p>
            </div>
          </div>
          <div>
            {{i.hospitalName}}
          </div>
        </cell>
      </group>
    </div>
    <loading :show="isLoading"/>
  </section>
</template>
<script>
  import Api from '@/api/Content.js'
  import {
    Icon
  } from 'vux'
  import loading from '@/components/loading/ChrysanthemumLoading'

  export default {
    components: {
      Icon,
      loading
    },
    data() {
      return {
        mylist: [],
        isLoading: false,
        isCompleteEnterGroup: null,
        diagnosisName: null,
        alldata: [],
        doctorName: '',
        screen: require('@/assets/drawable-xhdpi/screen.png'),
        Canscreen: false,
        screenData: [{
            name: '所有' + this.$getDictItem('patient'),
            children: []
          },
          {
            name: this.patient + '状态',
            children: [{
              name: this.register + this.patient,
              isCompleteEnterGroup: 2
            }, {
              name: '扫码' + this.patient,
              isCompleteEnterGroup: 1
            }]
          },
          {
            name: this.diagnostictype,
            children: []
          },

        ],
        ScreenIndex: '',
        screenChildren: [],
        selectedIndex: '',
        name: this.$getDictItem('doctor'),
        patient: this.$getDictItem('patient'),
        register: this.$getDictItem('register'),
        diagnostictype: this.$getDictItem('diagnostictype')
      }
    },

    mounted() {
      this.title = "所有" + this.name
      if (this.$route.query) {
        this.diagnosisName = this.$route.query.diagnosisName || null
        this.isCompleteEnterGroup = this.$route.query.isCompleteEnterGroup
        //患者列表标题
        if (this.$route.query.type == 0) {
          this.title = "扫码" + this.$getDictItem('patient')
        } else if (this.$route.query.type == 1) {
          this.title = "注册" + this.$getDictItem('patient')
        } else {
          if (this.$route.query.diagnosisName) {
            this.title = this.$route.query.diagnosisName
          } else {
            this.title = "所有" + this.$getDictItem('patient')
          }
        }
      }
      this.isLoading = true
      this.getInfo()
    },
    methods: {
      onSearch() {
        this.isLoading = true
        this.getInfo()
      },
      //聊天条目点击跳转
      itemClick(item) {
        this.$emit('doctorSelectBtn', item)
      },
      getInfo() {
        const params = {
          name: this.doctorName
        }
        Api.doctorQuery(params).then((res) => {
          this.isLoading = false
          if (res.data.code === 0) {
            this.alldata = res.data.data
          } else {
            this.$vux.toast.text(res.data.msg, 'center')
          }
        })
      },
      screenBtn(i, k) {
        this.ScreenIndex = k
        this.screenChildren = i.children
        this.selectedIndex = ''
        if (i.children.length === 0) {
          this.getInfo()
          this.Canscreen = !this.Canscreen
        }
      },
      selectedBtn(i, k) {
        this.selectedIndex = k
        let isCompleteEnterGroup = ''
        let diagnosisName = ''
        if (i.name) {
          if (i.isCompleteEnterGroup === 2) {
            isCompleteEnterGroup = 1
          } else {
            isCompleteEnterGroup = 0
          }
        } else {
          diagnosisName = i.diagnosisName
        }
        this.Canscreen = !this.Canscreen

        const params = {
          doctorId: localStorage.getItem('caring_doctor_id'),
          diagnosisName: diagnosisName,
          isCompleteEnterGroup: isCompleteEnterGroup,
        }
        Api.postquery(params).then((res) => {
          if (res.data.code === 0) {
            this.alldata = res.data.data
          }
        })
      }
    }
  }

</script>


<style lang="less" scoped>
  /deep/ .vux-header {
    height: 50px;
  }

  .myheader {
    .weui-cells__title {
      line-height: 20px;
    }
  }

  .slide {
    width: 100vw;
    position: absolute;
    z-index: 10;
    background: rgba(0, 0, 0, 0.5);
    overflow: hidden;
    max-height: 0;
    transition: max-height .5s cubic-bezier(0, 1, 0, 1) -.1s;
    display: flex;
    justify-content: space-between;

    .itemTitle {
      background: #F7F7F7;
      text-align: left;
      width: 50vw;

      .item {
        padding: 10px 0px;
        width: 45vw;
        padding-left: 5vw;
        font-size: 14px;
      }

    }

    .itemInner {
      background: #fff;
      text-align: left;
      width: 45vw;
      padding-left: 5vw;

      .item {
        padding: 10px 0px 9px;
        font-size: 14px;
        border-bottom: 1px solid #DDDDDD;
        display: flex;
        justify-content: space-between;
      }
    }
  }
  .ones{
    line-height: 40px;
  }
  .animate {
    height: 100vh;
    max-height: 9999px;
    transition-timing-function: cubic-bezier(0.5, 0, 1, 0);
    transition-delay: 0s;
  }

</style>
