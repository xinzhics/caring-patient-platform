<template>
  <section class="downSelect">
    <div class="headerList">
      <ul>
        <li :class="active===0?'activeClass':''" @click="selectBtn({channelName:'全部'},0)">全部</li>
        <li v-for="(i,k) in headerArray" :key="k" :class="(k+1)===active?'activeClass':''" @click="selectBtn(i,(k+1))">{{ i.channelName }}</li>
      </ul>
      <!-- <span class="setBtn el-icon-setting" @click="setBtn" /> -->
    </div>
  </section>
</template>
<script>
export default {
  name: 'DownSelect',
  props: {
    headerArray: {
      type: Array,
      default() {
        return []
      }
    }
  },
  data() {
    return {

      mainList: [],
      active: 0
    }
  },
  methods: {

    selectBtn(i, k) {
      this.active = k
      this.$emit('selectBtn', i)
    },
    outBtn() {
      this.mainList = []
    },
    setBtn() {
      this.$emit('setBtn')
    }
  }

}
</script>
<style lang="scss" scoped>
.downSelect{
   color: rgba(0,0,0, 0.65);

    .headerList{
        width: 100%;
        background: #FFF;
        display: flex;
        justify-content: space-between;
        padding:0px 20px;
        font-size: 0.8rem;

        span{
            width:5%;
        }
        .setBtn{
            font-size:18px;
            line-height: 40px;
            text-align: right;
        }
        ul{
            width: 90%;
            margin: 0px;
            padding: 0px;
            margin: 10px 0px;
            li{
              margin-top: 4px;
              margin-right:30px;
              float: left;
              list-style: none;
              cursor: pointer;
              padding:1px 3px;
              border-radius: 3px;
              line-height: 20px;
              &:hover{
                background: #1890FF;
                color: #fff;
              }
            }
            .activeClass{
                background: #1890FF;
                color: #fff;
            }
        }
    }
    .inCont{
      position: relative;
      width: 100%;
      .headerMain{
        position:absolute;
        top: 0;
        z-index: 999;
        background: #fff;
        padding: 0px 20px;
        width: 100%;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        div{
          // width: 100%;
          // display: block;
          margin: 0px;
          padding: 8px 0px;
          border-top: 1px dashed #999;
          span{
            display: inline-block;
            list-style: none;
            margin:8px 30px 8px 0px;
            cursor: pointer;
            padding:0px 3px;
            border-radius: 3px;
            line-height: 16px;
            &:hover{
              background: #1890FF;
              color: #fff;
            }
          }
          .activeClass{
            background: #1890FF;
            color: #fff;
          }
        }
      }
    }

}
</style>
