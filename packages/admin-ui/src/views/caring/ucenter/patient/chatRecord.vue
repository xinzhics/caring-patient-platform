<template>
  <div class="app-container">
    <div class="filter-container">
      <div >
        <el-input
          v-model="label"
          :placeholder="$t('table.patient.chatHint')"
          class="filter-item search-item"
        />
        <el-button
          class="filter-item"
          plain
          type="primary"
          @click="search"
        >
          {{ $t('table.search') }}
        </el-button>

        <div style="float: right; color: #0a76a4; font-size: 18px; ">
          仅显示 :
          <el-button
            class="filter-item"
            plain
            type="primary"
            style="margin-left: 20px"
            @click="textSearch"
          >
            {{ $t('table.patient.textSearch') }}
          </el-button>

          <el-button
            class="filter-item"
            plain
            type="primary"
            @click="imageSearch"
          >
            {{ $t('table.patient.imageSearch') }}
          </el-button>

          <el-button
            class="filter-item"
            plain
            type="primary"
            @click="cmsSearch"
          >
            {{ $t('table.patient.cmsSearch') }}
          </el-button>

          <el-button
            class="filter-item"
            plain
            type="warning"
            @click="reset"
          >
            {{ $t('table.reset') }}
          </el-button>
        </div>
      </div>
      <div class="content" @scroll="handleScroll($event)" ref="msgContent">
          <div
            v-for="(item,i) in messageList"
            :key="i"
            class="list-item"
          >
            <div v-if="i % 20 === 0"
                 style="display: flex; align-items: center; justify-content: center; font-size: 12px; color: #9B9B9B; margin-top: 8px; margin-bottom: 8px">
              {{ item.updateTime }}
            </div>

            <div style="display: inline-block; margin-left: 15px; margin-top: 15px">
              <div style="display: flex; float: left;">
                <div>
                  <div>
                    <el-avatar
                      style="width: 40px; height: 40px"
                      :key="item.senderAvatar"
                      :src="(item.senderAvatar)"
                      fit="fill"
                      icon="el-icon-user-solid"
                    ></el-avatar>
                  </div>

            </div>
            <div>
              <div style="font-size: 12px; color: #3a3a3a; margin-left: 15px; margin-bottom: 5px">{{ item.senderName }}</div>
              <div style="display: flex;">
                <div
                  style="margin-top: 6px; right: -16px; width:0;height:0; font-size:0; border:solid 8px;border-color:#E9E9E9 #ffffff #E9E9E9 #E9E9E9">
                </div>
                <div
                  v-if="'text' ===item.type"
                  style="text-align: left; margin-right: 60px; word-wrap: break-word; word-break: break-all; overflow: hidden;
                background: #FFF; padding: 15px; border-radius: 5px; height: fit-content">
                  {{ item.content }}
                </div>
                <div
                  v-if="'image' ===item.type"
                  style="text-align: left; margin-right: 60px; word-wrap: break-word; word-break: break-all; overflow: hidden;
                background: #FFF; padding: 15px; border-radius: 5px">

                  <el-image
                    style="width: 100px; height: 100px"
                    :src="item.content"
                    :preview-src-list="[item.content]"></el-image>
                </div>
                <div
                  v-if="'cms' ===item.type"
                  style="text-align: left; margin-right: 60px; word-wrap: break-word; word-break: break-all; overflow: hidden;
                background: #FFF; padding: 15px; border-radius: 5px; height: fit-content">
                  <div style="background: #F5F5F5; align-items: center; width: 100%; height: 100%; display: flex">
                    <el-image
                      style="width: 60px; height: 60px"
                      :src="JSON.parse(item.content).icon"
                      fit="fit"></el-image>
                    <div style="margin-left: 7px; margin-right: 7px; background: #FFFFFF">
                      {{ JSON.parse(item.content).title }}
                    </div>
                  </div>
                </div>
              </div>
            </div>

              </div>
            </div>

          </div>
      </div>

    </div>
  </div>
</template>

<script>

import ChatApi from "@/api/Chat.js";
import {initQueryParams} from '@/utils/commons'

export default {
  name: "chatRecord",
  data() {
    return {
      //数据模板
      queryParams: initQueryParams(),
      id: '',
      messageList: [],
      isLoading: true,
      label: ''
    }
  },
  methods: {
    //获取历史聊天
    getChatList() {
      this.queryParams.model.patientId = this.id;
      this.queryParams.size = 20;
      console.log(this.isLoading)
      if (!this.isLoading) {
        return
      }
      ChatApi.page(this.queryParams).then(response => {
        if (this.queryParams.current === 1) {
          this.messageList = []
        }
        response.data.data.records.forEach(item => {
          this.messageList.unshift(item)
        })
        if (this.queryParams.current == 1) {
          setTimeout(() => {
            const dom = this.$refs.msgContent;
            if (!dom) return;
            dom.scrollTop = dom.scrollHeight;
          }, 0);
        }

        if (20 === response.data.data.records.length) {
          this.queryParams.current = this.queryParams.current + 1
        }else {
          this.isLoading = false;
        }

        console.log(this.isLoading+'========'+response.data.data.records.length)
      });
    },

    handleScroll(e) {
      if (e.target.scrollTop  == 0) {
        this.getChatList();
      }
    },

    search() {
      this.messageList = [];
      this.isLoading = true;
      this.queryParams.model.type =  "text";
      this.queryParams.current = 1;
      this.queryParams.model.content = this.label;
      this.getChatList()
    },
    textSearch() {
      this.messageList = []
      this.isLoading = true;
      this.queryParams.model.type =  "text";
      this.queryParams.current = 1;
      this.queryParams.model.content = "";
      this.getChatList()
    },
    imageSearch() {
      this.messageList = []
      this.isLoading = true;
      this.queryParams.model.type =  "image";
      this.queryParams.current = 1;
      this.queryParams.model.content = "";
      this.getChatList()
    },
    cmsSearch() {
      this.messageList = []
      this.isLoading = true;
      this.queryParams.model.type =  "cms";
      this.queryParams.current = 1;
      this.queryParams.model.content = "";
      this.getChatList()
    },
    reset() {
      this.label = ''
      this.messageList = []
      this.isLoading = true;
      this.queryParams.model.type =  "";
      this.queryParams.current = 1;
      this.queryParams.model.content = "";
      this.getChatList()
    }
  },

  mounted() {
    const that = this
    if(that.$route.query&&that.$route.query.id){
      that.id = that.$route.query.id
      that.getChatList()
    }
  },
}
</script>

<style lang="scss" scoped>
.app-container {
  background: #f8f8f8;
  padding: 0px;

  .filter-container {
    background: #fff;
    padding: 20px;
    height: 85%;

    .content {
      margin-top: 10px;
      background: #E9E9E9;
      padding: 20px;
      height: 78vh;
      overflow: scroll;
    }
  }
}


</style>
